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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.client.models.BuyDownFeeAmortizationDetails;
import org.apache.fineract.client.models.GetLoansLoanIdResponse;
import org.apache.fineract.client.models.GetLoansLoanIdTransactions;
import org.apache.fineract.client.models.PostClientsResponse;
import org.apache.fineract.client.models.PostLoanProductsRequest;
import org.apache.fineract.client.models.PostLoanProductsResponse;
import org.apache.fineract.client.models.PostLoansLoanIdTransactionsRequest;
import org.apache.fineract.client.models.PostLoansLoanIdTransactionsResponse;
import org.apache.fineract.client.models.PostLoansLoanIdTransactionsTransactionIdRequest;
import org.apache.fineract.client.models.PostLoansResponse;
import org.apache.fineract.client.util.CallFailedRuntimeException;
import org.apache.fineract.integrationtests.common.BusinessStepHelper;
import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.externalevents.BusinessEvent;
import org.apache.fineract.integrationtests.common.externalevents.ExternalEventHelper;
import org.apache.fineract.integrationtests.common.externalevents.LoanAdjustTransactionBusinessEvent;
import org.apache.fineract.integrationtests.common.externalevents.LoanTransactionMinimalBusinessEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for Buy Down Fee functionality in Progressive Loans
 */
@Slf4j
public class LoanBuyDownFeeTest extends BaseLoanIntegrationTest {

    private Long clientId;
    private Long loanId;

    @AfterAll
    public static void teardown() {
        ExternalEventHelper externalEventHelper = new ExternalEventHelper();
        externalEventHelper.disableBusinessEvent("LoanAdjustTransactionBusinessEvent");
        externalEventHelper.disableBusinessEvent("LoanBuyDownFeeTransactionCreatedBusinessEvent");
        externalEventHelper.disableBusinessEvent("LoanBuyDownFeeAdjustmentTransactionCreatedBusinessEvent");
        externalEventHelper.disableBusinessEvent("LoanBuyDownFeeAmortizationTransactionCreatedBusinessEvent");
        externalEventHelper.disableBusinessEvent("LoanBuyDownFeeAmortizationAdjustmentTransactionCreatedBusinessEvent");
    }

