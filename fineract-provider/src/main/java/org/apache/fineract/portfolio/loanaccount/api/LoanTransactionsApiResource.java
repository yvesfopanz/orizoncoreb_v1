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
package org.apache.fineract.portfolio.loanaccount.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.api.DateParam;
import org.apache.fineract.infrastructure.core.api.jersey.Pagination;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.DateFormat;
import org.apache.fineract.infrastructure.core.domain.ExternalId;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.CommandParameterUtil;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.ExternalIdFactory;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.data.LoanRepaymentScheduleInstallmentData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.apache.fineract.portfolio.loanaccount.exception.InvalidLoanTransactionTypeException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanNotFoundException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanTransactionNotFoundException;
import org.apache.fineract.portfolio.loanaccount.service.LoanChargePaidByReadService;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Path("/v1/loans")
@Component
@AllArgsConstructor
@Tag(name = "Loan Transactions", description = "Capabilities include loan repayment's, interest waivers and the ability to 'adjust' an existing transaction. An 'adjustment' of a transaction is really a 'reversal' of existing transaction followed by creation of a new transaction with the provided details.")
public class LoanTransactionsApiResource {

    public static final String CHARGE_OFF_COMMAND_VALUE = "charge-off";
    public static final String UNDO_CHARGE_OFF_COMMAND_VALUE = "undo-charge-off";
    public static final String DOWN_PAYMENT = "downPayment";
    public static final String UNDO_REAGE = "undoReAge";
    public static final String REAGE = "reAge";
    public static final String REAMORTIZE = "reAmortize";
    public static final String UNDO_REAMORTIZE = "undoReAmortize";
    public static final String CAPITALIZED_INCOME = "capitalizedIncome";
    public static final String INTEREST_REFUND_COMMAND_VALUE = "interest-refund";
    private final Set<String> responseDataParameters = new HashSet<>(Arrays.asList("id", "type", "date", "currency", "amount", "externalId",
            LoanApiConstants.REVERSAL_EXTERNAL_ID_PARAMNAME, LoanApiConstants.REVERSED_ON_DATE_PARAMNAME));

    private static final String RESOURCE_NAME_FOR_PERMISSIONS = "LOAN";

    private final PlatformSecurityContext context;
    private final LoanReadPlatformService loanReadPlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final DefaultToApiJsonSerializer<LoanTransactionData> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final PaymentTypeReadPlatformService paymentTypeReadPlatformService;
    private final LoanChargePaidByReadService loanChargePaidByReadService;

