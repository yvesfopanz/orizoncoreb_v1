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
package org.apache.fineract.integrationtests;

import static org.apache.fineract.integrationtests.common.funds.FundsResourceHandler.createFund;
import static org.apache.fineract.integrationtests.common.loans.LoanProductTestBuilder.ADVANCED_PAYMENT_ALLOCATION_STRATEGY;
import static org.apache.fineract.integrationtests.common.loans.LoanProductTestBuilder.DEFAULT_STRATEGY;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.fineract.client.models.AdvancedPaymentData;
import org.apache.fineract.client.models.AllowAttributeOverrides;
import org.apache.fineract.client.models.ChargeRequest;
import org.apache.fineract.client.models.GetLoanPaymentChannelToFundSourceMappings;
import org.apache.fineract.client.models.GetLoansLoanIdResponse;
import org.apache.fineract.client.models.GetLoansLoanIdStatus;
import org.apache.fineract.client.models.LoanProductChargeData;
import org.apache.fineract.client.models.LoanProductChargeToGLAccountMapper;
import org.apache.fineract.client.models.PostChargesResponse;
import org.apache.fineract.client.models.PostClientsResponse;
import org.apache.fineract.client.models.PostLoanProductsRequest;
import org.apache.fineract.client.models.PostLoanProductsResponse;
import org.apache.fineract.client.models.PostLoansLoanIdChargesRequest;
import org.apache.fineract.client.models.PostLoansLoanIdChargesResponse;
import org.apache.fineract.client.models.PostLoansLoanIdRequest;
import org.apache.fineract.client.models.PostLoansRequest;
import org.apache.fineract.client.models.PostLoansResponse;
import org.apache.fineract.integrationtests.common.BusinessStepHelper;
import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.SchedulerJobHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.charges.ChargesHelper;
import org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper;
import org.apache.fineract.integrationtests.common.products.DelinquencyBucketsHelper;
import org.apache.fineract.integrationtests.inlinecob.InlineLoanCOBHelper;
import org.apache.fineract.portfolio.charge.domain.ChargeCalculationType;
import org.apache.fineract.portfolio.charge.domain.ChargePaymentMode;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoanTransactionAccrualActivityPostingTest extends BaseLoanIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(LoanTransactionAccrualActivityPostingTest.class);
    private static final String DATETIME_PATTERN = "dd MMMM yyyy";
    private static ResponseSpecification responseSpec;
    private static RequestSpecification requestSpec;
    private static LoanTransactionHelper loanTransactionHelper;
    private static PostClientsResponse client;
    private static ChargesHelper chargesHelper;
    private static InlineLoanCOBHelper inlineLoanCOBHelper;
    private static BusinessStepHelper businessStepHelper;
    private static SchedulerJobHelper schedulerJobHelper;

    @BeforeAll
    public static void setup() {
        Utils.initializeRESTAssured();
        requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        requestSpec.header("Fineract-Platform-TenantId", "default");
        responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        loanTransactionHelper = new LoanTransactionHelper(requestSpec, responseSpec);
        schedulerJobHelper = new SchedulerJobHelper(requestSpec);
        ClientHelper clientHelper = new ClientHelper(requestSpec, responseSpec);
        chargesHelper = new ChargesHelper();
        client = clientHelper.createClient(ClientHelper.defaultClientCreationRequest());
        inlineLoanCOBHelper = new InlineLoanCOBHelper(requestSpec, responseSpec);
        businessStepHelper = new BusinessStepHelper();
        // setup COB Business Steps to prevent test failing due other integration test configurations
        businessStepHelper.updateSteps("LOAN_CLOSE_OF_BUSINESS", "APPLY_CHARGE_TO_OVERDUE_LOANS", "LOAN_DELINQUENCY_CLASSIFICATION",
                "CHECK_LOAN_REPAYMENT_DUE", "CHECK_LOAN_REPAYMENT_OVERDUE", "UPDATE_LOAN_ARREARS_AGING", "ADD_PERIODIC_ACCRUAL_ENTRIES",
                "EXTERNAL_ASSET_OWNER_TRANSFER", "ACCRUAL_ACTIVITY_POSTING");
    }

    /**
     * Using Interest bearing Progressive Loan, Accrual Activity Posting, NO InterestRecalculation, 25% yearly interest
     * 6 repayment 450 USD principal.
     * <li>apply, approve and disburse backdated on 17 August 2024</li>
     * <li>repay 600 on 17 January 2025</li>
     * <li>verify Accrual and Accrual Activity transaction creation</li>
     * <li>verify that the loan become overpaid</li>
     * <li>reverse repayment on same day</li>
     * <li>verify transaction reversals</li>
     */
    @Test
    public void testInterestBearingProgressiveNoInterestRecalculationReopenDueReverseRepayment1() {
        runAt("17 January 2025", () -> {
            final PostLoanProductsResponse loanProductsResponse = loanProductHelper.createLoanProduct(create4IProgressive() //
                    .description("Interest bearing Progressive Loan USD, Accrual Activity Posting, NO InterestRecalculation") //
                    .enableAccrualActivityPosting(true) //
                    .daysInMonthType(DaysInMonthType.ACTUAL) //
                    .daysInYearType(DaysInYearType.ACTUAL) //
                    .isInterestRecalculationEnabled(false));//
            PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applyLP2ProgressiveLoanRequest(client.getClientId(),
                    loanProductsResponse.getResourceId(), "17 August 2024", 450.0, 25.0, 6, null));
            Long loanId = postLoansResponse.getLoanId();
            Assertions.assertNotNull(loanId);
            loanTransactionHelper.approveLoan(loanId, approveLoanRequest(450.0, "17 August 2024"));
            disburseLoan(loanId, BigDecimal.valueOf(450.0), "17 August 2024");
            verifyTransactions(loanId, //
                    transaction(450.0, "Disbursement", "17 August 2024") //
            );
            Long repaymentId = loanTransactionHelper.makeLoanRepayment("17 January 2025", 600.0f, loanId.intValue()).getResourceId();
            Assertions.assertNotNull(repaymentId);
            GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            verifyLoanStatus(loanDetails, GetLoansLoanIdStatus::getOverpaid);
            verifyTransactions(loanId, //
                    transaction(450.0, "Disbursement", "17 August 2024"), //
                    transaction(600.0, "Repayment", "17 January 2025"), //
                    transaction(33.52, "Accrual", "17 January 2025"), //
                    transaction(9.53, "Accrual Activity", "17 September 2024"), //
                    transaction(7.77, "Accrual Activity", "17 October 2024"), //
                    transaction(6.48, "Accrual Activity", "17 November 2024"), //
                    transaction(4.75, "Accrual Activity", "17 December 2024"), //
                    transaction(4.99, "Accrual Activity", "17 January 2025")); //
            loanTransactionHelper.reverseRepayment(loanId.intValue(), repaymentId.intValue(), "17 January 2025");
            loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            verifyLoanStatus(loanDetails, GetLoansLoanIdStatus::getActive);
            verifyTransactions(loanId, transaction(450.0, "Disbursement", "17 August 2024"), //
                    transaction(33.52, "Accrual", "17 January 2025"), //
                    reversedTransaction(600.0, "Repayment", "17 January 2025"), //
                    transaction(9.53, "Accrual Activity", "17 September 2024"), //
                    transaction(7.77, "Accrual Activity", "17 October 2024"), //
                    transaction(6.48, "Accrual Activity", "17 November 2024"), //
                    transaction(4.75, "Accrual Activity", "17 December 2024")); //
        });
    }

    /**
     * Using Interest bearing Progressive Loan, Accrual Activity Posting, InterestRecalculation, 25% yearly interest 6
     * repayment 450 USD principal.
     * <li>apply, approve and disburse backdated on 17 August 2024</li>
     * <li>repay 600 on 17 January 2025</li>
     * <li>verify Accrual and Accrual Activity transaction creation</li>
     * <li>verify that the loan become overpaid</li>
     * <li>reverse repayment on same day</li>
     * <li>verify transaction reversals</li>
     */
    @Test
    public void testInterestBearingProgressiveInterestRecalculationReopenDueReverseRepayment() {
        runAt("17 January 2025", () -> {
            final PostLoanProductsResponse loanProductsResponse = loanProductHelper.createLoanProduct(create4IProgressive() //
                    .description("Interest bearing Progressive Loan USD, Accrual Activity Posting, NO InterestRecalculation") //
                    .enableAccrualActivityPosting(true) //
                    .daysInMonthType(DaysInMonthType.ACTUAL) //
                    .daysInYearType(DaysInYearType.ACTUAL) //
                    .isInterestRecalculationEnabled(false));//
            PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applyLP2ProgressiveLoanRequest(client.getClientId(),
                    loanProductsResponse.getResourceId(), "17 August 2024", 450.0, 25.0, 6, null));
            Long loanId = postLoansResponse.getLoanId();
            Assertions.assertNotNull(loanId);
            loanTransactionHelper.approveLoan(loanId, approveLoanRequest(450.0, "17 August 2024"));
            disburseLoan(loanId, BigDecimal.valueOf(450.0), "17 August 2024");
            verifyTransactions(loanId, //
                    transaction(450.0, "Disbursement", "17 August 2024") //
            );
            Long repaymentId = loanTransactionHelper.makeLoanRepayment("17 January 2025", 600.0f, loanId.intValue()).getResourceId();
            Assertions.assertNotNull(repaymentId);
            GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            verifyLoanStatus(loanDetails, GetLoansLoanIdStatus::getOverpaid);
            verifyTransactions(loanId, //
                    transaction(450.0, "Disbursement", "17 August 2024"), //
                    transaction(600.0, "Repayment", "17 January 2025"), //
                    transaction(33.52, "Accrual", "17 January 2025"), //
                    transaction(9.53, "Accrual Activity", "17 September 2024"), //
                    transaction(7.77, "Accrual Activity", "17 October 2024"), //
                    transaction(6.48, "Accrual Activity", "17 November 2024"), //
                    transaction(4.75, "Accrual Activity", "17 December 2024"), //
                    transaction(4.99, "Accrual Activity", "17 January 2025")); //
            loanTransactionHelper.reverseRepayment(loanId.intValue(), repaymentId.intValue(), "17 January 2025");

            loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            verifyLoanStatus(loanDetails, GetLoansLoanIdStatus::getActive);
            verifyTransactions(loanId, transaction(450.0, "Disbursement", "17 August 2024"), //
                    transaction(33.52, "Accrual", "17 January 2025"), //
                    reversedTransaction(600.0, "Repayment", "17 January 2025"), //
                    transaction(9.53, "Accrual Activity", "17 September 2024"), //
                    transaction(7.77, "Accrual Activity", "17 October 2024"), //
                    transaction(6.48, "Accrual Activity", "17 November 2024"), //
                    transaction(4.75, "Accrual Activity", "17 December 2024")); //
        });
    }

    /**
     * Using Interest bearing Progressive Loan, Accrual Activity Posting, NO InterestRecalculation, 25% yearly interest
     * 6 repayment 450 USD principal.
     * <li>apply, approve and disburse backdated on 17 August 2024</li>
     * <li>repay 600 on 17 January 2025</li>
     * <li>verify Accrual and Accrual Activity transaction creation</li>
     * <li>verify that the loan become overpaid</li>
     * <li>reverse repayment on same day</li>
     * <li>verify transaction reversals</li>
     */
    @Test
    public void testInterestBearingProgressiveNoInterestRecalculationReopenDueReverseRepayment1b() {
        runAt("18 January 2025", () -> {
            final PostLoanProductsResponse loanProductsResponse = loanProductHelper.createLoanProduct(create4IProgressive() //
                    .description("Interest bearing Progressive Loan USD, Accrual Activity Posting, NO InterestRecalculation") //
                    .enableAccrualActivityPosting(true) //
                    .daysInMonthType(DaysInMonthType.ACTUAL) //
                    .daysInYearType(DaysInYearType.ACTUAL) //
                    .isInterestRecalculationEnabled(false));//
            PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applyLP2ProgressiveLoanRequest(client.getClientId(),
                    loanProductsResponse.getResourceId(), "17 August 2024", 450.0, 25.0, 6, null));
            Long loanId = postLoansResponse.getLoanId();
            Assertions.assertNotNull(loanId);
            loanTransactionHelper.approveLoan(loanId, approveLoanRequest(450.0, "17 August 2024"));
            disburseLoan(loanId, BigDecimal.valueOf(450.0), "17 August 2024");
            verifyTransactions(loanId, //
                    transaction(450.0, "Disbursement", "17 August 2024") //
            );
            Long repaymentId = loanTransactionHelper.makeLoanRepayment("17 January 2025", 600.0f, loanId.intValue()).getResourceId();
            Assertions.assertNotNull(repaymentId);
            GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            verifyLoanStatus(loanDetails, GetLoansLoanIdStatus::getOverpaid);
            verifyTransactions(loanId, //
                    transaction(450.0, "Disbursement", "17 August 2024"), //
                    transaction(600.0, "Repayment", "17 January 2025"), //
                    transaction(33.52, "Accrual", "18 January 2025"), //
                    transaction(9.53, "Accrual Activity", "17 September 2024"), //
                    transaction(7.77, "Accrual Activity", "17 October 2024"), //
                    transaction(6.48, "Accrual Activity", "17 November 2024"), //
                    transaction(4.75, "Accrual Activity", "17 December 2024"), //
                    transaction(4.99, "Accrual Activity", "17 January 2025")); //
            loanTransactionHelper.reverseRepayment(loanId.intValue(), repaymentId.intValue(), "17 January 2025");
            loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            verifyLoanStatus(loanDetails, GetLoansLoanIdStatus::getActive);
            verifyTransactions(loanId, transaction(450.0, "Disbursement", "17 August 2024"), //
                    transaction(33.52, "Accrual", "18 January 2025"), //
                    reversedTransaction(600.0, "Repayment", "17 January 2025"), //
                    transaction(9.53, "Accrual Activity", "17 September 2024"), //
                    transaction(7.77, "Accrual Activity", "17 October 2024"), //
                    transaction(6.48, "Accrual Activity", "17 November 2024"), //
                    transaction(4.75, "Accrual Activity", "17 December 2024"), //
                    transaction(3.31, "Accrual Activity", "17 January 2025")); //
        });
    }

    @Test
    public void testAccrualActivityPostingAndReversalsInterestBearingProgressiveInterestRecalculationMerchantIssuedRefund() {
        runAt("17 January 2025", () -> {
            final PostLoanProductsResponse loanProductsResponse = loanProductHelper.createLoanProduct(create4IProgressive() //
                    .description("Interest bearing Progressive Loan USD, Accrual Activity Posting, InterestRecalculation") //
                    .enableAccrualActivityPosting(true) //
                    .currencyCode("USD") //
                    .daysInMonthType(DaysInMonthType.ACTUAL) //
                    .daysInYearType(DaysInYearType.ACTUAL) //
                    .isInterestRecalculationEnabled(true));//
            PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applyLP2ProgressiveLoanRequest(client.getClientId(),
                    loanProductsResponse.getResourceId(), "17 August 2024", 450.0, 25.0, 6, null));
            Long loanId = postLoansResponse.getLoanId();
            Assertions.assertNotNull(loanId);
            loanTransactionHelper.approveLoan(loanId, approveLoanRequest(450.0, "17 August 2024"));
            disburseLoan(loanId, BigDecimal.valueOf(450.0), "17 August 2024");
            verifyTransactions(loanId, //
                    transaction(450.0, "Disbursement", "17 August 2024") //
            );
            Long repaymentId = loanTransactionHelper.makeLoanRepayment("17 January 2025", 497.04f, loanId.intValue()).getResourceId();
            Assertions.assertNotNull(repaymentId);
            GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            verifyLoanStatus(loanDetails, GetLoansLoanIdStatus::getClosedObligationsMet);
            verifyTransactions(loanId, //
                    transaction(450.0, "Disbursement", "17 August 2024"), //
                    transaction(497.04, "Repayment", "17 January 2025"), //
                    transaction(47.04, "Accrual", "17 January 2025"), //
                    transaction(9.53, "Accrual Activity", "17 September 2024"), //
                    transaction(9.22, "Accrual Activity", "17 October 2024"), //
                    transaction(9.53, "Accrual Activity", "17 November 2024"), //
                    transaction(9.22, "Accrual Activity", "17 December 2024"), //
                    transaction(9.54, "Accrual Activity", "17 January 2025")); //
            loanTransactionHelper.makeLoanRepayment("MerchantIssuedRefund", "17 August 2024", 450.0f, loanId.intValue()).getResourceId();
            loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            verifyLoanStatus(loanDetails, GetLoansLoanIdStatus::getOverpaid);
            verifyTransactions(loanId, transaction(450.0, "Disbursement", "17 August 2024"), //
                    transaction(450.0, "Merchant Issued Refund", "17 August 2024"), //
                    transaction(497.04, "Repayment", "17 January 2025"), //
                    transaction(47.04, "Accrual", "17 January 2025"), //
                    transaction(47.04, "Accrual Adjustment", "17 January 2025")); //
        });
    }

    /**
     * Using Interest bearing Progressive Loan, Accrual Activity Posting, NO InterestRecalculation, 25% yearly interest
     * 6 repayment 450 USD principal.
     * <li>apply, approve and disburse backdated on 17 August 2024</li>
     * <li>repay 600 on 17 January 2025</li>
     * <li>verify Accrual and Accrual Activity transaction creation</li>
     * <li>verify that the loan become overpaid</li>
     * <li>reverse repayment on same day verify transaction reversals</li>
     */
    @Test
    public void testInterestBearingProgressiveNoInterestRecalculationReopenDueReverseRepayment2b() {
        runAt("17 January 2025", () -> {
            final PostLoanProductsResponse loanProductsResponse = loanProductHelper.createLoanProduct(create4IProgressive() //
                    .description("Interest bearing Progressive Loan USD, Accrual Activity Posting, NO InterestRecalculation") //
                    .enableAccrualActivityPosting(true) //
                    .currencyCode("USD") //
                    .daysInMonthType(DaysInMonthType.ACTUAL) //
                    .daysInYearType(DaysInYearType.ACTUAL) //
                    .isInterestRecalculationEnabled(false));//
            PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applyLP2ProgressiveLoanRequest(client.getClientId(),
                    loanProductsResponse.getResourceId(), "17 August 2024", 450.0, 25.0, 6, null));
            Long loanId = postLoansResponse.getLoanId();
            Assertions.assertNotNull(loanId);
            loanTransactionHelper.approveLoan(loanId, approveLoanRequest(450.0, "17 August 2024"));
            disburseLoan(loanId, BigDecimal.valueOf(450.0), "17 August 2024");
            verifyTransactions(loanId, //
                    transaction(450.0, "Disbursement", "17 August 2024") //
            );
            Long repaymentId = loanTransactionHelper.makeLoanRepayment("17 January 2025", 483.52f, loanId.intValue()).getResourceId();
            Assertions.assertNotNull(repaymentId);
            GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            verifyLoanStatus(loanDetails, GetLoansLoanIdStatus::getClosedObligationsMet);
            verifyTransactions(loanId, //
                    transaction(450.0, "Disbursement", "17 August 2024"), //
                    transaction(483.52, "Repayment", "17 January 2025"), //
                    transaction(33.52, "Accrual", "17 January 2025"), //
                    transaction(9.53, "Accrual Activity", "17 September 2024"), //
                    transaction(7.77, "Accrual Activity", "17 October 2024"), //
                    transaction(6.48, "Accrual Activity", "17 November 2024"), //
                    transaction(4.75, "Accrual Activity", "17 December 2024"), //
                    transaction(4.99, "Accrual Activity", "17 January 2025")); //
            addCharge(loanId, false, 15.0, "15 January 2025");
            loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            verifyLoanStatus(loanDetails, GetLoansLoanIdStatus::getActive);
            verifyTransactions(loanId, transaction(450.0, "Disbursement", "17 August 2024"), //
                    transaction(33.52, "Accrual", "17 January 2025"), //
                    transaction(483.52, "Repayment", "17 January 2025"), //
                    transaction(9.53, "Accrual Activity", "17 September 2024"), //
                    transaction(7.77, "Accrual Activity", "17 October 2024"), //
                    transaction(6.48, "Accrual Activity", "17 November 2024"), //
                    transaction(4.75, "Accrual Activity", "17 December 2024")); //
        });
    }

    /**
     * Using Interest bearing Progressive Loan, Accrual Activity Posting, NO InterestRecalculation, 25% yearly interest
     * 6 repayment 450 USD principal.
     * <li>apply, approve and disburse backdated on 17 August 2024</li>
     * <li>repay 600 on 17 January 2025</li>
     * <li>verify Accrual and Accrual Activity transaction creation</li>
     * <li>verify that the loan become overpaid</li>
     * <li>reverse repayment on same day verify transaction reversals</li>
     */
    @Test
    public void testInterestBearingProgressiveNoInterestRecalculationReopenDueReverseRepayment2c() {
        runAt("18 January 2025", () -> {
            final PostLoanProductsResponse loanProductsResponse = loanProductHelper.createLoanProduct(create4IProgressive() //
                    .description("Interest bearing Progressive Loan USD, Accrual Activity Posting, NO InterestRecalculation") //
                    .enableAccrualActivityPosting(true) //
                    .currencyCode("USD") //
                    .daysInMonthType(DaysInMonthType.ACTUAL) //
                    .daysInYearType(DaysInYearType.ACTUAL) //
                    .isInterestRecalculationEnabled(false));//
            PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applyLP2ProgressiveLoanRequest(client.getClientId(),
                    loanProductsResponse.getResourceId(), "17 August 2024", 450.0, 25.0, 6, null));
            Long loanId = postLoansResponse.getLoanId();
            Assertions.assertNotNull(loanId);
            loanTransactionHelper.approveLoan(loanId, approveLoanRequest(450.0, "17 August 2024"));
            disburseLoan(loanId, BigDecimal.valueOf(450.0), "17 August 2024");
            verifyTransactions(loanId, //
                    transaction(450.0, "Disbursement", "17 August 2024") //
            );
            Long repaymentId = loanTransactionHelper.makeLoanRepayment("17 January 2025", 483.52f, loanId.intValue()).getResourceId();
            Assertions.assertNotNull(repaymentId);
            GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            verifyLoanStatus(loanDetails, GetLoansLoanIdStatus::getClosedObligationsMet);
            verifyTransactions(loanId, //
                    transaction(450.0, "Disbursement", "17 August 2024"), //
                    transaction(483.52, "Repayment", "17 January 2025"), //
                    transaction(33.52, "Accrual", "18 January 2025"), //
                    transaction(9.53, "Accrual Activity", "17 September 2024"), //
                    transaction(7.77, "Accrual Activity", "17 October 2024"), //
                    transaction(6.48, "Accrual Activity", "17 November 2024"), //
                    transaction(4.75, "Accrual Activity", "17 December 2024"), //
                    transaction(4.99, "Accrual Activity", "17 January 2025")); //
            addCharge(loanId, false, 15.0, "15 January 2025");
            loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            verifyLoanStatus(loanDetails, GetLoansLoanIdStatus::getActive);
            verifyTransactions(loanId, transaction(450.0, "Disbursement", "17 August 2024"), //
                    transaction(33.52, "Accrual", "18 January 2025"), //
                    transaction(483.52, "Repayment", "17 January 2025"), //
                    transaction(9.53, "Accrual Activity", "17 September 2024"), //
                    transaction(7.77, "Accrual Activity", "17 October 2024"), //
                    transaction(6.48, "Accrual Activity", "17 November 2024"), //
                    transaction(4.75, "Accrual Activity", "17 December 2024"), //
                    transaction(18.31, "Accrual Activity", "17 January 2025")); //
        });
    }

    // Create Loan with Interest and enabled Accrual Activity Posting
    // Approve and disburse loan
    // charge penalty with due date as 1st installment
    // charge fee with due date as 1st installment
    // charge penalty with due date as 3rd installment
    // charge fee with due date as 2nd installment
    // set business day to the day before closing day of 1st installment, run COB for loan, verify no Accrual Activity
    // posted
    // set business day to the closing day of 1st installment, run COB for loan, verify Accrual Activity posted
    // set business day to the day after closing day of 1st installment, run COB for loan, verify no Accrual Activity
    // posted
    @Test
    public void testAccrualActivityPosting() {
        final String disbursementDay = "01 January 2023";
        final String repaymentPeriod1DueDate = "01 February 2023";
        final String repaymentPeriod1CloseDate = "02 February 2023";
        final String repaymentPeriod1OneDayBeforeCloseDate = "01 February 2023";
        final String repaymentPeriod1OneDayAfterCloseDate = "03 February 2023";
        final String repaymentPeriod2DueDate = "01 March 2023";
        final String repaymentPeriod3DueDate = "01 April 2023";
        final String creationBusinessDay = "15 January 2023";
        AtomicReference<Long> loanId = new AtomicReference<>();
        runAt(creationBusinessDay, () -> {

            Long localLoanProductId = createLoanProductAccountingAccrualPeriodicWithInterest();
            loanId.set(applyForLoanApplicationWithInterest(client.getClientId(), localLoanProductId, BigDecimal.valueOf(40000),
                    disbursementDay));

            loanTransactionHelper.approveLoan(loanId.get(), new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(1000))
                    .dateFormat(DATETIME_PATTERN).approvedOnDate(disbursementDay).locale("en"));

            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(1000.0)).locale("en"));

            chargePenalty(loanId.get(), 30.0, repaymentPeriod1DueDate);
            chargePenalty(loanId.get(), 50.0, repaymentPeriod2DueDate);
            chargeFee(loanId.get(), 40.0, repaymentPeriod1DueDate);
            chargeFee(loanId.get(), 60.0, repaymentPeriod3DueDate);

        });
        runAt(repaymentPeriod1OneDayBeforeCloseDate, () -> {
            inlineLoanCOBHelper.executeInlineCOB(List.of(loanId.get()));
            verifyTransactions(loanId.get(), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    transaction(19.35, "Accrual", "31 January 2023", 0, 0, 19.35, 0, 0, 0.0, 0.0));
        });
        runAt(repaymentPeriod1CloseDate, () -> {
            inlineLoanCOBHelper.executeInlineCOB(List.of(loanId.get()));
            verifyTransactions(loanId.get(), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    transaction(19.35, "Accrual", "31 January 2023", 0, 0, 19.35, 0, 0, 0.0, 0.0),
                    transaction(70.65, "Accrual", "01 February 2023", 0, 0, 0.65, 40, 30, 0.0, 0.0),
                    transaction(90.0, "Accrual Activity", "01 February 2023", 0, 0, 20.0, 40.0, 30.0, 0.0, 0.0));

        });
        runAt(repaymentPeriod1OneDayAfterCloseDate, () -> {
            inlineLoanCOBHelper.executeInlineCOB(List.of(loanId.get()));
            verifyTransactions(loanId.get(), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    transaction(19.35, "Accrual", "31 January 2023", 0, 0, 19.35, 0, 0, 0.0, 0.0),
                    transaction(70.65, "Accrual", "01 February 2023", 0, 0, 0.65, 40, 30, 0.0, 0.0),
                    transaction(90.0, "Accrual Activity", "01 February 2023", 0, 0, 20.0, 40.0, 30.0, 0.0, 0.0),
                    transaction(0.71, "Accrual", "02 February 2023", 0, 0, 0.71, 0, 0, 0.0, 0.0));

        });
    }

    // Create Loan with Interest and enabled Accrual Activity Posting
    // Approve and disburse loan
    // charge penalty with due date as 1st installment
    // charge fee with due date as 1st installment
    // charge penalty with due date as 3rd installment
    // charge fee with due date as 2nd installment
    // set business day to the day before closing day of 1st installment, run "Accrual Activity Posting" Job, verify no
    // Accrual Activity
    // posted
    // set business day to the closing day of 1st installment, run "Accrual Activity Posting" Job, verify Accrual
    // Activity posted
    // set business day to the day after closing day of 1st installment, run "Accrual Activity Posting" Job, verify no
    // Accrual Activity
    // posted
    @Test
    public void testAccrualActivityPostingJob() {
        final String disbursementDay = "01 January 2023";
        final String repaymentPeriod1DueDate = "01 February 2023";
        final String repaymentPeriod1CloseDate = "02 February 2023";
        final String repaymentPeriod1OneDayBeforeCloseDate = "01 February 2023";
        final String repaymentPeriod1OneDayAfterCloseDate = "03 February 2023";
        final String repaymentPeriod2DueDate = "01 March 2023";
        final String repaymentPeriod3DueDate = "01 April 2023";
        final String creationBusinessDay = "15 January 2023";
        AtomicReference<Long> loanId = new AtomicReference<>();
        runAt(creationBusinessDay, () -> {

            Long localLoanProductId = createLoanProductAccountingAccrualPeriodicWithInterest();
            loanId.set(applyForLoanApplicationWithInterest(client.getClientId(), localLoanProductId, BigDecimal.valueOf(40000),
                    disbursementDay));

            loanTransactionHelper.approveLoan(loanId.get(), new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(1000))
                    .dateFormat(DATETIME_PATTERN).approvedOnDate(disbursementDay).locale("en"));

            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(1000.0)).locale("en"));

            chargePenalty(loanId.get(), 30.0, repaymentPeriod1DueDate);
            chargePenalty(loanId.get(), 50.0, repaymentPeriod2DueDate);
            chargeFee(loanId.get(), 40.0, repaymentPeriod1DueDate);
            chargeFee(loanId.get(), 60.0, repaymentPeriod3DueDate);

        });
        runAt(repaymentPeriod1OneDayBeforeCloseDate, () -> {
            schedulerJobHelper.executeAndAwaitJob("Accrual Activity Posting");
            verifyTransactions(loanId.get(), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
        });
        runAt(repaymentPeriod1CloseDate, () -> {
            schedulerJobHelper.executeAndAwaitJob("Accrual Activity Posting");

            verifyTransactions(loanId.get(), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    transaction(90.0, "Accrual Activity", "01 February 2023", 0, 0, 20.0, 40.0, 30.0, 0.0, 0.0));
        });
        runAt(repaymentPeriod1OneDayAfterCloseDate, () -> {
            schedulerJobHelper.executeAndAwaitJob("Accrual Activity Posting");
            verifyTransactions(loanId.get(), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    transaction(90.0, "Accrual Activity", "01 February 2023", 0, 0, 20.0, 40.0, 30.0, 0.0, 0.0));
        });
    }

    // Create Loan with Interest and enabled Accrual Activity Posting
    // Approve and disburse loan
    // charge penalty with due date as 1st installment
    // charge fee with due date as 1st installment
    // charge penalty with due date as 3rd installment
    // charge fee with due date as 2nd installment
    // set business day to the day before closing day of 1st installment, run "Accrual Activity Posting" Job, verify no
    // Accrual Activity
    // posted
    // set business day to the closing day of 1st installment, run "Accrual Activity Posting" Job, verify Accrual
    // Activity posted
    // set business day to the day after closing day of 1st installment, run "Accrual Activity Posting" Job, verify no
    // Accrual Activity
    // posted
    @Test
    public void testAccrualActivityPostingJobForMultipleLoans() {
        final String disbursementDay = "01 January 2023";
        final String repaymentPeriod1DueDate = "01 February 2023";
        final String repaymentPeriod1CloseDate = "02 February 2023";
        final String creationBusinessDay = "15 January 2023";
        AtomicReference<Long> loanId1 = new AtomicReference<>();
        AtomicReference<Long> loanId2 = new AtomicReference<>();
        runAt(creationBusinessDay, () -> {

            Long localLoanProductId = createLoanProductAccountingAccrualPeriodicWithInterest();

            loanId1.set(applyForLoanApplicationWithInterest(client.getClientId(), localLoanProductId, BigDecimal.valueOf(40000),
                    disbursementDay));
            loanId2.set(applyForLoanApplicationWithInterest(client.getClientId(), localLoanProductId, BigDecimal.valueOf(40000),
                    disbursementDay));

            loanTransactionHelper.approveLoan(loanId1.get(), new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(1000))
                    .dateFormat(DATETIME_PATTERN).approvedOnDate(disbursementDay).locale("en"));
            loanTransactionHelper.approveLoan(loanId2.get(), new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(1000))
                    .dateFormat(DATETIME_PATTERN).approvedOnDate(disbursementDay).locale("en"));

            loanTransactionHelper.disburseLoan(loanId1.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(1000.0)).locale("en"));
            loanTransactionHelper.disburseLoan(loanId2.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(1000.0)).locale("en"));

            chargePenalty(loanId1.get(), 30.0, repaymentPeriod1DueDate);
            chargeFee(loanId1.get(), 40.0, repaymentPeriod1DueDate);

            chargePenalty(loanId2.get(), 30.0, repaymentPeriod1DueDate);
            chargeFee(loanId2.get(), 40.0, repaymentPeriod1DueDate);

        });
        runAt(repaymentPeriod1CloseDate, () -> {
            schedulerJobHelper.executeAndAwaitJob("Accrual Activity Posting");

            verifyTransactions(loanId1.get(), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    transaction(90.0, "Accrual Activity", "01 February 2023", 0, 0, 20.0, 40.0, 30.0, 0.0, 0.0));
            verifyTransactions(loanId2.get(), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    transaction(90.0, "Accrual Activity", "01 February 2023", 0, 0, 20.0, 40.0, 30.0, 0.0, 0.0));
        });
    }

    // Create Loan with Interest and enabled Accrual Activity Posting
    // Approve and disburse loan
    // make partial repayment before first installment day
    // run COB on closing day of first installment
    // verify that the Accrual Activity transaction is created
    // make repayment before the first repayment
    // verify that Accrual Activity transaction is NOT modified/reversed/replayed
    // charge backdated penalty before first installment due date
    // verify that the Accrual Activity transaction is reverse replayed
    // verify that the Accrual Activity holds the correct portions
    // charge backdated penalty before first installment due date
    // verify that the Accrual Activity transaction is reverse replayed
    // verify that the Accrual Activity holds the correct portions

    @Test
    public void testAccrualActivityPostingReverseReplay() {
        final String disbursementDay = "01 January 2023";
        final String repaymentPeriod1CloseDate = "02 February 2023";
        final String repaymentPeriod1OneDayAfterCloseDate = "03 February 2023";
        final String creationBusinessDay = "15 January 2023";
        AtomicReference<Long> loanId = new AtomicReference<>();
        runAt(creationBusinessDay, () -> {

            Long localLoanProductId = createLoanProductAccountingAccrualPeriodicWithInterest();
            loanId.set(applyForLoanApplicationWithInterest(client.getClientId(), localLoanProductId, BigDecimal.valueOf(40000),
                    disbursementDay));

            loanTransactionHelper.approveLoan(loanId.get(), new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(1000))
                    .dateFormat(DATETIME_PATTERN).approvedOnDate(disbursementDay).locale("en"));

            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(1000.0)).locale("en"));

            addRepaymentForLoan(loanId.get(), 50.0, "10 January 2023");
            verifyTransactions(loanId.get(), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    transaction(50.0, "Repayment", "10 January 2023", 970, 30, 20, 0, 0, 0.0, 0.0));

        });
        runAt(repaymentPeriod1CloseDate, () -> {
            inlineLoanCOBHelper.executeInlineCOB(List.of(loanId.get()));
            verifyTransactions(loanId.get(), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    transaction(20.0, "Accrual", "01 February 2023", 0, 0, 20, 0, 0, 0.0, 0.0),
                    transaction(50.0, "Repayment", "10 January 2023", 970, 30, 20, 0, 0, 0.0, 0.0),
                    transaction(20.0, "Accrual Activity", "01 February 2023", 0, 0, 20.0, 0.0, 0.0, 0.0, 0.0));

        });
        runAt(repaymentPeriod1OneDayAfterCloseDate, () -> {

            addRepaymentForLoan(loanId.get(), 200.0, "8 January 2023");

            verifyTransactions(loanId.get(), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    transaction(20.0, "Accrual", "01 February 2023", 0, 0, 20, 0, 0, 0.0, 0.0),
                    transaction(50.0, "Repayment", "10 January 2023", 770, 50, 0, 0, 0, 0.0, 0.0),
                    transaction(200.0, "Repayment", "08 January 2023", 820, 180, 20, 0, 0, 0.0, 0.0),
                    transaction(20.0, "Accrual Activity", "01 February 2023", 0, 0, 20.0, 0.0, 0.0, 0.0, 0.0));

            chargePenalty(loanId.get(), 33.0, "01 February 2023");

            verifyTransactions(loanId.get(), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    transaction(20.0, "Accrual", "01 February 2023", 0, 0, 20, 0, 0, 0.0, 0.0),
                    transaction(50.0, "Repayment", "10 January 2023", 803, 50, 0, 0, 0, 0.0, 0.0),
                    transaction(200.0, "Repayment", "08 January 2023", 853, 147, 20, 0, 33, 0.0, 0.0),
                    transaction(53.0, "Accrual Activity", "01 February 2023", 0, 0, 20.0, 0.0, 33.0, 0.0, 0.0));

            chargeFee(loanId.get(), 12.0, "01 February 2023");

            verifyTransactions(loanId.get(), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    transaction(20.0, "Accrual", "01 February 2023", 0, 0, 20, 0, 0, 0.0, 0.0),
                    transaction(50.0, "Repayment", "10 January 2023", 815, 50, 0, 0, 0, 0.0, 0.0),
                    transaction(200.0, "Repayment", "08 January 2023", 865, 135, 20, 12, 33, 0.0, 0.0),
                    transaction(65.0, "Accrual Activity", "01 February 2023", 0, 0, 20.0, 12.0, 33.0, 0.0, 0.0));

        });
    }

    // Create Loan with Advanced Payment Allocation and enabled Accrual Activity Posting
    // Approve and disburse loan
    // charge penalty for 1st installment
    // make partial repayment before first installment day
    // run COB on closing day of first installment
    // verify that the Accrual Activity transaction is created
    // make repayment before the first repayment
    // verify that Accrual Activity transaction is NOT modified/reversed/replayed
    // charge backdated penalty before first installment due date
    // verify that the Accrual Activity transaction is reverse replayed
    // verify that the Accrual Activity holds the correct portions
    // charge backdated penalty before first installment due date
    // verify that the Accrual Activity transaction is reverse replayed
    // verify that the Accrual Activity holds the correct portions
    @ParameterizedTest
    @CsvSource({ "29 January 2023,30 January 2023,31 January 2023", "31 January 2023,30 January 2023,29 January 2023",
            "31 January 2023,31 January 2023,31 January 2023", "01 February 2023,01 February 2023,01 February 2023" })
    public void testAccrualActivityPostingReverseReplayAdvancedPaymentAllocation(final String chargeDueDate1st,
            final String chargeDueDate2st, final String chargeDueDate3st) {
        final String disbursementDay = "01 January 2023";
        final String repaymentPeriod1CloseDate = "02 February 2023";
        final String repaymentPeriod1OneDayAfterCloseDate = "03 February 2023";
        final String creationBusinessDay = "15 January 2023";
        AtomicReference<Long> loanId = new AtomicReference<>();
        runAt(creationBusinessDay, () -> {
            Long localLoanProductId = createLoanProductAccountingAccrualPeriodicAdvancedPaymentAllocation();
            loanId.set(applyForLoanApplicationAdvancedPaymentAllocation(client.getClientId(), localLoanProductId, BigDecimal.valueOf(40000),
                    disbursementDay, BigDecimal.ZERO));

            loanTransactionHelper.approveLoan(loanId.get(), new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(1000))
                    .dateFormat(DATETIME_PATTERN).approvedOnDate(disbursementDay).locale("en"));

            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(1000)).locale("en"));

            chargePenalty(loanId.get(), 20.0, chargeDueDate1st);

            addRepaymentForLoan(loanId.get(), 50.0, "10 January 2023");
            verifyTransactions(loanId.get(), //
                    transaction(1000, "Disbursement", disbursementDay, 1000, 0, 0, 0, 0, 0, 0),
                    transaction(50, "Repayment", "10 January 2023", 950, 50, 0, 0, 0, 0, 0));

        });
        runAt(repaymentPeriod1CloseDate, () -> {
            inlineLoanCOBHelper.executeInlineCOB(List.of(loanId.get()));
            verifyTransactions(loanId.get(), //
                    transaction(1000, "Disbursement", disbursementDay, 1000, 0, 0, 0, 0, 0, 0),
                    transaction(20, "Accrual", "01 February 2023", 0, 0, 0, 0, 20, 0, 0),
                    transaction(50, "Repayment", "10 January 2023", 950, 50, 0, 0, 0, 0, 0),
                    transaction(20, "Accrual Activity", "01 February 2023", 0, 0, 0, 0, 20, 0, 0));

        });
        runAt(repaymentPeriod1OneDayAfterCloseDate, () -> {
            addRepaymentForLoan(loanId.get(), 220.0, "8 January 2023");

            verifyTransactions(loanId.get(), //
                    transaction(1000, "Disbursement", disbursementDay, 1000, 0, 0, 0, 0, 0, 0),
                    transaction(20, "Accrual", "01 February 2023", 0, 0, 0, 0, 20, 0, 0),
                    transaction(50, "Repayment", "10 January 2023", 730, 50, 0, 0, 0, 0, 0),
                    transaction(220, "Repayment", "08 January 2023", 780, 220, 0, 0, 0, 0, 0),
                    transaction(20, "Accrual Activity", "01 February 2023", 0, 0, 0, 0, 20, 0, 0));

            chargePenalty(loanId.get(), 33.0, chargeDueDate2st);

            verifyTransactions(loanId.get(), //
                    transaction(1000, "Disbursement", disbursementDay, 1000, 0, 0, 0, 0, 0, 0),
                    transaction(20, "Accrual", "01 February 2023", 0, 0, 0, 0, 20, 0, 0),
                    transaction(50, "Repayment", "10 January 2023", 730, 50, 0, 0, 0, 0, 0),
                    transaction(220, "Repayment", "08 January 2023", 780, 220, 0, 0, 0, 0, 0),
                    transaction(53, "Accrual Activity", "01 February 2023", 0, 0, 0, 0, 53, 0, 0));

            chargeFee(loanId.get(), 12.0, chargeDueDate3st);

            verifyTransactions(loanId.get(), //
                    transaction(1000, "Disbursement", disbursementDay, 1000, 0, 0, 0, 0, 0, 0),
                    transaction(20, "Accrual", "01 February 2023", 0, 0, 0, 0, 20, 0, 0),
                    transaction(50, "Repayment", "10 January 2023", 730, 50, 0, 0, 0, 0, 0),
                    transaction(220, "Repayment", "08 January 2023", 780, 220, 0, 0, 0, 0, 0),
                    transaction(65, "Accrual Activity", "01 February 2023", 0, 0, 0, 12, 53, 0, 0));

        });
    }

    // Create Loan with Advanced Payment Allocation and enabled Accrual Activity Posting
    // Approve and disburse loan
    // charge penalty for 1st installment
    // run COB on closing day of first installment
    // verify that the Accrual Activity transaction is created
    // charge backdated fee before first installment due date
    // verify that the Accrual Activity transaction is reverse replayed
    // verify that the Accrual Activity holds the correct portions
    @ParameterizedTest
    @CsvSource({ "29 January 2023,30 January 2023,31 January 2023", "31 January 2023,30 January 2023,29 January 2023",
            "31 January 2023,31 January 2023,31 January 2023", "01 February 2023,01 February 2023,01 February 2023" })
    public void testAccrualActivityPostingReverseReplayAdvancedPaymentAllocationBasicFlow(final String chargeDueDate1st,
            final String chargeDueDate2st, final String chargeDueDate3st) {
        final String disbursementDay = "01 January 2023";
        final String repaymentPeriod1CloseDate = "02 February 2023";
        final String repaymentPeriod1OneDayAfterCloseDate = "03 February 2023";
        final String creationBusinessDay = "15 January 2023";
        AtomicReference<Long> loanId = new AtomicReference<>();
        runAt(creationBusinessDay, () -> {

            Long localLoanProductId = createLoanProductAccountingAccrualPeriodicAdvancedPaymentAllocation();
            loanId.set(applyForLoanApplicationAdvancedPaymentAllocation(client.getClientId(), localLoanProductId, BigDecimal.valueOf(40000),
                    disbursementDay, BigDecimal.ZERO));

            loanTransactionHelper.approveLoan(loanId.get(), new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(1000))
                    .dateFormat(DATETIME_PATTERN).approvedOnDate(disbursementDay).locale("en"));

            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(1000.0)).locale("en"));

            chargePenalty(loanId.get(), 20.0, chargeDueDate1st);

            verifyTransactions(loanId.get(), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));

        });
        runAt(repaymentPeriod1CloseDate, () -> {
            inlineLoanCOBHelper.executeInlineCOB(List.of(loanId.get()));
            verifyTransactions(loanId.get(), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    transaction(20.0, "Accrual", "01 February 2023", 0, 0, 0, 0, 20, 0.0, 0.0),
                    transaction(20.0, "Accrual Activity", "01 February 2023", 0, 0, 0.0, 0.0, 20.0, 0.0, 0.0));

        });
        runAt(repaymentPeriod1OneDayAfterCloseDate, () -> {
            chargePenalty(loanId.get(), 33.0, chargeDueDate2st);

            verifyTransactions(loanId.get(), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    transaction(20.0, "Accrual", "01 February 2023", 0, 0, 0, 0, 20, 0.0, 0.0),
                    transaction(53.0, "Accrual Activity", "01 February 2023", 0, 0, 0.0, 0.0, 53.0, 0.0, 0.0));

            chargeFee(loanId.get(), 12.0, chargeDueDate3st);

            verifyTransactions(loanId.get(), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    transaction(20.0, "Accrual", "01 February 2023", 0, 0, 0, 0, 20, 0.0, 0.0),
                    transaction(65.0, "Accrual Activity", "01 February 2023", 0, 0, 0.0, 12.0, 53.0, 0.0, 0.0));

        });
    }

    @Test
    public void testAccrualActivityPostingForProgressiveLoanWithEarlyRepaymentAndReverseRepayment() {
        final String disbursementDay = "01 January 2023";
        final String repaymentDate1 = "15 January 2023";
        final String repaymentPeriod1DueDate = "01 February 2023";
        final String repaymentPeriod1CloseDate = "02 February 2023";
        final String creationBusinessDay = "15 January 2023";
        AtomicReference<Long> loanId = new AtomicReference<>();
        AtomicReference<Long> repaymentId = new AtomicReference<>();
        runAt(creationBusinessDay, () -> {
            Long localLoanProductId = createLoanProductAccountingAccrualPeriodicAdvancedPaymentAllocation();
            loanId.set(applyForLoanApplicationAdvancedPaymentAllocation(client.getClientId(), localLoanProductId, BigDecimal.valueOf(40000),
                    disbursementDay, BigDecimal.ZERO));

            loanTransactionHelper.approveLoan(loanId.get(), new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(1000))
                    .dateFormat(DATETIME_PATTERN).approvedOnDate(disbursementDay).locale("en"));

            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(1000.0)).locale("en"));

            chargePenalty(loanId.get(), 30.0, repaymentPeriod1DueDate);
            chargeFee(loanId.get(), 40.0, repaymentPeriod1DueDate);

            repaymentId.set(addRepaymentForLoan(loanId.get(), 1070.0, repaymentDate1));

            verifyTransactions(loanId.get(),
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(1070.0, "Repayment", repaymentDate1, 0.0, 1000.0, 0.0, 40.0, 30, 0.0, 0.0, false), //
                    transaction(70.0, "Accrual", repaymentDate1, 0.0, 0.0, 0.0, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(70.0, "Accrual Activity", repaymentDate1, 0.0, 0.0, 0.0, 40.0, 30.0, 0.0, 0.0, false)); //

        });
        runAt(repaymentPeriod1CloseDate, () -> {

            inlineLoanCOBHelper.executeInlineCOB(List.of(loanId.get()));

            verifyTransactions(loanId.get(),
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(1070.0, "Repayment", repaymentDate1, 0.0, 1000.0, 0.0, 40.0, 30, 0.0, 0.0, false), //
                    transaction(70.0, "Accrual", repaymentDate1, 0.0, 0.0, 0.0, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(70.0, "Accrual Activity", repaymentDate1, 0.0, 0.0, 0.0, 40.0, 30.0, 0.0, 0.0, false)); //

            loanTransactionHelper.reverseRepayment(loanId.get().intValue(), repaymentId.get().intValue(), repaymentDate1);

            verifyTransactions(loanId.get(),
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(1070.0, "Repayment", repaymentDate1, 0.0, 1000.0, 0.0, 40.0, 30, 0.0, 0.0, true), //
                    transaction(70.0, "Accrual", repaymentDate1, 0.0, 0.0, 0.0, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(70.0, "Accrual Activity", repaymentPeriod1DueDate, 0.0, 0.0, 0.0, 40.0, 30.0, 0.0, 0.0, false)); //
        });
    }

    @Test
    public void testAccrualActivityPostingForLoanWithEarlyRepaymentAndReverseRepayment() {
        final String disbursementDay = "01 January 2023";
        final String repaymentDate1 = "15 January 2023";
        final String repaymentPeriod1DueDate = "01 February 2023";
        final String repaymentPeriod1CloseDate = "02 February 2023";
        final String creationBusinessDay = "15 January 2023";
        AtomicReference<Long> loanId = new AtomicReference<>();
        AtomicReference<Long> repaymentId = new AtomicReference<>();
        runAt(creationBusinessDay, () -> {
            Long localLoanProductId = createLoanProductAccountingAccrualPeriodicWithInterest();
            loanId.set(applyForLoanApplicationWithInterest(client.getClientId(), localLoanProductId, BigDecimal.valueOf(40000),
                    disbursementDay));

            loanTransactionHelper.approveLoan(loanId.get(), new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(1000))
                    .dateFormat(DATETIME_PATTERN).approvedOnDate(disbursementDay).locale("en"));

            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(1000.0)).locale("en"));

            chargePenalty(loanId.get(), 30.0, repaymentPeriod1DueDate);
            chargeFee(loanId.get(), 40.0, repaymentPeriod1DueDate);

            repaymentId.set(addRepaymentForLoan(loanId.get(), 1150.0, repaymentDate1));

            verifyTransactions(loanId.get(),
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(1150.0, "Repayment", repaymentDate1, 0.0, 1000.0, 80.0, 40.0, 30, 0.0, 0.0, false), //
                    transaction(150.0, "Accrual", repaymentDate1, 0.0, 0.0, 80.0, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(150.0, "Accrual Activity", repaymentDate1, 0.0, 0.0, 80.0, 40.0, 30.0, 0.0, 0.0, false)); //

        });
        runAt(repaymentPeriod1CloseDate, () -> {

            inlineLoanCOBHelper.executeInlineCOB(List.of(loanId.get()));

            verifyTransactions(loanId.get(),
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(1150.0, "Repayment", repaymentDate1, 0.0, 1000.0, 80.0, 40, 30, 0.0, 0.0, false), //
                    transaction(150.0, "Accrual", repaymentDate1, 0.0, 0.0, 80.0, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(150.0, "Accrual Activity", repaymentDate1, 0.0, 0.0, 80.0, 40.0, 30.0, 0.0, 0.0, false)); //

            loanTransactionHelper.reverseRepayment(loanId.get().intValue(), repaymentId.get().intValue(), repaymentDate1);

            verifyTransactions(loanId.get(),
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(1150.0, "Repayment", repaymentDate1, 0.0, 1000.0, 80.0, 40.0, 30, 0.0, 0.0, true), //
                    transaction(90.0, "Accrual Activity", repaymentPeriod1DueDate, 0.0, 0.0, 20.0, 40.0, 30.0, 0.0, 0.0, false)); //

        });
    }

    @Test
    public void testAccrualActivityPostingForProgressiveMultiDisburseLoanWithEarlyRepayment2ndDisbursement() {
        final String disbursementDay = "01 January 2023";
        final String disbursementDay2 = "25 January 2023";
        final String repaymentDate1 = "15 January 2023";
        final String repaymentPeriod1DueDate = "01 February 2023";
        final String repaymentPeriod1CloseDate = "02 February 2023";
        final String creationBusinessDay = "15 January 2023";
        AtomicReference<Long> loanId = new AtomicReference<>();
        runAt(creationBusinessDay, () -> {
            Long localLoanProductId = createLoanProductAccountingAccrualPeriodicAdvancedPaymentAllocation(true);

            loanId.set(applyForLoanApplicationAdvancedPaymentAllocation(client.getClientId(), localLoanProductId, BigDecimal.valueOf(40000),
                    disbursementDay, disbursementDay2, BigDecimal.ZERO));

            loanTransactionHelper.approveLoan(loanId.get(), new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(1000))
                    .dateFormat(DATETIME_PATTERN).approvedOnDate(disbursementDay).locale("en"));

            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(500.0)).locale("en"));

            chargePenalty(loanId.get(), 30.0, repaymentPeriod1DueDate);
            chargeFee(loanId.get(), 40.0, repaymentPeriod1DueDate);

            addRepaymentForLoan(loanId.get(), 650.0, repaymentDate1);

            verifyTransactions(loanId.get(),
                    transaction(500.0, "Disbursement", disbursementDay, 500.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(650.0, "Repayment", repaymentDate1, 0.0, 500.0, 0.0, 40.0, 30.0, 0.0, 80.0, false), //
                    transaction(70.0, "Accrual", repaymentDate1, 0.0, 0.0, 0.0, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(70.0, "Accrual Activity", repaymentDate1, 0.0, 0.0, 0.0, 40.0, 30.0, 0.0, 0.0, false) //
            );
        });
        runAt(disbursementDay2, () -> {

            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay2)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(500.0)).locale("en"));

            verifyTransactions(loanId.get(),
                    transaction(500.0, "Disbursement", disbursementDay, 500.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(650.0, "Repayment", repaymentDate1, 0.0, 500.0, 0.0, 40.0, 30.0, 0.0, 80.0, false), //
                    transaction(70.0, "Accrual", repaymentDate1, 0.0, 0.0, 0.0, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(500.0, "Disbursement", disbursementDay2, 420.0, 0.0, 0.0, 0.0, 0.0, 0.0, 80.0, false) //
            );
        });
        runAt(repaymentPeriod1CloseDate, () -> {

            inlineLoanCOBHelper.executeInlineCOB(List.of(loanId.get()));

            verifyTransactions(loanId.get(),
                    transaction(500.0, "Disbursement", disbursementDay, 500.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(650.0, "Repayment", repaymentDate1, 0.0, 500.0, 0.0, 40.0, 30.0, 0.0, 80.0, false), //
                    transaction(70.0, "Accrual", repaymentDate1, 0.0, 0.0, 0.0, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(500.0, "Disbursement", disbursementDay2, 420.0, 0.0, 0.0, 0.0, 0.0, 0.0, 80.0, false), //
                    transaction(70.0, "Accrual Activity", repaymentPeriod1DueDate, 0.0, 0.0, 0.0, 40.0, 30.0, 0.0, 0.0, false) //
            );
        });
    }

    @Test
    public void testAccrualActivityPostingForProgressiveMultiDisburseLoanWithEarlyRepaymentBackdated2ndDisbursement() {
        final String disbursementDay = "01 January 2023";
        final String disbursementDay2 = "25 January 2023";
        final String repaymentDate1 = "15 January 2023";
        final String repaymentPeriod1DueDate = "01 February 2023";
        final String repaymentPeriod1CloseDate = "02 February 2023";
        final String creationBusinessDay = "15 January 2023";
        AtomicReference<Long> loanId = new AtomicReference<>();
        runAt(creationBusinessDay, () -> {
            Long localLoanProductId = createLoanProductAccountingAccrualPeriodicAdvancedPaymentAllocation(true);

            loanId.set(applyForLoanApplicationAdvancedPaymentAllocation(client.getClientId(), localLoanProductId, BigDecimal.valueOf(40000),
                    disbursementDay, disbursementDay2, BigDecimal.ZERO));

            loanTransactionHelper.approveLoan(loanId.get(), new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(1000))
                    .dateFormat(DATETIME_PATTERN).approvedOnDate(disbursementDay).locale("en"));

            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(500.0)).locale("en"));

            chargePenalty(loanId.get(), 30.0, repaymentPeriod1DueDate);
            chargeFee(loanId.get(), 40.0, repaymentPeriod1DueDate);

            addRepaymentForLoan(loanId.get(), 650.0, repaymentDate1);

            verifyTransactions(loanId.get(), transaction(650.0, "Repayment", repaymentDate1, 0.0, 500.0, 0.0, 40.0, 30.0, 0.0, 80.0, false), //
                    transaction(70.0, "Accrual", repaymentDate1, 0.0, 0.0, 0.0, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(70.0, "Accrual Activity", repaymentDate1, 0.0, 0.0, 0.0, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(500.0, "Disbursement", disbursementDay, 500.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false)); //

        });
        runAt(repaymentPeriod1CloseDate, () -> {

            inlineLoanCOBHelper.executeInlineCOB(List.of(loanId.get()));

            verifyTransactions(loanId.get(), transaction(650.0, "Repayment", repaymentDate1, 0.0, 500.0, 0.0, 40.0, 30.0, 0.0, 80.0, false), //
                    transaction(70.0, "Accrual", repaymentDate1, 0.0, 0.0, 0.0, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(70.0, "Accrual Activity", repaymentDate1, 0.0, 0.0, 0.0, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(500.0, "Disbursement", disbursementDay, 500.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false)); //

            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay2)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(500.0)).locale("en"));

            verifyTransactions(loanId.get(), transaction(650.0, "Repayment", repaymentDate1, 0.0, 500.0, 0.0, 40.0, 30.0, 0.0, 80.0, false), //
                    transaction(500.0, "Disbursement", disbursementDay2, 420.0, 0.0, 0.0, 0.0, 0.0, 0.0, 80.0, false), //
                    transaction(70.0, "Accrual", repaymentDate1, 0.0, 0.0, 0.0, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(70.0, "Accrual Activity", repaymentPeriod1DueDate, 0.0, 0.0, 0.0, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(500.0, "Disbursement", disbursementDay, 500.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false)); //

        });
    }

    @Test
    public void testAccrualActivityPostingForMultiDisburseLoanWithEarlyRepaymentBackdated2ndDisbursement() {
        final String disbursementDay = "01 January 2023";
        final String disbursementDay2 = "25 January 2023";
        final String repaymentDate1 = "15 January 2023";
        final String repaymentPeriod1DueDate = "01 February 2023";
        final String repaymentPeriod1CloseDate = "02 February 2023";
        final String creationBusinessDay = "15 January 2023";
        AtomicReference<Long> loanId = new AtomicReference<>();
        runAt(creationBusinessDay, () -> {
            Long localLoanProductId = createLoanProductAccountingAccrualPeriodicWithInterest(true);
            loanId.set(applyForLoanApplicationWithInterest(client.getClientId(), localLoanProductId, BigDecimal.valueOf(40000),
                    disbursementDay, disbursementDay2));

            loanTransactionHelper.approveLoan(loanId.get(), new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(1000))
                    .dateFormat(DATETIME_PATTERN).approvedOnDate(disbursementDay).locale("en"));

            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(500.0)).locale("en"));

            chargePenalty(loanId.get(), 30.0, repaymentPeriod1DueDate);
            chargeFee(loanId.get(), 40.0, repaymentPeriod1DueDate);

            addRepaymentForLoan(loanId.get(), 650.0, repaymentDate1);

            verifyTransactions(loanId.get(), transaction(94.9, "Accrual", repaymentDate1, 0.0, 0.0, 24.9, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(94.9, "Accrual Activity", repaymentDate1, 0.0, 0.0, 24.9, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(650.0, "Repayment", repaymentDate1, 0.0, 500.0, 24.9, 40.0, 30.0, 0.0, 55.1, false), //
                    transaction(500.0, "Disbursement", disbursementDay, 500.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false)); //
        });
        runAt(repaymentPeriod1CloseDate, () -> {

            inlineLoanCOBHelper.executeInlineCOB(List.of(loanId.get()));

            verifyTransactions(loanId.get(), transaction(94.9, "Accrual", repaymentDate1, 0.0, 0.0, 24.9, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(94.9, "Accrual Activity", repaymentDate1, 0.0, 0.0, 24.9, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(650.0, "Repayment", repaymentDate1, 0.0, 500.0, 24.9, 40.0, 30.0, 0.0, 55.1, false), //
                    transaction(500.0, "Disbursement", disbursementDay, 500.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false)); //

            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay2)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(500.0)).locale("en"));

            verifyTransactions(loanId.get(),
                    transaction(500.0, "Disbursement", disbursementDay, 500.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(82.49, "Accrual Activity", repaymentPeriod1DueDate, 0.0, 0.0, 12.49, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(500.0, "Disbursement", disbursementDay2, 456.52, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(650.0, "Repayment", repaymentDate1, 0.0, 543.48, 36.52, 40.0, 30.0, 0.0, 0.0, false)); //

        });
    }

    @Test
    public void testAccrualActivityPostingForMultiDisburseProgressiveLoan() {
        final String disbursementDay = "01 January 2023";
        final String disbursementDay2 = "02 February 2023";
        final String repaymentDate1 = "15 January 2023";
        final String repaymentPeriod1DueDate = "01 February 2023";
        final String repaymentPeriod2DueDate = "01 March 2023";
        final String repaymentPeriod1CloseDate = "02 February 2023";
        final String repaymentPeriod2CloseDate = "02 March 2023";
        final String creationBusinessDay = "15 January 2023";
        AtomicReference<Long> loanId = new AtomicReference<>();
        runAt(creationBusinessDay, () -> {
            Long localLoanProductId = createLoanProductAccountingAccrualPeriodicAdvancedPaymentAllocation(true);
            loanId.set(applyForLoanApplicationAdvancedPaymentAllocation(client.getClientId(), localLoanProductId, BigDecimal.valueOf(1000),
                    disbursementDay, disbursementDay2, BigDecimal.ZERO));
            loanTransactionHelper.approveLoan(loanId.get(), new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(1000))
                    .dateFormat(DATETIME_PATTERN).approvedOnDate(disbursementDay).locale("en"));

            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(500.0)).locale("en"));
            chargePenalty(loanId.get(), 30.0, repaymentPeriod1DueDate);
            chargeFee(loanId.get(), 40.0, repaymentPeriod1DueDate);

            addRepaymentForLoan(loanId.get(), 570.0, repaymentDate1);

            verifyTransactions(loanId.get(), transaction(500.0, "Disbursement", disbursementDay, 500.0, 0, 0, 0, 0, 0, 0, false),
                    transaction(570, "Repayment", repaymentDate1, 0.0, 500.0, 0, 40.0, 30.0, 0, 0, false),
                    transaction(70.0, "Accrual", repaymentDate1, 0, 0, 0.0, 40.0, 30.0, 0, 0, false),
                    transaction(70.0, "Accrual Activity", repaymentDate1, 0, 0, 0.0, 40.0, 30.0, 0, 0, false));
        });
        runAt(repaymentPeriod1CloseDate, () -> {

            inlineLoanCOBHelper.executeInlineCOB(List.of(loanId.get()));

            verifyTransactions(loanId.get(), transaction(500.0, "Disbursement", disbursementDay, 500.0, 0, 0, 0, 0, 0, 0, false),
                    transaction(570, "Repayment", repaymentDate1, 0.0, 500.0, 0, 40.0, 30.0, 0, 0, true),
                    transaction(70.0, "Accrual", repaymentDate1, 0, 0, 0.0, 40.0, 30.0, 0, 0, false),
                    transaction(70.0, "Accrual Activity", repaymentDate1, 0, 0, 0.0, 40.0, 30.0, 0, 0, false));

            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay2)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(500.0)).locale("en"));

            verifyTransactions(loanId.get(), transaction(500.0, "Disbursement", disbursementDay, 500.0, 0, 0, 0, 0, 0, 0, false),
                    transaction(570, "Repayment", repaymentDate1, 0.0, 500.0, 0, 40.0, 30.0, 0, 0, false),
                    transaction(70.0, "Accrual", repaymentDate1, 0, 0, 0.0, 40.0, 30.0, 0, 0, false),
                    transaction(70.0, "Accrual Activity", repaymentPeriod1DueDate, 0, 0, 0.0, 40.0, 30.0, 0, 0, false),
                    transaction(500.0, "Disbursement", disbursementDay2, 500, 0, 0, 0, 0, 0, 0, false));
        });
        runAt(repaymentPeriod2CloseDate, () -> {

            inlineLoanCOBHelper.executeInlineCOB(List.of(loanId.get()));

            verifyTransactions(loanId.get(), transaction(500.0, "Disbursement", disbursementDay, 500.0, 0, 0, 0, 0, 0, 0, false),
                    transaction(570.0, "Repayment", repaymentDate1, 0.0, 500.0, 0, 40.0, 30.0, 0, 0, false),
                    transaction(70.0, "Accrual", repaymentDate1, 0, 0, 0.0, 40.0, 30.0, 0, 0, false),
                    transaction(70.0, "Accrual Activity", repaymentPeriod1DueDate, 0, 0, 0.0, 40.0, 30.0, 0, 0, false),
                    transaction(500.0, "Disbursement", disbursementDay2, 500.0, 0, 0, 0, 0, 0, 0, false));
        });
    }

    @Test
    public void testAccrualActivityPostingForMultiDisburseLoan() {
        final String disbursementDay = "01 January 2023";
        final String disbursementDay2 = "02 February 2023";
        final String repaymentDate1 = "15 January 2023";
        final String repaymentPeriod1DueDate = "01 February 2023";
        final String repaymentPeriod1CloseDate = "02 February 2023";
        final String repaymentPeriod2DueDate = "01 March 2023";
        final String repaymentPeriod2CloseDate = "02 March 2023";
        final String creationBusinessDay = "15 January 2023";
        AtomicReference<Long> loanId = new AtomicReference<>();
        runAt(creationBusinessDay, () -> {
            Long localLoanProductId = createLoanProductAccountingAccrualPeriodicWithInterest(true);
            loanId.set(applyForLoanApplicationWithInterest(client.getClientId(), localLoanProductId, BigDecimal.valueOf(40000),
                    disbursementDay, disbursementDay2));
            loanTransactionHelper.approveLoan(loanId.get(), new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(1000))
                    .dateFormat(DATETIME_PATTERN).approvedOnDate(disbursementDay).locale("en"));

            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(500.0)).locale("en"));
            chargePenalty(loanId.get(), 30.0, repaymentPeriod1DueDate);
            chargeFee(loanId.get(), 40.0, repaymentPeriod1DueDate);
            addRepaymentForLoan(loanId.get(), 650.0, repaymentDate1);
            verifyTransactions(loanId.get(),
                    transaction(650.0, "Repayment", repaymentDate1, 0.0, 500.0, 24.9, 40.0, 30.0, 0.0, 55.1, false),
                    transaction(94.90, "Accrual Activity", repaymentDate1, 0.0, 0.0, 24.9, 40.0, 30.0, 0.0, 0.0, false),
                    transaction(94.90, "Accrual", repaymentDate1, 0.0, 0.0, 24.9, 40.0, 30.0, 0.0, 0.0, false),
                    transaction(500.0, "Disbursement", disbursementDay, 500.0, 0, 0, 0, 0, 0, 0, false));
        });
        runAt(repaymentPeriod1CloseDate, () -> {
            inlineLoanCOBHelper.executeInlineCOB(List.of(loanId.get()));
            verifyTransactions(loanId.get(),
                    transaction(650.0, "Repayment", repaymentDate1, 0.0, 500.0, 24.9, 40.0, 30.0, 0.0, 55.1, false), //
                    transaction(94.90, "Accrual Activity", repaymentDate1, 0.0, 0.0, 24.9, 40.0, 30.0, 0.0, 0.0, false),
                    transaction(94.90, "Accrual", repaymentDate1, 0.0, 0.0, 24.9, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(500.0, "Disbursement", disbursementDay, 500.0, 0, 0, 0, 0, 0, 0, false) //
            );
            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay2)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(500.0)).locale("en"));
            verifyTransactions(loanId.get(),
                    transaction(500.0, "Disbursement", disbursementDay2, 453.79, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(650.0, "Repayment", repaymentDate1, 0.0, 546.21, 33.79, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(80.19, "Accrual Activity", repaymentPeriod1DueDate, 0.0, 0.0, 10.19, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(500.0, "Disbursement", disbursementDay, 500.0, 0, 0, 0, 0, 0, 0, false) //
            );
        });
        runAt(repaymentPeriod2CloseDate, () -> {
            inlineLoanCOBHelper.executeInlineCOB(List.of(loanId.get()));
            verifyTransactions(loanId.get(),
                    transaction(500.0, "Disbursement", disbursementDay2, 453.79, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(650.0, "Repayment", repaymentDate1, 0.0, 546.21, 33.79, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(80.19, "Accrual Activity", repaymentPeriod1DueDate, 0.0, 0.0, 10.19, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(80.19, "Accrual", repaymentPeriod1DueDate, 0.0, 0.0, 10.19, 40.0, 30.0, 0.0, 0.0, false), //
                    transaction(13.43, "Accrual", repaymentPeriod2DueDate, 0.0, 0.0, 13.43, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(13.43, "Accrual Activity", repaymentPeriod2DueDate, 0.0, 0.0, 13.43, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(500.0, "Disbursement", disbursementDay, 500.0, 0, 0, 0, 0, 0, 0, false) //
            );
        });
    }

    Long interestBearingProgressiveLoanProductId = null;

    public void createInterestBearingProgressiveNoInterestRecalculationAutoDownPayment25percentLoanProductIfNotExists() {
        if (interestBearingProgressiveLoanProductId == null) {
            final PostLoanProductsResponse loanProductsResponse = loanProductHelper.createLoanProduct(create4IProgressive()
                    .currencyCode("USD").enableAccrualActivityPosting(true).enableDownPayment(true)
                    .disbursedAmountPercentageForDownPayment(BigDecimal.valueOf(25.0)).enableAutoRepaymentForDownPayment(true)
                    .currencyCode("USD").daysInMonthType(DaysInMonthType.ACTUAL).daysInYearType(DaysInYearType.ACTUAL)
                    .isInterestRecalculationEnabled(false).description(
                            "Interest bearing Progressive Loan USD, Auto Down Payment 25%, Accrual Activity Posting, NO InterestRecalculation"));
            interestBearingProgressiveLoanProductId = loanProductsResponse.getResourceId();
        }
    }

    /*
     * using Interest bearing Progressive Loan USD, Auto Down Payment 25%, Accrual Activity Posting, NO
     * InterestRecalculation 9.99 yearly interest 6 repayment 400 USD principal apply, approve and disburse on 1 January
     * 2024 auto down payment 100 USD on 1 January 2024 repayment 370USD on 2 January 2024 verify Accrual and Accrual
     * Activity transaction creation verify that the loan become overpaid reverse the repayment on same day verify
     * transaction reversals
     */
    @Test
    public void testInterestBearingProgressiveNoInterestRecalculationAutoDownPayment25percentReopenDueReverseRepayment1() {
        createInterestBearingProgressiveNoInterestRecalculationAutoDownPayment25percentLoanProductIfNotExists();
        AtomicReference<Long> loanIdRef = new AtomicReference<>(null);
        runAt("1 January 2024", () -> {
            PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applyLP2ProgressiveLoanRequest(client.getClientId(),
                    interestBearingProgressiveLoanProductId, "01 January 2024", 400.0, 9.99, 6, null));
            Long loanId = postLoansResponse.getLoanId();
            Assertions.assertNotNull(loanId);
            loanIdRef.set(loanId);
            loanTransactionHelper.approveLoan(loanId, approveLoanRequest(400.0, "01 January 2024"));
            disburseLoan(loanId, BigDecimal.valueOf(400.0), "01 January 2024");
            verifyTransactions(loanId, //
                    transaction(400.0, "Disbursement", "01 January 2024"), //
                    transaction(100.0, "Down Payment", "01 January 2024") //
            );
        });
        runAt("2 January 2024", () -> {
            Long loanId = loanIdRef.get();
            Long repaymentId = loanTransactionHelper.makeLoanRepayment("02 January 2024", 370.0f, loanId.intValue()).getResourceId();
            Assertions.assertNotNull(repaymentId);
            GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            verifyLoanStatus(loanDetails, GetLoansLoanIdStatus::getOverpaid);

            verifyTransactions(loanId, transaction(400.0, "Disbursement", "01 January 2024"),
                    transaction(100.0, "Down Payment", "01 January 2024"), transaction(8.76, "Accrual", "02 January 2024"),
                    transaction(8.76, "Accrual Activity", "02 January 2024"), transaction(370.0, "Repayment", "02 January 2024"));
            loanTransactionHelper.reverseRepayment(loanId.intValue(), repaymentId.intValue(), "02 January 2024");
        });
    }

    /*
     * using Interest bearing Progressive Loan USD, Auto Down Payment 25%, Accrual Activity Posting, NO
     * InterestRecalculation 9.99 yearly interest 6 repayment 400 USD principal apply, approve and disburse on 1 January
     * 2024 auto down payment 100 USD on 1 January 2024 repayment 370USD on 1 January 2024 verify Accrual and Accrual
     * Activity transaction creation verify that the loan become overpaid reverse the repayment on same day verify
     * transaction reversals
     */
    @Test
    public void testInterestBearingProgressiveNoInterestRecalculationAutoDownPayment25percentReopenDueReverseRepayment2() {
        createInterestBearingProgressiveNoInterestRecalculationAutoDownPayment25percentLoanProductIfNotExists();
        runAt("1 January 2024", () -> {
            PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applyLP2ProgressiveLoanRequest(client.getClientId(),
                    interestBearingProgressiveLoanProductId, "01 January 2024", 400.0, 9.99, 6, null));
            Long loanId = postLoansResponse.getLoanId();
            Assertions.assertNotNull(loanId);
            loanTransactionHelper.approveLoan(loanId, approveLoanRequest(400.0, "01 January 2024"));
            disburseLoan(loanId, BigDecimal.valueOf(400.0), "01 January 2024");
            verifyTransactions(loanId, //
                    transaction(400.0, "Disbursement", "01 January 2024"), //
                    transaction(100.0, "Down Payment", "01 January 2024") //
            );
            Long repaymentId = loanTransactionHelper.makeLoanRepayment("01 January 2024", 370.0f, loanId.intValue()).getResourceId();
            Assertions.assertNotNull(repaymentId);
            GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            verifyLoanStatus(loanDetails, GetLoansLoanIdStatus::getOverpaid);

            verifyTransactions(loanId, transaction(400.0, "Disbursement", "01 January 2024"),
                    transaction(100.0, "Down Payment", "01 January 2024"), transaction(8.76, "Accrual", "01 January 2024"),
                    transaction(8.76, "Accrual Activity", "01 January 2024"), transaction(370.0, "Repayment", "01 January 2024"));

            loanTransactionHelper.reverseRepayment(loanId.intValue(), repaymentId.intValue(), "01 January 2024");
            loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            verifyLoanStatus(loanDetails, GetLoansLoanIdStatus::getActive);
            verifyTransactions(loanId, transaction(400.0, "Disbursement", "01 January 2024"),
                    transaction(100.0, "Down Payment", "01 January 2024"), transaction(8.76, "Accrual", "01 January 2024"),
                    reversedTransaction(370.0, "Repayment", "01 January 2024"));

        });
    }

    /*
     * using Interest bearing Progressive Loan USD, Auto Down Payment 25%, Accrual Activity Posting, NO
     * InterestRecalculation 9.99 yearly interest 6 repayment 400 USD principal apply, approve and disburse on 1 January
     * 2024 auto down payment 100 USD on 1 January 2024 charge 30USD fee on 1 January 2024 repayment 370USD on 1 January
     * 2024 verify Accrual and Accrual Activity transaction creation verify that the loan become overpaid reverse the
     * repayment on same day verify transaction reversals
     */
    @Test
    public void testInterestBearingProgressiveNoInterestRecalculationAutoDownPayment25percentReopenDueReverseRepayment3() {
        createInterestBearingProgressiveNoInterestRecalculationAutoDownPayment25percentLoanProductIfNotExists();
        runAt("1 January 2024", () -> {
            PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applyLP2ProgressiveLoanRequest(client.getClientId(),
                    interestBearingProgressiveLoanProductId, "01 January 2024", 400.0, 9.99, 6, null));
            Long loanId = postLoansResponse.getLoanId();
            Assertions.assertNotNull(loanId);
            loanTransactionHelper.approveLoan(loanId, approveLoanRequest(400.0, "01 January 2024"));
            disburseLoan(loanId, BigDecimal.valueOf(400.0), "01 January 2024");
            verifyTransactions(loanId, //
                    transaction(400.0, "Disbursement", "01 January 2024"), //
                    transaction(100.0, "Down Payment", "01 January 2024") //
            );
            addCharge(loanId, false, 30.0, "01 January 2024");
            Long repaymentId = loanTransactionHelper.makeLoanRepayment("01 January 2024", 370.0f, loanId.intValue()).getResourceId();
            Assertions.assertNotNull(repaymentId);
            GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            verifyLoanStatus(loanDetails, GetLoansLoanIdStatus::getOverpaid);

            verifyTransactions(loanId, transaction(400.0, "Disbursement", "01 January 2024"),
                    transaction(100.0, "Down Payment", "01 January 2024"), transaction(38.76, "Accrual", "01 January 2024"),
                    transaction(38.76, "Accrual Activity", "01 January 2024"), transaction(370.0, "Repayment", "01 January 2024"));
            loanTransactionHelper.reverseRepayment(loanId.intValue(), repaymentId.intValue(), "01 January 2024");
            loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            verifyLoanStatus(loanDetails, GetLoansLoanIdStatus::getActive);
            verifyTransactions(loanId, transaction(400.0, "Disbursement", "01 January 2024"),
                    transaction(100.0, "Down Payment", "01 January 2024"), transaction(38.76, "Accrual", "01 January 2024"),
                    reversedTransaction(370.0, "Repayment", "01 January 2024"));
        });
    }

    @Test
    public void test() {
        final String disbursementDay = "01 January 2023";
        final String repaymentDate1 = "15 January 2023";
        final String repaymentPeriod1DueDate = "31 January 2023";
        final String repaymentPeriod1CloseDate = "01 February 2023";
        final String creationBusinessDay = "15 January 2023";
        AtomicReference<Long> loanId = new AtomicReference<>();
        runAt(creationBusinessDay, () -> {
            Long localLoanProductId = loanTransactionHelper
                    .createLoanProduct(loanProductsRequestInterestDecliningBalanceDailyRecalculationCompoundingNoneAccrualActivity())
                    .getResourceId();
            loanId.set(applyForLoanApplication(client.getClientId(), localLoanProductId, BigDecimal.valueOf(1000), disbursementDay));
            loanTransactionHelper.approveLoan(loanId.get(), new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(1000))
                    .dateFormat(DATETIME_PATTERN).approvedOnDate(disbursementDay).locale("en"));

            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(1000.0)).locale("en"));
        });
        runAt(repaymentPeriod1CloseDate, () -> {
            inlineLoanCOBHelper.executeInlineCOB(List.of(loanId.get()));
            verifyTransactions(loanId.get(),
                    transaction(1.64, "Accrual", repaymentPeriod1DueDate, 0.0, 0.0, 1.64, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(1.64, "Accrual Activity", repaymentPeriod1DueDate, 0.0, 0.0, 1.64, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false));
            loanTransactionHelper.makeLoanRepayment(repaymentDate1, 150.0F, loanId.get().intValue());

            verifyTransactions(loanId.get(),
                    transaction(150.0, "Repayment", repaymentDate1, 851.52, 148.48, 1.52, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(1.52, "Accrual Activity", repaymentPeriod1DueDate, 0.0, 0.0, 1.52, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(1.52, "Accrual", repaymentPeriod1DueDate, 0.0, 0.0, 1.52, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(1000.0, "Disbursement", disbursementDay, 1000.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false));
        });
    }

    @Test
    public void testReverseAndReplayedCoupleOfTimesAfterBackdatedRepayment() {
        final String disbursementDay = "01 January 2025";
        AtomicReference<Long> loanId = new AtomicReference<>();
        runAt(disbursementDay, () -> {
            Long localLoanProductId = createLoanProductAccountingAccrualPeriodicAdvancedPaymentAllocationInterestRecalculation(true);
            loanId.set(applyForLoanApplicationAdvancedPaymentAllocation(client.getClientId(), localLoanProductId, BigDecimal.valueOf(800),
                    disbursementDay, disbursementDay, BigDecimal.valueOf(0.3)));

            loanTransactionHelper.approveLoan(loanId.get(), new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(800))
                    .dateFormat(DATETIME_PATTERN).approvedOnDate(disbursementDay).locale("en"));

            loanTransactionHelper.disburseLoan(loanId.get(), new PostLoansLoanIdRequest().actualDisbursementDate(disbursementDay)
                    .dateFormat(DATETIME_PATTERN).transactionAmount(BigDecimal.valueOf(800.0)).locale("en"));
        });

        runAt("02 February 2025", () -> {
            inlineLoanCOBHelper.executeInlineCOB(List.of(loanId.get()));
            verifyTransactions(loanId.get(),
                    transaction(10.60, "Accrual Activity", "01 February 2025", 0.0, 0.0, 10.60, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(10.60, "Accrual", "01 February 2025", 0.0, 0.0, 10.60, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(800.0, "Disbursement", disbursementDay, 800.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false));

            loanTransactionHelper.makeLoanRepayment("31 January 2025", 900.0F, loanId.get().intValue());

            verifyTransactions(loanId.get(),
                    transaction(0.34, "Accrual Adjustment", "02 February 2025", 0.0, 0.0, 0.34, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(10.60, "Accrual", "01 February 2025", 0.0, 0.0, 10.60, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(10.26, "Accrual Activity", "31 January 2025", 0.0, 0.0, 10.26, 0.0, 0.0, 0.0, 0.0, false), //
                    transaction(900.0, "Repayment", "31 January 2025", 0.0, 800, 10.26, 0.0, 0.0, 0.0, 89.74, false), //
                    transaction(800.0, "Disbursement", disbursementDay, 800.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false));

        });
    }

    private PostLoanProductsRequest loanProductsRequestInterestDecliningBalanceDailyRecalculationCompoundingNoneAccrualActivity() {
        String name = Utils.uniqueRandomStringGenerator("LOAN_PRODUCT_", 6);
        String shortName = Utils.uniqueRandomStringGenerator("", 4);

        List<Integer> principalVariationsForBorrowerCycle = new ArrayList<>();
        List<Integer> numberOfRepaymentVariationsForBorrowerCycle = new ArrayList<>();
        List<Integer> interestRateVariationsForBorrowerCycle = new ArrayList<>();
        List<LoanProductChargeData> charges = new ArrayList<>();
        List<LoanProductChargeToGLAccountMapper> penaltyToIncomeAccountMappings = new ArrayList<>();
        List<LoanProductChargeToGLAccountMapper> feeToIncomeAccountMappings = new ArrayList<>();

        List<GetLoanPaymentChannelToFundSourceMappings> paymentChannelToFundSourceMappings = new ArrayList<>();
        GetLoanPaymentChannelToFundSourceMappings loanPaymentChannelToFundSourceMappings = new GetLoanPaymentChannelToFundSourceMappings();
        loanPaymentChannelToFundSourceMappings.fundSourceAccountId(fundSource.getAccountID().longValue());
        loanPaymentChannelToFundSourceMappings.paymentTypeId(1L);
        paymentChannelToFundSourceMappings.add(loanPaymentChannelToFundSourceMappings);

        final Integer fundId = createFund(requestSpec, responseSpec);
        Assertions.assertNotNull(fundId);

        final Integer delinquencyBucketId = DelinquencyBucketsHelper.createDelinquencyBucket(requestSpec, responseSpec);
        Assertions.assertNotNull(delinquencyBucketId);

        return new PostLoanProductsRequest()//
                .name(name)//
                .enableAccrualActivityPosting(true)//
                .shortName(shortName)//
                .description(
                        "LP1 with 12% DECLINING BALANCE interest, interest period: Daily, Interest recalculation-Daily, Compounding:none")//
                .fundId(fundId.longValue())//
                .startDate(null)//
                .closeDate(null)//
                .includeInBorrowerCycle(false)//
                .currencyCode("EUR")//
                .digitsAfterDecimal(2)//
                .inMultiplesOf(1)//
                .installmentAmountInMultiplesOf(1)//
                .useBorrowerCycle(false)//
                .minPrincipal(100.0)//
                .principal(1000.0)//
                .maxPrincipal(10000.0)//
                .minNumberOfRepayments(1)//
                .numberOfRepayments(1)//
                .maxNumberOfRepayments(30)//
                .isLinkedToFloatingInterestRates(false)//
                .minInterestRatePerPeriod(0.0)//
                .interestRatePerPeriod(12.0)//
                .maxInterestRatePerPeriod(30.0)//
                .interestRateFrequencyType(2)// Month
                .interestRateFrequencyType(3)//
                .repaymentEvery(30)//
                .repaymentFrequencyType(0L)//
                .principalVariationsForBorrowerCycle(principalVariationsForBorrowerCycle)//
                .numberOfRepaymentVariationsForBorrowerCycle(numberOfRepaymentVariationsForBorrowerCycle)//
                .interestRateVariationsForBorrowerCycle(interestRateVariationsForBorrowerCycle)//
                .amortizationType(1)//
                .interestType(0)//
                .isEqualAmortization(false)//
                .interestCalculationPeriodType(0)//
                .transactionProcessingStrategyCode("mifos-standard-strategy")//
                .daysInYearType(1)//
                .daysInMonthType(1)//
                .canDefineInstallmentAmount(true)//
                .graceOnArrearsAgeing(3)//
                .overdueDaysForNPA(179)//
                .accountMovesOutOfNPAOnlyOnArrearsCompletion(false)//
                .principalThresholdForLastInstallment(50)//
                .allowVariableInstallments(false)//
                .canUseForTopup(false)//
                .holdGuaranteeFunds(false)//
                .multiDisburseLoan(false)//
                .allowAttributeOverrides(new AllowAttributeOverrides()//
                        .amortizationType(true)//
                        .interestType(true)//
                        .transactionProcessingStrategyCode(true)//
                        .interestCalculationPeriodType(true)//
                        .inArrearsTolerance(true)//
                        .repaymentEvery(true)//
                        .graceOnPrincipalAndInterestPayment(true)//
                        .graceOnArrearsAgeing(true))
                .outstandingLoanBalance(10000.0)//
                .charges(charges)//
                .accountingRule(3)//

                .fundSourceAccountId(suspenseAccount.getAccountID().longValue())//
                .loanPortfolioAccountId(loansReceivableAccount.getAccountID().longValue())//
                .transfersInSuspenseAccountId(suspenseAccount.getAccountID().longValue())//
                .interestOnLoanAccountId(interestIncomeAccount.getAccountID().longValue())//
                .incomeFromFeeAccountId(feeIncomeAccount.getAccountID().longValue())//
                .incomeFromPenaltyAccountId(penaltyIncomeAccount.getAccountID().longValue())//
                .incomeFromRecoveryAccountId(recoveriesAccount.getAccountID().longValue())//
                .writeOffAccountId(writtenOffAccount.getAccountID().longValue())//
                .overpaymentLiabilityAccountId(overpaymentAccount.getAccountID().longValue())//
                .receivableInterestAccountId(interestReceivableAccount.getAccountID().longValue())//
                .receivableFeeAccountId(feeReceivableAccount.getAccountID().longValue())//
                .receivablePenaltyAccountId(penaltyReceivableAccount.getAccountID().longValue())//
                .goodwillCreditAccountId(goodwillExpenseAccount.getAccountID().longValue())//
                .incomeFromGoodwillCreditInterestAccountId(interestIncomeChargeOffAccount.getAccountID().longValue())//
                .incomeFromGoodwillCreditFeesAccountId(feeChargeOffAccount.getAccountID().longValue())//
                .incomeFromGoodwillCreditPenaltyAccountId(feeChargeOffAccount.getAccountID().longValue())//
                .incomeFromChargeOffInterestAccountId(interestIncomeChargeOffAccount.getAccountID().longValue())//
                .incomeFromChargeOffFeesAccountId(feeChargeOffAccount.getAccountID().longValue())//
                .chargeOffExpenseAccountId(chargeOffExpenseAccount.getAccountID().longValue())//
                .chargeOffFraudExpenseAccountId(chargeOffFraudExpenseAccount.getAccountID().longValue())//
                .incomeFromChargeOffPenaltyAccountId(penaltyChargeOffAccount.getAccountID().longValue())//

                .dateFormat("dd MMMM yyyy")//
                .locale("en")//
                .disallowExpectedDisbursements(false)//
                .allowApprovedDisbursedAmountsOverApplied(false)//
                .delinquencyBucketId(delinquencyBucketId.longValue())//
                .paymentChannelToFundSourceMappings(paymentChannelToFundSourceMappings)//
                .penaltyToIncomeAccountMappings(penaltyToIncomeAccountMappings)//
                .feeToIncomeAccountMappings(feeToIncomeAccountMappings)//
                .isInterestRecalculationEnabled(true)//
                .preClosureInterestCalculationStrategy(1)//
                .rescheduleStrategyMethod(3)//
                .interestRecalculationCompoundingMethod(0)//
                .recalculationRestFrequencyType(2)//
                .recalculationRestFrequencyInterval(1)//
                .allowPartialPeriodInterestCalcualtion(false);//

    }

    private void chargeFee(Long loanId, Double amount, String dueDate) {
        LOG.info("Charge FEE amount {} dueDate {}", amount, dueDate);
        PostChargesResponse feeCharge = chargesHelper.createCharges(new ChargeRequest().penalty(false).amount(9.0)
                .chargeCalculationType(ChargeCalculationType.FLAT.getValue()).chargeTimeType(ChargeTimeType.SPECIFIED_DUE_DATE.getValue())
                .chargePaymentMode(ChargePaymentMode.REGULAR.getValue()).currencyCode("USD")
                .name(Utils.randomStringGenerator("FEE_" + Calendar.getInstance().getTimeInMillis(), 5)).chargeAppliesTo(1).locale("en")
                .active(true));
        PostLoansLoanIdChargesResponse feeLoanChargeResult = loanTransactionHelper.addChargesForLoan(loanId,
                new PostLoansLoanIdChargesRequest().chargeId(feeCharge.getResourceId()).dateFormat(DATETIME_PATTERN).locale("en")
                        .amount(amount).dueDate(dueDate));
        assertNotNull(feeLoanChargeResult);
        assertNotNull(feeLoanChargeResult.getResourceId());
    }

    private void chargePenalty(Long loanId, Double amount, String dueDate) {
        LOG.info("Charge PENALTY amount {} dueDate {}", amount, dueDate);
        PostChargesResponse penaltyCharge = chargesHelper.createCharges(new ChargeRequest().penalty(true).amount(10.0)
                .chargeCalculationType(ChargeCalculationType.FLAT.getValue()).chargeTimeType(ChargeTimeType.SPECIFIED_DUE_DATE.getValue())
                .chargePaymentMode(ChargePaymentMode.REGULAR.getValue()).currencyCode("USD")
                .name(Utils.randomStringGenerator("PENALTY_" + Calendar.getInstance().getTimeInMillis(), 5)).chargeAppliesTo(1).locale("en")
                .active(true));
        PostLoansLoanIdChargesResponse penaltyLoanChargeResult = loanTransactionHelper.addChargesForLoan(loanId,
                new PostLoansLoanIdChargesRequest().chargeId(penaltyCharge.getResourceId()).dateFormat(DATETIME_PATTERN).locale("en")
                        .amount(amount).dueDate(dueDate));
        assertNotNull(penaltyLoanChargeResult);
        assertNotNull(penaltyLoanChargeResult.getResourceId());
    }

    private Long createLoanProductAccountingAccrualPeriodicWithInterest() {
        return createLoanProductAccountingAccrualPeriodicWithInterest(false);
    }

    private Long createLoanProductAccountingAccrualPeriodicWithInterest(boolean isMultiDisburse) {
        String name = Utils.uniqueRandomStringGenerator("LOAN_PRODUCT_", 6);
        String shortName = Utils.uniqueRandomStringGenerator("", 4);
        Long resourceId = loanTransactionHelper.createLoanProduct(new PostLoanProductsRequest().name(name).shortName(shortName)
                .multiDisburseLoan(isMultiDisburse).maxTrancheCount(isMultiDisburse ? 2 : 1).interestType(isMultiDisburse ? 0 : 1)
                .interestCalculationPeriodType(isMultiDisburse ? 0 : 1).disallowExpectedDisbursements(isMultiDisburse)
                .description("Test loan description").currencyCode("USD").digitsAfterDecimal(2).daysInYearType(1).daysInMonthType(1)
                .interestRecalculationCompoundingMethod(0).recalculationRestFrequencyType(1).rescheduleStrategyMethod(1)
                .recalculationRestFrequencyInterval(0).isInterestRecalculationEnabled(false).interestRateFrequencyType(2).locale("en_GB")
                .numberOfRepayments(4).repaymentFrequencyType(2L).interestRatePerPeriod(2.0).repaymentEvery(1).minPrincipal(100.0)
                .principal(1000.0).maxPrincipal(10000000.0).amortizationType(1).dateFormat("dd MMMM yyyy")
                .transactionProcessingStrategyCode(DEFAULT_STRATEGY).accountingRule(3).enableAccrualActivityPosting(true)
                .fundSourceAccountId(fundSource.getAccountID().longValue())//
                .loanPortfolioAccountId(loansReceivableAccount.getAccountID().longValue())//
                .transfersInSuspenseAccountId(suspenseAccount.getAccountID().longValue())//
                .interestOnLoanAccountId(interestIncomeAccount.getAccountID().longValue())//
                .incomeFromFeeAccountId(feeIncomeAccount.getAccountID().longValue())//
                .incomeFromPenaltyAccountId(feeIncomeAccount.getAccountID().longValue())//
                .incomeFromRecoveryAccountId(recoveriesAccount.getAccountID().longValue())//
                .writeOffAccountId(writtenOffAccount.getAccountID().longValue())//
                .overpaymentLiabilityAccountId(overpaymentAccount.getAccountID().longValue())//
                .receivableInterestAccountId(interestReceivableAccount.getAccountID().longValue())//
                .receivableFeeAccountId(feeReceivableAccount.getAccountID().longValue())//
                .receivablePenaltyAccountId(penaltyReceivableAccount.getAccountID().longValue())//
                .goodwillCreditAccountId(goodwillExpenseAccount.getAccountID().longValue())//
                .incomeFromGoodwillCreditInterestAccountId(goodwillIncomeAccount.getAccountID().longValue())//
                .incomeFromGoodwillCreditFeesAccountId(goodwillIncomeAccount.getAccountID().longValue())//
                .incomeFromGoodwillCreditPenaltyAccountId(goodwillIncomeAccount.getAccountID().longValue())//
                .incomeFromChargeOffInterestAccountId(interestIncomeChargeOffAccount.getAccountID().longValue())//
                .incomeFromChargeOffFeesAccountId(feeChargeOffAccount.getAccountID().longValue())//
                .chargeOffExpenseAccountId(chargeOffExpenseAccount.getAccountID().longValue())//
                .chargeOffFraudExpenseAccountId(chargeOffFraudExpenseAccount.getAccountID().longValue())//
                .incomeFromChargeOffPenaltyAccountId(penaltyChargeOffAccount.getAccountID().longValue())//
        ).getResourceId();
        LOG.info("Test Loan Product With Interest Id {} isMultiDisburse {} http://localhost:4200/#/products/loan-products/{1}/general",
                resourceId, isMultiDisburse);
        return resourceId;
    }

    private static Long applyForLoanApplication(final Long clientID, final Long loanProductID, BigDecimal principal,
            String applicationDisbursementDate) {
        final PostLoansRequest loanRequest = new PostLoansRequest() //
                .locale("en_GB").dateFormat("dd MMMM yyyy").expectedDisbursementDate(applicationDisbursementDate)
                .submittedOnDate(applicationDisbursementDate).interestCalculationPeriodType(0).repaymentFrequencyType(0).repaymentEvery(30)
                .principal(BigDecimal.valueOf(1000)).loanTermFrequency(120).loanTermFrequencyType(0)
                .transactionProcessingStrategyCode(DEFAULT_STRATEGY).interestType(0).loanType("individual").numberOfRepayments(4)
                .amortizationType(1).interestRatePerPeriod(BigDecimal.valueOf(2)).clientId(clientID).productId(loanProductID);
        Long loanId = loanTransactionHelper.applyLoan(loanRequest).getLoanId();
        LOG.info("Test Loan http://localhost:4200/#/clients/{}/loans-accounts/{}/transactions", client.getClientId(), loanId);
        return loanId;
    }

    private static Long applyForLoanApplicationWithInterest(final Long clientID, final Long loanProductID, BigDecimal principal,
            String applicationDisbursementDate) {
        final PostLoansRequest loanRequest = new PostLoansRequest() //
                .loanTermFrequency(4).locale("en_GB").loanTermFrequencyType(2).numberOfRepayments(4).repaymentFrequencyType(2)
                .interestRatePerPeriod(BigDecimal.valueOf(2)).repaymentEvery(1).principal(principal).amortizationType(1).interestType(1)
                .interestCalculationPeriodType(1).dateFormat("dd MMMM yyyy").transactionProcessingStrategyCode(DEFAULT_STRATEGY)
                .loanType("individual").expectedDisbursementDate(applicationDisbursementDate).submittedOnDate(applicationDisbursementDate)
                .clientId(clientID).productId(loanProductID);
        Long loanId = loanTransactionHelper.applyLoan(loanRequest).getLoanId();
        LOG.info("Test Loan with Interest Id {2} http://localhost:4200/#/clients/{}/loans-accounts/{}/transactions", client.getClientId(),
                loanId);
        return loanId;
    }

    private static Long applyForLoanApplicationWithInterest(final Long clientID, final Long loanProductID, BigDecimal principal,
            String applicationDisbursementDate, String applicationDisbursementDate2) {
        final PostLoansRequest loanRequest = new PostLoansRequest() //
                .loanTermFrequency(4).locale("en_GB").loanTermFrequencyType(2).numberOfRepayments(4).repaymentFrequencyType(2)
                .interestRatePerPeriod(BigDecimal.valueOf(2)).repaymentEvery(1).principal(principal).amortizationType(1).interestType(0)
                .interestCalculationPeriodType(0).dateFormat("dd MMMM yyyy").transactionProcessingStrategyCode(DEFAULT_STRATEGY)
                .loanType("individual").submittedOnDate(applicationDisbursementDate).expectedDisbursementDate(applicationDisbursementDate2)
                .clientId(clientID).productId(loanProductID);
        Long loanId = loanTransactionHelper.applyLoan(loanRequest).getLoanId();
        LOG.info("Test Loan with Interest Id MultiDisbursed {2} http://localhost:4200/#/clients/{}/loans-accounts/{}/transactions",
                client.getClientId(), loanId);
        return loanId;
    }

    private Long createLoanProductAccountingAccrualPeriodicAdvancedPaymentAllocation() {
        return createLoanProductAccountingAccrualPeriodicAdvancedPaymentAllocation(false);
    }

    private Long createLoanProductAccountingAccrualPeriodicAdvancedPaymentAllocation(boolean isMultiDisburse) {
        Long resourceId = loanTransactionHelper
                .createLoanProduct(loanProductAccountingAccrualAdvanvedPaymentAllocationAccrualActivity(isMultiDisburse)).getResourceId();
        LOG.info("Test Progressive Loan Product Id {} isMultiDisburse {} http://localhost:4200/#/products/loan-products/{1}/general",
                resourceId, isMultiDisburse);
        return resourceId;
    }

    private PostLoanProductsRequest loanProductAccountingAccrualAdvanvedPaymentAllocationAccrualActivity(boolean isMultiDisburse) {
        String name = Utils.uniqueRandomStringGenerator("LOAN_PRODUCT_", 6);
        String shortName = Utils.uniqueRandomStringGenerator("", 4);
        AdvancedPaymentData defaultAllocation = createDefaultPaymentAllocation();
        return new PostLoanProductsRequest().name(name).shortName(shortName).multiDisburseLoan(isMultiDisburse)
                .maxTrancheCount(isMultiDisburse ? 2 : 1).interestType(isMultiDisburse ? 0 : 1)
                .interestCalculationPeriodType(isMultiDisburse ? 0 : 1).disallowExpectedDisbursements(isMultiDisburse)
                .description("Test loan description").currencyCode("USD").digitsAfterDecimal(2).daysInYearType(1).daysInMonthType(1)
                .recalculationRestFrequencyType(1).rescheduleStrategyMethod(1).loanScheduleType(LoanScheduleType.PROGRESSIVE.name())
                .recalculationRestFrequencyInterval(0).locale("en_GB").numberOfRepayments(4).repaymentFrequencyType(2L).repaymentEvery(1)
                .minPrincipal(100.0).principal(1000.0).maxPrincipal(10000000.0).amortizationType(1).interestRatePerPeriod(0.0)
                .interestRateFrequencyType(1).dateFormat("dd MMMM yyyy")
                .transactionProcessingStrategyCode(ADVANCED_PAYMENT_ALLOCATION_STRATEGY).paymentAllocation(List.of(defaultAllocation))
                .accountingRule(3).isInterestRecalculationEnabled(false).enableAccrualActivityPosting(true)
                .fundSourceAccountId(fundSource.getAccountID().longValue())//
                .loanPortfolioAccountId(loansReceivableAccount.getAccountID().longValue())//
                .transfersInSuspenseAccountId(suspenseAccount.getAccountID().longValue())//
                .interestOnLoanAccountId(interestIncomeAccount.getAccountID().longValue())//
                .incomeFromFeeAccountId(feeIncomeAccount.getAccountID().longValue())//
                .incomeFromPenaltyAccountId(feeIncomeAccount.getAccountID().longValue())//
                .incomeFromRecoveryAccountId(recoveriesAccount.getAccountID().longValue())//
                .writeOffAccountId(writtenOffAccount.getAccountID().longValue())//
                .overpaymentLiabilityAccountId(overpaymentAccount.getAccountID().longValue())//
                .receivableInterestAccountId(interestReceivableAccount.getAccountID().longValue())//
                .receivableFeeAccountId(feeReceivableAccount.getAccountID().longValue())//
                .receivablePenaltyAccountId(penaltyReceivableAccount.getAccountID().longValue())//
                .goodwillCreditAccountId(goodwillExpenseAccount.getAccountID().longValue())//
                .incomeFromGoodwillCreditInterestAccountId(goodwillIncomeAccount.getAccountID().longValue())//
                .incomeFromGoodwillCreditFeesAccountId(goodwillIncomeAccount.getAccountID().longValue())//
                .incomeFromGoodwillCreditPenaltyAccountId(goodwillIncomeAccount.getAccountID().longValue())//
                .incomeFromChargeOffInterestAccountId(interestIncomeChargeOffAccount.getAccountID().longValue())//
                .incomeFromChargeOffFeesAccountId(feeChargeOffAccount.getAccountID().longValue())//
                .chargeOffExpenseAccountId(chargeOffExpenseAccount.getAccountID().longValue())//
                .chargeOffFraudExpenseAccountId(chargeOffFraudExpenseAccount.getAccountID().longValue())//
                .incomeFromChargeOffPenaltyAccountId(penaltyChargeOffAccount.getAccountID().longValue());//
    }

    private Long createLoanProductAccountingAccrualPeriodicAdvancedPaymentAllocationInterestRecalculation(boolean isMultiDisburse) {
        Long resourceId = loanTransactionHelper
                .createLoanProduct(loanProductAccountingAccrualAdvanvedPaymentAllocationAccrualActivity(isMultiDisburse) //
                        .interestRatePerPeriod(10.0) //
                        .isInterestRecalculationEnabled(true)//
                        .preClosureInterestCalculationStrategy(1) // TILL_PRE_CLOSE_DATE
                        .rescheduleStrategyMethod(4) // ADJUST_LAST_UNPAID_PERIOD
                        .interestRecalculationCompoundingMethod(0) // NONE
                        .recalculationRestFrequencyType(2) // DAILY
                        .recalculationRestFrequencyInterval(1)//
                ).getResourceId();
        LOG.info("Test Progressive Loan Product Id {} isMultiDisburse {} http://localhost:4200/#/products/loan-products/{1}/general",
                resourceId, isMultiDisburse);
        return resourceId;
    }

    private static Long applyForLoanApplicationAdvancedPaymentAllocation(final Long clientID, final Long loanProductID,
            BigDecimal principal, String applicationDisbursementDate, BigDecimal interestRatePerPeriod) {
        final PostLoansRequest loanRequest = new PostLoansRequest() //
                .loanTermFrequency(4).locale("en_GB").loanTermFrequencyType(2).numberOfRepayments(4).repaymentFrequencyType(2)
                .repaymentEvery(1).principal(principal).amortizationType(1).interestType(0).interestRatePerPeriod(interestRatePerPeriod)
                .interestCalculationPeriodType(1).dateFormat("dd MMMM yyyy")
                .transactionProcessingStrategyCode(ADVANCED_PAYMENT_ALLOCATION_STRATEGY).loanType("individual")
                .expectedDisbursementDate(applicationDisbursementDate).submittedOnDate(applicationDisbursementDate).clientId(clientID)
                .productId(loanProductID);
        Long loanId = loanTransactionHelper.applyLoan(loanRequest).getLoanId();
        LOG.info("Test Progressive Loan Id {2} http://localhost:4200/#/clients/{}/loans-accounts/{}/transactions", client.getClientId(),
                loanId);
        return loanId;
    }

    private static Long applyForLoanApplicationAdvancedPaymentAllocation(final Long clientID, final Long loanProductID,
            BigDecimal principal, String applicationDisbursementDate, String applicationDisbursementDate2,
            BigDecimal interestRatePerPeriod) {
        final PostLoansRequest loanRequest = new PostLoansRequest() //
                .loanTermFrequency(4).locale("en_GB").loanTermFrequencyType(2).numberOfRepayments(4).repaymentFrequencyType(2)
                .repaymentEvery(1).principal(principal).amortizationType(1).interestType(0).interestRatePerPeriod(interestRatePerPeriod)
                .interestCalculationPeriodType(0).dateFormat("dd MMMM yyyy")
                .transactionProcessingStrategyCode(ADVANCED_PAYMENT_ALLOCATION_STRATEGY).loanType("individual")
                .submittedOnDate(applicationDisbursementDate).clientId(clientID).expectedDisbursementDate(applicationDisbursementDate2)
                .productId(loanProductID);
        Long loanId = loanTransactionHelper.applyLoan(loanRequest).getLoanId();
        LOG.info("Test Progressive Loan Multi Disbursed Id {2} http://localhost:4200/#/clients/{}/loans-accounts/{}/transactions",
                client.getClientId(), loanId);
        return loanId;
    }

}
