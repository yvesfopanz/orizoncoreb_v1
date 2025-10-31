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

import static java.lang.System.lineSeparator;
import static org.apache.fineract.integrationtests.BaseLoanIntegrationTest.TransactionProcessingStrategyCode.ADVANCED_PAYMENT_ALLOCATION_STRATEGY;
import static org.apache.fineract.integrationtests.common.loans.LoanApplicationTestBuilder.DUE_PENALTY_FEE_INTEREST_PRINCIPAL_IN_ADVANCE_PRINCIPAL_PENALTY_FEE_INTEREST_STRATEGY;
import static org.apache.fineract.integrationtests.common.loans.LoanApplicationTestBuilder.DUE_PENALTY_INTEREST_PRINCIPAL_FEE_IN_ADVANCE_PENALTY_INTEREST_PRINCIPAL_FEE_STRATEGY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.batch.domain.BatchRequest;
import org.apache.fineract.batch.domain.BatchResponse;
import org.apache.fineract.client.models.AdvancedPaymentData;
import org.apache.fineract.client.models.AllowAttributeOverrides;
import org.apache.fineract.client.models.BusinessDateUpdateRequest;
import org.apache.fineract.client.models.GetJournalEntriesTransactionIdResponse;
import org.apache.fineract.client.models.GetLoansLoanIdChargesChargeIdResponse;
import org.apache.fineract.client.models.GetLoansLoanIdRepaymentPeriod;
import org.apache.fineract.client.models.GetLoansLoanIdResponse;
import org.apache.fineract.client.models.GetLoansLoanIdStatus;
import org.apache.fineract.client.models.GetLoansLoanIdTransactions;
import org.apache.fineract.client.models.GetLoansLoanIdTransactionsTemplateResponse;
import org.apache.fineract.client.models.JournalEntryTransactionItem;
import org.apache.fineract.client.models.LoanApprovedAmountHistoryData;
import org.apache.fineract.client.models.LoanPointInTimeData;
import org.apache.fineract.client.models.PaymentAllocationOrder;
import org.apache.fineract.client.models.PostChargesResponse;
import org.apache.fineract.client.models.PostLoanProductsRequest;
import org.apache.fineract.client.models.PostLoansLoanIdChargesRequest;
import org.apache.fineract.client.models.PostLoansLoanIdChargesResponse;
import org.apache.fineract.client.models.PostLoansLoanIdRequest;
import org.apache.fineract.client.models.PostLoansLoanIdResponse;
import org.apache.fineract.client.models.PostLoansLoanIdTransactionsRequest;
import org.apache.fineract.client.models.PostLoansLoanIdTransactionsResponse;
import org.apache.fineract.client.models.PostLoansLoanIdTransactionsTransactionIdRequest;
import org.apache.fineract.client.models.PostLoansRequest;
import org.apache.fineract.client.models.PostLoansResponse;
import org.apache.fineract.client.models.PostRolesRequest;
import org.apache.fineract.client.models.PostUsersRequest;
import org.apache.fineract.client.models.PutGlobalConfigurationsRequest;
import org.apache.fineract.client.models.PutLoansApprovedAmountRequest;
import org.apache.fineract.client.models.PutLoansApprovedAmountResponse;
import org.apache.fineract.client.models.PutLoansAvailableDisbursementAmountRequest;
import org.apache.fineract.client.models.PutLoansAvailableDisbursementAmountResponse;
import org.apache.fineract.client.models.PutLoansLoanIdResponse;
import org.apache.fineract.client.models.PutRolesRoleIdPermissionsRequest;
import org.apache.fineract.client.models.RetrieveLoansPointInTimeRequest;
import org.apache.fineract.client.util.CallFailedRuntimeException;
import org.apache.fineract.client.util.Calls;
import org.apache.fineract.client.util.FineractClient;
import org.apache.fineract.infrastructure.configuration.api.GlobalConfigurationConstants;
import org.apache.fineract.infrastructure.event.external.data.ExternalEventResponse;
import org.apache.fineract.integrationtests.client.IntegrationTest;
import org.apache.fineract.integrationtests.common.BatchHelper;
import org.apache.fineract.integrationtests.common.BusinessDateHelper;
import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.GlobalConfigurationHelper;
import org.apache.fineract.integrationtests.common.SchedulerJobHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.accounting.Account;
import org.apache.fineract.integrationtests.common.accounting.AccountHelper;
import org.apache.fineract.integrationtests.common.accounting.JournalEntryHelper;
import org.apache.fineract.integrationtests.common.charges.ChargesHelper;
import org.apache.fineract.integrationtests.common.error.ErrorResponse;
import org.apache.fineract.integrationtests.common.externalevents.BusinessEvent;
import org.apache.fineract.integrationtests.common.externalevents.ExternalEventHelper;
import org.apache.fineract.integrationtests.common.externalevents.ExternalEventsExtension;
import org.apache.fineract.integrationtests.common.loans.LoanAccountLockHelper;
import org.apache.fineract.integrationtests.common.loans.LoanProductHelper;
import org.apache.fineract.integrationtests.common.loans.LoanProductTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanTestLifecycleExtension;
import org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper;
import org.apache.fineract.integrationtests.common.products.DelinquencyBucketsHelper;
import org.apache.fineract.integrationtests.common.system.CodeHelper;
import org.apache.fineract.integrationtests.inlinecob.InlineLoanCOBHelper;
import org.apache.fineract.integrationtests.useradministration.users.UserHelper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.impl.AdvancedPaymentScheduleTransactionProcessor;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleProcessingType;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleType;
import org.apache.fineract.portfolio.loanproduct.domain.PaymentAllocationType;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import retrofit2.Call;
import retrofit2.Response;

@Slf4j
@ExtendWith({ LoanTestLifecycleExtension.class, ExternalEventsExtension.class })
public abstract class BaseLoanIntegrationTest extends IntegrationTest {

    protected static final String DATETIME_PATTERN = "dd MMMM yyyy";
    protected static final String LOCALE = "en";

    static {
        Utils.initializeRESTAssured();
    }

    protected final ResponseSpecification responseSpec = createResponseSpecification(Matchers.is(200));
    protected final ResponseSpecification responseSpec204 = createResponseSpecification(Matchers.is(204));
    protected final LoanProductHelper loanProductHelper = new LoanProductHelper();
    private final String fullAdminAuthKey = getFullAdminAuthKey();
    protected final RequestSpecification requestSpec = createRequestSpecification(fullAdminAuthKey);
    private final String nonByPassUserAuthKey = getNonByPassUserAuthKey(requestSpec, responseSpec);
    protected final AccountHelper accountHelper = new AccountHelper(requestSpec, responseSpec);
    // asset
    protected final Account loansReceivableAccount = accountHelper.createAssetAccount("loanPortfolio");
    protected final Account interestReceivableAccount = accountHelper.createAssetAccount("interestReceivable");
    protected final Account feeReceivableAccount = accountHelper.createAssetAccount("feeReceivable");
    protected final Account penaltyReceivableAccount = accountHelper.createAssetAccount("penaltyReceivable");
    protected final Account suspenseAccount = accountHelper.createAssetAccount("suspense");
    // liability
    protected final Account fundSource = accountHelper.createLiabilityAccount("fundSource");
    protected final Account overpaymentAccount = accountHelper.createLiabilityAccount("overpayment");
    // income
    protected final Account interestIncomeAccount = accountHelper.createIncomeAccount("interestIncome");
    protected final Account feeIncomeAccount = accountHelper.createIncomeAccount("feeIncome");
    protected final Account penaltyIncomeAccount = accountHelper.createIncomeAccount("penaltyIncome");
    protected final Account feeChargeOffAccount = accountHelper.createIncomeAccount("feeChargeOff");
    protected final Account penaltyChargeOffAccount = accountHelper.createIncomeAccount("penaltyChargeOff");
    protected final Account recoveriesAccount = accountHelper.createIncomeAccount("recoveries");
    protected final Account interestIncomeChargeOffAccount = accountHelper.createIncomeAccount("interestIncomeChargeOff");
    // expense
    protected final Account chargeOffExpenseAccount = accountHelper.createExpenseAccount("chargeOff");
    protected final Account chargeOffFraudExpenseAccount = accountHelper.createExpenseAccount("chargeOffFraud");
    protected final Account writtenOffAccount = accountHelper.createExpenseAccount("writtenOffAccount");
    protected final Account goodwillExpenseAccount = accountHelper.createExpenseAccount("goodwillExpenseAccount");
    protected final Account goodwillIncomeAccount = accountHelper.createIncomeAccount("goodwillIncomeAccount");
    protected final Account deferredIncomeLiabilityAccount = accountHelper.createLiabilityAccount("deferredIncomeLiabilityAccount");
    protected final Account buyDownExpenseAccount = accountHelper.createExpenseAccount("buyDownExpenseAccount");
    protected final LoanTransactionHelper loanTransactionHelper = new LoanTransactionHelper(requestSpec, responseSpec);
    protected JournalEntryHelper journalEntryHelper = new JournalEntryHelper(requestSpec, responseSpec);
    protected ClientHelper clientHelper = new ClientHelper(requestSpec, responseSpec);
    protected SchedulerJobHelper schedulerJobHelper = new SchedulerJobHelper(requestSpec);
    protected final InlineLoanCOBHelper inlineLoanCOBHelper = new InlineLoanCOBHelper(requestSpec, responseSpec);
    protected final LoanAccountLockHelper loanAccountLockHelper = new LoanAccountLockHelper(requestSpec,
            createResponseSpecification(Matchers.is(202)));
    protected BusinessDateHelper businessDateHelper = new BusinessDateHelper();
    protected DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
    protected GlobalConfigurationHelper globalConfigurationHelper = new GlobalConfigurationHelper();
    protected final CodeHelper codeHelper = new CodeHelper();
    protected final ChargesHelper chargesHelper = new ChargesHelper();
    protected final ExternalEventHelper externalEventHelper = new ExternalEventHelper();

    protected static void validateRepaymentPeriod(GetLoansLoanIdResponse loanDetails, Integer index, LocalDate dueDate, double principalDue,
            double principalPaid, double principalOutstanding, double paidInAdvance, double paidLate) {
        GetLoansLoanIdRepaymentPeriod period = loanDetails.getRepaymentSchedule().getPeriods().stream()
                .filter(p -> Objects.equals(p.getPeriod(), index)).findFirst().orElseThrow();
        assertEquals(dueDate, period.getDueDate());
        assertEquals(principalDue, Utils.getDoubleValue(period.getPrincipalDue()));
        assertEquals(principalPaid, Utils.getDoubleValue(period.getPrincipalPaid()));
        assertEquals(principalOutstanding, Utils.getDoubleValue(period.getPrincipalOutstanding()));
        assertEquals(paidInAdvance, Utils.getDoubleValue(period.getTotalPaidInAdvanceForPeriod()));
        assertEquals(paidLate, Utils.getDoubleValue(period.getTotalPaidLateForPeriod()));
    }

    protected static void validateRepaymentPeriod(GetLoansLoanIdResponse loanDetails, Integer index, double principalDue,
            double principalPaid, double principalOutstanding, double paidInAdvance, double paidLate) {
        GetLoansLoanIdRepaymentPeriod period = loanDetails.getRepaymentSchedule().getPeriods().stream()
                .filter(p -> Objects.equals(p.getPeriod(), index)).findFirst().orElseThrow();
        assertEquals(principalDue, Utils.getDoubleValue(period.getPrincipalDue()));
        assertEquals(principalPaid, Utils.getDoubleValue(period.getPrincipalPaid()));
        assertEquals(principalOutstanding, Utils.getDoubleValue(period.getPrincipalOutstanding()));
        assertEquals(paidInAdvance, Utils.getDoubleValue(period.getTotalPaidInAdvanceForPeriod()));
        assertEquals(paidLate, Utils.getDoubleValue(period.getTotalPaidLateForPeriod()));
    }

    protected static void validateFullyUnpaidRepaymentPeriod(GetLoansLoanIdResponse loanDetails, Integer index, String dueDate,
            double principalDue, double feeDue, double penaltyDue, double interestDue) {
        validateRepaymentPeriod(loanDetails, index, LocalDate.parse(dueDate, DateTimeFormatter.ofPattern(DATETIME_PATTERN, Locale.ENGLISH)),
                principalDue, 0, principalDue, feeDue, 0, feeDue, penaltyDue, 0, penaltyDue, interestDue, 0, interestDue, 0, 0);
    }

