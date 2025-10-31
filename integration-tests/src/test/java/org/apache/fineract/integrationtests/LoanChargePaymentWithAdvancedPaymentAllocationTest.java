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

import static org.apache.fineract.accounting.common.AccountingConstants.FinancialActivity.LIABILITY_TRANSFER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.client.models.AdvancedPaymentData;
import org.apache.fineract.client.models.BusinessDateUpdateRequest;
import org.apache.fineract.client.models.GetLoansLoanIdResponse;
import org.apache.fineract.client.models.PostClientsResponse;
import org.apache.fineract.client.models.PostLoansLoanIdRequest;
import org.apache.fineract.client.models.PostLoansRequest;
import org.apache.fineract.client.models.PostLoansResponse;
import org.apache.fineract.client.models.PutGlobalConfigurationsRequest;
import org.apache.fineract.infrastructure.configuration.api.GlobalConfigurationConstants;
import org.apache.fineract.integrationtests.common.BusinessDateHelper;
import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.CollateralManagementHelper;
import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.SchedulerJobHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.accounting.Account;
import org.apache.fineract.integrationtests.common.accounting.AccountHelper;
import org.apache.fineract.integrationtests.common.accounting.FinancialActivityAccountHelper;
import org.apache.fineract.integrationtests.common.charges.ChargesHelper;
import org.apache.fineract.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanProductTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper;
import org.apache.fineract.integrationtests.common.savings.SavingsAccountHelper;
import org.apache.fineract.integrationtests.common.savings.SavingsProductHelper;
import org.apache.fineract.integrationtests.common.savings.SavingsStatusChecker;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.impl.AdvancedPaymentScheduleTransactionProcessor;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleProcessingType;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
public class LoanChargePaymentWithAdvancedPaymentAllocationTest extends BaseLoanIntegrationTest {

    private static final String DATETIME_PATTERN = "dd MMMM yyyy";
    private static final String ACCOUNT_TYPE_INDIVIDUAL = "INDIVIDUAL";
    private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder().appendPattern(DATETIME_PATTERN).toFormatter();
    private static RequestSpecification requestSpec;
    private static ResponseSpecification responseSpec;
    private static LoanTransactionHelper loanTransactionHelper;
    private static AccountHelper accountHelper;
    private static Integer commonLoanProductId;
    private static PostClientsResponse client;
    private static BusinessDateHelper businessDateHelper;
    private static SchedulerJobHelper scheduleJobHelper;
    private static SavingsAccountHelper savingsAccountHelper;
    private static SavingsProductHelper savingsProductHelper;
    private static FinancialActivityAccountHelper financialActivityAccountHelper;
    private static Integer financialActivityAccountId;
    private static Account liabilityTransferAccount;

