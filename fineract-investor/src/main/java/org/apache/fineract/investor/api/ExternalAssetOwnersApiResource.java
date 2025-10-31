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
package org.apache.fineract.investor.api;

import static org.apache.fineract.infrastructure.core.service.CommandParameterUtil.BUY_BACK_COMMAND_VALUE;
import static org.apache.fineract.infrastructure.core.service.CommandParameterUtil.CANCEL_COMMAND_VALUE;
import static org.apache.fineract.infrastructure.core.service.CommandParameterUtil.INTERMEDIARY_SALE_COMMAND_VALUE;
import static org.apache.fineract.infrastructure.core.service.CommandParameterUtil.SALE_COMMAND_VALUE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.batch.command.CommandHandlerRegistry;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.domain.ExternalId;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.PagedRequest;
import org.apache.fineract.infrastructure.security.service.PlatformUserRightsContext;
import org.apache.fineract.investor.api.search.ExternalAssetOwnersSearchApiDelegate;
import org.apache.fineract.investor.config.InvestorModuleIsEnabledCondition;
import org.apache.fineract.investor.data.ExternalOwnerJournalEntryData;
import org.apache.fineract.investor.data.ExternalOwnerTransferJournalEntryData;
import org.apache.fineract.investor.data.ExternalTransferData;
import org.apache.fineract.investor.data.request.ExternalAssetOwnerRequest;
import org.apache.fineract.investor.service.ExternalAssetOwnersReadService;
import org.apache.fineract.investor.service.search.domain.ExternalAssetOwnerSearchRequest;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformServiceCommon;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Path("/v1/external-asset-owners")
@Component
@Tag(name = "External Asset Owners", description = "External Asset Owners")
@RequiredArgsConstructor
@Conditional(InvestorModuleIsEnabledCondition.class)
public class ExternalAssetOwnersApiResource {

    private final PlatformUserRightsContext platformUserRightsContext;
    private final ExternalAssetOwnersReadService externalAssetOwnersReadService;
    private final DefaultToApiJsonSerializer<String> postApiJsonSerializerService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final LoanReadPlatformServiceCommon loanReadPlatformService;
    private final ExternalAssetOwnersSearchApiDelegate delegate;

    private static final CommandHandlerRegistry<String, Long, String, CommandWrapper> COMMAND_HANDLER_REGISTRY = new CommandHandlerRegistry<>(
            Map.of(CANCEL_COMMAND_VALUE, (id, json) -> new CommandWrapperBuilder().cancelTransactionByIdToExternalAssetOwner(id).build(),
                    INTERMEDIARY_SALE_COMMAND_VALUE,
                    (id, json) -> new CommandWrapperBuilder().withJson(json).intermediarySaleLoanToExternalAssetOwner(id).build(),
                    SALE_COMMAND_VALUE, (id, json) -> new CommandWrapperBuilder().withJson(json).saleLoanToExternalAssetOwner(id).build(),
                    BUY_BACK_COMMAND_VALUE,
                    (id, json) -> new CommandWrapperBuilder().withJson(json).buybackLoanToExternalAssetOwner(id).build()));

