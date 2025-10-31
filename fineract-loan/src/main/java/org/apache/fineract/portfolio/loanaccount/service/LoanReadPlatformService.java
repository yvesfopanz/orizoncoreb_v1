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
package org.apache.fineract.portfolio.loanaccount.service;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.apache.fineract.infrastructure.core.domain.ExternalId;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.portfolio.calendar.data.CalendarData;
import org.apache.fineract.portfolio.floatingrates.data.InterestRatePeriodData;
import org.apache.fineract.portfolio.loanaccount.data.DisbursementData;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.data.LoanApprovalData;
import org.apache.fineract.portfolio.loanaccount.data.LoanRepaymentScheduleInstallmentData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.data.PaidInAdvanceData;
import org.apache.fineract.portfolio.loanaccount.data.RepaymentScheduleRelatedLoanData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionRepaymentPeriodData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.OverdueLoanScheduleData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleType;
import org.springframework.data.domain.Pageable;

public interface LoanReadPlatformService {

    LoanAccountData retrieveOne(Long loanId);

    LoanAccountData fetchRepaymentScheduleData(LoanAccountData accountData);

    LoanScheduleData retrieveRepaymentSchedule(Long loanId, RepaymentScheduleRelatedLoanData repaymentScheduleRelatedData,
            Collection<DisbursementData> disbursementData, Collection<LoanTransactionRepaymentPeriodData> capitalizedIncomeData,
            boolean isInterestRecalculationEnabled, LoanScheduleType loanScheduleType);

    Collection<LoanTransactionData> retrieveLoanTransactions(Long loanId);

    org.springframework.data.domain.Page<LoanTransactionData> retrieveLoanTransactions(@NotNull Long loanId,
            Set<LoanTransactionType> excludedTransactionTypes, Pageable pageable);

    LoanAccountData retrieveTemplateWithClientAndProductDetails(Long clientId, Long productId);

    LoanAccountData retrieveTemplateWithGroupAndProductDetails(Long groupId, Long productId);

    LoanTransactionData retrieveLoanTransactionTemplate(Long loanId);

    LoanTransactionData retrieveLoanTransactionTemplate(Long loanId, LoanTransactionType transactionType, Long transactionId);

    LoanTransactionData retrieveWaiveInterestDetails(Long loanId);

    LoanTransactionData retrieveLoanTransaction(Long loanId, Long transactionId);

    LoanTransactionData retrieveNewClosureDetails();

    LoanTransactionData retrieveDisbursalTemplate(Long loanId, boolean paymentDetailsRequired);

    LoanApprovalData retrieveApprovalTemplate(Long loanId);

    LoanAccountData retrieveTemplateWithCompleteGroupAndProductDetails(Long groupId, Long productId);

    LoanAccountData retrieveLoanProductDetailsTemplate(Long productId, Long clientId, Long groupId);

    Collection<CalendarData> retrieveCalendars(Long groupId);

    Page<LoanAccountData> retrieveAll(SearchParameters searchParameters);

    Collection<StaffData> retrieveAllowedLoanOfficers(Long selectedOfficeId, boolean staffInSelectedOfficeOnly);

    /*
     * musoni-specific at present - will find overdue scheduled installments that have a special 'overdue charge'
     * associated with the loan product.
     *
     * The 'overdue-charge' is only ever applied once to an installment and as a result overdue installments with this
     * charge already applied are not returned.
     */
    Collection<OverdueLoanScheduleData> retrieveAllLoansWithOverdueInstallments(Long penaltyWaitPeriod, Boolean backdatePenalties);

    Collection<OverdueLoanScheduleData> retrieveAllOverdueInstallmentsForLoan(Loan loan);

    Integer retriveLoanCounter(Long groupId, Integer loanType, Long productId);

    Integer retriveLoanCounter(Long clientId, Long productId);

    Collection<DisbursementData> retrieveLoanDisbursementDetails(Long loanId);

    DisbursementData retrieveLoanDisbursementDetail(Long loanId, Long disbursementId);

    LoanTransactionData retrieveRecoveryPaymentTemplate(Long loanId);

    LoanTransactionData retrieveLoanWriteoffTemplate(Long loanId);

    LoanTransactionData retrieveLoanChargeOffTemplate(Long loanId);

    Collection<Long> fetchLoansForInterestRecalculation();

    List<Long> fetchLoansForInterestRecalculation(Integer pageSize, Long maxLoanIdInList, String officeHierarchy);

    LoanTransactionData retrieveLoanPrePaymentTemplate(LoanTransactionType repaymentTransactionType, Long loanId, LocalDate onDate);

    boolean isGuaranteeRequired(Long loanId);

    LocalDate retrieveMinimumDateOfRepaymentTransaction(Long loanId);

    PaidInAdvanceData retrieveTotalPaidInAdvance(Long loanId);

    LoanTransactionData retrieveRefundByCashTemplate(Long loanId);

    LoanTransactionData retrieveCreditBalanceRefundTemplate(Long loanId);

    Collection<InterestRatePeriodData> retrieveLoanInterestRatePeriodData(LoanAccountData loan);

    Collection<Long> retrieveLoanIdsWithPendingIncomePostingTransactions();

    LoanTransactionData retrieveLoanForeclosureTemplate(Long loanId, LocalDate transactionDate);

    LoanAccountData retrieveLoanByLoanAccount(String loanAccountNumber);

    Long retrieveLoanIdByAccountNumber(String loanAccountNumber);

    String retrieveAccountNumberByAccountId(Long accountId);

    Integer retrieveNumberOfActiveLoans();

    Integer retrieveNumberOfRepayments(Long loanId);

    List<LoanAccountData> retrieveGLIMChildLoansByGLIMParentAccount(String parentloanAccountNumber);

    List<LoanRepaymentScheduleInstallmentData> getRepaymentDataResponse(Long loanId);

    Long retrieveLoanTransactionIdByExternalId(ExternalId externalId);

    Long retrieveLoanIdByExternalId(ExternalId externalId);

    List<Long> retrieveLoanIdsByExternalIds(List<ExternalId> externalIds);

    boolean existsByLoanId(Long loanId);

    LoanTransactionData retrieveManualInterestRefundTemplate(Long loanId, Long targetTransactionId);

    Long getResolvedLoanId(ExternalId loanExternalId);

}
