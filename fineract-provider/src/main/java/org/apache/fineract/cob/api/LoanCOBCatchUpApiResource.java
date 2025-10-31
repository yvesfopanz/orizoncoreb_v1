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
package org.apache.fineract.cob.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.cob.data.IsCatchUpRunningDTO;
import org.apache.fineract.cob.data.OldestCOBProcessedLoanDTO;
import org.apache.fineract.cob.service.LoanCOBCatchUpService;
import org.apache.fineract.infrastructure.core.exception.JobIsNotFoundOrNotEnabledException;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.springframework.stereotype.Component;

@Path("/v1/loans")
@Component
@Tag(name = "Loan COB Catch Up", description = "")
@RequiredArgsConstructor
public class LoanCOBCatchUpApiResource {

    private final Optional<LoanCOBCatchUpService> loanCOBCatchUpServiceOp;

    @GET
    @Path("oldest-cob-closed")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieves the oldest COB processed loan", description = "Retrieves the COB business date and the oldest COB processed loan")
    public OldestCOBProcessedLoanDTO getOldestCOBProcessedLoan() {
        return loanCOBCatchUpServiceOp.map(LoanCOBCatchUpService::getOldestCOBProcessedLoan)
                .orElseThrow(() -> new JobIsNotFoundOrNotEnabledException(JobName.LOAN_COB.name()));
    }

    @POST
    @Path("catch-up")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Executes Loan COB Catch Up", description = "Executes the Loan COB job on every day from the oldest Loan to the current COB business date")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "All loans are up to date"),
            @ApiResponse(responseCode = "202", description = "Catch Up has been started"),
            @ApiResponse(responseCode = "400", description = "Catch Up is already running") })
    public Response executeLoanCOBCatchUp() {
        return loanCOBCatchUpServiceOp.map(loanCOBCatchUpService -> {
            if (loanCOBCatchUpService.isCatchUpRunning().isCatchUpRunning()) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            loanCOBCatchUpService.unlockHardLockedLoans();
            OldestCOBProcessedLoanDTO oldestCOBProcessedLoan = loanCOBCatchUpService.getOldestCOBProcessedLoan();

            if (oldestCOBProcessedLoan.getCobProcessedDate().equals(oldestCOBProcessedLoan.getCobBusinessDate())) {
                return Response.status(Response.Status.OK).build();
            }
            loanCOBCatchUpService.executeLoanCOBCatchUp();
            return Response.status(Response.Status.ACCEPTED).build();
        }).orElseThrow(() -> new JobIsNotFoundOrNotEnabledException(JobName.LOAN_COB.name()));
    }

    @GET
    @Path("is-catch-up-running")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieves whether Loan COB catch up is running", description = "Retrieves whether Loan COB catch up is running, and the current execution date if it is running.")
    public IsCatchUpRunningDTO isCatchUpRunning() {
        return loanCOBCatchUpServiceOp.map(LoanCOBCatchUpService::isCatchUpRunning)
                .orElseThrow(() -> new JobIsNotFoundOrNotEnabledException(JobName.LOAN_COB.name()));
    }
}