    @POST
    @Path("/transfers/loans/{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = ExternalAssetOwnerRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ExternalAssetOwnersApiResourceSwagger.PostInitiateTransferResponse.class))),
            @ApiResponse(responseCode = "403", description = "Transfer cannot be initiated") })
    public CommandProcessingResult transferRequestWithLoanId(@PathParam("loanId") final Long loanId,
            @QueryParam("command") @Parameter(description = "command") final String commandParam,
            @Parameter(hidden = true) ExternalAssetOwnerRequest assetOwnerReq) {
        platformUserRightsContext.isAuthenticated();
        final String serializedAssetRequest = postApiJsonSerializerService.serialize(assetOwnerReq);
        final CommandWrapper commandRequest = COMMAND_HANDLER_REGISTRY.execute(commandParam, loanId, serializedAssetRequest,
                new UnrecognizedQueryParamException("command", commandParam));
        return this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
    }

    @POST
    @Path("/transfers/loans/external-id/{loanExternalId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = ExternalAssetOwnerRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ExternalAssetOwnersApiResourceSwagger.PostInitiateTransferResponse.class))),
            @ApiResponse(responseCode = "403", description = "Transfer cannot be initiated") })
    public CommandProcessingResult transferRequestWithLoanExternalId(@PathParam("loanExternalId") final String externalLoanId,
            @QueryParam("command") @Parameter(description = "command") final String commandParam,
            @Parameter(hidden = true) ExternalAssetOwnerRequest assetOwnerReq) {
        platformUserRightsContext.isAuthenticated();
        final Long loanId = loanReadPlatformService.getLoanIdByLoanExternalId(externalLoanId);
        final String serializedAssetRequest = postApiJsonSerializerService.serialize(assetOwnerReq);
        final CommandWrapper commandRequest = COMMAND_HANDLER_REGISTRY.execute(commandParam, loanId, serializedAssetRequest,
                new UnrecognizedQueryParamException("command", commandParam));
        return this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

    }

    @POST
    @Path("/transfers/{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ExternalAssetOwnersApiResourceSwagger.PostInitiateTransferResponse.class))),
            @ApiResponse(responseCode = "403", description = "Transfer cannot be initiated") })
    public CommandProcessingResult transferRequestWithId(@PathParam("id") final Long id,
            @QueryParam("command") @Parameter(description = "command") final String commandParam,
            @Parameter(hidden = true) ExternalAssetOwnerRequest assetOwnerReq) {
        platformUserRightsContext.isAuthenticated();
        final String serializedAssetRequest = postApiJsonSerializerService.serialize(assetOwnerReq);
        final CommandWrapper commandRequest = COMMAND_HANDLER_REGISTRY.execute(commandParam, id, serializedAssetRequest,
                new UnrecognizedQueryParamException("command", commandParam));
        return this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
    }

    @POST
    @Path("/transfers/external-id/{externalId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ExternalAssetOwnersApiResourceSwagger.PostInitiateTransferResponse.class))),
            @ApiResponse(responseCode = "403", description = "Transfer cannot be initiated") })
    public CommandProcessingResult transferRequestWithId(@PathParam("externalId") final String externalId,
            @QueryParam("command") @Parameter(description = "command") final String commandParam,
            @Parameter(hidden = true) ExternalAssetOwnerRequest assetOwnerReq) {
        platformUserRightsContext.isAuthenticated();
        final Long id = externalAssetOwnersReadService.retrieveLastTransferIdByExternalId(new ExternalId(externalId));
        final String serializedAssetRequest = postApiJsonSerializerService.serialize(assetOwnerReq);
        final CommandWrapper commandRequest = COMMAND_HANDLER_REGISTRY.execute(commandParam, id, serializedAssetRequest,
                new UnrecognizedQueryParamException("command", commandParam));
        return this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
    }

    @GET
    @Path("/transfers")
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(tags = {
            "External Asset Owners" }, summary = "Retrieve External Asset Owner Transfers", description = "Retrieve External Asset Owner Transfer items by transferExternalId, loanId or loanExternalId")
    public Page<ExternalTransferData> getTransfers(
            @QueryParam("transferExternalId") @Parameter(description = "transferExternalId") final String transferExternalId,
            @QueryParam("loanId") @Parameter(description = "loanId") final Long loanId,
            @QueryParam("loanExternalId") @Parameter(description = "loanExternalId") final String loanExternalId,
            @QueryParam("offset") @Parameter(description = "offset") final Integer offset,
            @QueryParam("limit") @Parameter(description = "limit") final Integer limit) {
        platformUserRightsContext.isAuthenticated();
        return externalAssetOwnersReadService.retrieveTransferData(loanId, loanExternalId, transferExternalId, offset, limit);
    }

    @GET
    @Path("/transfers/active-transfer")
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(tags = {
            "External Asset Owners" }, summary = "Retrieve Active Asset Owner Transfer", description = "Retrieve Active External Asset Owner Transfer by transferExternalId, loanId or loanExternalId")
    public ExternalTransferData getActiveTransfer(
            @QueryParam("transferExternalId") @Parameter(description = "transferExternalId") final String transferExternalId,
            @QueryParam("loanId") @Parameter(description = "loanId") final Long loanId,
            @QueryParam("loanExternalId") @Parameter(description = "loanExternalId") final String loanExternalId) {
        platformUserRightsContext.isAuthenticated();
        return externalAssetOwnersReadService.retrieveActiveTransferData(loanId, loanExternalId, transferExternalId);
    }

    @GET
    @Path("/transfers/{transferId}/journal-entries")
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(tags = {
            "External Asset Owners" }, summary = "Retrieve Journal Entries of Transfer", description = "Retrieve Journal entries of transfer by transferId")
    public ExternalOwnerTransferJournalEntryData getJournalEntriesOfTransfer(
            @PathParam("transferId") @Parameter(description = "transferId") final Long transferId,
            @QueryParam("offset") @Parameter(description = "offset") final Integer offset,
            @QueryParam("limit") @Parameter(description = "limit") final Integer limit) {
        platformUserRightsContext.isAuthenticated();
        return externalAssetOwnersReadService.retrieveJournalEntriesOfTransfer(transferId, offset, limit);
    }

    @GET
    @Path("/owners/external-id/{ownerExternalId}/journal-entries")
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(tags = {
            "External Asset Owners" }, summary = "Retrieve Journal Entries of Owner", description = "Retrieve Journal entries of owner by owner externalId")
    public ExternalOwnerJournalEntryData getJournalEntriesOfOwner(
            @PathParam("ownerExternalId") @Parameter(description = "ownerExternalId") final String ownerExternalId,
            @QueryParam("offset") @Parameter(description = "offset") final Integer offset,
            @QueryParam("limit") @Parameter(description = "limit") final Integer limit) {
        platformUserRightsContext.isAuthenticated();
        return externalAssetOwnersReadService.retrieveJournalEntriesOfOwner(ownerExternalId, offset, limit);
    }

    @POST
    @Path("/search")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Search External Asset Owner Transfers by text or date ranges to settlement or effective dates")
    public Page<ExternalTransferData> searchInvestorData(@Parameter PagedRequest<ExternalAssetOwnerSearchRequest> request) {
        platformUserRightsContext.isAuthenticated();
        return delegate.searchInvestorData(request);
    }
}
