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
package org.apache.fineract.infrastructure.configuration.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationData;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;
import org.apache.fineract.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.stereotype.Component;

@Path("/v1/configurations")
@Component
@Tag(name = "Global Configuration", description = "Global configuration related to set of supported enable/disable configurations:\n" + "\n"
        + "maker-checker - defaults to false - if true turns on maker-checker functionality\n"
        + "reschedule-future-repayments - defaults to false - if true reschedules repayemnts which falls on a non-working day to configured repayment rescheduling rule\n"
        + "allow-transactions-on-non-workingday - defaults to false - if true allows transactions on non-working days\n"
        + "reschedule-repayments-on-holidays - defaults to false - if true reschedules repayemnts which falls on a non-working day to defined reschedule date\n"
        + "allow-transactions-on-holiday - defaults to false - if true allows transactions on holidays\n"
        + "savings-interest-posting-current-period-end - Set it at the database level before any savings interest is posted. When set as false(default), interest will be posted on the first date of next period. If set as true, interest will be posted on last date of current period. There is no difference in the interest amount posted.\n"
        + "financial-year-beginning-month - Set it at the database level before any savings interest is posted. Allowed values 1 - 12 (January - December). Interest posting periods are evaluated based on this configuration.\n"
        + "meetings-mandatory-for-jlg-loans - if set to true, enforces all JLG loans to follow a meeting schedule belonging to either the parent group or Center.")
@RequiredArgsConstructor
public class GlobalConfigurationApiResource {

    private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("globalConfiguration"));

    private static final String RESOURCE_NAME_FOR_PERMISSIONS = "CONFIGURATION";

    private final PlatformSecurityContext context;
    private final ConfigurationReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<GlobalConfigurationData> toApiJsonSerializer;
    private final DefaultToApiJsonSerializer<GlobalConfigurationPropertyData> propertyDataJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Global Configuration | Retrieve Global Configuration for surveys", description = "Returns the list global enable/disable configurations.\n"
            + "\n" + "Example Requests:\n" + "\n" + "configurations\n\n" + "\n"
            + "Returns the list global enable/disable survey configurations.\n" + "\n" + "Example Requests:\n" + "\n"
            + "configurations/survey")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of example \\n response \\nsurveys response   \\ngiven below", content = @Content(schema = @Schema(implementation = GlobalConfigurationApiResourceSwagger.GetGlobalConfigurationsResponse.class))) })
    public String retrieveConfiguration(@Context final UriInfo uriInfo,
            @DefaultValue("false") @QueryParam("survey") @Parameter(description = "survey") final boolean survey) {

        this.context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);

        final GlobalConfigurationData configurationData = this.readPlatformService.retrieveGlobalConfiguration(survey);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, configurationData, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("{configId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Global Configuration", description = "Returns a global enable/disable configurations.\n" + "\n"
            + "Example Requests:\n" + "\n" + "configurations/1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = GlobalConfigurationPropertyData.class))) })
    public String retrieveOne(@PathParam("configId") @Parameter(description = "configId") final Long configId,
            @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);

        final GlobalConfigurationPropertyData configurationData = this.readPlatformService.retrieveGlobalConfiguration(configId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.propertyDataJsonSerializer.serialize(settings, configurationData, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("name/{name}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Global Configuration", description = "Returns a global enable/disable configuration.\n" + "\n"
            + "Example Requests:\n" + "\n" + "configurations/name/enable-address")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = GlobalConfigurationPropertyData.class))) })
    public String retrieveOneByName(@PathParam("name") @Parameter(description = "name") final String name, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);

        final GlobalConfigurationPropertyData configurationData = this.readPlatformService.retrieveGlobalConfiguration(name);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.propertyDataJsonSerializer.serialize(settings, configurationData, RESPONSE_DATA_PARAMETERS);
    }

    @PUT
    @Path("{configId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Update Global Configuration", description = "Updates an enable/disable global configuration item.")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = GlobalConfigurationApiResourceSwagger.PutGlobalConfigurationsRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = GlobalConfigurationApiResourceSwagger.PutGlobalConfigurationsResponse.class))) })
    public String updateConfiguration(@PathParam("configId") @Parameter(description = "configId") final Long configId,
            @Parameter(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateGlobalConfiguration(configId) //
                .withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("/name/{configName}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Update Global Configuration by name", description = "Updates an enable/disable global configuration item by name")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = GlobalConfigurationApiResourceSwagger.PutGlobalConfigurationsRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = GlobalConfigurationApiResourceSwagger.PutGlobalConfigurationsResponse.class))) })
    public String updateConfigurationByName(@PathParam("configName") @Parameter(description = "configName") final String configName,
            @Parameter(hidden = true) final String apiRequestBodyAsJson) {

        // TODO: Would be better to support string based identifier in Commands and resolve the entity by name in the
        // service
        final GlobalConfigurationPropertyData configurationData = this.readPlatformService.retrieveGlobalConfiguration(configName);

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateGlobalConfiguration(configurationData.getId()) //
                .withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
}
