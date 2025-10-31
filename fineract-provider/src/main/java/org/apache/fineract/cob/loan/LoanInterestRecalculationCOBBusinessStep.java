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
package org.apache.fineract.cob.loan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.infrastructure.core.domain.ActionContext;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.service.LoanWritePlatformService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanInterestRecalculationCOBBusinessStep implements LoanCOBBusinessStep {

    private final LoanWritePlatformService loanWritePlatformService;

    @Override
    public Loan execute(Loan loan) {
        try {
            ThreadLocalContextUtil.setActionContext(ActionContext.DEFAULT);
            if (!loan.getStatus().isActive() || loan.isNpa() || loan.isChargedOff()
                    || !loan.isInterestBearingAndInterestRecalculationEnabled()
                    || loan.getLoanInterestRecalculationDetails().disallowInterestCalculationOnPastDue() || !hasOverdueInstallment(loan)) {
                log.debug(
                        "Skip processing loan interest recalculation [{}] - Possible reasons: Loan is not an interest bearing loan, Loan is not active, Interest recalculation on past due is disabled on this loan",
                        loan.getId());
                return loan;
            }

            log.debug("Start processing loan interest recalculation [{}]", loan.getId());
            loan = loanWritePlatformService.recalculateInterest(loan);
            log.debug("End processing loan interest recalculation [{}]", loan.getId());
            return loan;
        } finally {
            ThreadLocalContextUtil.setActionContext(ActionContext.COB);
        }
    }

    private boolean hasOverdueInstallment(Loan loan) {
        return loan.getRepaymentScheduleInstallments().stream()
                .anyMatch(installment -> DateUtils.isBeforeBusinessDate(installment.getDueDate()) && !installment.isObligationsMet());
    }

    @Override
    public String getEnumStyledName() {
        return "LOAN_INTEREST_RECALCULATION";
    }

    @Override
    public String getHumanReadableName() {
        return "Loan Interest Recalculation";
    }

}
