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
package org.apache.fineract.integrationtests.common.loans;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.client.models.AdvancedPaymentData;
import org.apache.fineract.client.models.BuyDownFeeAmortizationDetails;
import org.apache.fineract.client.models.CapitalizedIncomeDetails;
import org.apache.fineract.client.models.CommandProcessingResult;
import org.apache.fineract.client.models.DeleteLoansLoanIdChargesChargeIdResponse;
import org.apache.fineract.client.models.DeleteLoansLoanIdResponse;
import org.apache.fineract.client.models.DisbursementDetail;
import org.apache.fineract.client.models.GetDelinquencyActionsResponse;
import org.apache.fineract.client.models.GetDelinquencyTagHistoryResponse;
import org.apache.fineract.client.models.GetLoanProductsProductIdResponse;
import org.apache.fineract.client.models.GetLoanProductsResponse;
import org.apache.fineract.client.models.GetLoansApprovalTemplateResponse;
import org.apache.fineract.client.models.GetLoansLoanIdChargesChargeIdResponse;
import org.apache.fineract.client.models.GetLoansLoanIdChargesTemplateResponse;
import org.apache.fineract.client.models.GetLoansLoanIdDelinquencySummary;
import org.apache.fineract.client.models.GetLoansLoanIdDisbursementDetails;
import org.apache.fineract.client.models.GetLoansLoanIdRepaymentPeriod;
import org.apache.fineract.client.models.GetLoansLoanIdRepaymentSchedule;
import org.apache.fineract.client.models.GetLoansLoanIdResponse;
import org.apache.fineract.client.models.GetLoansLoanIdSummary;
import org.apache.fineract.client.models.GetLoansLoanIdTransactions;
import org.apache.fineract.client.models.GetLoansLoanIdTransactionsResponse;
import org.apache.fineract.client.models.GetLoansLoanIdTransactionsTemplateResponse;
import org.apache.fineract.client.models.GetLoansLoanIdTransactionsTransactionIdResponse;
import org.apache.fineract.client.models.GetLoansResponse;
import org.apache.fineract.client.models.InterestPauseRequestDto;
import org.apache.fineract.client.models.LoanCapitalizedIncomeData;
import org.apache.fineract.client.models.PaymentTypeData;
import org.apache.fineract.client.models.PostAddAndDeleteDisbursementDetailRequest;
import org.apache.fineract.client.models.PostLoanProductsRequest;
import org.apache.fineract.client.models.PostLoanProductsResponse;
import org.apache.fineract.client.models.PostLoansDelinquencyActionRequest;
import org.apache.fineract.client.models.PostLoansDelinquencyActionResponse;
import org.apache.fineract.client.models.PostLoansLoanIdChargesChargeIdRequest;
import org.apache.fineract.client.models.PostLoansLoanIdChargesChargeIdResponse;
import org.apache.fineract.client.models.PostLoansLoanIdChargesRequest;
import org.apache.fineract.client.models.PostLoansLoanIdChargesResponse;
import org.apache.fineract.client.models.PostLoansLoanIdRequest;
import org.apache.fineract.client.models.PostLoansLoanIdResponse;
import org.apache.fineract.client.models.PostLoansLoanIdTransactionsRequest;
import org.apache.fineract.client.models.PostLoansLoanIdTransactionsResponse;
import org.apache.fineract.client.models.PostLoansLoanIdTransactionsTransactionIdRequest;
import org.apache.fineract.client.models.PostLoansRequest;
import org.apache.fineract.client.models.PostLoansResponse;
import org.apache.fineract.client.models.PutChargeTransactionChangesRequest;
import org.apache.fineract.client.models.PutChargeTransactionChangesResponse;
import org.apache.fineract.client.models.PutLoanProductsProductIdRequest;
import org.apache.fineract.client.models.PutLoanProductsProductIdResponse;
import org.apache.fineract.client.models.PutLoansLoanIdChargesChargeIdRequest;
import org.apache.fineract.client.models.PutLoansLoanIdChargesChargeIdResponse;
import org.apache.fineract.client.models.PutLoansLoanIdRequest;
import org.apache.fineract.client.models.PutLoansLoanIdResponse;
import org.apache.fineract.client.models.TransactionType;
import org.apache.fineract.client.util.CallFailedRuntimeException;
import org.apache.fineract.client.util.Calls;
import org.apache.fineract.client.util.JSON;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.FineractClientHelper;
import org.apache.fineract.integrationtests.common.PaymentTypeHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.accounting.Account;
import org.apache.fineract.portfolio.delinquency.domain.DelinquencyAction;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import retrofit2.Response;

@Slf4j
@SuppressWarnings({ "rawtypes", "unchecked" })
public class LoanTransactionHelper {

