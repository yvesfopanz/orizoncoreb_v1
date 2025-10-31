/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.commands.service;

import static org.apache.fineract.commands.domain.CommandProcessingResultType.ERROR;
import static org.apache.fineract.commands.domain.CommandProcessingResultType.PROCESSED;
import static org.apache.http.HttpStatus.SC_OK;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.resilience4j.retry.Retry;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.batch.exception.ErrorInfo;
import org.apache.fineract.commands.configuration.RetryConfigurationAssembler;
import org.apache.fineract.commands.domain.CommandProcessingResultType;
import org.apache.fineract.commands.domain.CommandSource;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.exception.CommandResultPersistenceException;
import org.apache.fineract.commands.exception.UnsupportedCommandException;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.commands.provider.CommandHandlerProvider;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.domain.BatchRequestContextHolder;
import org.apache.fineract.infrastructure.core.domain.FineractRequestContextHolder;
import org.apache.fineract.infrastructure.core.exception.ErrorHandler;
import org.apache.fineract.infrastructure.core.exception.IdempotentCommandProcessFailedException;
import org.apache.fineract.infrastructure.core.exception.IdempotentCommandProcessSucceedException;
import org.apache.fineract.infrastructure.core.exception.IdempotentCommandProcessUnderProcessingException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.GoogleGsonSerializerHelper;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.hooks.event.HookEvent;
import org.apache.fineract.infrastructure.hooks.event.HookEventSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SynchronousCommandProcessingService implements CommandProcessingService {

    public static final String IDEMPOTENCY_KEY_STORE_FLAG = "idempotencyKeyStoreFlag";

    public static final String IDEMPOTENCY_KEY_ATTRIBUTE = "IdempotencyKeyAttribute";
    public static final String COMMAND_SOURCE_ID = "commandSourceId";
    private final PlatformSecurityContext context;
    private final ApplicationContext applicationContext;
    private final ToApiJsonSerializer<Map<String, Object>> toApiJsonSerializer;
    private final ToApiJsonSerializer<CommandProcessingResult> toApiResultJsonSerializer;
    private final ConfigurationDomainService configurationDomainService;
    private final CommandHandlerProvider commandHandlerProvider;
    private final IdempotencyKeyResolver idempotencyKeyResolver;
    private final CommandSourceService commandSourceService;
    private final RetryConfigurationAssembler retryConfigurationAssembler;

    private final FineractRequestContextHolder fineractRequestContextHolder;
    private final Gson gson = GoogleGsonSerializerHelper.createSimpleGson();

    private CommandProcessingResult retryWrapper(Supplier<CommandProcessingResult> supplier) {
        try {
            if (!BatchRequestContextHolder.isEnclosingTransaction()) {
                return retryConfigurationAssembler.getRetryConfigurationForExecuteCommand().executeSupplier(supplier);
            }
            return supplier.get();
        } catch (RuntimeException e) {
            return fallbackExecuteCommand(e);
        }
    }

    @Override
    public CommandProcessingResult executeCommand(final CommandWrapper wrapper, final JsonCommand command,
            final boolean isApprovedByChecker) {
        return retryWrapper(() -> {
            // Do not store the idempotency key because of the exception handling
            setIdempotencyKeyStoreFlag(false);

            Long commandId = (Long) fineractRequestContextHolder.getAttribute(COMMAND_SOURCE_ID, null);
            boolean isRetry = commandId != null;
            boolean isEnclosingTransaction = BatchRequestContextHolder.isEnclosingTransaction();

            CommandSource commandSource = null;
            String idempotencyKey;
            if (isRetry) {
                commandSource = commandSourceService.getCommandSource(commandId);
                idempotencyKey = commandSource.getIdempotencyKey();
            } else if ((commandId = command.commandId()) != null) { // action on the command itself
                commandSource = commandSourceService.getCommandSource(commandId);
                idempotencyKey = commandSource.getIdempotencyKey();
            } else {
                idempotencyKey = idempotencyKeyResolver.resolve(wrapper);
            }
            exceptionWhenTheRequestAlreadyProcessed(wrapper, idempotencyKey, isRetry);

            AppUser user = context.authenticatedUser(wrapper);
            if (commandSource == null) {
                if (isEnclosingTransaction) {
                    commandSource = commandSourceService.getInitialCommandSource(wrapper, command, user, idempotencyKey);
                } else {
                    commandSource = commandSourceService.saveInitialNewTransaction(wrapper, command, user, idempotencyKey);
                    commandId = commandSource.getId();
                }
            }
            if (commandId != null) {
                storeCommandIdInContext(commandSource); // Store command id as a request attribute
            }

            setIdempotencyKeyStoreFlag(true);

            return executeCommand(wrapper, command, isApprovedByChecker, commandSource, user, isEnclosingTransaction);
        });
    }

    private CommandProcessingResult executeCommand(final CommandWrapper wrapper, final JsonCommand command,
            final boolean isApprovedByChecker, CommandSource commandSource, AppUser user, boolean isEnclosingTransaction) {

        final CommandProcessingResult result;
        try {
            result = commandSourceService.processCommand(findCommandHandler(wrapper), command, commandSource, user, isApprovedByChecker);
        } catch (Throwable t) { // NOSONAR
            RuntimeException mappable = ErrorHandler.getMappable(t);
            ErrorInfo errorInfo = commandSourceService.generateErrorInfo(mappable);
            Integer statusCode = errorInfo.getStatusCode();
            commandSource.setResultStatusCode(statusCode);
            commandSource.setResult(errorInfo.getMessage());
            if (statusCode != SC_OK) {
                commandSource.setStatus(ERROR);
            }
            if (!isEnclosingTransaction) { // TODO: temporary solution
                commandSourceService.saveResultNewTransaction(commandSource);
            }
            // must not throw any exception; must persist in new transaction as the current transaction was already
            // marked as rollback
            publishHookErrorEvent(wrapper, command, errorInfo);
            throw mappable;
        }

        Retry persistenceRetry = retryConfigurationAssembler.getRetryConfigurationForCommandResultPersistence();

        try {
            CommandSource finalCommandSource = commandSource;
            CommandSource savedCommandSource = persistenceRetry.executeSupplier(() -> {
                // Get metrics for logging
                long attemptNumber = persistenceRetry.getMetrics().getNumberOfFailedCallsWithRetryAttempt() + 1;

                // Critical: Refetch on retry attempts (not on first attempt)
                CommandSource currentSource = finalCommandSource;
                if (attemptNumber > 1) {
                    log.info("Retrying command result save - attempt {} for command ID {}", attemptNumber, finalCommandSource.getId());
                    currentSource = commandSourceService.getCommandSource(finalCommandSource.getId());
                }

                // Update command source with results
                currentSource.setResultStatusCode(SC_OK);
                currentSource.updateForAudit(result);
                currentSource.setResult(toApiResultJsonSerializer.serializeResult(result));
                currentSource.setStatus(PROCESSED);

                // Return saved command source
                return commandSourceService.saveResultSameTransaction(currentSource);
            });

            // Command successfully saved
            storeCommandIdInContext(savedCommandSource);

        } catch (Exception e) {
            // After all retries have been exhausted
            log.error("Failed to persist command result after multiple retries for command ID {}", commandSource.getId(), e);
            throw new CommandResultPersistenceException("Failed to persist command result after multiple retries", e);
        }

        result.setRollbackTransaction(null);
        publishHookEvent(wrapper.entityName(), wrapper.actionName(), command, result); // TODO must be performed in a
        // new transaction
        return result;
    }

    private void storeCommandIdInContext(CommandSource savedCommandSource) {
        if (savedCommandSource.getId() == null) {
            throw new IllegalStateException("Command source not saved");
        }
        // Idempotency filters and retry need this
        fineractRequestContextHolder.setAttribute(COMMAND_SOURCE_ID, savedCommandSource.getId());
    }

    private void publishHookErrorEvent(CommandWrapper wrapper, JsonCommand command, ErrorInfo errorInfo) {
        try {
            publishHookEvent(wrapper.entityName(), wrapper.actionName(), command, gson.toJson(errorInfo));
        } catch (Exception e) {
            log.error("Failed to publish hook error event for entity: {}, action: {}", wrapper.entityName(), wrapper.actionName(), e);
        }
    }

    private void exceptionWhenTheRequestAlreadyProcessed(CommandWrapper wrapper, String idempotencyKey, boolean retry) {
        CommandSource command = commandSourceService.findCommandSource(wrapper, idempotencyKey);
        if (command == null) {
            return;
        }
        CommandProcessingResultType status = CommandProcessingResultType.fromInt(command.getStatus());
        switch (status) {
            case UNDER_PROCESSING -> {
                Class<?> lastExecutionExceptionClass = retryConfigurationAssembler.getLastException();
                if (lastExecutionExceptionClass == null
                        || IdempotentCommandProcessUnderProcessingException.class.isAssignableFrom(lastExecutionExceptionClass)) {
                    throw new IdempotentCommandProcessUnderProcessingException(wrapper, idempotencyKey);
                }
            }
            case PROCESSED -> throw new IdempotentCommandProcessSucceedException(wrapper, idempotencyKey, command);
            case ERROR -> {
                if (!retry) {
                    throw new IdempotentCommandProcessFailedException(wrapper, idempotencyKey, command);
                }
            }
            default -> {
            }
        }
    }

    private void setIdempotencyKeyStoreFlag(boolean flag) {
        fineractRequestContextHolder.setAttribute(IDEMPOTENCY_KEY_STORE_FLAG, flag);
    }

    public CommandProcessingResult fallbackExecuteCommand(Exception e) {
        throw ErrorHandler.getMappable(e);
    }

    private NewCommandSourceHandler findCommandHandler(final CommandWrapper wrapper) {
        NewCommandSourceHandler handler;

        if (wrapper.isDatatableResource()) {
            if (wrapper.isCreateDatatable()) {
                handler = applicationContext.getBean("createDatatableCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isDeleteDatatable()) {
                handler = applicationContext.getBean("deleteDatatableCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isUpdateDatatable()) {
                handler = applicationContext.getBean("updateDatatableCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isCreate()) {
                handler = applicationContext.getBean("createDatatableEntryCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isUpdateMultiple()) {
                handler = applicationContext.getBean("updateOneToManyDatatableEntryCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isUpdateOneToOne()) {
                handler = applicationContext.getBean("updateOneToOneDatatableEntryCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isDeleteMultiple()) {
                handler = applicationContext.getBean("deleteOneToManyDatatableEntryCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isDeleteOneToOne()) {
                handler = applicationContext.getBean("deleteOneToOneDatatableEntryCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isRegisterDatatable()) {
                handler = applicationContext.getBean("registerDatatableCommandHandler", NewCommandSourceHandler.class);
            } else {
                throw new UnsupportedCommandException(wrapper.commandName());
            }
        } else if (wrapper.isNoteResource()) {
            if (wrapper.isCreate()) {
                handler = applicationContext.getBean("createNoteCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isUpdate()) {
                handler = applicationContext.getBean("updateNoteCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isDelete()) {
                handler = applicationContext.getBean("deleteNoteCommandHandler", NewCommandSourceHandler.class);
            } else {
                throw new UnsupportedCommandException(wrapper.commandName());
            }
        } else if (wrapper.isSurveyResource()) {
            if (wrapper.isRegisterSurvey()) {
                handler = applicationContext.getBean("registerSurveyCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isFullFilSurvey()) {
                handler = applicationContext.getBean("fullFilSurveyCommandHandler", NewCommandSourceHandler.class);
            } else {
                throw new UnsupportedCommandException(wrapper.commandName());
            }
        } else if (wrapper.isLoanDisburseDetailResource()) {
            if (wrapper.isUpdateDisbursementDate()) {
                handler = applicationContext.getBean("updateLoanDisburseDateCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.addAndDeleteDisbursementDetails()) {
                handler = applicationContext.getBean("addAndDeleteLoanDisburseDetailsCommandHandler", NewCommandSourceHandler.class);
            } else {
                throw new UnsupportedCommandException(wrapper.commandName());
            }
        } else if (wrapper.isInterestPauseResource()) {
            if (wrapper.isInterestPauseCreateResource()) {
                handler = applicationContext.getBean("createInterestPauseCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isInterestPauseUpdateResource()) {
                handler = applicationContext.getBean("updateInterestPauseCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isInterestPauseDeleteResource()) {
                handler = applicationContext.getBean("deleteInterestPauseCommandHandler", NewCommandSourceHandler.class);
            } else {
                throw new UnsupportedCommandException(wrapper.commandName());
            }
        } else {
            handler = commandHandlerProvider.getHandler(wrapper.entityName(), wrapper.actionName());
        }

        return handler;
    }

    @Override
    public boolean validateRollbackCommand(final CommandWrapper commandWrapper, final AppUser user) {
        user.validateHasPermissionTo(commandWrapper.getTaskPermissionName());
        boolean isMakerChecker = configurationDomainService.isMakerCheckerEnabledForTask(commandWrapper.taskPermissionName());
        return isMakerChecker && !user.isCheckerSuperUser();
    }

    protected void publishHookEvent(final String entityName, final String actionName, JsonCommand command, final Object result) {
        try {
            final AppUser appUser = context.authenticatedUser(CommandWrapper.wrap(actionName, entityName, null, null));

            final HookEventSource hookEventSource = new HookEventSource(entityName, actionName);

            // TODO: Add support for publishing array events
            if (command.json() != null) {
                Type type = new TypeToken<Map<String, Object>>() {

                }.getType();

                Map<String, Object> myMap;

                try {
                    myMap = gson.fromJson(command.json(), type);
                } catch (Exception e) {
                    throw new PlatformApiDataValidationException("error.msg.invalid.json", "The provided JSON is invalid.",
                            new ArrayList<>(), e);
                }

                Map<String, Object> reqmap = new HashMap<>();
                reqmap.put("entityName", entityName);
                reqmap.put("actionName", actionName);
                reqmap.put("createdBy", context.authenticatedUser().getId());
                reqmap.put("createdByName", context.authenticatedUser().getUsername());
                reqmap.put("createdByFullName", context.authenticatedUser().getDisplayName());

                reqmap.put("request", myMap);
                if (result instanceof CommandProcessingResult) {
                    CommandProcessingResult resultCopy = CommandProcessingResult
                            .fromCommandProcessingResult((CommandProcessingResult) result);

                    reqmap.put("officeId", resultCopy.getOfficeId());
                    reqmap.put("clientId", resultCopy.getClientId());
                    resultCopy.setOfficeId(null);
                    reqmap.put("response", resultCopy);
                } else if (result instanceof ErrorInfo ex) {
                    reqmap.put("status", "Exception");

                    Map<String, Object> errorMap = new HashMap<>();

                    try {
                        errorMap = gson.fromJson(ex.getMessage(), type);
                    } catch (Exception e) {
                        errorMap.put("errorMessage", ex.getMessage());
                    }

                    errorMap.put("errorCode", ex.getErrorCode());
                    errorMap.put("statusCode", ex.getStatusCode());

                    reqmap.put("response", errorMap);
                }

                reqmap.put("timestamp", Instant.now().toString());

                final String serializedResult = toApiJsonSerializer.serialize(reqmap);

                final HookEvent applicationEvent = new HookEvent(hookEventSource, serializedResult, appUser,
                        ThreadLocalContextUtil.getContext());

                applicationContext.publishEvent(applicationEvent);
            }
        } catch (Exception e) {
            log.error("Failed to publish hook event for entity: {}, action: {}", entityName, actionName, e);
        }
    }
}