    @BeforeAll
    public static void setup() {
        Utils.initializeRESTAssured();
        ClientHelper clientHelper = new ClientHelper(requestSpec, responseSpec);
        requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        requestSpec.header("Fineract-Platform-TenantId", "default");
        responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();

        loanTransactionHelper = new LoanTransactionHelper(requestSpec, responseSpec);
        accountHelper = new AccountHelper(requestSpec, responseSpec);
        final Account assetAccount = accountHelper.createAssetAccount();
        final Account incomeAccount = accountHelper.createIncomeAccount();
        final Account expenseAccount = accountHelper.createExpenseAccount();
        final Account overpaymentAccount = accountHelper.createLiabilityAccount();
        client = clientHelper.createClient(ClientHelper.defaultClientCreationRequest());
        businessDateHelper = new BusinessDateHelper();
        scheduleJobHelper = new SchedulerJobHelper(requestSpec);
        savingsAccountHelper = new SavingsAccountHelper(requestSpec, responseSpec);
        savingsProductHelper = new SavingsProductHelper();
        commonLoanProductId = createLoanProduct("500", "15", "4", assetAccount, incomeAccount, expenseAccount, overpaymentAccount);
        financialActivityAccountHelper = new FinancialActivityAccountHelper(requestSpec);

        List<HashMap> financialActivities = financialActivityAccountHelper.getAllFinancialActivityAccounts(responseSpec);
        if (financialActivities.isEmpty()) {
            /** Setup liability transfer account **/
            /** Create a Liability and an Asset Transfer Account **/
            liabilityTransferAccount = accountHelper.createLiabilityAccount();
            Assertions.assertNotNull(liabilityTransferAccount);

            /*** Create A Financial Activity to Account Mapping **/
            financialActivityAccountId = (Integer) financialActivityAccountHelper.createFinancialActivityAccount(
                    LIABILITY_TRANSFER.getValue(), liabilityTransferAccount.getAccountID(), responseSpec,
                    CommonConstants.RESPONSE_RESOURCE_ID);
            Assertions.assertNotNull(financialActivityAccountId);
        } else {
            boolean existFinancialActivity = false;
            for (HashMap financialActivity : financialActivities) {
                HashMap financialActivityData = (HashMap) financialActivity.get("financialActivityData");
                if (financialActivityData.get("id").equals(FinancialActivityAccountsTest.LIABILITY_TRANSFER_FINANCIAL_ACTIVITY_ID)) {
                    HashMap glAccountData = (HashMap) financialActivity.get("glAccountData");
                    liabilityTransferAccount = new Account((Integer) glAccountData.get("id"), Account.AccountType.LIABILITY);
                    financialActivityAccountId = (Integer) financialActivity.get("id");
                    existFinancialActivity = true;
                    break;
                }
            }
            if (!existFinancialActivity) {
                liabilityTransferAccount = accountHelper.createLiabilityAccount();
                Assertions.assertNotNull(liabilityTransferAccount);

                /*** Create A Financial Activity to Account Mapping **/
                financialActivityAccountId = (Integer) financialActivityAccountHelper.createFinancialActivityAccount(
                        LIABILITY_TRANSFER.getValue(), liabilityTransferAccount.getAccountID(), responseSpec,
                        CommonConstants.RESPONSE_RESOURCE_ID);
                Assertions.assertNotNull(financialActivityAccountId);
            }
        }
    }

    @AfterAll
    public static void tearDown() {
        Integer deletedFinancialActivityAccountId = financialActivityAccountHelper
                .deleteFinancialActivityAccount(financialActivityAccountId, responseSpec, CommonConstants.RESPONSE_RESOURCE_ID);
        Assertions.assertNotNull(deletedFinancialActivityAccountId);
        Assertions.assertEquals(financialActivityAccountId, deletedFinancialActivityAccountId);
    }

