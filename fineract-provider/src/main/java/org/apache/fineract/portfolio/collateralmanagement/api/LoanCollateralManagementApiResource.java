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
package org.apache.fineract.portfolio.collateralmanagement.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.collateralmanagement.data.LoanCollateralResponseData;
import org.apache.fineract.portfolio.collateralmanagement.service.LoanCollateralManagementReadPlatformService;
import org.springframework.stereotype.Component;

@Path("/v1/loan-collateral-management")
@Component
@Tag(name = "Loan Collateral Management", description = "Loan Collateral Management is for managing collateral operations")
@RequiredArgsConstructor
public class LoanCollateralManagementApiResource {

    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final LoanCollateralManagementReadPlatformService loanCollateralManagementReadPlatformService;

    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Operation(description = "Delete Loan Collateral", summary = "Delete Loan Collateral")
    public CommandProcessingResult deleteLoanCollateral(@PathParam("loanId") @Parameter(description = "loanId") final Long loanId,
            @PathParam("id") @Parameter(description = "loan collateral id") final Long id) {

        final CommandWrapper commandWrapper = new CommandWrapperBuilder().deleteLoanCollateral(loanId, id).build();
        return this.commandsSourceWritePlatformService.logCommandSource(commandWrapper);
    }

    @GET
    @Path("{collateralId}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    @Operation(description = "Get Loan Collateral Details", summary = "Get Loan Collateral Details")
    public LoanCollateralResponseData getLoanCollateral(
            @PathParam("collateralId") @Parameter(description = "collateralId") final Long collateralId) {
        return this.loanCollateralManagementReadPlatformService.getLoanCollateralResponseData(collateralId);
    }

}
