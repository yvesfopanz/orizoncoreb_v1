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
package org.apache.fineract.accounting.journalentry.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.accounting.closure.domain.GLClosure;
import org.apache.fineract.accounting.common.AccountingConstants.CashAccountsForLoan;
import org.apache.fineract.accounting.common.AccountingConstants.FinancialActivity;
import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.accounting.journalentry.data.ChargePaymentDTO;
import org.apache.fineract.accounting.journalentry.data.GLAccountBalanceHolder;
import org.apache.fineract.accounting.journalentry.data.LoanDTO;
import org.apache.fineract.accounting.journalentry.data.LoanTransactionDTO;
import org.apache.fineract.infrastructure.core.service.MathUtil;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CashBasedAccountingProcessorForLoan implements AccountingProcessorForLoan {

    private final AccountingProcessorHelper helper;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService;

    @Override
    public void createJournalEntriesForLoan(final LoanDTO loanDTO) {
        final Long officeId = loanDTO.getOfficeId();
        final GLClosure latestGLClosure = this.helper.getLatestClosureByBranch(officeId);
        final Long loanProductId = loanDTO.getLoanProductId();
        final String currencyCode = loanDTO.getCurrencyCode();
        final Office office = this.helper.getOfficeById(officeId);
        for (final LoanTransactionDTO loanTransactionDTO : loanDTO.getNewLoanTransactions()) {
            final LocalDate transactionDate = loanTransactionDTO.getTransactionDate();
            final String transactionId = loanTransactionDTO.getTransactionId();
            final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();
            final Long loanId = loanDTO.getLoanId();
            final LoanTransactionEnumData transactionType = loanTransactionDTO.getTransactionType();

            this.helper.checkForBranchClosures(latestGLClosure, transactionDate);

            if (loanTransactionDTO.isReversed()) {
                journalEntryWritePlatformService.createJournalEntryForReversedLoanTransaction(transactionDate, transactionId, officeId);
                continue;
            }

            /** Handle Disbursements **/
            if (transactionType.isDisbursement()) {
                createJournalEntriesForDisbursements(loanDTO, loanTransactionDTO, office);
            }
            /***
             * Logic for repayments, repayments at disbursement (except charge adjustment)
             ***/
            else if ((transactionType.isRepaymentType() && !transactionType.isChargeAdjustment())
                    || transactionType.isRepaymentAtDisbursement() || transactionType.isChargePayment()) {
                createJournalEntriesForRepayments(loanDTO, loanTransactionDTO, office);
            }

            /** Logic for handling recovery payments **/
            else if (transactionType.isRecoveryRepayment()) {
                createJournalEntriesForRecoveryRepayments(loanDTO, loanTransactionDTO, office);
            }

            /** Logic for Refunds of Overpayments **/
            else if (transactionType.isRefund()) {
                createJournalEntriesForRefund(loanDTO, loanTransactionDTO, office);
            }

            /** Logic for Credit Balance Refunds **/
            else if (transactionType.isCreditBalanceRefund()) {
                createJournalEntriesForCreditBalanceRefund(loanDTO, loanTransactionDTO, office);
            }

            /***
             * Only principal write off affects cash based accounting (interest and fee write off need not be
             * considered). Debit losses written off and credit Loan Portfolio
             **/
            else if (transactionType.isWriteOff()) {
                final BigDecimal principalAmount = loanTransactionDTO.getPrincipal();
                if (principalAmount != null && principalAmount.compareTo(BigDecimal.ZERO) > 0) {
                    this.helper.createJournalEntriesForLoan(office, currencyCode, CashAccountsForLoan.LOSSES_WRITTEN_OFF.getValue(),
                            CashAccountsForLoan.LOAN_PORTFOLIO.getValue(), loanProductId, paymentTypeId, loanId, transactionId,
                            transactionDate, principalAmount);

                }
            } else if (transactionType.isInitiateTransfer() || transactionType.isApproveTransfer()
                    || transactionType.isWithdrawTransfer()) {
                createJournalEntriesForTransfers(loanDTO, loanTransactionDTO, office);
            }
            /** Logic for Refunds of Active Loans **/
            else if (transactionType.isRefundForActiveLoans()) {
                createJournalEntriesForRefundForActiveLoan(loanDTO, loanTransactionDTO, office);
            }
            // Logic for Chargebacks
            else if (transactionType.isChargeback()) {
                createJournalEntriesForChargeback(loanDTO, loanTransactionDTO, office);
            }
            // Logic for Charge Adjustment
            else if (transactionType.isChargeAdjustment()) {
                createJournalEntriesForChargeAdjustment(loanDTO, loanTransactionDTO, office);
            }
            // Logic for Charge-Off
            else if (transactionType.isChargeoff()) {
                createJournalEntriesForChargeOff(loanDTO, loanTransactionDTO, office);
            }
        }
    }

    private void createJournalEntriesForChargeOff(LoanDTO loanDTO, LoanTransactionDTO loanTransactionDTO, Office office) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();
        final Long loanId = loanDTO.getLoanId();
        final String currencyCode = loanDTO.getCurrencyCode();
        final boolean isMarkedFraud = loanDTO.isMarkedAsFraud();

        // transaction properties
        final String transactionId = loanTransactionDTO.getTransactionId();
        final LocalDate transactionDate = loanTransactionDTO.getTransactionDate();
        final BigDecimal principalAmount = loanTransactionDTO.getPrincipal();
        final BigDecimal interestAmount = loanTransactionDTO.getInterest();
        final BigDecimal feesAmount = loanTransactionDTO.getFees();
        final BigDecimal penaltiesAmount = loanTransactionDTO.getPenalties();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        GLAccountBalanceHolder glAccountBalanceHolder = new GLAccountBalanceHolder();

        // principal payment
        if (principalAmount != null && principalAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (isMarkedFraud) {
                populateCreditDebitMaps(loanProductId, principalAmount, paymentTypeId, CashAccountsForLoan.LOAN_PORTFOLIO.getValue(),
                        CashAccountsForLoan.CHARGE_OFF_FRAUD_EXPENSE.getValue(), glAccountBalanceHolder);
            } else {
                populateCreditDebitMaps(loanProductId, principalAmount, paymentTypeId, CashAccountsForLoan.LOAN_PORTFOLIO.getValue(),
                        CashAccountsForLoan.CHARGE_OFF_EXPENSE.getValue(), glAccountBalanceHolder);
            }
        }

        // interest payment
        if (interestAmount != null && interestAmount.compareTo(BigDecimal.ZERO) > 0) {
            populateCreditDebitMaps(loanProductId, interestAmount, paymentTypeId, CashAccountsForLoan.INTEREST_ON_LOANS.getValue(),
                    CashAccountsForLoan.INCOME_FROM_CHARGE_OFF_INTEREST.getValue(), glAccountBalanceHolder);
        }

        // handle fees payment
        if (feesAmount != null && feesAmount.compareTo(BigDecimal.ZERO) > 0) {
            populateCreditDebitMaps(loanProductId, feesAmount, paymentTypeId, CashAccountsForLoan.INCOME_FROM_FEES.getValue(),
                    CashAccountsForLoan.INCOME_FROM_CHARGE_OFF_FEES.getValue(), glAccountBalanceHolder);
        }

        // handle penalties payment
        if (penaltiesAmount != null && penaltiesAmount.compareTo(BigDecimal.ZERO) > 0) {
            populateCreditDebitMaps(loanProductId, penaltiesAmount, paymentTypeId, CashAccountsForLoan.INCOME_FROM_PENALTIES.getValue(),
                    CashAccountsForLoan.INCOME_FROM_CHARGE_OFF_PENALTY.getValue(), glAccountBalanceHolder);
        }

        // create credit entries
        for (Map.Entry<Long, BigDecimal> creditEntry : glAccountBalanceHolder.getCreditBalances().entrySet()) {
            GLAccount glAccount = glAccountBalanceHolder.getGlAccountMap().get(creditEntry.getKey());
            this.helper.createCreditJournalEntryForLoan(office, currencyCode, loanId, transactionId, transactionDate,
                    creditEntry.getValue(), glAccount);
        }
        // create debit entries
        for (Map.Entry<Long, BigDecimal> debitEntry : glAccountBalanceHolder.getDebitBalances().entrySet()) {
            GLAccount glAccount = glAccountBalanceHolder.getGlAccountMap().get(debitEntry.getKey());
            this.helper.createDebitJournalEntryForLoan(office, currencyCode, loanId, transactionId, transactionDate, debitEntry.getValue(),
                    glAccount);
        }
    }

    private void populateCreditDebitMaps(Long loanProductId, BigDecimal transactionPartAmount, Long paymentTypeId,
            Integer creditAccountType, Integer debitAccountType, GLAccountBalanceHolder glAccountBalanceHolder) {
        // Resolve Credit
        GLAccount accountCredit = this.helper.getLinkedGLAccountForLoanProduct(loanProductId, creditAccountType, paymentTypeId);
        glAccountBalanceHolder.addToCredit(accountCredit, transactionPartAmount);
        // Resolve Debit
        GLAccount accountDebit = this.helper.getLinkedGLAccountForLoanProduct(loanProductId, debitAccountType, paymentTypeId);
        glAccountBalanceHolder.addToDebit(accountDebit, transactionPartAmount);
    }

    private Integer returnExistingDebitAccountInMapMatchingGLAccount(Long loanProductId, Long paymentTypeId, Integer accountType,
            Map<Integer, BigDecimal> accountMap) {
        GLAccount glAccount = this.helper.getLinkedGLAccountForLoanProduct(loanProductId, accountType, paymentTypeId);
        Integer accountEntry = accountMap.entrySet().stream().filter(account -> this.helper
                .getLinkedGLAccountForLoanProduct(loanProductId, account.getKey(), paymentTypeId).getGlCode().equals(glAccount.getGlCode()))
                .map(Map.Entry::getKey).findFirst().orElse(accountType);
        return accountEntry;
    }

    private void createJournalEntriesForChargeAdjustment(LoanDTO loanDTO, LoanTransactionDTO loanTransactionDTO, Office office) {
        final boolean isMarkedAsChargeOff = loanDTO.isMarkedAsChargeOff();
        if (isMarkedAsChargeOff) {
            createJournalEntriesForChargeOffLoanChargeAdjustment(loanDTO, loanTransactionDTO, office);
        } else {
            createJournalEntriesForLoanChargeAdjustment(loanDTO, loanTransactionDTO, office);
        }

    }

    private void createJournalEntriesForChargeOffLoanChargeAdjustment(LoanDTO loanDTO, LoanTransactionDTO loanTransactionDTO,
            Office office) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();
        final Long loanId = loanDTO.getLoanId();
        final String currencyCode = loanDTO.getCurrencyCode();

        // transaction properties
        final String transactionId = loanTransactionDTO.getTransactionId();
        final LocalDate transactionDate = loanTransactionDTO.getTransactionDate();
        final BigDecimal principalAmount = loanTransactionDTO.getPrincipal();
        final BigDecimal interestAmount = loanTransactionDTO.getInterest();
        final BigDecimal feesAmount = loanTransactionDTO.getFees();
        final BigDecimal penaltiesAmount = loanTransactionDTO.getPenalties();
        final BigDecimal overPaymentAmount = loanTransactionDTO.getOverPayment();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        BigDecimal totalDebitAmount = new BigDecimal(0);

        Map<GLAccount, BigDecimal> accountMap = new LinkedHashMap<>();

        // handle principal payment
        if (principalAmount != null && principalAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(principalAmount);
            GLAccount account = this.helper.getLinkedGLAccountForLoanProduct(loanProductId,
                    CashAccountsForLoan.INCOME_FROM_CHARGE_OFF_FEES.getValue(), paymentTypeId);
            accountMap.put(account, principalAmount);
        }

        // handle interest payment
        if (interestAmount != null && interestAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(interestAmount);
            GLAccount account = this.helper.getLinkedGLAccountForLoanProduct(loanProductId,
                    CashAccountsForLoan.INCOME_FROM_CHARGE_OFF_FEES.getValue(), paymentTypeId);
            if (accountMap.containsKey(account)) {
                BigDecimal amount = accountMap.get(account).add(interestAmount);
                accountMap.put(account, amount);
            } else {
                accountMap.put(account, interestAmount);
            }
        }

        // handle fees payment
        if (feesAmount != null && feesAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(feesAmount);
            GLAccount account = this.helper.getLinkedGLAccountForLoanProduct(loanProductId,
                    CashAccountsForLoan.INCOME_FROM_CHARGE_OFF_FEES.getValue(), paymentTypeId);
            if (accountMap.containsKey(account)) {
                BigDecimal amount = accountMap.get(account).add(feesAmount);
                accountMap.put(account, amount);
            } else {
                accountMap.put(account, feesAmount);
            }
        }

        // handle penalties payment
        if (penaltiesAmount != null && penaltiesAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(penaltiesAmount);
            GLAccount account = this.helper.getLinkedGLAccountForLoanProduct(loanProductId,
                    CashAccountsForLoan.INCOME_FROM_CHARGE_OFF_PENALTY.getValue(), paymentTypeId);
            if (accountMap.containsKey(account)) {
                BigDecimal amount = accountMap.get(account).add(penaltiesAmount);
                accountMap.put(account, amount);
            } else {
                accountMap.put(account, penaltiesAmount);
            }
        }

        // handle overpayment
        if (overPaymentAmount != null && overPaymentAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(overPaymentAmount);
            GLAccount account = this.helper.getLinkedGLAccountForLoanProduct(loanProductId, CashAccountsForLoan.OVERPAYMENT.getValue(),
                    paymentTypeId);
            if (accountMap.containsKey(account)) {
                BigDecimal amount = accountMap.get(account).add(overPaymentAmount);
                accountMap.put(account, amount);
            } else {
                accountMap.put(account, overPaymentAmount);
            }
        }

        for (Map.Entry<GLAccount, BigDecimal> entry : accountMap.entrySet()) {
            this.helper.createCreditJournalEntryForLoan(office, currencyCode, loanId, transactionId, transactionDate, entry.getValue(),
                    entry.getKey());
        }

        if (totalDebitAmount.compareTo(BigDecimal.ZERO) > 0) {
            Long chargeId = loanTransactionDTO.getLoanChargeData().getChargeId();
            Integer accountMappingTypeId;
            if (loanTransactionDTO.getLoanChargeData().isPenalty()) {
                accountMappingTypeId = CashAccountsForLoan.INCOME_FROM_PENALTIES.getValue();
            } else {
                accountMappingTypeId = CashAccountsForLoan.INCOME_FROM_FEES.getValue();
            }
            this.helper.createDebitJournalEntryForLoanCharges(office, currencyCode, accountMappingTypeId, loanProductId, chargeId, loanId,
                    transactionId, transactionDate, totalDebitAmount);
        }
    }

    private void createJournalEntriesForLoanChargeAdjustment(LoanDTO loanDTO, LoanTransactionDTO loanTransactionDTO, Office office) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();
        final Long loanId = loanDTO.getLoanId();
        final String currencyCode = loanDTO.getCurrencyCode();

        // transaction properties
        final String transactionId = loanTransactionDTO.getTransactionId();
        final LocalDate transactionDate = loanTransactionDTO.getTransactionDate();
        final BigDecimal principalAmount = loanTransactionDTO.getPrincipal();
        final BigDecimal interestAmount = loanTransactionDTO.getInterest();
        final BigDecimal feesAmount = loanTransactionDTO.getFees();
        final BigDecimal penaltiesAmount = loanTransactionDTO.getPenalties();
        final BigDecimal overPaymentAmount = loanTransactionDTO.getOverPayment();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        BigDecimal totalDebitAmount = new BigDecimal(0);

        Map<GLAccount, BigDecimal> accountMap = new LinkedHashMap<>();

        // handle principal payment
        if (principalAmount != null && principalAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(principalAmount);
            GLAccount account = this.helper.getLinkedGLAccountForLoanProduct(loanProductId, CashAccountsForLoan.LOAN_PORTFOLIO.getValue(),
                    paymentTypeId);
            accountMap.put(account, principalAmount);
        }

        // handle interest payment
        if (interestAmount != null && interestAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(interestAmount);
            GLAccount account = this.helper.getLinkedGLAccountForLoanProduct(loanProductId,
                    CashAccountsForLoan.INTEREST_ON_LOANS.getValue(), paymentTypeId);
            if (accountMap.containsKey(account)) {
                BigDecimal amount = accountMap.get(account).add(interestAmount);
                accountMap.put(account, amount);
            } else {
                accountMap.put(account, interestAmount);
            }
        }

        // handle fees payment
        if (feesAmount != null && feesAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(feesAmount);
            GLAccount account = this.helper.getLinkedGLAccountForLoanProduct(loanProductId, CashAccountsForLoan.INCOME_FROM_FEES.getValue(),
                    paymentTypeId);
            if (accountMap.containsKey(account)) {
                BigDecimal amount = accountMap.get(account).add(feesAmount);
                accountMap.put(account, amount);
            } else {
                accountMap.put(account, feesAmount);
            }
        }

        // handle penalties payment
        if (penaltiesAmount != null && penaltiesAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(penaltiesAmount);
            GLAccount account = this.helper.getLinkedGLAccountForLoanProduct(loanProductId,
                    CashAccountsForLoan.INCOME_FROM_PENALTIES.getValue(), paymentTypeId);
            if (accountMap.containsKey(account)) {
                BigDecimal amount = accountMap.get(account).add(penaltiesAmount);
                accountMap.put(account, amount);
            } else {
                accountMap.put(account, penaltiesAmount);
            }
        }

        // handle overpayment
        if (overPaymentAmount != null && overPaymentAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(overPaymentAmount);
            GLAccount account = this.helper.getLinkedGLAccountForLoanProduct(loanProductId, CashAccountsForLoan.OVERPAYMENT.getValue(),
                    paymentTypeId);
            if (accountMap.containsKey(account)) {
                BigDecimal amount = accountMap.get(account).add(overPaymentAmount);
                accountMap.put(account, amount);
            } else {
                accountMap.put(account, overPaymentAmount);
            }
        }

        for (Map.Entry<GLAccount, BigDecimal> entry : accountMap.entrySet()) {
            this.helper.createCreditJournalEntryForLoan(office, currencyCode, loanId, transactionId, transactionDate, entry.getValue(),
                    entry.getKey());
        }

        if (totalDebitAmount.compareTo(BigDecimal.ZERO) > 0) {
            Long chargeId = loanTransactionDTO.getLoanChargeData().getChargeId();
            Integer accountMappingTypeId;
            if (loanTransactionDTO.getLoanChargeData().isPenalty()) {
                accountMappingTypeId = CashAccountsForLoan.INCOME_FROM_PENALTIES.getValue();
            } else {
                accountMappingTypeId = CashAccountsForLoan.INCOME_FROM_FEES.getValue();
            }
            this.helper.createDebitJournalEntryForLoanCharges(office, currencyCode, accountMappingTypeId, loanProductId, chargeId, loanId,
                    transactionId, transactionDate, totalDebitAmount);
        }
    }

    /**
     * Handle chargeback journal entry creation
     *
     * @param loanDTO
     * @param loanTransactionDTO
     * @param office
     */
    private void createJournalEntriesForChargeback(LoanDTO loanDTO, LoanTransactionDTO loanTransactionDTO, Office office) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();
        final Long loanId = loanDTO.getLoanId();
        final String currencyCode = loanDTO.getCurrencyCode();

        // transaction properties
        final String transactionId = loanTransactionDTO.getTransactionId();
        final LocalDate transactionDate = loanTransactionDTO.getTransactionDate();
        final BigDecimal amount = loanTransactionDTO.getAmount();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        this.helper.createJournalEntriesForLoan(office, currencyCode, CashAccountsForLoan.LOAN_PORTFOLIO.getValue(),
                CashAccountsForLoan.FUND_SOURCE.getValue(), loanProductId, paymentTypeId, loanId, transactionId, transactionDate, amount);
    }

    /**
     * Debit loan Portfolio and credit Fund source for a Disbursement <br/>
     *
     * @param loanDTO
     * @param loanTransactionDTO
     * @param office
     */
    private void createJournalEntriesForDisbursements(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final Office office) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();
        final Long loanId = loanDTO.getLoanId();
        final String currencyCode = loanDTO.getCurrencyCode();

        // transaction properties
        final String transactionId = loanTransactionDTO.getTransactionId();
        final LocalDate transactionDate = loanTransactionDTO.getTransactionDate();
        final BigDecimal overpaymentPortion = loanTransactionDTO.getOverPayment() != null ? loanTransactionDTO.getOverPayment()
                : BigDecimal.ZERO;
        final BigDecimal principalPortion = loanTransactionDTO.getAmount().subtract(overpaymentPortion);
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        if (MathUtil.isGreaterThanZero(principalPortion)) {
            this.helper.createDebitJournalEntryForLoan(office, currencyCode, CashAccountsForLoan.LOAN_PORTFOLIO.getValue(), loanProductId,
                    paymentTypeId, loanId, transactionId, transactionDate, principalPortion);
        }

        if (MathUtil.isGreaterThanZero(overpaymentPortion)) {
            this.helper.createDebitJournalEntryForLoan(office, currencyCode, CashAccountsForLoan.OVERPAYMENT.getValue(), loanProductId,
                    paymentTypeId, loanId, transactionId, transactionDate, overpaymentPortion);
        }
        if (loanTransactionDTO.isLoanToLoanTransfer()) {
            this.helper.createCreditJournalEntryForLoan(office, currencyCode, FinancialActivity.ASSET_TRANSFER.getValue(), loanProductId,
                    paymentTypeId, loanId, transactionId, transactionDate, loanTransactionDTO.getAmount());
        } else if (loanTransactionDTO.isAccountTransfer()) {
            this.helper.createCreditJournalEntryForLoan(office, currencyCode, FinancialActivity.LIABILITY_TRANSFER.getValue(),
                    loanProductId, paymentTypeId, loanId, transactionId, transactionDate, loanTransactionDTO.getAmount());
        } else {
            this.helper.createCreditJournalEntryForLoan(office, currencyCode, CashAccountsForLoan.FUND_SOURCE.getValue(), loanProductId,
                    paymentTypeId, loanId, transactionId, transactionDate, loanTransactionDTO.getAmount());
        }
    }

    /**
     * Debit loan Portfolio and credit Fund source for a Disbursement <br/>
     *
     * @param loanDTO
     * @param loanTransactionDTO
     * @param office
     */
    private void createJournalEntriesForRefund(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO, final Office office) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();
        final Long loanId = loanDTO.getLoanId();
        final String currencyCode = loanDTO.getCurrencyCode();

        // transaction properties
        final String transactionId = loanTransactionDTO.getTransactionId();
        final LocalDate transactionDate = loanTransactionDTO.getTransactionDate();
        final BigDecimal refundAmount = loanTransactionDTO.getAmount();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        if (loanTransactionDTO.isAccountTransfer()) {
            this.helper.createJournalEntriesForLoan(office, currencyCode, CashAccountsForLoan.OVERPAYMENT.getValue(),
                    FinancialActivity.LIABILITY_TRANSFER.getValue(), loanProductId, paymentTypeId, loanId, transactionId, transactionDate,
                    refundAmount);
        } else {
            this.helper.createJournalEntriesForLoan(office, currencyCode, CashAccountsForLoan.OVERPAYMENT.getValue(),
                    CashAccountsForLoan.FUND_SOURCE.getValue(), loanProductId, paymentTypeId, loanId, transactionId, transactionDate,
                    refundAmount);
        }
    }

    /**
     * Create a single Debit to fund source and multiple credits if applicable (loan portfolio for principal repayments,
     * Interest on loans for interest repayments, Income from fees for fees payment and Income from penalties for
     * penalty payment)
     */
    private void createJournalEntriesForRepayments(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final Office office) {

        final boolean isMarkedChargeOff = loanDTO.isMarkedAsChargeOff();
        if (isMarkedChargeOff) {
            createJournalEntriesForChargeOffLoanRepayments(loanDTO, loanTransactionDTO, office);
        } else {
            createJournalEntriesForLoanRepayments(loanDTO, loanTransactionDTO, office);
        }

    }

    private void createJournalEntriesForChargeOffLoanRepayments(LoanDTO loanDTO, LoanTransactionDTO loanTransactionDTO, Office office) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();
        final Long loanId = loanDTO.getLoanId();
        final String currencyCode = loanDTO.getCurrencyCode();
        final boolean isMarkedFraud = loanDTO.isMarkedAsFraud();

        // transaction properties
        final String transactionId = loanTransactionDTO.getTransactionId();
        final LocalDate transactionDate = loanTransactionDTO.getTransactionDate();
        final BigDecimal principalAmount = loanTransactionDTO.getPrincipal();
        final BigDecimal interestAmount = loanTransactionDTO.getInterest();
        final BigDecimal feesAmount = loanTransactionDTO.getFees();
        final BigDecimal penaltiesAmount = loanTransactionDTO.getPenalties();
        final BigDecimal overPaymentAmount = loanTransactionDTO.getOverPayment();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        GLAccountBalanceHolder glAccountBalanceHolder = new GLAccountBalanceHolder();
        BigDecimal totalDebitAmount = new BigDecimal(0);

        // principal payment
        if (principalAmount != null && principalAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(principalAmount);
            if (loanTransactionDTO.getTransactionType().isMerchantIssuedRefund()) {
                if (isMarkedFraud) {
                    populateCreditDebitMaps(loanProductId, principalAmount, paymentTypeId,
                            CashAccountsForLoan.CHARGE_OFF_FRAUD_EXPENSE.getValue(), CashAccountsForLoan.FUND_SOURCE.getValue(),
                            glAccountBalanceHolder);
                } else {
                    populateCreditDebitMaps(loanProductId, principalAmount, paymentTypeId,
                            CashAccountsForLoan.CHARGE_OFF_EXPENSE.getValue(), CashAccountsForLoan.FUND_SOURCE.getValue(),
                            glAccountBalanceHolder);
                }
            } else if (loanTransactionDTO.getTransactionType().isPayoutRefund()) {
                if (isMarkedFraud) {
                    populateCreditDebitMaps(loanProductId, principalAmount, paymentTypeId,
                            CashAccountsForLoan.CHARGE_OFF_FRAUD_EXPENSE.getValue(), CashAccountsForLoan.FUND_SOURCE.getValue(),
                            glAccountBalanceHolder);

                } else {
                    populateCreditDebitMaps(loanProductId, principalAmount, paymentTypeId,
                            CashAccountsForLoan.CHARGE_OFF_EXPENSE.getValue(), CashAccountsForLoan.FUND_SOURCE.getValue(),
                            glAccountBalanceHolder);
                }

            } else if (loanTransactionDTO.getTransactionType().isGoodwillCredit()) {
                populateCreditDebitMaps(loanProductId, principalAmount, paymentTypeId, CashAccountsForLoan.INCOME_FROM_RECOVERY.getValue(),
                        CashAccountsForLoan.GOODWILL_CREDIT.getValue(), glAccountBalanceHolder);

            } else if (loanTransactionDTO.getTransactionType().isRepayment()) {
                populateCreditDebitMaps(loanProductId, principalAmount, paymentTypeId, CashAccountsForLoan.INCOME_FROM_RECOVERY.getValue(),
                        CashAccountsForLoan.FUND_SOURCE.getValue(), glAccountBalanceHolder);

            } else {
                populateCreditDebitMaps(loanProductId, principalAmount, paymentTypeId, CashAccountsForLoan.LOAN_PORTFOLIO.getValue(),
                        CashAccountsForLoan.FUND_SOURCE.getValue(), glAccountBalanceHolder);
            }

        }

        // interest payment
        if (interestAmount != null && interestAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(interestAmount);
            if (loanTransactionDTO.getTransactionType().isMerchantIssuedRefund()) {
                populateCreditDebitMaps(loanProductId, interestAmount, paymentTypeId,
                        CashAccountsForLoan.INCOME_FROM_CHARGE_OFF_INTEREST.getValue(), CashAccountsForLoan.FUND_SOURCE.getValue(),
                        glAccountBalanceHolder);

            } else if (loanTransactionDTO.getTransactionType().isPayoutRefund()) {
                populateCreditDebitMaps(loanProductId, interestAmount, paymentTypeId,
                        CashAccountsForLoan.INCOME_FROM_CHARGE_OFF_INTEREST.getValue(), CashAccountsForLoan.FUND_SOURCE.getValue(),
                        glAccountBalanceHolder);

            } else if (loanTransactionDTO.getTransactionType().isGoodwillCredit()) {
                populateCreditDebitMaps(loanProductId, interestAmount, paymentTypeId, CashAccountsForLoan.INCOME_FROM_RECOVERY.getValue(),
                        CashAccountsForLoan.INCOME_FROM_GOODWILL_CREDIT_INTEREST.getValue(), glAccountBalanceHolder);
            } else if (loanTransactionDTO.getTransactionType().isRepayment()) {
                populateCreditDebitMaps(loanProductId, interestAmount, paymentTypeId, CashAccountsForLoan.INCOME_FROM_RECOVERY.getValue(),
                        CashAccountsForLoan.FUND_SOURCE.getValue(), glAccountBalanceHolder);

            } else {
                populateCreditDebitMaps(loanProductId, interestAmount, paymentTypeId, CashAccountsForLoan.INTEREST_ON_LOANS.getValue(),
                        CashAccountsForLoan.FUND_SOURCE.getValue(), glAccountBalanceHolder);
            }

        }

        // handle fees payment
        if (feesAmount != null && feesAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(feesAmount);
            if (loanTransactionDTO.getTransactionType().isMerchantIssuedRefund()) {
                populateCreditDebitMaps(loanProductId, feesAmount, paymentTypeId,
                        CashAccountsForLoan.INCOME_FROM_CHARGE_OFF_FEES.getValue(), CashAccountsForLoan.FUND_SOURCE.getValue(),
                        glAccountBalanceHolder);

            } else if (loanTransactionDTO.getTransactionType().isPayoutRefund()) {
                populateCreditDebitMaps(loanProductId, feesAmount, paymentTypeId,
                        CashAccountsForLoan.INCOME_FROM_CHARGE_OFF_FEES.getValue(), CashAccountsForLoan.FUND_SOURCE.getValue(),
                        glAccountBalanceHolder);

            } else if (loanTransactionDTO.getTransactionType().isGoodwillCredit()) {
                populateCreditDebitMaps(loanProductId, feesAmount, paymentTypeId, CashAccountsForLoan.INCOME_FROM_RECOVERY.getValue(),
                        CashAccountsForLoan.INCOME_FROM_GOODWILL_CREDIT_FEES.getValue(), glAccountBalanceHolder);

            } else if (loanTransactionDTO.getTransactionType().isRepayment()) {
                populateCreditDebitMaps(loanProductId, feesAmount, paymentTypeId, CashAccountsForLoan.INCOME_FROM_RECOVERY.getValue(),
                        CashAccountsForLoan.FUND_SOURCE.getValue(), glAccountBalanceHolder);

            } else {
                populateCreditDebitMaps(loanProductId, feesAmount, paymentTypeId, CashAccountsForLoan.INCOME_FROM_FEES.getValue(),
                        CashAccountsForLoan.FUND_SOURCE.getValue(), glAccountBalanceHolder);
            }

        }

        // handle penalties payment
        if (penaltiesAmount != null && penaltiesAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(penaltiesAmount);
            if (loanTransactionDTO.getTransactionType().isMerchantIssuedRefund()) {
                populateCreditDebitMaps(loanProductId, penaltiesAmount, paymentTypeId,
                        CashAccountsForLoan.INCOME_FROM_CHARGE_OFF_PENALTY.getValue(), CashAccountsForLoan.FUND_SOURCE.getValue(),
                        glAccountBalanceHolder);

            } else if (loanTransactionDTO.getTransactionType().isPayoutRefund()) {
                populateCreditDebitMaps(loanProductId, penaltiesAmount, paymentTypeId,
                        CashAccountsForLoan.INCOME_FROM_CHARGE_OFF_PENALTY.getValue(), CashAccountsForLoan.FUND_SOURCE.getValue(),
                        glAccountBalanceHolder);

            } else if (loanTransactionDTO.getTransactionType().isGoodwillCredit()) {
                populateCreditDebitMaps(loanProductId, penaltiesAmount, paymentTypeId, CashAccountsForLoan.INCOME_FROM_RECOVERY.getValue(),
                        CashAccountsForLoan.INCOME_FROM_GOODWILL_CREDIT_PENALTY.getValue(), glAccountBalanceHolder);

            } else if (loanTransactionDTO.getTransactionType().isRepayment()) {
                populateCreditDebitMaps(loanProductId, penaltiesAmount, paymentTypeId, CashAccountsForLoan.INCOME_FROM_RECOVERY.getValue(),
                        CashAccountsForLoan.FUND_SOURCE.getValue(), glAccountBalanceHolder);

            } else {
                populateCreditDebitMaps(loanProductId, penaltiesAmount, paymentTypeId, CashAccountsForLoan.INCOME_FROM_PENALTIES.getValue(),
                        CashAccountsForLoan.FUND_SOURCE.getValue(), glAccountBalanceHolder);
            }

        }

        // overpayment
        if (overPaymentAmount != null && overPaymentAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(overPaymentAmount);
            if (loanTransactionDTO.getTransactionType().isMerchantIssuedRefund()) {
                populateCreditDebitMaps(loanProductId, overPaymentAmount, paymentTypeId, CashAccountsForLoan.OVERPAYMENT.getValue(),
                        CashAccountsForLoan.FUND_SOURCE.getValue(), glAccountBalanceHolder);

            } else if (loanTransactionDTO.getTransactionType().isPayoutRefund()) {
                populateCreditDebitMaps(loanProductId, overPaymentAmount, paymentTypeId, CashAccountsForLoan.OVERPAYMENT.getValue(),
                        CashAccountsForLoan.FUND_SOURCE.getValue(), glAccountBalanceHolder);

            } else if (loanTransactionDTO.getTransactionType().isGoodwillCredit()) {
                populateCreditDebitMaps(loanProductId, overPaymentAmount, paymentTypeId, CashAccountsForLoan.OVERPAYMENT.getValue(),
                        CashAccountsForLoan.GOODWILL_CREDIT.getValue(), glAccountBalanceHolder);
            } else {
                populateCreditDebitMaps(loanProductId, overPaymentAmount, paymentTypeId, CashAccountsForLoan.OVERPAYMENT.getValue(),
                        CashAccountsForLoan.FUND_SOURCE.getValue(), glAccountBalanceHolder);
            }

        }

        // create credit entries
        for (Map.Entry<Long, BigDecimal> creditEntry : glAccountBalanceHolder.getCreditBalances().entrySet()) {
            GLAccount glAccount = glAccountBalanceHolder.getGlAccountMap().get(creditEntry.getKey());
            this.helper.createCreditJournalEntryForLoan(office, currencyCode, loanId, transactionId, transactionDate,
                    creditEntry.getValue(), glAccount);
        }

        /*** create a single debit entry for the entire amount **/
        if (loanTransactionDTO.isLoanToLoanTransfer()) {
            this.helper.createDebitJournalEntryForLoan(office, currencyCode, FinancialActivity.ASSET_TRANSFER.getValue(), loanProductId,
                    paymentTypeId, loanId, transactionId, transactionDate, totalDebitAmount);
        } else if (loanTransactionDTO.isAccountTransfer()) {
            this.helper.createDebitJournalEntryForLoan(office, currencyCode, FinancialActivity.LIABILITY_TRANSFER.getValue(), loanProductId,
                    paymentTypeId, loanId, transactionId, transactionDate, totalDebitAmount);
        } else {
            // create debit entries
            for (Map.Entry<Long, BigDecimal> debitEntry : glAccountBalanceHolder.getDebitBalances().entrySet()) {
                GLAccount glAccount = glAccountBalanceHolder.getGlAccountMap().get(debitEntry.getKey());
                this.helper.createDebitJournalEntryForLoan(office, currencyCode, loanId, transactionId, transactionDate,
                        debitEntry.getValue(), glAccount);
            }
        }

        /**
         * Charge Refunds have an extra refund related pair of journal entries in addition to those related to the
         * repayment above
         ***/
        if (totalDebitAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (loanTransactionDTO.getTransactionType().isChargeRefund()) {
                Integer incomeAccount = this.helper.getValueForFeeOrPenaltyIncomeAccount(loanTransactionDTO.getChargeRefundChargeType());
                this.helper.createJournalEntriesForLoan(office, currencyCode, incomeAccount, CashAccountsForLoan.FUND_SOURCE.getValue(),
                        loanProductId, paymentTypeId, loanId, transactionId, transactionDate, totalDebitAmount);
            }
        }
    }

    private void createJournalEntriesForLoanRepayments(LoanDTO loanDTO, LoanTransactionDTO loanTransactionDTO, Office office) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();
        final Long loanId = loanDTO.getLoanId();
        final String currencyCode = loanDTO.getCurrencyCode();

        // transaction properties
        final String transactionId = loanTransactionDTO.getTransactionId();
        final LocalDate transactionDate = loanTransactionDTO.getTransactionDate();
        final BigDecimal principalAmount = loanTransactionDTO.getPrincipal();
        final BigDecimal interestAmount = loanTransactionDTO.getInterest();
        final BigDecimal feesAmount = loanTransactionDTO.getFees();
        final BigDecimal penaltiesAmount = loanTransactionDTO.getPenalties();
        final BigDecimal overPaymentAmount = loanTransactionDTO.getOverPayment();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        BigDecimal totalDebitAmount = new BigDecimal(0);
        Map<Integer, BigDecimal> debitAccountMapForGoodwillCredit = new LinkedHashMap<>();

        if (principalAmount != null && principalAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(principalAmount);
            this.helper.createCreditJournalEntryForLoan(office, currencyCode, CashAccountsForLoan.LOAN_PORTFOLIO, loanProductId,
                    paymentTypeId, loanId, transactionId, transactionDate, principalAmount);
            if (loanTransactionDTO.getTransactionType().isGoodwillCredit()) {
                populateDebitAccountEntry(loanProductId, principalAmount, CashAccountsForLoan.GOODWILL_CREDIT.getValue(),
                        debitAccountMapForGoodwillCredit, paymentTypeId);
            }
        }

        if (interestAmount != null && interestAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(interestAmount);
            this.helper.createCreditJournalEntryForLoan(office, currencyCode, CashAccountsForLoan.INTEREST_ON_LOANS, loanProductId,
                    paymentTypeId, loanId, transactionId, transactionDate, interestAmount);
            if (loanTransactionDTO.getTransactionType().isGoodwillCredit()) {
                populateDebitAccountEntry(loanProductId, interestAmount,
                        CashAccountsForLoan.INCOME_FROM_GOODWILL_CREDIT_INTEREST.getValue(), debitAccountMapForGoodwillCredit,
                        paymentTypeId);
            }
        }

        if (feesAmount != null && feesAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(feesAmount);
            this.helper.createCreditJournalEntryForLoanCharges(office, currencyCode, CashAccountsForLoan.INCOME_FROM_FEES.getValue(),
                    loanProductId, loanId, transactionId, transactionDate, feesAmount, loanTransactionDTO.getFeePayments());
            if (loanTransactionDTO.getTransactionType().isGoodwillCredit()) {
                populateDebitAccountEntry(loanProductId, feesAmount, CashAccountsForLoan.INCOME_FROM_GOODWILL_CREDIT_FEES.getValue(),
                        debitAccountMapForGoodwillCredit, paymentTypeId);
            }
        }

        if (penaltiesAmount != null && penaltiesAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(penaltiesAmount);
            this.helper.createCreditJournalEntryForLoanCharges(office, currencyCode, CashAccountsForLoan.INCOME_FROM_PENALTIES.getValue(),
                    loanProductId, loanId, transactionId, transactionDate, penaltiesAmount, loanTransactionDTO.getPenaltyPayments());
            if (loanTransactionDTO.getTransactionType().isGoodwillCredit()) {
                populateDebitAccountEntry(loanProductId, penaltiesAmount,
                        CashAccountsForLoan.INCOME_FROM_GOODWILL_CREDIT_PENALTY.getValue(), debitAccountMapForGoodwillCredit,
                        paymentTypeId);
            }
        }

        if (overPaymentAmount != null && overPaymentAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(overPaymentAmount);
            this.helper.createCreditJournalEntryForLoan(office, currencyCode, CashAccountsForLoan.OVERPAYMENT, loanProductId, paymentTypeId,
                    loanId, transactionId, transactionDate, overPaymentAmount);
            if (loanTransactionDTO.getTransactionType().isGoodwillCredit()) {
                populateDebitAccountEntry(loanProductId, overPaymentAmount, CashAccountsForLoan.GOODWILL_CREDIT.getValue(),
                        debitAccountMapForGoodwillCredit, paymentTypeId);
            }
        }

        /*** create a single debit entry for the entire amount **/
        if (loanTransactionDTO.isLoanToLoanTransfer()) {
            this.helper.createDebitJournalEntryForLoan(office, currencyCode, FinancialActivity.ASSET_TRANSFER.getValue(), loanProductId,
                    paymentTypeId, loanId, transactionId, transactionDate, totalDebitAmount);
        } else if (loanTransactionDTO.isAccountTransfer()) {
            this.helper.createDebitJournalEntryForLoan(office, currencyCode, FinancialActivity.LIABILITY_TRANSFER.getValue(), loanProductId,
                    paymentTypeId, loanId, transactionId, transactionDate, totalDebitAmount);
        } else {
            if (loanTransactionDTO.getTransactionType().isGoodwillCredit()) {

                // create debit entries
                for (Map.Entry<Integer, BigDecimal> debitEntry : debitAccountMapForGoodwillCredit.entrySet()) {
                    this.helper.createDebitJournalEntryForLoan(office, currencyCode, debitEntry.getKey(), loanProductId, paymentTypeId,
                            loanId, transactionId, transactionDate, debitEntry.getValue());
                }

            } else {
                this.helper.createDebitJournalEntryForLoan(office, currencyCode, CashAccountsForLoan.FUND_SOURCE.getValue(), loanProductId,
                        paymentTypeId, loanId, transactionId, transactionDate, totalDebitAmount);
            }
        }

        /**
         * Charge Refunds have an extra refund related pair of journal entries in addition to those related to the
         * repayment above
         ***/
        if (totalDebitAmount.compareTo(BigDecimal.ZERO) > 0 && loanTransactionDTO.getTransactionType().isChargeRefund()) {
            Integer incomeAccount = this.helper.getValueForFeeOrPenaltyIncomeAccount(loanTransactionDTO.getChargeRefundChargeType());
            this.helper.createJournalEntriesForLoan(office, currencyCode, incomeAccount, CashAccountsForLoan.FUND_SOURCE.getValue(),
                    loanProductId, paymentTypeId, loanId, transactionId, transactionDate, totalDebitAmount);
        }
    }

    private void populateDebitAccountEntry(Long loanProductId, BigDecimal transactionPartAmount, Integer debitAccountType,
            Map<Integer, BigDecimal> accountMapForDebit, Long paymentTypeId) {
        Integer accountDebit = returnExistingDebitAccountInMapMatchingGLAccount(loanProductId, paymentTypeId, debitAccountType,
                accountMapForDebit);
        if (accountMapForDebit.containsKey(accountDebit)) {
            BigDecimal amount = accountMapForDebit.get(accountDebit).add(transactionPartAmount);
            accountMapForDebit.put(accountDebit, amount);
        } else {
            accountMapForDebit.put(accountDebit, transactionPartAmount);
        }
    }

    /**
     * Create a single Debit to fund source and a single credit to "Income from Recovery"
     */
    private void createJournalEntriesForRecoveryRepayments(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final Office office) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();
        final Long loanId = loanDTO.getLoanId();
        final String currencyCode = loanDTO.getCurrencyCode();

        // transaction properties
        final String transactionId = loanTransactionDTO.getTransactionId();
        final LocalDate transactionDate = loanTransactionDTO.getTransactionDate();
        final BigDecimal amount = loanTransactionDTO.getAmount();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        this.helper.createJournalEntriesForLoan(office, currencyCode, CashAccountsForLoan.FUND_SOURCE.getValue(),
                CashAccountsForLoan.INCOME_FROM_RECOVERY.getValue(), loanProductId, paymentTypeId, loanId, transactionId, transactionDate,
                amount);

    }

    /**
     * Credit loan Portfolio and Debit Suspense Account for a Transfer Initiation. A Transfer acceptance would be
     * treated the opposite i.e Debit Loan Portfolio and Credit Suspense Account <br/>
     *
     * All debits are turned into credits and vice versa in case of Transfer Initiation disbursals
     *
     *
     * @param loanDTO
     * @param loanTransactionDTO
     * @param office
     */
    private void createJournalEntriesForTransfers(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO, final Office office) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();
        final Long loanId = loanDTO.getLoanId();
        final String currencyCode = loanDTO.getCurrencyCode();

        // transaction properties
        final String transactionId = loanTransactionDTO.getTransactionId();
        final LocalDate transactionDate = loanTransactionDTO.getTransactionDate();
        final BigDecimal principalAmount = loanTransactionDTO.getPrincipal();

        if (loanTransactionDTO.getTransactionType().isInitiateTransfer()) {
            this.helper.createJournalEntriesForLoan(office, currencyCode, CashAccountsForLoan.TRANSFERS_SUSPENSE.getValue(),
                    CashAccountsForLoan.LOAN_PORTFOLIO.getValue(), loanProductId, null, loanId, transactionId, transactionDate,
                    principalAmount);
        } else if (loanTransactionDTO.getTransactionType().isApproveTransfer()
                || loanTransactionDTO.getTransactionType().isWithdrawTransfer()) {
            this.helper.createJournalEntriesForLoan(office, currencyCode, CashAccountsForLoan.LOAN_PORTFOLIO.getValue(),
                    CashAccountsForLoan.TRANSFERS_SUSPENSE.getValue(), loanProductId, null, loanId, transactionId, transactionDate,
                    principalAmount);
        }
    }

    private void createJournalEntriesForCreditBalanceRefund(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final Office office) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();
        final Long loanId = loanDTO.getLoanId();
        final String currencyCode = loanDTO.getCurrencyCode();

        // transaction properties
        final String transactionId = loanTransactionDTO.getTransactionId();
        final LocalDate transactionDate = loanTransactionDTO.getTransactionDate();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        BigDecimal overpaymentAmount = loanTransactionDTO.getOverPayment();
        BigDecimal principalAmount = loanTransactionDTO.getPrincipal();

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<JournalAmountHolder> journalAmountHolders = new ArrayList<>();

        if (principalAmount != null && principalAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalAmount = totalAmount.add(principalAmount);
            journalAmountHolders.add(new JournalAmountHolder(CashAccountsForLoan.LOAN_PORTFOLIO.getValue(), principalAmount));
        }
        if (overpaymentAmount != null && overpaymentAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalAmount = totalAmount.add(overpaymentAmount);
            journalAmountHolders.add(new JournalAmountHolder(CashAccountsForLoan.OVERPAYMENT.getValue(), overpaymentAmount));
        }

        JournalAmountHolder totalAmountHolder = new JournalAmountHolder(CashAccountsForLoan.FUND_SOURCE.getValue(), totalAmount);
        helper.createSplitJournalEntriesForLoan(office, currencyCode, journalAmountHolders, totalAmountHolder, loanProductId, paymentTypeId,
                loanId, transactionId, transactionDate);
    }

    private void createJournalEntriesForRefundForActiveLoan(LoanDTO loanDTO, LoanTransactionDTO loanTransactionDTO, Office office) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();
        final Long loanId = loanDTO.getLoanId();
        final String currencyCode = loanDTO.getCurrencyCode();

        // transaction properties
        final String transactionId = loanTransactionDTO.getTransactionId();
        final LocalDate transactionDate = loanTransactionDTO.getTransactionDate();
        final BigDecimal principalAmount = loanTransactionDTO.getPrincipal();
        final BigDecimal interestAmount = loanTransactionDTO.getInterest();
        final BigDecimal feesAmount = loanTransactionDTO.getFees();
        final BigDecimal penaltiesAmount = loanTransactionDTO.getPenalties();
        final BigDecimal overPaymentAmount = loanTransactionDTO.getOverPayment();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        BigDecimal totalDebitAmount = new BigDecimal(0);

        if (principalAmount != null && principalAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(principalAmount);
            this.helper.createDebitJournalEntryForLoan(office, currencyCode, CashAccountsForLoan.LOAN_PORTFOLIO.getValue(), loanProductId,
                    paymentTypeId, loanId, transactionId, transactionDate, principalAmount);
        }

        if (interestAmount != null && interestAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(interestAmount);
            this.helper.createDebitJournalEntryForLoan(office, currencyCode, CashAccountsForLoan.INTEREST_ON_LOANS.getValue(),
                    loanProductId, paymentTypeId, loanId, transactionId, transactionDate, interestAmount);
        }

        if (feesAmount != null && feesAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(feesAmount);

            List<ChargePaymentDTO> chargePaymentDTOs = new ArrayList<>();

            for (ChargePaymentDTO chargePaymentDTO : loanTransactionDTO.getFeePayments()) {
                chargePaymentDTOs.add(new ChargePaymentDTO(chargePaymentDTO.getChargeId(),
                        chargePaymentDTO.getAmount().floatValue() < 0 ? chargePaymentDTO.getAmount().multiply(new BigDecimal(-1))
                                : chargePaymentDTO.getAmount(),
                        chargePaymentDTO.getLoanChargeId()));
            }
            this.helper.createDebitJournalEntryForLoanCharges(office, currencyCode, CashAccountsForLoan.INCOME_FROM_FEES.getValue(),
                    loanProductId, loanId, transactionId, transactionDate, feesAmount, chargePaymentDTOs);
        }

        if (penaltiesAmount != null && penaltiesAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(penaltiesAmount);
            List<ChargePaymentDTO> chargePaymentDTOs = new ArrayList<>();

            for (ChargePaymentDTO chargePaymentDTO : loanTransactionDTO.getPenaltyPayments()) {
                chargePaymentDTOs.add(new ChargePaymentDTO(chargePaymentDTO.getChargeId(),
                        chargePaymentDTO.getAmount().floatValue() < 0 ? chargePaymentDTO.getAmount().multiply(new BigDecimal(-1))
                                : chargePaymentDTO.getAmount(),
                        chargePaymentDTO.getLoanChargeId()));
            }

            this.helper.createDebitJournalEntryForLoanCharges(office, currencyCode, CashAccountsForLoan.INCOME_FROM_PENALTIES.getValue(),
                    loanProductId, loanId, transactionId, transactionDate, penaltiesAmount, chargePaymentDTOs);
        }

        if (overPaymentAmount != null && overPaymentAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalDebitAmount = totalDebitAmount.add(overPaymentAmount);
            this.helper.createDebitJournalEntryForLoan(office, currencyCode, CashAccountsForLoan.OVERPAYMENT.getValue(), loanProductId,
                    paymentTypeId, loanId, transactionId, transactionDate, overPaymentAmount);
        }

        /*** create a single debit entry (or reversal) for the entire amount **/
        this.helper.createCreditJournalEntryForLoan(office, currencyCode, CashAccountsForLoan.FUND_SOURCE.getValue(), loanProductId,
                paymentTypeId, loanId, transactionId, transactionDate, totalDebitAmount);
    }
}
