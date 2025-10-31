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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.loanproduct.domain.InterestRecalculationCompoundingMethod;
import org.apache.fineract.portfolio.loanproduct.domain.LoanPreCloseInterestCalculationStrategy;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductInterestRecalculationDetails;
import org.apache.fineract.portfolio.loanproduct.domain.LoanRescheduleStrategyMethod;
import org.apache.fineract.portfolio.loanproduct.domain.RecalculationFrequencyType;

/**
 * Entity for holding interest recalculation setting, details will be copied from product directly
 *
 * @author conflux
 */

@Entity
@Table(name = "m_loan_recalculation_details")
public class LoanInterestRecalculationDetails extends AbstractPersistableCustom<Long> {

    @OneToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    /**
     * {@link InterestRecalculationCompoundingMethod}
     */
    @Column(name = "compound_type_enum", nullable = false)
    private Integer interestRecalculationCompoundingMethod;

    /**
     * {@link LoanRescheduleStrategyMethod}
     */
    @Column(name = "reschedule_strategy_enum", nullable = false)
    private Integer rescheduleStrategyMethod;

    @Column(name = "rest_frequency_type_enum", nullable = false)
    private Integer restFrequencyType;

    @Getter
    @Column(name = "rest_frequency_interval", nullable = false)
    private Integer restInterval;

    @Getter
    @Column(name = "rest_frequency_nth_day_enum")
    private Integer restFrequencyNthDay;

    @Getter
    @Column(name = "rest_frequency_weekday_enum")
    private Integer restFrequencyWeekday;

    @Getter
    @Column(name = "rest_frequency_on_day")
    private Integer restFrequencyOnDay;

    @Column(name = "compounding_frequency_type_enum")
    private Integer compoundingFrequencyType;

    @Getter
    @Column(name = "compounding_frequency_interval")
    private Integer compoundingInterval;

    @Getter
    @Column(name = "compounding_frequency_nth_day_enum")
    private Integer compoundingFrequencyNthDay;
    @Getter
    @Column(name = "compounding_frequency_weekday_enum")
    private Integer compoundingFrequencyWeekday;
    @Getter
    @Column(name = "compounding_frequency_on_day")
    private Integer compoundingFrequencyOnDay;

    @Column(name = "is_compounding_to_be_posted_as_transaction")
    private Boolean isCompoundingToBePostedAsTransaction;
    @Column(name = "allow_compounding_on_eod")
    private Boolean allowCompoundingOnEod;

    @Getter
    @Column(name = "pre_close_interest_calculation_strategy")
    @Enumerated(EnumType.ORDINAL)
    private LoanPreCloseInterestCalculationStrategy preCloseInterestCalculationStrategy;

    @Column(name = "disallow_interest_calc_on_past_due")
    private Boolean disallowInterestCalculationOnPastDue;

    protected LoanInterestRecalculationDetails() {
        // Default constructor for jpa repository
    }

    private LoanInterestRecalculationDetails(final Integer interestRecalculationCompoundingMethod, final Integer rescheduleStrategyMethod,
            final Integer restFrequencyType, final Integer restInterval, final Integer restFrequencyNthDay, Integer restFrequencyWeekday,
            Integer restFrequencyOnDay, Integer compoundingFrequencyType, Integer compoundingInterval, Integer compoundingFrequencyNthDay,
            Integer compoundingFrequencyWeekday, Integer compoundingFrequencyOnDay, final boolean isCompoundingToBePostedAsTransaction,
            final boolean allowCompoundingOnEod, final boolean disallowInterestCalculationOnPastDue,
            final LoanPreCloseInterestCalculationStrategy preCloseInterestCalculationStrategy) {
        this.interestRecalculationCompoundingMethod = interestRecalculationCompoundingMethod;
        this.rescheduleStrategyMethod = rescheduleStrategyMethod;
        this.restFrequencyNthDay = restFrequencyNthDay;
        this.restFrequencyWeekday = restFrequencyWeekday;
        this.restFrequencyOnDay = restFrequencyOnDay;
        this.restFrequencyType = restFrequencyType;
        this.restInterval = restInterval;
        this.compoundingFrequencyNthDay = compoundingFrequencyNthDay;
        this.compoundingFrequencyWeekday = compoundingFrequencyWeekday;
        this.compoundingFrequencyOnDay = compoundingFrequencyOnDay;
        this.compoundingFrequencyType = compoundingFrequencyType;
        this.compoundingInterval = compoundingInterval;
        this.isCompoundingToBePostedAsTransaction = isCompoundingToBePostedAsTransaction;
        this.allowCompoundingOnEod = allowCompoundingOnEod;
        this.disallowInterestCalculationOnPastDue = disallowInterestCalculationOnPastDue;
        this.preCloseInterestCalculationStrategy = preCloseInterestCalculationStrategy;
    }

    public static LoanInterestRecalculationDetails createFrom(
            final LoanProductInterestRecalculationDetails loanProductInterestRecalculationDetails) {
        return new LoanInterestRecalculationDetails(loanProductInterestRecalculationDetails.getInterestRecalculationCompoundingMethod(),
                loanProductInterestRecalculationDetails.getRescheduleStrategyMethod(),
                loanProductInterestRecalculationDetails.getRestFrequencyType().getValue(),
                loanProductInterestRecalculationDetails.getRestInterval(), loanProductInterestRecalculationDetails.getRestFrequencyNthDay(),
                loanProductInterestRecalculationDetails.getRestFrequencyWeekday(),
                loanProductInterestRecalculationDetails.getRestFrequencyOnDay(),
                loanProductInterestRecalculationDetails.getCompoundingFrequencyType().getValue(),
                loanProductInterestRecalculationDetails.getCompoundingInterval(),
                loanProductInterestRecalculationDetails.getCompoundingFrequencyNthDay(),
                loanProductInterestRecalculationDetails.getCompoundingFrequencyWeekday(),
                loanProductInterestRecalculationDetails.getCompoundingFrequencyOnDay(),
                loanProductInterestRecalculationDetails.getIsCompoundingToBePostedAsTransaction(),
                loanProductInterestRecalculationDetails.getAllowCompoundingOnEod(),
                loanProductInterestRecalculationDetails.getDisallowInterestCalculationOnPastDue(),
                loanProductInterestRecalculationDetails.getPreCloseInterestCalculationStrategy());
    }

    public void updateLoan(final Loan loan) {
        this.loan = loan;
    }

    public InterestRecalculationCompoundingMethod getInterestRecalculationCompoundingMethod() {
        return InterestRecalculationCompoundingMethod.fromInt(this.interestRecalculationCompoundingMethod);
    }

    public LoanRescheduleStrategyMethod getRescheduleStrategyMethod() {
        return LoanRescheduleStrategyMethod.fromInt(this.rescheduleStrategyMethod);
    }

    public RecalculationFrequencyType getRestFrequencyType() {
        return RecalculationFrequencyType.fromInt(this.restFrequencyType);
    }

    public RecalculationFrequencyType getCompoundingFrequencyType() {
        return RecalculationFrequencyType.fromInt(this.compoundingFrequencyType);
    }

    public boolean isCompoundingToBePostedAsTransaction() {
        return this.isCompoundingToBePostedAsTransaction != null && this.isCompoundingToBePostedAsTransaction;
    }

    public boolean allowCompoundingOnEod() {
        return this.allowCompoundingOnEod;
    }

    public Boolean disallowInterestCalculationOnPastDue() {
        return disallowInterestCalculationOnPastDue;
    }

}
