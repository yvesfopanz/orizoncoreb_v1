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
package org.apache.fineract.portfolio.charge.service;

import java.util.List;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;

public interface ChargeReadPlatformService {

    List<ChargeData> retrieveAllCharges();

    List<ChargeData> retrieveAllChargesForCurrency(String currencyCode);

    ChargeData retrieveCharge(Long chargeId);

    ChargeData retrieveNewChargeDetails();

    /**
     * Returns all charges that can be applied to Cients
     *
     * @return
     */
    List<ChargeData> retrieveAllChargesApplicableToClients();

    /**
     * Returns all Fees (excluding penalties) applicable for loans
     *
     * @return
     */
    List<ChargeData> retrieveLoanApplicableFees();

    /**
     * Returns all charges applicable for a given loan account
     *
     * @param excludeChargeTimes
     *            Excludes Given List of Charge Types from the response
     * @return
     */
    List<ChargeData> retrieveLoanAccountApplicableCharges(Long loanId, ChargeTimeType[] excludeChargeTimes);

    /**
     * Returns all charges applicable for a given loan product (filter based on Currency of Selected Loan Product)
     *
     * @param excludeChargeTimes
     *            Excludes Given List of Charge Types from the response
     * @return
     */
    List<ChargeData> retrieveLoanProductApplicableCharges(Long loanProductId, ChargeTimeType[] excludeChargeTimes);

    /**
     * Returns all Penalties applicable for loans
     *
     * @return
     */
    List<ChargeData> retrieveLoanApplicablePenalties();

    /**
     * Returns all Charges associated with a given Loan Product
     *
     * @param loanProductId
     * @return
     */
    List<ChargeData> retrieveLoanProductCharges(Long loanProductId);

    /**
     * Returns all charges applicable for a given loan product
     *
     * @param loanProductId
     * @param chargeTime
     *            Filters based on the type of the charge to be returned
     * @return
     */
    List<ChargeData> retrieveLoanProductCharges(Long loanProductId, ChargeTimeType chargeTime);

    /**
     * Returns all charges applicable for savings
     *
     * @param feeChargesOnly
     * @return
     */
    List<ChargeData> retrieveSavingsProductApplicableCharges(boolean feeChargesOnly);

    /**
     * Returns all penalties applicable for savings
     *
     * @return
     */
    List<ChargeData> retrieveSavingsApplicablePenalties();

    /**
     * Returns all charges applicable for a given savings product
     *
     * @param savingsProductId
     * @return
     */
    List<ChargeData> retrieveSavingsProductCharges(Long savingsProductId);

    /** Retrieve savings account charges **/
    List<ChargeData> retrieveSavingsAccountApplicableCharges(Long savingsId);

    /**
     * Returns charge definitions which are applicable for shares
     */
    List<ChargeData> retrieveSharesApplicableCharges();

    List<ChargeData> retrieveShareProductCharges(Long shareProductId);
}