    @BeforeEach
    public void beforeEach() {
        new BusinessStepHelper().updateSteps("LOAN_CLOSE_OF_BUSINESS", "APPLY_CHARGE_TO_OVERDUE_LOANS", "LOAN_DELINQUENCY_CLASSIFICATION",
                "CHECK_LOAN_REPAYMENT_DUE", "CHECK_LOAN_REPAYMENT_OVERDUE", "CHECK_DUE_INSTALLMENTS", "UPDATE_LOAN_ARREARS_AGING",
                "ADD_PERIODIC_ACCRUAL_ENTRIES", "ACCRUAL_ACTIVITY_POSTING", "CAPITALIZED_INCOME_AMORTIZATION", "BUY_DOWN_FEE_AMORTIZATION",
                "LOAN_INTEREST_RECALCULATION", "EXTERNAL_ASSET_OWNER_TRANSFER");
        externalEventHelper.enableBusinessEvent("LoanAdjustTransactionBusinessEvent");
        externalEventHelper.enableBusinessEvent("LoanBuyDownFeeTransactionCreatedBusinessEvent");
        externalEventHelper.enableBusinessEvent("LoanBuyDownFeeAdjustmentTransactionCreatedBusinessEvent");
        externalEventHelper.enableBusinessEvent("LoanBuyDownFeeAmortizationTransactionCreatedBusinessEvent");
        externalEventHelper.enableBusinessEvent("LoanBuyDownFeeAmortizationAdjustmentTransactionCreatedBusinessEvent");
        runAt("01 September 2024", () -> {
            clientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();
            final PostLoanProductsResponse loanProductsResponse = loanProductHelper
                    .createLoanProduct(createProgressiveLoanProductWithBuyDownFee());

            // Apply for the loan with proper progressive loan settings
            PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applyLP2ProgressiveLoanRequest(clientId,
                    loanProductsResponse.getResourceId(), "01 September 2024", 1000.0, 10.0, 12, null));
            loanId = postLoansResponse.getLoanId();
            loanTransactionHelper.approveLoan(loanId, approveLoanRequest(1000.0, "01 September 2024"));
            disburseLoan(loanId, BigDecimal.valueOf(1000.0), "01 September 2024");
        });
    }

    @Test
    public void testBuyDownFeeOnProgressiveLoan() {
        runAt("02 September 2024", () -> {
            // Verify loan product has buy down fee enabled
            final GetLoansLoanIdResponse loanDetailsBeforeTransaction = loanTransactionHelper.getLoanDetails(loanId);
            assertNotNull(loanDetailsBeforeTransaction);
            log.info("Loan Product: {}", loanDetailsBeforeTransaction.getLoanProductName());

            deleteAllExternalEvents();
            // Create buy down fee transaction
            Long buyDownFeeTransactionId = addBuyDownFeeForLoan(loanId, 500.0, "02 September 2024");
            verifyBusinessEvents(new BusinessEvent("LoanBuyDownFeeTransactionCreatedBusinessEvent", "02 September 2024"));

            assertNotNull(buyDownFeeTransactionId);

            // Verify transaction was created in loan details
            final GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            assertNotNull(loanDetails);

            // Find the buy down fee transaction
            boolean buyDownFeeFound = false;
            for (GetLoansLoanIdTransactions transaction : loanDetails.getTransactions()) {
                if (transaction.getType() != null && transaction.getType().getId() != null && transaction.getType().getId().equals(40L)) {
                    buyDownFeeFound = true;
                    assertEquals(0, BigDecimal.valueOf(500.0).compareTo(transaction.getAmount()));
                    assertEquals(Long.valueOf(40), transaction.getType().getId());
                    assertEquals("Buy Down Fee", transaction.getType().getValue());
                    break;
                }
            }
            assertTrue(buyDownFeeFound, "Buy down fee transaction should be found in loan transactions");
        });
    }

    @Test
    public void testBuyDownFeeWithNote() {
        runAt("03 September 2024", () -> {
            String externalId = UUID.randomUUID().toString();
            String noteText = "Buy Down Fee - Test Note";

            deleteAllExternalEvents();

            PostLoansLoanIdTransactionsResponse response = loanTransactionHelper.makeLoanBuyDownFee(loanId,
                    new PostLoansLoanIdTransactionsRequest().dateFormat(DATETIME_PATTERN).transactionDate("03 September 2024").locale("en")
                            .transactionAmount(250.0).externalId(externalId).note(noteText));

            assertNotNull(response.getResourceId());
            verifyBusinessEvents(new BusinessEvent("LoanBuyDownFeeTransactionCreatedBusinessEvent", "03 September 2024"));

            // Verify transaction details
            final GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            GetLoansLoanIdTransactions buyDownFeeTransaction = loanDetails.getTransactions().stream()
                    .filter(t -> t.getType() != null && t.getType().getId() != null && t.getType().getId().equals(40L))
                    .filter(t -> externalId.equals(t.getExternalId())).findFirst().orElse(null);

            assertNotNull(buyDownFeeTransaction, "Buy down fee transaction should exist");
            assertEquals(0, BigDecimal.valueOf(250.0).compareTo(buyDownFeeTransaction.getAmount()));
            assertEquals(externalId, buyDownFeeTransaction.getExternalId());
        });
    }

    @Test
    public void testMultipleBuyDownFees() {
        runAt("04 September 2024", () -> {
            deleteAllExternalEvents();

            // Add first buy down fee
            Long firstBuyDownFeeId = addBuyDownFeeForLoan(loanId, 200.0, "02 September 2024");

            verifyBusinessEvents(new BusinessEvent("LoanBuyDownFeeTransactionCreatedBusinessEvent", "04 September 2024"));
            deleteAllExternalEvents();

            // Add second buy down fee
            Long secondBuyDownFeeId = addBuyDownFeeForLoan(loanId, 150.0, "04 September 2024");
            verifyBusinessEvents(new BusinessEvent("LoanBuyDownFeeTransactionCreatedBusinessEvent", "04 September 2024"));

            assertNotNull(firstBuyDownFeeId);
            assertNotNull(secondBuyDownFeeId);

            // Verify both transactions exist
            final GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            long buyDownFeeCount = loanDetails.getTransactions().stream()
                    .filter(t -> t.getType() != null && t.getType().getId() != null && t.getType().getId().equals(40L)).count();

            assertEquals(2, buyDownFeeCount, "Should have 2 buy down fee transactions");
        });
    }

    @Test
    public void testBuyDownFeeAccountingEntries() {
        runAt("04 September 2024", () -> {
            // Add Buy Down fee transaction
            Long buyDownFeeTransactionId = addBuyDownFeeForLoan(loanId, 250.0, "04 September 2024");
            assertNotNull(buyDownFeeTransactionId);

            final GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            GetLoansLoanIdTransactions buyDownFeeTransaction = loanDetails.getTransactions().stream()
                    .filter(t -> t.getType() != null && t.getType().getId() != null && t.getType().getId().equals(40L))
                    .filter(t -> buyDownFeeTransactionId.equals(t.getId())).findFirst().orElse(null);

            assertNotNull(buyDownFeeTransaction, "Buy down fee transaction should exist");
            assertEquals(0, BigDecimal.valueOf(250.0).compareTo(buyDownFeeTransaction.getAmount()));

            verifyTRJournalEntries(buyDownFeeTransactionId, debit(buyDownExpenseAccount, 250.0), // DR: Buy Down Expense
                    credit(deferredIncomeLiabilityAccount, 250.0) // CR: Deferred Income Liability
            );

            log.info("Buy Down Fee transaction created successfully (accounting validation pending client model regeneration)");
        });
    }

    @Test
    public void testBuyDownFeeValidation() {
        runAt("05 September 2024", () -> {
            // Test with negative amount (should fail)
            try {
                PostLoansLoanIdTransactionsResponse response = loanTransactionHelper.makeLoanBuyDownFee(loanId,
                        new PostLoansLoanIdTransactionsRequest().dateFormat(DATETIME_PATTERN).transactionDate("05 September 2024")
                                .locale("en").transactionAmount(-100.0).note("Invalid negative amount"));
                assertTrue(false, "Buy down fee with negative amount should have failed");
            } catch (Exception e) {
                // Expected: validation should prevent negative amounts
                log.info("Expected validation error for negative amount: {}", e.getMessage());
            }

            // Test with zero amount (should fail)
            try {
                PostLoansLoanIdTransactionsResponse response = loanTransactionHelper.makeLoanBuyDownFee(loanId,
                        new PostLoansLoanIdTransactionsRequest().dateFormat(DATETIME_PATTERN).transactionDate("05 September 2024")
                                .locale("en").transactionAmount(0.0).note("Invalid zero amount"));
                assertTrue(false, "Buy down fee with zero amount should have failed");
            } catch (Exception e) {
                // Expected: validation should prevent zero amounts
                log.info("Expected validation error for zero amount: {}", e.getMessage());
            }
        });
    }

    /**
     * Creates a progressive loan product with buy down fee enabled
     */
    private PostLoanProductsRequest createProgressiveLoanProductWithBuyDownFee() {
        // Create a progressive loan product with accrual-based accounting and proper GL mappings
        return new PostLoanProductsRequest().name(Utils.uniqueRandomStringGenerator("BUY_DOWN_FEE_PROGRESSIVE_", 6))
                .shortName(Utils.uniqueRandomStringGenerator("", 4)).description("Progressive loan product with buy down fee enabled")
                .includeInBorrowerCycle(false).useBorrowerCycle(false).currencyCode("USD").digitsAfterDecimal(2).principal(1000.0)
                .minPrincipal(100.0).maxPrincipal(10000.0).numberOfRepayments(12).minNumberOfRepayments(6).maxNumberOfRepayments(24)
                .repaymentEvery(1).repaymentFrequencyType(RepaymentFrequencyType.MONTHS_L).interestRatePerPeriod(10.0)
                .minInterestRatePerPeriod(0.0).maxInterestRatePerPeriod(120.0).interestRateFrequencyType(InterestRateFrequencyType.YEARS)
                .amortizationType(AmortizationType.EQUAL_INSTALLMENTS).interestType(InterestType.DECLINING_BALANCE)
                .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY).allowPartialPeriodInterestCalcualtion(false)
                .transactionProcessingStrategyCode("advanced-payment-allocation-strategy")
                .paymentAllocation(List.of(createDefaultPaymentAllocation("NEXT_INSTALLMENT"))).creditAllocation(List.of())
                .daysInMonthType(30).daysInYearType(360).isInterestRecalculationEnabled(false).accountingRule(3) // Accrual-based
                                                                                                                 // accounting
                // GL Account Mappings for Accrual-Based Accounting
                .fundSourceAccountId(fundSource.getAccountID().longValue())
                .loanPortfolioAccountId(loansReceivableAccount.getAccountID().longValue())
                .transfersInSuspenseAccountId(suspenseAccount.getAccountID().longValue())
                .interestOnLoanAccountId(interestIncomeAccount.getAccountID().longValue())
                .incomeFromFeeAccountId(feeIncomeAccount.getAccountID().longValue())
                .incomeFromPenaltyAccountId(penaltyIncomeAccount.getAccountID().longValue())
                .incomeFromRecoveryAccountId(recoveriesAccount.getAccountID().longValue())
                .writeOffAccountId(writtenOffAccount.getAccountID().longValue())
                .overpaymentLiabilityAccountId(overpaymentAccount.getAccountID().longValue())
                // Receivable accounts required for accrual-based accounting
                .receivableInterestAccountId(interestReceivableAccount.getAccountID().longValue())
                .receivableFeeAccountId(feeReceivableAccount.getAccountID().longValue())
                .receivablePenaltyAccountId(penaltyReceivableAccount.getAccountID().longValue())
                .buyDownExpenseAccountId(buyDownExpenseAccount.getAccountID().longValue())
                .incomeFromBuyDownAccountId(feeIncomeAccount.getAccountID().longValue())
                .deferredIncomeLiabilityAccountId(deferredIncomeLiabilityAccount.getAccountID().longValue()).loanScheduleType("PROGRESSIVE")
                .loanScheduleProcessingType("HORIZONTAL").enableBuyDownFee(true).merchantBuyDownFee(true)
                .buyDownFeeCalculationType(PostLoanProductsRequest.BuyDownFeeCalculationTypeEnum.FLAT)
                .buyDownFeeStrategy(PostLoanProductsRequest.BuyDownFeeStrategyEnum.EQUAL_AMORTIZATION)
                .buyDownFeeIncomeType(PostLoanProductsRequest.BuyDownFeeIncomeTypeEnum.FEE).locale("en").dateFormat("dd MMMM yyyy");
    }

    @Test
    public void testBuyDownFeeAdjustment() {
        runAt("06 September 2024", () -> {
            deleteAllExternalEvents();
            // Add initial buy down fee
            Long buyDownFeeTransactionId = addBuyDownFeeForLoan(loanId, 500.0, "06 September 2024");
            assertNotNull(buyDownFeeTransactionId);

            verifyBusinessEvents(new BusinessEvent("LoanBuyDownFeeTransactionCreatedBusinessEvent", "06 September 2024"));
            deleteAllExternalEvents();

            // Create buy down fee adjustment (use same date as business date)
            PostLoansLoanIdTransactionsResponse adjustmentResponse = loanTransactionHelper.buyDownFeeAdjustment(loanId,
                    buyDownFeeTransactionId, "06 September 2024", 100.0);

            verifyBusinessEvents(new LoanTransactionMinimalBusinessEvent("LoanBuyDownFeeAdjustmentTransactionCreatedBusinessEvent",
                    "06 September 2024", 100.0, false));

            assertNotNull(adjustmentResponse);
            assertNotNull(adjustmentResponse.getLoanId());
            assertNotNull(adjustmentResponse.getClientId());
            assertNotNull(adjustmentResponse.getOfficeId());
            assertEquals(loanId, adjustmentResponse.getLoanId());

            // Verify loan details show both transactions
            final GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            assertNotNull(loanDetails);

            List<GetLoansLoanIdTransactions> transactions = loanDetails.getTransactions();
            assertTrue(transactions.size() >= 3); // Disbursement, Buy Down Fee, Buy Down Fee Adjustment

            // Find the buy down fee adjustment transaction
            GetLoansLoanIdTransactions adjustmentTransaction = transactions.stream()
                    .filter(txn -> "Buy Down Fee Adjustment".equals(txn.getType().getValue())).findFirst().orElse(null);

            assertNotNull(adjustmentTransaction);
            assertEquals(0, BigDecimal.valueOf(100.0).compareTo(adjustmentTransaction.getAmount()));
            assertEquals("06 September 2024", adjustmentTransaction.getDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));

            deleteAllExternalEvents();
            loanTransactionHelper.reverseLoanTransaction(loanId, adjustmentResponse.getResourceId(), "06 September 2024");
            verifyBusinessEvents(new LoanAdjustTransactionBusinessEvent("LoanAdjustTransactionBusinessEvent", "06 September 2024",
                    "loanTransactionType.buyDownFeeAdjustment", "2024-09-06"));

        });
    }

    @Test
    public void testBuyDownFeeAdjustmentValidations() {
        runAt("08 September 2024", () -> {
            // Add initial buy down fee
            Long buyDownFeeTransactionId = addBuyDownFeeForLoan(loanId, 300.0, "08 September 2024");
            assertNotNull(buyDownFeeTransactionId);

            // Test 1: Adjustment amount more than original amount (should fail)
            try {
                loanTransactionHelper.buyDownFeeAdjustment(loanId, buyDownFeeTransactionId, "08 September 2024", 400.0);
                assertTrue(false, "Expected validation error for adjustment amount exceeding original amount");
            } catch (Exception e) {
                log.info("Expected validation error for excessive adjustment amount: {}", e.getMessage());
                assertTrue(e.getMessage().contains("amount") || e.getMessage().contains("exceed"));
            }

            // Test 2: Adjustment date before original transaction date (should fail)
            try {
                loanTransactionHelper.buyDownFeeAdjustment(loanId, buyDownFeeTransactionId, "07 September 2024", 100.0);
                assertTrue(false, "Expected validation error for adjustment date before original transaction date");
            } catch (Exception e) {
                log.info("Expected validation error for early adjustment date: {}", e.getMessage());
                assertTrue(e.getMessage().contains("date") || e.getMessage().contains("before"));
            }

            // Test 3: Valid adjustment should succeed
            PostLoansLoanIdTransactionsResponse validAdjustment = loanTransactionHelper.buyDownFeeAdjustment(loanId,
                    buyDownFeeTransactionId, "08 September 2024", 150.0);
            assertNotNull(validAdjustment);

            // Test 4: Second adjustment that would exceed total should fail
            try {
                loanTransactionHelper.buyDownFeeAdjustment(loanId, buyDownFeeTransactionId, "08 September 2024", 200.0);
                assertTrue(false, "Expected validation error for total adjustments exceeding original amount");
            } catch (Exception e) {
                log.info("Expected validation error for cumulative adjustment excess: {}", e.getMessage());
                assertTrue(e.getMessage().contains("amount") || e.getMessage().contains("exceed"));
            }
        });
    }

    @Test
    public void testBuyDownFeeAdjustmentAccountingEntries() {
        runAt("10 September 2024", () -> {
            // Add initial buy down fee
            Long buyDownFeeTransactionId = addBuyDownFeeForLoan(loanId, 400.0, "10 September 2024");
            assertNotNull(buyDownFeeTransactionId);

            // Verify initial buy down fee accounting entries
            verifyTRJournalEntries(buyDownFeeTransactionId, debit(buyDownExpenseAccount, 400.0),
                    credit(deferredIncomeLiabilityAccount, 400.0));

            // Create buy down fee adjustment
            PostLoansLoanIdTransactionsResponse adjustmentResponse = loanTransactionHelper.buyDownFeeAdjustment(loanId,
                    buyDownFeeTransactionId, "10 September 2024", 120.0);
            assertNotNull(adjustmentResponse);
        });
    }

    @Test
    public void testMultipleBuyDownFeeAdjustments() {
        runAt("12 September 2024", () -> {
            deleteAllExternalEvents();

            // Add initial buy down fee
            Long buyDownFeeTransactionId = addBuyDownFeeForLoan(loanId, 600.0, "12 September 2024");
            assertNotNull(buyDownFeeTransactionId);

            verifyBusinessEvents(new BusinessEvent("LoanBuyDownFeeTransactionCreatedBusinessEvent", "12 September 2024"));
            deleteAllExternalEvents();

            // First adjustment
            PostLoansLoanIdTransactionsResponse adjustment1 = loanTransactionHelper.buyDownFeeAdjustment(loanId, buyDownFeeTransactionId,
                    "12 September 2024", 100.0);
            assertNotNull(adjustment1);

            verifyBusinessEvents(new BusinessEvent("LoanBuyDownFeeAdjustmentTransactionCreatedBusinessEvent", "12 September 2024"));
            deleteAllExternalEvents();

            // Second adjustment
            PostLoansLoanIdTransactionsResponse adjustment2 = loanTransactionHelper.buyDownFeeAdjustment(loanId, buyDownFeeTransactionId,
                    "12 September 2024", 150.0);
            assertNotNull(adjustment2);

            verifyBusinessEvents(new BusinessEvent("LoanBuyDownFeeAdjustmentTransactionCreatedBusinessEvent", "12 September 2024"));

            // Verify both adjustments are recorded
            final GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            assertNotNull(loanDetails);

            List<GetLoansLoanIdTransactions> adjustmentTransactions = loanDetails.getTransactions().stream()
                    .filter(txn -> "Buy Down Fee Adjustment".equals(txn.getType().getValue())).toList();

            assertEquals(2, adjustmentTransactions.size());

            // Verify total adjustment amounts
            BigDecimal totalAdjustments = adjustmentTransactions.stream().map(GetLoansLoanIdTransactions::getAmount).reduce(BigDecimal.ZERO,
                    BigDecimal::add);
            assertEquals(0, BigDecimal.valueOf(250.0).compareTo(totalAdjustments));

            // Third adjustment that would exceed limit should fail
            try {
                loanTransactionHelper.buyDownFeeAdjustment(loanId, buyDownFeeTransactionId, "12 September 2024", 400.0);
                assertTrue(false, "Expected validation error for total adjustments exceeding original amount");
            } catch (Exception e) {
                log.info("Expected validation error for cumulative adjustments exceeding limit: {}", e.getMessage());
            }
        });
    }

    @Test
    public void testBuyDownFeeAdjustmentWithExternalId() {
        runAt("16 September 2024", () -> {
            deleteAllExternalEvents();
            // Add initial buy down fee
            Long buyDownFeeTransactionId = addBuyDownFeeForLoan(loanId, 350.0, "16 September 2024");
            assertNotNull(buyDownFeeTransactionId);
            verifyBusinessEvents(new BusinessEvent("LoanBuyDownFeeTransactionCreatedBusinessEvent", "16 September 2024"));
            deleteAllExternalEvents();
            // Create adjustment with external ID
            String adjustmentExternalId = UUID.randomUUID().toString();
            PostLoansLoanIdTransactionsResponse adjustmentResponse = loanTransactionHelper.buyDownFeeAdjustment(loanId,
                    buyDownFeeTransactionId,
                    new PostLoansLoanIdTransactionsTransactionIdRequest().transactionDate("16 September 2024").transactionAmount(80.0)
                            .externalId(adjustmentExternalId).note("Buy Down Fee Adjustment with external ID").dateFormat("dd MMMM yyyy")
                            .locale("en"));

            assertNotNull(adjustmentResponse);
            assertEquals(adjustmentExternalId, adjustmentResponse.getResourceExternalId());

            verifyBusinessEvents(new BusinessEvent("LoanBuyDownFeeAdjustmentTransactionCreatedBusinessEvent", "16 September 2024"));

            // Verify adjustment transaction details
            final GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);
            GetLoansLoanIdTransactions adjustmentTransaction = loanDetails.getTransactions().stream()
                    .filter(txn -> adjustmentExternalId.equals(txn.getExternalId())).findFirst().orElse(null);

            assertNotNull(adjustmentTransaction);
            assertEquals(0, BigDecimal.valueOf(80.0).compareTo(adjustmentTransaction.getAmount()));
        });
    }

    /**
     * Helper method to add buy down fee for a loan
     *
     * @param loanId
     *            the ID of the loan to add the buy down fee to
     * @param amount
     *            the amount of the buy down fee
     * @param date
     *            the transaction date in format specified by DATETIME_PATTERN
     * @return the ID of the created buy down fee transaction
     */
    private Long addBuyDownFeeForLoan(Long loanId, Double amount, String date) {
        String buyDownFeeExternalId = UUID.randomUUID().toString();
        PostLoansLoanIdTransactionsResponse response = loanTransactionHelper.makeLoanBuyDownFee(loanId,
                new PostLoansLoanIdTransactionsRequest().dateFormat(DATETIME_PATTERN).transactionDate(date).locale("en")
                        .transactionAmount(amount).externalId(buyDownFeeExternalId).note("Buy Down Fee Transaction"));
        return response.getResourceId();
    }

    @Test
    public void testBuyDownFeeDailyAmortization() {
        final AtomicReference<Long> loanIdRef = new AtomicReference<>();
        final AtomicReference<Long> buyDownFeeTransactionIdIdRef = new AtomicReference<>();

        final PostClientsResponse client = clientHelper.createClient(ClientHelper.defaultClientCreationRequest());

        final PostLoanProductsResponse loanProductsResponse = loanProductHelper.createLoanProduct(create4IProgressive()
                .enableBuyDownFee(true).buyDownFeeCalculationType(PostLoanProductsRequest.BuyDownFeeCalculationTypeEnum.FLAT)
                .buyDownFeeStrategy(PostLoanProductsRequest.BuyDownFeeStrategyEnum.EQUAL_AMORTIZATION)
                .buyDownFeeIncomeType(PostLoanProductsRequest.BuyDownFeeIncomeTypeEnum.FEE)
                .buyDownExpenseAccountId(buyDownExpenseAccount.getAccountID().longValue()).merchantBuyDownFee(true)
                .deferredIncomeLiabilityAccountId(deferredIncomeLiabilityAccount.getAccountID().longValue())
                .incomeFromBuyDownAccountId(feeIncomeAccount.getAccountID().longValue()));

        runAt("1 January 2024", () -> {
            Long loanId = applyAndApproveProgressiveLoan(client.getClientId(), loanProductsResponse.getResourceId(), "1 January 2024",
                    500.0, 7.0, 3, null);
            loanIdRef.set(loanId);

            disburseLoan(loanId, BigDecimal.valueOf(100), "1 January 2024");
            deleteAllExternalEvents();
            PostLoansLoanIdTransactionsResponse transactionsResponse = loanTransactionHelper.makeLoanBuyDownFee(loanId, "1 January 2024",
                    50.0);
            buyDownFeeTransactionIdIdRef.set(transactionsResponse.getResourceId());
            verifyBusinessEvents(new BusinessEvent("LoanBuyDownFeeTransactionCreatedBusinessEvent", "01 January 2024"));
        });
        runAt("31 January 2024", () -> {
            Long loanId = loanIdRef.get();
            deleteAllExternalEvents();
            executeInlineCOB(loanId);
            verifyBusinessEvents(new BusinessEvent("LoanBuyDownFeeAmortizationTransactionCreatedBusinessEvent", "30 January 2024"));

            // summarized amortization
            verifyTransactions(loanId, //
                    transaction(100.0, "Disbursement", "01 January 2024"), //
                    transaction(0.55, "Accrual", "30 January 2024"), //
                    transaction(50.0, "Buy Down Fee", "01 January 2024"), //
                    transaction(16.48, "Buy Down Fee Amortization", "30 January 2024"));
        });
        runAt("1 February 2024", () -> {
            Long loanId = loanIdRef.get();
            deleteAllExternalEvents();
            executeInlineCOB(loanId);
            verifyBusinessEvents(new BusinessEvent("LoanBuyDownFeeAmortizationTransactionCreatedBusinessEvent", "31 January 2024"));

            // daily amortization
            verifyTransactions(loanId, //
                    transaction(100.0, "Disbursement", "01 January 2024"), //
                    transaction(50.0, "Buy Down Fee", "01 January 2024"), //
                    transaction(0.55, "Accrual", "30 January 2024"), //
                    transaction(16.48, "Buy Down Fee Amortization", "30 January 2024"), //
                    transaction(0.01, "Accrual", "31 January 2024"), //
                    transaction(0.55, "Buy Down Fee Amortization", "31 January 2024"));

            deleteAllExternalEvents();
            loanTransactionHelper.buyDownFeeAdjustment(loanId, buyDownFeeTransactionIdIdRef.get(), "1 February 2024", 10.0);
            verifyBusinessEvents(new BusinessEvent("LoanBuyDownFeeAdjustmentTransactionCreatedBusinessEvent", "01 February 2024"));
        });
        runAt("2 February 2024", () -> {
            Long loanId = loanIdRef.get();
            deleteAllExternalEvents();
            executeInlineCOB(loanId);
            verifyBusinessEvents(new BusinessEvent("LoanBuyDownFeeAmortizationTransactionCreatedBusinessEvent", "01 February 2024"));

            // not backdated and not large buy down fee adjustment -> lowered daily amount
            verifyTransactions(loanId, //
                    transaction(100.0, "Disbursement", "01 January 2024"), //
                    transaction(50.0, "Buy Down Fee", "01 January 2024"), //
                    transaction(0.55, "Accrual", "30 January 2024"), //
                    transaction(16.48, "Buy Down Fee Amortization", "30 January 2024"), //
                    transaction(0.01, "Accrual", "31 January 2024"), //
                    transaction(0.55, "Buy Down Fee Amortization", "31 January 2024"), //
                    transaction(10.0, "Buy Down Fee Adjustment", "01 February 2024"), //
                    transaction(0.02, "Accrual", "01 February 2024"), //
                    transaction(0.39, "Buy Down Fee Amortization", "01 February 2024"));

            deleteAllExternalEvents();
            loanTransactionHelper.buyDownFeeAdjustment(loanId, buyDownFeeTransactionIdIdRef.get(), "10 January 2024", 10.0);
            verifyBusinessEvents(new BusinessEvent("LoanBuyDownFeeAdjustmentTransactionCreatedBusinessEvent", "02 February 2024"));
        });
        runAt("3 February 2024", () -> {
            Long loanId = loanIdRef.get();
            deleteAllExternalEvents();
            executeInlineCOB(loanId);
            verifyBusinessEvents(
                    new BusinessEvent("LoanBuyDownFeeAmortizationAdjustmentTransactionCreatedBusinessEvent", "02 February 2024"));

            // backdated buy down fee adjustment -> amortization adjustment
            verifyTransactions(loanId, //
                    transaction(100.0, "Disbursement", "01 January 2024"), //
                    transaction(50.0, "Buy Down Fee", "01 January 2024"), //
                    transaction(10.0, "Buy Down Fee Adjustment", "10 January 2024"), //
                    transaction(0.55, "Accrual", "30 January 2024"), //
                    transaction(16.48, "Buy Down Fee Amortization", "30 January 2024"), //
                    transaction(0.01, "Accrual", "31 January 2024"), //
                    transaction(0.55, "Buy Down Fee Amortization", "31 January 2024"), //
                    transaction(10.0, "Buy Down Fee Adjustment", "01 February 2024"), //
                    transaction(0.02, "Accrual", "01 February 2024"), //
                    transaction(0.39, "Buy Down Fee Amortization", "01 February 2024"), //
                    transaction(0.02, "Accrual", "02 February 2024"), //
                    transaction(2.55, "Buy Down Fee Amortization Adjustment", "02 February 2024"));

            deleteAllExternalEvents();
            loanTransactionHelper.buyDownFeeAdjustment(loanId, buyDownFeeTransactionIdIdRef.get(), "03 February 2024", 20.0);
            verifyBusinessEvents(new BusinessEvent("LoanBuyDownFeeAdjustmentTransactionCreatedBusinessEvent", "03 February 2024"));
        });
        runAt("4 February 2024", () -> {
            Long loanId = loanIdRef.get();
            deleteAllExternalEvents();
            executeInlineCOB(loanId);
            verifyBusinessEvents(
                    new BusinessEvent("LoanBuyDownFeeAmortizationAdjustmentTransactionCreatedBusinessEvent", "03 February 2024"));

            // large (more than remaining unrecognized (15.13)) buy down fee adjustment -> amortization adjustment
            verifyTransactions(loanId, //
                    transaction(100.0, "Disbursement", "01 January 2024"), //
                    transaction(50.0, "Buy Down Fee", "01 January 2024"), //
                    transaction(10.0, "Buy Down Fee Adjustment", "10 January 2024"), //
                    transaction(0.55, "Accrual", "30 January 2024"), //
                    transaction(16.48, "Buy Down Fee Amortization", "30 January 2024"), //
                    transaction(0.01, "Accrual", "31 January 2024"), //
                    transaction(0.55, "Buy Down Fee Amortization", "31 January 2024"), //
                    transaction(10.0, "Buy Down Fee Adjustment", "01 February 2024"), //
                    transaction(0.02, "Accrual", "01 February 2024"), //
                    transaction(0.39, "Buy Down Fee Amortization", "01 February 2024"), //
                    transaction(0.02, "Accrual", "02 February 2024"), //
                    transaction(2.55, "Buy Down Fee Amortization Adjustment", "02 February 2024"), //
                    transaction(20.0, "Buy Down Fee Adjustment", "03 February 2024"), //
                    transaction(0.02, "Accrual", "03 February 2024"), //
                    transaction(4.87, "Buy Down Fee Amortization Adjustment", "03 February 2024"));

            // Check journal entries of amortization and amortization adjustment
            GetLoansLoanIdResponse loanDetails = loanTransactionHelper.getLoanDetails(loanId);

            Optional<GetLoansLoanIdTransactions> amortizationTransactionOpt = loanDetails.getTransactions().stream()
                    .filter(transaction -> LocalDate.of(2024, 2, 1).equals(transaction.getDate())
                            && transaction.getType().getBuyDownFeeAmortization())
                    .findFirst();
            Assertions.assertTrue(amortizationTransactionOpt.isPresent());

            verifyTRJournalEntries(amortizationTransactionOpt.get().getId(), //
                    journalEntry(0.39, feeIncomeAccount, "CREDIT"), //
                    journalEntry(0.39, deferredIncomeLiabilityAccount, "DEBIT"));

            Optional<GetLoansLoanIdTransactions> amortizationAdjustmentTransactionOpt = loanDetails.getTransactions().stream()
                    .filter(transaction -> LocalDate.of(2024, 2, 3).equals(transaction.getDate())
                            && transaction.getType().getBuyDownFeeAmortizationAdjustment())
                    .findFirst();
            Assertions.assertTrue(amortizationAdjustmentTransactionOpt.isPresent());

            verifyTRJournalEntries(amortizationAdjustmentTransactionOpt.get().getId(), //
                    journalEntry(4.87, deferredIncomeLiabilityAccount, "CREDIT"), //
                    journalEntry(4.87, feeIncomeAccount, "DEBIT"));
        });
    }

    @Test
    public void testRetrieveBuyDownFeeAmortizationDetails() {
        final PostClientsResponse client = clientHelper.createClient(ClientHelper.defaultClientCreationRequest());
        final PostLoanProductsResponse loanProduct = loanProductHelper.createLoanProduct(createProgressiveLoanProductWithBuyDownFee());

        final long loanId = applyAndApproveProgressiveLoan(client.getClientId(), loanProduct.getResourceId(), "1 February 2024", 1000.0,
                7.0, 6, null);

        disburseLoan(loanId, BigDecimal.valueOf(1000), "1 February 2024");

        addBuyDownFeeForLoan(loanId, 100.0, "1 February 2024");

        final List<BuyDownFeeAmortizationDetails> amortizationDetails = loanTransactionHelper.fetchBuyDownFeeAmortizationDetails(loanId);

        assertNotNull(amortizationDetails);
        assertFalse(amortizationDetails.isEmpty());

        final BuyDownFeeAmortizationDetails amortizationDetail = amortizationDetails.getFirst();
        assertNotNull(amortizationDetail);
        assertNotNull(amortizationDetail.getId());
        assertEquals(loanId, amortizationDetail.getLoanId());
        assertNotNull(amortizationDetail.getTransactionId());
        assertEquals(LocalDate.of(2024, 2, 1), amortizationDetail.getBuyDownFeeDate());
        assertNotNull(amortizationDetail.getBuyDownFeeAmount());
        assertEquals(0, BigDecimal.valueOf(100.0).compareTo(amortizationDetail.getBuyDownFeeAmount()));
        assertNotNull(amortizationDetail.getAmortizedAmount());
        assertEquals(0, amortizationDetail.getAmortizedAmount().signum());
        assertNotNull(amortizationDetail.getNotYetAmortizedAmount());
        assertEquals(0, BigDecimal.valueOf(100.0).compareTo(amortizationDetail.getNotYetAmortizedAmount()));
    }

    @Test
    public void testRetrieveBuyDownFeeAmortizationDetails_notEnabled() {
        final PostClientsResponse client = clientHelper.createClient(ClientHelper.defaultClientCreationRequest());
        final PostLoanProductsResponse loanProduct = loanProductHelper
                .createLoanProduct(createProgressiveLoanProductWithBuyDownFee().enableBuyDownFee(false));

        final long loanId = applyAndApproveProgressiveLoan(client.getClientId(), loanProduct.getResourceId(), "1 February 2024", 1000.0,
                7.0, 6, null);

        disburseLoan(loanId, BigDecimal.valueOf(1000), "1 February 2024");

        final CallFailedRuntimeException exception = assertThrows(CallFailedRuntimeException.class, () -> {
            addBuyDownFeeForLoan(loanId, 100.0, "1 February 2024");
        });

        assertEquals(400, exception.getResponse().code());
        assertTrue(exception.getMessage().contains("buy.down.fee.not.enabled"));
        assertTrue(exception.getMessage().contains("Buy down fee is not enabled for this loan product"));
    }

    @Test
    public void tesReverseBuyDownFeeTransactionWithAmortizationAdjustmentTransaction() {
        AtomicReference<Long> buyDownFeeTransactionIdRef = new AtomicReference<>();
        runAt("2 September 2024", () -> {
            deleteAllExternalEvents();
            // Add initial buy down fee
            Long buyDownFeeTransactionId = addBuyDownFeeForLoan(loanId, 400.0, "02 September 2024");
            buyDownFeeTransactionIdRef.set(buyDownFeeTransactionId);
            assertNotNull(buyDownFeeTransactionId);
            verifyBusinessEvents(new LoanTransactionMinimalBusinessEvent("LoanBuyDownFeeTransactionCreatedBusinessEvent",
                    "02 September 2024", 400.0, false));
        });
        runAt("10 September 2024", () -> {
            deleteAllExternalEvents();
            inlineLoanCOBHelper.executeInlineCOB(loanId);

            verifyBusinessEvents(new LoanTransactionMinimalBusinessEvent("LoanBuyDownFeeAmortizationTransactionCreatedBusinessEvent",
                    "09 September 2024", 8.79, false));

            deleteAllExternalEvents();
            Long buyDownFeeTransactionId = buyDownFeeTransactionIdRef.get();
            // Reverse Buy Down Fee
            PostLoansLoanIdTransactionsResponse transactionsResponse = loanTransactionHelper.reverseLoanTransaction(loanId,
                    buyDownFeeTransactionId, new PostLoansLoanIdTransactionsTransactionIdRequest().dateFormat(DATETIME_PATTERN)
                            .transactionDate("10 September 2024").note("Buy Down Fee reversed").transactionAmount(0.0).locale("en"));
            Assertions.assertNotNull(transactionsResponse);
            Assertions.assertNotNull(transactionsResponse.getResourceId());
            Assertions.assertEquals(transactionsResponse.getResourceId(), buyDownFeeTransactionId);

            verifyBusinessEvents(new LoanAdjustTransactionBusinessEvent("LoanAdjustTransactionBusinessEvent", "10 September 2024",
                    "loanTransactionType.buyDownFee", "2024-09-02"));

        });
        runAt("11 September 2024", () -> {
            executeInlineCOB(loanId);
            verifyTransactions(loanId, //
                    transaction(1000.000000, "Disbursement", "01 September 2024", 1000.000000, 0, 0, 0, 0, 0, 0, false), //
                    transaction(400.000000, "Buy Down Fee", "02 September 2024", 0, 0, 0, 0, 0, 0, 0, true), //
                    transaction(2.220000, "Accrual", "09 September 2024", 0, 0, 2.220000, 0, 0, 0, 0, false), //
                    transaction(8.790000, "Buy Down Fee Amortization", "09 September 2024", 0, 0, 0, 8.790000, 0, 0, 0, false), //
                    transaction(0.28, "Accrual", "10 September 2024", 0, 0, 0.28, 0, 0, 0, 0, false), //
                    transaction(8.790000, "Buy Down Fee Amortization Adjustment", "10 September 2024", 0, 0, 0, 8.790000, 0, 0, 0, false) //
            );

        });
    }

    @Test
    public void testReverseBuyDownFeeTransactionWithoutAmortizationAdjustmentTransaction() {
        final AtomicReference<Long> buyDownFeeTransactionIdIdRef = new AtomicReference<>();

        runAt("10 September 2024", () -> {
            // Add initial buy down fee
            final Long buyDownFeeTransactionId = addBuyDownFeeForLoan(loanId, 400.0, "10 September 2024");
            assertNotNull(buyDownFeeTransactionId);
            buyDownFeeTransactionIdIdRef.set(buyDownFeeTransactionId);

            // Verify initial buy down fee accounting entries
            verifyTRJournalEntries(buyDownFeeTransactionId, debit(buyDownExpenseAccount, 400.0),
                    credit(deferredIncomeLiabilityAccount, 400.0));

            verifyTransactions(loanId, //
                    transaction(1000.0, "Disbursement", "01 September 2024"), //
                    transaction(400.0, "Buy Down Fee", "10 September 2024"));

            // Reverse Buy Down Fee
            PostLoansLoanIdTransactionsResponse transactionsResponse = loanTransactionHelper.reverseLoanTransaction(loanId,
                    buyDownFeeTransactionId, new PostLoansLoanIdTransactionsTransactionIdRequest().dateFormat(DATETIME_PATTERN)
                            .transactionDate("10 September 2024").note("Buy Down Fee reversed").transactionAmount(0.0).locale("en"));
            Assertions.assertNotNull(transactionsResponse);
            Assertions.assertNotNull(transactionsResponse.getResourceId());
            Assertions.assertEquals(transactionsResponse.getResourceId(), buyDownFeeTransactionId);

            verifyTransactions(loanId, //
                    transaction(1000.000000, "Disbursement", "01 September 2024", 1000.000000, 0, 0, 0, 0, 0, 0, false), //
                    transaction(400.000000, "Buy Down Fee", "10 September 2024", 0, 0, 0, 0, 0, 0, 0, true) //
            );
        });
    }

    @Test
    public void testReverseBuyDownFeeTransaction() {
        final AtomicReference<Long> buyDownFeeTransactionIdIdRef = new AtomicReference<>();

        runAt("10 September 2024", () -> {
            deleteAllExternalEvents();
            // Add initial buy down fee
            final Long buyDownFeeTransactionId = addBuyDownFeeForLoan(loanId, 400.0, "10 September 2024");
            assertNotNull(buyDownFeeTransactionId);
            buyDownFeeTransactionIdIdRef.set(buyDownFeeTransactionId);

            verifyBusinessEvents(new LoanTransactionMinimalBusinessEvent("LoanBuyDownFeeTransactionCreatedBusinessEvent",
                    "10 September 2024", 400.0, false));

            // Verify initial buy down fee accounting entries
            verifyTRJournalEntries(buyDownFeeTransactionId, debit(buyDownExpenseAccount, 400.0),
                    credit(deferredIncomeLiabilityAccount, 400.0));

            verifyTransactions(loanId, //
                    transaction(1000.0, "Disbursement", "01 September 2024"), //
                    transaction(400.0, "Buy Down Fee", "10 September 2024"));
        });

        runAt("23 September 2024", () -> {
            deleteAllExternalEvents();
            final Long buyDownFeeTransactionId = buyDownFeeTransactionIdIdRef.get();
            executeInlineCOB(loanId);
            verifyBusinessEvents(new LoanTransactionMinimalBusinessEvent("LoanBuyDownFeeAmortizationTransactionCreatedBusinessEvent",
                    "22 September 2024", 14.61, false));

            deleteAllExternalEvents();
            // Reverse Buy Down Fee
            PostLoansLoanIdTransactionsResponse transactionsResponse = loanTransactionHelper.reverseLoanTransaction(loanId,
                    buyDownFeeTransactionId, new PostLoansLoanIdTransactionsTransactionIdRequest().dateFormat(DATETIME_PATTERN)
                            .transactionDate("10 September 2024").note("Buy Down Fee reversed").transactionAmount(0.0).locale("en"));
            Assertions.assertNotNull(transactionsResponse);
            Assertions.assertNotNull(transactionsResponse.getResourceId());
            Assertions.assertEquals(transactionsResponse.getResourceId(), buyDownFeeTransactionId);

            verifyBusinessEvents(new LoanAdjustTransactionBusinessEvent("LoanAdjustTransactionBusinessEvent", "23 September 2024",
                    "loanTransactionType.buyDownFee", "2024-09-10"));

            // Verify initial buy down fee reversed accounting entries
            verifyTRJournalEntries(buyDownFeeTransactionId, debit(buyDownExpenseAccount, 400.0),
                    credit(deferredIncomeLiabilityAccount, 400.0), credit(buyDownExpenseAccount, 400.0),
                    debit(deferredIncomeLiabilityAccount, 400.0));

            verifyTransactions(loanId, //
                    transaction(1000.000000, "Disbursement", "01 September 2024", 1000.000000, 0, 0, 0, 0, 0, 0, false), //
                    transaction(400.000000, "Buy Down Fee", "10 September 2024", 0, 0, 0, 0, 0, 0, 0, true), //
                    transaction(5.830000, "Accrual", "22 September 2024", 0, 0, 5.830000, 0, 0, 0, 0, false), //
                    transaction(14.610000, "Buy Down Fee Amortization", "22 September 2024", 0, 0, 0, 14.610000, 0, 0, 0, false) //
            );
        });
    }

    @Test
    public void testTryToReverseBuyDownFeeTransactionWithBuyDownFeeAdjustment() {
        final AtomicReference<Long> buyDownFeeTransactionIdIdRef = new AtomicReference<>();

        runAt("10 September 2024", () -> {
            // Add initial buy down fee
            final Long buyDownFeeTransactionId = addBuyDownFeeForLoan(loanId, 400.0, "10 September 2024");
            assertNotNull(buyDownFeeTransactionId);
            buyDownFeeTransactionIdIdRef.set(buyDownFeeTransactionId);

            // Verify initial buy down fee accounting entries
            verifyTRJournalEntries(buyDownFeeTransactionId, debit(buyDownExpenseAccount, 400.0),
                    credit(deferredIncomeLiabilityAccount, 400.0));

            verifyTransactions(loanId, //
                    transaction(1000.0, "Disbursement", "01 September 2024"), //
                    transaction(400.0, "Buy Down Fee", "10 September 2024"));
        });

        runAt("23 September 2024", () -> {
            final Long buyDownFeeTransactionId = buyDownFeeTransactionIdIdRef.get();
            executeInlineCOB(loanId);

            loanTransactionHelper.buyDownFeeAdjustment(loanId, buyDownFeeTransactionId, "23 September 2024", 200.0);

            // Try to Reverse Buy Down Fee that has linked a Buy Down Fee Adjustment
            CallFailedRuntimeException exception = assertThrows(CallFailedRuntimeException.class,
                    () -> loanTransactionHelper.reverseLoanTransaction(loanId, buyDownFeeTransactionId,
                            new PostLoansLoanIdTransactionsTransactionIdRequest().dateFormat(DATETIME_PATTERN)
                                    .transactionDate("10 September 2024").note("Buy Down Fee reversed").transactionAmount(0.0)
                                    .locale("en")));
            assertEquals(403, exception.getResponse().code());
            assertTrue(exception.getMessage().contains("buy.down.fee.cannot.be.reversed.when.adjusted"));
        });
    }

    @Test
    public void testBuyDownFeeForNonMerchant() {
        runAt("10 September 2024", () -> {
            deleteAllExternalEvents();
            // Add initial buy down fee

            final PostLoanProductsResponse loanProductsResponse = loanProductHelper
                    .createLoanProduct(createProgressiveLoanProductWithBuyDownFee().merchantBuyDownFee(false));

            // Apply for the loan with proper progressive loan settings
            PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applyLP2ProgressiveLoanRequest(clientId,
                    loanProductsResponse.getResourceId(), "01 September 2024", 1000.0, 10.0, 12, null));
            loanId = postLoansResponse.getLoanId();
            loanTransactionHelper.approveLoan(loanId, approveLoanRequest(1000.0, "01 September 2024"));
            disburseLoan(loanId, BigDecimal.valueOf(1000.0), "01 September 2024");

            Long buyDownFeeTransactionId = addBuyDownFeeForLoan(loanId, 400.0, "10 September 2024");
            assertNotNull(buyDownFeeTransactionId);

            verifyTransactions(loanId, //
                    transaction(1000.0, "Disbursement", "01 September 2024"), //
                    transaction(400.0, "Buy Down Fee", "10 September 2024"));

            // Verify initial buy down fee (non merchant) accounting entries
            verifyTRJournalEntries(buyDownFeeTransactionId, debit(fundSource, 400.0), credit(deferredIncomeLiabilityAccount, 400.0));

            // Reverse Buy Down Fee (non merchant)
            PostLoansLoanIdTransactionsResponse transactionsResponse = loanTransactionHelper.reverseLoanTransaction(loanId,
                    buyDownFeeTransactionId, new PostLoansLoanIdTransactionsTransactionIdRequest().dateFormat(DATETIME_PATTERN)
                            .transactionDate("10 September 2024").note("Buy Down Fee reversed").transactionAmount(0.0).locale("en"));
            Assertions.assertNotNull(transactionsResponse);
            Assertions.assertNotNull(transactionsResponse.getResourceId());
            Assertions.assertEquals(transactionsResponse.getResourceId(), buyDownFeeTransactionId);

            // Verify initial buy down fee (non merchant) reversed accounting entries
            verifyTRJournalEntries(buyDownFeeTransactionId, debit(fundSource, 400.0), credit(deferredIncomeLiabilityAccount, 400.0),
                    credit(fundSource, 400.0), debit(deferredIncomeLiabilityAccount, 400.0));

            buyDownFeeTransactionId = addBuyDownFeeForLoan(loanId, 400.0, "10 September 2024");
            assertNotNull(buyDownFeeTransactionId);

            // Buy Down Fee Adjustment (non merchant)
            final PostLoansLoanIdTransactionsResponse buyDownFeeAdjustmentTransaction = loanTransactionHelper.buyDownFeeAdjustment(loanId,
                    buyDownFeeTransactionId, "10 September 2024", 200.0);
            assertNotNull(buyDownFeeAdjustmentTransaction);

            // Verify buy down fee adjustment (non merchant)
            verifyTRJournalEntries(buyDownFeeAdjustmentTransaction.getResourceId(), debit(deferredIncomeLiabilityAccount, 200.0),
                    credit(fundSource, 200.0));
        });
    }

}
