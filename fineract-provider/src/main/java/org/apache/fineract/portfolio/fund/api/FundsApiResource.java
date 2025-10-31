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
package org.apache.fineract.portfolio.fund.api;

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
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.codes.data.CodeData;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.fund.data.FundData;
import org.apache.fineract.portfolio.fund.data.FundRequest;
import org.apache.fineract.portfolio.fund.service.FundReadPlatformService;
import org.springframework.stereotype.Component;

@Path("/v1/funds")
@Component
@Tag(name = "Funds", description = "")
@RequiredArgsConstructor
public class FundsApiResource {

    /**
     * The set of parameters that are supported in response for {@link CodeData}
     */
    private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "name", "externalId"));

    private static final String RESOURCE_NAME_FOR_PERMISSIONS = "FUND";

    private final PlatformSecurityContext context;
    private final FundReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<FundData> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Funds", description = "Returns the list of funds.\n" + "\n" + "Example Requests:\n" + "\n" + "funds")
    public List<FundData> retrieveFunds() {
        context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);
        return readPlatformService.retrieveAllFunds();
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Create a Fund", description = "Creates a Fund")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = FundRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = FundsApiResourceSwagger.PostFundsResponse.class))) })
    public CommandProcessingResult createFund(@Parameter(hidden = true) final FundRequest fundRequest) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().createFund().withJson(toApiJsonSerializer.serialize(fundRequest))
                .build();
        return commandsSourceWritePlatformService.logCommandSource(commandRequest);
    }

    @GET
    @Path("{fundId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve a Fund", description = "Returns the details of a Fund.\n" + "\n" + "Example Requests:\n" + "\n"
            + "funds/1")
    public FundData retrieveFund(@PathParam("fundId") @Parameter(description = "fundId") final Long fundId) {
        context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);
        return readPlatformService.retrieveFund(fundId);
    }

    @PUT
    @Path("{fundId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Update a Fund", description = "Updates the details of a fund.")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = FundRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = FundsApiResourceSwagger.PutFundsFundIdResponse.class))) })
    public String updateFund(@PathParam("fundId") @Parameter(description = "fundId") final Long fundId,
            @Parameter(hidden = true) final FundRequest fundRequest) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateFund(fundId)
                .withJson(toApiJsonSerializer.serialize(fundRequest)).build();
        final CommandProcessingResult result = commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return toApiJsonSerializer.serialize(result);
    }
}
