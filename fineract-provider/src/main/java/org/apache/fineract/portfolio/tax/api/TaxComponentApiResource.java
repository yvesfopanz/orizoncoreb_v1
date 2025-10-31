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
package org.apache.fineract.portfolio.tax.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.tax.data.TaxComponentData;
import org.apache.fineract.portfolio.tax.request.TaxComponentRequest;
import org.apache.fineract.portfolio.tax.service.TaxReadPlatformService;
import org.springframework.stereotype.Component;

@Path("/v1/taxes/component")
@Component
@Tag(name = "Tax Components", description = "This defines the Tax Components")
@RequiredArgsConstructor
public class TaxComponentApiResource {

    private static final String RESOURCE_NAME_FOR_PERMISSIONS = "TAXCOMPONENT";

    private final PlatformSecurityContext context;
    private final TaxReadPlatformService readPlatformService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final DefaultToApiJsonSerializer<String> toApiJsonSerializer;

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "List Tax Components", description = "List Tax Components")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaxComponentApiResourceSwagger.GetTaxesComponentsResponse.class)))) })
    public List<TaxComponentData> retrieveAllTaxComponents() {
        context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);
        return readPlatformService.retrieveAllTaxComponents();
    }

    @GET
    @Path("{taxComponentId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Tax Component", description = "Retrieve Tax Component")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = TaxComponentApiResourceSwagger.GetTaxesComponentsResponse.class))) })
    public TaxComponentData retrieveTaxComponent(
            @PathParam("taxComponentId") @Parameter(description = "taxComponentId") final Long taxComponentId) {
        context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);
        return readPlatformService.retrieveTaxComponentData(taxComponentId);
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public TaxComponentData retrieveTemplate() {
        context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);
        return readPlatformService.retrieveTaxComponentTemplate();
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Create a new Tax Component", description = "Creates a new Tax Component\n\n"
            + "Mandatory Fields: name, percentage\n\n"
            + "Optional Fields: debitAccountType, debitAcountId, creditAccountType, creditAcountId, startDate")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = TaxComponentApiResourceSwagger.PostTaxesComponentsRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = TaxComponentApiResourceSwagger.PostTaxesComponentsResponse.class))) })
    public CommandProcessingResult createTaxComponent(@Parameter(hidden = true) TaxComponentRequest taxComponentRequest) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().createTaxComponent()
                .withJson(toApiJsonSerializer.serialize(taxComponentRequest)).build();
        return commandsSourceWritePlatformService.logCommandSource(commandRequest);
    }

    @PUT
    @Path("{taxComponentId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Update Tax Component", description = "Updates Tax component. Debit and credit account details cannot be modified. All the future tax components would be replaced with the new percentage.")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = TaxComponentApiResourceSwagger.PutTaxesComponentsTaxComponentIdRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = TaxComponentApiResourceSwagger.PutTaxesComponentsTaxComponentIdResponse.class))) })
    public CommandProcessingResult updateTaxCompoent(
            @PathParam("taxComponentId") @Parameter(description = "taxComponentId") final Long taxComponentId,
            @Parameter(hidden = true) TaxComponentRequest taxComponentRequest) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateTaxComponent(taxComponentId)
                .withJson(toApiJsonSerializer.serialize(taxComponentRequest)).build();
        return commandsSourceWritePlatformService.logCommandSource(commandRequest);
    }

}
