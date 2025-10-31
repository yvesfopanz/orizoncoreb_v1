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
package org.apache.fineract.portfolio.loanaccount.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.service.MathUtil;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;

@Entity
@Table(name = "m_loan_transaction_repayment_schedule_mapping")
@Getter
public class LoanTransactionToRepaymentScheduleMapping extends AbstractPersistableCustom<Long> {

    @Setter
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "loan_transaction_id", nullable = false)
    private LoanTransaction loanTransaction;

    @Setter
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "loan_repayment_schedule_id", nullable = false)
    private LoanRepaymentScheduleInstallment installment;

    @Column(name = "principal_portion_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal principalPortion;

    @Column(name = "interest_portion_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal interestPortion;

    @Column(name = "fee_charges_portion_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal feeChargesPortion;

    @Column(name = "penalty_charges_portion_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal penaltyChargesPortion;

    @Column(name = "amount", scale = 6, precision = 19)
    private BigDecimal amount;

    protected LoanTransactionToRepaymentScheduleMapping() {

    }

    private LoanTransactionToRepaymentScheduleMapping(final LoanTransaction loanTransaction,
            final LoanRepaymentScheduleInstallment installment, final BigDecimal principalPortion, final BigDecimal interestPortion,
            final BigDecimal feeChargesPortion, final BigDecimal penaltyChargesPortion, final BigDecimal amount) {
        this.loanTransaction = loanTransaction;
        this.installment = installment;
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
        this.feeChargesPortion = feeChargesPortion;
        this.penaltyChargesPortion = penaltyChargesPortion;
        this.amount = amount;
    }

    public static LoanTransactionToRepaymentScheduleMapping createFrom(final LoanTransaction loanTransaction,
            final LoanRepaymentScheduleInstallment installment, final Money principalPortion, final Money interestPortion,
            final Money feeChargesPortion, final Money penaltyChargesPortion) {
        return new LoanTransactionToRepaymentScheduleMapping(loanTransaction, installment, defaultToNullIfZero(principalPortion),
                defaultToNullIfZero(interestPortion), defaultToNullIfZero(feeChargesPortion), defaultToNullIfZero(penaltyChargesPortion),
                defaultToNullIfZero(MathUtil.plus(principalPortion, interestPortion, feeChargesPortion, penaltyChargesPortion)));
    }

    private static BigDecimal defaultToNullIfZero(final Money value) {
        return (value == null || value.isZero()) ? null : value.getAmount();
    }

    public LoanRepaymentScheduleInstallment getLoanRepaymentScheduleInstallment() {
        return this.installment;
    }

    public void updateComponents(Money principal, Money interest, Money feeCharges, Money penaltyCharges) {
        updateComponents(MathUtil.toBigDecimal(principal), MathUtil.toBigDecimal(interest), MathUtil.toBigDecimal(feeCharges),
                MathUtil.toBigDecimal(penaltyCharges));
    }

    void updateComponents(final BigDecimal principal, final BigDecimal interest, final BigDecimal feeCharges,
            final BigDecimal penaltyCharges) {
        this.principalPortion = MathUtil.zeroToNull(MathUtil.add(getPrincipalPortion(), principal));
        this.interestPortion = MathUtil.zeroToNull(MathUtil.add(getInterestPortion(), interest));
        updateChargesComponents(feeCharges, penaltyCharges);
        updateAmount();
    }

    private void updateAmount() {
        this.amount = MathUtil.add(getPrincipalPortion(), getInterestPortion(), getFeeChargesPortion(), getPenaltyChargesPortion());
    }

    public void setComponents(final BigDecimal principal, final BigDecimal interest, final BigDecimal feeCharges,
            final BigDecimal penaltyCharges) {
        this.principalPortion = principal;
        this.interestPortion = interest;
        this.feeChargesPortion = feeCharges;
        this.penaltyChargesPortion = penaltyCharges;
        updateAmount();
    }

    private void updateChargesComponents(final BigDecimal feeCharges, final BigDecimal penaltyCharges) {
        this.feeChargesPortion = MathUtil.zeroToNull(MathUtil.add(getFeeChargesPortion(), feeCharges));
        this.penaltyChargesPortion = MathUtil.zeroToNull(MathUtil.add(getPenaltyChargesPortion(), penaltyCharges));
    }

    public Money getPrincipalPortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.principalPortion);
    }

    public Money getInterestPortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.interestPortion);
    }

    public Money getFeeChargesPortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.feeChargesPortion);
    }

    public Money getPenaltyChargesPortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.penaltyChargesPortion);
    }

}
