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
package org.apache.fineract.portfolio.client.api;

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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.address.data.AddressData;
import org.apache.fineract.portfolio.address.filter.ClientAddressSearchParam;
import org.apache.fineract.portfolio.address.service.AddressReadPlatformServiceImpl;
import org.apache.fineract.portfolio.client.data.ClientAddressRequest;
import org.springframework.stereotype.Component;

@Path("/v1/client")
@Component
@Tag(name = "Clients Address", description = "Address module is an optional module and can be configured into the system by using GlobalConfiguration setting: enable-address. In order to activate Address module, we need to enable the configuration, enable-address by setting its value to true.")
@RequiredArgsConstructor
public class ClientAddressApiResource {

    private static final String RESOURCE_NAME_FOR_PERMISSIONS = "Address";
    private final PlatformSecurityContext context;
    private final AddressReadPlatformServiceImpl readPlatformService;
    private final DefaultToApiJsonSerializer<AddressData> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @GET
    @Path("addresses/template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AddressData getAddressesTemplate() {
        context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);
        return readPlatformService.retrieveTemplate();

    }

    @POST
    @Path("/{clientid}/addresses")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Create an address for a Client", description = "Mandatory Fields : \n" + "type and clientId")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = ClientAddressRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ClientAddressApiResourcesSwagger.PostClientClientIdAddressesResponse.class))) })
    public CommandProcessingResult addClientAddress(@QueryParam("type") @Parameter(description = "type") final long addressTypeId,
            @PathParam("clientid") @Parameter(description = "clientId") final long clientid,
            @Parameter(hidden = true) ClientAddressRequest clientAddressRequest) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().addClientAddress(clientid, addressTypeId)
                .withJson(toApiJsonSerializer.serialize(clientAddressRequest)).build();

        return commandsSourceWritePlatformService.logCommandSource(commandRequest);
    }

    @GET
    @Path("/{clientid}/addresses")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "List all addresses for a Client", description = "Example Requests:\n" + "\n" + "client/1/addresses\n" + "\n"
            + "\n" + "clients/1/addresses?status=false,true&&type=1,2,3")
    public List<AddressData> getAddresses(@QueryParam("status") @Parameter(description = "status") final String status,
            @QueryParam("type") @Parameter(description = "type") final long addressTypeId,
            @PathParam("clientid") @Parameter(description = "clientId") final long clientid) {
        context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);
        return readPlatformService.retrieveBySearchParam(new ClientAddressSearchParam(clientid, addressTypeId, status));
    }

    @PUT
    @Path("/{clientid}/addresses")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Update an address for a Client", description = "All the address fields can be updated by using update client address API\n"
            + "\n" + "Mandatory Fields\n" + "type and addressId")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = ClientAddressRequest.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ClientAddressApiResourcesSwagger.PutClientClientIdAddressesResponse.class))) })
    public CommandProcessingResult updateClientAddress(@PathParam("clientid") @Parameter(description = "clientId") final long clientid,
            @Parameter(hidden = true) ClientAddressRequest clientAddressRequest) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateClientAddress(clientid)
                .withJson(toApiJsonSerializer.serialize(clientAddressRequest)).build();
        return commandsSourceWritePlatformService.logCommandSource(commandRequest);
    }
}
