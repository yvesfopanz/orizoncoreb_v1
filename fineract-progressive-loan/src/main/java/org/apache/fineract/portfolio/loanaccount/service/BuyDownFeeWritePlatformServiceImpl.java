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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.domain.ExternalId;
import org.apache.fineract.infrastructure.core.service.ExternalIdFactory;
import org.apache.fineract.infrastructure.core.service.MathUtil;
import org.apache.fineract.infrastructure.event.business.domain.loan.transaction.LoanBuyDownFeeAdjustmentTransactionCreatedBusinessEvent;
import org.apache.fineract.infrastructure.event.business.domain.loan.transaction.LoanBuyDownFeeTransactionCreatedBusinessEvent;
import org.apache.fineract.infrastructure.event.business.service.BusinessEventNotifierService;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.exception.ClientNotActiveException;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.exception.GroupNotActiveException;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanBuyDownFeeBalance;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionRelation;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionRelationTypeEnum;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionRepository;
import org.apache.fineract.portfolio.loanaccount.repository.LoanBuyDownFeeBalanceRepository;
import org.apache.fineract.portfolio.note.service.NoteWritePlatformService;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailWritePlatformService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class BuyDownFeeWritePlatformServiceImpl implements BuyDownFeePlatformService {

    private final ProgressiveLoanTransactionValidator loanTransactionValidator;
    private final LoanAssembler loanAssembler;
    private final LoanTransactionRepository loanTransactionRepository;
    private final PaymentDetailWritePlatformService paymentDetailWritePlatformService;
    private final LoanJournalEntryPoster loanJournalEntryPoster;
    private final NoteWritePlatformService noteWritePlatformService;
    private final ExternalIdFactory externalIdFactory;
    private final LoanBuyDownFeeBalanceRepository loanBuyDownFeeBalanceRepository;
    private final BusinessEventNotifierService businessEventNotifierService;

    @Transactional
    @Override
    public CommandProcessingResult makeLoanBuyDownFee(final Long loanId, final JsonCommand command) {

        this.loanTransactionValidator.validateBuyDownFee(command, loanId);

        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        checkClientOrGroupActive(loan);

        final Map<String, Object> changes = new LinkedHashMap<>();

        // Create payment details
        final PaymentDetail paymentDetail = this.paymentDetailWritePlatformService.createAndPersistPaymentDetail(command, changes);

        // Extract transaction details
        final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed("transactionAmount");
        final ExternalId txnExternalId = externalIdFactory.createFromCommand(command, "externalId");

        // Create buy down fee transaction
        final Money buyDownFeeAmount = Money.of(loan.getCurrency(), transactionAmount); // FLAT calculation
        final LoanTransaction buyDownFeeTransaction = LoanTransaction.buyDownFee(loan, buyDownFeeAmount, paymentDetail, transactionDate,
                txnExternalId);

        // Add to loan (NO schedule recalculation as per requirements)
        loan.addLoanTransaction(buyDownFeeTransaction);

        // Save transaction
        loanTransactionRepository.saveAndFlush(buyDownFeeTransaction);

        // Create Buy Down Fee balance
        createBuyDownFeeBalance(buyDownFeeTransaction);

        // Add note if provided
        final String noteText = command.stringValueOfParameterNamed("note");
        if (StringUtils.isNotBlank(noteText)) {
            noteWritePlatformService.createLoanTransactionNote(buyDownFeeTransaction.getId(), noteText);
        }

        loanJournalEntryPoster.postJournalEntriesForLoanTransaction(buyDownFeeTransaction, false, false);

        // Notify business events
        businessEventNotifierService.notifyPostBusinessEvent(new LoanBuyDownFeeTransactionCreatedBusinessEvent(buyDownFeeTransaction));

        return new CommandProcessingResultBuilder() //
                .withClientId(loan.getClientId()) //
                .withOfficeId(loan.getOfficeId()) //
                .withLoanId(loan.getId()) //
                .withEntityId(buyDownFeeTransaction.getId()) //
                .withEntityExternalId(buyDownFeeTransaction.getExternalId()) //
                .build();
    }

    @Override
    @Transactional
    public CommandProcessingResult buyDownFeeAdjustment(final Long loanId, final Long buyDownFeeTransactionId, final JsonCommand command) {
        this.loanTransactionValidator.validateBuyDownFeeAdjustment(command, loanId, buyDownFeeTransactionId);
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        checkClientOrGroupActive(loan);

        final Map<String, Object> changes = new LinkedHashMap<>();

        // Create payment details
        final PaymentDetail paymentDetail = this.paymentDetailWritePlatformService.createAndPersistPaymentDetail(command, changes);

        // Extract transaction details
        final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed("transactionAmount");
        final ExternalId txnExternalId = externalIdFactory.createFromCommand(command, "externalId");

        // Find and validate original buy down fee transaction
        Optional<LoanTransaction> originalBuyDownFee = loanTransactionRepository.findById(buyDownFeeTransactionId);
        if (originalBuyDownFee.isEmpty() || !originalBuyDownFee.get().isBuyDownFee()) {
            throw new IllegalArgumentException("Original transaction must be a valid Buy Down Fee transaction");
        }

        // Create buy down fee adjustment transaction
        LoanTransaction buyDownFeeAdjustment = LoanTransaction.buyDownFeeAdjustment(loan, Money.of(loan.getCurrency(), transactionAmount),
                paymentDetail, transactionDate, txnExternalId);

        // Link to original transaction
        buyDownFeeAdjustment.getLoanTransactionRelations().add(LoanTransactionRelation.linkToTransaction(buyDownFeeAdjustment,
                originalBuyDownFee.get(), LoanTransactionRelationTypeEnum.ADJUSTMENT));

        // Add transaction to loan
        loan.addLoanTransaction(buyDownFeeAdjustment);

        // Save transaction
        LoanTransaction savedBuyDownFeeAdjustment = loanTransactionRepository.saveAndFlush(buyDownFeeAdjustment);

        // Update buy down fee balance
        LoanBuyDownFeeBalance buydownFeeBalance = loanBuyDownFeeBalanceRepository.findByLoanIdAndLoanTransactionId(loanId,
                buyDownFeeTransactionId);
        if (buydownFeeBalance != null) {
            buydownFeeBalance.setAmountAdjustment(MathUtil.nullToZero(buydownFeeBalance.getAmountAdjustment()).add(transactionAmount));
            buydownFeeBalance
                    .setUnrecognizedAmount(MathUtil.negativeToZero(buydownFeeBalance.getUnrecognizedAmount().subtract(transactionAmount)));
            loanBuyDownFeeBalanceRepository.save(buydownFeeBalance);
        }

        // Create a note if provided
        final String noteText = command.stringValueOfParameterNamed("note");
        if (StringUtils.isNotBlank(noteText)) {
            noteWritePlatformService.createLoanTransactionNote(savedBuyDownFeeAdjustment.getId(), noteText);
        }

        loanJournalEntryPoster.postJournalEntriesForLoanTransaction(savedBuyDownFeeAdjustment, false, false);

        // Notify business events
        businessEventNotifierService
                .notifyPostBusinessEvent(new LoanBuyDownFeeAdjustmentTransactionCreatedBusinessEvent(savedBuyDownFeeAdjustment));

        return new CommandProcessingResultBuilder().withEntityId(savedBuyDownFeeAdjustment.getId())
                .withEntityExternalId(savedBuyDownFeeAdjustment.getExternalId()).withOfficeId(loan.getOfficeId())
                .withClientId(loan.getClientId()).withLoanId(loan.getId()).build();
    }

    private void checkClientOrGroupActive(final Loan loan) {
        final Client client = loan.client();
        if (client != null && client.isNotActive()) {
            throw new ClientNotActiveException(client.getId());
        }
        final Group group = loan.group();
        if (group != null && group.isNotActive()) {
            throw new GroupNotActiveException(group.getId());
        }
    }

    private void createBuyDownFeeBalance(final LoanTransaction buyDownFeeTransaction) {
        LoanBuyDownFeeBalance buyDownFeeBalance = new LoanBuyDownFeeBalance();
        buyDownFeeBalance.setLoan(buyDownFeeTransaction.getLoan());
        buyDownFeeBalance.setLoanTransaction(buyDownFeeTransaction);
        buyDownFeeBalance.setDate(buyDownFeeTransaction.getTransactionDate());
        buyDownFeeBalance.setAmount(buyDownFeeTransaction.getAmount());
        buyDownFeeBalance.setUnrecognizedAmount(buyDownFeeTransaction.getAmount());
        loanBuyDownFeeBalanceRepository.saveAndFlush(buyDownFeeBalance);
    }
}