    protected static void validateFullyPaidRepaymentPeriod(GetLoansLoanIdResponse loanDetails, Integer index, String dueDate,
            double principalDue, double feeDue, double penaltyDue, double interestDue) {
        validateRepaymentPeriod(loanDetails, index, LocalDate.parse(dueDate, DateTimeFormatter.ofPattern(DATETIME_PATTERN, Locale.ENGLISH)),
                principalDue, principalDue, 0, feeDue, feeDue, 0, penaltyDue, penaltyDue, 0, interestDue, interestDue, 0, 0, 0);
    }

    protected static void validateFullyPaidRepaymentPeriod(GetLoansLoanIdResponse loanDetails, Integer index, String dueDate,
            double principalDue, double feeDue, double penaltyDue, double interestDue, double paidLate) {
        validateRepaymentPeriod(loanDetails, index, LocalDate.parse(dueDate, DateTimeFormatter.ofPattern(DATETIME_PATTERN, Locale.ENGLISH)),
                principalDue, principalDue, 0, feeDue, feeDue, 0, penaltyDue, penaltyDue, 0, interestDue, interestDue, 0, 0, paidLate);
    }

    protected static void validateFullyPaidRepaymentPeriod(GetLoansLoanIdResponse loanDetails, Integer index, String dueDate,
            double principalDue, double feeDue, double penaltyDue, double interestDue, double paidLate, double paidInAdvance) {
        validateRepaymentPeriod(loanDetails, index, LocalDate.parse(dueDate, DateTimeFormatter.ofPattern(DATETIME_PATTERN, Locale.ENGLISH)),
                principalDue, principalDue, 0, feeDue, feeDue, 0, penaltyDue, penaltyDue, 0, interestDue, interestDue, 0, paidInAdvance,
                paidLate);
    }

    protected static void validateRepaymentPeriod(GetLoansLoanIdResponse loanDetails, Integer index, LocalDate dueDate, double principalDue,
            double feeDue, double penaltyDue, double interestDue) {
        validateRepaymentPeriod(loanDetails, index, dueDate, principalDue, 0, principalDue, feeDue, 0, feeDue, penaltyDue, 0, penaltyDue,
                interestDue, 0, interestDue, 0, 0);
    }

    protected static void validateRepaymentPeriod(GetLoansLoanIdResponse loanDetails, Integer index, LocalDate dueDate, double principalDue,
            double principalPaid, double principalOutstanding, double feeDue, double feePaid, double feeOutstanding, double penaltyDue,
            double penaltyPaid, double penaltyOutstanding, double interestDue, double interestPaid, double interestOutstanding,
            double paidInAdvance, double paidLate) {
        GetLoansLoanIdRepaymentPeriod period = loanDetails.getRepaymentSchedule().getPeriods().stream()
                .filter(p -> Objects.equals(p.getPeriod(), index)).findFirst().orElseThrow();
        assertEquals(dueDate, period.getDueDate());
        assertEquals(principalDue, Utils.getDoubleValue(period.getPrincipalDue()));
        assertEquals(principalPaid, Utils.getDoubleValue(period.getPrincipalPaid()));
        assertEquals(principalOutstanding, Utils.getDoubleValue(period.getPrincipalOutstanding()));
        assertEquals(feeDue, Utils.getDoubleValue(period.getFeeChargesDue()));
        assertEquals(feePaid, Utils.getDoubleValue(period.getFeeChargesPaid()));
        assertEquals(feeOutstanding, Utils.getDoubleValue(period.getFeeChargesOutstanding()));
        assertEquals(penaltyDue, Utils.getDoubleValue(period.getPenaltyChargesDue()));
        assertEquals(penaltyPaid, Utils.getDoubleValue(period.getPenaltyChargesPaid()));
        assertEquals(penaltyOutstanding, Utils.getDoubleValue(period.getPenaltyChargesOutstanding()));
        assertEquals(interestDue, Utils.getDoubleValue(period.getInterestDue()));
        assertEquals(interestPaid, Utils.getDoubleValue(period.getInterestPaid()));
        assertEquals(interestOutstanding, Utils.getDoubleValue(period.getInterestOutstanding()));
        assertEquals(paidInAdvance, Utils.getDoubleValue(period.getTotalPaidInAdvanceForPeriod()));
        assertEquals(paidLate, Utils.getDoubleValue(period.getTotalPaidLateForPeriod()));
    }

    /**
     * Verifies the loan status by applying the given extractor function to the status of the loan details. This method
     * ensures that the loan details, loan status, and the result of the extractor are not null and asserts that the
     * result of the extractor function is true.
     *
     * @param loanDetails
     *            the loan details object containing the loan status
     * @param extractor
     *            a function that extracts a boolean value from the loan status for verification
     * @throws AssertionError
     *             if any of the following conditions are not met:
     *             <ul>
     *             <li>The loan details object is not null</li>
     *             <li>The loan status in the loan details is not null</li>
     *             <li>The value extracted by the extractor function is not null</li>
     *             <li>The value extracted by the extractor function is true</li>
     *             </ul>
     */
    protected void verifyLoanStatus(GetLoansLoanIdResponse loanDetails, Function<GetLoansLoanIdStatus, Boolean> extractor) {
        Assertions.assertNotNull(loanDetails);
        Assertions.assertNotNull(loanDetails.getStatus());
        Boolean actualValue = extractor.apply(loanDetails.getStatus());
        Assertions.assertNotNull(actualValue);
        Assertions.assertTrue(actualValue);
    }

    protected GetLoansLoanIdTransactionsTemplateResponse getPrepayAmount(Long loanId, String date) {
        return ok(fineractClient().loanTransactions.retrieveTransactionTemplate(loanId, "prepayLoan", DATETIME_PATTERN, date, "en", null));
    }

    protected Long verifyPrepayAmountByRepayment(Long loanId, String date) {
        GetLoansLoanIdTransactionsTemplateResponse prepayAmount = getPrepayAmount(loanId, date);
        Double amountToPrepayLoan = prepayAmount.getAmount();
        Long repaymentId = null;
        if (amountToPrepayLoan != null && amountToPrepayLoan > 0) {
            PostLoansLoanIdTransactionsResponse repayment = loanTransactionHelper.makeLoanRepayment(loanId, "repayment", date,
                    amountToPrepayLoan);
            Assertions.assertNotNull(repayment);
            Assertions.assertNotNull(repayment.getResourceId());
            repaymentId = repayment.getResourceId();
        }
        verifyLoanStatus(loanId, LoanStatus.CLOSED_OBLIGATIONS_MET);
        return repaymentId;
    }

    /**
     * Executes a Loan transaction request by a user created with no permissions then verifies it fails with
     * authentication error. Then alters user permissions to get the given permission then executes the query again. It
     * verifies that it returns with no error.
     *
     * @param loanId
     *            loan id
     * @param postLoansLoanIdTransactionsRequest
     *            transaction request
     * @param command
     *            the command for loan transaction
     * @param permission
     *            the given permission related to the loan transaction
     * @return Result body
     */
    public PostLoansLoanIdTransactionsResponse makeLoanTransactionWithPermissionVerification(final Long loanId,
            PostLoansLoanIdTransactionsRequest postLoansLoanIdTransactionsRequest, final String command, final String permission) {
        return performPermissionTestForRequest(permission, fineractClient -> fineractClient.loanTransactions.executeLoanTransaction(loanId,
                postLoansLoanIdTransactionsRequest, command));
    }

    /**
     * Executes a Loan transaction adjustment request by a user created with no permissions then verifies it fails with
     * authentication error. Then alters user permissions to get the given permission then executes the query again. It
     * verifies that it returns with no error.
     *
     * @param loanId
     *            loan ID
     * @param transactionIdToAdjust
     *            transaction ID to adjust
     * @param postLoansLoanIdTransactionsRequest
     *            transaction request
     * @param command
     *            the command for loan transaction
     * @param permission
     *            the given permission related to the loan transaction
     * @return Result body
     */
    public PostLoansLoanIdTransactionsResponse adjustLoanTransactionWithPermissionVerification(final Long loanId,
            final Long transactionIdToAdjust, PostLoansLoanIdTransactionsTransactionIdRequest postLoansLoanIdTransactionsRequest,
            final String command, final String permission) {
        return performPermissionTestForRequest(permission, fineractClient -> fineractClient.loanTransactions.adjustLoanTransaction(loanId,
                transactionIdToAdjust, postLoansLoanIdTransactionsRequest, command));
    }

    public <T> T performPermissionTestForRequest(final String permission, Function<FineractClient, Call<T>> callback) {
        // create role
        String roleName = Utils.uniqueRandomStringGenerator("TEST_ROLE_", 10);
        Long roleId = Calls
                .ok(fineractClient().roles.createRole(new PostRolesRequest().name(roleName).description("Test role Description")))
                .getResourceId();

        Calls.ok(fineractClient().roles.updateRolePermissions(roleId,
                new PutRolesRoleIdPermissionsRequest().putPermissionsItem(permission, false)));
        // create user with role
        String firstname = "Test";
        String lastname = Utils.uniqueRandomStringGenerator("User", 6);
        String userName = Utils.uniqueRandomStringGenerator("testUserName", 4);
        String password = "AKleRbDhK421$";
        String email = firstname + "." + lastname + "@whatever.mifos.org";
        Calls.ok(fineractClient().users
                .create15(new PostUsersRequest().addRolesItem(roleId).email(email).firstname(firstname).lastname(lastname)
                        .repeatPassword(password).sendPasswordToEmail(false).officeId(1L).username(userName).password(password)));

        // login user
        FineractClient fineractClientOfUser = newFineractClient(userName, password);

        // try to make transaction - should fail
        Response<T> responseFail = Calls.executeU(callback.apply(fineractClientOfUser));
        Assertions.assertEquals(403, responseFail.code());

        // edit role to have permission for transaction
        Calls.ok(fineractClient().roles.updateRolePermissions(roleId,
                new PutRolesRoleIdPermissionsRequest().putPermissionsItem(permission, true)));
        // try to make transaction - should pass
        Response<T> responseOk = Calls.executeU(callback.apply(fineractClientOfUser));
        Assertions.assertEquals(200, responseOk.code());
        return responseOk.body();
    }

    private String getNonByPassUserAuthKey(RequestSpecification requestSpec, ResponseSpecification responseSpec) {
        // creates the user
        UserHelper.getSimpleUserWithoutBypassPermission(requestSpec, responseSpec);
        return Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey(UserHelper.SIMPLE_USER_NAME, UserHelper.SIMPLE_USER_PASSWORD);
    }

    private String getFullAdminAuthKey() {
        return Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey();
    }

    protected PostLoanProductsRequest createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct() {
        return createOnePeriod30DaysPeriodicAccrualProduct((double) 0);
    }