    @GET
    @Path("{loanId}/transactions/template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Loan Transaction Template", description = "This is a convenience resource. It can be useful when building maintenance user interface screens for client applications. The template data returned consists of any or all of:\n"
            + "\n" + "Field Defaults\n" + "Allowed Value Lists\n\n" + "Example Requests:\n" + "\n"
            + "loans/1/transactions/template?command=repayment" + "loans/1/transactions/template?command=merchantIssuedRefund"
            + "loans/1/transactions/template?command=payoutRefund" + "loans/1/transactions/template?command=goodwillCredit" + "\n"
            + "loans/1/transactions/template?command=waiveinterest" + "\n" + "loans/1/transactions/template?command=writeoff" + "\n"
            + "loans/1/transactions/template?command=close-rescheduled" + "\n" + "loans/1/transactions/template?command=close" + "\n"
            + "loans/1/transactions/template?command=disburse" + "\n" + "loans/1/transactions/template?command=disburseToSavings" + "\n"
            + "loans/1/transactions/template?command=recoverypayment" + "\n" + "loans/1/transactions/template?command=prepayLoan" + "\n"
            + "loans/1/transactions/template?command=refundbycash" + "\n" + "loans/1/transactions/template?command=refundbytransfer" + "\n"
            + "loans/1/transactions/template?command=foreclosure" + "\n" + "loans/1/transactions/template?command=interestPaymentWaiver"
            + "\n" + "loans/1/transactions/template?command=creditBalanceRefund (returned 'amount' field will have the overpaid value)"
            + "\n" + "loans/1/transactions/template?command=charge-off" + "\n" + "loans/1/transactions/template?command=downPayment" + "\n"
            + "loans/1/transactions/template?command=interest-refund")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.GetLoansLoanIdTransactionsTemplateResponse.class))) })
    public String retrieveTransactionTemplate(@PathParam("loanId") @Parameter(description = "loanId", required = true) final Long loanId,
            @QueryParam("command") @Parameter(description = "command") final String commandParam, @Context final UriInfo uriInfo,
            @QueryParam("dateFormat") @Parameter(description = "dateFormat") final String rawDateFormat,
            @QueryParam("transactionDate") @Parameter(description = "transactionDate") final DateParam transactionDateParam,
            @QueryParam("locale") @Parameter(description = "locale") final String locale,
            @QueryParam("transactionId") @Parameter(description = "transactionId") final Long transactionId) {

        final DateFormat dateFormat = StringUtils.isBlank(rawDateFormat) ? null : new DateFormat(rawDateFormat);

        return retrieveTransactionTemplate(loanId, null, commandParam, uriInfo, dateFormat, transactionDateParam, locale, transactionId);
    }

    @GET
    @Path("external-id/{loanExternalId}/transactions/template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Loan Transaction Template", description = "This is a convenience resource. It can be useful when building maintenance user interface screens for client applications. The template data returned consists of any or all of:\n"
            + "\n" + "Field Defaults\n" + "Allowed Value Lists\n\n" + "Example Requests:\n" + "\n"
            + "loans/1/transactions/template?command=repayment" + "loans/1/transactions/template?command=merchantIssuedRefund"
            + "loans/1/transactions/template?command=payoutRefund" + "loans/1/transactions/template?command=goodwillCredit" + "\n"
            + "loans/1/transactions/template?command=waiveinterest" + "\n" + "loans/1/transactions/template?command=writeoff" + "\n"
            + "loans/1/transactions/template?command=close-rescheduled" + "\n" + "loans/1/transactions/template?command=close" + "\n"
            + "loans/1/transactions/template?command=disburse" + "\n" + "loans/1/transactions/template?command=disburseToSavings" + "\n"
            + "loans/1/transactions/template?command=recoverypayment" + "\n" + "loans/1/transactions/template?command=prepayLoan" + "\n"
            + "loans/1/transactions/template?command=refundbycash" + "\n" + "loans/1/transactions/template?command=refundbytransfer" + "\n"
            + "loans/1/transactions/template?command=foreclosure" + "\n" + "loans/1/transactions/template?command=interestPaymentWaiver"
            + "\n" + "loans/1/transactions/template?command=creditBalanceRefund (returned 'amount' field will have the overpaid value)"
            + "\n" + "loans/1/transactions/template?command=charge-off" + "\n" + "loans/1/transactions/template?command=downPayment" + "\n"
            + "loans/1/transactions/template?command=interest-refund")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.GetLoansLoanIdTransactionsTemplateResponse.class))) })
    public String retrieveTransactionTemplate(
            @PathParam("loanExternalId") @Parameter(description = "loanExternalId", required = true) final String loanExternalId,
            @QueryParam("command") @Parameter(description = "command") final String commandParam, @Context final UriInfo uriInfo,
            @QueryParam("dateFormat") @Parameter(description = "dateFormat") final String rawDateFormat,
            @QueryParam("transactionDate") @Parameter(description = "transactionDate") final DateParam transactionDateParam,
            @QueryParam("locale") @Parameter(description = "locale") final String locale,
            @QueryParam("transactionId") @Parameter(description = "transactionId") final Long transactionId) {

        final DateFormat dateFormat = StringUtils.isBlank(rawDateFormat) ? null : new DateFormat(rawDateFormat);

        return retrieveTransactionTemplate(null, loanExternalId, commandParam, uriInfo, dateFormat, transactionDateParam, locale,
                transactionId);
    }

    @GET
    @Path("{loanId}/transactions/{transactionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve a Transaction Details", description = "Retrieves a Transaction Details\n\n" + "Example Request:\n" + "\n"
            + "loans/5/transactions/3")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.GetLoansLoanIdTransactionsTransactionIdResponse.class))) })
    public String retrieveTransaction(@PathParam("loanId") @Parameter(description = "loanId", required = true) final Long loanId,
            @PathParam("transactionId") @Parameter(description = "transactionId", required = true) final Long transactionId,
            @QueryParam("fields") @Parameter(in = ParameterIn.QUERY, name = "fields", description = "Optional Loan Transaction attribute list to be in the response", required = false, example = "id,date,amount") final String fields,
            @Context final UriInfo uriInfo) {

        return retrieveTransaction(loanId, null, transactionId, null, uriInfo);
    }

    @GET
    @Path("{loanId}/transactions/external-id/{externalTransactionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve a Transaction Details", description = "Retrieves a Transaction Details\n\n" + "Example Request:\n" + "\n"
            + "loans/5/transactions/external-id/5dd80a7c-ccba-4446-b378-01eb6f53e871")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.GetLoansLoanIdTransactionsTransactionIdResponse.class))) })
    public String retrieveTransactionByTransactionExternalId(
            @PathParam("loanId") @Parameter(description = "loanId", required = true) final Long loanId,
            @PathParam("externalTransactionId") @Parameter(description = "externalTransactionId", required = true) final String externalTransactionId,
            @QueryParam("fields") @Parameter(in = ParameterIn.QUERY, name = "fields", description = "Optional Loan Transaction attribute list to be in the response", required = false, example = "id,date,amount") final String fields,
            @Context final UriInfo uriInfo) {

        return retrieveTransaction(loanId, null, null, externalTransactionId, uriInfo);
    }

    @GET
    @Path("external-id/{loanExternalId}/transactions/{transactionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve a Transaction Details", description = "Retrieves a Transaction Details\n\n" + "Example Request:\n" + "\n"
            + "loans/5/transactions/3")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.GetLoansLoanIdTransactionsTransactionIdResponse.class))) })
    public String retrieveTransactionByLoanExternalIdAndTransactionId(
            @PathParam("loanExternalId") @Parameter(description = "loanExternalId", required = true) final String loanExternalId,
            @PathParam("transactionId") @Parameter(description = "transactionId", required = true) final Long transactionId,
            @QueryParam("fields") @Parameter(in = ParameterIn.QUERY, name = "fields", description = "Optional Loan Transaction attribute list to be in the response", required = false, example = "id,date,amount") final String fields,
            @Context final UriInfo uriInfo) {

        return retrieveTransaction(null, loanExternalId, transactionId, null, uriInfo);
    }

    @GET
    @Path("external-id/{loanExternalId}/transactions/external-id/{externalTransactionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve a Transaction Details", description = "Retrieves a Transaction Details\n\n" + "Example Request:\n" + "\n"
            + "loans/external-id/7dd80a7c-ycba-a446-t378-91eb6f53e854/transactions/external-id/5dd80a7c-ccba-4446-b378-01eb6f53e871")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.GetLoansLoanIdTransactionsTransactionIdResponse.class))) })
    public String retrieveTransactionByLoanExternalIdAndTransactionExternalId(
            @PathParam("loanExternalId") @Parameter(description = "loanExternalId", required = true) final String loanExternalId,
            @PathParam("externalTransactionId") @Parameter(description = "externalTransactionId", required = true) final String externalTransactionId,
            @QueryParam("fields") @Parameter(in = ParameterIn.QUERY, name = "fields", description = "Optional Loan Transaction attribute list to be in the response", required = false, example = "id,date,amount") final String fields,
            @Context final UriInfo uriInfo) {

        return retrieveTransaction(null, loanExternalId, null, externalTransactionId, uriInfo);
    }

    @GET
    @Path("{loanId}/transactions")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Transactions", description = "Retrieves transactions of a loan")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.GetLoansLoanIdTransactionsResponse.class))) })
    public Page<LoanTransactionData> retrieveTransactionsByLoanId(
            @PathParam("loanId") @Parameter(description = "loanId", required = true) final Long loanId,
            @QueryParam("excludedTypes") @Parameter(description = "excludedTypes", in = ParameterIn.QUERY, schema = @Schema(enumAsRef = true)) final List<LoanTransactionApiConstants.TransactionType> excludedTypes,
            @QueryParam("page") @Parameter(description = "page") final Integer page,
            @QueryParam("size") @Parameter(description = "size") final Integer size,
            @QueryParam("sort") @Parameter(description = "sort") final String sort, @Parameter(hidden = true) @Pagination Pageable pageable,
            @Context final UriInfo uriInfo) {
        return retrieveTransactions(loanId, null, excludedTypes, pageable, uriInfo);
    }

    @GET
    @Path("external-id/{loanExternalId}/transactions")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Retrieve Transactions", description = "Retrieves transactions of a loan")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.GetLoansLoanIdTransactionsResponse.class))) })
    public Page<LoanTransactionData> retrieveTransactionsByExternalLoanId(
            @PathParam("loanExternalId") @Parameter(description = "loanId", required = true) final String loanExternalId,
            @QueryParam("excludedTypes") @Parameter(description = "excludedTypes", in = ParameterIn.QUERY, schema = @Schema(enumAsRef = true)) final List<LoanTransactionApiConstants.TransactionType> excludedTypes,
            @QueryParam("page") @Parameter(description = "page") final Integer page,
            @QueryParam("size") @Parameter(description = "size") final Integer size,
            @QueryParam("sort") @Parameter(description = "sort") final String sort, @Parameter(hidden = true) @Pagination Pageable pageable,
            @Context final UriInfo uriInfo) {
        return retrieveTransactions(null, loanExternalId, excludedTypes, pageable, uriInfo);
    }

    @POST
    @Path("{loanId}/transactions")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Significant Loan Transactions", description = "This API covers the major loan transaction functionality\n\n"
            + "Example Requests:\n\n" + "loans/1/transactions?command=repayment" + " | Make a Repayment | \n"
            + "loans/1/transactions?command=merchantIssuedRefund" + " | Merchant Issued Refund | \n"
            + "loans/1/transactions?command=payoutRefund" + " | Payout Refund | \n" + "loans/1/transactions?command=goodwillCredit"
            + " | Goodwil Credit | \n" + "loans/1/transactions?command=chargeRefund" + " | Charge Refund | \n"
            + "loans/1/transactions?command=waiveinterest" + " | Waive Interest | \n" + "loans/1/transactions?command=writeoff"
            + " | Write-off Loan | \n" + "loans/1/transactions?command=close-rescheduled" + " | Close Rescheduled Loan | \n"
            + "loans/1/transactions?command=close" + " | Close Loan | \n" + "loans/1/transactions?command=undowriteoff"
            + " | Undo Loan Write-off | \n" + "loans/1/transactions?command=recoverypayment" + " | Make Recovery Payment | \n"
            + "loans/1/transactions?command=refundByCash" + " | Make a Refund of an Active Loan by Cash | \n"
            + "loans/1/transactions?command=foreclosure" + " | Foreclosure of an Active Loan | \n"
            + "loans/1/transactions?command=creditBalanceRefund" + " | Credit Balance Refund" + " |  \n"
            + "loans/external-id/7dd80a7c-ycba-a446-t378-91eb6f53e854/transactions?command=charge-off" + " | Charge-off Loan" + " |  \n"
            + "loans/1/transactions?command=downPayment" + " | Down Payment" + " |  \n")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PostLoansLoanIdTransactionsRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PostLoansLoanIdTransactionsResponse.class))) })
    public String executeLoanTransaction(@PathParam("loanId") @Parameter(description = "loanId", required = true) final Long loanId,
            @QueryParam("command") @Parameter(description = "command") final String commandParam,
            @Parameter(hidden = true) final String apiRequestBodyAsJson) {

        return executeTransaction(loanId, null, commandParam, apiRequestBodyAsJson);
    }

    @POST
    @Path("external-id/{loanExternalId}/transactions")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Significant Loan Transactions", description = "This API covers the major loan transaction functionality\n\n"
            + "Example Requests:\n\n" + "loans/external-id/7dd80a7c-ycba-a446-t378-91eb6f53e854/transactions?command=repayment"
            + " | Make a Repayment | \n"
            + "loans/external-id/7dd80a7c-ycba-a446-t378-91eb6f53e854/transactions?command=merchantIssuedRefund"
            + " | Merchant Issued Refund | \n" + "loans/external-id/7dd80a7c-ycba-a446-t378-91eb6f53e854/transactions?command=payoutRefund"
            + " | Payout Refund | \n" + "loans/external-id/7dd80a7c-ycba-a446-t378-91eb6f53e854/transactions?command=goodwillCredit"
            + " | Goodwil Credit | \n" + "loans/external-id/7dd80a7c-ycba-a446-t378-91eb6f53e854/transactions?command=chargeRefund"
            + " | Charge Refund | \n" + "loans/external-id/7dd80a7c-ycba-a446-t378-91eb6f53e854/transactions?command=waiveinterest"
            + " | Waive Interest | \n" + "loans/external-id/7dd80a7c-ycba-a446-t378-91eb6f53e854/transactions?command=writeoff"
            + " | Write-off Loan | \n" + "loans/external-id/7dd80a7c-ycba-a446-t378-91eb6f53e854/transactions?command=close-rescheduled"
            + " | Close Rescheduled Loan | \n" + "loans/external-id/7dd80a7c-ycba-a446-t378-91eb6f53e854/transactions?command=close"
            + " | Close Loan | \n" + "loans/external-id/7dd80a7c-ycba-a446-t378-91eb6f53e854/transactions?command=undowriteoff"
            + " | Undo Loan Write-off | \n" + "loans/external-id/7dd80a7c-ycba-a446-t378-91eb6f53e854/transactions?command=recoverypayment"
            + " | Make Recovery Payment | \n" + "loans/external-id/7dd80a7c-ycba-a446-t378-91eb6f53e854/transactions?command=refundByCash"
            + " | Make a Refund of an Active Loan by Cash | \n"
            + "loans/external-id/7dd80a7c-ycba-a446-t378-91eb6f53e854/transactions?command=foreclosure"
            + " | Foreclosure of an Active Loan | \n"
            + "loans/external-id/7dd80a7c-ycba-a446-t378-91eb6f53e854/transactions?command=creditBalanceRefund" + " | Credit Balance Refund"
            + " |  \n" + "loans/external-id/7dd80a7c-ycba-a446-t378-91eb6f53e854/transactions?command=charge-off" + " | Charge-off Loan"
            + " |  \n" + "loans/external-id/7dd80a7c-ycba-a446-t378-91eb6f53e854/transactions?command=downPayment" + " | Down Payment"
            + " |  \n")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PostLoansLoanIdTransactionsRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PostLoansLoanIdTransactionsResponse.class))) })
    public String executeLoanTransaction(
            @PathParam("loanExternalId") @Parameter(description = "loanExternalId", required = true) final String loanExternalId,
            @QueryParam("command") @Parameter(description = "command") final String commandParam,
            @Parameter(hidden = true) final String apiRequestBodyAsJson) {

        return executeTransaction(null, loanExternalId, commandParam, apiRequestBodyAsJson);
    }

    @POST
    @Path("{loanId}/transactions/{transactionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Adjust a Transaction", description = "Note: there is no need to specify command={transactionType} parameter.\n\n"
            + "Mandatory Fields: transactionDate, transactionAmount")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PostLoansLoanIdTransactionsTransactionIdRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PostLoansLoanIdTransactionsResponse.class))) })
    public String adjustLoanTransaction(@PathParam("loanId") @Parameter(description = "loanId", required = true) final Long loanId,
            @PathParam("transactionId") @Parameter(description = "transactionId", required = true) final Long transactionId,
            @Parameter(hidden = true) final String apiRequestBodyAsJson,
            @QueryParam("command") @Parameter(description = "command") final String commandParam) {

        return adjustTransaction(loanId, null, transactionId, null, apiRequestBodyAsJson, commandParam);
    }

    @POST
    @Path("external-id/{loanExternalId}/transactions/{transactionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Adjust a Transaction", description = "Note: there is no need to specify command={transactionType} parameter.\n\n"
            + "Mandatory Fields: transactionDate, transactionAmount")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PostLoansLoanIdTransactionsTransactionIdRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PostLoansLoanIdTransactionsResponse.class))) })
    public String adjustLoanTransaction(
            @PathParam("loanExternalId") @Parameter(description = "loanExternalId", required = true) final String loanExternalId,
            @PathParam("transactionId") @Parameter(description = "transactionId", required = true) final Long transactionId,
            @Parameter(hidden = true) final String apiRequestBodyAsJson,
            @QueryParam("command") @Parameter(description = "command") final String commandParam) {

        return adjustTransaction(null, loanExternalId, transactionId, null, apiRequestBodyAsJson, commandParam);
    }

    @POST
    @Path("{loanId}/transactions/external-id/{externalTransactionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Adjust a Transaction", description = "Note: there is no need to specify command={transactionType} parameter.\n\n"
            + "Mandatory Fields: transactionDate, transactionAmount")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PostLoansLoanIdTransactionsTransactionIdRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PostLoansLoanIdTransactionsResponse.class))) })
    public String adjustLoanTransaction(@PathParam("loanId") @Parameter(description = "loanId", required = true) final Long loanId,
            @PathParam("externalTransactionId") @Parameter(description = "externalTransactionId", required = true) final String transactionExternalId,
            @Parameter(hidden = true) final String apiRequestBodyAsJson,
            @QueryParam("command") @Parameter(description = "command") final String commandParam) {

        return adjustTransaction(loanId, null, null, transactionExternalId, apiRequestBodyAsJson, commandParam);
    }

    @POST
    @Path("external-id/{loanExternalId}/transactions/external-id/{externalTransactionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Adjust a Transaction", description = "Note: there is no need to specify command={transactionType} parameter.\n\n"
            + "Mandatory Fields: transactionDate, transactionAmount")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PostLoansLoanIdTransactionsTransactionIdRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PostLoansLoanIdTransactionsResponse.class))) })
    public String adjustLoanTransaction(
            @PathParam("loanExternalId") @Parameter(description = "loanExternalId", required = true) final String loanExternalId,
            @PathParam("externalTransactionId") @Parameter(description = "externalTransactionId", required = true) final String transactionExternalId,
            @Parameter(hidden = true) final String apiRequestBodyAsJson,
            @QueryParam("command") @Parameter(description = "command") final String commandParam) {

        return adjustTransaction(null, loanExternalId, null, transactionExternalId, apiRequestBodyAsJson, commandParam);
    }

    @PUT
    @Path("{loanId}/transactions/{transactionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Undo a Waive Charge Transaction", description = "Undo a Waive Charge Transaction")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PutChargeTransactionChangesRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PutChargeTransactionChangesResponse.class))) })
    public String undoWaiveCharge(@PathParam("loanId") @Parameter(description = "loanId", required = true) final Long loanId,
            @PathParam("transactionId") @Parameter(description = "transactionId", required = true) final Long transactionId) {

        return undoWaiveCharge(loanId, null, transactionId, null);
    }

    @PUT
    @Path("external-id/{loanExternalId}/transactions/{transactionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Undo a Waive Charge Transaction", description = "Undo a Waive Charge Transaction")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PutChargeTransactionChangesRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PutChargeTransactionChangesResponse.class))) })
    public String undoWaiveCharge(
            @PathParam("loanExternalId") @Parameter(description = "loanExternalId", required = true) final String loanExternalId,
            @PathParam("transactionId") @Parameter(description = "transactionId", required = true) final Long transactionId) {

        return undoWaiveCharge(null, loanExternalId, transactionId, null);
    }

    @PUT
    @Path("{loanId}/transactions/external-id/{transactionExternalId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Undo a Waive Charge Transaction", description = "Undo a Waive Charge Transaction")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PutChargeTransactionChangesRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PutChargeTransactionChangesResponse.class))) })
    public String undoWaiveCharge(@PathParam("loanId") @Parameter(description = "loanId", required = true) final Long loanId,
            @PathParam("transactionExternalId") @Parameter(description = "transactionExternalId", required = true) final String transactionExternalId) {

        return undoWaiveCharge(loanId, null, null, transactionExternalId);
    }

    @PUT
    @Path("external-id/{loanExternalId}/transactions/external-id/{transactionExternalId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Undo a Waive Charge Transaction", description = "Undo a Waive Charge Transaction")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PutChargeTransactionChangesRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoanTransactionsApiResourceSwagger.PutChargeTransactionChangesResponse.class))) })
    public String undoWaiveCharge(
            @PathParam("loanExternalId") @Parameter(description = "loanExternalId", required = true) final String loanExternalId,
            @PathParam("transactionExternalId") @Parameter(description = "transactionExternalId", required = true) final String transactionExternalId) {

        return undoWaiveCharge(null, loanExternalId, null, transactionExternalId);
    }

    private String retrieveTransaction(final Long loanId, final String loanExternalIdStr, final Long transactionId,
            final String transactionExternalIdStr, final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);

        ExternalId loanExternalId = ExternalIdFactory.produce(loanExternalIdStr);
        ExternalId transactionExternalId = ExternalIdFactory.produce(transactionExternalIdStr);

        Long resolvedLoanId = loanId == null ? loanReadPlatformService.getResolvedLoanId(loanExternalId) : loanId;
        Long resolvedLoanTransactionId = getResolvedLoanTransactionId(transactionId, transactionExternalId);

        LoanTransactionData transactionData = this.loanReadPlatformService.retrieveLoanTransaction(resolvedLoanId,
                resolvedLoanTransactionId);
        transactionData
                .setLoanChargePaidByList(loanChargePaidByReadService.fetchLoanChargesPaidByDataTransactionId(transactionData.getId()));
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        if (settings.isTemplate()) {
            final Collection<PaymentTypeData> paymentTypeOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
            transactionData = LoanTransactionData.templateOnTop(transactionData, paymentTypeOptions);
        }

        return this.toApiJsonSerializer.serialize(settings, transactionData, this.responseDataParameters);
    }

    private Page<LoanTransactionData> retrieveTransactions(final Long loanId, final String loanExternalIdStr,
            final List<LoanTransactionApiConstants.TransactionType> excludedTypes, Pageable pageable, final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);

        ExternalId loanExternalId = ExternalIdFactory.produce(loanExternalIdStr);
        Long resolvedLoanId = getResolvedLoanIdWithExistsCheck(loanId, loanExternalId);

        Set<LoanTransactionType> excludedTransactionTypes = new HashSet<>();
        if (CollectionUtils.isNotEmpty(excludedTypes)) {
            excludedTypes.forEach(x -> excludedTransactionTypes.add(transactionTypeFromParam(x)));
        }

        return loanReadPlatformService.retrieveLoanTransactions(resolvedLoanId, excludedTransactionTypes, pageable);
    }

    private LoanTransactionType transactionTypeFromParam(LoanTransactionApiConstants.TransactionType transactionTypeParam) {
        return switch (transactionTypeParam) {
            case disbursement -> LoanTransactionType.DISBURSEMENT;
            case repayment -> LoanTransactionType.REPAYMENT;
            case waiver -> LoanTransactionType.WAIVE_INTEREST;
            case repaymentAtDisbursement -> LoanTransactionType.REPAYMENT_AT_DISBURSEMENT;
            case writeOff -> LoanTransactionType.WRITEOFF;
            case markedForRescheduling -> LoanTransactionType.MARKED_FOR_RESCHEDULING;
            case recoveryRepayment -> LoanTransactionType.RECOVERY_REPAYMENT;
            case waiveCharges -> LoanTransactionType.WAIVE_CHARGES;
            case accrual -> LoanTransactionType.ACCRUAL;
            case initiateTransfer -> LoanTransactionType.INITIATE_TRANSFER;
            case approveTransfer -> LoanTransactionType.APPROVE_TRANSFER;
            case withdrawTransfer -> LoanTransactionType.WITHDRAW_TRANSFER;
            case rejectTransfer -> LoanTransactionType.REJECT_TRANSFER;
            case refund -> LoanTransactionType.REFUND;
            case chargePayment -> LoanTransactionType.CHARGE_PAYMENT;
            case incomePosting -> LoanTransactionType.INCOME_POSTING;
            case creditBalanceRefund -> LoanTransactionType.CREDIT_BALANCE_REFUND;
            case merchantIssuedRefund -> LoanTransactionType.MERCHANT_ISSUED_REFUND;
            case payoutRefund -> LoanTransactionType.PAYOUT_REFUND;
            case goodwillCredit -> LoanTransactionType.GOODWILL_CREDIT;
            case chargeRefund -> LoanTransactionType.CHARGE_REFUND;
            case chargeback -> LoanTransactionType.CHARGEBACK;
            case chargeAdjustment -> LoanTransactionType.CHARGE_ADJUSTMENT;
            case chargeOff -> LoanTransactionType.CHARGE_OFF;
            case reAge -> LoanTransactionType.REAGE;
            case reAmortize -> LoanTransactionType.REAMORTIZE;
            case interestPaymentWaiver -> LoanTransactionType.INTEREST_PAYMENT_WAIVER;
            case accrualActivity -> LoanTransactionType.ACCRUAL_ACTIVITY;
            case interestRefund -> LoanTransactionType.INTEREST_REFUND;
            case accrualAdjustment -> LoanTransactionType.ACCRUAL_ADJUSTMENT;
            case capitalizedIncome -> LoanTransactionType.CAPITALIZED_INCOME;
            case capitalizedIncomeAmortization -> LoanTransactionType.CAPITALIZED_INCOME_AMORTIZATION;
            case capitalizedIncomeAdjustment -> LoanTransactionType.CAPITALIZED_INCOME_ADJUSTMENT;
            case contractTermination -> LoanTransactionType.CONTRACT_TERMINATION;
            case capitalizedIncomeAmortizationAdjustment -> LoanTransactionType.CAPITALIZED_INCOME_AMORTIZATION_ADJUSTMENT;
            case buyDownFeeAmortization -> LoanTransactionType.BUY_DOWN_FEE_AMORTIZATION;
            case buyDownFeeAmortizationAdjustment -> LoanTransactionType.BUY_DOWN_FEE_AMORTIZATION_ADJUSTMENT;
            default ->
                throw new InvalidLoanTransactionTypeException("transaction", transactionTypeParam.name(), "Unknown transaction type");
        };
    }

    private String executeTransaction(final Long loanId, final String loanExternalIdStr, final String commandParam,
            final String apiRequestBodyAsJson) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

        ExternalId loanExternalId = ExternalIdFactory.produce(loanExternalIdStr);
        Long resolvedLoanId = loanId == null ? loanReadPlatformService.getResolvedLoanId(loanExternalId) : loanId;

        CommandWrapper commandRequest = null;
        if (CommandParameterUtil.is(commandParam, "repayment")) {
            commandRequest = builder.loanRepaymentTransaction(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, "merchantIssuedRefund")) {
            commandRequest = builder.loanMerchantIssuedRefundTransaction(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, "payoutRefund")) {
            commandRequest = builder.loanPayoutRefundTransaction(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, "goodwillCredit")) {
            commandRequest = builder.loanGoodwillCreditTransaction(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, "interestPaymentWaiver")) {
            commandRequest = builder.loanInterestPaymentWaiverTransaction(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, "chargeRefund")) {
            commandRequest = builder.refundLoanCharge(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, "waiveinterest")) {
            commandRequest = builder.waiveInterestPortionTransaction(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, "writeoff")) {
            commandRequest = builder.writeOffLoanTransaction(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, "close-rescheduled")) {
            commandRequest = builder.closeLoanAsRescheduledTransaction(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, "close")) {
            commandRequest = builder.closeLoanTransaction(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, "undowriteoff")) {
            commandRequest = builder.undoWriteOffLoanTransaction(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, "recoverypayment")) {
            commandRequest = builder.loanRecoveryPaymentTransaction(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, "refundByCash")) {
            commandRequest = builder.refundLoanTransactionByCash(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, "foreclosure")) {
            commandRequest = builder.loanForeclosure(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, "creditBalanceRefund")) {
            commandRequest = builder.creditBalanceRefund(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, CHARGE_OFF_COMMAND_VALUE)) {
            commandRequest = builder.chargeOff(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, UNDO_CHARGE_OFF_COMMAND_VALUE)) {
            commandRequest = builder.undoChargeOff(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, DOWN_PAYMENT)) {
            commandRequest = builder.downPayment(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, REAGE)) {
            commandRequest = builder.reAge(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, UNDO_REAGE)) {
            commandRequest = builder.undoReAge(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, REAMORTIZE)) {
            commandRequest = builder.reAmortize(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, UNDO_REAMORTIZE)) {
            commandRequest = builder.undoReAmortize(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, CAPITALIZED_INCOME)) {
            commandRequest = builder.addCapitalizedIncome(resolvedLoanId).build();
        } else if (CommandParameterUtil.is(commandParam, LoanApiConstants.BUY_DOWN_FEE_COMMAND)) {
            commandRequest = builder.makeLoanBuyDownFee(resolvedLoanId).build();
        }

        if (commandRequest == null) {
            throw new UnrecognizedQueryParamException("command", commandParam);
        }
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    private String retrieveTransactionTemplate(Long loanId, String loanExternalIdStr, String commandParam, UriInfo uriInfo,
            DateFormat dateFormat, DateParam transactionDateParam, String locale, Long transactionId) {
        this.context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);

        ExternalId loanExternalId = ExternalIdFactory.produce(loanExternalIdStr);

        Long resolvedLoanId = loanId == null ? loanReadPlatformService.getResolvedLoanId(loanExternalId) : loanId;
        LoanTransactionData transactionData;

        if (CommandParameterUtil.is(commandParam, "repayment")) {
            transactionData = this.loanReadPlatformService.retrieveLoanTransactionTemplate(resolvedLoanId);
        } else if (CommandParameterUtil.is(commandParam, "merchantIssuedRefund")) {
            LocalDate transactionDate = DateUtils.getBusinessLocalDate();
            transactionData = this.loanReadPlatformService.retrieveLoanPrePaymentTemplate(LoanTransactionType.MERCHANT_ISSUED_REFUND,
                    resolvedLoanId, transactionDate);
        } else if (CommandParameterUtil.is(commandParam, "payoutRefund")) {
            LocalDate transactionDate = DateUtils.getBusinessLocalDate();
            transactionData = this.loanReadPlatformService.retrieveLoanPrePaymentTemplate(LoanTransactionType.PAYOUT_REFUND, resolvedLoanId,
                    transactionDate);
        } else if (CommandParameterUtil.is(commandParam, "goodwillCredit")) {
            LocalDate transactionDate = DateUtils.getBusinessLocalDate();
            transactionData = this.loanReadPlatformService.retrieveLoanPrePaymentTemplate(LoanTransactionType.GOODWILL_CREDIT,
                    resolvedLoanId, transactionDate);
        } else if (CommandParameterUtil.is(commandParam, "interestPaymentWaiver")) {
            LocalDate transactionDate = DateUtils.getBusinessLocalDate();
            transactionData = this.loanReadPlatformService.retrieveLoanPrePaymentTemplate(LoanTransactionType.INTEREST_PAYMENT_WAIVER,
                    resolvedLoanId, transactionDate);
        } else if (CommandParameterUtil.is(commandParam, "waiveinterest")) {
            transactionData = this.loanReadPlatformService.retrieveWaiveInterestDetails(resolvedLoanId);
        } else if (CommandParameterUtil.is(commandParam, "writeoff")) {
            transactionData = this.loanReadPlatformService.retrieveLoanWriteoffTemplate(resolvedLoanId);
        } else if (CommandParameterUtil.is(commandParam, "close-rescheduled")) {
            transactionData = this.loanReadPlatformService.retrieveNewClosureDetails();
        } else if (CommandParameterUtil.is(commandParam, "close")) {
            transactionData = this.loanReadPlatformService.retrieveNewClosureDetails();
        } else if (CommandParameterUtil.is(commandParam, "disburse")) {
            transactionData = this.loanReadPlatformService.retrieveDisbursalTemplate(resolvedLoanId, true);
            transactionData.setNumberOfRepayments(this.loanReadPlatformService.retrieveNumberOfRepayments(resolvedLoanId));
            final List<LoanRepaymentScheduleInstallmentData> loanRepaymentScheduleInstallmentData = this.loanReadPlatformService
                    .getRepaymentDataResponse(resolvedLoanId);
            transactionData.setLoanRepaymentScheduleInstallments(loanRepaymentScheduleInstallmentData);
        } else if (CommandParameterUtil.is(commandParam, "disburseToSavings")) {
            transactionData = this.loanReadPlatformService.retrieveDisbursalTemplate(resolvedLoanId, false);
        } else if (CommandParameterUtil.is(commandParam, "recoverypayment")) {
            transactionData = this.loanReadPlatformService.retrieveRecoveryPaymentTemplate(resolvedLoanId);
        } else if (CommandParameterUtil.is(commandParam, "prepayLoan")
                || CommandParameterUtil.is(commandParam, LoanApiConstants.CONTRACT_TERMINATION_COMMAND)) {
            LocalDate transactionDate;
            if (transactionDateParam == null) {
                transactionDate = DateUtils.getBusinessLocalDate();
            } else {
                transactionDate = transactionDateParam.getDate("transactionDate", dateFormat, locale);
            }
            transactionData = this.loanReadPlatformService.retrieveLoanPrePaymentTemplate(LoanTransactionType.REPAYMENT, resolvedLoanId,
                    transactionDate);
        } else if (CommandParameterUtil.is(commandParam, "refundbycash")) {
            transactionData = this.loanReadPlatformService.retrieveRefundByCashTemplate(resolvedLoanId);
        } else if (CommandParameterUtil.is(commandParam, "refundbytransfer")) {
            transactionData = this.loanReadPlatformService.retrieveDisbursalTemplate(resolvedLoanId, true);
        } else if (CommandParameterUtil.is(commandParam, "foreclosure")) {
            LocalDate transactionDate;
            if (transactionDateParam == null) {
                transactionDate = DateUtils.getBusinessLocalDate();
            } else {
                transactionDate = transactionDateParam.getDate("transactionDate", dateFormat, locale);
            }
            transactionData = this.loanReadPlatformService.retrieveLoanForeclosureTemplate(resolvedLoanId, transactionDate);
        } else if (CommandParameterUtil.is(commandParam, "creditBalanceRefund")) {
            transactionData = this.loanReadPlatformService.retrieveCreditBalanceRefundTemplate(resolvedLoanId);
        } else if (CommandParameterUtil.is(commandParam, CHARGE_OFF_COMMAND_VALUE)) {
            transactionData = this.loanReadPlatformService.retrieveLoanChargeOffTemplate(resolvedLoanId);
        } else if (CommandParameterUtil.is(commandParam, DOWN_PAYMENT)) {
            transactionData = this.loanReadPlatformService.retrieveLoanTransactionTemplate(resolvedLoanId);
        } else if (CommandParameterUtil.is(commandParam, LoanApiConstants.CAPITALIZED_INCOME_TRANSACTION_COMMAND)) {
            transactionData = this.loanReadPlatformService.retrieveLoanTransactionTemplate(resolvedLoanId,
                    LoanTransactionType.CAPITALIZED_INCOME, transactionId);
        } else if (CommandParameterUtil.is(commandParam, LoanApiConstants.CAPITALIZED_INCOME_ADJUSTMENT_TRANSACTION_COMMAND)) {
            transactionData = this.loanReadPlatformService.retrieveLoanTransactionTemplate(resolvedLoanId,
                    LoanTransactionType.CAPITALIZED_INCOME_ADJUSTMENT, transactionId);
        } else if (CommandParameterUtil.is(commandParam, LoanApiConstants.BUY_DOWN_FEE_COMMAND)) {
            transactionData = this.loanReadPlatformService.retrieveLoanTransactionTemplate(resolvedLoanId, LoanTransactionType.BUY_DOWN_FEE,
                    transactionId);
        } else if (CommandParameterUtil.is(commandParam, LoanApiConstants.BUY_DOWN_FEE_ADJUSTMENT_COMMAND)) {
            transactionData = this.loanReadPlatformService.retrieveLoanTransactionTemplate(resolvedLoanId,
                    LoanTransactionType.BUY_DOWN_FEE_ADJUSTMENT, transactionId);
        } else if (CommandParameterUtil.is(commandParam, INTEREST_REFUND_COMMAND_VALUE)) {
            transactionData = this.loanReadPlatformService.retrieveManualInterestRefundTemplate(resolvedLoanId, transactionId);
        } else {
            throw new UnrecognizedQueryParamException("command", commandParam);
        }

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, transactionData, this.responseDataParameters);
    }

    private String adjustTransaction(final Long loanId, final String loanExternalIdStr, final Long transactionId,
            final String transactionExternalIdStr, final String apiRequestBodyAsJson, final String commandParam) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

        ExternalId loanExternalId = ExternalIdFactory.produce(loanExternalIdStr);
        ExternalId transactionExternalId = ExternalIdFactory.produce(transactionExternalIdStr);

        Long resolvedLoanId = loanId == null ? loanReadPlatformService.getResolvedLoanId(loanExternalId) : loanId;
        Long resolvedTransactionId = getResolvedLoanTransactionId(transactionId, transactionExternalId);
        CommandWrapper commandRequest;
        if (CommandParameterUtil.is(commandParam, LoanApiConstants.CHARGEBACK_TRANSACTION_COMMAND)) {
            commandRequest = builder.chargebackTransaction(resolvedLoanId, resolvedTransactionId).build();
        } else if (CommandParameterUtil.is(commandParam, LoanApiConstants.CAPITALIZED_INCOME_ADJUSTMENT_TRANSACTION_COMMAND)) {
            commandRequest = builder.capitalizedIncomeAdjustment(resolvedLoanId, resolvedTransactionId).build();
        } else if (CommandParameterUtil.is(commandParam, LoanApiConstants.BUY_DOWN_FEE_ADJUSTMENT_COMMAND)) {
            commandRequest = builder.buyDownFeeAdjustment(resolvedLoanId, resolvedTransactionId).build();
        } else if (CommandParameterUtil.is(commandParam, INTEREST_REFUND_COMMAND_VALUE)) {
            commandRequest = builder.manualInterestRefund(resolvedLoanId, resolvedTransactionId).build();
        } else { // Default to adjust the Loan Transaction
            commandRequest = builder.adjustTransaction(resolvedLoanId, resolvedTransactionId).build();
        }

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    private String undoWaiveCharge(final Long loanId, final String loanExternalIdStr, final Long transactionId,
            final String transactionExternalIdStr) {
        ExternalId loanExternalId = ExternalIdFactory.produce(loanExternalIdStr);
        ExternalId transactionExternalId = ExternalIdFactory.produce(transactionExternalIdStr);

        Long resolvedLoanId = loanId == null ? loanReadPlatformService.getResolvedLoanId(loanExternalId) : loanId;
        Long resolvedTransactionId = getResolvedLoanTransactionId(transactionId, transactionExternalId);
        final CommandWrapper commandRequest = new CommandWrapperBuilder().undoWaiveChargeTransaction(resolvedLoanId, resolvedTransactionId)
                .build();
        CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    private Long getResolvedLoanTransactionId(final Long transactionId, final ExternalId externalTransactionId) {
        Long resolvedLoanTransactionId = transactionId;
        if (resolvedLoanTransactionId == null) {
            externalTransactionId.throwExceptionIfEmpty();
            resolvedLoanTransactionId = this.loanReadPlatformService.retrieveLoanTransactionIdByExternalId(externalTransactionId);
            if (resolvedLoanTransactionId == null) {
                throw new LoanTransactionNotFoundException(externalTransactionId);
            }
        }
        return resolvedLoanTransactionId;
    }

    private Long getResolvedLoanIdWithExistsCheck(final Long loanId, final ExternalId loanExternalId) {
        if (loanId != null) {
            if (!loanReadPlatformService.existsByLoanId(loanId)) {
                throw new LoanNotFoundException(loanId);
            }
            return loanId;
        } else if (loanExternalId != null) {
            final Long resolvedLoanId = loanReadPlatformService.retrieveLoanIdByExternalId(loanExternalId);
            if (resolvedLoanId == null) {
                throw new LoanNotFoundException(loanExternalId);
            }
            return resolvedLoanId;
        } else {
            throw new IllegalArgumentException("loanId and loanExternalId cannot be both null");
        }
    }
}