    public static final String DATE_FORMAT = "d MMMM yyyy";
    public static final String DATE_TIME_FORMAT = "dd MMMM yyyy HH:mm";
    private static final String LOAN_PRODUCTS_URL = "/fineract-provider/api/v1/loanproducts";
    private static final String CREATE_LOAN_PRODUCT_URL = "/fineract-provider/api/v1/loanproducts?" + Utils.TENANT_IDENTIFIER;
    private static final String APPLY_LOAN_URL = "/fineract-provider/api/v1/loans?" + Utils.TENANT_IDENTIFIER;
    private static final String LOAN_ACCOUNT_URL = "/fineract-provider/api/v1/loans";
    private static final String APPROVE_LOAN_COMMAND = "approve";
    private static final String UNDO_APPROVAL_LOAN_COMMAND = "undoApproval";
    private static final String DISBURSE_LOAN_COMMAND = "disburse";
    private static final String DISBURSE_LOAN_TO_SAVINGS_COMMAND = "disburseToSavings";
    private static final String DISBURSE_LOAN_WITHOUT_AUTO_PAYMENT_COMMAND = "disburseWithoutAutoDownPayment";
    private static final String UNDO_DISBURSE_LOAN_COMMAND = "undoDisbursal";
    private static final String REJECT_LOAN_COMMAND = "reject";
    private static final String UNDO_LAST_DISBURSE_LOAN_COMMAND = "undolastdisbursal";
    private static final String WRITE_OFF_LOAN_COMMAND = "writeoff";
    private static final String WAIVE_INTEREST_COMMAND = "waiveinterest";
    private static final String MAKE_REPAYMENT_COMMAND = "repayment";
    private static final String INTEREST_PAUSE_COMMAND = "interestpause";
    private static final String UNDO = "undo";
    private static final String LOANCHARGE_REFUND_REPAYMENT_COMMAND = "chargeRefund";
    private static final String CREDIT_BALANCE_REFUND_COMMAND = "creditBalanceRefund";
    private static final String WITHDRAW_LOAN_APPLICATION_COMMAND = "withdrawnByApplicant";
    private static final String RECOVER_FROM_GUARANTORS_COMMAND = "recoverGuarantees";
    private static final String MAKE_REFUND_BY_CASH_COMMAND = "refundByCash";
    private static final String FORECLOSURE_COMMAND = "foreclosure";
    private static final Gson GSON = new JSON().getGson();
    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;
    private PaymentTypeHelper paymentTypeHelper;

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public LoanTransactionHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
        this.paymentTypeHelper = new PaymentTypeHelper();
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public GetLoanProductsProductIdResponse getLoanProduct(final Integer loanProductId) {
        final String GET_LOANPRODUCT_URL = "/fineract-provider/api/v1/loanproducts/" + loanProductId + "?" + Utils.TENANT_IDENTIFIER;
        final String response = Utils.performServerGet(this.requestSpec, this.responseSpec, GET_LOANPRODUCT_URL);
        return GSON.fromJson(response, GetLoanProductsProductIdResponse.class);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public GetLoanProductsResponse[] listAllLoanProducts() {
        final String GET_LOANPRODUCT_URL = "/fineract-provider/api/v1/loanproducts?" + Utils.TENANT_IDENTIFIER;
        final String response = Utils.performServerGet(this.requestSpec, this.responseSpec, GET_LOANPRODUCT_URL);
        return GSON.fromJson(response, GetLoanProductsResponse[].class);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Integer getLoanProductId(final String loanProductJSON) {
        return Utils.performServerPost(this.requestSpec, this.responseSpec, CREATE_LOAN_PRODUCT_URL, loanProductJSON, "resourceId");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public <T> T getLoanProductError(final String loanProductJSON, final String jsonAttributeToGetBack) {
        return Utils.performServerPost(this.requestSpec, this.responseSpec, CREATE_LOAN_PRODUCT_URL, loanProductJSON,
                jsonAttributeToGetBack);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Integer getLoanId(final String loanApplicationJSON) {
        return this.getLoanId(loanApplicationJSON, this.requestSpec, this.responseSpec);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap getLoanId(final String loanApplicationJSON, final String responseAttribute) {
        return (HashMap) this.getLoanId(loanApplicationJSON, responseAttribute, this.requestSpec, this.responseSpec);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object getLoanId(final String loanApplicationJSON, final String responseAttribute, RequestSpecification requestSpec,
            ResponseSpecification responseSpec) {
        return Utils.performServerPost(requestSpec, responseSpec, APPLY_LOAN_URL, loanApplicationJSON, responseAttribute);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Integer getLoanId(final String loanApplicationJSON, RequestSpecification requestSpec, ResponseSpecification responseSpec) {
        return (Integer) getLoanId(loanApplicationJSON, "loanId", requestSpec, responseSpec);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap<String, Integer> getGlimId(final String loanApplicationJSON) {
        return Utils.performServerPost(this.requestSpec, this.responseSpec, APPLY_LOAN_URL, loanApplicationJSON, "");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object getGlimLoanId(final String glimId) {
        final String GET_LOAN_URL = "/fineract-provider/api/v1/loans/glimAccount/" + glimId + "?" + Utils.TENANT_IDENTIFIER;
        return Utils.performServerGet(this.requestSpec, this.responseSpec, GET_LOAN_URL, "childLoanId");

    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object getLoanError(final String loanApplicationJSON, final String responseAttribute) {
        return Utils.performServerPost(this.requestSpec, this.responseSpec, APPLY_LOAN_URL, loanApplicationJSON, responseAttribute);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Integer getLoanOfficerId(final String loanId) {
        final String GET_LOAN_URL = "/fineract-provider/api/v1/loans/" + loanId + "?" + Utils.TENANT_IDENTIFIER;
        return Utils.performServerGet(this.requestSpec, this.responseSpec, GET_LOAN_URL, "loanOfficerId");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object createLoanAccount(final String loanApplicationJSON, final String responseAttribute) {
        return Utils.performServerPost(this.requestSpec, this.responseSpec, APPLY_LOAN_URL, loanApplicationJSON, responseAttribute);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Integer updateLoan(final Integer id, final String loanApplicationJSON) {
        return Utils.performServerPut(this.requestSpec, this.responseSpec,
                "/fineract-provider/api/v1/loans/" + id + "?" + Utils.TENANT_IDENTIFIER, loanApplicationJSON, "loanId");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public PutLoansLoanIdResponse modifyLoanApplication(final Integer id, final String loanApplicationJSON) {
        final String response = Utils.performServerPut(this.requestSpec, this.responseSpec,
                "/fineract-provider/api/v1/loans/" + id + "?" + Utils.TENANT_IDENTIFIER, loanApplicationJSON, null);
        return GSON.fromJson(response, PutLoansLoanIdResponse.class);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public PutLoansLoanIdResponse modifyLoanCommand(final Integer loanId, final String command, final String payload,
            ResponseSpecification responseSpec) {
        final String url = "/fineract-provider/api/v1/loans/" + loanId + "?" + Utils.TENANT_IDENTIFIER + "&command=" + command;
        final String response = Utils.performServerPut(this.requestSpec, responseSpec, url, payload, null);
        return GSON.fromJson(response, PutLoansLoanIdResponse.class);
    }

    public PutLoansLoanIdResponse modifyLoanApplication(final String loanExternalId, final String command,
            final PutLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.modifyLoanApplication1(loanExternalId, request, command));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public ArrayList getLoanRepaymentSchedule(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer loanID) {
        final String URL = "/fineract-provider/api/v1/loans/" + loanID + "?associations=repaymentSchedule&" + Utils.TENANT_IDENTIFIER;
        final HashMap response = Utils.performServerGet(requestSpec, responseSpec, URL, "repaymentSchedule");
        return (ArrayList) response.get("periods");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public ArrayList getLoanCharges(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer loanID) {
        final String URL = "/fineract-provider/api/v1/loans/" + loanID + "?associations=charges&" + Utils.TENANT_IDENTIFIER;
        return (ArrayList) Utils.performServerGet(requestSpec, responseSpec, URL, "charges");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public ArrayList getLoanTransactions(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer loanID) {
        final String URL = "/fineract-provider/api/v1/loans/" + loanID + "?associations=transactions&" + Utils.TENANT_IDENTIFIER;
        return (ArrayList) Utils.performServerGet(requestSpec, responseSpec, URL, "transactions");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public ArrayList getLoanFutureRepaymentSchedule(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer loanID) {
        final String URL = "/fineract-provider/api/v1/loans/" + loanID + "?associations=repaymentSchedule,futureSchedule&"
                + Utils.TENANT_IDENTIFIER;
        final HashMap response = Utils.performServerGet(requestSpec, responseSpec, URL, "repaymentSchedule");
        return (ArrayList) response.get("futurePeriods");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap getLoanSummary(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final Integer loanID) {
        final String URL = "/fineract-provider/api/v1/loans/" + loanID + "?" + Utils.TENANT_IDENTIFIER;
        final HashMap response = Utils.performServerGet(requestSpec, responseSpec, URL, "summary");
        return response;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public <T> T getLoanDetail(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final Integer loanID,
            final String param) {
        final String URL = "/fineract-provider/api/v1/loans/" + loanID + "?associations=all&" + Utils.TENANT_IDENTIFIER;
        return Utils.performServerGet(requestSpec, responseSpec, URL, param);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public GetLoansLoanIdResponse getLoan(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer loanId) {
        final String URL = "/fineract-provider/api/v1/loans/" + loanId + "?associations=all&" + Utils.TENANT_IDENTIFIER;
        final String response = Utils.performServerGet(requestSpec, responseSpec, URL);
        return GSON.fromJson(response, GetLoansLoanIdResponse.class);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object getLoanDetailExcludeFutureSchedule(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer loanID, final String param) {
        final String URL = "/fineract-provider/api/v1/loans/" + loanID + "?associations=all&exclude=guarantors,futureSchedule&"
                + Utils.TENANT_IDENTIFIER;
        return Utils.performServerGet(requestSpec, responseSpec, URL, param);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public String getLoanDetails(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final Integer loanID) {
        final String URL = "/fineract-provider/api/v1/loans/" + loanID + "?associations=all&" + Utils.TENANT_IDENTIFIER;
        return Utils.performServerGet(requestSpec, responseSpec, URL, null);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public ArrayList<GetDelinquencyTagHistoryResponse> getLoanDelinquencyTags(final RequestSpecification requestSpec,
            final ResponseSpecification responseSpec, final Integer loanID) {
        final String URL = "/fineract-provider/api/v1/loans/" + loanID + "/delinquencytags?" + Utils.TENANT_IDENTIFIER;
        final String response = Utils.performServerGet(requestSpec, responseSpec, URL);
        Type delinquencyTagsListType = new TypeToken<ArrayList<GetDelinquencyTagHistoryResponse>>() {

        }.getType();
        return GSON.fromJson(response, delinquencyTagsListType);
    }

    public List<GetDelinquencyActionsResponse> getLoanDelinquencyActions(final Long loanID) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.getLoanDelinquencyActions(loanID));
    }

    public List<GetDelinquencyActionsResponse> getLoanDelinquencyActions(String externalId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.getLoanDelinquencyActions1(externalId));
    }

    public PostLoansDelinquencyActionResponse createLoanDelinquencyAction(final Long loanid, DelinquencyAction action, String startDate,
            String endDate) {
        PostLoansDelinquencyActionRequest postLoansDelinquencyAction = new PostLoansDelinquencyActionRequest().action(action.name())
                .startDate(startDate).endDate(endDate).locale("en").dateFormat("dd MMMM yyyy");
        return Calls.ok(FineractClientHelper.getFineractClient().loans.createLoanDelinquencyAction(loanid, postLoansDelinquencyAction));
    }

    public PostLoansDelinquencyActionResponse createLoanDelinquencyAction(String externalId, DelinquencyAction action, String startDate,
            String endDate) {
        PostLoansDelinquencyActionRequest postLoansDelinquencyAction = new PostLoansDelinquencyActionRequest().action(action.name())
                .startDate(startDate).endDate(endDate).locale("en").dateFormat("dd MMMM yyyy");
        return Calls
                .ok(FineractClientHelper.getFineractClient().loans.createLoanDelinquencyAction1(externalId, postLoansDelinquencyAction));
    }

    public PostLoansDelinquencyActionResponse createLoanDelinquencyAction(final Long loanid, DelinquencyAction action, String startDate) {
        return createLoanDelinquencyAction(loanid, action, startDate, null);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object getLoanProductDetail(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer loanProductId, final String jsonAttributeToGetBack) {
        final String URL = "/fineract-provider/api/v1/loanproducts/" + loanProductId + "?associations=all&" + Utils.TENANT_IDENTIFIER;
        return Utils.performServerGet(requestSpec, responseSpec, URL, jsonAttributeToGetBack);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public String getLoanProductDetails(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer loanProductId) {
        final String URL = "/fineract-provider/api/v1/loanproducts/" + loanProductId + "?associations=all&" + Utils.TENANT_IDENTIFIER;
        return Utils.performServerGet(requestSpec, responseSpec, URL, null);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public ArrayList getLoanCharges(final Integer loanId) {
        final String GET_LOAN_CHARGES_URL = "/fineract-provider/api/v1/loans/" + loanId + "/charges?" + Utils.TENANT_IDENTIFIER;
        return Utils.performServerGet(requestSpec, responseSpec, GET_LOAN_CHARGES_URL, "");
    }

    public List<GetLoansLoanIdChargesChargeIdResponse> getLoanCharges(final Long loanId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.retrieveAllLoanCharges(loanId));
    }

    public List<GetLoansLoanIdChargesChargeIdResponse> getLoanCharges(final String loanExternalId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.retrieveAllLoanCharges1(loanExternalId));
    }

    public GetLoansLoanIdChargesTemplateResponse getLoanChargeTemplate(final Long loanId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.retrieveTemplate8(loanId));
    }

    public GetLoansLoanIdChargesTemplateResponse getLoanChargeTemplate(final String loanExternalId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.retrieveTemplate9(loanExternalId));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap applyLoan(final String payload, final ResponseSpecification responseSpec) {
        final String postURLForLoan = "/fineract-provider/api/v1/loans?" + Utils.TENANT_IDENTIFIER;
        return Utils.performServerPost(this.requestSpec, this.responseSpec, postURLForLoan, payload, null);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public List getRepaymentTemplate(final Integer loanId) {
        final String GET_REPAYMENTS_URL = "/fineract-provider/api/v1/loans/" + loanId + "/transactions/template?command=repayment&"
                + Utils.TENANT_IDENTIFIER;
        return Utils.performServerGet(requestSpec, responseSpec, GET_REPAYMENTS_URL, "$");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public ArrayList<HashMap> getRepayments(final Integer loanId) {
        final String GET_REPAYMENTS_URL = "/fineract-provider/api/v1/loans/" + loanId + "/transactions/template?command=disburse&"
                + Utils.TENANT_IDENTIFIER;
        return Utils.performServerGet(requestSpec, responseSpec, GET_REPAYMENTS_URL, "loanRepaymentScheduleInstallments");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public PostLoansLoanIdTransactionsResponse applyLoanTransactionCommand(final Integer loanId, final Integer transactionId,
            final String command, final String payload, final ResponseSpecification responseSpec) {
        final String LOAN_TRANSACTION_URL = "/fineract-provider/api/v1/loans/" + loanId + "/transactions/" + transactionId + "?command="
                + command + "&" + Utils.TENANT_IDENTIFIER;
        final String response = Utils.performServerPost(requestSpec, responseSpec, LOAN_TRANSACTION_URL, payload, null);
        return GSON.fromJson(response, PostLoansLoanIdTransactionsResponse.class);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap approveLoan(final String approvalDate, final Integer loanID) {
        String loanApprovalCommand = createLoanOperationURL(APPROVE_LOAN_COMMAND, loanID);
        String loanApprovalRequest = getApproveLoanAsJSON(approvalDate);
        log.info("Loan approval command: {} ", loanApprovalCommand);
        log.info("Loan approval request: {} ", loanApprovalRequest);
        return performLoanTransaction(loanApprovalCommand, loanApprovalRequest);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap approveLoanWithApproveAmount(final String approvalDate, final String expectedDisbursementDate,
            final String approvalAmount, final Integer loanID, List<HashMap> tranches) {
        return performLoanTransaction(createLoanOperationURL(APPROVE_LOAN_COMMAND, loanID),
                getApproveLoanAsJSON(approvalDate, expectedDisbursementDate, approvalAmount, tranches));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public List<HashMap<String, Object>> approveLoanForTranches(final String approvalDate, final String expectedDisbursementDate,
            final String approvalAmount, final Integer loanID, List<HashMap> tranches, final String responseAttribute) {
        return (List<HashMap<String, Object>>) performLoanTransaction(createLoanOperationURL(APPROVE_LOAN_COMMAND, loanID),
                getApproveLoanAsJSON(approvalDate, expectedDisbursementDate, approvalAmount, tranches), responseAttribute);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object approveLoan(final String approvalDate, final String approvalAmount, final Integer loanID,
            final String responseAttribute) {

        final String approvalURL = createLoanOperationURL(APPROVE_LOAN_COMMAND, loanID);
        final String approvalJSONData = getApproveLoanAsJSON(approvalDate, null, approvalAmount, null);

        return performLoanTransaction(approvalURL, approvalJSONData, responseAttribute);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap undoApproval(final Integer loanID) {
        final String undoBodyJson = "{'note':'UNDO APPROVAL'}";
        return performLoanTransaction(createLoanOperationURL(UNDO_APPROVAL_LOAN_COMMAND, loanID), undoBodyJson);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap disburseLoanWithNetDisbursalAmount(final String date, final Integer loanID, final String netDisbursalAmount) {
        return performLoanTransaction(createLoanOperationURL(DISBURSE_LOAN_COMMAND, loanID),
                getDisburseLoanAsJSON(date, null, netDisbursalAmount));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap disburseLoan(final String date, final Integer loanID, final String transactionAmount, final String externalId) {
        return (HashMap) performLoanTransaction(createLoanOperationURL(DISBURSE_LOAN_COMMAND, loanID),
                getDisburseLoanAsJSON(date, transactionAmount, null, externalId), "");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object disburseLoanWithTransactionAmount(final String date, final Integer loanID, final String transactionAmount,
            ResponseSpecification responseSpec) {
        return performLoanTransaction(createLoanOperationURL(DISBURSE_LOAN_COMMAND, loanID),
                getDisburseLoanAsJSON(date, transactionAmount, null), responseSpec);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap disburseLoanWithTransactionAmount(final String date, final Integer loanID, final String transactionAmount) {
        return performLoanTransaction(createLoanOperationURL(DISBURSE_LOAN_COMMAND, loanID),
                getDisburseLoanAsJSON(date, transactionAmount, null));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap disburseLoanWithTransactionAmountAndWithoutAutoPayment(final String date, final Integer loanID,
            final String transactionAmount) {
        return performLoanTransaction(createLoanOperationURL(DISBURSE_LOAN_WITHOUT_AUTO_PAYMENT_COMMAND, loanID),
                getDisburseLoanAsJSON(date, transactionAmount, null));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap disburseLoanWithPostDatedChecks(final String date, final Integer loanId, final BigDecimal transactionAmount,
            final List<HashMap> postDatedChecks) {
        return performLoanTransaction(createLoanOperationURL(DISBURSE_LOAN_COMMAND, loanId),
                getDisburseLoanWithPostDatedChecksAsJSON(date, transactionAmount.toString(), postDatedChecks));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getDisburseLoanWithPostDatedChecksAsJSON(final String actualDisbursementDate, final String transactionAmount,
            final List<HashMap> postDatedChecks) {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("actualDisbursementDate", actualDisbursementDate);
        map.put("note", "DISBURSE NOTE");
        if (transactionAmount != null) {
            map.put("transactionAmount", transactionAmount);
        }

        map.put("postDatedChecks", postDatedChecks);
        log.info("Loan Application disburse request : {} ", map);
        return new Gson().toJson(map);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap disburseLoanWithRepaymentReschedule(final String date, final Integer loanID, String adjustRepaymentDate) {
        return performLoanTransaction(createLoanOperationURL(DISBURSE_LOAN_COMMAND, loanID),
                getDisburseLoanWithRepaymentRescheduleAsJSON(date, null, adjustRepaymentDate));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap disburseLoanWithNetDisbursalAmount(final String date, final Integer loanID, final String disburseAmt,
            final String netDisbursalAmount) {
        return performLoanTransaction(createLoanOperationURL(DISBURSE_LOAN_COMMAND, loanID),
                getDisburseLoanAsJSON(date, disburseAmt, netDisbursalAmount));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object disburseLoanWithNetDisbursalAmount(final String date, final Integer loanID, ResponseSpecification responseValidationError,
            final String netDisbursalAmount) {
        return performLoanTransaction(createLoanOperationURL(DISBURSE_LOAN_COMMAND, loanID),
                getDisburseLoanAsJSON(date, null, netDisbursalAmount), responseValidationError);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap disburseLoanToSavings(final String date, final Integer loanID, final String netDisbursalAmount) {
        return performLoanTransaction(createLoanOperationURL(DISBURSE_LOAN_TO_SAVINGS_COMMAND, loanID),
                getDisburseLoanAsJSON(date, null, netDisbursalAmount));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public PostLoansLoanIdResponse applyLoanCommand(final Integer loanId, final String command) {
        String undoBodyJson = "{}";
        String url = "";
        if (command.equals(UNDO_APPROVAL_LOAN_COMMAND)) {
            undoBodyJson = "{'note':'UNDO APPROVAL'}";
            url = createLoanOperationURL(UNDO_APPROVAL_LOAN_COMMAND, loanId);
        } else if (command.equals(UNDO_DISBURSE_LOAN_COMMAND)) {
            undoBodyJson = "{'note' : 'UNDO DISBURSAL'}";
            url = createLoanOperationURL(UNDO_DISBURSE_LOAN_COMMAND, loanId);
        }
        final String response = Utils.performServerPost(this.requestSpec, this.responseSpec, url, undoBodyJson, null);
        return GSON.fromJson(response, PostLoansLoanIdResponse.class);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap undoDisbursal(final Integer loanID) {
        final String undoDisburseJson = "{'note' : 'UNDO DISBURSAL'}";
        log.info("IN DISBURSE LOAN");
        final String url = createLoanOperationURL(UNDO_DISBURSE_LOAN_COMMAND, loanID);
        log.info("IN DISBURSE LOAN URL : {} ", url);
        return performLoanTransaction(createLoanOperationURL(UNDO_DISBURSE_LOAN_COMMAND, loanID), undoDisburseJson);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Float undoLastDisbursal(final Integer loanID) {
        final String undoLastDisburseJson = "{'note' : 'UNDO LAST DISBURSAL'}";
        final String url = createLoanOperationURL(UNDO_LAST_DISBURSE_LOAN_COMMAND, loanID);
        log.info("IN UNDO LAST DISBURSE LOAN URL : {} ", url);
        return performUndoLastLoanDisbursementTransaction(createLoanOperationURL(UNDO_LAST_DISBURSE_LOAN_COMMAND, loanID),
                undoLastDisburseJson);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap approveGlimAccount(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final List<Map<String, Object>> approvalFormData, final Integer glimID) {
        String approvalForm = new LoanApplicationTestBuilder() //
                .withApprovalFormData(approvalFormData).build();

        final String approvalURL = createGlimAccountURL(APPROVE_LOAN_COMMAND, glimID);
        return performLoanTransaction(approvalURL, approvalForm);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap disburseGlimAccount(final String date, final Integer glimID) {
        log.info("--------------------------------- GLIM DISBURSEMENT APPLICATION -------------------------------");
        return performLoanTransaction(createGlimAccountURL(DISBURSE_LOAN_COMMAND, glimID), getDisbursementAsJSON(date));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap undoDisburseGlimAccount(final Integer glimID) {
        log.info("--------------------------------- UNDO DISBURSAL GLIM APPLICATION -------------------------------");
        final String undoBodyJson = "{'note':'UNDO DISBURSAL'}";
        return performLoanTransaction(createGlimAccountURL(UNDO_DISBURSE_LOAN_COMMAND, glimID), undoBodyJson);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap undoApprovalGlimAccount(final Integer glimID) {
        log.info("--------------------------------- UNDO APPROVAL GLIM APPLICATION -------------------------------");
        final String undoBodyJson = "{'note':'UNDO APPROVAL'}";
        return performLoanTransaction(createGlimAccountURL(UNDO_APPROVAL_LOAN_COMMAND, glimID), undoBodyJson);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap rejectGlimAccount(final String date, final Integer glimID) {
        log.info("--------------------------------- REJECT GLIM APPLICATION -------------------------------");
        return performLoanTransaction(createGlimAccountURL(REJECT_LOAN_COMMAND, glimID), getRejectAsJSON(date));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public void recoverFromGuarantor(final Integer loanID) {
        performLoanTransaction(createLoanOperationURL(RECOVER_FROM_GUARANTORS_COMMAND, loanID), "", "");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap writeOffLoan(final String date, final Integer loanID) {
        return performLoanTransaction(createLoanTransactionURL(WRITE_OFF_LOAN_COMMAND, loanID), getWriteOffBodyAsJSON(date));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap waiveInterest(final String date, final String amountToBeWaived, final Integer loanID) {
        return performLoanTransaction(createLoanTransactionURL(WAIVE_INTEREST_COMMAND, loanID), getWaiveBodyAsJSON(date, amountToBeWaived));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Integer waiveInterestAndReturnTransactionId(final String date, final String amountToBeWaived, final Integer loanID) {
        Integer resourceId = Utils.performServerPost(this.requestSpec, this.responseSpec,
                createLoanTransactionURL(WAIVE_INTEREST_COMMAND, loanID), getWaiveBodyAsJSON(date, amountToBeWaived), "resourceId");
        return resourceId;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object creditBalanceRefund(final String date, final Float amountToBePaid, final String externalId, final Integer loanID,
            String jsonAttributeToGetback) {
        return performLoanTransaction(createLoanTransactionURL(CREDIT_BALANCE_REFUND_COMMAND, loanID),
                getCreditBalanceRefundBodyAsJSON(date, amountToBePaid, externalId), jsonAttributeToGetback);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object loanChargeRefund(final Integer loanChargeId, final Integer installmentNumber, final Float amountToBePaid,
            final String externalId, final Integer loanID, String jsonAttributeToGetback) {
        return performLoanTransaction(createLoanTransactionURL(LOANCHARGE_REFUND_REPAYMENT_COMMAND, loanID),
                getLoanChargeRefundBodyAsJSON(loanChargeId, installmentNumber, amountToBePaid, externalId), jsonAttributeToGetback);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object makeRepaymentTypePayment(final String repaymentTypeCommand, final String date, final Float amountToBePaid,
            final Integer loanID, String jsonAttributeToGetback) {
        return performLoanTransaction(createLoanTransactionURL(repaymentTypeCommand, loanID), getRepaymentBodyAsJSON(date, amountToBePaid),
                jsonAttributeToGetback);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap makeRepayment(final String date, final Float amountToBePaid, final Integer loanID) {
        return (HashMap) performLoanTransaction(createLoanTransactionURL(MAKE_REPAYMENT_COMMAND, loanID),
                getRepaymentBodyAsJSON(date, amountToBePaid), "");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap makeRepaymentWithAccountNo(final String date, final Float amountToBePaid, final String accountNo) {
        return (HashMap) performLoanTransaction(createInteroperationLoanTransactionURL(accountNo),
                getRepaymentBodyAsJSON(date, amountToBePaid), "");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap reverseRepayment(final Integer loanId, final Integer transactionId, String date) {
        return (HashMap) performLoanTransaction(createLoanTransactionURL(UNDO, loanId, transactionId),
                getAdjustTransactionJsonBody(date, "0"), "");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public PostLoansLoanIdTransactionsResponse makeLoanRepayment(final String repaymentTypeCommand, final String date,
            final Float amountToBePaid, final Integer loanID) {
        log.info("{} with amount {} in {} for Loan {}", repaymentTypeCommand, amountToBePaid, date, loanID);
        return postLoanTransaction(createLoanTransactionURL(repaymentTypeCommand, loanID), getRepaymentBodyAsJSON(date, amountToBePaid));
    }

    public PostLoansLoanIdTransactionsResponse makeLoanRepayment(final Long loanId, final String command, final String date,
            final Double amountToBePaid) {
        log.info("Make loan transaction. Command - {} with amount {} in {} for Loan {}", command, amountToBePaid, date, loanId);
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId,
                new PostLoansLoanIdTransactionsRequest().transactionAmount(amountToBePaid).transactionDate(date).dateFormat("dd MMMM yyyy")
                        .locale("en"),
                command));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public PostLoansLoanIdTransactionsResponse makeLoanRepayment(final String date, final Float amountToBePaid, final Integer loanID) {
        log.info("Repayment with amount {} in {} for Loan {}", amountToBePaid, date, loanID);
        return postLoanTransaction(createLoanTransactionURL(MAKE_REPAYMENT_COMMAND, loanID), getRepaymentBodyAsJSON(date, amountToBePaid));
    }

    public PostLoansLoanIdTransactionsResponse executeLoanTransaction(final Long loanId, final PostLoansLoanIdTransactionsRequest request,
            final String command) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, command));
    }

    public PostLoansLoanIdTransactionsResponse makeLoanRepayment(final Long loanId, final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "repayment"));
    }

    public PostLoansLoanIdTransactionsResponse makeLoanRepayment(final Long loanId, final PostLoansLoanIdTransactionsRequest request,
            final String user, final String pass) {
        return Calls.ok(FineractClientHelper.createNewFineractClient(user, pass).loanTransactions.executeLoanTransaction(loanId, request,
                "repayment"));
    }

    public PostLoansLoanIdTransactionsResponse addCapitalizedIncome(final Long loanId, final PostLoansLoanIdTransactionsRequest request) {
        return Calls
                .ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "capitalizedIncome"));
    }

    public PostLoansLoanIdTransactionsResponse addCapitalizedIncome(final Long loanId, final String transactionDate, final double amount) {
        return addCapitalizedIncome(loanId, new PostLoansLoanIdTransactionsRequest().transactionAmount(amount)
                .transactionDate(transactionDate).dateFormat("dd MMMM yyyy").locale("en"));
    }

    public Response<CommandProcessingResult> createInterestPause(Long loanId, String startDate, String endDate) {
        log.info("Creating interest pause for Loan {} from {} to {}", loanId, startDate, endDate);
        return Calls.executeU(FineractClientHelper.getFineractClient().loanInterestPauseApi.createInterestPause(loanId,
                new InterestPauseRequestDto().startDate(startDate).endDate(endDate).dateFormat(DATE_FORMAT).locale("en")));
    }

    public PostLoansLoanIdTransactionsResponse capitalizedIncomeAdjustment(final Long loanId, final Long capitalizedIncomeTransactionId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction(loanId,
                capitalizedIncomeTransactionId, request, "capitalizedIncomeAdjustment"));
    }

    public PostLoansLoanIdTransactionsResponse capitalizedIncomeAdjustment(final String loanExternalId, final Long transactionId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction2(loanExternalId, transactionId,
                request, "capitalizedIncomeAdjustment"));
    }

    public PostLoansLoanIdTransactionsResponse capitalizedIncomeAdjustment(final String loanExternalId, final String transactionExternalId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction3(loanExternalId,
                transactionExternalId, request, "capitalizedIncomeAdjustment"));
    }

    public PostLoansLoanIdTransactionsResponse capitalizedIncomeAdjustment(final Long loanId, final String transactionExternalId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction1(loanId, transactionExternalId,
                request, "capitalizedIncomeAdjustment"));
    }

    public PostLoansLoanIdTransactionsResponse capitalizedIncomeAdjustment(final Long loanId, final Long capitalizedIncomeTransactionId,
            final String transactionDate, final double amount) {
        return capitalizedIncomeAdjustment(loanId, capitalizedIncomeTransactionId, new PostLoansLoanIdTransactionsTransactionIdRequest()
                .transactionAmount(amount).transactionDate(transactionDate).dateFormat("dd MMMM yyyy").locale("en"));
    }

    public PostLoansLoanIdTransactionsResponse buyDownFeeAdjustment(final Long loanId, final Long buyDownFeeTransactionId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction(loanId, buyDownFeeTransactionId,
                request, "buyDownFeeAdjustment"));
    }

    public PostLoansLoanIdTransactionsResponse buyDownFeeAdjustment(final String loanExternalId, final Long transactionId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction2(loanExternalId, transactionId,
                request, "buyDownFeeAdjustment"));
    }

    public PostLoansLoanIdTransactionsResponse buyDownFeeAdjustment(final String loanExternalId, final String transactionExternalId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction3(loanExternalId,
                transactionExternalId, request, "buyDownFeeAdjustment"));
    }

    public PostLoansLoanIdTransactionsResponse buyDownFeeAdjustment(final Long loanId, final String transactionExternalId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction1(loanId, transactionExternalId,
                request, "buyDownFeeAdjustment"));
    }

    public PostLoansLoanIdTransactionsResponse buyDownFeeAdjustment(final Long loanId, final Long buyDownFeeTransactionId,
            final String transactionDate, final double amount) {
        return buyDownFeeAdjustment(loanId, buyDownFeeTransactionId, new PostLoansLoanIdTransactionsTransactionIdRequest()
                .transactionAmount(amount).transactionDate(transactionDate).dateFormat("dd MMMM yyyy").locale("en"));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public PostLoansLoanIdTransactionsResponse createInterestPauseByLoanId(final String startDate, final String endDate,
            final String dateFormat, final String locale, final Integer loanID) {
        log.info("Creating interest pause for Loan {} from {} to {} with dateFormat {} and locale {}", loanID, startDate, endDate,
                dateFormat, locale);
        String body = getInterestPauseBodyAsJSON(startDate, endDate, dateFormat, locale);
        return postLoanTransaction(createInterestPause(INTEREST_PAUSE_COMMAND, loanID), body);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public PostLoansLoanIdTransactionsResponse createInterestPauseByExternalId(final String startDate, final String endDate,
            final String dateFormat, final String locale, final String externalId) {
        log.info("Creating interest pause for Loan {} from {} to {} with dateFormat {} and locale {}", externalId, startDate, endDate,
                dateFormat, locale);
        String body = getInterestPauseBodyAsJSON(startDate, endDate, dateFormat, locale);
        return postLoanTransaction(createInterestPause(INTEREST_PAUSE_COMMAND, externalId), body);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public PostLoansLoanIdTransactionsResponse updateInterestPauseByLoanId(final Long termVariationId, final String startDate,
            final String endDate, final String dateFormat, final String locale, final Integer loanID) {
        log.info("Updating interest pause for Loan {} with Term Variation ID {}: startDate={} endDate={} dateFormat={} locale={}", loanID,
                termVariationId, startDate, endDate, dateFormat, locale);
        String body = getInterestPauseBodyAsJSON(startDate, endDate, dateFormat, locale);
        return putLoanTransaction(updateInterestPause(termVariationId, loanID), body);
    }

    public PostLoansLoanIdTransactionsResponse updateInterestPauseByExternalId(final Long termVariationId, final String startDate,
            final String endDate, final String dateFormat, final String locale, final String externalID) {
        log.info("Updating interest pause for Loan {} with Term Variation ID {}: startDate={} endDate={} dateFormat={} locale={}",
                externalID, termVariationId, startDate, endDate, dateFormat, locale);
        String body = getInterestPauseBodyAsJSON(startDate, endDate, dateFormat, locale);
        return putLoanTransaction(updateInterestPause(termVariationId, externalID), body);
    }

    public void deleteInterestPauseByLoanId(final Long termVariationId, final Integer loanID) {
        log.info("Deleting interest pause for Loan ID {} with Term Variation ID {}", loanID, termVariationId);
        deleteLoanTransaction(deleteInterestPause(termVariationId, loanID));
    }

    public void deleteInterestPauseByExternalId(final Long termVariationId, final String externalID) {
        log.info("Deleting interest pause for Loan ID {} with Term Variation ID {}", externalID, termVariationId);
        deleteLoanTransaction(deleteInterestPause(termVariationId, externalID));
    }

    public String retrieveInterestPauseByLoanId(final Integer loanID) {
        log.info("Retrieving interest pauses for Loan ID {}", loanID);
        String url = retrieveInterestPause(loanID);
        return Utils.performServerGet(requestSpec, responseSpec, url);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public String retrieveInterestPauseByExternalId(final String externalId) {
        log.info("Retrieving interest pauses for External ID {}", externalId);
        String url = retrieveInterestPause(externalId);
        return Utils.performServerGet(requestSpec, responseSpec, url);
    }

    public PostLoansLoanIdTransactionsResponse makeInterestPaymentWaiver(final Long loanId,
            final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(
                FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "interestPaymentWaiver"));
    }

    public PostLoansLoanIdTransactionsResponse makeInterestPaymentWaiver(final String loanExternalId,
            final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request,
                "interestPaymentWaiver"));
    }

    public PostLoansLoanIdTransactionsResponse reAge(final Long loanId, final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "reAge"));
    }

    public PostLoansLoanIdTransactionsResponse reAmortize(final Long loanId, final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "reAmortize"));
    }

    public PostLoansLoanIdTransactionsResponse undoReAge(final Long loanId, final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "undoReAge"));
    }

    public PostLoansLoanIdTransactionsResponse undoReAmortize(final Long loanId, final PostLoansLoanIdTransactionsRequest request) {
        return Calls
                .ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "undoReAmortize"));
    }

    public PutChargeTransactionChangesResponse undoWaiveLoanCharge(final Long loanId, final Long transactionId,
            final PutChargeTransactionChangesRequest request) {
        log.info("--------------------------------- UNDO WAIVE CHARGES FOR LOAN --------------------------------");
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.undoWaiveCharge(loanId, transactionId, request));
    }

    public PutChargeTransactionChangesResponse undoWaiveLoanCharge(final Long loanId, final String transactionExternalId,
            final PutChargeTransactionChangesRequest request) {
        log.info("--------------------------------- UNDO WAIVE CHARGES FOR LOAN --------------------------------");
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.undoWaiveCharge1(loanId, transactionExternalId, request));
    }

    public PutChargeTransactionChangesResponse undoWaiveLoanCharge(final String loanExternalId, final Long transactionId,
            final PutChargeTransactionChangesRequest request) {
        log.info("--------------------------------- UNDO WAIVE CHARGES FOR LOAN --------------------------------");
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.undoWaiveCharge2(loanExternalId, transactionId, request));
    }

    public PutChargeTransactionChangesResponse undoWaiveLoanCharge(final String loanExternalId, final String transactionExternalId,
            final PutChargeTransactionChangesRequest request) {
        log.info("--------------------------------- UNDO WAIVE CHARGES FOR LOAN --------------------------------");
        return Calls.ok(
                FineractClientHelper.getFineractClient().loanTransactions.undoWaiveCharge3(loanExternalId, transactionExternalId, request));
    }

    public PostLoansLoanIdChargesChargeIdResponse waiveLoanCharge(final Long loanId, final Long loanChargeId,
            final PostLoansLoanIdChargesChargeIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.executeLoanCharge2(loanId, loanChargeId, request, "waive"));
    }

    public PostLoansLoanIdChargesChargeIdResponse waiveLoanCharge(final String loanExternalId, final Long loanChargeId,
            final PostLoansLoanIdChargesChargeIdRequest request) {
        return Calls.ok(
                FineractClientHelper.getFineractClient().loanCharges.executeLoanCharge4(loanExternalId, loanChargeId, request, "waive"));
    }

    public PostLoansLoanIdChargesChargeIdResponse waiveLoanCharge(final Long loanId, final String loanChargeExternalId,
            final PostLoansLoanIdChargesChargeIdRequest request) {
        return Calls.ok(
                FineractClientHelper.getFineractClient().loanCharges.executeLoanCharge3(loanId, loanChargeExternalId, request, "waive"));
    }

    public PostLoansLoanIdChargesChargeIdResponse waiveLoanCharge(final String loanExternalId, final String loanChargeExternalId,
            final PostLoansLoanIdChargesChargeIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.executeLoanCharge5(loanExternalId, loanChargeExternalId,
                request, "waive"));
    }

    public PostLoansLoanIdTransactionsResponse makeLoanRepayment(final String loanExternalId,
            final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(
                FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request, "repayment"));
    }

    public PostLoansLoanIdTransactionsResponse makeMerchantIssuedRefund(final Long loanId,
            final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(
                FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "merchantIssuedRefund"));
    }

    public PostLoansLoanIdTransactionsResponse makeMerchantIssuedRefund(final String loanExternalId,
            final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request,
                "merchantIssuedRefund"));
    }

    public PostLoansLoanIdTransactionsResponse makePayoutRefund(final Long loanId, final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "payoutRefund"));
    }

    public PostLoansLoanIdTransactionsResponse makePayoutRefund(final String loanExternalId,
            final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(
                FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request, "payoutRefund"));
    }

    public PostLoansLoanIdTransactionsResponse makeChargeRefund(final Long loanId, final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "chargeRefund"));
    }

    public PostLoansLoanIdTransactionsResponse makeChargeRefund(final String loanExternalId,
            final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(
                FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request, "chargeRefund"));
    }

    public PostLoansLoanIdTransactionsResponse manualInterestRefund(final Long loanId, final Long targetTransactionId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction(loanId, targetTransactionId,
                request, "interest-refund"));
    }

    public PostLoansLoanIdTransactionsResponse makeGoodwillCredit(final Long loanId, final PostLoansLoanIdTransactionsRequest request) {
        return Calls
                .ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "goodwillCredit"));
    }

    public PostLoansLoanIdTransactionsResponse makeGoodwillCredit(final String loanExternalId,
            final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request,
                "goodwillCredit"));
    }

    public PostLoansLoanIdTransactionsResponse makeWaiveInterest(final Long loanId, final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "waiveinterest"));
    }

    public PostLoansLoanIdTransactionsResponse makeWaiveInterest(final String loanExternalId,
            final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request,
                "waiveinterest"));
    }

    public PostLoansLoanIdTransactionsResponse makeWriteoff(final Long loanId, final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "writeoff"));
    }

    public PostLoansLoanIdTransactionsResponse makeWriteoff(final String loanExternalId, final PostLoansLoanIdTransactionsRequest request) {
        return Calls
                .ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request, "writeoff"));
    }

    public PostLoansLoanIdTransactionsResponse makeUndoWriteoff(final Long loanId, final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "undowriteoff"));
    }

    public PostLoansLoanIdTransactionsResponse makeUndoWriteoff(final String loanExternalId,
            final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(
                FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request, "undowriteoff"));
    }

    public PostLoansLoanIdTransactionsResponse makeRecoveryPayment(final Long loanId, final PostLoansLoanIdTransactionsRequest request) {
        return Calls
                .ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "recoverypayment"));
    }

    public PostLoansLoanIdTransactionsResponse makeRecoveryPayment(final String loanExternalId,
            final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request,
                "recoverypayment"));
    }

    public PostLoansLoanIdTransactionsResponse makeRefundByCash(final Long loanId, final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "refundByCash"));
    }

    public PostLoansLoanIdTransactionsResponse makeRefundByCash(final String loanExternalId,
            final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(
                FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request, "refundByCash"));
    }

    public PostLoansLoanIdTransactionsResponse makeCreditBalanceRefund(final Long loanId,
            final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(
                FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "creditBalanceRefund"));
    }

    public PostLoansLoanIdTransactionsResponse makeCreditBalanceRefund(final String loanExternalId,
            final PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request,
                "creditBalanceRefund"));
    }

    public PostLoansLoanIdTransactionsResponse reverseLoanTransaction(final String loanExternalId, final Long transactionId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction2(loanExternalId, transactionId,
                request, "undo"));
    }

    public PostLoansLoanIdTransactionsResponse reverseLoanTransaction(final String loanExternalId, final String transactionExternalId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction3(loanExternalId,
                transactionExternalId, request, "undo"));
    }

    public PostLoansLoanIdTransactionsResponse reverseLoanTransaction(final Long loanId, final String transactionExternalId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction1(loanId, transactionExternalId,
                request, "undo"));
    }

    public PostLoansLoanIdTransactionsResponse chargebackLoanTransaction(final Long loanId, final Long transactionId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction(loanId, transactionId, request,
                "chargeback"));
    }

    public PostLoansLoanIdTransactionsResponse chargebackLoanTransaction(final String loanExternalId, final Long transactionId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction2(loanExternalId, transactionId,
                request, "chargeback"));
    }

    public PostLoansLoanIdTransactionsResponse chargebackLoanTransaction(final String loanExternalId, final String transactionExternalId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction3(loanExternalId,
                transactionExternalId, request, "chargeback"));
    }

    public PostLoansLoanIdTransactionsResponse chargebackLoanTransaction(final Long loanId, final String transactionExternalId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction1(loanId, transactionExternalId,
                request, "chargeback"));
    }

    public PostLoansLoanIdTransactionsResponse adjustLoanTransaction(final String loanExternalId, final Long transactionId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction2(loanExternalId, transactionId,
                request, "adjust"));
    }

    public PostLoansLoanIdTransactionsResponse adjustLoanTransaction(final String loanExternalId, final String transactionExternalId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction3(loanExternalId,
                transactionExternalId, request, "adjust"));
    }

    public PostLoansLoanIdTransactionsResponse adjustLoanTransaction(final Long loanId, final String transactionExternalId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction1(loanId, transactionExternalId,
                request, "adjust"));
    }

    public PostLoansLoanIdTransactionsResponse adjustLoanTransaction(final Long loanId, final Long transactionId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(
                FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction(loanId, transactionId, request, "adjust"));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public PostLoansLoanIdTransactionsResponse adjustLoanTransaction(final Integer loanId, final Long transactionId, String date,
            ResponseSpecification responseSpec) {
        return postLoanTransaction(createLoanTransactionURL(null, loanId, transactionId.intValue()),
                getAdjustTransactionJsonBody(date, "10"), responseSpec);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public PostLoansLoanIdTransactionsResponse reverseLoanTransaction(final Integer loanId, final Long transactionId, String date,
            ResponseSpecification responseSpec) {
        return postLoanTransaction(createLoanTransactionURL(UNDO, loanId, transactionId.intValue()),
                getAdjustTransactionJsonBody(date, "0"), responseSpec);
    }

    public PostLoansLoanIdTransactionsResponse reverseLoanTransaction(final Long loanId, final Long transactionId,
            final PostLoansLoanIdTransactionsTransactionIdRequest request) {
        return Calls.ok(
                FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction(loanId, transactionId, request, "undo"));
    }

    public PostLoansLoanIdTransactionsResponse reverseLoanTransaction(final Long loanId, final Long transactionId, String date) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.adjustLoanTransaction(loanId, transactionId,
                new PostLoansLoanIdTransactionsTransactionIdRequest().dateFormat(DATE_FORMAT).transactionDate(date).transactionAmount(0.0)
                        .locale("en"),
                "undo"));
    }

    public HashMap makeRepaymentWithPDC(final String date, final Float amountToBePaid, final Integer loanID, final Long paymentType) {
        return (HashMap) performLoanTransaction(createLoanTransactionURL(MAKE_REPAYMENT_COMMAND, loanID),
                getRepaymentWithPDCBodyAsJSON(date, amountToBePaid, paymentType), "");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap forecloseLoan(final String transactionDate, final Integer loanID) {
        return (HashMap) performLoanTransaction(createLoanTransactionURL(FORECLOSURE_COMMAND, loanID),
                getForeclosureBodyAsJSON(transactionDate, loanID), "");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap withdrawLoanApplicationByClient(final String date, final Integer loanID) {
        return performLoanTransaction(createLoanOperationURL(WITHDRAW_LOAN_APPLICATION_COMMAND, loanID),
                getWithdrawLoanApplicationBodyAsJSON(date));
    }

    public PostLoansLoanIdChargesResponse addLoanCharge(final Long loanId, final PostLoansLoanIdChargesRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.executeLoanCharge(loanId, request, ""));
    }

    public PostLoansLoanIdChargesResponse addLoanCharge(final String loanExternalId, final PostLoansLoanIdChargesRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.executeLoanCharge1(loanExternalId, request, ""));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Integer addChargesForLoan(final Integer loanId, final String request) {
        log.info("--------------------------------- ADD CHARGES FOR LOAN --------------------------------");
        final String ADD_CHARGES_URL = LOAN_ACCOUNT_URL + "/" + loanId + "/charges?" + Utils.TENANT_IDENTIFIER;
        final HashMap response = Utils.performServerPost(requestSpec, responseSpec, ADD_CHARGES_URL, request, "");
        return (Integer) response.get("resourceId");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap addChargesForLoanGetFullResponse(final Integer loanId, final String request) {
        log.info("--------------------------------- ADD CHARGES FOR LOAN --------------------------------");
        final String ADD_CHARGES_URL = LOAN_ACCOUNT_URL + "/" + loanId + "/charges?" + Utils.TENANT_IDENTIFIER;
        return Utils.performServerPost(requestSpec, responseSpec, ADD_CHARGES_URL, request, "");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Integer addChargesForLoan(final Integer loanId, final String request, final ResponseSpecification responseSpecParam) {
        log.info("--------------------------------- ADD CHARGES FOR LOAN --------------------------------");
        final String ADD_CHARGES_URL = LOAN_ACCOUNT_URL + "/" + loanId + "/charges?" + Utils.TENANT_IDENTIFIER;
        final HashMap response = Utils.performServerPost(requestSpec, responseSpecParam, ADD_CHARGES_URL, request, "");
        return (Integer) response.get("resourceId");
    }

    public PostLoansLoanIdChargesResponse addChargesForLoan(final Long loanId, PostLoansLoanIdChargesRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.executeLoanCharge(loanId, request, null));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public PostLoansLoanIdChargesResponse addChargeForLoan(final Integer loanId, final String payload,
            final ResponseSpecification responseSpecParam) {
        log.info("--------------------------------- ADD CHARGES FOR LOAN --------------------------------");
        final String ADD_CHARGES_URL = LOAN_ACCOUNT_URL + "/" + loanId + "/charges?" + Utils.TENANT_IDENTIFIER;
        final String response = Utils.performServerPost(requestSpec, responseSpecParam, ADD_CHARGES_URL, payload);
        return GSON.fromJson(response, PostLoansLoanIdChargesResponse.class);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object addChargesForAllreadyDisursedLoan(final Integer loanId, final String request,
            final ResponseSpecification responseSpecification) {
        final String ADD_CHARGES_URL = "/fineract-provider/api/v1/loans/" + loanId + "/charges?" + Utils.TENANT_IDENTIFIER;
        return Utils.performServerPost(this.requestSpec, responseSpecification, ADD_CHARGES_URL, request, "");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Integer updateChargesForLoan(final Integer loanId, final Integer loanchargeId, final String request) {
        log.info("--------------------------------- ADD CHARGES FOR LOAN --------------------------------");
        final String UPDATE_CHARGES_URL = "/fineract-provider/api/v1/loans/" + loanId + "/charges/" + loanchargeId + "?"
                + Utils.TENANT_IDENTIFIER;
        final HashMap response = Utils.performServerPut(requestSpec, responseSpec, UPDATE_CHARGES_URL, request, "");
        return (Integer) response.get("resourceId");
    }

    public PutLoansLoanIdChargesChargeIdResponse updateLoanCharge(final Long loanId, final Long loanChargeId,
            final PutLoansLoanIdChargesChargeIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.updateLoanCharge(loanId, loanChargeId, request));
    }

    public PutLoansLoanIdChargesChargeIdResponse updateLoanCharge(final Long loanId, final String loanChargeExternalId,
            final PutLoansLoanIdChargesChargeIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.updateLoanCharge1(loanId, loanChargeExternalId, request));
    }

    public PutLoansLoanIdChargesChargeIdResponse updateLoanCharge(final String loanExternalId, final Long loanChargeId,
            final PutLoansLoanIdChargesChargeIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.updateLoanCharge2(loanExternalId, loanChargeId, request));
    }

    public PutLoansLoanIdChargesChargeIdResponse updateLoanCharge(final String loanExternalId, final String loanChargeExternalId,
            final PutLoansLoanIdChargesChargeIdRequest request) {
        return Calls
                .ok(FineractClientHelper.getFineractClient().loanCharges.updateLoanCharge3(loanExternalId, loanChargeExternalId, request));
    }

    public DeleteLoansLoanIdChargesChargeIdResponse deleteLoanCharge(final Long loanId, final Long loanChargeId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.deleteLoanCharge(loanId, loanChargeId));
    }

    public DeleteLoansLoanIdChargesChargeIdResponse deleteLoanCharge(final Long loanId, final String loanChargeExternalId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.deleteLoanCharge1(loanId, loanChargeExternalId));
    }

    public DeleteLoansLoanIdChargesChargeIdResponse deleteLoanCharge(final String loanExternalId, final Long loanChargeId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.deleteLoanCharge2(loanExternalId, loanChargeId));
    }

    public DeleteLoansLoanIdChargesChargeIdResponse deleteLoanCharge(final String loanExternalId, final String loanChargeExternalId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.deleteLoanCharge3(loanExternalId, loanChargeExternalId));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Integer deleteChargesForLoan(final Integer loanId, final Integer loanchargeId) {
        log.info("--------------------------------- DELETE CHARGES FOR LOAN --------------------------------");
        final String DELETE_CHARGES_URL = "/fineract-provider/api/v1/loans/" + loanId + "/charges/" + loanchargeId + "?"
                + Utils.TENANT_IDENTIFIER;
        final HashMap response = Utils.performServerDelete(requestSpec, responseSpec, DELETE_CHARGES_URL, "");
        return (Integer) response.get("resourceId");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public PostLoansLoanIdChargesChargeIdResponse applyLoanChargeCommand(final Integer loanId, final Long loanchargeId, final String commad,
            final String json) {
        log.info("--------------------------------- WAIVE CHARGES FOR LOAN --------------------------------");
        final String CHARGES_URL = "/fineract-provider/api/v1/loans/" + loanId + "/charges/" + loanchargeId + "?command=" + commad + "&"
                + Utils.TENANT_IDENTIFIER;
        final String response = Utils.performServerPost(requestSpec, responseSpec, CHARGES_URL, json, null);
        return GSON.fromJson(response, PostLoansLoanIdChargesChargeIdResponse.class);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Integer waiveChargesForLoan(final Integer loanId, final Integer loanchargeId, final String json) {
        log.info("--------------------------------- WAIVE CHARGES FOR LOAN --------------------------------");
        final String CHARGES_URL = "/fineract-provider/api/v1/loans/" + loanId + "/charges/" + loanchargeId + "?command=waive&"
                + Utils.TENANT_IDENTIFIER;
        final HashMap response = Utils.performServerPost(requestSpec, responseSpec, CHARGES_URL, json, "");
        return (Integer) response.get("resourceId");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap undoWaiveChargesForLoan(final Integer loanId, final Integer transactionId, final String body) {
        log.info("--------------------------------- UNDO WAIVE CHARGES FOR LOAN --------------------------------");
        final String TRANSAC_URL = "/fineract-provider/api/v1/loans/" + loanId + "/transactions/" + transactionId + "?"
                + Utils.TENANT_IDENTIFIER;
        return Utils.performServerPut(requestSpec, responseSpec, TRANSAC_URL, body, "");
    }

    public PostLoansLoanIdChargesChargeIdResponse chargeAdjustment(final Long loanId, final Long chargeId,
            final PostLoansLoanIdChargesChargeIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.executeLoanCharge2(loanId, chargeId, request, "adjustment"));
    }

    public PostLoansLoanIdChargesChargeIdResponse chargeAdjustment(final String loanExternalId, final String loanChargeExternalId,
            final PostLoansLoanIdChargesChargeIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.executeLoanCharge5(loanExternalId, loanChargeExternalId,
                request, "adjustment"));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Integer undoWaiveChargesForLoanReturnResourceId(final Integer loanId, final Integer transactionId, final String body) {
        log.info("--------------------------------- UNDO WAIVE CHARGES FOR LOAN --------------------------------");
        final String TRANSAC_URL = "/fineract-provider/api/v1/loans/" + loanId + "/transactions/" + transactionId + "?"
                + Utils.TENANT_IDENTIFIER;
        final HashMap response = Utils.performServerPut(requestSpec, responseSpec, TRANSAC_URL, body, "");
        return (Integer) response.get("resourceId");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Integer payChargesForLoan(final Integer loanId, final Integer loanchargeId, final String json) {
        log.info("--------------------------------- WAIVE CHARGES FOR LOAN --------------------------------");
        final String CHARGES_URL = "/fineract-provider/api/v1/loans/" + loanId + "/charges/" + loanchargeId + "?command=pay&"
                + Utils.TENANT_IDENTIFIER;
        final HashMap response = Utils.performServerPost(requestSpec, responseSpec, CHARGES_URL, json, "");
        return (Integer) response.get("resourceId");
    }

    public PostLoansLoanIdChargesChargeIdResponse payLoanCharge(final Long loanId, final Long loanChargeId,
            final PostLoansLoanIdChargesChargeIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.executeLoanCharge2(loanId, loanChargeId, request, "pay"));
    }

    public PostLoansLoanIdChargesChargeIdResponse payLoanCharge(final String loanExternalId, final Long loanChargeId,
            final PostLoansLoanIdChargesChargeIdRequest request) {
        return Calls
                .ok(FineractClientHelper.getFineractClient().loanCharges.executeLoanCharge4(loanExternalId, loanChargeId, request, "pay"));
    }

    public PostLoansLoanIdChargesChargeIdResponse payLoanCharge(final String loanExternalId, final String loanChargeExternalId,
            final PostLoansLoanIdChargesChargeIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.executeLoanCharge5(loanExternalId, loanChargeExternalId,
                request, "pay"));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public ArrayList<HashMap> getLoanTransactionDetails(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer loanID) {
        final String URL = "/fineract-provider/api/v1/loans/" + loanID + "?associations=all&exclude=guarantors,futureSchedule&"
                + Utils.TENANT_IDENTIFIER;
        return Utils.performServerGet(requestSpec, responseSpec, URL, "transactions");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap getLoanCharge(final Integer loanId, final Integer chargeId) {
        final String GET_LOAN_CHARGES_URL = "/fineract-provider/api/v1/loans/" + loanId + "/charges/" + chargeId + "?"
                + Utils.TENANT_IDENTIFIER;
        return Utils.performServerGet(requestSpec, responseSpec, GET_LOAN_CHARGES_URL, "");
    }

    public GetLoansLoanIdChargesChargeIdResponse getLoanCharge(final Long loanId, final Long loanChargeId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.retrieveLoanCharge(loanId, loanChargeId));
    }

    public GetLoansLoanIdChargesChargeIdResponse getLoanCharge(final String loanExternalId, final Long loanChargeId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.retrieveLoanCharge2(loanExternalId, loanChargeId));
    }

    public GetLoansLoanIdChargesChargeIdResponse getLoanCharge(final Long loanId, final String loanChargeExternalId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.retrieveLoanCharge1(loanId, loanChargeExternalId));
    }

    public GetLoansLoanIdChargesChargeIdResponse getLoanCharge(final String loanExternalId, final String loanChargeExternalId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCharges.retrieveLoanCharge3(loanExternalId, loanChargeExternalId));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object getLoanTransactionDetails(final Integer loanId, final Integer txnId, final String param) {
        final String GET_LOAN_CHARGES_URL = "/fineract-provider/api/v1/loans/" + loanId + "/transactions/" + txnId + "?"
                + Utils.TENANT_IDENTIFIER;
        return Utils.performServerGet(requestSpec, responseSpec, GET_LOAN_CHARGES_URL, param);
    }

    public GetLoansLoanIdTransactionsTransactionIdResponse getLoanTransactionDetails(final Long loanId, final Long transactionId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.retrieveTransaction(loanId, transactionId, null));
    }

    public GetLoansLoanIdTransactionsTransactionIdResponse getLoanTransactionDetails(final String loanExternalId,
            final Long transactionId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions
                .retrieveTransactionByLoanExternalIdAndTransactionId(loanExternalId, transactionId, null));
    }

    public GetLoansLoanIdTransactionsTransactionIdResponse getLoanTransactionDetails(final Long loanId,
            final String transactionExternalId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.retrieveTransactionByTransactionExternalId(loanId,
                transactionExternalId, null));
    }

    public GetLoansLoanIdTransactionsTransactionIdResponse getLoanTransactionDetails(final String loanExternalId,
            final String transactionExternalId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions
                .retrieveTransactionByLoanExternalIdAndTransactionExternalId(loanExternalId, transactionExternalId, null));
    }

    public GetLoansLoanIdResponse getLoanDetails(final Long loanId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.retrieveLoan(loanId, false, "all", null, null));
    }

    public GetLoansLoanIdResponse getLoanDetails(final String loanExternalId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.retrieveLoan1(loanExternalId, false, "all", null, null));
    }

    public GetLoansLoanIdTransactionsResponse getLoanTransactions(final Long loanId) {
        return getLoanTransactions(loanId, Collections.emptyList(), null, null, null);
    }

    public GetLoansLoanIdTransactionsResponse getLoanTransactions(final Long loanId, List<TransactionType> excludedTransactionTypes) {
        return getLoanTransactions(loanId, excludedTransactionTypes, null, null, null);
    }

    public GetLoansLoanIdTransactionsResponse getLoanTransactions(final Long loanId, List<TransactionType> excludedTransactionTypes,
            Integer page, Integer size, String sort) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.retrieveTransactionsByLoanId(loanId,
                excludedTransactionTypes, page, size, sort));
    }

    public GetLoansLoanIdTransactionsResponse getLoanTransactionsByExternalId(final String loanExternalId) {
        return getLoanTransactionsByExternalId(loanExternalId, Collections.emptyList(), null, null, null);
    }

    public GetLoansLoanIdTransactionsResponse getLoanTransactionsByExternalId(final String loanExternalId,
            List<TransactionType> excludedTransactionTypes) {
        return getLoanTransactionsByExternalId(loanExternalId, excludedTransactionTypes, null, null, null);
    }

    public GetLoansLoanIdTransactionsResponse getLoanTransactionsByExternalId(final String loanExternalId,
            List<TransactionType> excludedTransactionTypes, Integer page, Integer size, String sort) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.retrieveTransactionsByExternalLoanId(loanExternalId,
                excludedTransactionTypes, page, size, sort));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)

    public GetLoansResponse retrieveAllLoans(final String accountNumber, final String associations, final Long clientId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.retrieveAll27(null, 0, 10, null, null, accountNumber, associations,
                clientId, null));
    }

    @Deprecated(forRemoval = true)
    public GetLoansLoanIdTransactionsTransactionIdResponse getLoanTransaction(final Integer loanId, final Integer txnId) {
        final String GET_LOAN_CHARGES_URL = "/fineract-provider/api/v1/loans/" + loanId + "/transactions/" + txnId + "?"
                + Utils.TENANT_IDENTIFIER;
        final String response = Utils.performServerGet(requestSpec, responseSpec, GET_LOAN_CHARGES_URL);
        return GSON.fromJson(response, GetLoansLoanIdTransactionsTransactionIdResponse.class);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap getPostDatedCheck(final Integer loanId, final Integer installmentId) {
        final String GET_POST_DATED_TRANS_URL = "/fineract-provider/api/v1/loans/" + loanId + "/postdatedchecks/" + installmentId + "?"
                + Utils.TENANT_IDENTIFIER;
        return Utils.performServerGet(requestSpec, responseSpec, GET_POST_DATED_TRANS_URL, "");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getDisburseLoanAsJSON(final String actualDisbursementDate, final String transactionAmount,
            final String netDisbursalAmount) {
        return getDisburseLoanAsJSON(actualDisbursementDate, transactionAmount, netDisbursalAmount, null);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getDisburseLoanAsJSON(final String actualDisbursementDate, final String transactionAmount,
            final String netDisbursalAmount, final String externalId) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("actualDisbursementDate", actualDisbursementDate);
        if (netDisbursalAmount != null) {
            map.put("netDisbursalAmount", netDisbursalAmount);
        }
        map.put("note", "DISBURSE NOTE");
        if (transactionAmount != null) {
            map.put("transactionAmount", transactionAmount);
        }
        if (externalId != null) {
            map.put("externalId", externalId);
        }
        log.info("Loan Application disburse request : {} ", map);
        return new Gson().toJson(map);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getDisburseLoanWithRepaymentRescheduleAsJSON(final String actualDisbursementDate, final String transactionAmount,
            final String adjustRepaymentDate) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("actualDisbursementDate", actualDisbursementDate);
        map.put("adjustRepaymentDate", adjustRepaymentDate);
        map.put("note", "DISBURSE NOTE");
        if (transactionAmount != null) {
            map.put("transactionAmount", transactionAmount);
        }
        log.info("Loan Application disburse request : {} ", map);
        return new Gson().toJson(map);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public String getApproveLoanAsJSON(final String approvalDate) {
        return getApproveLoanAsJSON(approvalDate, null, null, null);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getApproveLoanAsJSON(final String approvalDate, final String expectedDisbursementDate, final String approvalAmount,
            List<HashMap> tranches) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        if (approvalAmount != null) {
            map.put("approvedLoanAmount", approvalAmount);
        }
        map.put("approvedOnDate", approvalDate);
        if (expectedDisbursementDate != null) {
            map.put("expectedDisbursementDate", expectedDisbursementDate);
        }
        if (tranches != null && tranches.size() > 0) {
            map.put("disbursementData", tranches);
        }
        map.put("note", "Approval NOTE");
        return new Gson().toJson(map);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getDisbursementAsJSON(final String date) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("actualDisbursementDate", date);
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        return new Gson().toJson(map);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getRejectAsJSON(final String date) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("rejectedOnDate", date);
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        return new Gson().toJson(map);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getLoanChargeRefundBodyAsJSON(final Integer loanChargeId, final Integer installmentNumber, final Float transactionAmount,
            final String externalId) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("loanChargeId", loanChargeId.toString());
        map.put("transactionAmount", transactionAmount.toString());
        map.put("note", "Loancharge Refund Made!!!");
        if (externalId != null) {
            map.put("externalId", externalId);
        }
        if (installmentNumber != null) {
            map.put("installmentNumber", installmentNumber.toString());
        }
        return new Gson().toJson(map);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getCreditBalanceRefundBodyAsJSON(final String transactionDate, final Float transactionAmount, final String externalId) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("transactionDate", transactionDate);
        map.put("transactionAmount", transactionAmount.toString());
        map.put("note", "Credit Balance Refund Made!!!");
        if (externalId != null) {
            map.put("externalId", externalId);
        }
        return new Gson().toJson(map);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getRepaymentBodyAsJSON(final String transactionDate, final Float transactionAmount) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("transactionDate", transactionDate);
        map.put("transactionAmount", transactionAmount.toString());
        map.put("note", "Repayment Made!!!");
        return new Gson().toJson(map);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getInterestPauseBodyAsJSON(final String startDate, final String endDate, final String dateFormat, final String locale) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("dateFormat", dateFormat);
        map.put("locale", locale);

        return new Gson().toJson(map);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getAdjustTransactionJsonBody(String date, String amount) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("transactionDate", date);
        map.put("transactionAmount", amount);
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("locale", "en");
        return new Gson().toJson(map);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getRepaymentWithPDCBodyAsJSON(final String transactionDate, final Float transactionAmount, final Long paymentTypeId) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("locale", "en");
        map.put("paymentTypeId", paymentTypeId.toString());
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("transactionDate", transactionDate);
        map.put("transactionAmount", transactionAmount.toString());
        map.put("note", "Repayment Made!!!");
        return new Gson().toJson(map);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getForeclosureBodyAsJSON(final String transactionDate, final Integer loanId) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("transactionDate", transactionDate);
        map.put("note", "Foreclosure Made!!!");
        String json = new Gson().toJson(map);
        log.info("{}", json);
        return json;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getWriteOffBodyAsJSON(final String transactionDate) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("locale", "en");
        map.put("note", " LOAN WRITE OFF!!!");
        map.put("transactionDate", transactionDate);
        return new Gson().toJson(map);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getWaiveBodyAsJSON(final String transactionDate, final String amountToBeWaived) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("transactionDate", transactionDate);
        map.put("transactionAmount", amountToBeWaived);
        map.put("note", " Interest Waived!!!");
        return new Gson().toJson(map);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getWithdrawLoanApplicationBodyAsJSON(final String withdrawDate) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("withdrawnOnDate", withdrawDate);
        map.put("note", " Loan Withdrawn By Client!!!");
        return new Gson().toJson(map);

    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static String getSpecifiedDueDateChargesForLoanAsJSON(final String chargeId) {
        return getSpecifiedDueDateChargesForLoanAsJSON(chargeId, "12 January 2013", "100", null);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static String getSpecifiedDueDateChargesForLoanAsJSON(final String chargeId, final String dueDate, final String amount) {
        return getSpecifiedDueDateChargesForLoanAsJSON(chargeId, dueDate, amount, null);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static String getSpecifiedDueDateChargesForLoanAsJSON(final String chargeId, final String dueDate, final String amount,
            final String externalId) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("locale", "en_GB");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("amount", amount);
        map.put("dueDate", dueDate);
        map.put("chargeId", chargeId);
        if (externalId != null) {
            map.put("externalId", externalId);
        }
        String json = new Gson().toJson(map);
        log.info("{}", json);
        return json;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static String getSpecifiedInstallmentChargesForLoanAsJSON(final String chargeId, final String amount) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("locale", "en_GB");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("amount", amount);
        map.put("chargeId", chargeId);
        String json = new Gson().toJson(map);
        log.info("{}", json);
        return json;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static String getDisbursementChargesForLoanAsJSON(final String chargeId) {
        return getDisbursementChargesForLoanAsJSON(chargeId, "100");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static String getDisbursementChargesForLoanAsJSON(final String chargeId, String amount) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("locale", "en_GB");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("amount", amount);
        map.put("chargeId", chargeId);
        String json = new Gson().toJson(map);
        log.info("{}", json);
        return json;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static String getInstallmentChargesForLoanAsJSON(final String chargeId, final String amount) {
        return getInstallmentChargesForLoanAsJSON(chargeId, amount, Locale.UK);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static String getInstallmentChargesForLoanAsJSON(final String chargeId, final Object amount, final Locale locale) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("locale", locale.getLanguage());
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("amount", amount);
        map.put("chargeId", chargeId);
        String json = new Gson().toJson(map);
        log.info("{}", json);
        return json;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static String getUpdateChargesForLoanAsJSON(String amount) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("locale", "en_GB");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("amount", amount);
        String json = new Gson().toJson(map);
        log.info("{}", json);
        return json;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static String getPayChargeJSON(final String date, final String installmentNumber) {
        return getPayChargeJSON(date, installmentNumber, null);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static String getPayChargeJSON(final String date, final String installmentNumber, final String externalId) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("locale", "en_GB");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("transactionDate", date);
        if (installmentNumber != null) {
            map.put("installmentNumber", installmentNumber);
        }
        if (externalId != null) {
            map.put("externalId", externalId);
        }
        String json = new Gson().toJson(map);
        log.info("{}", json);
        return json;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static String getWaiveChargeJSON(final String installmentNumber) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("locale", "en_GB");
        map.put("installmentNumber", installmentNumber);
        String json = new Gson().toJson(map);
        log.info("{}", json);
        return json;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public String getLoanCalculationBodyAsJSON(final String productID) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("locale", "en_GB");
        map.put("productId", productID);
        map.put("principal", "4,500.00");
        map.put("loanTermFrequency", "4");
        map.put("loanTermFrequencyType", "2");
        map.put("numberOfRepayments", "4");
        map.put("repaymentEvery", "1");
        map.put("repaymentFrequencyType", "2");
        map.put("interestRateFrequencyType", "2");
        map.put("interestRatePerPeriod", "2");
        map.put("amortizationType", "1");
        map.put("interestType", "0");
        map.put("interestCalculationPeriodType", "1");
        map.put("expectedDisbursementDate", "20 September 2011");
        map.put("transactionProcessingStrategyCode", "mifos-standard-strategy");
        return new Gson().toJson(map);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public String createLoanOperationURL(final String command, final Integer loanID) {
        return "/fineract-provider/api/v1/loans/" + loanID + "?command=" + command + "&" + Utils.TENANT_IDENTIFIER;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String createLoanTransactionURL(final String command, final Integer loanID) {
        return "/fineract-provider/api/v1/loans/" + loanID + "/transactions?command=" + command + "&" + Utils.TENANT_IDENTIFIER;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String createInterestPause(final String command, final Integer loanID) {
        return "/fineract-provider/api/v1/loans/" + loanID + "/interest-pauses?command=" + command + "&" + Utils.TENANT_IDENTIFIER;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String createInterestPause(final String command, final String externalId) {
        return "/fineract-provider/api/v1/loans/external-id/" + externalId + "/interest-pauses?command=" + command + "&"
                + Utils.TENANT_IDENTIFIER;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String retrieveInterestPause(final Integer loanID) {
        return "/fineract-provider/api/v1/loans/" + loanID + "/interest-pauses?" + Utils.TENANT_IDENTIFIER;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String retrieveInterestPause(final String externalId) {
        return "/fineract-provider/api/v1/loans/external-id/" + externalId + "/interest-pauses?" + Utils.TENANT_IDENTIFIER;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String updateInterestPause(final Long termVariationId, final Integer loanID) {
        return "/fineract-provider/api/v1/loans/" + loanID + "/interest-pauses/" + termVariationId + "?" + Utils.TENANT_IDENTIFIER;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String updateInterestPause(final Long termVariationId, final String externalID) {
        return "/fineract-provider/api/v1/loans/external-id/" + externalID + "/interest-pauses/" + termVariationId + "?"
                + Utils.TENANT_IDENTIFIER;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String deleteInterestPause(final Long termVariationId, final Integer loanID) {
        return "/fineract-provider/api/v1/loans/" + loanID + "/interest-pauses/" + termVariationId + "?" + Utils.TENANT_IDENTIFIER;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String deleteInterestPause(final Long termVariationId, final String externalID) {
        return "/fineract-provider/api/v1/loans/external-id/" + externalID + "/interest-pauses/" + termVariationId + "?"
                + Utils.TENANT_IDENTIFIER;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String createInteroperationLoanTransactionURL(final String accountNo) {
        return "/fineract-provider/api/v1/interoperation/transactions/" + accountNo + "/loanrepayment";
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String createLoanTransactionURL(final String command, final Integer loanID, final Integer transactionId) {
        String url = "/fineract-provider/api/v1/loans/" + loanID + "/transactions/" + transactionId + "?";
        if (command != null) {
            url = url + "command=" + command + "&";
        }
        return url + Utils.TENANT_IDENTIFIER;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String createGlimAccountURL(final String command, final Integer glimID) {
        return "/fineract-provider/api/v1/loans/glimAccount/" + glimID + "?command=" + command + "&" + Utils.TENANT_IDENTIFIER;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private HashMap performLoanTransaction(final String postURLForLoanTransaction, final String jsonToBeSent) {
        log.info("URL: {}", postURLForLoanTransaction);
        log.info("Body: {}", jsonToBeSent);
        final HashMap response = Utils.performServerPost(this.requestSpec, this.responseSpec, postURLForLoanTransaction, jsonToBeSent,
                "changes");
        return (HashMap) response.get("status");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private Float performUndoLastLoanDisbursementTransaction(final String postURLForLoanTransaction, final String jsonToBeSent) {

        final HashMap response = Utils.performServerPost(this.requestSpec, this.responseSpec, postURLForLoanTransaction, jsonToBeSent,
                "changes");
        return (Float) response.get("disbursedAmount");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private Object performLoanTransaction(final String postURLForLoanTransaction, final String jsonToBeSent,
            final String responseAttribute) {
        return Utils.performServerPost(this.requestSpec, this.responseSpec, postURLForLoanTransaction, jsonToBeSent, responseAttribute);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private PostLoansLoanIdTransactionsResponse postLoanTransaction(final String postURLForLoanTransaction, final String jsonToBeSent) {
        return postLoanTransaction(postURLForLoanTransaction, jsonToBeSent, this.responseSpec);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private PostLoansLoanIdTransactionsResponse postLoanTransaction(final String postURLForLoanTransaction, final String jsonToBeSent,
            ResponseSpecification responseSpec) {
        final String response = Utils.performServerPost(this.requestSpec, responseSpec, postURLForLoanTransaction, jsonToBeSent);
        return GSON.fromJson(response, PostLoansLoanIdTransactionsResponse.class);
    }

    private PostLoansLoanIdTransactionsResponse putLoanTransaction(final String putURLForLoanTransaction, final String jsonToBeSent) {
        final String response = Utils.performServerPut(this.requestSpec, this.responseSpec, putURLForLoanTransaction, jsonToBeSent);
        return GSON.fromJson(response, PostLoansLoanIdTransactionsResponse.class);
    }

    private void deleteLoanTransaction(final String deleteURLForLoanTransaction) {
        Utils.performServerDelete(this.requestSpec, this.responseSpec, deleteURLForLoanTransaction, null);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private Object performLoanTransaction(final String postURLForLoanTransaction, final String jsonToBeSent,
            ResponseSpecification responseValidationError) {

        return Utils.performServerPost(this.requestSpec, responseValidationError, postURLForLoanTransaction, jsonToBeSent,
                CommonConstants.RESPONSE_ERROR);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object adjustLoanTransaction(final Integer loanId, final Integer transactionId, final String date,
            final String transactionAmount, final String responseAttribute) {
        return adjustLoanTransaction(loanId, transactionId, getAdjustTransactionJSON(date, transactionAmount), responseAttribute);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private Object adjustLoanTransaction(final Integer loanId, final Integer tansactionId, final String jsonToBeSent,
            final String responseAttribute) {
        final String URL = "/fineract-provider/api/v1/loans/" + loanId + "/transactions/" + tansactionId + "?" + Utils.TENANT_IDENTIFIER;
        return Utils.performServerPost(this.requestSpec, this.responseSpec, URL, jsonToBeSent, responseAttribute);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getAdjustTransactionJSON(final String date, final String transactionAmount) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("locale", "en_GB");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("transactionDate", date);
        map.put("transactionAmount", transactionAmount);
        String json = new Gson().toJson(map);
        log.info("{}", json);
        return json;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap getPrepayAmount(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final Integer loanID) {
        final String URL = "/fineract-provider/api/v1/loans/" + loanID + "/transactions/template?command=prepayLoan&"
                + Utils.TENANT_IDENTIFIER;
        final HashMap response = Utils.performServerGet(requestSpec, responseSpec, URL, "");
        return response;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap getPrepayAmount(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final Integer loanID,
            final LocalDate transactionDate) {
        final String URL = "/fineract-provider/api/v1/loans/" + loanID
                + "/transactions/template?command=prepayLoan&locale=en&dateFormat=yyyy-MM-dd&transactionDate=" + transactionDate + "&"
                + Utils.TENANT_IDENTIFIER;
        final HashMap response = Utils.performServerGet(requestSpec, responseSpec, URL, "");
        return response;
    }

    public GetLoansLoanIdTransactionsTemplateResponse getPrepaymentAmount(final Long loanId, final String transactionDate,
            String dateformat) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.retrieveTransactionTemplate(loanId, "prepayLoan",
                dateformat, transactionDate, "en", null));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String createLoanRefundTransferURL() {
        return "/fineract-provider/api/v1/accounttransfers/refundByTransfer?tenantIdentifier=default";
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public void verifyRepaymentScheduleEntryFor(final int repaymentNumber, final float expectedPrincipalOutstanding, final Integer loanID) {
        log.info("---------------------------GETTING LOAN REPAYMENT SCHEDULE--------------------------------");
        final ArrayList<HashMap> repaymentPeriods = getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        assertEquals(expectedPrincipalOutstanding, repaymentPeriods.get(repaymentNumber).get("principalLoanBalanceOutstanding"),
                "Mismatch in Principal Loan Balance Outstanding ");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public void checkAccrualTransactionForRepayment(final LocalDate transactionDate, final Float interestPortion, final Float feePortion,
            final Float penaltyPortion, final Integer loanID) {

        ArrayList<HashMap> transactions = (ArrayList<HashMap>) getLoanTransactions(this.requestSpec, this.responseSpec, loanID);
        boolean isTransactionFound = false;
        for (int i = 0; i < transactions.size(); i++) {
            HashMap transactionType = (HashMap) transactions.get(i).get("type");
            boolean isAccrualTransaction = (Boolean) transactionType.get("accrual");

            if (isAccrualTransaction) {
                ArrayList<Integer> accrualEntryDateAsArray = (ArrayList<Integer>) transactions.get(i).get("date");
                LocalDate accrualEntryDate = LocalDate.of(accrualEntryDateAsArray.get(0), accrualEntryDateAsArray.get(1),
                        accrualEntryDateAsArray.get(2));

                if (DateUtils.isEqual(transactionDate, accrualEntryDate)) {
                    isTransactionFound = true;
                    assertEquals(interestPortion, Float.valueOf(String.valueOf(transactions.get(i).get("interestPortion"))),
                            "Mismatch in transaction amounts");
                    assertEquals(feePortion, Float.valueOf(String.valueOf(transactions.get(i).get("feeChargesPortion"))),
                            "Mismatch in transaction amounts");
                    assertEquals(penaltyPortion, Float.valueOf(String.valueOf(transactions.get(i).get("penaltyChargesPortion"))),
                            "Mismatch in transaction amounts");
                    break;
                }
            }
        }
        assertTrue(isTransactionFound, "No Accrual entries are posted");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public void noAccrualTransactionForRepayment(final Integer loanID) {
        ArrayList<HashMap> transactions = (ArrayList<HashMap>) getLoanTransactions(this.requestSpec, this.responseSpec, loanID);
        for (HashMap transaction : transactions) {
            HashMap transactionType = (HashMap) transaction.get("type");
            assertFalse((Boolean) transactionType.get("accrual"), "Accrual entries are posted!");
        }
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap makeRefundByCash(final String date, final Float amountToBeRefunded, final Integer loanID) {
        return performLoanTransaction(createLoanTransactionURL(MAKE_REFUND_BY_CASH_COMMAND, loanID),
                getRefundByCashBodyAsJSON(date, amountToBeRefunded));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap makeRefundByTransfer(final Integer fromAccountId, final Integer toClientId, final Integer toAccountId,
            final Integer fromClientId, final String date, final Float amountToBeRefunded) {
        return performLoanTransaction(createLoanRefundTransferURL(),
                getRefundByTransferBodyAsJSON(fromAccountId, toClientId, toAccountId, fromClientId, date, amountToBeRefunded));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getRefundByCashBodyAsJSON(final String transactionDate, final Float transactionAmount) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("transactionDate", transactionDate);
        map.put("transactionAmount", transactionAmount.toString());
        map.put("note", "Refund Made!!!");
        return new Gson().toJson(map);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String getRefundByTransferBodyAsJSON(final Integer fromAccountId, final Integer toClientId, final Integer toAccountId,
            final Integer fromClientId, final String transactionDate, final Float transactionAmount) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("fromAccountId", fromAccountId.toString());
        map.put("fromAccountType", "1");
        map.put("toOfficeId", "1");
        map.put("toClientId", toClientId.toString());
        map.put("toAccountType", "2");
        map.put("toAccountId", toAccountId.toString());
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("transferDate", transactionDate);
        map.put("transferAmount", transactionAmount.toString());
        map.put("transferDescription", "Refund Made!!!");
        map.put("fromClientId", fromClientId.toString());
        map.put("fromOfficeId", "1");
        map.put("locale", "en");
        return new Gson().toJson(map);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public String getLoanFraudPayloadAsJSON(final String attrName, final String attrValue) {
        final HashMap<String, String> map = new HashMap<>();
        map.put(attrName, attrValue);
        return new Gson().toJson(map);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public HashMap createTrancheDetail(final String id, final String date, final String amount) {
        HashMap<String, Object> detail = new HashMap<>();
        if (id != null) {
            detail.put("id", id);
        }
        detail.put("expectedDisbursementDate", date);
        detail.put("principal", amount);

        return detail;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object editDisbursementDetail(final Integer loanID, final Integer disbursementId, final String approvalAmount,
            final String expectedDisbursementDate, final String updatedExpectedDisbursementDate, final String updatedPrincipal,
            final String jsonAttributeToGetBack) {

        return Utils.performServerPut(this.requestSpec, this.responseSpec, createEditDisbursementURL(loanID, disbursementId),
                getEditDisbursementsAsJSON(approvalAmount, expectedDisbursementDate, updatedExpectedDisbursementDate, updatedPrincipal),
                jsonAttributeToGetBack);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object addAndDeleteDisbursementDetail(final Integer loanID, final String approvalAmount, final String expectedDisbursementDate,
            List<HashMap> disbursementData, final String jsonAttributeToGetBack) {

        return Utils.performServerPut(this.requestSpec, this.responseSpec, createAddAndDeleteDisbursementURL(loanID),
                getAddAndDeleteDisbursementsAsJSON(approvalAmount, expectedDisbursementDate, disbursementData), jsonAttributeToGetBack);
    }

    public String addAndDeleteDisbursementDetail(final Long loanId, PostAddAndDeleteDisbursementDetailRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanDisbursementDetails.addAndDeleteDisbursementDetail(loanId, request));
    }

    public String addAndDeleteDisbursementDetail(final Long loanId, final List<DisbursementDetail> disbursementDetails) {
        return addAndDeleteDisbursementDetail(loanId, new PostAddAndDeleteDisbursementDetailRequest().locale("en")
                .dateFormat("dd MMMM yyyy").disbursementData(disbursementDetails));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String createEditDisbursementURL(Integer loanID, Integer disbursementId) {
        return "/fineract-provider/api/v1/loans/" + loanID + "/disbursements/" + disbursementId + "?" + Utils.TENANT_IDENTIFIER;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String createAddAndDeleteDisbursementURL(Integer loanID) {
        return "/fineract-provider/api/v1/loans/" + loanID + "/disbursements/editDisbursements?" + Utils.TENANT_IDENTIFIER;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static String getEditDisbursementsAsJSON(final String approvalAmount, final String expectedDisbursementDate,
            final String updatedExpectedDisbursementDate, final String updatedPrincipal) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("approvedLoanAmount", approvalAmount);
        map.put("expectedDisbursementDate", expectedDisbursementDate);
        map.put("updatedExpectedDisbursementDate", updatedExpectedDisbursementDate);
        map.put("updatedPrincipal", updatedPrincipal);
        String json = new Gson().toJson(map);
        log.info("{}", json);
        return json;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static String getAddAndDeleteDisbursementsAsJSON(final String approvalAmount, final String expectedDisbursementDate,
            final List<HashMap> disbursementData) {
        final HashMap map = new HashMap<>();
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("approvedLoanAmount", approvalAmount);
        map.put("expectedDisbursementDate", expectedDisbursementDate);
        map.put("disbursementData", disbursementData);
        String json = new Gson().toJson(map);
        log.info("{}", json);
        return json;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static List<HashMap<String, Object>> getTestDatatableAsJson(final String registeredTableName) {
        List<HashMap<String, Object>> datatablesListMap = new ArrayList<>();
        HashMap<String, Object> datatableMap = new HashMap<>();
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("locale", "en");
        dataMap.put("Spouse Name", Utils.randomStringGenerator("Spouse_name", 4));
        dataMap.put("Number of Dependents", 5);
        dataMap.put("Time of Visit", "01 December 2016 04:03");
        dataMap.put("dateFormat", DATE_TIME_FORMAT);
        dataMap.put("Date of Approval", "02 December 2016 00:00");
        datatableMap.put("registeredTableName", registeredTableName);
        datatableMap.put("data", dataMap);
        datatablesListMap.add(datatableMap);
        return datatablesListMap;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Workbook getLoanWorkbook(String dateFormat) throws IOException {
        requestSpec.header(HttpHeaders.CONTENT_TYPE, "application/vnd.ms-excel");
        byte[] byteArray = Utils.performGetBinaryResponse(requestSpec, responseSpec,
                LOAN_ACCOUNT_URL + "/downloadtemplate" + "?" + Utils.TENANT_IDENTIFIER + "&dateFormat=" + dateFormat);
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        Workbook workbook = new HSSFWorkbook(inputStream);
        return workbook;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public String importLoanTemplate(File file) {

        String locale = "en";
        String dateFormat = "dd MMMM yyyy";
        String legalFormType = null;
        requestSpec.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA);
        return Utils.performServerTemplatePost(requestSpec, responseSpec,
                LOAN_ACCOUNT_URL + "/uploadtemplate" + "?" + Utils.TENANT_IDENTIFIER, legalFormType, file, locale, dateFormat);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public String getOutputTemplateLocation(final String importDocumentId) {
        requestSpec.header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN);
        return Utils.performServerOutputTemplateLocationGet(requestSpec, responseSpec,
                "/fineract-provider/api/v1/imports/getOutputTemplateLocation" + "?" + Utils.TENANT_IDENTIFIER, importDocumentId);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static HashMap<String, Object> getLoanAuditFields(final RequestSpecification requestSpec,
            final ResponseSpecification responseSpec, final Integer loanId, final String jsonReturn) {
        final String GET_LOAN_URL = "/fineract-provider/api/v1/internal/loan/" + loanId + "/audit?" + Utils.TENANT_IDENTIFIER;
        log.info("---------------------------------GET A LOAN ENTITY AUDIT FIELDS---------------------------------------------");
        return Utils.performServerGet(requestSpec, responseSpec, GET_LOAN_URL, jsonReturn);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static HashMap<String, Object> getLoanTransactionAuditFields(final RequestSpecification requestSpec,
            final ResponseSpecification responseSpec, final Integer loanId, final Integer transactionId, final String jsonReturn) {
        final String GET_LOAN_TRANSACTION_URL = "/fineract-provider/api/v1/internal/loan/" + loanId + "/transaction/" + transactionId
                + "/audit?" + Utils.TENANT_IDENTIFIER;
        log.info(
                "---------------------------------GET A LOAN TRANSACTION ENTITY AUDIT FIELDS---------------------------------------------");
        return Utils.performServerGet(requestSpec, responseSpec, GET_LOAN_TRANSACTION_URL, jsonReturn);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Long applyInterestRefundLoanTransaction(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Long loanId, final String jsonBody) {
        final String POST_LOAN_TRANSACTION_URL = "/fineract-provider/api/v1/internal/loan/" + loanId + "/apply-interest-refund/" + "?"
                + Utils.TENANT_IDENTIFIER;
        final String reponse = Utils.performServerPost(requestSpec, responseSpec, POST_LOAN_TRANSACTION_URL, jsonBody);
        return Long.valueOf(reponse);
    }

    public void printRepaymentSchedule(GetLoansLoanIdResponse getLoansLoanIdResponse) {
        GetLoansLoanIdRepaymentSchedule getLoanRepaymentSchedule = getLoansLoanIdResponse.getRepaymentSchedule();
        if (getLoanRepaymentSchedule != null) {
            log.info("Loan with {} periods", getLoanRepaymentSchedule.getPeriods().size());
            for (GetLoansLoanIdRepaymentPeriod period : getLoanRepaymentSchedule.getPeriods()) {
                log.info("Period number {} for due date {} and outstanding {} {}", period.getPeriod(), period.getDueDate(),
                        period.getTotalOutstandingForPeriod(), period.getComplete());
            }
        }
    }

    public void printDelinquencyData(GetLoansLoanIdResponse getLoansLoanIdResponse) {
        GetLoansLoanIdDelinquencySummary getLoansLoanIdCollectionData = getLoansLoanIdResponse.getDelinquent();
        if (getLoansLoanIdCollectionData != null) {
            log.info("Loan Delinquency {}", getLoansLoanIdCollectionData);
        }
    }

    public void evaluateLoanTransactionData(GetLoansLoanIdResponse getLoansLoanIdResponse, String transactionType, Double amountExpected) {
        List<GetLoansLoanIdTransactions> transactions = getLoansLoanIdResponse.getTransactions();
        log.info("Loan with {} transactions", transactions.size());
        Double transactionsAmount = 0.0;
        for (GetLoansLoanIdTransactions transaction : transactions) {
            log.info("  Id {} code {} date {} amount {}", transaction.getId(), transaction.getType().getCode(), transaction.getDate(),
                    transaction.getAmount());
            if (transactionType.equals(transaction.getType().getCode())) {
                transactionsAmount += Utils.getDoubleValue(transaction.getAmount());
            }
        }
        assertEquals(amountExpected, transactionsAmount);
    }

    public Long evaluateLastLoanTransactionData(GetLoansLoanIdResponse getLoansLoanIdResponse, String transactionType,
            String transactionExpected, Double amountExpected) {
        List<GetLoansLoanIdTransactions> transactions = getLoansLoanIdResponse.getTransactions();
        log.info("Loan with {} transactions", transactions.size());
        GetLoansLoanIdTransactions lastTransaction = null;
        for (GetLoansLoanIdTransactions transaction : transactions) {
            log.info("  Id {} code {} date {} amount {}", transaction.getId(), transaction.getType().getCode(), transaction.getDate(),
                    transaction.getAmount());
            if (transactionType.equals(transaction.getType().getCode())) {
                lastTransaction = transaction;
            }
        }
        assertEquals(transactionExpected, Utils.dateFormatter.format(lastTransaction.getDate()));
        assertEquals(amountExpected, Utils.getDoubleValue(lastTransaction.getAmount()));
        return lastTransaction.getId();
    }

    public void validateLoanStatus(GetLoansLoanIdResponse getLoansLoanIdResponse, final String statusCodeExpected) {
        final String statusCode = getLoansLoanIdResponse.getStatus().getCode();
        log.info("Loan with Id {} is with Status {}", getLoansLoanIdResponse.getId(), statusCode);
        assertEquals(statusCodeExpected, statusCode);
    }

    public void validateLoanPrincipalOustandingBalance(GetLoansLoanIdResponse getLoansLoanIdResponse, Double amountExpected) {
        GetLoansLoanIdSummary getLoansLoanIdSummary = getLoansLoanIdResponse.getSummary();
        if (getLoansLoanIdSummary != null) {
            log.info("Loan with Principal Outstanding Balance {} expected {}", getLoansLoanIdSummary.getPrincipalOutstanding(),
                    amountExpected);
            assertEquals(amountExpected, Utils.getDoubleValue(getLoansLoanIdSummary.getPrincipalOutstanding()));
        }
    }

    public void validateLoanFeesOustandingBalance(GetLoansLoanIdResponse getLoansLoanIdResponse, Double amountExpected) {
        GetLoansLoanIdSummary getLoansLoanIdSummary = getLoansLoanIdResponse.getSummary();
        if (getLoansLoanIdSummary != null) {
            log.info("Loan with Fees Outstanding Balance {} expected {}", getLoansLoanIdSummary.getFeeChargesOutstanding(), amountExpected);
            assertEquals(amountExpected, Utils.getDoubleValue(getLoansLoanIdSummary.getFeeChargesOutstanding()));
        }
    }

    public void validateLoanPenaltiesOustandingBalance(GetLoansLoanIdResponse getLoansLoanIdResponse, Double amountExpected) {
        GetLoansLoanIdSummary getLoansLoanIdSummary = getLoansLoanIdResponse.getSummary();
        assertNotNull(getLoansLoanIdSummary);
        log.info("Loan with Fees Outstanding Balance {} expected {}", getLoansLoanIdSummary.getFeeChargesOutstanding(), amountExpected);
        assertEquals(amountExpected, Utils.getDoubleValue(getLoansLoanIdSummary.getPenaltyChargesOutstanding()));
    }

    public void validateLoanTotalOustandingBalance(GetLoansLoanIdResponse getLoansLoanIdResponse, Double amountExpected) {
        GetLoansLoanIdSummary getLoansLoanIdSummary = getLoansLoanIdResponse.getSummary();
        if (getLoansLoanIdSummary != null) {
            log.info("Loan with Total Outstanding Balance {} expected {}", getLoansLoanIdSummary.getTotalOutstanding(), amountExpected);
            assertEquals(amountExpected, Utils.getDoubleValue(getLoansLoanIdSummary.getTotalOutstanding()));
        }
    }

    public void evaluateLoanDisbursementDetails(GetLoansLoanIdResponse getLoansLoanIdResponse, Integer numItems, Double amountExpected) {
        log.info("Disbursement details items: {}", getLoansLoanIdResponse.getDisbursementDetails().size());
        assertEquals(numItems, getLoansLoanIdResponse.getDisbursementDetails().size());
        Double amount = Double.valueOf("0.0");
        for (GetLoansLoanIdDisbursementDetails disbursementDetails : getLoansLoanIdResponse.getDisbursementDetails()) {
            amount = amount + disbursementDetails.getPrincipal();
            log.info("Disbursement details with principal {} {}", disbursementDetails.getExpectedDisbursementDate(),
                    disbursementDetails.getPrincipal());
        }
        assertEquals(amountExpected, amount);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Long applyChargebackTransaction(final Integer loanId, final Long transactionId, final String amount,
            final Integer paymentTypeIdx, ResponseSpecification responseSpec) {
        List<PaymentTypeData> paymentTypeList = paymentTypeHelper.getAllPaymentTypes(false);
        assertTrue(!paymentTypeList.isEmpty());

        final String payload = createChargebackPayload(amount, paymentTypeList.get(paymentTypeIdx).getId());
        log.info("Loan Chargeback: {}", payload);
        PostLoansLoanIdTransactionsResponse postLoansTransactionCommandResponse = applyLoanTransactionCommand(loanId,
                transactionId.intValue(), "chargeback", payload, responseSpec);
        assertNotNull(postLoansTransactionCommandResponse);

        log.info("Loan Chargeback Id: {}", postLoansTransactionCommandResponse.getResourceId());
        return postLoansTransactionCommandResponse.getResourceId();
    }

    public void reviewLoanTransactionRelations(final Integer loanId, final Long transactionId, final Integer expectedSize) {
        GetLoansLoanIdTransactionsTransactionIdResponse getLoansTransactionResponse = getLoanTransaction(loanId, transactionId.intValue());
        assertNotNull(getLoansTransactionResponse);
        assertNotNull(getLoansTransactionResponse.getTransactionRelations());
        assertEquals(expectedSize, getLoansTransactionResponse.getTransactionRelations().size());
        log.info("Loan with {} Chargeback Transactions", getLoansTransactionResponse.getTransactionRelations().size());
    }

    public void evaluateLoanSummaryAdjustments(GetLoansLoanIdResponse getLoansLoanIdResponse, Double amountExpected) {
        // Evaluate The Loan Summary Principal Adjustments
        GetLoansLoanIdSummary getLoansLoanIdSummary = getLoansLoanIdResponse.getSummary();
        if (getLoansLoanIdSummary != null) {
            log.info("Loan with Principal Adjustments {} expected {}", getLoansLoanIdSummary.getPrincipalAdjustments(), amountExpected);
            assertEquals(amountExpected, Utils.getDoubleValue(getLoansLoanIdSummary.getPrincipalAdjustments()));
        }
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private String createChargebackPayload(final String transactionAmount, final Long paymentTypeId) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("transactionAmount", transactionAmount);
        map.put("paymentTypeId", paymentTypeId);
        map.put("locale", CommonConstants.LOCALE);
        final String chargebackPayload = new Gson().toJson(map);
        log.info("{}", chargebackPayload);
        return chargebackPayload;
    }

    public GetLoansLoanIdTransactionsTemplateResponse retrieveTransactionTemplate(Long loanId, String command, String dateFormat,
            String transactionDate, String locale, Long transactionId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.retrieveTransactionTemplate(loanId, command, dateFormat,
                transactionDate, locale, transactionId));
    }

    public GetLoansLoanIdTransactionsTemplateResponse retrieveTransactionTemplate(Long loanId, String command, String dateFormat,
            String transactionDate, String locale) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.retrieveTransactionTemplate(loanId, command, dateFormat,
                transactionDate, locale, null));
    }

    public GetLoansLoanIdTransactionsTemplateResponse retrieveTransactionTemplate(String loanExternalIdStr, String command,
            String dateFormat, String transactionDate, String locale) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.retrieveTransactionTemplate1(loanExternalIdStr, command,
                dateFormat, transactionDate, locale, null));
    }

    public GetLoansApprovalTemplateResponse getLoanApprovalTemplate(String loanExternalIdStr) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.retrieveApprovalTemplate1(loanExternalIdStr, "approval"));
    }

    public DeleteLoansLoanIdResponse deleteLoanApplication(String loanExternalId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.deleteLoanApplication1(loanExternalId));
    }

    public List<GetDelinquencyTagHistoryResponse> getLoanDelinquencyTags(String loanExternalId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.getDelinquencyTagHistory1(loanExternalId));
    }

    public PostLoansResponse applyLoan(PostLoansRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.calculateLoanScheduleOrSubmitLoanApplication(request, null));
    }

    public void applyLoanWithError(PostLoansRequest request, Integer httpStatus) {
        CallFailedRuntimeException exception = assertThrows(CallFailedRuntimeException.class,
                () -> Calls.ok(FineractClientHelper.getFineractClient().loans.calculateLoanScheduleOrSubmitLoanApplication(request, null)));
        assertEquals(exception.getResponse().code(), httpStatus);
    }

    public PostLoansLoanIdResponse approveLoan(String loanExternalId, PostLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions1(loanExternalId, request, "approve"));
    }

    public PostLoansLoanIdResponse approveLoan(Long loanId, PostLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions(loanId, request, "approve"));
    }

    public PostLoansLoanIdResponse rejectLoan(String loanExternalId, PostLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions1(loanExternalId, request, "reject"));
    }

    public PostLoansLoanIdResponse rejectLoan(Long loanId, PostLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions(loanId, request, "reject"));
    }

    public PostLoansLoanIdResponse withdrawnByApplicantLoan(String loanExternalId, PostLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions1(loanExternalId, request, "withdrawnByApplicant"));
    }

    public PostLoansLoanIdResponse disburseLoan(String loanExternalId, PostLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions1(loanExternalId, request, "disburse"));
    }

    public PostLoansLoanIdResponse disburseLoan(Long loanId, PostLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions(loanId, request, "disburse"));
    }

    public PostLoansLoanIdResponse moveLoanState(Long loanId, PostLoansLoanIdRequest request, String command) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions(loanId, request, command));
    }

    /**
     * Disburse loan on provided date and amount.
     *
     * @param loanId
     *            loan Id
     * @param date
     *            formatted to "d MMMM yyyy"
     * @param amount
     *            amount to disburse
     * @return Post Loans Loan Id Response
     */
    public PostLoansLoanIdResponse disburseLoan(Long loanId, String date, Double amount) {
        return disburseLoan(loanId, new PostLoansLoanIdRequest().actualDisbursementDate(date).dateFormat(DATE_FORMAT)
                .transactionAmount(BigDecimal.valueOf(amount)).locale("en"));
    }

    public PostLoansLoanIdResponse disburseToSavingsLoan(String loanExternalId, PostLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions1(loanExternalId, request, "disburseToSavings"));
    }

    public PostLoansLoanIdResponse undoApprovalLoan(String loanExternalId, PostLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions1(loanExternalId, request, "undoapproval"));
    }

    public PostLoansLoanIdResponse undoDisbursalLoan(String loanExternalId, PostLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions1(loanExternalId, request, "undodisbursal"));
    }

    public PostLoansLoanIdResponse undoDisbursalLoan(Long loanId, PostLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions(loanId, request, "undodisbursal"));
    }

    public PostLoansLoanIdResponse undoLastDisbursalLoan(String loanExternalId, PostLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions1(loanExternalId, request, "undolastdisbursal"));
    }

    public PostLoansLoanIdResponse undoLastDisbursalLoan(Long loanId, PostLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions(loanId, request, "undolastdisbursal"));
    }

    public PostLoansLoanIdResponse assignLoanOfficerLoan(String loanExternalId, PostLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions1(loanExternalId, request, "assignloanofficer"));
    }

    public PostLoansLoanIdResponse unassignLoanOfficerLoan(String loanExternalId, PostLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions1(loanExternalId, request, "unassignloanofficer"));
    }

    public PostLoansLoanIdResponse recoverGuaranteesLoan(String loanExternalId, PostLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions1(loanExternalId, request, "recoverGuarantees"));
    }

    public PostLoansLoanIdResponse assignDelinquencyLoan(String loanExternalId, PostLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions1(loanExternalId, request, "assigndelinquency"));
    }

    public PostLoansLoanIdTransactionsResponse closeRescheduledLoan(String loanExternalId, PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request,
                "close-rescheduled"));
    }

    public PostLoansLoanIdTransactionsResponse closeRescheduledLoan(Long loanId, PostLoansLoanIdTransactionsRequest request) {
        return Calls
                .ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "close-rescheduled"));
    }

    public PostLoansLoanIdTransactionsResponse closeLoan(String loanExternalId, PostLoansLoanIdTransactionsRequest request) {
        return Calls
                .ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request, "close"));
    }

    public PostLoansLoanIdTransactionsResponse closeLoan(Long loanId, PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "close"));
    }

    public PostLoansLoanIdTransactionsResponse forecloseLoan(String loanExternalId, PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(
                FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request, "foreclosure"));
    }

    public PostLoansLoanIdTransactionsResponse forecloseLoan(Long loanId, PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "foreclosure"));
    }

    public PostLoansLoanIdTransactionsResponse chargeOffLoan(String loanExternalId, PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(
                FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request, "charge-off"));
    }

    public PostLoansLoanIdTransactionsResponse chargeOffLoan(Long loanId, PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "charge-off"));
    }

    public PostLoansLoanIdTransactionsResponse undoChargeOffLoan(String loanExternalId, PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request,
                "undo-charge-off"));
    }

    public PostLoansLoanIdTransactionsResponse undoChargeOffLoan(Long loanId, PostLoansLoanIdTransactionsRequest request) {
        return Calls
                .ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "undo-charge-off"));
    }

    @Deprecated(forRemoval = true)
    public LoanCapitalizedIncomeData fetchLoanCapitalizedIncomeData(Long loanId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCapitalizedIncome.fetchLoanCapitalizedIncomeData(loanId));
    }

    public List<CapitalizedIncomeDetails> fetchCapitalizedIncomeDetails(Long loanId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanCapitalizedIncome.fetchCapitalizedIncomeDetails(loanId));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static List<Integer> getLoanIdsByStatusId(RequestSpecification requestSpec, ResponseSpecification responseSpec,
            Integer statusId) {
        final String GET_LOAN_URL = "/fineract-provider/api/v1/internal/loan/status/" + statusId + "?" + Utils.TENANT_IDENTIFIER;
        log.info("---------------------------------GET LOANS BY STATUS---------------------------------------------");
        final String get = Utils.performServerGet(requestSpec, responseSpec, GET_LOAN_URL, null);
        return new Gson().fromJson(get, new TypeToken<ArrayList<Integer>>() {}.getType());
    }

    public static List<Long> getLoanIdsByStatusId(Integer statusId) {
        return Calls.ok(FineractClientHelper.getFineractClient().legacy.getLoansByStatus(statusId));
    }

    public PutLoanProductsProductIdResponse updateLoanProduct(Long id, PutLoanProductsProductIdRequest requestModifyLoan) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanProducts.updateLoanProduct(id, requestModifyLoan));
    }

    public PostLoanProductsResponse createLoanProduct(PostLoanProductsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanProducts.createLoanProduct(request));
    }

    public PostLoansLoanIdTransactionsResponse makeLoanDownPayment(String loanExternalId, PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(
                FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request, "downPayment"));
    }

    public PostLoansLoanIdTransactionsResponse makeLoanDownPayment(Long loanId, PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "downPayment"));
    }

    public PostLoansLoanIdTransactionsResponse makeLoanBuyDownFee(Long loanId, PostLoansLoanIdTransactionsRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction(loanId, request, "buyDownFee"));
    }

    public PostLoansLoanIdTransactionsResponse makeLoanBuyDownFee(Long loanId, String date, double amount) {
        return makeLoanBuyDownFee(loanId, new PostLoansLoanIdTransactionsRequest().dateFormat("dd MMMM yyyy").transactionDate(date)
                .locale("en").transactionAmount(amount));
    }

    public List<AdvancedPaymentData> getAdvancedPaymentAllocationRules(final Integer loanId) {
        return Calls.ok(FineractClientHelper.getFineractClient().legacy.getAdvancedPaymentAllocationRulesOfLoan(loanId.longValue()));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object disburseLoanWithTransactionAmountWithError(final String date, final Integer loanID, final String transactionAmount,
            final String jsonAttributeToGetBack) {
        return performLoanTransaction(createLoanOperationURL(DISBURSE_LOAN_COMMAND, loanID),
                getDisburseLoanAsJSON(date, transactionAmount, null), jsonAttributeToGetBack);
    }

    public PostLoansLoanIdTransactionsResponse writeOffLoanAccount(final String loanExternalId,
            final PostLoansLoanIdTransactionsRequest request) {
        return Calls
                .ok(FineractClientHelper.getFineractClient().loanTransactions.executeLoanTransaction1(loanExternalId, request, "writeoff"));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object addChargesForLoanWithError(final Integer loanId, final String request, final String jsonAttributeToGetBack) {
        log.info("--------------------------------- ADD CHARGES FOR LOAN --------------------------------");
        final String ADD_CHARGES_URL = LOAN_ACCOUNT_URL + "/" + loanId + "/charges?" + Utils.TENANT_IDENTIFIER;
        return Utils.performServerPost(requestSpec, responseSpec, ADD_CHARGES_URL, request, jsonAttributeToGetBack);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Object updateLoanProduct(final Long loanProductId, final String request) {
        final String UPDATE_LOAN_PRODUCT_URL = LOAN_PRODUCTS_URL + "/" + loanProductId + "?" + Utils.TENANT_IDENTIFIER;
        return Utils.performServerPut(requestSpec, responseSpec, UPDATE_LOAN_PRODUCT_URL, request, null);
    }

    public PostLoansLoanIdResponse undoApprovalForLoan(Long loanId, PostLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.stateTransitions(loanId, request, "undoapproval"));
    }

    public PutLoansLoanIdResponse modifyApplicationForLoan(final Long loanId, final String command, final PutLoansLoanIdRequest request) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.modifyLoanApplication(loanId, request, command));
    }

    public PostLoansResponse calculateRepaymentScheduleForApplyLoan(PostLoansRequest request, String command) {
        return Calls.ok(FineractClientHelper.getFineractClient().loans.calculateLoanScheduleOrSubmitLoanApplication(request, command));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Integer createLoanProduct(final String inMultiplesOf, final String digitsAfterDecimal, final String repaymentStrategy,
            final String accountingRule, final Account... accounts) {
        log.info("------------------------------CREATING NEW LOAN PRODUCT ---------------------------------------");
        final String loanProductJSON = new LoanProductTestBuilder().withPrincipal("10000000.00").withNumberOfRepayments("24")
                .withRepaymentAfterEvery("1").withRepaymentTypeAsMonth().withinterestRatePerPeriod("2")
                .withInterestRateFrequencyTypeAsMonths().withRepaymentStrategy(repaymentStrategy)
                .withAmortizationTypeAsEqualPrincipalPayment().withInterestTypeAsDecliningBalance()
                .currencyDetails(digitsAfterDecimal, inMultiplesOf).withAccounting(accountingRule, accounts).build(null);
        return getLoanProductId(loanProductJSON);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public Integer applyForLoanApplicationWithPaymentStrategyAndPastMonth(final Integer clientID, final Integer loanProductID,
            List<HashMap> charges, final String savingsId, String principal, final String repaymentStrategy, final String submittedOnDate,
            final String disbursementDate) {
        log.info("--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");

        final String loanApplicationJSON = new LoanApplicationTestBuilder().withPrincipal(principal).withLoanTermFrequency("6")
                .withLoanTermFrequencyAsMonths().withNumberOfRepayments("6").withRepaymentEveryAfter("1")
                .withRepaymentFrequencyTypeAsMonths().withInterestRatePerPeriod("2").withAmortizationTypeAsEqualInstallments()
                .withInterestTypeAsFlatBalance().withInterestCalculationPeriodTypeSameAsRepaymentPeriod()
                .withExpectedDisbursementDate(disbursementDate).withSubmittedOnDate(submittedOnDate)
                .withRepaymentStrategy(repaymentStrategy).withCharges(charges)
                .build(clientID.toString(), loanProductID.toString(), savingsId);
        return getLoanId(loanApplicationJSON);
    }

    public List<BuyDownFeeAmortizationDetails> fetchBuyDownFeeAmortizationDetails(Long loanId) {
        return Calls.ok(FineractClientHelper.getFineractClient().loanBuyDownFeesApi.retrieveLoanBuyDownFeeAmortizationDetails(loanId));
    }
}