    @Test
    public void feeAndPenaltyChargePaymentWithDefaultAllocationRuleTest() {
        try {

            final String jobName = "Transfer Fee For Loans From Savings";
            final String startDate = "10 April 2022";

            globalConfigurationHelper.updateGlobalConfiguration(GlobalConfigurationConstants.ENABLE_BUSINESS_DATE,
                    new PutGlobalConfigurationsRequest().enabled(true));
            businessDateHelper.updateBusinessDate(new BusinessDateUpdateRequest().type(BusinessDateUpdateRequest.TypeEnum.BUSINESS_DATE)
                    .date("2023.02.15").dateFormat("yyyy.MM.dd").locale("en"));

            final Integer savingsId = createSavingsAccountDailyPosting(client.getClientId().intValue(), startDate);

            savingsAccountHelper.depositToSavingsAccount(savingsId, "10000", startDate, CommonConstants.RESPONSE_RESOURCE_ID);

            final PostLoansResponse loanResponse = applyForLoanApplication(client.getClientId(), commonLoanProductId, 1000L, 45, 15, 3,
                    BigDecimal.ZERO, "01 January 2023", "01 January 2023");

            int loanId = loanResponse.getLoanId().intValue();

            loanTransactionHelper.updateLoan(loanId,
                    updateLoanJson(client.getClientId().intValue(), commonLoanProductId, savingsId.toString()));

            loanTransactionHelper.approveLoan(loanResponse.getLoanId(),
                    new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(1000)).dateFormat(DATETIME_PATTERN)
                            .approvedOnDate("01 January 2023").locale("en"));

            loanTransactionHelper.disburseLoan(loanResponse.getLoanId(),
                    new PostLoansLoanIdRequest().actualDisbursementDate("01 January 2023").dateFormat(DATETIME_PATTERN)
                            .transactionAmount(BigDecimal.valueOf(1000.00)).locale("en"));

            final double feePortion = 50.0d;
            final double penaltyPortion = 100.0d;

            Integer fee = ChargesHelper.createCharges(requestSpec, responseSpec,
                    ChargesHelper.getLoanSpecifiedDueDateWithAccountTransferJSON(ChargesHelper.CHARGE_CALCULATION_TYPE_FLAT,
                            String.valueOf(feePortion), false));

            Integer penalty = ChargesHelper.createCharges(requestSpec, responseSpec,
                    ChargesHelper.getLoanSpecifiedDueDateWithAccountTransferJSON(ChargesHelper.CHARGE_CALCULATION_TYPE_FLAT,
                            String.valueOf(penaltyPortion), true));

            LocalDate targetDate = LocalDate.of(2023, 1, 3);
            final String penaltyChargeAddedDate = DATE_FORMATTER.format(targetDate);
            loanTransactionHelper.addChargesForLoan(loanId, LoanTransactionHelper
                    .getSpecifiedDueDateChargesForLoanAsJSON(String.valueOf(fee), penaltyChargeAddedDate, String.valueOf(feePortion)));

            loanTransactionHelper.addChargesForLoan(loanId, LoanTransactionHelper.getSpecifiedDueDateChargesForLoanAsJSON(
                    String.valueOf(penalty), penaltyChargeAddedDate, String.valueOf(penaltyPortion)));

            loanTransactionHelper.noAccrualTransactionForRepayment(loanId);

            GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails((long) loanId);

            assertEquals(5, loanDetails.getRepaymentSchedule().getPeriods().size());
            assertEquals(feePortion, Utils.getDoubleValue(loanDetails.getRepaymentSchedule().getPeriods().get(2).getFeeChargesDue()));
            assertEquals(feePortion,
                    Utils.getDoubleValue(loanDetails.getRepaymentSchedule().getPeriods().get(2).getFeeChargesOutstanding()));
            assertEquals(penaltyPortion,
                    Utils.getDoubleValue(loanDetails.getRepaymentSchedule().getPeriods().get(2).getPenaltyChargesDue()));
            assertEquals(penaltyPortion,
                    Utils.getDoubleValue(loanDetails.getRepaymentSchedule().getPeriods().get(2).getPenaltyChargesOutstanding()));
            assertEquals(400.0d, Utils.getDoubleValue(loanDetails.getRepaymentSchedule().getPeriods().get(2).getTotalDueForPeriod()));
            assertEquals(400.0d,
                    Utils.getDoubleValue(loanDetails.getRepaymentSchedule().getPeriods().get(2).getTotalOutstandingForPeriod()));
            assertEquals(LocalDate.of(2023, 1, 16), loanDetails.getRepaymentSchedule().getPeriods().get(2).getDueDate());

            scheduleJobHelper.executeAndAwaitJob(jobName);

            loanDetails = loanTransactionHelper.getLoanDetails((long) loanId);
            assertEquals(5, loanDetails.getRepaymentSchedule().getPeriods().size());
            assertEquals(feePortion, Utils.getDoubleValue(loanDetails.getRepaymentSchedule().getPeriods().get(2).getFeeChargesDue()));
            assertEquals(0.0d, Utils.getDoubleValue(loanDetails.getRepaymentSchedule().getPeriods().get(2).getFeeChargesOutstanding()));
            assertEquals(penaltyPortion,
                    Utils.getDoubleValue(loanDetails.getRepaymentSchedule().getPeriods().get(2).getPenaltyChargesDue()));
            assertEquals(0.0d, Utils.getDoubleValue(loanDetails.getRepaymentSchedule().getPeriods().get(2).getPenaltyChargesOutstanding()));
            assertEquals(400.0d, Utils.getDoubleValue(loanDetails.getRepaymentSchedule().getPeriods().get(2).getTotalDueForPeriod()));
            assertEquals(250.0d,
                    Utils.getDoubleValue(loanDetails.getRepaymentSchedule().getPeriods().get(2).getTotalOutstandingForPeriod()));
            assertEquals(LocalDate.of(2023, 1, 16), loanDetails.getRepaymentSchedule().getPeriods().get(2).getDueDate());
        } finally {
            globalConfigurationHelper.updateGlobalConfiguration(GlobalConfigurationConstants.ENABLE_BUSINESS_DATE,
                    new PutGlobalConfigurationsRequest().enabled(false));
        }
    }

    private Integer createSavingsAccountDailyPosting(final Integer clientID, String startDate) {
        final Integer savingsProductID = createSavingsProductDailyPosting();
        Assertions.assertNotNull(savingsProductID);
        final Integer savingsId = savingsAccountHelper.applyForSavingsApplicationOnDate(clientID, savingsProductID, ACCOUNT_TYPE_INDIVIDUAL,
                startDate);
        Assertions.assertNotNull(savingsId);
        HashMap savingsStatusHashMap = savingsAccountHelper.approveSavingsOnDate(savingsId, startDate);
        SavingsStatusChecker.verifySavingsIsApproved(savingsStatusHashMap);
        savingsStatusHashMap = savingsAccountHelper.activateSavingsAccount(savingsId, startDate);
        SavingsStatusChecker.verifySavingsIsActive(savingsStatusHashMap);
        return savingsId;
    }

    private Integer createSavingsProductDailyPosting() {
        final String savingsProductJSON = savingsProductHelper.withInterestCompoundingPeriodTypeAsDaily()
                .withInterestPostingPeriodTypeAsMonthly().withInterestCalculationPeriodTypeAsDailyBalance()
                .withMinimumOpenningBalance("10000.0").build();
        return SavingsProductHelper.createSavingsProduct(savingsProductJSON, requestSpec, responseSpec);
    }

    private static Integer createLoanProduct(final String principal, final String repaymentAfterEvery, final String numberOfRepayments,
            final Account... accounts) {
        AdvancedPaymentData defaultAllocation = createDefaultPaymentAllocation();
        AdvancedPaymentData goodwillCreditAllocation = createPaymentAllocation("GOODWILL_CREDIT", "LAST_INSTALLMENT");
        AdvancedPaymentData merchantIssuedRefundAllocation = createPaymentAllocation("MERCHANT_ISSUED_REFUND", "REAMORTIZATION");
        AdvancedPaymentData payoutRefundAllocation = createPaymentAllocation("PAYOUT_REFUND", "NEXT_INSTALLMENT");
        log.info("------------------------------CREATING NEW LOAN PRODUCT ---------------------------------------");
        final String loanProductJSON = new LoanProductTestBuilder().withMinPrincipal(principal).withPrincipal(principal)
                .withRepaymentTypeAsDays().withRepaymentAfterEvery(repaymentAfterEvery).withNumberOfRepayments(numberOfRepayments)
                .withEnableDownPayment(true, "25", true).withinterestRatePerPeriod("0").withInterestRateFrequencyTypeAsMonths()
                .withRepaymentStrategy(AdvancedPaymentScheduleTransactionProcessor.ADVANCED_PAYMENT_ALLOCATION_STRATEGY)
                .withLoanScheduleType(LoanScheduleType.PROGRESSIVE).withLoanScheduleProcessingType(LoanScheduleProcessingType.HORIZONTAL)
                .withAmortizationTypeAsEqualPrincipalPayment().withInterestTypeAsFlat().withAccountingRulePeriodicAccrual(accounts)
                .addAdvancedPaymentAllocation(defaultAllocation, goodwillCreditAllocation, merchantIssuedRefundAllocation,
                        payoutRefundAllocation)
                .withDaysInMonth("30").withDaysInYear("365").withMoratorium("0", "0").build(null);
        return loanTransactionHelper.getLoanProductId(loanProductJSON);
    }

    private static PostLoansResponse applyForLoanApplication(final Long clientId, final Integer loanProductId, final Long principal,
            final int loanTermFrequency, final int repaymentAfterEvery, final int numberOfRepayments, final BigDecimal interestRate,
            final String expectedDisbursementDate, final String submittedOnDate) {
        log.info("--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
        return loanTransactionHelper.applyLoan(new PostLoansRequest().clientId(clientId).productId(loanProductId.longValue())
                .expectedDisbursementDate(expectedDisbursementDate).dateFormat(DATETIME_PATTERN)
                .transactionProcessingStrategyCode(AdvancedPaymentScheduleTransactionProcessor.ADVANCED_PAYMENT_ALLOCATION_STRATEGY)
                .loanScheduleProcessingType(LoanScheduleProcessingType.HORIZONTAL.toString()).locale("en").submittedOnDate(submittedOnDate)
                .amortizationType(1).interestRatePerPeriod(interestRate).interestCalculationPeriodType(1).interestType(0)
                .repaymentFrequencyType(0).repaymentEvery(repaymentAfterEvery).repaymentFrequencyType(0)
                .numberOfRepayments(numberOfRepayments).loanTermFrequency(loanTermFrequency).loanTermFrequencyType(0)
                .principal(BigDecimal.valueOf(principal)).loanType("individual"));
    }

    private String updateLoanJson(final Integer clientID, final Integer loanProductID, String savingsId) {
        log.info("--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
        List<HashMap> collaterals = new ArrayList<>();
        final Integer collateralId = CollateralManagementHelper.createCollateralProduct(this.requestSpec, this.responseSpec);
        Assertions.assertNotNull(collateralId);
        final Integer clientCollateralId = CollateralManagementHelper.createClientCollateral(this.requestSpec, this.responseSpec,
                clientID.toString(), collateralId);
        Assertions.assertNotNull(clientCollateralId);
        addCollaterals(collaterals, clientCollateralId, BigDecimal.valueOf(1));

        final String loanApplicationJSON = new LoanApplicationTestBuilder() //
                .withPrincipal("1,000.00") //
                .withLoanTermFrequency("45") //
                .withLoanTermFrequencyAsDays() //
                .withNumberOfRepayments("3") //
                .withRepaymentEveryAfter("15") //
                .withRepaymentFrequencyTypeAsDays() //
                .withInterestRatePerPeriod("0") //
                .withRepaymentStrategy(AdvancedPaymentScheduleTransactionProcessor.ADVANCED_PAYMENT_ALLOCATION_STRATEGY)
                .withLoanScheduleProcessingType(LoanScheduleProcessingType.HORIZONTAL.toString()) //
                .withAmortizationTypeAsEqualInstallments() //
                .withInterestTypeAsDecliningBalance() //
                .withInterestCalculationPeriodTypeSameAsRepaymentPeriod() //
                .withExpectedDisbursementDate("01 January 2023") //
                .withSubmittedOnDate("01 January 2023") //
                .withCollaterals(collaterals) //
                .build(clientID.toString(), loanProductID.toString(), savingsId);
        return loanApplicationJSON;
    }

    private void addCollaterals(List<HashMap> collaterals, Integer collateralId, BigDecimal quantity) {
        collaterals.add(collaterals(collateralId, quantity));
    }

    private HashMap<String, String> collaterals(Integer collateralId, BigDecimal quantity) {
        HashMap<String, String> collateral = new HashMap<>(2);
        collateral.put("clientCollateralId", collateralId.toString());
        collateral.put("quantity", quantity.toString());
        return collateral;
    }
}
