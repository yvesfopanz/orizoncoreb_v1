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
package org.apache.fineract.portfolio.loanproduct.calc.data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.fineract.infrastructure.core.serialization.gson.JsonExclude;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.MathUtil;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanproduct.domain.InterestMethod;

@Getter
@ToString(exclude = { "repaymentPeriod" })
@EqualsAndHashCode(exclude = { "repaymentPeriod" })
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class InterestPeriod implements Comparable<InterestPeriod> {

    @JsonExclude
    private final RepaymentPeriod repaymentPeriod;
    @Setter
    @NotNull
    private LocalDate fromDate;
    @Setter
    @NotNull
    private LocalDate dueDate;

    @Setter
    private BigDecimal rateFactor;
    @Setter
    private BigDecimal rateFactorTillPeriodDueDate;

    /** Stores credited principals. Related transactions: Chargeback or Credit Balance Refound */
    private Money creditedPrincipal;
    /** Stores credited interest. Related transaction: Chargeback */
    private Money creditedInterest;
    private Money disbursementAmount;
    private Money balanceCorrectionAmount;
    private Money outstandingLoanBalance;
    private Money capitalizedIncomePrincipal;
    @JsonExclude
    @Getter
    private final MathContext mc;

    private final boolean isPaused;

    public static InterestPeriod copy(@NotNull RepaymentPeriod repaymentPeriod, @NotNull InterestPeriod interestPeriod, MathContext mc) {
        return new InterestPeriod(repaymentPeriod, interestPeriod.getFromDate(), interestPeriod.getDueDate(),
                interestPeriod.getRateFactor(), interestPeriod.getRateFactorTillPeriodDueDate(), interestPeriod.getCreditedPrincipal(),
                interestPeriod.getCreditedInterest(), interestPeriod.getDisbursementAmount(), interestPeriod.getBalanceCorrectionAmount(),
                interestPeriod.getOutstandingLoanBalance(), interestPeriod.getCapitalizedIncomePrincipal(), mc, interestPeriod.isPaused());
    }

    public static InterestPeriod empty(@NotNull RepaymentPeriod repaymentPeriod, MathContext mc) {
        return new InterestPeriod(repaymentPeriod, null, null, null, null, null, null, null, null, null, null, mc, false);
    }

    public static InterestPeriod copy(@NotNull RepaymentPeriod repaymentPeriod, @NotNull InterestPeriod interestPeriod) {
        return new InterestPeriod(repaymentPeriod, interestPeriod.getFromDate(), interestPeriod.getDueDate(),
                interestPeriod.getRateFactor(), interestPeriod.getRateFactorTillPeriodDueDate(), interestPeriod.getCreditedPrincipal(),
                interestPeriod.getCreditedInterest(), interestPeriod.getDisbursementAmount(), interestPeriod.getBalanceCorrectionAmount(),
                interestPeriod.getOutstandingLoanBalance(), interestPeriod.getCapitalizedIncomePrincipal(), interestPeriod.getMc(),
                interestPeriod.isPaused());
    }

    public static InterestPeriod withEmptyAmounts(@NotNull RepaymentPeriod repaymentPeriod, @NotNull LocalDate fromDate,
            LocalDate dueDate) {
        final Money zero = repaymentPeriod.getZero();
        return new InterestPeriod(repaymentPeriod, fromDate, dueDate, BigDecimal.ZERO, BigDecimal.ZERO, zero, zero, zero, zero, zero, zero,
                zero.getMc(), false);
    }

    public static InterestPeriod withEmptyAmounts(@NotNull RepaymentPeriod repaymentPeriod, @NotNull LocalDate fromDate, LocalDate dueDate,
            boolean isPaused) {
        final Money zero = repaymentPeriod.getZero();
        return new InterestPeriod(repaymentPeriod, fromDate, dueDate, BigDecimal.ZERO, BigDecimal.ZERO, zero, zero, zero, zero, zero, zero,
                zero.getMc(), isPaused);
    }

    public static InterestPeriod withPausedAndEmptyAmounts(@NotNull RepaymentPeriod repaymentPeriod, @NotNull LocalDate fromDate,
            LocalDate dueDate) {
        final Money zero = repaymentPeriod.getZero();
        return new InterestPeriod(repaymentPeriod, fromDate, dueDate, BigDecimal.ZERO, BigDecimal.ZERO, zero, zero, zero, zero, zero, zero,
                zero.getMc(), true);
    }

    @Override
    public int compareTo(@NotNull InterestPeriod o) {
        return getDueDate().compareTo(o.getDueDate());
    }

    public void addBalanceCorrectionAmount(final Money additionalBalanceCorrectionAmount) {
        this.balanceCorrectionAmount = MathUtil.plus(this.getBalanceCorrectionAmount(), additionalBalanceCorrectionAmount);
    }

    public void addDisbursementAmount(final Money additionalDisbursementAmount) {
        this.disbursementAmount = MathUtil.plus(this.getDisbursementAmount(), additionalDisbursementAmount, getMc());
    }

    public void addCreditedPrincipalAmount(final Money additionalCreditedPrincipal) {
        this.creditedPrincipal = MathUtil.plus(this.getCreditedPrincipal(), additionalCreditedPrincipal, getMc());
    }

    public void addCreditedInterestAmount(final Money additionalCreditedInterest) {
        this.creditedInterest = MathUtil.plus(this.getCreditedInterest(), additionalCreditedInterest, getMc());
    }

    public void addCapitalizedIncomePrincipalAmount(final Money additionalCapitalizedIncomePrincipal) {
        this.capitalizedIncomePrincipal = MathUtil.plus(this.getCapitalizedIncomePrincipal(), additionalCapitalizedIncomePrincipal,
                getMc());
    }

    public BigDecimal getCalculatedDueInterest() {
        if (isPaused()) {
            return getCreditedInterest().getAmount();
        }

        long lengthTillPeriodDueDate = getLengthTillPeriodDueDate();
        final BigDecimal interestDueTillRepaymentDueDate = getCalculatedDueInterest(
                getRepaymentPeriod().getLoanProductRelatedDetail().getInterestMethod(), lengthTillPeriodDueDate); //
        return MathUtil.negativeToZero(MathUtil.add(getMc(), getCreditedInterest().getAmount(), interestDueTillRepaymentDueDate));
    }

    public BigDecimal getCalculatedDueInterest(InterestMethod method, long lengthTillPeriodDueDate) {
        if (lengthTillPeriodDueDate == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal baseAmount = switch (method) {
            case FLAT -> getRepaymentPeriod().calculateTotalDisbursedAndCapitalizedIncomeAmountTillGivenPeriod(this).getAmount();
            case DECLINING_BALANCE -> getOutstandingLoanBalance().getAmount();
            default -> throw new UnsupportedOperationException("Method not implemented: " + method);
        };
        return baseAmount //
                .multiply(getRateFactorTillPeriodDueDate(), getMc()) //
                .divide(BigDecimal.valueOf(lengthTillPeriodDueDate), getMc()) //
                .multiply(BigDecimal.valueOf(getLength()), getMc());
    }

    public long getLength() {
        return DateUtils.getDifferenceInDays(getFromDate(), getDueDate());
    }

    public long getLengthTillPeriodDueDate() {
        return DateUtils.getDifferenceInDays(getFromDate(), getRepaymentPeriod().getDueDate());
    }

    public void updateOutstandingLoanBalance() {
        if (isFirstInterestPeriod()) {
            Optional<RepaymentPeriod> previousRepaymentPeriod = getRepaymentPeriod().getPrevious();
            if (previousRepaymentPeriod.isPresent()) {
                InterestPeriod previousInterestPeriod = previousRepaymentPeriod.get().getLastInterestPeriod();
                this.outstandingLoanBalance = MathUtil.negativeToZero(previousInterestPeriod.getOutstandingLoanBalance()//
                        .plus(previousInterestPeriod.getDisbursementAmount(), getMc())//
                        .plus(previousInterestPeriod.getCapitalizedIncomePrincipal(), getMc())//
                        .plus(previousInterestPeriod.getBalanceCorrectionAmount(), getMc())//
                        .minus(previousRepaymentPeriod.get().getDuePrincipal(), getMc())//
                        .plus(previousRepaymentPeriod.get().getPaidPrincipal(), getMc()), getMc());//
            }
        } else {
            int index = getRepaymentPeriod().getInterestPeriods().indexOf(this);
            InterestPeriod previousInterestPeriod = getRepaymentPeriod().getInterestPeriods().get(index - 1);
            this.outstandingLoanBalance = MathUtil.negativeToZero(previousInterestPeriod.getOutstandingLoanBalance() //
                    .plus(previousInterestPeriod.getBalanceCorrectionAmount(), getMc()) //
                    .plus(previousInterestPeriod.getCapitalizedIncomePrincipal(), getMc()) //
                    .plus(previousInterestPeriod.getDisbursementAmount(), getMc())); //
        }
    }

    /**
     * Include principal like amounts (all disbursement amount + credited principal)
     */
    public Money getCreditedAmounts() {
        return MathUtil.plus(mc, getDisbursementAmount(), getCreditedPrincipal(), getCapitalizedIncomePrincipal());
    }

    public boolean isFirstInterestPeriod() {
        return this.equals(getRepaymentPeriod().getFirstInterestPeriod());
    }

    private MonetaryCurrency getCurrency() {
        return getRepaymentPeriod().getCurrency();
    }

    public Money getCreditedPrincipal() {
        return MathUtil.nullToZero(creditedPrincipal, getCurrency(), getMc());
    }

    public Money getCreditedInterest() {
        return MathUtil.nullToZero(creditedInterest, getCurrency(), getMc());
    }

    public Money getDisbursementAmount() {
        return MathUtil.nullToZero(disbursementAmount, getCurrency(), getMc());
    }

    public Money getBalanceCorrectionAmount() {
        return MathUtil.nullToZero(balanceCorrectionAmount, getCurrency(), getMc());
    }

    public Money getOutstandingLoanBalance() {
        return MathUtil.nullToZero(outstandingLoanBalance, getCurrency(), getMc());
    }

    public Money getCapitalizedIncomePrincipal() {
        return MathUtil.nullToZero(capitalizedIncomePrincipal, getCurrency(), getMc());
    }

    public BigDecimal getRateFactor() {
        return MathUtil.nullToZero(rateFactor);
    }

    public BigDecimal getRateFactorTillPeriodDueDate() {
        return MathUtil.nullToZero(rateFactorTillPeriodDueDate);
    }

}