    protected PostLoanProductsRequest create4ICumulative() {
        final Integer delinquencyBucketId = DelinquencyBucketsHelper.createDelinquencyBucket(requestSpec, responseSpec);
        Assertions.assertNotNull(delinquencyBucketId);

        return new PostLoanProductsRequest().name(Utils.uniqueRandomStringGenerator("4I_PROGRESSIVE_", 6))//
                .shortName(Utils.uniqueRandomStringGenerator("", 4))//
                .description("4 installment product - progressive")//
                .includeInBorrowerCycle(false)//
                .useBorrowerCycle(false)//
                .currencyCode("EUR")//
                .digitsAfterDecimal(2)//
                .principal(1000.0)//
                .minPrincipal(100.0)//
                .maxPrincipal(10000.0)//
                .numberOfRepayments(4)//
                .repaymentEvery(1)//
                .repaymentFrequencyType(RepaymentFrequencyType.MONTHS_L)//
                .interestRatePerPeriod(10D)//
                .minInterestRatePerPeriod(0D)//
                .maxInterestRatePerPeriod(120D)//
                .interestRateFrequencyType(InterestRateFrequencyType.YEARS)//
                .isLinkedToFloatingInterestRates(false)//
                .isLinkedToFloatingInterestRates(false)//
                .allowVariableInstallments(false)//
                .amortizationType(AmortizationType.EQUAL_INSTALLMENTS)//
                .interestType(InterestType.DECLINING_BALANCE)//
                .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY)//
                .allowPartialPeriodInterestCalcualtion(false)//
                .creditAllocation(List.of())//
                .overdueDaysForNPA(179)//
                .daysInMonthType(30)//
                .daysInYearType(360)//
                .isInterestRecalculationEnabled(true)//
                .interestRecalculationCompoundingMethod(0)//
                .rescheduleStrategyMethod(RescheduleStrategyMethod.RESCHEDULE_NEXT_REPAYMENTS)//
                .recalculationRestFrequencyType(RecalculationRestFrequencyType.DAILY)//
                .recalculationRestFrequencyInterval(1)//
                .isArrearsBasedOnOriginalSchedule(false)//
                .isCompoundingToBePostedAsTransaction(false)//
                .preClosureInterestCalculationStrategy(1)//
                .allowCompoundingOnEod(false)//
                .canDefineInstallmentAmount(true)//
                .repaymentStartDateType(1)//
                .charges(List.of())//
                .principalVariationsForBorrowerCycle(List.of())//
                .interestRateVariationsForBorrowerCycle(List.of())//
                .numberOfRepaymentVariationsForBorrowerCycle(List.of())//
                .accountingRule(3)//
                .canUseForTopup(false)//
                .fundSourceAccountId(fundSource.getAccountID().longValue())//
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
                .incomeFromChargeOffPenaltyAccountId(penaltyChargeOffAccount.getAccountID().longValue())//
                .chargeOffExpenseAccountId(chargeOffExpenseAccount.getAccountID().longValue())//
                .chargeOffFraudExpenseAccountId(chargeOffFraudExpenseAccount.getAccountID().longValue())//
                .dateFormat(DATETIME_PATTERN)//
                .locale("en")//
                .enableAccrualActivityPosting(false)//
                .multiDisburseLoan(true)//
                .maxTrancheCount(10)//
                .outstandingLoanBalance(10000.0)//
                .disallowExpectedDisbursements(true)//
                .allowApprovedDisbursedAmountsOverApplied(true)//
                .overAppliedCalculationType("percentage")//
                .overAppliedNumber(50)//
                .principalThresholdForLastInstallment(50)//
                .holdGuaranteeFunds(false)//
                .accountMovesOutOfNPAOnlyOnArrearsCompletion(false)//
                .allowAttributeOverrides(new AllowAttributeOverrides()//
                        .amortizationType(true)//
                        .interestType(true)//
                        .transactionProcessingStrategyCode(true)//
                        .interestCalculationPeriodType(true)//
                        .inArrearsTolerance(true)//
                        .repaymentEvery(true)//
                        .graceOnPrincipalAndInterestPayment(true)//
                        .graceOnArrearsAgeing(true)//
                ).isEqualAmortization(false)//
                .delinquencyBucketId(delinquencyBucketId.longValue())//
                .enableDownPayment(false)//
                .enableInstallmentLevelDelinquency(false)//
                .transactionProcessingStrategyCode(
                        LoanProductTestBuilder.DUE_PENALTY_FEE_INTEREST_PRINCIPAL_IN_ADVANCE_PRINCIPAL_PENALTY_FEE_INTEREST_STRATEGY)//
                .loanScheduleType(LoanScheduleType.CUMULATIVE.toString());//
    }

    protected PostLoanProductsRequest create4IProgressive() {
        final Integer delinquencyBucketId = DelinquencyBucketsHelper.createDelinquencyBucket(requestSpec, responseSpec);
        Assertions.assertNotNull(delinquencyBucketId);

        return new PostLoanProductsRequest().name(Utils.uniqueRandomStringGenerator("4I_PROGRESSIVE_", 6))//
                .shortName(Utils.uniqueRandomStringGenerator("", 4))//
                .description("4 installment product - progressive")//
                .includeInBorrowerCycle(false)//
                .useBorrowerCycle(false)//
                .currencyCode("EUR")//
                .digitsAfterDecimal(2)//
                .principal(1000.0)//
                .minPrincipal(100.0)//
                .maxPrincipal(10000.0)//
                .numberOfRepayments(4)//
                .repaymentEvery(1)//
                .repaymentFrequencyType(RepaymentFrequencyType.MONTHS_L)//
                .interestRatePerPeriod(10D)//
                .minInterestRatePerPeriod(0D)//
                .maxInterestRatePerPeriod(120D)//
                .interestRateFrequencyType(InterestRateFrequencyType.YEARS)//
                .isLinkedToFloatingInterestRates(false)//
                .isLinkedToFloatingInterestRates(false)//
                .allowVariableInstallments(false)//
                .amortizationType(AmortizationType.EQUAL_INSTALLMENTS)//
                .interestType(InterestType.DECLINING_BALANCE)//
                .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY)//
                .allowPartialPeriodInterestCalcualtion(false)//
                .transactionProcessingStrategyCode(ADVANCED_PAYMENT_ALLOCATION_STRATEGY)//
                .paymentAllocation(List.of(createDefaultPaymentAllocation("NEXT_INSTALLMENT")))//
                .creditAllocation(List.of())//
                .overdueDaysForNPA(179)//
                .daysInMonthType(30)//
                .daysInYearType(360)//
                .isInterestRecalculationEnabled(true)//
                .interestRecalculationCompoundingMethod(0)//
                .rescheduleStrategyMethod(RescheduleStrategyMethod.ADJUST_LAST_UNPAID_PERIOD)//
                .recalculationRestFrequencyType(RecalculationRestFrequencyType.DAILY)//
                .recalculationRestFrequencyInterval(1)//
                .isArrearsBasedOnOriginalSchedule(false)//
                .isCompoundingToBePostedAsTransaction(false)//
                .preClosureInterestCalculationStrategy(1)//
                .allowCompoundingOnEod(false)//
                .canDefineInstallmentAmount(true)//
                .repaymentStartDateType(1)//
                .charges(List.of())//
                .principalVariationsForBorrowerCycle(List.of())//
                .interestRateVariationsForBorrowerCycle(List.of())//
                .numberOfRepaymentVariationsForBorrowerCycle(List.of())//
                .accountingRule(3)//
                .canUseForTopup(false)//
                .fundSourceAccountId(fundSource.getAccountID().longValue())//
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
                .incomeFromChargeOffPenaltyAccountId(penaltyChargeOffAccount.getAccountID().longValue())//
                .chargeOffExpenseAccountId(chargeOffExpenseAccount.getAccountID().longValue())//
                .chargeOffFraudExpenseAccountId(chargeOffFraudExpenseAccount.getAccountID().longValue())//
                .dateFormat(DATETIME_PATTERN)//
                .locale("en")//
                .enableAccrualActivityPosting(false)//
                .multiDisburseLoan(true)//
                .maxTrancheCount(10)//
                .outstandingLoanBalance(10000.0)//
                .disallowExpectedDisbursements(true)//
                .allowApprovedDisbursedAmountsOverApplied(true)//
                .overAppliedCalculationType("percentage")//
                .overAppliedNumber(50)//
                .principalThresholdForLastInstallment(50)//
                .holdGuaranteeFunds(false)//
                .accountMovesOutOfNPAOnlyOnArrearsCompletion(false)//
                .allowAttributeOverrides(new AllowAttributeOverrides()//
                        .amortizationType(true)//
                        .interestType(true)//
                        .transactionProcessingStrategyCode(true)//
                        .interestCalculationPeriodType(true)//
                        .inArrearsTolerance(true)//
                        .repaymentEvery(true)//
                        .graceOnPrincipalAndInterestPayment(true)//
                        .graceOnArrearsAgeing(true)//
                ).isEqualAmortization(false)//
                .delinquencyBucketId(delinquencyBucketId.longValue())//
                .enableDownPayment(false)//
                .enableInstallmentLevelDelinquency(false)//
                .loanScheduleType("PROGRESSIVE")//
                .loanScheduleProcessingType("HORIZONTAL");//
    }

    protected PostLoanProductsRequest create4IProgressiveWithCapitalizedIncome() {
        return create4IProgressive().enableIncomeCapitalization(true)//
                .capitalizedIncomeCalculationType(PostLoanProductsRequest.CapitalizedIncomeCalculationTypeEnum.FLAT)//
                .capitalizedIncomeStrategy(PostLoanProductsRequest.CapitalizedIncomeStrategyEnum.EQUAL_AMORTIZATION)//
                .deferredIncomeLiabilityAccountId(deferredIncomeLiabilityAccount.getAccountID().longValue())//
                .incomeFromCapitalizationAccountId(feeIncomeAccount.getAccountID().longValue())//
                .capitalizedIncomeType(PostLoanProductsRequest.CapitalizedIncomeTypeEnum.FEE);
    }

    // Loan product with proper accounting setup
    protected PostLoanProductsRequest createOnePeriod30DaysPeriodicAccrualProduct(double interestRatePerPeriod) {
        return new PostLoanProductsRequest().name(Utils.uniqueRandomStringGenerator("LOAN_PRODUCT_", 6))//
                .shortName(Utils.uniqueRandomStringGenerator("", 4))//
                .description("Loan Product Description")//
                .includeInBorrowerCycle(false)//
                .currencyCode("USD")//
                .digitsAfterDecimal(2)//
                .inMultiplesOf(0)//
                .installmentAmountInMultiplesOf(1)//
                .useBorrowerCycle(false)//
                .minPrincipal(100.0)//
                .principal(1000.0)//
                .maxPrincipal(100000.0)//
                .minNumberOfRepayments(1)//
                .numberOfRepayments(1)//
                .maxNumberOfRepayments(30)//
                .isLinkedToFloatingInterestRates(false)//
                .minInterestRatePerPeriod((double) 0)//
                .interestRatePerPeriod(interestRatePerPeriod)//
                .maxInterestRatePerPeriod((double) 100)//
                .interestRateFrequencyType(2)//
                .repaymentEvery(30)//
                .repaymentFrequencyType(0L)//
                .amortizationType(1)//
                .interestType(0)//
                .isEqualAmortization(false)//
                .interestCalculationPeriodType(1)//
                .transactionProcessingStrategyCode(
                        LoanProductTestBuilder.DUE_PENALTY_FEE_INTEREST_PRINCIPAL_IN_ADVANCE_PRINCIPAL_PENALTY_FEE_INTEREST_STRATEGY)//
                .loanScheduleType(LoanScheduleType.CUMULATIVE.toString()) //
                .daysInYearType(1)//
                .daysInMonthType(1)//
                .canDefineInstallmentAmount(true)//
                .graceOnArrearsAgeing(3)//
                .overdueDaysForNPA(179)//
                .accountMovesOutOfNPAOnlyOnArrearsCompletion(false)//
                .principalThresholdForLastInstallment(50)//
                .allowVariableInstallments(false)//
                .canUseForTopup(false)//
                .isInterestRecalculationEnabled(false)//
                .holdGuaranteeFunds(false)//
                .multiDisburseLoan(true)//
                .allowAttributeOverrides(new AllowAttributeOverrides()//
                        .amortizationType(true)//
                        .interestType(true)//
                        .transactionProcessingStrategyCode(true)//
                        .interestCalculationPeriodType(true)//
                        .inArrearsTolerance(true)//
                        .repaymentEvery(true)//
                        .graceOnPrincipalAndInterestPayment(true)//
                        .graceOnArrearsAgeing(true))//
                .allowPartialPeriodInterestCalcualtion(true)//
                .maxTrancheCount(10)//
                .outstandingLoanBalance(10000.0)//
                .charges(Collections.emptyList())//
                .accountingRule(3)//
                .fundSourceAccountId(fundSource.getAccountID().longValue())//
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
                .incomeFromChargeOffPenaltyAccountId(penaltyChargeOffAccount.getAccountID().longValue())//
                .chargeOffExpenseAccountId(chargeOffExpenseAccount.getAccountID().longValue())//
                .chargeOffFraudExpenseAccountId(chargeOffFraudExpenseAccount.getAccountID().longValue())//
                .dateFormat(DATETIME_PATTERN)//
                .locale("en_GB")//
                .disallowExpectedDisbursements(true)//
                .allowApprovedDisbursedAmountsOverApplied(true)//
                .overAppliedCalculationType("percentage")//
                .overAppliedNumber(50);
    }

    protected PostLoanProductsRequest createOnePeriod30DaysLongNoInterestPeriodicAccrualProductWithAdvancedPaymentAllocation() {
        String futureInstallmentAllocationRule = "NEXT_INSTALLMENT";
        AdvancedPaymentData defaultAllocation = createDefaultPaymentAllocation(futureInstallmentAllocationRule);

        return createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct() //
                .transactionProcessingStrategyCode("advanced-payment-allocation-strategy")//
                .loanScheduleType(LoanScheduleType.PROGRESSIVE.toString()) //
                .loanScheduleProcessingType(LoanScheduleProcessingType.HORIZONTAL.toString()) //
                .addPaymentAllocationItem(defaultAllocation);
    }

    protected PostLoanProductsRequest createOnePeriod30DaysPeriodicAccrualProductWithAdvancedPaymentAllocationAndInterestRecalculation(
            final double interestRatePerPeriod, final Integer rescheduleStrategyMethod) {
        String futureInstallmentAllocationRule = "NEXT_INSTALLMENT";
        AdvancedPaymentData defaultAllocation = createDefaultPaymentAllocation(futureInstallmentAllocationRule);

        return createOnePeriod30DaysPeriodicAccrualProduct(interestRatePerPeriod) //
                .transactionProcessingStrategyCode("advanced-payment-allocation-strategy")//
                .loanScheduleType(LoanScheduleType.PROGRESSIVE.toString()) //
                .loanScheduleProcessingType(LoanScheduleProcessingType.HORIZONTAL.toString()) //
                .addPaymentAllocationItem(defaultAllocation).enableDownPayment(false) //
                .isInterestRecalculationEnabled(true).interestRecalculationCompoundingMethod(0) //
                .preClosureInterestCalculationStrategy(1).recalculationRestFrequencyType(1).allowPartialPeriodInterestCalcualtion(true) //
                .rescheduleStrategyMethod(rescheduleStrategyMethod);
    }

    protected static List<PaymentAllocationOrder> getPaymentAllocationOrder(PaymentAllocationType... paymentAllocationTypes) {
        AtomicInteger integer = new AtomicInteger(1);
        return Arrays.stream(paymentAllocationTypes).map(pat -> {
            PaymentAllocationOrder paymentAllocationOrder = new PaymentAllocationOrder();
            paymentAllocationOrder.setPaymentAllocationRule(pat.name());
            paymentAllocationOrder.setOrder(integer.getAndIncrement());
            return paymentAllocationOrder;
        }).toList();
    }

    public AdvancedPaymentData createDefaultPaymentAllocation(String futureInstallmentAllocationRule) {
        AdvancedPaymentData advancedPaymentData = new AdvancedPaymentData();
        advancedPaymentData.setTransactionType("DEFAULT");
        advancedPaymentData.setFutureInstallmentAllocationRule(futureInstallmentAllocationRule);

        List<PaymentAllocationOrder> paymentAllocationOrders = getPaymentAllocationOrder(PaymentAllocationType.PAST_DUE_PENALTY,
                PaymentAllocationType.PAST_DUE_FEE, PaymentAllocationType.PAST_DUE_PRINCIPAL, PaymentAllocationType.PAST_DUE_INTEREST,
                PaymentAllocationType.DUE_PENALTY, PaymentAllocationType.DUE_FEE, PaymentAllocationType.DUE_PRINCIPAL,
                PaymentAllocationType.DUE_INTEREST, PaymentAllocationType.IN_ADVANCE_PENALTY, PaymentAllocationType.IN_ADVANCE_FEE,
                PaymentAllocationType.IN_ADVANCE_PRINCIPAL, PaymentAllocationType.IN_ADVANCE_INTEREST);

        advancedPaymentData.setPaymentAllocationOrder(paymentAllocationOrders);
        return advancedPaymentData;
    }

    protected PostLoanProductsRequest create4Period1MonthLongWithoutInterestProduct(String repaymentStrategy) {
        PostLoanProductsRequest productRequest = createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct().multiDisburseLoan(false)//
                .disallowExpectedDisbursements(false)//
                .allowApprovedDisbursedAmountsOverApplied(false)//
                .overAppliedCalculationType(null)//
                .overAppliedNumber(null)//
                .principal(1000.0)//
                .numberOfRepayments(4)//
                .repaymentEvery(1)//
                .repaymentFrequencyType(RepaymentFrequencyType.MONTHS.longValue())//
                .transactionProcessingStrategyCode(repaymentStrategy)//
        ;
        if (AdvancedPaymentScheduleTransactionProcessor.ADVANCED_PAYMENT_ALLOCATION_STRATEGY.equals(repaymentStrategy)) {
            productRequest.loanScheduleType("PROGRESSIVE").loanScheduleProcessingType("HORIZONTAL")
                    .addPaymentAllocationItem(createDefaultPaymentAllocation("NEXT_INSTALLMENT"));
        } else {
            productRequest.loanScheduleType("CUMULATIVE").loanScheduleProcessingType(null).paymentAllocation(null);
        }
        return productRequest;
    }

    protected PostLoanProductsRequest create1InstallmentAmountInMultiplesOf4Period1MonthLongWithInterestAndAmortizationProduct(
            int interestType, int amortizationType) {
        return createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct().multiDisburseLoan(false)//
                .disallowExpectedDisbursements(false)//
                .allowApprovedDisbursedAmountsOverApplied(false)//
                .overAppliedCalculationType(null)//
                .overAppliedNumber(null)//
                .principal(1250.0)//
                .numberOfRepayments(4)//
                .repaymentEvery(1)//
                .repaymentFrequencyType(RepaymentFrequencyType.MONTHS.longValue())//
                .interestType(interestType)//
                .amortizationType(amortizationType);
    }

    private RequestSpecification createRequestSpecification(String authKey) {
        RequestSpecification requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        requestSpec.header("Authorization", "Basic " + authKey);
        requestSpec.header("Fineract-Platform-TenantId", "default");
        return requestSpec;
    }

    protected ResponseSpecification createResponseSpecification(Matcher<Integer> statusCodeMatcher) {
        return new ResponseSpecBuilder().expectStatusCode(statusCodeMatcher).build();
    }

    protected void verifyUndoLastDisbursalShallFail(Long loanId, String expectedError) {
        ResponseSpecification errorResponse = new ResponseSpecBuilder().expectStatusCode(403).build();
        LoanTransactionHelper validationErrorHelper = new LoanTransactionHelper(this.requestSpec, errorResponse);
        CallFailedRuntimeException exception = assertThrows(CallFailedRuntimeException.class, () -> {
            validationErrorHelper.undoLastDisbursalLoan(loanId, new PostLoansLoanIdRequest());
        });
        assertTrue(exception.getMessage().contains(expectedError));
    }

    protected void verifyNoTransactions(Long loanId) {
        verifyTransactions(loanId, (Transaction[]) null);
    }

    protected void verifyTransactions(Long loanId, Transaction... transactions) {
        GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoan(requestSpec, responseSpec, loanId.intValue());
        if (transactions == null || transactions.length == 0) {
            Assertions.assertTrue(loanDetails.getTransactions().isEmpty(), "No transaction is expected on loan " + loanId);
        } else {
            Assertions.assertEquals(transactions.length, loanDetails.getTransactions().size());
            Arrays.stream(transactions).forEach(tr -> {
                Optional<GetLoansLoanIdTransactions> optTx = loanDetails.getTransactions().stream()
                        .filter(item -> Objects.equals(Utils.getDoubleValue(item.getAmount()), tr.amount) //
                                && Objects.equals(item.getType().getValue(), tr.type) //
                                && Objects.equals(item.getDate(), LocalDate.parse(tr.date, dateTimeFormatter)))
                        .findFirst();
                Assertions.assertTrue(optTx.isPresent(), "Required transaction  not found: " + tr + " on loan " + loanId);

                GetLoansLoanIdTransactions tx = optTx.get();

                if (tr.reversed != null) {
                    Assertions.assertEquals(tr.reversed, tx.getManuallyReversed(),
                            "Transaction is not reversed: " + tr + " on loan " + loanId);
                }
            });
        }
    }

    protected void verifyTransactions(final Long loanId, final TransactionExt... transactions) {
        final GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoan(requestSpec, responseSpec, loanId.intValue());
        if (transactions == null || transactions.length == 0) {
            assertNull(loanDetails.getTransactions(), "No transaction is expected on loan " + loanId);
        } else {
            Assertions.assertNotNull(loanDetails.getTransactions());
            Assertions.assertEquals(transactions.length, loanDetails.getTransactions().size(), "Number of transactions on loan " + loanId);

            Arrays.stream(transactions).forEach(tr -> {
                final List<GetLoansLoanIdTransactions> transactionsByDate = loanDetails.getTransactions().stream()
                        .filter(item -> Objects.equals(item.getDate(), LocalDate.parse(tr.date, dateTimeFormatter))).toList();

                if (transactionsByDate.isEmpty()) {
                    Assertions.fail("No transactions found for date " + tr.date + " on loan " + loanId);
                    return;
                }

                final boolean found = transactionsByDate.stream()
                        .anyMatch(item -> Objects.equals(Utils.getDoubleValue(item.getAmount()), tr.amount)
                                && Objects.equals(item.getType().getValue(), tr.type)
                                && Objects.equals(Utils.getDoubleValue(item.getOutstandingLoanBalance()), tr.outstandingPrincipal)
                                && Objects.equals(Utils.getDoubleValue(item.getPrincipalPortion()), tr.principalPortion)
                                && Objects.equals(Utils.getDoubleValue(item.getInterestPortion()), tr.interestPortion)
                                && Objects.equals(Utils.getDoubleValue(item.getFeeChargesPortion()), tr.feePortion)
                                && Objects.equals(Utils.getDoubleValue(item.getPenaltyChargesPortion()), tr.penaltyPortion)
                                && Objects.equals(Utils.getDoubleValue(item.getOverpaymentPortion()), tr.overpaymentPortion)
                                && Objects.equals(Utils.getDoubleValue(item.getUnrecognizedIncomePortion()), tr.unrecognizedPortion));

                if (!found) {
                    final StringBuilder errorMessage = new StringBuilder();
                    errorMessage.append("Required transaction not found: ").append(tr).append(" on loan ").append(loanId);
                    errorMessage.append("\nTransactions found for date ").append(tr.date).append(":");

                    for (int i = 0; i < transactionsByDate.size(); i++) {
                        GetLoansLoanIdTransactions item = transactionsByDate.get(i);
                        errorMessage.append("\n  Transaction ").append(i + 1).append(": ");
                        errorMessage.append("amount=").append(Utils.getDoubleValue(item.getAmount()));
                        errorMessage.append(", type=").append(item.getType().getValue());
                        errorMessage.append(", date=").append(item.getDate().format(dateTimeFormatter));
                        errorMessage.append(", outstandingPrincipal=").append(Utils.getDoubleValue(item.getOutstandingLoanBalance()));
                        errorMessage.append(", principalPortion=").append(Utils.getDoubleValue(item.getPrincipalPortion()));
                        errorMessage.append(", interestPortion=").append(Utils.getDoubleValue(item.getInterestPortion()));
                        errorMessage.append(", feePortion=").append(Utils.getDoubleValue(item.getFeeChargesPortion()));
                        errorMessage.append(", penaltyPortion=").append(Utils.getDoubleValue(item.getPenaltyChargesPortion()));
                        errorMessage.append(", unrecognizedPortion=").append(Utils.getDoubleValue(item.getUnrecognizedIncomePortion()));
                        errorMessage.append(", overpaymentPortion=").append(Utils.getDoubleValue(item.getOverpaymentPortion()));
                        errorMessage.append(", reversed=").append(item.getManuallyReversed() != null ? item.getManuallyReversed() : false);
                    }

                    Assertions.fail(errorMessage.toString());
                }
            });
        }
    }

    protected void verifyArreals(LoanPointInTimeData pointInTimeData, boolean isOverDue, String overdueSince) {
        assertThat(Objects.requireNonNull(pointInTimeData.getArrears()).getOverdue()).isEqualTo(isOverDue);
        if (isOverDue) {
            assertThat(Objects.requireNonNull(pointInTimeData.getArrears().getOverDueSince()).toString()).isEqualTo(overdueSince);
        } else {
            assertThat(pointInTimeData.getArrears().getOverDueSince()).isNull();
        }
    }

    protected void placeHardLockOnLoan(Long loanId) {
        loanAccountLockHelper.placeSoftLockOnLoanAccount(loanId.intValue(), "LOAN_COB_CHUNK_PROCESSING");
    }

    protected void placeHardLockOnLoan(Long loanId, String error) {
        loanAccountLockHelper.placeSoftLockOnLoanAccount(loanId.intValue(), "LOAN_COB_CHUNK_PROCESSING", error);
    }

    protected void executeInlineCOB(Long loanId) {
        inlineLoanCOBHelper.executeInlineCOB(List.of(loanId));
    }

    protected void reAgeLoan(Long loanId, String frequencyType, int frequencyNumber, String startDate, Integer numberOfInstallments) {
        PostLoansLoanIdTransactionsRequest request = new PostLoansLoanIdTransactionsRequest();
        request.setDateFormat(DATETIME_PATTERN);
        request.setLocale("en");
        request.setFrequencyType(frequencyType);
        request.setFrequencyNumber(frequencyNumber);
        request.setStartDate(startDate);
        request.setNumberOfInstallments(numberOfInstallments);
        loanTransactionHelper.reAge(loanId, request);
    }

    protected void reAmortizeLoan(Long loanId) {
        PostLoansLoanIdTransactionsRequest request = new PostLoansLoanIdTransactionsRequest();
        request.setDateFormat(DATETIME_PATTERN);
        request.setLocale("en");
        loanTransactionHelper.reAmortize(loanId, request);
    }

    protected void undoReAgeLoan(Long loanId) {
        loanTransactionHelper.undoReAge(loanId, new PostLoansLoanIdTransactionsRequest());
    }

    protected void undoReAmortizeLoan(Long loanId) {
        loanTransactionHelper.undoReAmortize(loanId, new PostLoansLoanIdTransactionsRequest());
    }

    protected void verifyLastClosedBusinessDate(Long loanId, String lastClosedBusinessDate) {
        GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);
        assertNotNull(loanDetails.getLastClosedBusinessDate());
        Assertions.assertEquals(lastClosedBusinessDate, loanDetails.getLastClosedBusinessDate().format(dateTimeFormatter));
    }

    protected void disburseLoan(Long loanId, BigDecimal amount, String date) {
        log.info("Disbursing loan with id {} with amount {}", loanId, amount);
        loanTransactionHelper.disburseLoan(loanId, new PostLoansLoanIdRequest().actualDisbursementDate(date).dateFormat(DATETIME_PATTERN)
                .transactionAmount(amount).locale("en"));
    }

    protected void undoDisbursement(Integer loanId) {
        loanTransactionHelper.undoDisbursal(loanId);
    }

    protected void undoLastDisbursement(Long loanId) {
        loanTransactionHelper.undoLastDisbursalLoan(loanId, new PostLoansLoanIdRequest());
    }

    protected LoanPointInTimeData getPointInTimeData(Long loanId, String date) {
        return Calls.ok(fineractClient().loansPointInTimeApi.retrieveLoanPointInTime(loanId, date, DATETIME_PATTERN, "en"));
    }

    protected List<LoanPointInTimeData> getPointInTimeData(List<Long> loanIds, String date) {
        RetrieveLoansPointInTimeRequest request = new RetrieveLoansPointInTimeRequest().loanIds(loanIds).date(date)
                .dateFormat(DATETIME_PATTERN).locale("en");
        return Calls.ok(fineractClient().loansPointInTimeApi.retrieveLoansPointInTime(request));
    }

    protected PutLoansApprovedAmountResponse modifyLoanApprovedAmount(Long loanId, BigDecimal approvedAmount) {
        PutLoansApprovedAmountRequest request = new PutLoansApprovedAmountRequest().amount(approvedAmount).locale("en");
        return Calls.ok(fineractClient().loans.modifyLoanApprovedAmount(loanId, request));
    }

    protected List<LoanApprovedAmountHistoryData> getLoanApprovedAmountHistory(Long loanId) {
        return Calls.ok(fineractClient().loans.getLoanApprovedAmountHistory(loanId));
    }

    protected PutLoansAvailableDisbursementAmountResponse modifyLoanAvailableDisbursementAmount(Long loanId, BigDecimal approvedAmount) {
        PutLoansAvailableDisbursementAmountRequest request = new PutLoansAvailableDisbursementAmountRequest().amount(approvedAmount)
                .locale("en");
        return Calls.ok(fineractClient().loans.modifyLoanAvailableDisbursementAmount(loanId, request));
    }

    protected void verifyOutstanding(LoanPointInTimeData loan, OutstandingAmounts outstanding) {
        assertThat(BigDecimal.valueOf(outstanding.principalOutstanding))
                .isEqualByComparingTo(loan.getPrincipal().getPrincipalOutstanding());
        assertThat(BigDecimal.valueOf(outstanding.interestOutstanding)).isEqualByComparingTo(loan.getInterest().getInterestOutstanding());
        assertThat(BigDecimal.valueOf(outstanding.feeOutstanding)).isEqualByComparingTo(loan.getFee().getFeeChargesOutstanding());
        assertThat(BigDecimal.valueOf(outstanding.penaltyOutstanding))
                .isEqualByComparingTo(loan.getPenalty().getPenaltyChargesOutstanding());
        assertThat(BigDecimal.valueOf(outstanding.totalOutstanding)).isEqualByComparingTo(loan.getTotal().getTotalOutstanding());
    }

    // Note: this is buggy because if multiple journal entries are for the same account, amount and type, the
    // verification will pass
    // not all journal entries have been validated - since there might be duplicates
    protected void verifyJournalEntries(Long loanId, Journal... entries) {
        GetJournalEntriesTransactionIdResponse journalEntriesForLoan = journalEntryHelper.getJournalEntriesForLoan(loanId);
        Assertions.assertEquals(entries.length, journalEntriesForLoan.getPageItems().size(),
                "Actual is: " + lineSeparator() + journalEntriesForLoan.getPageItems().toString());
        Arrays.stream(entries).forEach(journalEntry -> {
            boolean found = journalEntriesForLoan.getPageItems().stream()
                    .anyMatch(item -> Objects.equals(item.getAmount(), journalEntry.amount)
                            && Objects.equals(item.getGlAccountId(), journalEntry.account.getAccountID().longValue())
                            && Objects.requireNonNull(item.getEntryType()).getValue().equals(journalEntry.type));
            Assertions.assertTrue(found, "Required journal entry not found: " + journalEntry);
        });
    }

    protected void verifyJournalEntriesSequentially(Long loanId, Journal... entries) {
        GetJournalEntriesTransactionIdResponse journalEntriesForLoan = journalEntryHelper.getJournalEntriesForLoan(loanId);
        List<JournalEntryTransactionItem> sortedJournalEntries = journalEntriesForLoan.getPageItems().stream()
                .sorted(Comparator.comparing(JournalEntryTransactionItem::getId)).toList();
        for (int i = 0; i < entries.length && i < journalEntriesForLoan.getPageItems().size(); i++) {
            Journal journalEntry = entries[i];
            JournalEntryTransactionItem item = sortedJournalEntries.get(i);
            boolean found = Objects.equals(item.getAmount(), journalEntry.amount)
                    && Objects.equals(item.getGlAccountId(), journalEntry.account.getAccountID().longValue())
                    && Objects.requireNonNull(item.getEntryType()).getValue().equals(journalEntry.type);
            assertTrue(found, "Journal entry mismatch at position " + i + "." + lineSeparator() + "Wanted Journal entry: " + journalEntry
                    + lineSeparator() + "Actual Journal entry: " + item);
        }
        if (journalEntriesForLoan.getPageItems().size() > entries.length) {
            fail("Some Journal Entries are not verified. The missing entries are here: "
                    + sortedJournalEntries.subList(entries.length, sortedJournalEntries.size()));
        }
        Assertions.assertEquals(entries.length, journalEntriesForLoan.getPageItems().size(),
                "There were more journal entries expected than actually present.");
    }

    protected void verifyTRJournalEntries(Long transactionId, Journal... entries) {
        Assertions.assertNotNull(transactionId, "transactionId is null");
        GetJournalEntriesTransactionIdResponse journalEntriesForLoan = journalEntryHelper.getJournalEntries("L" + transactionId.toString());
        Assertions.assertEquals(entries.length, journalEntriesForLoan.getPageItems().size());
        Arrays.stream(entries).forEach(journalEntry -> {
            boolean found = journalEntriesForLoan.getPageItems().stream()
                    .anyMatch(item -> Objects.equals(item.getAmount(), journalEntry.amount)
                            && Objects.equals(item.getGlAccountId(), journalEntry.account.getAccountID().longValue())
                            && Objects.requireNonNull(item.getEntryType()).getValue().equals(journalEntry.type));
            Assertions.assertTrue(found, "Required journal entry not found: " + journalEntry);
        });
    }

    protected Long addCharge(Long loanId, boolean isPenalty, double amount, String dueDate) {
        Integer chargeId = ChargesHelper.createCharges(requestSpec, responseSpec,
                ChargesHelper.getLoanSpecifiedDueDateJSON(ChargesHelper.CHARGE_CALCULATION_TYPE_FLAT, String.valueOf(amount), isPenalty));
        assertNotNull(chargeId);
        Integer loanChargeId = this.loanTransactionHelper.addChargesForLoan(loanId.intValue(),
                LoanTransactionHelper.getSpecifiedDueDateChargesForLoanAsJSON(String.valueOf(chargeId), dueDate, String.valueOf(amount)));
        assertNotNull(loanChargeId);
        return loanChargeId.longValue();
    }

    protected Long createDisbursementPercentageCharge(double percentageAmount) {
        Integer chargeId = ChargesHelper.createCharges(requestSpec, responseSpec, ChargesHelper
                .getLoanDisbursementJSON(ChargesHelper.CHARGE_CALCULATION_TYPE_PERCENTAGE_AMOUNT, String.valueOf(percentageAmount)));
        assertNotNull(chargeId);
        return chargeId.longValue();
    }

    protected Long createOverduePenaltyPercentageCharge(double percentageAmount, Integer feeFrequency, int feeInterval) {
        Integer chargeId = ChargesHelper.createCharges(requestSpec, responseSpec,
                ChargesHelper.getLoanOverdueFeeJSONWithCalculationTypePercentageWithFeeInterval(String.valueOf(percentageAmount),
                        feeFrequency, feeInterval));
        assertNotNull(chargeId);
        return chargeId.longValue();
    }

    protected void verifyRepaymentSchedule(GetLoansLoanIdResponse savedLoanResponse, GetLoansLoanIdResponse actualLoanResponse,
            int totalPeriods, int identicalPeriods) {
        List<GetLoansLoanIdRepaymentPeriod> savedPeriods = savedLoanResponse.getRepaymentSchedule().getPeriods();
        List<GetLoansLoanIdRepaymentPeriod> actualPeriods = actualLoanResponse.getRepaymentSchedule().getPeriods();

        assertEquals(totalPeriods, savedPeriods.size(), "Unexpected number of periods in savedPeriods list.");
        assertEquals(totalPeriods, actualPeriods.size(), "Unexpected number of periods in actualPeriods list.");

        verifyPeriodsEquality(savedPeriods, actualPeriods, 0, identicalPeriods, true);

        verifyPeriodsEquality(savedPeriods, actualPeriods, identicalPeriods, totalPeriods, false);
    }

    private void verifyPeriodsEquality(List<GetLoansLoanIdRepaymentPeriod> savedPeriods, List<GetLoansLoanIdRepaymentPeriod> actualPeriods,
            int startIndex, int endIndex, boolean shouldEqual) {
        for (int i = startIndex; i < endIndex; i++) {
            Double savedTotalDue = Utils.getDoubleValue(savedPeriods.get(i).getTotalDueForPeriod());
            Double actualTotalDue = Utils.getDoubleValue(actualPeriods.get(i).getTotalDueForPeriod());

            if (shouldEqual) {
                assertEquals(savedTotalDue, actualTotalDue, String.format(
                        "Period %d should be identical in both responses. Expected: %s, Actual: %s", i + 1, savedTotalDue, actualTotalDue));
            } else {
                assertNotEquals(savedTotalDue, actualTotalDue, String
                        .format("Period %d should differ between responses. Saved: %s, Actual: %s", i + 1, savedTotalDue, actualTotalDue));
            }
        }
    }

    protected void verifyRepaymentSchedule(Long loanId, Installment... installments) {
        GetLoansLoanIdResponse loanResponse = loanTransactionHelper.getLoan(requestSpec, responseSpec, loanId.intValue());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

        assertNotNull(loanResponse.getRepaymentSchedule());
        assertNotNull(loanResponse.getRepaymentSchedule().getPeriods());
        Assertions.assertEquals(installments.length, loanResponse.getRepaymentSchedule().getPeriods().size(),
                "Expected installments are not matching with the installments configured on the loan");

        int installmentNumber = 0;
        for (int i = 0; i < installments.length; i++) {
            GetLoansLoanIdRepaymentPeriod period = loanResponse.getRepaymentSchedule().getPeriods().get(i);
            Double principalDue = Utils.getDoubleValue(period.getPrincipalDue());
            Double amount = installments[i].principalAmount;

            if (installments[i].completed == null) { // this is for the disbursement
                Assertions.assertEquals(amount, Utils.getDoubleValue(period.getPrincipalLoanBalanceOutstanding()),
                        "%d. installment's principal due is different, expected: %.2f, actual: %.2f".formatted(i, amount,
                                Utils.getDoubleValue(period.getPrincipalLoanBalanceOutstanding())));
            } else {
                Assertions.assertEquals(amount, principalDue,
                        "%d. installment's principal due is different, expected: %.2f, actual: %.2f".formatted(i, amount, principalDue));

                Double interestAmount = installments[i].interestAmount;
                Double interestDue = Utils.getDoubleValue(period.getInterestDue());
                if (interestAmount != null) {
                    Assertions.assertEquals(interestAmount, interestDue,
                            "%d. installment's interest due is different, expected: %.2f, actual: %.2f".formatted(i, interestAmount,
                                    interestDue));
                }

                Double feeAmount = installments[i].feeAmount;
                Double feeDue = Utils.getDoubleValue(period.getFeeChargesDue());
                if (feeAmount != null) {
                    Assertions.assertEquals(feeAmount, feeDue,
                            "%d. installment's fee charges due is different, expected: %.2f, actual: %.2f".formatted(i, feeAmount, feeDue));
                }

                Double penaltyAmount = installments[i].penaltyAmount;
                Double penaltyDue = Utils.getDoubleValue(period.getPenaltyChargesDue());
                if (penaltyAmount != null) {
                    Assertions.assertEquals(penaltyAmount, penaltyDue,
                            "%d. installment's penalty charges due is different, expected: %.2f, actual: %.2f".formatted(i, penaltyAmount,
                                    penaltyDue));
                }

                Double outstandingAmount = installments[i].totalOutstandingAmount;
                Double totalOutstanding = Utils.getDoubleValue(period.getTotalOutstandingForPeriod());
                if (outstandingAmount != null) {
                    Assertions.assertEquals(outstandingAmount, totalOutstanding,
                            "%d. installment's total outstanding is different, expected: %.2f, actual: %.2f".formatted(i, outstandingAmount,
                                    totalOutstanding));
                }

                Double outstandingPrincipalExpected = installments[i].outstandingAmounts != null
                        ? installments[i].outstandingAmounts.principalOutstanding
                        : null;
                Double outstandingPrincipal = Utils.getDoubleValue(period.getPrincipalOutstanding());
                if (outstandingPrincipalExpected != null) {
                    Assertions.assertEquals(outstandingPrincipalExpected, outstandingPrincipal,
                            "%d. installment's outstanding principal is different, expected: %.2f, actual: %.2f".formatted(i,
                                    outstandingPrincipalExpected, outstandingPrincipal));
                }

                Double outstandingFeeExpected = installments[i].outstandingAmounts != null
                        ? installments[i].outstandingAmounts.feeOutstanding
                        : null;
                Double outstandingFee = Utils.getDoubleValue(period.getFeeChargesOutstanding());
                if (outstandingFeeExpected != null) {
                    Assertions.assertEquals(outstandingFeeExpected, outstandingFee,
                            "%d. installment's outstanding fee is different, expected: %.2f, actual: %.2f".formatted(i,
                                    outstandingFeeExpected, outstandingFee));
                }

                Double outstandingPenaltyExpected = installments[i].outstandingAmounts != null
                        ? installments[i].outstandingAmounts.penaltyOutstanding
                        : null;
                Double outstandingPenalty = Utils.getDoubleValue(period.getPenaltyChargesOutstanding());
                if (outstandingPenaltyExpected != null) {
                    Assertions.assertEquals(outstandingPenaltyExpected, outstandingPenalty,
                            "%d. installment's outstanding penalty is different, expected: %.2f, actual: %.2f".formatted(i,
                                    outstandingPenaltyExpected, outstandingPenalty));
                }

                Double outstandingTotalExpected = installments[i].outstandingAmounts != null
                        ? installments[i].outstandingAmounts.totalOutstanding
                        : null;
                Double outstandingTotal = Utils.getDoubleValue(period.getTotalOutstandingForPeriod());
                if (outstandingTotalExpected != null) {
                    Assertions.assertEquals(outstandingTotalExpected, outstandingTotal,
                            "%d. installment's total outstanding is different, expected: %.2f, actual: %.2f".formatted(i,
                                    outstandingTotalExpected, outstandingTotal));
                }

                Double loanBalanceExpected = installments[i].loanBalance;
                Double loanBalance = Utils.getDoubleValue(period.getPrincipalLoanBalanceOutstanding());
                if (loanBalanceExpected != null) {
                    Assertions.assertEquals(loanBalanceExpected, loanBalance,
                            "%d. installment's loan balance is different, expected: %.2f, actual: %.2f".formatted(i, loanBalanceExpected,
                                    loanBalance));
                }
                installmentNumber++;
                Assertions.assertEquals(installmentNumber, period.getPeriod());
            }
            Assertions.assertEquals(installments[i].completed, period.getComplete());
            Assertions.assertEquals(LocalDate.parse(installments[i].dueDate, dateTimeFormatter), period.getDueDate());
        }
    }

    protected void runFromToInclusive(String fromDate, String toDate, Runnable runnable) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
        LocalDate startDate = LocalDate.parse(fromDate, format);
        LocalDate endDate = LocalDate.parse(toDate, format);

        LocalDate currentDate = startDate;
        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            runAt(format.format(currentDate), runnable);
            currentDate = currentDate.plusDays(1);
        }
    }

    protected void runAt(String date, Runnable runnable) {
        try {
            globalConfigurationHelper.updateGlobalConfiguration(GlobalConfigurationConstants.ENABLE_BUSINESS_DATE,
                    new PutGlobalConfigurationsRequest().enabled(true));
            businessDateHelper.updateBusinessDate(new BusinessDateUpdateRequest().type(BusinessDateUpdateRequest.TypeEnum.BUSINESS_DATE)
                    .date(date).dateFormat(DATETIME_PATTERN).locale("en"));
            runnable.run();
        } finally {
            globalConfigurationHelper.updateGlobalConfiguration(GlobalConfigurationConstants.ENABLE_BUSINESS_DATE,
                    new PutGlobalConfigurationsRequest().enabled(false));
        }
    }

    protected void runAsNonByPass(Runnable runnable) {
        RequestSpecificationImpl requestSpecImpl = (RequestSpecificationImpl) requestSpec;
        try {
            requestSpecImpl.replaceHeader("Authorization", "Basic " + nonByPassUserAuthKey);
            runnable.run();
        } finally {
            requestSpecImpl.replaceHeader("Authorization", "Basic " + fullAdminAuthKey);
        }
    }

    protected PostLoansRequest applyLoanRequest(Long clientId, Long loanProductId, String loanDisbursementDate, Double amount,
            int numberOfRepayments) {
        return applyLoanRequest(clientId, loanProductId, loanDisbursementDate, amount, numberOfRepayments, null);
    }

    protected PostLoansRequest applyLoanRequest(Long clientId, Long loanProductId, String loanDisbursementDate, Double amount,
            int numberOfRepayments, Consumer<PostLoansRequest> customizer) {

        PostLoansRequest postLoansRequest = new PostLoansRequest().clientId(clientId).productId(loanProductId)
                .expectedDisbursementDate(loanDisbursementDate).dateFormat(DATETIME_PATTERN)
                .transactionProcessingStrategyCode(DUE_PENALTY_INTEREST_PRINCIPAL_FEE_IN_ADVANCE_PENALTY_INTEREST_PRINCIPAL_FEE_STRATEGY)
                .locale("en").submittedOnDate(loanDisbursementDate).amortizationType(1).interestRatePerPeriod(BigDecimal.ZERO)
                .interestCalculationPeriodType(1).interestType(0).repaymentEvery(30).repaymentFrequencyType(0)
                .numberOfRepayments(numberOfRepayments).loanTermFrequency(numberOfRepayments * 30).loanTermFrequencyType(0)
                .maxOutstandingLoanBalance(BigDecimal.valueOf(amount)).principal(BigDecimal.valueOf(amount)).loanType("individual");
        if (customizer != null) {
            customizer.accept(postLoansRequest);
        }
        return postLoansRequest;
    }

    protected PostLoansRequest applyCumulativeLoanRequest(Long clientId, Long loanProductId, String loanDisbursementDate, Double amount,
            Double interestRate, int numberOfRepayments, Consumer<PostLoansRequest> customizer) {

        PostLoansRequest postLoansRequest = new PostLoansRequest().clientId(clientId)
                .transactionProcessingStrategyCode(DUE_PENALTY_FEE_INTEREST_PRINCIPAL_IN_ADVANCE_PRINCIPAL_PENALTY_FEE_INTEREST_STRATEGY)
                .productId(loanProductId).expectedDisbursementDate(loanDisbursementDate).dateFormat(DATETIME_PATTERN).locale("en")
                .submittedOnDate(loanDisbursementDate).amortizationType(1).interestRatePerPeriod(BigDecimal.valueOf(interestRate))
                .numberOfRepayments(numberOfRepayments).principal(BigDecimal.valueOf(amount)).loanTermFrequency(numberOfRepayments)
                .repaymentEvery(1).repaymentFrequencyType(RepaymentFrequencyType.MONTHS)
                .loanTermFrequencyType(RepaymentFrequencyType.MONTHS).interestType(InterestType.DECLINING_BALANCE)
                .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY).loanType("individual");
        if (customizer != null) {
            customizer.accept(postLoansRequest);
        }
        return postLoansRequest;
    }

    protected PostLoansRequest applyLP2ProgressiveLoanRequest(Long clientId, Long loanProductId, String loanDisbursementDate, Double amount,
            Double interestRate, int numberOfRepayments, Consumer<PostLoansRequest> customizer) {

        PostLoansRequest postLoansRequest = new PostLoansRequest().clientId(clientId)
                .transactionProcessingStrategyCode(ADVANCED_PAYMENT_ALLOCATION_STRATEGY).productId(loanProductId)
                .expectedDisbursementDate(loanDisbursementDate).dateFormat(DATETIME_PATTERN).locale("en")
                .submittedOnDate(loanDisbursementDate).amortizationType(1).interestRatePerPeriod(BigDecimal.valueOf(interestRate))
                .numberOfRepayments(numberOfRepayments).principal(BigDecimal.valueOf(amount)).loanTermFrequency(numberOfRepayments)
                .repaymentEvery(1).repaymentFrequencyType(RepaymentFrequencyType.MONTHS)
                .loanTermFrequencyType(RepaymentFrequencyType.MONTHS).interestType(InterestType.DECLINING_BALANCE)
                .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY).loanType("individual");
        if (customizer != null) {
            customizer.accept(postLoansRequest);
        }
        return postLoansRequest;
    }

    protected PostLoansLoanIdRequest approveLoanRequest(Double amount, String approvalDate) {
        return new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(amount)).dateFormat(DATETIME_PATTERN)
                .approvedOnDate(approvalDate).locale("en");
    }

    protected PostLoansLoanIdRequest approveLoanRequest(Double amount, String approvalDate, String expectedDisbursementDate) {
        return new PostLoansLoanIdRequest().approvedLoanAmount(BigDecimal.valueOf(amount))
                .expectedDisbursementDate(expectedDisbursementDate).dateFormat(DATETIME_PATTERN).approvedOnDate(approvalDate).locale("en");
    }

    protected Long applyAndApproveLoan(Long clientId, Long loanProductId, String loanDisbursementDate, Double amount,
            int numberOfRepayments) {
        return applyAndApproveLoan(clientId, loanProductId, loanDisbursementDate, amount, numberOfRepayments, null);
    }

    protected Long applyAndApproveLoan(Long clientId, Long loanProductId, String loanDisbursementDate, Double amount,
            int numberOfRepayments, Consumer<PostLoansRequest> customizer) {
        PostLoansResponse postLoansResponse = loanTransactionHelper
                .applyLoan(applyLoanRequest(clientId, loanProductId, loanDisbursementDate, amount, numberOfRepayments, customizer));

        PostLoansLoanIdResponse approvedLoanResult = loanTransactionHelper.approveLoan(postLoansResponse.getResourceId(),
                approveLoanRequest(amount, loanDisbursementDate));

        return approvedLoanResult.getLoanId();
    }

    protected Long applyAndApproveCumulativeLoan(Long clientId, Long loanProductId, String loanDisbursementDate, Double amount,
            Double interestRate, int numberOfRepayments, Consumer<PostLoansRequest> customizer) {
        PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applyCumulativeLoanRequest(clientId, loanProductId,
                loanDisbursementDate, amount, interestRate, numberOfRepayments, customizer));

        PostLoansLoanIdResponse approvedLoanResult = loanTransactionHelper.approveLoan(postLoansResponse.getResourceId(),
                approveLoanRequest(amount, loanDisbursementDate));

        return approvedLoanResult.getLoanId();
    }

    protected Long applyAndApproveProgressiveLoan(Long clientId, Long loanProductId, String loanDisbursementDate, Double amount,
            Double interestRate, int numberOfRepayments, Consumer<PostLoansRequest> customizer) {
        PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applyLP2ProgressiveLoanRequest(clientId, loanProductId,
                loanDisbursementDate, amount, interestRate, numberOfRepayments, customizer));

        PostLoansLoanIdResponse approvedLoanResult = loanTransactionHelper.approveLoan(postLoansResponse.getResourceId(),
                approveLoanRequest(amount, loanDisbursementDate));

        return approvedLoanResult.getLoanId();
    }

    protected Long applyAndApproveLoan(Long clientId, Long loanProductId, String loanDisbursementDate, Double amount) {
        return applyAndApproveLoan(clientId, loanProductId, loanDisbursementDate, amount, 1);
    }

    protected Long addRepaymentForLoan(Long loanId, Double amount, String date) {
        String firstRepaymentUUID = UUID.randomUUID().toString();
        PostLoansLoanIdTransactionsResponse response = loanTransactionHelper.makeLoanRepayment(loanId,
                new PostLoansLoanIdTransactionsRequest().dateFormat(DATETIME_PATTERN).transactionDate(date).locale("en")
                        .transactionAmount(amount).externalId(firstRepaymentUUID));
        return response.getResourceId();
    }

    protected Long addInterestPaymentWaiverForLoan(Long loanId, Double amount, String date) {
        String firstRepaymentUUID = UUID.randomUUID().toString();
        PostLoansLoanIdTransactionsResponse response = loanTransactionHelper.makeInterestPaymentWaiver(loanId,
                new PostLoansLoanIdTransactionsRequest().dateFormat(DATETIME_PATTERN).transactionDate(date).locale("en")
                        .transactionAmount(amount).externalId(firstRepaymentUUID));
        return response.getResourceId();
    }

    protected Long chargeOffLoan(Long loanId, String date) {
        String randomText = Utils.randomStringGenerator("en", 5) + Utils.randomNumberGenerator(6) + Utils.randomStringGenerator("is", 5);
        Integer chargeOffReasonId = CodeHelper.createChargeOffCodeValue(requestSpec, responseSpec, randomText, 1);
        String transactionExternalId = UUID.randomUUID().toString();

        PostLoansLoanIdTransactionsResponse chargeOffTransaction = this.loanTransactionHelper.chargeOffLoan((long) loanId,
                new PostLoansLoanIdTransactionsRequest().transactionDate(date).locale("en").dateFormat("dd MMMM yyyy")
                        .externalId(transactionExternalId).chargeOffReasonId((long) chargeOffReasonId));
        return chargeOffTransaction.getResourceId();
    }

    protected void changeLoanFraudState(Long loanId, boolean fraudState) {
        String payload = loanTransactionHelper.getLoanFraudPayloadAsJSON("fraud", fraudState ? "true" : "false");
        PutLoansLoanIdResponse response = loanTransactionHelper.modifyLoanCommand(Math.toIntExact(loanId), "markAsFraud", payload,
                responseSpec);
        assertNotNull(response);
    }

    protected Long addChargebackForLoan(Long loanId, Long transactionId, Double amount) {
        PostLoansLoanIdTransactionsResponse response = loanTransactionHelper.chargebackLoanTransaction(loanId, transactionId,
                new PostLoansLoanIdTransactionsTransactionIdRequest().locale("en").transactionAmount(amount).paymentTypeId(1L));
        return response.getResourceId();
    }

    protected PostChargesResponse createCharge(Double amount) {
        String payload = ChargesHelper.getLoanSpecifiedDueDateJSON(ChargesHelper.CHARGE_CALCULATION_TYPE_FLAT, amount.toString(), false);
        return ChargesHelper.createLoanCharge(requestSpec, responseSpec, payload);
    }

    protected PostChargesResponse createCharge(Double amount, String currencyCode) {
        String payload = ChargesHelper.getLoanSpecifiedDueDateJSON(ChargesHelper.CHARGE_CALCULATION_TYPE_FLAT, amount.toString(), false,
                currencyCode);
        return ChargesHelper.createLoanCharge(requestSpec, responseSpec, payload);
    }

    protected PostLoansLoanIdChargesResponse addLoanCharge(Long loanId, Long chargeId, String date, Double amount) {
        String payload = LoanTransactionHelper.getSpecifiedDueDateChargesForLoanAsJSON(chargeId.toString(), date, amount.toString());
        return loanTransactionHelper.addChargeForLoan(loanId.intValue(), payload, responseSpec);
    }

    protected List<GetLoansLoanIdChargesChargeIdResponse> getOverdueInstallmentLoanCharges(Long loanId) {
        return ok(fineractClient().loanCharges.retrieveAllLoanCharges(loanId)).stream() //
                .filter(ch -> ch.getChargeTimeType().getId().intValue() == ChargesHelper.CHARGE_OVERDUE_INSTALLMENT_FEE) //
                .toList(); //
    }

    protected void deactivateOverdueLoanCharges(Long loanId, String fromDueDate) {
        ok(fineractClient().loanCharges.executeLoanCharge(loanId,
                new PostLoansLoanIdChargesRequest().dueDate(fromDueDate).dateFormat(DATETIME_PATTERN).locale("en"), "deactivateOverdue"));
    }

    protected void waiveLoanCharge(Long loanId, Long chargeId, Integer installmentNumber) {
        String payload = LoanTransactionHelper.getWaiveChargeJSON(installmentNumber.toString());
        loanTransactionHelper.waiveChargesForLoan(loanId.intValue(), chargeId.intValue(), payload);
    }

    protected void updateBusinessDate(String date) {
        businessDateHelper.updateBusinessDate(new BusinessDateUpdateRequest().type(BusinessDateUpdateRequest.TypeEnum.BUSINESS_DATE)
                .date(date).dateFormat(DATETIME_PATTERN).locale("en"));
    }

    protected Long getTransactionId(Long loanId, String type, String date) {
        GetLoansLoanIdResponse loan = loanTransactionHelper.getLoan(requestSpec, responseSpec, loanId.intValue());
        return loan.getTransactions().stream().filter(tr -> Objects.equals(tr.getType().getValue(), type)
                && Objects.equals(tr.getDate(), LocalDate.parse(date, dateTimeFormatter))).findAny().orElseThrow().getId();
    }

    protected Journal journalEntry(double amount, Account account, String type) {
        return new Journal(amount, account, type);
    }

    protected Journal debit(Account account, double amount) {
        return new Journal(amount, account, "DEBIT");
    }

    protected Journal credit(Account account, double amount) {
        return new Journal(amount, account, "CREDIT");
    }

    protected Transaction transaction(double amount, String type, String date) {
        return new Transaction(amount, type, date, null);
    }

    protected Transaction reversedTransaction(double principalAmount, String type, String date) {
        return new Transaction(principalAmount, type, date, true);
    }

    protected TransactionExt transaction(double amount, String type, String date, double outstandingPrincipal, double principalPortion,
            double interestPortion, double feePortion, double penaltyPortion, double unrecognizedIncomePortion, double overpaymentPortion) {
        return new TransactionExt(amount, type, date, outstandingPrincipal, principalPortion, interestPortion, feePortion, penaltyPortion,
                unrecognizedIncomePortion, overpaymentPortion, false);
    }

    protected TransactionExt transaction(double amount, String type, String date, double outstandingPrincipal, double principalPortion,
            double interestPortion, double feePortion, double penaltyPortion, double unrecognizedIncomePortion, double overpaymentPortion,
            boolean reversed) {
        return new TransactionExt(amount, type, date, outstandingPrincipal, principalPortion, interestPortion, feePortion, penaltyPortion,
                unrecognizedIncomePortion, overpaymentPortion, reversed);
    }

    protected Installment installment(double principalAmount, Boolean completed, String dueDate) {
        return new Installment(principalAmount, null, null, null, null, completed, dueDate, null, null);
    }

    protected Installment installment(double principalAmount, double interestAmount, double totalOutstandingAmount, Boolean completed,
            String dueDate) {
        return new Installment(principalAmount, interestAmount, null, null, totalOutstandingAmount, completed, dueDate, null, null);
    }

    protected Installment fullyRepaidInstallment(double principalAmount, double interestAmount, String dueDate) {
        return new Installment(principalAmount, interestAmount, null, null, 0.0, true, dueDate, null, null);
    }

    protected Installment unpaidInstallment(double principalAmount, double interestAmount, String dueDate) {
        Double amount = BigDecimal.valueOf(principalAmount).add(BigDecimal.valueOf(interestAmount)).doubleValue();
        return new Installment(principalAmount, interestAmount, null, null, amount, false, dueDate, null, null);
    }

    protected Installment installment(double principalAmount, double interestAmount, double feeAmount, double totalOutstandingAmount,
            Boolean completed, String dueDate) {
        return new Installment(principalAmount, interestAmount, feeAmount, null, totalOutstandingAmount, completed, dueDate, null, null);
    }

    protected Installment installment(double principalAmount, double interestAmount, double feeAmount, double penaltyAmount,
            double totalOutstandingAmount, Boolean completed, String dueDate) {
        return new Installment(principalAmount, interestAmount, feeAmount, penaltyAmount, totalOutstandingAmount, completed, dueDate, null,
                null);
    }

    protected Installment installment(double principalAmount, double interestAmount, double feeAmount, double penaltyAmount,
            OutstandingAmounts outstandingAmounts, Boolean completed, String dueDate) {
        return new Installment(principalAmount, interestAmount, feeAmount, penaltyAmount, null, completed, dueDate, outstandingAmounts,
                null);
    }

    protected Installment installment(double principalAmount, double interestAmount, double feeAmount, double penaltyAmount,
            double totalOutstanding, Boolean completed, String dueDate, double loanBalance) {
        return new Installment(principalAmount, interestAmount, feeAmount, penaltyAmount, totalOutstanding, completed, dueDate, null,
                loanBalance);
    }

    protected OutstandingAmounts outstanding(double principal, double interestOutstanding, double fee, double penalty, double total) {
        return new OutstandingAmounts(principal, interestOutstanding, fee, penalty, total);
    }

    protected BatchRequestBuilder batchRequest() {
        return new BatchRequestBuilder(requestSpec, responseSpec);
    }

    protected void validateLoanSummaryBalances(GetLoansLoanIdResponse loanDetails, Double totalOutstanding, Double totalRepayment,
            Double principalOutstanding, Double principalPaid, Double totalOverpaid) {
        assertEquals(totalOutstanding, Utils.getDoubleValue(loanDetails.getSummary().getTotalOutstanding()));
        assertEquals(totalRepayment, Utils.getDoubleValue(loanDetails.getSummary().getTotalRepayment()));
        assertEquals(principalOutstanding, Utils.getDoubleValue(loanDetails.getSummary().getPrincipalOutstanding()));
        assertEquals(principalPaid, Utils.getDoubleValue(loanDetails.getSummary().getPrincipalPaid()));
        assertEquals(totalOverpaid, Utils.getDoubleValue(loanDetails.getTotalOverpaid()));
    }

    protected void checkMaturityDates(long loanId, LocalDate expectedMaturityDate, LocalDate actualMaturityDate) {
        GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);

        assertEquals(expectedMaturityDate, loanDetails.getTimeline().getExpectedMaturityDate());
        assertEquals(actualMaturityDate, loanDetails.getTimeline().getActualMaturityDate());
    }

    protected void verifyLoanStatus(GetLoansLoanIdResponse loanDetails, LoanStatus loanStatus) {
        assertEquals(loanStatus.getCode(), loanDetails.getStatus().getCode());
    }

    protected void verifyLoanStatus(long loanId, LoanStatus loanStatus) {
        GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);

        assertEquals(loanStatus.getCode(), loanDetails.getStatus().getCode());
    }

    protected void undoLoanApproval(Long loanId) {
        loanTransactionHelper.undoApprovalForLoan(loanId, new PostLoansLoanIdRequest());
    }

    protected void rejectLoan(Long loanId, String rejectedOnDate) {
        loanTransactionHelper.rejectLoan(loanId,
                new PostLoansLoanIdRequest().rejectedOnDate(rejectedOnDate).locale("en").dateFormat(DATETIME_PATTERN));
    }

    protected void verifyBusinessEvents(BusinessEvent... businessEvents) {
        List<ExternalEventResponse> allExternalEvents = ExternalEventHelper.getAllExternalEvents(requestSpec, responseSpec);
        logBusinessEvents(allExternalEvents);
        Assertions.assertNotNull(businessEvents);
        Assertions.assertNotNull(allExternalEvents);
        Assertions.assertTrue(businessEvents.length <= allExternalEvents.size(),
                "Expected business event count is less than actual. Expected: " + businessEvents.length + " Actual: "
                        + allExternalEvents.size());
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN, Locale.ENGLISH);
        for (BusinessEvent businessEvent : businessEvents) {
            long count = allExternalEvents.stream().filter(externalEvent -> businessEvent.verify(externalEvent, formatter)).count();
            Assertions.assertEquals(1, count, "Expected business event not found " + businessEvent);
        }
    }

    protected void logBusinessEvents(List<ExternalEventResponse> allExternalEvents) {
        allExternalEvents.forEach(externalEventDTO -> {
            log.info("Event Received\n type:'{}'\n businessDate:'{}'", externalEventDTO.getType(), externalEventDTO.getBusinessDate());
            if ("org.apache.fineract.avro.loan.v1.LoanTransactionDataV1".equals(externalEventDTO.getSchema())) {
                Object amount = externalEventDTO.getPayLoad().get("amount");
                Object outstandingLoanBalance = externalEventDTO.getPayLoad().get("outstandingLoanBalance");
                Object principalPortion = externalEventDTO.getPayLoad().get("principalPortion");
                Object interestPortion = externalEventDTO.getPayLoad().get("interestPortion");
                Object feePortion = externalEventDTO.getPayLoad().get("feeChargesPortion");
                Object penaltyPortion = externalEventDTO.getPayLoad().get("penaltyChargesPortion");
                Object reversed = externalEventDTO.getPayLoad().get("reversed");
                log.info(
                        "Values\n amount: {}\n outstandingLoanBalance: {}\n principalPortion: {}\n interestPortion: {}\n feePortion: {}\n penaltyPortion: {}\n reversed: {}",
                        amount, outstandingLoanBalance, principalPortion, interestPortion, feePortion, penaltyPortion, reversed);
            } else {
                log.info("Schema: {}", externalEventDTO.getSchema());
            }
        });
    }

    protected void deleteAllExternalEvents() {
        ExternalEventHelper.deleteAllExternalEvents(requestSpec, createResponseSpecification(Matchers.is(204)));
        List<ExternalEventResponse> allExternalEvents = ExternalEventHelper.getAllExternalEvents(requestSpec, responseSpec);
        Assertions.assertEquals(0, allExternalEvents.size());
    }

    @RequiredArgsConstructor
    public static class BatchRequestBuilder {

        private final RequestSpecification requestSpec;
        private final ResponseSpecification responseSpec;
        private List<BatchRequest> requests = new ArrayList<>();

        public BatchRequestBuilder rescheduleLoan(Long requestId, Long loanId, String submittedOnDate, String rescheduleFromDate,
                String adjustedDueDate) {
            BatchRequest bRequest = new BatchRequest();
            bRequest.setRequestId(requestId);
            bRequest.setRelativeUrl("rescheduleloans");
            bRequest.setMethod("POST");

            bRequest.setBody("""
                        {
                            "loanId": %d,
                            "rescheduleFromDate": "%s",
                            "rescheduleReasonId": 1,
                            "submittedOnDate": "%s",
                            "rescheduleReasonComment": "",
                            "adjustedDueDate": "%s",
                            "graceOnPrincipal": "",
                            "graceOnInterest": "",
                            "extraTerms": "",
                            "newInterestRate": "",
                            "dateFormat": "%s",
                            "locale": "en"
                        }
                    """.formatted(loanId, rescheduleFromDate, submittedOnDate, adjustedDueDate, DATETIME_PATTERN));

            requests.add(bRequest);
            return this;
        }

        public BatchRequestBuilder approveRescheduleLoan(Long requestId, Long rescheduleBatchRequestId, String approvedOnDate) {
            BatchRequest bRequest = new BatchRequest();
            bRequest.setRequestId(requestId);
            bRequest.setRelativeUrl("rescheduleloans/$.resourceId?command=approve");
            bRequest.setMethod("POST");
            bRequest.setReference(rescheduleBatchRequestId);

            bRequest.setBody("""
                        {
                            "approvedOnDate": "%s",
                            "dateFormat": "%s",
                            "locale": "en"
                        }
                    """.formatted(approvedOnDate, DATETIME_PATTERN));

            requests.add(bRequest);
            return this;
        }

        public List<BatchResponse> executeEnclosingTransaction() {
            return BatchHelper.postBatchRequestsWithEnclosingTransaction(requestSpec, responseSpec, BatchHelper.toJsonString(requests));
        }

        public ErrorResponse executeEnclosingTransactionError(ResponseSpecification responseSpec) {
            return BatchHelper.postBatchRequestsWithoutEnclosingTransactionError(requestSpec, responseSpec,
                    BatchHelper.toJsonString(requests));
        }
    }

    @ToString
    @AllArgsConstructor
    public static class Transaction {

        Double amount;
        String type;
        String date;
        Boolean reversed;
    }

    @ToString
    @AllArgsConstructor
    public static class TransactionExt {

        Double amount;
        String type;
        String date;
        Double outstandingPrincipal;
        Double principalPortion;
        Double interestPortion;
        Double feePortion;
        Double penaltyPortion;
        Double unrecognizedPortion;
        Double overpaymentPortion;
        Boolean reversed;
    }

    @ToString
    @AllArgsConstructor
    public static class Journal {

        Double amount;
        Account account;
        String type;
    }

    @ToString
    @AllArgsConstructor
    public static class Installment {

        Double principalAmount;
        Double interestAmount;
        Double feeAmount;
        Double penaltyAmount;
        Double totalOutstandingAmount;
        Boolean completed;
        String dueDate;
        OutstandingAmounts outstandingAmounts;
        Double loanBalance;
    }

    @AllArgsConstructor
    @ToString
    public static class OutstandingAmounts {

        Double principalOutstanding;
        Double interestOutstanding;
        Double feeOutstanding;
        Double penaltyOutstanding;
        Double totalOutstanding;
    }

    public static class AmortizationType {

        public static final Integer EQUAL_INSTALLMENTS = 1;
    }

    public static class InterestType {

        public static final Integer DECLINING_BALANCE = 0;
        public static final Integer FLAT = 1;
    }

    public static class InterestRecalculationCompoundingMethod {

        public static final Integer NONE = 0;
    }

    public static class RepaymentFrequencyType {

        public static final Integer MONTHS = 2;
        public static final Long MONTHS_L = 2L;
        public static final String MONTHS_STRING = "MONTHS";
        public static final Integer DAYS = 0;
        public static final Long DAYS_L = 0L;
        public static final String DAYS_STRING = "DAYS";
    }

    public static class RecalculationRestFrequencyType {

        public static final Integer SAME_AS_REPAYMENT_PERIOD = 1;
        public static final Integer DAILY = 2;
    }

    public static class InterestCalculationPeriodType {

        public static final Integer DAILY = 0;
        public static final Integer SAME_AS_REPAYMENT_PERIOD = 1;
    }

    public static class InterestRateFrequencyType {

        public static final Integer MONTHS = 2;
        public static final Integer YEARS = 3;
        public static final Integer WHOLE_TERM = 4;
    }

    public static class TransactionProcessingStrategyCode {

        public static final String ADVANCED_PAYMENT_ALLOCATION_STRATEGY = "advanced-payment-allocation-strategy";
    }

    public static class RescheduleStrategyMethod {

        public static final Integer RESCHEDULE_NEXT_REPAYMENTS = 1;
        public static final Integer REDUCE_EMI_AMOUNT = 3;
        public static final Integer ADJUST_LAST_UNPAID_PERIOD = 4;
    }

    public static class DaysInYearType {

        public static final Integer INVALID = 0;
        public static final Integer ACTUAL = 1;
        public static final Integer DAYS_360 = 360;
        public static final Integer DAYS_364 = 364;
        public static final Integer DAYS_365 = 365;
    }

    public static class DaysInMonthType {

        public static final Integer INVALID = 0;
        public static final Integer ACTUAL = 1;
        public static final Integer DAYS_30 = 30;
    }

    public static class FuturePaymentAllocationRule {

        public static final String LAST_INSTALLMENT = "LAST_INSTALLMENT";
        public static final String NEXT_INSTALLMENT = "NEXT_INSTALLMENT";
        public static final String NEXT_LAST_INSTALLMENT = "NEXT_LAST_INSTALLMENT";

    }

    public static class SupportedInterestRefundTypesItem {

        public static final String MERCHANT_ISSUED_REFUND = "MERCHANT_ISSUED_REFUND";
        public static final String PAYOUT_REFUND = "PAYOUT_REFUND";
    }

    protected static AdvancedPaymentData createDefaultPaymentAllocation() {
        AdvancedPaymentData advancedPaymentData = new AdvancedPaymentData();
        advancedPaymentData.setTransactionType("DEFAULT");
        advancedPaymentData.setFutureInstallmentAllocationRule("NEXT_INSTALLMENT");

        List<PaymentAllocationOrder> paymentAllocationOrders = getPaymentAllocationOrder(PaymentAllocationType.PAST_DUE_PENALTY,
                PaymentAllocationType.PAST_DUE_FEE, PaymentAllocationType.PAST_DUE_PRINCIPAL, PaymentAllocationType.PAST_DUE_INTEREST,
                PaymentAllocationType.DUE_PENALTY, PaymentAllocationType.DUE_FEE, PaymentAllocationType.DUE_PRINCIPAL,
                PaymentAllocationType.DUE_INTEREST, PaymentAllocationType.IN_ADVANCE_PENALTY, PaymentAllocationType.IN_ADVANCE_FEE,
                PaymentAllocationType.IN_ADVANCE_PRINCIPAL, PaymentAllocationType.IN_ADVANCE_INTEREST);

        advancedPaymentData.setPaymentAllocationOrder(paymentAllocationOrders);
        return advancedPaymentData;
    }

    protected static AdvancedPaymentData createPaymentAllocation(String transactionType, String futureInstallmentAllocationRule) {
        AdvancedPaymentData advancedPaymentData = new AdvancedPaymentData();
        advancedPaymentData.setTransactionType(transactionType);
        advancedPaymentData.setFutureInstallmentAllocationRule(futureInstallmentAllocationRule);

        List<PaymentAllocationOrder> paymentAllocationOrders = getPaymentAllocationOrder(PaymentAllocationType.PAST_DUE_PENALTY,
                PaymentAllocationType.PAST_DUE_FEE, PaymentAllocationType.PAST_DUE_PRINCIPAL, PaymentAllocationType.PAST_DUE_INTEREST,
                PaymentAllocationType.DUE_PENALTY, PaymentAllocationType.DUE_FEE, PaymentAllocationType.DUE_PRINCIPAL,
                PaymentAllocationType.DUE_INTEREST, PaymentAllocationType.IN_ADVANCE_PENALTY, PaymentAllocationType.IN_ADVANCE_FEE,
                PaymentAllocationType.IN_ADVANCE_PRINCIPAL, PaymentAllocationType.IN_ADVANCE_INTEREST);

        advancedPaymentData.setPaymentAllocationOrder(paymentAllocationOrders);
        return advancedPaymentData;
    }

    protected static class DaysInYearCustomStrategy {

        public static String FEB_29_PERIOD_ONLY = "FEB_29_PERIOD_ONLY";
        public static String FULL_LEAP_YEAR = "FULL_LEAP_YEAR";
    }

}
