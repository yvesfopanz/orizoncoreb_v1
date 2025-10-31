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

import static java.lang.Boolean.TRUE;
import static org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations.interestType;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.ApiFacingEnum;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.data.StringEnumOptionData;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.domain.ExternalId;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.ExternalIdFactory;
import org.apache.fineract.infrastructure.core.service.MathUtil;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.core.service.database.DatabaseSpecificSQLGenerator;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.security.utils.ColumnValidator;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.organisation.staff.service.StaffReadPlatformService;
import org.apache.fineract.portfolio.account.data.AccountTransferData;
import org.apache.fineract.portfolio.accountdetails.data.LoanAccountSummaryData;
import org.apache.fineract.portfolio.accountdetails.domain.AccountType;
import org.apache.fineract.portfolio.accountdetails.service.AccountDetailsReadPlatformService;
import org.apache.fineract.portfolio.accountdetails.service.AccountEnumerations;
import org.apache.fineract.portfolio.calendar.data.CalendarData;
import org.apache.fineract.portfolio.calendar.domain.CalendarEntityType;
import org.apache.fineract.portfolio.calendar.service.CalendarReadPlatformService;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.service.ChargeReadPlatformService;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.ClientEnumerations;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.common.domain.DaysInYearCustomStrategyType;
import org.apache.fineract.portfolio.common.service.CommonEnumerations;
import org.apache.fineract.portfolio.delinquency.data.DelinquencyRangeData;
import org.apache.fineract.portfolio.delinquency.service.DelinquencyReadPlatformService;
import org.apache.fineract.portfolio.floatingrates.data.InterestRatePeriodData;
import org.apache.fineract.portfolio.floatingrates.service.FloatingRatesReadPlatformService;
import org.apache.fineract.portfolio.fund.data.FundData;
import org.apache.fineract.portfolio.fund.service.FundReadPlatformService;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;
import org.apache.fineract.portfolio.group.data.GroupRoleData;
import org.apache.fineract.portfolio.group.service.GroupReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.data.DisbursementData;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.data.LoanApplicationTimelineData;
import org.apache.fineract.portfolio.loanaccount.data.LoanApprovalData;
import org.apache.fineract.portfolio.loanaccount.data.LoanChargePaidByData;
import org.apache.fineract.portfolio.loanaccount.data.LoanInterestRecalculationData;
import org.apache.fineract.portfolio.loanaccount.data.LoanRepaymentScheduleInstallmentData;
import org.apache.fineract.portfolio.loanaccount.data.LoanSchedulePeriodDataWrapper;
import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;
import org.apache.fineract.portfolio.loanaccount.data.LoanSummaryData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionRelationData;
import org.apache.fineract.portfolio.loanaccount.data.OutstandingAmountsDTO;
import org.apache.fineract.portfolio.loanaccount.data.PaidInAdvanceData;
import org.apache.fineract.portfolio.loanaccount.data.RepaymentScheduleRelatedLoanData;
import org.apache.fineract.portfolio.loanaccount.data.ScheduleGeneratorDTO;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanBuyDownFeeBalance;
import org.apache.fineract.portfolio.loanaccount.domain.LoanBuyDownFeeCalculationType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanBuyDownFeeIncomeType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanBuyDownFeeStrategy;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCapitalizedIncomeBalance;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCapitalizedIncomeCalculationType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCapitalizedIncomeStrategy;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCapitalizedIncomeType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanChargeOffBehaviour;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.apache.fineract.portfolio.loanaccount.domain.LoanSubStatus;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionRepaymentPeriodData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.apache.fineract.portfolio.loanaccount.exception.LoanNotFoundException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanTransactionNotFoundException;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanSchedulePeriodData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.OverdueLoanScheduleData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleProcessingType;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleType;
import org.apache.fineract.portfolio.loanaccount.mapper.LoanTransactionMapper;
import org.apache.fineract.portfolio.loanaccount.repository.LoanBuyDownFeeBalanceRepository;
import org.apache.fineract.portfolio.loanaccount.repository.LoanCapitalizedIncomeBalanceRepository;
import org.apache.fineract.portfolio.loanaccount.serialization.LoanForeclosureValidator;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.data.TransactionProcessingStrategyData;
import org.apache.fineract.portfolio.loanproduct.domain.InterestMethod;
import org.apache.fineract.portfolio.loanproduct.service.LoanDropdownReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanReadPlatformServiceImpl implements LoanReadPlatformService, LoanReadPlatformServiceCommon {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final LoanRepositoryWrapper loanRepositoryWrapper;
    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final ClientReadPlatformService clientReadPlatformService;
    private final GroupReadPlatformService groupReadPlatformService;
    private final LoanDropdownReadPlatformService loanDropdownReadPlatformService;
    private final FundReadPlatformService fundReadPlatformService;
    private final ChargeReadPlatformService chargeReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final CalendarReadPlatformService calendarReadPlatformService;
    private final StaffReadPlatformService staffReadPlatformService;
    private final PaginationHelper paginationHelper;
    private final PaymentTypeReadPlatformService paymentTypeReadPlatformService;
    private final FloatingRatesReadPlatformService floatingRatesReadPlatformService;
    private final LoanUtilService loanUtilService;
    private final ConfigurationDomainService configurationDomainService;
    private final AccountDetailsReadPlatformService accountDetailsReadPlatformService;
    private final ColumnValidator columnValidator;
    private final DatabaseSpecificSQLGenerator sqlGenerator;
    private final DelinquencyReadPlatformService delinquencyReadPlatformService;
    private final LoanTransactionRepository loanTransactionRepository;
    private final LoanChargePaidByReadService loanChargePaidByReadService;
    private final LoanTransactionRelationReadService loanTransactionRelationReadService;
    private final LoanForeclosureValidator loanForeclosureValidator;
    private final LoanTransactionMapper loanTransactionMapper;
    private final LoanTransactionProcessingService loadTransactionProcessingService;
    private final LoanBalanceService loanBalanceService;
    private final LoanCapitalizedIncomeBalanceRepository loanCapitalizedIncomeBalanceRepository;
    private final LoanBuyDownFeeBalanceRepository loanBuyDownFeeBalanceRepository;
    private final InterestRefundServiceDelegate interestRefundServiceDelegate;
    private final LoanMaximumAmountCalculator loanMaximumAmountCalculator;

    @Override
    public LoanAccountData retrieveOne(final Long loanId) {

        try {
            final String hierarchy = getHierarchyString();
            final String hierarchySearchString = hierarchy + "%";

            final LoanMapper rm = new LoanMapper(sqlGenerator, delinquencyReadPlatformService);

            final StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("select ");
            sqlBuilder.append(rm.loanSchema());
            sqlBuilder.append(" join m_office o on (o.id = c.office_id or o.id = g.office_id) ");
            sqlBuilder.append(" left join m_office transferToOffice on transferToOffice.id = c.transfer_to_office_id ");
            sqlBuilder.append(" where l.id=? and ( o.hierarchy like ? or transferToOffice.hierarchy like ?)");

            return this.jdbcTemplate.queryForObject(sqlBuilder.toString(), rm, loanId, hierarchySearchString, hierarchySearchString);
        } catch (final EmptyResultDataAccessException e) {
            throw new LoanNotFoundException(loanId, e);
        }
    }

    private String getHierarchyString() {
        AppUser currentUser = null;
        if (this.context != null) {
            currentUser = this.context.getAuthenticatedUserIfPresent();
        }
        return Optional.ofNullable(currentUser).map(appUser -> appUser.getOffice().getHierarchy()).orElse(".");
    }

    @Override
    public LoanAccountData retrieveLoanByLoanAccount(String loanAccountNumber) {

        // final AppUser currentUser = this.context.authenticatedUser();
        this.context.authenticatedUser();
        final LoanMapper rm = new LoanMapper(sqlGenerator, delinquencyReadPlatformService);

        final String sql = "select " + rm.loanSchema() + " where l.account_no=?";

        return this.jdbcTemplate.queryForObject(sql, rm, loanAccountNumber); // NOSONAR

    }

    @Override
    public List<LoanAccountData> retrieveGLIMChildLoansByGLIMParentAccount(String parentloanAccountNumber) {
        this.context.authenticatedUser();
        final LoanMapper rm = new LoanMapper(sqlGenerator, delinquencyReadPlatformService);

        final String sql = "select " + rm.loanSchema()
                + " left join glim_parent_child_mapping as glim on glim.glim_child_account_id=l.account_no "
                + "where glim.glim_parent_account_id=?";

        return this.jdbcTemplate.query(sql, rm, parentloanAccountNumber); // NOSONAR

    }

    @Override
    public LoanAccountData fetchRepaymentScheduleData(LoanAccountData accountData) {
        final RepaymentScheduleRelatedLoanData repaymentScheduleRelatedData = new RepaymentScheduleRelatedLoanData(
                accountData.getTimeline().getExpectedDisbursementDate(), accountData.getTimeline().getActualDisbursementDate(),
                accountData.getCurrency(), accountData.getPrincipal(), accountData.getInArrearsTolerance(),
                accountData.getFeeChargesAtDisbursementCharged());

        final Collection<DisbursementData> disbursementData = retrieveLoanDisbursementDetails(accountData.getId());
        List<LoanTransactionRepaymentPeriodData> capitalizedIncomeData = loanCapitalizedIncomeBalanceRepository
                .findRepaymentPeriodDataByLoanId(accountData.getId());
        final LoanScheduleData repaymentSchedule = retrieveRepaymentSchedule(accountData.getId(), repaymentScheduleRelatedData,
                disbursementData, capitalizedIncomeData, accountData.isInterestRecalculationEnabled(),
                LoanScheduleType.fromEnumOptionData(accountData.getLoanScheduleType()));
        accountData.setRepaymentSchedule(repaymentSchedule);
        return accountData;
    }

    @Override
    public LoanScheduleData retrieveRepaymentSchedule(final Long loanId,
            final RepaymentScheduleRelatedLoanData repaymentScheduleRelatedLoanData, Collection<DisbursementData> disbursementData,
            Collection<LoanTransactionRepaymentPeriodData> capitalizedIncomeData, boolean isInterestRecalculationEnabled,
            LoanScheduleType loanScheduleType) {

        try {
            this.context.authenticatedUser();

            final LoanScheduleResultSetExtractor fullResultsetExtractor = new LoanScheduleResultSetExtractor(
                    repaymentScheduleRelatedLoanData, disbursementData, capitalizedIncomeData, isInterestRecalculationEnabled,
                    loanScheduleType);
            final String sql = "select " + fullResultsetExtractor.schema() + " where ls.loan_id = ? order by ls.loan_id, ls.installment";

            return this.jdbcTemplate.query(sql, fullResultsetExtractor, loanId); // NOSONAR
        } catch (final EmptyResultDataAccessException e) {
            throw new LoanNotFoundException(loanId, e);
        }
    }

    @Override
    public Collection<LoanTransactionData> retrieveLoanTransactions(final Long loanId) {
        try {
            this.context.authenticatedUser();

            final LoanTransactionsMapper rm = new LoanTransactionsMapper(sqlGenerator);

            // retrieve all loan transactions that are not invalid and have not
            // been 'contra'ed by another transaction
            // repayments at time of disbursement (e.g. charges)

            /***
             * TODO Vishwas: Remove references to "Contra" from the codebase
             ***/
            final String sql = "select " + rm.loanPaymentsSchema() + " where tr.loan_id = ? and tr.transaction_type_enum not in (0, 3) "
                    + " and (tr.is_reversed=false or tr.manually_adjusted_or_reversed = true)  order by tr.transaction_date, tr.created_on_utc, tr.id ";
            Collection<LoanTransactionData> loanTransactionData = this.jdbcTemplate.query(sql, rm, loanId); // NOSONAR
            // TODO: would worth to rework in the future. It is not nice to fetch relations one by one... might worth to
            // give a try to get rid of native queries
            final List<Long> loanIds = loanTransactionData.stream().map(LoanTransactionData::getId).collect(Collectors.toList());
            final List<LoanTransactionRelationData> loanTransactionRelationDatas = loanTransactionRelationReadService
                    .fetchLoanTransactionRelationDataFrom(loanIds);
            final List<LoanChargePaidByData> loanChargePaidByDatas = loanChargePaidByReadService
                    .fetchLoanChargesPaidByDataTransactionId(loanIds);
            for (LoanTransactionData loanTransaction : loanTransactionData) {
                loanTransaction.setLoanTransactionRelations(loanTransactionRelationDatas.stream().filter(
                        loanTransactionRelationData -> loanTransactionRelationData.getFromLoanTransaction().equals(loanTransaction.getId()))
                        .toList());
                loanTransaction.setLoanChargePaidByList(loanChargePaidByDatas.stream()
                        .filter(loanChargePaidByData -> loanChargePaidByData.getTransactionId().equals(loanTransaction.getId())).toList());
            }
            return loanTransactionData;
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Page<LoanAccountData> retrieveAll(final SearchParameters searchParameters) {

        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";
        final LoanMapper loanMapper = new LoanMapper(sqlGenerator, delinquencyReadPlatformService);

        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select " + sqlGenerator.calcFoundRows() + " ");
        sqlBuilder.append(loanMapper.loanSchema());

        // TODO - for time being this will data scope list of loans returned to
        // only loans that have a client associated.
        // to support scenario where loan has group_id only OR client_id will
        // probably require a UNION query
        // but that at present is an edge case
        sqlBuilder.append(" join m_office o on (o.id = c.office_id or o.id = g.office_id) ");
        sqlBuilder.append(" left join m_office transferToOffice on transferToOffice.id = c.transfer_to_office_id ");
        sqlBuilder.append(" where ( o.hierarchy like ? or transferToOffice.hierarchy like ?)");

        int arrayPos = 2;
        List<Object> extraCriterias = new ArrayList<>();
        extraCriterias.add(hierarchySearchString);
        extraCriterias.add(hierarchySearchString);

        if (searchParameters != null) {

            if (StringUtils.isNotBlank(searchParameters.getStatus())) {
                sqlBuilder.append(" and l.loan_status_id = ?");
                extraCriterias.add(Integer.parseInt(searchParameters.getStatus()));
                arrayPos = arrayPos + 1;
            }

            if (StringUtils.isNotBlank(searchParameters.getExternalId())) {
                sqlBuilder.append(" and l.external_id = ?");
                extraCriterias.add(searchParameters.getExternalId());
                arrayPos = arrayPos + 1;
            }
            if (searchParameters.getOfficeId() != null) {
                sqlBuilder.append("and c.office_id =?");
                extraCriterias.add(searchParameters.getOfficeId());
                arrayPos = arrayPos + 1;
            }

            if (StringUtils.isNotBlank(searchParameters.getAccountNo())) {
                sqlBuilder.append(" and l.account_no = ?");
                extraCriterias.add(searchParameters.getAccountNo());
                arrayPos = arrayPos + 1;
            }

            if (searchParameters.getClientId() != null) {
                sqlBuilder.append(" and l.client_id = ?");
                extraCriterias.add(searchParameters.getClientId());
                arrayPos = arrayPos + 1;
            }

            if (searchParameters.hasOrderBy()) {
                sqlBuilder.append(" order by ").append(searchParameters.getOrderBy());
                this.columnValidator.validateSqlInjection(sqlBuilder.toString(), searchParameters.getOrderBy());

                if (searchParameters.hasSortOrder()) {
                    sqlBuilder.append(' ').append(searchParameters.getSortOrder());
                    this.columnValidator.validateSqlInjection(sqlBuilder.toString(), searchParameters.getSortOrder());
                }
            }

            if (searchParameters.hasLimit()) {
                sqlBuilder.append(" ");
                if (searchParameters.hasOffset()) {
                    sqlBuilder.append(sqlGenerator.limit(searchParameters.getLimit(), searchParameters.getOffset()));
                } else {
                    sqlBuilder.append(sqlGenerator.limit(searchParameters.getLimit()));
                }
            }
        }
        final Object[] objectArray = extraCriterias.toArray();
        final Object[] finalObjectArray = Arrays.copyOf(objectArray, arrayPos);
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlBuilder.toString(), finalObjectArray, loanMapper);
    }

    @Override
    public LoanAccountData retrieveTemplateWithClientAndProductDetails(final Long clientId, final Long productId) {

        this.context.authenticatedUser();

        final ClientData clientAccount = this.clientReadPlatformService.retrieveOne(clientId);
        final LocalDate expectedDisbursementDate = DateUtils.getBusinessLocalDate();
        LoanAccountData loanDetails = new LoanAccountData().withClientData(clientAccount)
                .setExpectedDisbursementDate(expectedDisbursementDate);

        if (productId != null) {
            final LoanProductData selectedProduct = this.loanProductReadPlatformService.retrieveLoanProduct(productId);
            loanDetails = loanDetails.withProductData(selectedProduct, null);
        }

        return loanDetails;
    }

    @Override
    public LoanAccountData retrieveTemplateWithGroupAndProductDetails(final Long groupId, final Long productId) {

        this.context.authenticatedUser();

        final GroupGeneralData groupAccount = this.groupReadPlatformService.retrieveOne(groupId);
        final LocalDate expectedDisbursementDate = DateUtils.getBusinessLocalDate();
        LoanAccountData loanDetails = new LoanAccountData().setGroup(groupAccount).withExpectedDisbursementDate(expectedDisbursementDate);

        if (productId != null) {
            final LoanProductData selectedProduct = this.loanProductReadPlatformService.retrieveLoanProduct(productId);
            loanDetails = loanDetails.withProductData(selectedProduct, null);
        }

        return loanDetails;
    }

    @Override
    public LoanAccountData retrieveTemplateWithCompleteGroupAndProductDetails(final Long groupId, final Long productId) {

        this.context.authenticatedUser();

        GroupGeneralData groupAccount = this.groupReadPlatformService.retrieveOne(groupId);
        // get group associations
        final Collection<ClientData> membersOfGroup = this.clientReadPlatformService.retrieveClientMembersOfGroup(groupId);
        if (!CollectionUtils.isEmpty(membersOfGroup)) {
            final Collection<ClientData> activeClientMembers = null;
            final Collection<CalendarData> calendarsData = null;
            final CalendarData collectionMeetingCalendar = null;
            final Collection<GroupRoleData> groupRoles = null;
            groupAccount = GroupGeneralData.withAssocations(groupAccount, membersOfGroup, activeClientMembers, groupRoles, calendarsData,
                    collectionMeetingCalendar);
        }

        final LocalDate expectedDisbursementDate = DateUtils.getBusinessLocalDate();
        LoanAccountData loanDetails = new LoanAccountData().setGroup(groupAccount).withExpectedDisbursementDate(expectedDisbursementDate);

        if (productId != null) {
            final LoanProductData selectedProduct = this.loanProductReadPlatformService.retrieveLoanProduct(productId);
            loanDetails = loanDetails.withProductData(selectedProduct, null);
        }

        return loanDetails;
    }

    private CurrencyData retriveLoanCurrencyData(final Long loanId) {
        final LoanCurrencyDataMapper loanCurrencyMapper = new LoanCurrencyDataMapper(sqlGenerator);
        final String sql = "select " + loanCurrencyMapper.schema() + " where l.id = ?";

        return this.jdbcTemplate.queryForObject(sql, loanCurrencyMapper, loanId);
    }

    @Override
    public LoanTransactionData retrieveLoanTransactionTemplate(final Long loanId, final LoanTransactionType transactionType,
            final Long transactionId) {

        LoanTransactionData loanTransactionData = null;
        Collection<PaymentTypeData> paymentOptions = null;
        BigDecimal transactionAmount = BigDecimal.ZERO;
        switch (transactionType) {
            case CAPITALIZED_INCOME:
                final Loan loan = loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);
                BigDecimal capitalizedIncomeBalance = BigDecimal.ZERO;
                if (loan.getLoanProduct().getLoanProductRelatedDetail().isEnableIncomeCapitalization()) {
                    capitalizedIncomeBalance = loanCapitalizedIncomeBalanceRepository.findAllByLoanId(loanId).stream()
                            .map(LoanCapitalizedIncomeBalance::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                }
                transactionAmount = loan.getLoanProduct().isAllowApprovedDisbursedAmountsOverApplied()
                        ? loanMaximumAmountCalculator.getOverAppliedMax(loan)
                        : loan.getApprovedPrincipal();
                transactionAmount = transactionAmount.subtract(loan.getDisbursedAmount()).subtract(capitalizedIncomeBalance);
                paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
                loanTransactionData = LoanTransactionData.loanTransactionDataForCreditTemplate(
                        LoanEnumerations.transactionType(transactionType), DateUtils.getBusinessLocalDate(), transactionAmount,
                        paymentOptions, retriveLoanCurrencyData(loanId));
            break;
            case BUY_DOWN_FEE:
                paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
                loanTransactionData = LoanTransactionData.loanTransactionDataForCreditTemplate(
                        LoanEnumerations.transactionType(transactionType), DateUtils.getBusinessLocalDate(), transactionAmount,
                        paymentOptions, retriveLoanCurrencyData(loanId));
            break;
            case CAPITALIZED_INCOME_ADJUSTMENT:
                final LoanCapitalizedIncomeBalance loanCapitalizedIncomeBalance = loanCapitalizedIncomeBalanceRepository
                        .findByLoanIdAndLoanTransactionId(loanId, transactionId);

                transactionAmount = (loanCapitalizedIncomeBalance == null) ? BigDecimal.ZERO
                        : loanCapitalizedIncomeBalance.getAmount()
                                .subtract(MathUtil.nullToZero(loanCapitalizedIncomeBalance.getAmountAdjustment()));
                loanTransactionData = LoanTransactionData.loanTransactionDataForCreditTemplate(
                        LoanEnumerations.transactionType(transactionType), DateUtils.getBusinessLocalDate(), transactionAmount,
                        paymentOptions, retriveLoanCurrencyData(loanId));
            break;
            case BUY_DOWN_FEE_ADJUSTMENT:
                final LoanBuyDownFeeBalance loanBuyDownFeeBalance = loanBuyDownFeeBalanceRepository.findByLoanIdAndLoanTransactionId(loanId,
                        transactionId);

                transactionAmount = (loanBuyDownFeeBalance == null) ? BigDecimal.ZERO
                        : loanBuyDownFeeBalance.getAmount().subtract(MathUtil.nullToZero(loanBuyDownFeeBalance.getAmountAdjustment()));
                loanTransactionData = LoanTransactionData.loanTransactionDataForCreditTemplate(
                        LoanEnumerations.transactionType(transactionType), DateUtils.getBusinessLocalDate(), transactionAmount,
                        paymentOptions, retriveLoanCurrencyData(loanId));
            break;
            default:
                loanTransactionData = LoanTransactionData.templateOnTop(retrieveLoanTransactionTemplate(loanId),
                        LoanEnumerations.transactionType(transactionType));
            break;
        }

        return loanTransactionData;
    }

    @Override
    public LoanTransactionData retrieveLoanTransactionTemplate(final Long loanId) {

        this.context.authenticatedUser();

        RepaymentTransactionTemplateMapper mapper = new RepaymentTransactionTemplateMapper(sqlGenerator);
        String sql = "select " + mapper.schema();
        LoanTransactionData loanTransactionData = this.jdbcTemplate.queryForObject(sql, mapper, // NOSONAR
                LoanTransactionType.REPAYMENT.getValue(), LoanTransactionType.DOWN_PAYMENT.getValue(),
                LoanTransactionType.REPAYMENT.getValue(), LoanTransactionType.DOWN_PAYMENT.getValue(), loanId, loanId);
        final Collection<PaymentTypeData> paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
        return LoanTransactionData.templateOnTop(loanTransactionData, paymentOptions);
    }

    @Override
    public LoanTransactionData retrieveLoanPrePaymentTemplate(final LoanTransactionType repaymentTransactionType, final Long loanId,
            LocalDate onDate) {

        this.context.authenticatedUser();
        this.loanUtilService.validateRepaymentTransactionType(repaymentTransactionType);

        final Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId, true);

        final MonetaryCurrency currency = loan.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(currency);

        final CurrencyData currencyData = applicationCurrency.toData();

        final LocalDate earliestUnpaidInstallmentDate = DateUtils.getBusinessLocalDate();
        final LocalDate recalculateFrom = null;
        final ScheduleGeneratorDTO scheduleGeneratorDTO = loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom);
        final OutstandingAmountsDTO outstandingAmounts = loadTransactionProcessingService.fetchPrepaymentDetail(scheduleGeneratorDTO,
                onDate, loan);
        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(repaymentTransactionType);
        final Collection<PaymentTypeData> paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
        final BigDecimal outstandingLoanBalance = outstandingAmounts.principal().getAmount();
        final BigDecimal unrecognizedIncomePortion = null;

        BigDecimal adjustedChargeAmount = adjustPrepayInstallmentCharge(loan, onDate);
        BigDecimal totalAdjusted = outstandingAmounts.getTotalOutstanding().getAmount().subtract(adjustedChargeAmount);

        return new LoanTransactionData(null, null, null, transactionType, null, currencyData, earliestUnpaidInstallmentDate, totalAdjusted,
                loan.getNetDisbursalAmount(), outstandingAmounts.principal().getAmount(), outstandingAmounts.interest().getAmount(),
                outstandingAmounts.feeCharges().getAmount().subtract(adjustedChargeAmount), outstandingAmounts.penaltyCharges().getAmount(),
                null, unrecognizedIncomePortion, paymentOptions, ExternalId.empty(), null, null, outstandingLoanBalance, false, loanId,
                loan.getExternalId());
    }

    private BigDecimal adjustPrepayInstallmentCharge(Loan loan, final LocalDate onDate) {
        BigDecimal chargeAmount = BigDecimal.ZERO;
        /*
         * for(LoanCharge loanCharge: loan.charges()){ if(loanCharge.isInstalmentFee() &&
         * loanCharge.getCharge().getChargeCalculation()==ChargeCalculationType. FLAT.getValue()){ for
         * (LoanRepaymentScheduleInstallment installment : loan.getRepaymentScheduleInstallments()) {
         * if(onDate.isBefore(installment.getDueDate())){ LoanInstallmentCharge loanInstallmentCharge =
         * loanCharge.getInstallmentLoanCharge(installment.getInstallmentNumber( )); if(loanInstallmentCharge != null){
         * chargeAmount = chargeAmount.add(loanInstallmentCharge.getAmountOutstanding()); }
         *
         * break; } } } }
         */
        return chargeAmount;
    }

    @Override
    public LoanTransactionData retrieveWaiveInterestDetails(final Long loanId) {

        // TODO - KW -OPTIMIZE - write simple sql query to fetch back overdue
        // interest that can be waived along with the date of repayment period
        // interest is overdue.
        final Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId, true);
        final MonetaryCurrency currency = loan.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(currency);
        final CurrencyData currencyData = applicationCurrency.toData();

        final LoanTransaction waiveOfInterest = deriveDefaultInterestWaiverTransaction(loan);

        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.WAIVE_INTEREST);

        final BigDecimal amount = waiveOfInterest.getAmount(currency).getAmount();
        final BigDecimal outstandingLoanBalance = null;
        final BigDecimal unrecognizedIncomePortion = null;

        return new LoanTransactionData(null, null, null, transactionType, null, currencyData, waiveOfInterest.getTransactionDate(), amount,
                loan.getNetDisbursalAmount(), null, null, null, null, null, ExternalId.empty(), null, null, outstandingLoanBalance,
                unrecognizedIncomePortion, false, loanId, loan.getExternalId());
    }

    @Override
    public LoanTransactionData retrieveNewClosureDetails() {

        this.context.authenticatedUser();
        final BigDecimal outstandingLoanBalance = null;
        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.WRITEOFF);
        final BigDecimal unrecognizedIncomePortion = null;
        return new LoanTransactionData(null, null, null, transactionType, null, null, DateUtils.getBusinessLocalDate(), null, null, null,
                null, null, null, null, ExternalId.empty(), null, null, outstandingLoanBalance, unrecognizedIncomePortion, false, null,
                null);

    }

    @Override
    public LoanApprovalData retrieveApprovalTemplate(final Long loanId) {
        final Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId, true);
        final ApplicationCurrency appCurrency = applicationCurrencyRepository.findOneWithNotFoundDetection(loan.getCurrency());
        return new LoanApprovalData(loan.getProposedPrincipal(), DateUtils.getBusinessLocalDate(), loan.getNetDisbursalAmount(),
                appCurrency.toData());
    }

    @Override
    public LoanTransactionData retrieveDisbursalTemplate(final Long loanId, boolean paymentDetailsRequired) {
        final Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId, true);
        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.DISBURSEMENT);
        Collection<PaymentTypeData> paymentOptions = null;
        if (paymentDetailsRequired) {
            paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
        }
        final ApplicationCurrency appCurrency = applicationCurrencyRepository.findOneWithNotFoundDetection(loan.getCurrency());
        return LoanTransactionData.loanTransactionDataForDisbursalTemplate(transactionType,
                loan.getExpectedDisbursedOnLocalDateForTemplate(), loan.getDisburseAmountForTemplate(), loan.getNetDisbursalAmount(),
                paymentOptions, loan.retriveLastEmiAmount(), loan.getNextPossibleRepaymentDateForRescheduling(), appCurrency.toData());
    }

    @Override
    public Integer retrieveNumberOfRepayments(final Long loanId) {
        this.context.authenticatedUser();
        return this.loanRepositoryWrapper.getNumberOfRepayments(loanId);
    }

    @Override
    public List<LoanRepaymentScheduleInstallmentData> getRepaymentDataResponse(final Long loanId) {
        this.context.authenticatedUser();
        final List<LoanRepaymentScheduleInstallment> loanRepaymentScheduleInstallments = this.loanRepositoryWrapper
                .getLoanRepaymentScheduleInstallments(loanId);
        List<LoanRepaymentScheduleInstallmentData> loanRepaymentScheduleInstallmentData = new ArrayList<>();

        for (LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment : loanRepaymentScheduleInstallments) {
            loanRepaymentScheduleInstallmentData.add(LoanRepaymentScheduleInstallmentData.instanceOf(
                    loanRepaymentScheduleInstallment.getId(), loanRepaymentScheduleInstallment.getInstallmentNumber(),
                    loanRepaymentScheduleInstallment.getDueDate(), loanRepaymentScheduleInstallment
                            .getTotalOutstanding(loanRepaymentScheduleInstallment.getLoan().getCurrency()).getAmount()));
        }
        return loanRepaymentScheduleInstallmentData;
    }

    @Override
    public LoanTransactionData retrieveLoanTransaction(final Long loanId, final Long transactionId) {
        this.context.authenticatedUser();
        try {
            final LoanTransactionsMapper rm = new LoanTransactionsMapper(sqlGenerator);
            final String sql = "select " + rm.loanPaymentsSchema() + " where l.id = ? and tr.id = ? ";
            LoanTransactionData loanTransactionData = this.jdbcTemplate.queryForObject(sql, rm, loanId, transactionId); // NOSONAR
            loanTransactionData.setLoanTransactionRelations(
                    loanTransactionRelationReadService.fetchLoanTransactionRelationDataFrom(loanTransactionData.getId()));
            return loanTransactionData;
        } catch (final EmptyResultDataAccessException e) {
            throw new LoanTransactionNotFoundException(transactionId, e);
        }
    }

    @Override
    public org.springframework.data.domain.Page<LoanTransactionData> retrieveLoanTransactions(
            @jakarta.validation.constraints.NotNull Long loanId, Set<LoanTransactionType> excludedTransactionTypes, Pageable pageable) {
        final org.springframework.data.domain.Page<LoanTransaction> transactionPage = loanTransactionRepository
                .findAll((root, query, builder) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    final Join<Object, Object> loanjoin = root.join("loan");
                    predicates.add(builder.equal(loanjoin.get("id"), loanId));

                    if (excludedTransactionTypes != null && !excludedTransactionTypes.isEmpty()) {
                        final List<LoanTransactionType> excludedTransactionTypeValues = excludedTransactionTypes.stream().toList();
                        predicates.add(builder.not(root.get("typeOf").in(excludedTransactionTypeValues)));
                    }
                    return builder.and(predicates.toArray(new Predicate[] {}));
                }, pageable);

        return transactionPage.map(loanTransactionMapper::mapLoanTransaction);
    }

    @Override
    public Long getLoanIdByLoanExternalId(String externalId) {
        ExternalId loanExternalId = ExternalIdFactory.produce(externalId);
        Long loanId = loanRepositoryWrapper.findIdByExternalId(loanExternalId);
        if (Objects.isNull(loanId)) {
            throw new LoanNotFoundException(loanExternalId);
        }
        return loanId;
    }

    private static final class LoanCurrencyDataMapper implements RowMapper<CurrencyData> {

        private final DatabaseSpecificSQLGenerator sqlGenerator;

        LoanCurrencyDataMapper(DatabaseSpecificSQLGenerator sqlGenerator) {
            this.sqlGenerator = sqlGenerator;
        }

        public String schema() {
            return " l.currency_code as currencyCode, l.currency_digits as currencyDigits, l.currency_multiplesof as inMultiplesOf, rc."
                    + sqlGenerator.escape("name")
                    + " as currencyName, rc.display_symbol as currencyDisplaySymbol, rc.internationalized_name_code as currencyNameCode from m_loan l join m_currency rc on rc."
                    + sqlGenerator.escape("code") + " = l.currency_code ";
        }

        @Override
        public CurrencyData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            return new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf, currencyDisplaySymbol, currencyNameCode);
        }
    }

    private static final class LoanMapper implements RowMapper<LoanAccountData> {

        private final DatabaseSpecificSQLGenerator sqlGenerator;
        private final DelinquencyReadPlatformService delinquencyReadPlatformService;

        LoanMapper(DatabaseSpecificSQLGenerator sqlGenerator, DelinquencyReadPlatformService delinquencyReadPlatformService) {
            this.sqlGenerator = sqlGenerator;
            this.delinquencyReadPlatformService = delinquencyReadPlatformService;
        }

        public String loanSchema() {
            return "l.id as id, l.account_no as accountNo, l.external_id as externalId, l.fund_id as fundId, f.name as fundName,"
                    + " l.loan_type_enum as loanType, l.loanpurpose_cv_id as loanPurposeId, cv.code_value as loanPurposeName,"
                    + " lp.id as loanProductId, lp.name as loanProductName, lp.description as loanProductDescription,"
                    + " lp.is_linked_to_floating_interest_rates as isLoanProductLinkedToFloatingRate, "
                    + " lp.allow_variabe_installments as isvariableInstallmentsAllowed, "
                    + " lp.allow_multiple_disbursals as multiDisburseLoan, lp.disallow_expected_disbursements as disallowExpectedDisbursements, "
                    + " lp.can_define_fixed_emi_amount as canDefineInstallmentAmount,"
                    + " c.id as clientId, c.account_no as clientAccountNo, c.display_name as clientName, c.office_id as clientOfficeId, c.external_id as clientExternalId,"
                    + " g.id as groupId, g.account_no as groupAccountNo, g.display_name as groupName,"
                    + " g.office_id as groupOfficeId, g.staff_id As groupStaffId , g.parent_id as groupParentId, (select mg.display_name from m_group mg where mg.id = g.parent_id) as centerName, "
                    + " g.hierarchy As groupHierarchy , g.level_id as groupLevel, g.external_id As groupExternalId, "
                    + " g.status_enum as statusEnum, g.activation_date as activationDate, "
                    + " l.submittedon_date as submittedOnDate, sbu.username as submittedByUsername, sbu.firstname as submittedByFirstname, sbu.lastname as submittedByLastname,"
                    + " l.rejectedon_date as rejectedOnDate, rbu.username as rejectedByUsername, rbu.firstname as rejectedByFirstname, rbu.lastname as rejectedByLastname,"
                    + " l.withdrawnon_date as withdrawnOnDate, wbu.username as withdrawnByUsername, wbu.firstname as withdrawnByFirstname, wbu.lastname as withdrawnByLastname,"
                    + " l.approvedon_date as approvedOnDate, abu.username as approvedByUsername, abu.firstname as approvedByFirstname, abu.lastname as approvedByLastname,"
                    + " l.expected_disbursedon_date as expectedDisbursementDate, l.disbursedon_date as actualDisbursementDate, dbu.username as disbursedByUsername, dbu.firstname as disbursedByFirstname, dbu.lastname as disbursedByLastname,"
                    + " l.closedon_date as closedOnDate, cbu.username as closedByUsername, cbu.firstname as closedByFirstname, cbu.lastname as closedByLastname, l.writtenoffon_date as writtenOffOnDate, "
                    + " l.expected_firstrepaymenton_date as expectedFirstRepaymentOnDate, l.interest_calculated_from_date as interestChargedFromDate, l.maturedon_date as actualMaturityDate, l.expected_maturedon_date as expectedMaturityDate, "
                    + " l.principal_amount_proposed as proposedPrincipal, l.principal_amount as principal, l.total_principal_derived as totalPrincipal, l.approved_principal as approvedPrincipal, l.net_disbursal_amount as netDisbursalAmount, l.arrearstolerance_amount as inArrearsTolerance, l.number_of_repayments as numberOfRepayments, l.repay_every as repaymentEvery,"
                    + " l.grace_on_principal_periods as graceOnPrincipalPayment, l.recurring_moratorium_principal_periods as recurringMoratoriumOnPrincipalPeriods, l.grace_on_interest_periods as graceOnInterestPayment, l.grace_interest_free_periods as graceOnInterestCharged,l.grace_on_arrears_ageing as graceOnArrearsAgeing,"
                    + " l.nominal_interest_rate_per_period as interestRatePerPeriod, l.annual_nominal_interest_rate as annualInterestRate, "
                    + " l.repayment_period_frequency_enum as repaymentFrequencyType, l.interest_period_frequency_enum as interestRateFrequencyType, "
                    + " l.fixed_length as fixedLength, "
                    + " l.term_frequency as termFrequency, l.term_period_frequency_enum as termPeriodFrequencyType, "
                    + " l.amortization_method_enum as amortizationType, l.interest_method_enum as interestType, l.is_equal_amortization as isEqualAmortization, l.interest_calculated_in_period_enum as interestCalculationPeriodType,"
                    + " l.fixed_principal_percentage_per_installment fixedPrincipalPercentagePerInstallment, "
                    + " l.allow_partial_period_interest_calcualtion as allowPartialPeriodInterestCalcualtion,"
                    + " l.loan_status_id as lifeCycleStatusId, l.loan_transaction_strategy_code as transactionStrategyCode, "
                    + " l.loan_transaction_strategy_name as transactionStrategyName, l.enable_installment_level_delinquency as enableInstallmentLevelDelinquency, "
                    + " l.currency_code as currencyCode, l.currency_digits as currencyDigits, l.currency_multiplesof as inMultiplesOf, rc."
                    + sqlGenerator.escape("name")
                    + " as currencyName, rc.display_symbol as currencyDisplaySymbol, rc.internationalized_name_code as currencyNameCode, "
                    + " l.loan_officer_id as loanOfficerId, s.display_name as loanOfficerName, "
                    + " l.principal_disbursed_derived as principalDisbursed, l.capitalized_income_derived as totalCapitalizedIncome, l.capitalized_income_adjustment_derived as totalCapitalizedIncomeAdjustment, l.principal_repaid_derived as principalPaid,"
                    + " l.principal_adjustments_derived as principalAdjustments, l.principal_writtenoff_derived as principalWrittenOff,"
                    + " l.fee_adjustments_derived as feeAdjustments, l.penalty_adjustments_derived as penaltyAdjustments,"
                    + " l.principal_outstanding_derived as principalOutstanding, l.interest_charged_derived as interestCharged,"
                    + " l.interest_repaid_derived as interestPaid, l.interest_waived_derived as interestWaived,"
                    + " l.interest_writtenoff_derived as interestWrittenOff, l.interest_outstanding_derived as interestOutstanding,"
                    + " l.fee_charges_charged_derived as feeChargesCharged,"
                    + " l.total_charges_due_at_disbursement_derived as feeChargesDueAtDisbursementCharged,"
                    + " l.fee_charges_repaid_derived as feeChargesPaid, l.fee_charges_waived_derived as feeChargesWaived,"
                    + " l.fee_charges_writtenoff_derived as feeChargesWrittenOff,"
                    + " l.fee_charges_outstanding_derived as feeChargesOutstanding,"
                    + " l.penalty_charges_charged_derived as penaltyChargesCharged,"
                    + " l.penalty_charges_repaid_derived as penaltyChargesPaid,"
                    + " l.penalty_charges_waived_derived as penaltyChargesWaived,"
                    + " l.penalty_charges_writtenoff_derived as penaltyChargesWrittenOff,"
                    + " l.penalty_charges_outstanding_derived as penaltyChargesOutstanding, "
                    + " l.total_expected_repayment_derived as totalExpectedRepayment, l.total_repayment_derived as totalRepayment,"
                    + " l.total_expected_costofloan_derived as totalExpectedCostOfLoan, l.total_costofloan_derived as totalCostOfLoan,"
                    + " l.total_waived_derived as totalWaived, l.total_writtenoff_derived as totalWrittenOff,"
                    + " l.writeoff_reason_cv_id as writeoffReasonId, codev.code_value as writeoffReason,"
                    + " l.total_outstanding_derived as totalOutstanding, l.total_overpaid_derived as totalOverpaid,"
                    + " l.fixed_emi_amount as fixedEmiAmount, l.max_outstanding_loan_balance as outstandingLoanBalance,"
                    + " l.loan_sub_status_id as loanSubStatusId, la.principal_overdue_derived as principalOverdue, l.is_fraud as isFraud, "
                    + " la.interest_overdue_derived as interestOverdue, la.fee_charges_overdue_derived as feeChargesOverdue,"
                    + " la.penalty_charges_overdue_derived as penaltyChargesOverdue, la.total_overdue_derived as totalOverdue,"
                    + " la.overdue_since_date_derived as overdueSinceDate, "
                    + " l.sync_disbursement_with_meeting as syncDisbursementWithMeeting,"
                    + " l.loan_counter as loanCounter, l.loan_product_counter as loanProductCounter,"
                    + " l.is_npa as isNPA, l.days_in_month_enum as daysInMonth, l.days_in_year_enum as daysInYear, "
                    + " l.interest_recalculation_enabled as isInterestRecalculationEnabled, "
                    + " lir.id as lirId, lir.loan_id as loanId, lir.compound_type_enum as compoundType, lir.reschedule_strategy_enum as rescheduleStrategy, "
                    + " lir.rest_frequency_type_enum as restFrequencyEnum, lir.rest_frequency_interval as restFrequencyInterval, "
                    + " lir.rest_frequency_nth_day_enum as restFrequencyNthDayEnum, "
                    + " lir.rest_frequency_weekday_enum as restFrequencyWeekDayEnum, "
                    + " lir.rest_frequency_on_day as restFrequencyOnDay, "
                    + " lir.compounding_frequency_type_enum as compoundingFrequencyEnum, lir.compounding_frequency_interval as compoundingInterval, "
                    + " lir.compounding_frequency_nth_day_enum as compoundingFrequencyNthDayEnum, "
                    + " lir.compounding_frequency_weekday_enum as compoundingFrequencyWeekDayEnum, "
                    + " lir.compounding_frequency_on_day as compoundingFrequencyOnDay, "
                    + " lir.is_compounding_to_be_posted_as_transaction as isCompoundingToBePostedAsTransaction, "
                    + " lir.allow_compounding_on_eod as allowCompoundingOnEod, "
                    + " lir.disallow_interest_calc_on_past_due as disallowInterestCalculationOnPastDue, "
                    + " l.is_floating_interest_rate as isFloatingInterestRate, "
                    + " l.interest_rate_differential as interestRateDifferential, "
                    + " l.days_in_year_custom_strategy as daysInYearCustomStrategy, "
                    + " l.enable_income_capitalization as enableIncomeCapitalization, "
                    + " l.capitalized_income_calculation_type as capitalizedIncomeCalculationType, "
                    + " l.capitalized_income_strategy as capitalizedIncomeStrategy, "
                    + " l.capitalized_income_type as capitalizedIncomeType, l.is_merchant_buy_down_fee as merchantBuyDownFee, " //
                    + " l.enable_buy_down_fee as enableBuyDownFee, l.buy_down_fee_calculation_type as buyDownFeeCalculationType, "
                    + " l.buy_down_fee_strategy as buyDownFeeStrategy, l.buy_down_fee_income_type as buyDownFeeIncomeType, "
                    + " l.create_standing_instruction_at_disbursement as createStandingInstructionAtDisbursement, "
                    + " lpvi.minimum_gap as minimuminstallmentgap, lpvi.maximum_gap as maximuminstallmentgap, "
                    + " lp.can_use_for_topup as canUseForTopup, l.is_topup as isTopup, topup.closure_loan_id as closureLoanId, "
                    + " l.total_recovered_derived as totalRecovered, topuploan.account_no as closureLoanAccountNo, "
                    + " topup.topup_amount as topupAmount, l.last_closed_business_date as lastClosedBusinessDate,l.overpaidon_date as overpaidOnDate, "
                    + " l.is_charged_off as isChargedOff, l.charge_off_reason_cv_id as chargeOffReasonId, codec.code_value as chargeOffReason, l.charged_off_on_date as chargedOffOnDate, l.enable_down_payment as enableDownPayment, l.disbursed_amount_percentage_for_down_payment as disbursedAmountPercentageForDownPayment, l.enable_auto_repayment_for_down_payment as enableAutoRepaymentForDownPayment,"
                    + " cobu.username as chargedOffByUsername, cobu.firstname as chargedOffByFirstname, cobu.lastname as chargedOffByLastname, l.loan_schedule_type as loanScheduleType, l.loan_schedule_processing_type as loanScheduleProcessingType, "
                    + " l.charge_off_behaviour as chargeOffBehaviour, l.interest_recognition_on_disbursement_date as interestRecognitionOnDisbursementDate " //
                    + " from m_loan l" //
                    + " join m_product_loan lp on lp.id = l.product_id" //
                    + " left join m_loan_recalculation_details lir on lir.loan_id = l.id join m_currency rc on rc."
                    + sqlGenerator.escape("code") + " = l.currency_code" //
                    + " left join m_client c on c.id = l.client_id" //
                    + " left join m_group g on g.id = l.group_id" //
                    + " left join m_loan_arrears_aging la on la.loan_id = l.id" //
                    + " left join m_fund f on f.id = l.fund_id" //
                    + " left join m_staff s on s.id = l.loan_officer_id" //
                    + " left join m_appuser sbu on sbu.id = l.created_by left join m_appuser rbu on rbu.id = l.rejectedon_userid"
                    + " left join m_appuser wbu on wbu.id = l.withdrawnon_userid"
                    + " left join m_appuser abu on abu.id = l.approvedon_userid"
                    + " left join m_appuser dbu on dbu.id = l.disbursedon_userid left join m_appuser cbu on cbu.id = l.closedon_userid"
                    + " left join m_appuser cobu on cobu.id = l.charged_off_by_userid "
                    + " left join m_code_value cv on cv.id = l.loanpurpose_cv_id"
                    + " left join m_code_value codev on codev.id = l.writeoff_reason_cv_id"
                    + " left join m_code_value codec on codec.id = l.charge_off_reason_cv_id"
                    + " left join m_product_loan_variable_installment_config lpvi on lpvi.loan_product_id = l.product_id"
                    + " left join m_loan_topup as topup on l.id = topup.loan_id"
                    + " left join m_loan as topuploan on topuploan.id = topup.closure_loan_id ";

        }

        @Override
        public LoanAccountData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            final CurrencyData currencyData = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);

            final Long id = rs.getLong("id");
            final String accountNo = rs.getString("accountNo");
            final String externalIdStr = rs.getString("externalId");
            final ExternalId externalId = ExternalIdFactory.produce(externalIdStr);

            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final String clientAccountNo = rs.getString("clientAccountNo");
            final Long clientOfficeId = JdbcSupport.getLong(rs, "clientOfficeId");
            final ExternalId clientExternalId = ExternalIdFactory.produce(rs.getString("clientExternalId"));
            final String clientName = rs.getString("clientName");

            final Long groupId = JdbcSupport.getLong(rs, "groupId");
            final String groupName = rs.getString("groupName");
            final String groupAccountNo = rs.getString("groupAccountNo");
            final String groupExternalId = rs.getString("groupExternalId");
            final Long groupOfficeId = JdbcSupport.getLong(rs, "groupOfficeId");
            final Long groupStaffId = JdbcSupport.getLong(rs, "groupStaffId");
            final Long groupParentId = JdbcSupport.getLong(rs, "groupParentId");
            final String centerName = rs.getString("centerName");
            final String groupHierarchy = rs.getString("groupHierarchy");
            final String groupLevel = rs.getString("groupLevel");

            final Integer loanTypeId = JdbcSupport.getInteger(rs, "loanType");
            final EnumOptionData loanType = AccountEnumerations.loanType(loanTypeId);

            final Long fundId = JdbcSupport.getLong(rs, "fundId");
            final String fundName = rs.getString("fundName");

            final Long loanOfficerId = JdbcSupport.getLong(rs, "loanOfficerId");
            final String loanOfficerName = rs.getString("loanOfficerName");

            final Long loanPurposeId = JdbcSupport.getLong(rs, "loanPurposeId");
            final String loanPurposeName = rs.getString("loanPurposeName");

            final Long loanProductId = JdbcSupport.getLong(rs, "loanProductId");
            final String loanProductName = rs.getString("loanProductName");
            final String loanProductDescription = rs.getString("loanProductDescription");
            final boolean isLoanProductLinkedToFloatingRate = rs.getBoolean("isLoanProductLinkedToFloatingRate");
            final Boolean multiDisburseLoan = rs.getBoolean("multiDisburseLoan");
            final Boolean canDefineInstallmentAmount = rs.getBoolean("canDefineInstallmentAmount");
            final BigDecimal outstandingLoanBalance = rs.getBigDecimal("outstandingLoanBalance");

            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final String submittedByUsername = rs.getString("submittedByUsername");
            final String submittedByFirstname = rs.getString("submittedByFirstname");
            final String submittedByLastname = rs.getString("submittedByLastname");

            final LocalDate rejectedOnDate = JdbcSupport.getLocalDate(rs, "rejectedOnDate");
            final String rejectedByUsername = rs.getString("rejectedByUsername");
            final String rejectedByFirstname = rs.getString("rejectedByFirstname");
            final String rejectedByLastname = rs.getString("rejectedByLastname");

            final LocalDate withdrawnOnDate = JdbcSupport.getLocalDate(rs, "withdrawnOnDate");
            final String withdrawnByUsername = rs.getString("withdrawnByUsername");
            final String withdrawnByFirstname = rs.getString("withdrawnByFirstname");
            final String withdrawnByLastname = rs.getString("withdrawnByLastname");

            final LocalDate approvedOnDate = JdbcSupport.getLocalDate(rs, "approvedOnDate");
            final String approvedByUsername = rs.getString("approvedByUsername");
            final String approvedByFirstname = rs.getString("approvedByFirstname");
            final String approvedByLastname = rs.getString("approvedByLastname");

            final LocalDate expectedDisbursementDate = JdbcSupport.getLocalDate(rs, "expectedDisbursementDate");
            final LocalDate actualDisbursementDate = JdbcSupport.getLocalDate(rs, "actualDisbursementDate");
            final String disbursedByUsername = rs.getString("disbursedByUsername");
            final String disbursedByFirstname = rs.getString("disbursedByFirstname");
            final String disbursedByLastname = rs.getString("disbursedByLastname");

            final LocalDate closedOnDate = JdbcSupport.getLocalDate(rs, "closedOnDate");
            final String closedByUsername = rs.getString("closedByUsername");
            final String closedByFirstname = rs.getString("closedByFirstname");
            final String closedByLastname = rs.getString("closedByLastname");

            final LocalDate writtenOffOnDate = JdbcSupport.getLocalDate(rs, "writtenOffOnDate");
            final Long writeoffReasonId = JdbcSupport.getLong(rs, "writeoffReasonId");
            final String writeoffReason = rs.getString("writeoffReason");
            final LocalDate actualMaturityDate = JdbcSupport.getLocalDate(rs, "actualMaturityDate");
            final LocalDate expectedMaturityDate = JdbcSupport.getLocalDate(rs, "expectedMaturityDate");

            final Boolean isvariableInstallmentsAllowed = rs.getBoolean("isvariableInstallmentsAllowed");
            final Integer minimumGap = rs.getInt("minimuminstallmentgap");
            final Integer maximumGap = rs.getInt("maximuminstallmentgap");

            final LocalDate chargedOffOnDate = JdbcSupport.getLocalDate(rs, "chargedOffOnDate");
            final String chargedOffByUsername = rs.getString("chargedOffByUsername");
            final String chargedOffByFirstname = rs.getString("chargedOffByFirstname");
            final String chargedOffByLastname = rs.getString("chargedOffByLastname");
            final Long chargeOffReasonId = JdbcSupport.getLong(rs, "chargeOffReasonId");
            final String chargeOffReason = rs.getString("chargeOffReason");
            final boolean isChargedOff = rs.getBoolean("isChargedOff");

            final LoanApplicationTimelineData timeline = new LoanApplicationTimelineData(submittedOnDate, submittedByUsername,
                    submittedByFirstname, submittedByLastname, rejectedOnDate, rejectedByUsername, rejectedByFirstname, rejectedByLastname,
                    withdrawnOnDate, withdrawnByUsername, withdrawnByFirstname, withdrawnByLastname, approvedOnDate, approvedByUsername,
                    approvedByFirstname, approvedByLastname, expectedDisbursementDate, actualDisbursementDate, disbursedByUsername,
                    disbursedByFirstname, disbursedByLastname, closedOnDate, closedByUsername, closedByFirstname, closedByLastname,
                    actualMaturityDate, expectedMaturityDate, writtenOffOnDate, closedByUsername, closedByFirstname, closedByLastname,
                    chargedOffOnDate, chargedOffByUsername, chargedOffByFirstname, chargedOffByLastname);

            final BigDecimal principal = rs.getBigDecimal("principal");
            final BigDecimal approvedPrincipal = rs.getBigDecimal("approvedPrincipal");
            final BigDecimal proposedPrincipal = rs.getBigDecimal("proposedPrincipal");
            final BigDecimal netDisbursalAmount = rs.getBigDecimal("netDisbursalAmount");
            final BigDecimal totalOverpaid = rs.getBigDecimal("totalOverpaid");
            final BigDecimal inArrearsTolerance = rs.getBigDecimal("inArrearsTolerance");

            final Integer numberOfRepayments = JdbcSupport.getInteger(rs, "numberOfRepayments");
            final Integer repaymentEvery = JdbcSupport.getInteger(rs, "repaymentEvery");
            final BigDecimal interestRatePerPeriod = rs.getBigDecimal("interestRatePerPeriod");
            final BigDecimal annualInterestRate = rs.getBigDecimal("annualInterestRate");
            final BigDecimal interestRateDifferential = rs.getBigDecimal("interestRateDifferential");
            final boolean isFloatingInterestRate = rs.getBoolean("isFloatingInterestRate");

            final Integer graceOnPrincipalPayment = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnPrincipalPayment");
            final Integer recurringMoratoriumOnPrincipalPeriods = JdbcSupport.getIntegerDefaultToNullIfZero(rs,
                    "recurringMoratoriumOnPrincipalPeriods");
            final Integer graceOnInterestPayment = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnInterestPayment");
            final Integer graceOnInterestCharged = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnInterestCharged");
            final Integer graceOnArrearsAgeing = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnArrearsAgeing");

            final Integer termFrequency = JdbcSupport.getInteger(rs, "termFrequency");
            final Integer termPeriodFrequencyTypeInt = JdbcSupport.getInteger(rs, "termPeriodFrequencyType");
            final EnumOptionData termPeriodFrequencyType = LoanEnumerations.termFrequencyType(termPeriodFrequencyTypeInt);

            final int repaymentFrequencyTypeInt = JdbcSupport.getInteger(rs, "repaymentFrequencyType");
            final EnumOptionData repaymentFrequencyType = LoanEnumerations.repaymentFrequencyType(repaymentFrequencyTypeInt);

            final int interestRateFrequencyTypeInt = JdbcSupport.getInteger(rs, "interestRateFrequencyType");
            final EnumOptionData interestRateFrequencyType = LoanEnumerations.interestRateFrequencyType(interestRateFrequencyTypeInt);

            final String transactionStrategyCode = rs.getString("transactionStrategyCode");
            final String transactionStrategyName = rs.getString("transactionStrategyName");

            final int amortizationTypeInt = JdbcSupport.getInteger(rs, "amortizationType");
            final int interestTypeInt = JdbcSupport.getInteger(rs, "interestType");
            final int interestCalculationPeriodTypeInt = JdbcSupport.getInteger(rs, "interestCalculationPeriodType");
            final boolean isEqualAmortization = rs.getBoolean("isEqualAmortization");
            final EnumOptionData amortizationType = LoanEnumerations.amortizationType(amortizationTypeInt);
            final BigDecimal fixedPrincipalPercentagePerInstallment = rs.getBigDecimal("fixedPrincipalPercentagePerInstallment");
            final EnumOptionData interestType = LoanEnumerations.interestType(interestTypeInt);
            final EnumOptionData interestCalculationPeriodType = LoanEnumerations
                    .interestCalculationPeriodType(interestCalculationPeriodTypeInt);
            final Boolean allowPartialPeriodInterestCalcualtion = rs.getBoolean("allowPartialPeriodInterestCalcualtion");

            final Integer lifeCycleStatusId = JdbcSupport.getInteger(rs, "lifeCycleStatusId");
            final LoanStatusEnumData status = LoanEnumerations.status(lifeCycleStatusId);

            final Integer loanSubStatusId = JdbcSupport.getInteger(rs, "loanSubStatusId");
            EnumOptionData loanSubStatus = null;
            if (loanSubStatusId != null) {
                loanSubStatus = LoanSubStatus.loanSubStatus(loanSubStatusId);
            }

            // settings
            final LocalDate expectedFirstRepaymentOnDate = JdbcSupport.getLocalDate(rs, "expectedFirstRepaymentOnDate");
            final LocalDate interestChargedFromDate = JdbcSupport.getLocalDate(rs, "interestChargedFromDate");

            final Boolean syncDisbursementWithMeeting = rs.getBoolean("syncDisbursementWithMeeting");

            final BigDecimal feeChargesDueAtDisbursementCharged = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs,
                    "feeChargesDueAtDisbursementCharged");
            LoanSummaryData loanSummary = null;
            Boolean inArrears = false;
            if (status.getId().intValue() >= 300) {

                // loan summary
                final BigDecimal principalDisbursed = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalDisbursed");
                final BigDecimal totalPrincipal = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalPrincipal");
                final BigDecimal totalCapitalizedIncome = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalCapitalizedIncome");
                final BigDecimal totalCapitalizedIncomeAdjustment = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs,
                        "totalCapitalizedIncomeAdjustment");
                final BigDecimal principalAdjustments = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalAdjustments");
                final BigDecimal principalPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalPaid");
                final BigDecimal principalWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalWrittenOff");
                final BigDecimal principalOutstanding = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalOutstanding");
                final BigDecimal principalOverdue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalOverdue");

                final BigDecimal interestCharged = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestCharged");
                final BigDecimal interestPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestPaid");
                final BigDecimal interestWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWaived");
                final BigDecimal interestWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWrittenOff");
                final BigDecimal interestOutstanding = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestOutstanding");
                final BigDecimal interestOverdue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestOverdue");

                final BigDecimal feeChargesCharged = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesCharged");
                final BigDecimal feeAdjustments = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeAdjustments");
                final BigDecimal feeChargesPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesPaid");
                final BigDecimal feeChargesWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesWaived");
                final BigDecimal feeChargesWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesWrittenOff");
                final BigDecimal feeChargesOutstanding = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesOutstanding");
                final BigDecimal feeChargesOverdue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesOverdue");

                final BigDecimal penaltyChargesCharged = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesCharged");
                final BigDecimal penaltyAdjustments = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyAdjustments");
                final BigDecimal penaltyChargesPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesPaid");
                final BigDecimal penaltyChargesWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesWaived");
                final BigDecimal penaltyChargesWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesWrittenOff");
                final BigDecimal penaltyChargesOutstanding = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesOutstanding");
                final BigDecimal penaltyChargesOverdue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesOverdue");

                final BigDecimal totalExpectedRepayment = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalExpectedRepayment");
                final BigDecimal totalRepayment = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalRepayment");
                final BigDecimal totalExpectedCostOfLoan = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalExpectedCostOfLoan");
                final BigDecimal totalCostOfLoan = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalCostOfLoan");
                final BigDecimal totalWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalWaived");
                final BigDecimal totalWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalWrittenOff");
                final BigDecimal totalOutstanding = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalOutstanding");
                final BigDecimal totalOverdue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalOverdue");
                final BigDecimal totalRecovered = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalRecovered");

                final LocalDate overdueSinceDate = JdbcSupport.getLocalDate(rs, "overdueSinceDate");
                inArrears = (overdueSinceDate != null);

                loanSummary = LoanSummaryData.builder().currency(currencyData).principalDisbursed(principalDisbursed)
                        .totalPrincipal(totalPrincipal).totalCapitalizedIncome(totalCapitalizedIncome)
                        .totalCapitalizedIncomeAdjustment(totalCapitalizedIncomeAdjustment).principalAdjustments(principalAdjustments)
                        .principalPaid(principalPaid).principalWrittenOff(principalWrittenOff).principalOutstanding(principalOutstanding)
                        .principalOverdue(principalOverdue).interestCharged(interestCharged).interestPaid(interestPaid)
                        .interestWaived(interestWaived).interestWrittenOff(interestWrittenOff).interestOutstanding(interestOutstanding)
                        .interestOverdue(interestOverdue).feeChargesCharged(feeChargesCharged).feeAdjustments(feeAdjustments)
                        .feeChargesDueAtDisbursementCharged(feeChargesDueAtDisbursementCharged).feeChargesPaid(feeChargesPaid)
                        .feeChargesWaived(feeChargesWaived).feeChargesWrittenOff(feeChargesWrittenOff)
                        .feeChargesOutstanding(feeChargesOutstanding).feeChargesOverdue(feeChargesOverdue)
                        .penaltyChargesCharged(penaltyChargesCharged).penaltyAdjustments(penaltyAdjustments)
                        .penaltyChargesPaid(penaltyChargesPaid).penaltyChargesWaived(penaltyChargesWaived)
                        .penaltyChargesWrittenOff(penaltyChargesWrittenOff).penaltyChargesOutstanding(penaltyChargesOutstanding)
                        .penaltyChargesOverdue(penaltyChargesOverdue).totalExpectedRepayment(totalExpectedRepayment)
                        .totalRepayment(totalRepayment).totalExpectedCostOfLoan(totalExpectedCostOfLoan).totalCostOfLoan(totalCostOfLoan)
                        .totalWaived(totalWaived).totalWrittenOff(totalWrittenOff).totalOutstanding(totalOutstanding)
                        .totalOverdue(totalOverdue).overdueSinceDate(overdueSinceDate).writeoffReasonId(writeoffReasonId)
                        .writeoffReason(writeoffReason).totalRecovered(totalRecovered).chargeOffReasonId(chargeOffReasonId)
                        .chargeOffReason(chargeOffReason).build();

            }

            GroupGeneralData groupData = null;
            if (groupId != null) {
                final Integer groupStatusEnum = JdbcSupport.getInteger(rs, "statusEnum");
                final EnumOptionData groupStatus = ClientEnumerations.status(groupStatusEnum);
                final LocalDate activationDate = JdbcSupport.getLocalDate(rs, "activationDate");
                groupData = GroupGeneralData.instance(groupId, groupAccountNo, groupName, groupExternalId, groupStatus, activationDate,
                        groupOfficeId, null, groupParentId, centerName, groupStaffId, null, groupHierarchy, groupLevel, null);
            }

            final Integer loanCounter = JdbcSupport.getInteger(rs, "loanCounter");
            final Integer loanProductCounter = JdbcSupport.getInteger(rs, "loanProductCounter");
            final BigDecimal fixedEmiAmount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "fixedEmiAmount");
            final Boolean isNPA = rs.getBoolean("isNPA");

            final int daysInMonth = JdbcSupport.getInteger(rs, "daysInMonth");
            final EnumOptionData daysInMonthType = CommonEnumerations.daysInMonthType(daysInMonth);
            final int daysInYear = JdbcSupport.getInteger(rs, "daysInYear");
            final EnumOptionData daysInYearType = CommonEnumerations.daysInYearType(daysInYear);
            final boolean isInterestRecalculationEnabled = rs.getBoolean("isInterestRecalculationEnabled");
            final Boolean createStandingInstructionAtDisbursement = rs.getBoolean("createStandingInstructionAtDisbursement");

            LoanInterestRecalculationData interestRecalculationData = null;
            if (isInterestRecalculationEnabled) {

                final Long lprId = JdbcSupport.getLong(rs, "lirId");
                final Long productId = JdbcSupport.getLong(rs, "loanId");
                final int compoundTypeEnumValue = JdbcSupport.getInteger(rs, "compoundType");
                final EnumOptionData interestRecalculationCompoundingType = LoanEnumerations
                        .interestRecalculationCompoundingType(compoundTypeEnumValue);
                final int rescheduleStrategyEnumValue = JdbcSupport.getInteger(rs, "rescheduleStrategy");
                final EnumOptionData rescheduleStrategyType = LoanEnumerations.rescheduleStrategyType(rescheduleStrategyEnumValue);
                final CalendarData calendarData = null;
                final int restFrequencyEnumValue = JdbcSupport.getInteger(rs, "restFrequencyEnum");
                final EnumOptionData restFrequencyType = LoanEnumerations.interestRecalculationFrequencyType(restFrequencyEnumValue);
                final int restFrequencyInterval = JdbcSupport.getInteger(rs, "restFrequencyInterval");
                final Integer restFrequencyNthDayEnumValue = JdbcSupport.getInteger(rs, "restFrequencyNthDayEnum");
                EnumOptionData restFrequencyNthDayEnum = null;
                if (restFrequencyNthDayEnumValue != null) {
                    restFrequencyNthDayEnum = LoanEnumerations.interestRecalculationCompoundingNthDayType(restFrequencyNthDayEnumValue);
                }
                final Integer restFrequencyWeekDayEnumValue = JdbcSupport.getInteger(rs, "restFrequencyWeekDayEnum");
                EnumOptionData restFrequencyWeekDayEnum = null;
                if (restFrequencyWeekDayEnumValue != null) {
                    restFrequencyWeekDayEnum = LoanEnumerations
                            .interestRecalculationCompoundingDayOfWeekType(restFrequencyWeekDayEnumValue);
                }
                final Integer restFrequencyOnDay = JdbcSupport.getInteger(rs, "restFrequencyOnDay");
                final CalendarData compoundingCalendarData = null;
                final Integer compoundingFrequencyEnumValue = JdbcSupport.getInteger(rs, "compoundingFrequencyEnum");
                EnumOptionData compoundingFrequencyType = null;
                if (compoundingFrequencyEnumValue != null) {
                    compoundingFrequencyType = LoanEnumerations.interestRecalculationFrequencyType(compoundingFrequencyEnumValue);
                }
                final Integer compoundingInterval = JdbcSupport.getInteger(rs, "compoundingInterval");
                final Integer compoundingFrequencyNthDayEnumValue = JdbcSupport.getInteger(rs, "compoundingFrequencyNthDayEnum");
                EnumOptionData compoundingFrequencyNthDayEnum = null;
                if (compoundingFrequencyNthDayEnumValue != null) {
                    compoundingFrequencyNthDayEnum = LoanEnumerations
                            .interestRecalculationCompoundingNthDayType(compoundingFrequencyNthDayEnumValue);
                }
                final Integer compoundingFrequencyWeekDayEnumValue = JdbcSupport.getInteger(rs, "compoundingFrequencyWeekDayEnum");
                EnumOptionData compoundingFrequencyWeekDayEnum = null;
                if (compoundingFrequencyWeekDayEnumValue != null) {
                    compoundingFrequencyWeekDayEnum = LoanEnumerations
                            .interestRecalculationCompoundingDayOfWeekType(compoundingFrequencyWeekDayEnumValue);
                }
                final Integer compoundingFrequencyOnDay = JdbcSupport.getInteger(rs, "compoundingFrequencyOnDay");

                final Boolean isCompoundingToBePostedAsTransaction = rs.getBoolean("isCompoundingToBePostedAsTransaction");
                final Boolean allowCompoundingOnEod = rs.getBoolean("allowCompoundingOnEod");
                final Boolean disallowInterestCalculationOnPastDue = rs.getBoolean("disallowInterestCalculationOnPastDue");
                interestRecalculationData = new LoanInterestRecalculationData(lprId, productId, interestRecalculationCompoundingType,
                        rescheduleStrategyType, calendarData, restFrequencyType, restFrequencyInterval, restFrequencyNthDayEnum,
                        restFrequencyWeekDayEnum, restFrequencyOnDay, compoundingCalendarData, compoundingFrequencyType,
                        compoundingInterval, compoundingFrequencyNthDayEnum, compoundingFrequencyWeekDayEnum, compoundingFrequencyOnDay,
                        isCompoundingToBePostedAsTransaction, allowCompoundingOnEod, disallowInterestCalculationOnPastDue);
            }

            final boolean canUseForTopup = rs.getBoolean("canUseForTopup");
            final boolean isTopup = rs.getBoolean("isTopup");
            final Long closureLoanId = rs.getLong("closureLoanId");
            final String closureLoanAccountNo = rs.getString("closureLoanAccountNo");
            final BigDecimal topupAmount = rs.getBigDecimal("topupAmount");
            final boolean disallowExpectedDisbursements = rs.getBoolean("disallowExpectedDisbursements");
            // Current Delinquency Range Data
            DelinquencyRangeData delinquencyRange = this.delinquencyReadPlatformService.retrieveCurrentDelinquencyTag(id);

            final boolean isFraud = rs.getBoolean("isFraud");
            final LocalDate lastClosedBusinessDate = JdbcSupport.getLocalDate(rs, "lastClosedBusinessDate");
            final LocalDate overpaidOnDate = JdbcSupport.getLocalDate(rs, "overpaidOnDate");

            final boolean enableDownPayment = rs.getBoolean("enableDownPayment");
            final BigDecimal disbursedAmountPercentageForDownPayment = rs.getBigDecimal("disbursedAmountPercentageForDownPayment");
            final boolean enableAutoRepaymentForDownPayment = rs.getBoolean("enableAutoRepaymentForDownPayment");
            final boolean enableInstallmentLevelDelinquency = rs.getBoolean("enableInstallmentLevelDelinquency");
            final String loanScheduleTypeStr = rs.getString("loanScheduleType");
            final LoanScheduleType loanScheduleType = LoanScheduleType.valueOf(loanScheduleTypeStr);
            final String loanScheduleProcessingTypeStr = rs.getString("loanScheduleProcessingType");
            final LoanScheduleProcessingType loanScheduleProcessingType = LoanScheduleProcessingType.valueOf(loanScheduleProcessingTypeStr);
            final Integer fixedLength = JdbcSupport.getInteger(rs, "fixedLength");
            final LoanChargeOffBehaviour chargeOffBehaviour = LoanChargeOffBehaviour.valueOf(rs.getString("chargeOffBehaviour"));
            final boolean interestRecognitionOnDisbursementDate = rs.getBoolean("interestRecognitionOnDisbursementDate");
            final StringEnumOptionData daysInYearCustomStrategy = ApiFacingEnum.getStringEnumOptionData(DaysInYearCustomStrategyType.class,
                    rs.getString("daysInYearCustomStrategy"));
            final boolean enableIncomeCapitalization = rs.getBoolean("enableIncomeCapitalization");
            final StringEnumOptionData capitalizedIncomeCalculationType = ApiFacingEnum
                    .getStringEnumOptionData(LoanCapitalizedIncomeCalculationType.class, rs.getString("capitalizedIncomeCalculationType"));
            final StringEnumOptionData capitalizedIncomeStrategy = ApiFacingEnum
                    .getStringEnumOptionData(LoanCapitalizedIncomeStrategy.class, rs.getString("capitalizedIncomeStrategy"));
            final StringEnumOptionData capitalizedIncomeType = ApiFacingEnum.getStringEnumOptionData(LoanCapitalizedIncomeType.class,
                    rs.getString("capitalizedIncomeType"));
            final boolean enableBuyDownFee = rs.getBoolean("enableBuyDownFee");
            final StringEnumOptionData buyDownFeeCalculationType = ApiFacingEnum
                    .getStringEnumOptionData(LoanBuyDownFeeCalculationType.class, rs.getString("buyDownFeeCalculationType"));
            final StringEnumOptionData buyDownFeeStrategy = ApiFacingEnum.getStringEnumOptionData(LoanBuyDownFeeStrategy.class,
                    rs.getString("buyDownFeeStrategy"));
            final StringEnumOptionData buyDownFeeIncomeType = ApiFacingEnum.getStringEnumOptionData(LoanBuyDownFeeIncomeType.class,
                    rs.getString("buyDownFeeIncomeType"));
            final boolean merchantBuyDownFee = rs.getBoolean("merchantBuyDownFee");

            return LoanAccountData.basicLoanDetails(id, accountNo, status, externalId, clientId, clientAccountNo, clientName,
                    clientOfficeId, clientExternalId, groupData, loanType, loanProductId, loanProductName, loanProductDescription,
                    isLoanProductLinkedToFloatingRate, fundId, fundName, loanPurposeId, loanPurposeName, loanOfficerId, loanOfficerName,
                    currencyData, proposedPrincipal, principal, approvedPrincipal, netDisbursalAmount, totalOverpaid, inArrearsTolerance,
                    termFrequency, termPeriodFrequencyType, numberOfRepayments, repaymentEvery, repaymentFrequencyType, null, null,
                    transactionStrategyCode, transactionStrategyName, amortizationType, interestRatePerPeriod, interestRateFrequencyType,
                    annualInterestRate, interestType, isFloatingInterestRate, interestRateDifferential, interestCalculationPeriodType,
                    allowPartialPeriodInterestCalcualtion, expectedFirstRepaymentOnDate, graceOnPrincipalPayment,
                    recurringMoratoriumOnPrincipalPeriods, graceOnInterestPayment, graceOnInterestCharged, interestChargedFromDate,
                    timeline, loanSummary, feeChargesDueAtDisbursementCharged, syncDisbursementWithMeeting, loanCounter, loanProductCounter,
                    multiDisburseLoan, canDefineInstallmentAmount, fixedEmiAmount, outstandingLoanBalance, inArrears, graceOnArrearsAgeing,
                    isNPA, daysInMonthType, daysInYearType, isInterestRecalculationEnabled, interestRecalculationData,
                    createStandingInstructionAtDisbursement, isvariableInstallmentsAllowed, minimumGap, maximumGap, loanSubStatus,
                    canUseForTopup, isTopup, closureLoanId, closureLoanAccountNo, topupAmount, isEqualAmortization,
                    fixedPrincipalPercentagePerInstallment, delinquencyRange, disallowExpectedDisbursements, isFraud,
                    lastClosedBusinessDate, overpaidOnDate, isChargedOff, enableDownPayment, disbursedAmountPercentageForDownPayment,
                    enableAutoRepaymentForDownPayment, enableInstallmentLevelDelinquency, loanScheduleType.asEnumOptionData(),
                    loanScheduleProcessingType.asEnumOptionData(), fixedLength, chargeOffBehaviour.getValueAsStringEnumOptionData(),
                    interestRecognitionOnDisbursementDate, daysInYearCustomStrategy, enableIncomeCapitalization,
                    capitalizedIncomeCalculationType, capitalizedIncomeStrategy, capitalizedIncomeType, enableBuyDownFee,
                    buyDownFeeCalculationType, buyDownFeeStrategy, buyDownFeeIncomeType, merchantBuyDownFee);
        }
    }

    private static final class MusoniOverdueLoanScheduleMapper implements RowMapper<OverdueLoanScheduleData> {

        public String schema() {
            return " ls.loan_id as loanId, ls.installment as period, ls.fromdate as fromDate, ls.duedate as dueDate, ls.obligations_met_on_date as obligationsMetOnDate, ls.completed_derived as complete,"
                    + " ls.principal_amount as principalDue, ls.principal_completed_derived as principalPaid, ls.principal_writtenoff_derived as principalWrittenOff, "
                    + " ls.interest_amount as interestDue, ls.interest_completed_derived as interestPaid, ls.interest_waived_derived as interestWaived, ls.interest_writtenoff_derived as interestWrittenOff, "
                    + " ls.fee_charges_amount as feeChargesDue, ls.fee_charges_completed_derived as feeChargesPaid, ls.fee_charges_waived_derived as feeChargesWaived, ls.fee_charges_writtenoff_derived as feeChargesWrittenOff, "
                    + " ls.penalty_charges_amount as penaltyChargesDue, ls.penalty_charges_completed_derived as penaltyChargesPaid, ls.penalty_charges_waived_derived as penaltyChargesWaived, ls.penalty_charges_writtenoff_derived as penaltyChargesWrittenOff, "
                    + " ls.total_paid_in_advance_derived as totalPaidInAdvanceForPeriod, ls.total_paid_late_derived as totalPaidLateForPeriod, "
                    + " mc.amount,mc.id as chargeId  from m_loan_repayment_schedule ls " + " inner join m_loan ml on ml.id = ls.loan_id "
                    + " join m_product_loan_charge plc on plc.product_loan_id = ml.product_id "
                    + " join m_charge mc on mc.id = plc.charge_id ";

        }

        @Override
        public OverdueLoanScheduleData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long chargeId = rs.getLong("chargeId");
            final Long loanId = rs.getLong("loanId");
            final BigDecimal amount = rs.getBigDecimal("amount");
            final String dateFormat = "yyyy-MM-dd";
            final String dueDate = rs.getString("dueDate");
            final String locale = Locale.ENGLISH.toLanguageTag();

            final BigDecimal principalDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalDue");
            final BigDecimal principalPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalPaid");
            final BigDecimal principalWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalWrittenOff");

            final BigDecimal principalOutstanding = principalDue.subtract(principalPaid).subtract(principalWrittenOff);

            final BigDecimal interestExpectedDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestDue");
            final BigDecimal interestPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestPaid");
            final BigDecimal interestWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWaived");
            final BigDecimal interestWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWrittenOff");

            final BigDecimal interestActualDue = interestExpectedDue.subtract(interestWaived).subtract(interestWrittenOff);
            final BigDecimal interestOutstanding = interestActualDue.subtract(interestPaid);

            final Integer installmentNumber = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "period");

            return new OverdueLoanScheduleData(loanId, chargeId, dueDate, amount, dateFormat, locale, principalOutstanding,
                    interestOutstanding, installmentNumber);
        }
    }

    private static final class LoanScheduleResultSetExtractor implements ResultSetExtractor<LoanScheduleData> {

        private final CurrencyData currency;
        private final DisbursementData disbursement;
        private final BigDecimal totalFeeChargesDueAtDisbursement;
        private final Collection<DisbursementData> disbursementData;
        private final Collection<LoanTransactionRepaymentPeriodData> capitalizedIncomeData;
        private final LoanScheduleType loanScheduleType;
        private LocalDate lastDueDate;
        private BigDecimal outstandingLoanPrincipalBalance;
        private boolean excludePastUnDisbursed;

        LoanScheduleResultSetExtractor(final RepaymentScheduleRelatedLoanData repaymentScheduleRelatedLoanData,
                Collection<DisbursementData> disbursementData, Collection<LoanTransactionRepaymentPeriodData> capitalizedIncomeData,
                boolean isInterestRecalculationEnabled, LoanScheduleType loanScheduleType) {
            this.currency = repaymentScheduleRelatedLoanData.getCurrency();
            this.disbursement = repaymentScheduleRelatedLoanData.disbursementData();
            this.capitalizedIncomeData = capitalizedIncomeData;
            this.totalFeeChargesDueAtDisbursement = repaymentScheduleRelatedLoanData.getTotalFeeChargesAtDisbursement();
            this.lastDueDate = this.disbursement.disbursementDate();
            this.outstandingLoanPrincipalBalance = this.disbursement.getPrincipal();
            this.disbursementData = disbursementData;
            this.excludePastUnDisbursed = isInterestRecalculationEnabled;
            this.loanScheduleType = loanScheduleType;
        }

        public String schema() {

            return " ls.loan_id as loanId, ls.installment as period, ls.fromdate as fromDate, ls.duedate as dueDate, ls.obligations_met_on_date as obligationsMetOnDate, ls.completed_derived as complete,"
                    + " ls.principal_amount as principalDue, ls.principal_completed_derived as principalPaid, ls.principal_writtenoff_derived as principalWrittenOff, ls.is_additional as isAdditional, "
                    + " ls.interest_amount as interestDue, ls.interest_completed_derived as interestPaid, ls.interest_waived_derived as interestWaived, ls.interest_writtenoff_derived as interestWrittenOff, "
                    + " ls.fee_charges_amount as feeChargesDue, ls.fee_charges_completed_derived as feeChargesPaid, ls.fee_charges_waived_derived as feeChargesWaived, ls.fee_charges_writtenoff_derived as feeChargesWrittenOff, "
                    + " ls.penalty_charges_amount as penaltyChargesDue, ls.penalty_charges_completed_derived as penaltyChargesPaid, ls.penalty_charges_waived_derived as penaltyChargesWaived, "
                    + " ls.penalty_charges_writtenoff_derived as penaltyChargesWrittenOff, ls.total_paid_in_advance_derived as totalPaidInAdvanceForPeriod, "
                    + " ls.total_paid_late_derived as totalPaidLateForPeriod, ls.credits_amount as principalCredits, ls.credited_fee as feeCredits, ls.credited_penalty as penaltyCredits, ls.is_down_payment isDownPayment, "
                    + " ls.accrual_interest_derived as accrualInterest " + " from m_loan_repayment_schedule ls ";
        }

        @Override
        public LoanScheduleData extractData(@NonNull final ResultSet rs) throws SQLException, DataAccessException {
            BigDecimal waivedChargeAmount = BigDecimal.ZERO;
            for (DisbursementData disbursementDetail : disbursementData) {
                waivedChargeAmount = waivedChargeAmount.add(disbursementDetail.getWaivedChargeAmount());
            }
            final LoanSchedulePeriodData disbursementPeriod = LoanSchedulePeriodData.disbursementOnlyPeriod(
                    this.disbursement.disbursementDate(), this.disbursement.getPrincipal(), this.totalFeeChargesDueAtDisbursement,
                    this.disbursement.isDisbursed());

            final List<LoanSchedulePeriodData> periods = new ArrayList<>();
            final MonetaryCurrency monCurrency = new MonetaryCurrency(this.currency.getCode(), this.currency.getDecimalPlaces(),
                    this.currency.getInMultiplesOf());
            BigDecimal totalPrincipalDisbursed = BigDecimal.ZERO;
            BigDecimal disbursementChargeAmount = this.totalFeeChargesDueAtDisbursement;
            if (disbursementData.isEmpty()) {
                periods.add(disbursementPeriod);
                totalPrincipalDisbursed = Money.of(monCurrency, this.disbursement.getPrincipal()).getAmount();
            } else {
                if (!this.disbursement.isDisbursed()) {
                    excludePastUnDisbursed = false;
                }
                for (DisbursementData data : disbursementData) {
                    if (data.getChargeAmount() != null) {
                        disbursementChargeAmount = disbursementChargeAmount.subtract(data.getChargeAmount());
                    }
                }
                this.outstandingLoanPrincipalBalance = BigDecimal.ZERO;
            }

            Money totalPrincipalExpected = Money.zero(monCurrency);
            Money totalPrincipalPaid = Money.zero(monCurrency);
            Money totalInterestCharged = Money.zero(monCurrency);
            Money totalFeeChargesCharged = Money.zero(monCurrency);
            Money totalPenaltyChargesCharged = Money.zero(monCurrency);
            Money totalWaived = Money.zero(monCurrency);
            Money totalWrittenOff = Money.zero(monCurrency);
            Money totalRepaymentExpected = Money.zero(monCurrency);
            Money totalRepayment = Money.zero(monCurrency);
            Money totalPaidInAdvance = Money.zero(monCurrency);
            Money totalPaidLate = Money.zero(monCurrency);
            Money totalOutstanding = Money.zero(monCurrency);
            Money totalCredits = Money.zero(monCurrency);

            // update totals with details of fees charged during disbursement
            totalFeeChargesCharged = totalFeeChargesCharged.plus(disbursementPeriod.getFeeChargesDue().subtract(waivedChargeAmount));
            totalRepaymentExpected = totalRepaymentExpected.plus(disbursementPeriod.getFeeChargesDue()).minus(waivedChargeAmount);
            totalRepayment = totalRepayment.plus(disbursementPeriod.getFeeChargesPaid()).minus(waivedChargeAmount);
            totalOutstanding = totalOutstanding.plus(disbursementPeriod.getFeeChargesDue()).minus(disbursementPeriod.getFeeChargesPaid());

            Integer loanTermInDays = 0;
            Set<Long> disbursementPeriodIds = new HashSet<>();
            while (rs.next()) {

                final Integer period = JdbcSupport.getInteger(rs, "period");
                LocalDate fromDate = JdbcSupport.getLocalDate(rs, "fromDate");
                final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "dueDate");
                final LocalDate obligationsMetOnDate = JdbcSupport.getLocalDate(rs, "obligationsMetOnDate");
                final boolean complete = rs.getBoolean("complete");

                List<LoanSchedulePeriodDataWrapper> combinedDataList = new ArrayList<>();
                combinedDataList.addAll(
                        collectEligibleDisbursementData(loanScheduleType, disbursementData, fromDate, dueDate, disbursementPeriodIds));
                combinedDataList.addAll(collectEligibleCapitalizedIncomeData(fromDate, dueDate, disbursementPeriodIds));
                combinedDataList.sort(this::sortPeriodDataHolders);
                fillLoanSchedulePeriodData(periods, combinedDataList, disbursementChargeAmount, waivedChargeAmount);

                BigDecimal disbursedAmount = calculateDisbursedAmount(combinedDataList);

                // Add the Charge back or Credits to the initial amount to avoid negative balance
                final BigDecimal principalCredits = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalCredits");
                final BigDecimal feeCredits = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeCredits");
                final BigDecimal penaltyCredits = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyCredits");
                final BigDecimal credits = principalCredits.add(feeCredits).add(penaltyCredits);
                this.outstandingLoanPrincipalBalance = this.outstandingLoanPrincipalBalance.add(principalCredits);

                totalPrincipalDisbursed = totalPrincipalDisbursed.add(disbursedAmount);

                Integer daysInPeriod = 0;
                if (fromDate != null) {
                    daysInPeriod = DateUtils.getExactDifferenceInDays(fromDate, dueDate);
                    loanTermInDays = loanTermInDays + daysInPeriod;
                }

                final BigDecimal principalDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalDue");
                totalPrincipalExpected = totalPrincipalExpected.plus(principalDue);
                final BigDecimal principalPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalPaid");
                totalPrincipalPaid = totalPrincipalPaid.plus(principalPaid);
                final BigDecimal principalWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalWrittenOff");

                final BigDecimal principalOutstanding = principalDue.subtract(principalPaid).subtract(principalWrittenOff);

                final BigDecimal interestExpectedDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestDue");
                totalInterestCharged = totalInterestCharged.plus(interestExpectedDue);
                final BigDecimal interestPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestPaid");
                final BigDecimal interestWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWaived");
                final BigDecimal interestWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWrittenOff");
                final BigDecimal accrualInterest = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "accrualInterest");

                final BigDecimal interestActualDue = interestExpectedDue.subtract(interestWaived).subtract(interestWrittenOff);
                final BigDecimal interestOutstanding = interestActualDue.subtract(interestPaid);

                final BigDecimal feeChargesExpectedDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesDue");
                totalFeeChargesCharged = totalFeeChargesCharged.plus(feeChargesExpectedDue);
                final BigDecimal feeChargesPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesPaid");
                final BigDecimal feeChargesWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesWaived");
                final BigDecimal feeChargesWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesWrittenOff");

                final BigDecimal feeChargesActualDue = feeChargesExpectedDue.subtract(feeChargesWaived).subtract(feeChargesWrittenOff);
                final BigDecimal feeChargesOutstanding = feeChargesActualDue.subtract(feeChargesPaid);

                final BigDecimal penaltyChargesExpectedDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesDue");
                totalPenaltyChargesCharged = totalPenaltyChargesCharged.plus(penaltyChargesExpectedDue);
                final BigDecimal penaltyChargesPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesPaid");
                final BigDecimal penaltyChargesWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesWaived");
                final BigDecimal penaltyChargesWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesWrittenOff");

                final BigDecimal totalPaidInAdvanceForPeriod = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs,
                        "totalPaidInAdvanceForPeriod");
                final BigDecimal totalPaidLateForPeriod = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalPaidLateForPeriod");

                final BigDecimal penaltyChargesActualDue = penaltyChargesExpectedDue.subtract(penaltyChargesWaived)
                        .subtract(penaltyChargesWrittenOff);
                final BigDecimal penaltyChargesOutstanding = penaltyChargesActualDue.subtract(penaltyChargesPaid);

                final BigDecimal totalExpectedCostOfLoanForPeriod = interestExpectedDue.add(feeChargesExpectedDue)
                        .add(penaltyChargesExpectedDue);

                final BigDecimal totalDueForPeriod = principalDue.add(totalExpectedCostOfLoanForPeriod);
                final BigDecimal totalPaidForPeriod = principalPaid.add(interestPaid).add(feeChargesPaid).add(penaltyChargesPaid);
                final BigDecimal totalWaivedForPeriod = interestWaived.add(feeChargesWaived).add(penaltyChargesWaived);
                totalWaived = totalWaived.plus(totalWaivedForPeriod);
                final BigDecimal totalWrittenOffForPeriod = principalWrittenOff.add(interestWrittenOff).add(feeChargesWrittenOff)
                        .add(penaltyChargesWrittenOff);
                totalWrittenOff = totalWrittenOff.plus(totalWrittenOffForPeriod);

                final BigDecimal totalOutstandingForPeriod = principalOutstanding.add(interestOutstanding).add(feeChargesOutstanding)
                        .add(penaltyChargesOutstanding);

                totalRepaymentExpected = totalRepaymentExpected.plus(totalDueForPeriod);
                totalRepayment = totalRepayment.plus(totalPaidForPeriod);
                totalPaidInAdvance = totalPaidInAdvance.plus(totalPaidInAdvanceForPeriod);
                totalPaidLate = totalPaidLate.plus(totalPaidLateForPeriod);
                totalOutstanding = totalOutstanding.plus(totalOutstandingForPeriod);
                totalCredits = totalCredits.add(credits);

                if (fromDate == null) {
                    fromDate = this.lastDueDate;
                }

                BigDecimal outstandingPrincipalBalanceOfLoan = this.outstandingLoanPrincipalBalance.subtract(principalDue);

                // update based on current period values
                this.lastDueDate = dueDate;
                this.outstandingLoanPrincipalBalance = this.outstandingLoanPrincipalBalance.subtract(principalDue);

                final boolean isDownPayment = rs.getBoolean("isDownPayment");

                LoanSchedulePeriodData periodData;

                periodData = LoanSchedulePeriodData.periodWithPayments(period, fromDate, dueDate, obligationsMetOnDate, complete,
                        principalDue, principalPaid, principalWrittenOff, principalOutstanding, outstandingPrincipalBalanceOfLoan,
                        interestExpectedDue, interestPaid, interestWaived, interestWrittenOff, interestOutstanding, feeChargesExpectedDue,
                        feeChargesPaid, feeChargesWaived, feeChargesWrittenOff, feeChargesOutstanding, penaltyChargesExpectedDue,
                        penaltyChargesPaid, penaltyChargesWaived, penaltyChargesWrittenOff, penaltyChargesOutstanding, totalPaidForPeriod,
                        totalPaidInAdvanceForPeriod, totalPaidLateForPeriod, totalWaivedForPeriod, totalWrittenOffForPeriod, credits,
                        isDownPayment, accrualInterest);

                periods.add(periodData);
            }

            return new LoanScheduleData(this.currency, periods, loanTermInDays, totalPrincipalDisbursed, totalPrincipalExpected.getAmount(),
                    totalPrincipalPaid.getAmount(), totalInterestCharged.getAmount(), totalFeeChargesCharged.getAmount(),
                    totalPenaltyChargesCharged.getAmount(), totalWaived.getAmount(), totalWrittenOff.getAmount(),
                    totalRepaymentExpected.getAmount(), totalRepayment.getAmount(), totalPaidInAdvance.getAmount(),
                    totalPaidLate.getAmount(), totalOutstanding.getAmount(), totalCredits.getAmount());
        }

        private List<LoanSchedulePeriodDataWrapper> collectEligibleDisbursementData(LoanScheduleType loanScheduleType,
                Collection<DisbursementData> disbursementData, LocalDate fromDate, LocalDate dueDate, Set<Long> disbursementPeriodIds) {
            List<LoanSchedulePeriodDataWrapper> disbursementDataList = new ArrayList<>();
            // Collect eligible disbursement data
            for (final DisbursementData data : disbursementData) {
                boolean isDueForDisbursement = data.isDueForDisbursement(loanScheduleType, fromDate, dueDate);
                boolean isEligible = ((fromDate.equals(this.disbursement.disbursementDate()) && data.disbursementDate().equals(fromDate))
                        || (fromDate.equals(dueDate) && data.disbursementDate().equals(fromDate))
                        || canAddDisbursementData(data, isDueForDisbursement, excludePastUnDisbursed))
                        && !disbursementPeriodIds.contains(data.getId());

                if (isEligible) {
                    disbursementDataList.add(new LoanSchedulePeriodDataWrapper(data, data.disbursementDate(), true));
                    disbursementPeriodIds.add(data.getId());
                }
            }
            return disbursementDataList;
        }

        private List<LoanSchedulePeriodDataWrapper> collectEligibleCapitalizedIncomeData(LocalDate fromDate, LocalDate dueDate,
                Set<Long> disbursementPeriodIds) {
            List<LoanSchedulePeriodDataWrapper> capitalizedIncomeDataList = new ArrayList<>();
            // Collect eligible capitalized income data
            for (LoanTransactionRepaymentPeriodData data : capitalizedIncomeData) {
                boolean isEligible = canAddCapitalizedIncomeData(data, fromDate, dueDate)
                        && !disbursementPeriodIds.contains(data.getTransactionId());

                if (isEligible) {
                    capitalizedIncomeDataList.add(new LoanSchedulePeriodDataWrapper(data, data.getDate(), false));
                    disbursementPeriodIds.add(data.getTransactionId());
                }
            }
            return capitalizedIncomeDataList;
        }

        private void fillLoanSchedulePeriodData(List<LoanSchedulePeriodData> periods, List<LoanSchedulePeriodDataWrapper> combinedDataList,
                BigDecimal disbursementChargeAmount, BigDecimal waivedChargeAmount) {
            // Process all collected data in chronological order
            for (LoanSchedulePeriodDataWrapper dataItem : combinedDataList) {
                LoanSchedulePeriodData periodData;
                if (dataItem.isDisbursement()) {
                    // Process disbursement data
                    DisbursementData data = (DisbursementData) dataItem.getData();
                    periodData = createLoanSchedulePeriodData(data, disbursementChargeAmount, waivedChargeAmount);
                } else {
                    // Process capitalized income data
                    LoanTransactionRepaymentPeriodData data = (LoanTransactionRepaymentPeriodData) dataItem.getData();
                    periodData = createLoanSchedulePeriodData(data);
                }

                // Common processing for both data types
                periods.add(periodData);
                this.outstandingLoanPrincipalBalance = this.outstandingLoanPrincipalBalance.add(periodData.getPrincipalDisbursed());
            }
        }

        private BigDecimal calculateDisbursedAmount(List<LoanSchedulePeriodDataWrapper> combinedDataList) {
            BigDecimal disbursedAmount = BigDecimal.ZERO;
            for (LoanSchedulePeriodDataWrapper dataItem : combinedDataList) {
                if (dataItem.isDisbursement()) {
                    DisbursementData data = (DisbursementData) dataItem.getData();
                    disbursedAmount = disbursedAmount.add(data.getPrincipal());
                }
            }
            return disbursedAmount;
        }

        private int sortPeriodDataHolders(LoanSchedulePeriodDataWrapper item1, LoanSchedulePeriodDataWrapper item2) {
            int dateComparison = item1.getDate().compareTo(item2.getDate());
            if (dateComparison == 0 && item1.isDisbursement() != item2.isDisbursement()) {
                // If dates are equal, prioritize disbursement data
                return item1.isDisbursement() ? -1 : 1;
            }
            return dateComparison;
        }

        private LoanSchedulePeriodData createLoanSchedulePeriodData(final DisbursementData data, BigDecimal disbursementChargeAmount,
                BigDecimal waivedChargeAmount) {
            BigDecimal chargeAmount = data.getChargeAmount() == null ? disbursementChargeAmount
                    : disbursementChargeAmount.add(data.getChargeAmount()).subtract(waivedChargeAmount);
            return LoanSchedulePeriodData.disbursementOnlyPeriod(data.disbursementDate(), data.getPrincipal(), chargeAmount,
                    data.isDisbursed());
        }

        private LoanSchedulePeriodData createLoanSchedulePeriodData(final LoanTransactionRepaymentPeriodData data) {
            BigDecimal feeCharges = Objects.isNull(data.getFeeChargesPortion()) ? BigDecimal.ZERO : data.getFeeChargesPortion();
            return LoanSchedulePeriodData.disbursementOnlyPeriod(data.getDate(), data.getAmount(), feeCharges, !data.isReversed());
        }

        private boolean canAddDisbursementData(DisbursementData data, boolean isDueForDisbursement, boolean excludePastUnDisbursed) {
            return (!excludePastUnDisbursed || data.isDisbursed() || !DateUtils.isBeforeBusinessDate(data.disbursementDate()))
                    && isDueForDisbursement;
        }

        private boolean canAddCapitalizedIncomeData(LoanTransactionRepaymentPeriodData data, LocalDate fromDate, LocalDate dueDate) {
            return !data.isReversed() && DateUtils.isDateInRangeFromInclusiveToExclusive(fromDate, dueDate, data.getDate());
        }

    }

    private static final class LoanTransactionsMapper implements RowMapper<LoanTransactionData> {

        private final DatabaseSpecificSQLGenerator sqlGenerator;

        LoanTransactionsMapper(DatabaseSpecificSQLGenerator sqlGenerator) {
            this.sqlGenerator = sqlGenerator;
        }

        public String loanPaymentsSchema() {

            return " tr.id as id, tr.transaction_type_enum as transactionType, tr.transaction_date as " + sqlGenerator.escape("date")
                    + ", tr.amount as total, tr.principal_portion_derived as principal, tr.interest_portion_derived as interest, "
                    + " tr.fee_charges_portion_derived as fees, tr.penalty_charges_portion_derived as penalties,  "
                    + " tr.overpayment_portion_derived as overpayment, tr.outstanding_loan_balance_derived as outstandingLoanBalance, "
                    + " tr.unrecognized_income_portion as unrecognizedIncome, tr.submitted_on_date as submittedOnDate, "
                    + " tr.manually_adjusted_or_reversed as manuallyReversed, tr.reversal_external_id as reversalExternalId, tr.reversed_on_date as reversedOnDate, "
                    + " pd.payment_type_id as paymentType,pd.account_number as accountNumber,pd.check_number as checkNumber, "
                    + " pd.receipt_number as receiptNumber, pd.bank_number as bankNumber,pd.routing_code as routingCode, l.net_disbursal_amount as netDisbursalAmount,"
                    + " l.currency_code as currencyCode, l.currency_digits as currencyDigits, l.currency_multiplesof as inMultiplesOf, rc."
                    + sqlGenerator.escape("name") + " as currencyName, l.id as loanId, l.external_id as externalLoanId, "
                    + " rc.display_symbol as currencyDisplaySymbol, rc.internationalized_name_code as currencyNameCode, "
                    + " pt.value as paymentTypeName, tr.external_id as externalId, tr.office_id as officeId, office.name as officeName, "
                    + " fromtran.id as fromTransferId, fromtran.is_reversed as fromTransferReversed,"
                    + " fromtran.transaction_date as fromTransferDate, fromtran.amount as fromTransferAmount,"
                    + " fromtran.description as fromTransferDescription, "
                    + " totran.id as toTransferId, totran.is_reversed as toTransferReversed, "
                    + " totran.transaction_date as toTransferDate, totran.amount as toTransferAmount,"
                    + " totran.description as toTransferDescription from m_loan l join m_loan_transaction tr on tr.loan_id = l.id "
                    + " join m_currency rc on rc." + sqlGenerator.escape("code") + " = l.currency_code "
                    + " left JOIN m_payment_detail pd ON tr.payment_detail_id = pd.id"
                    + " left join m_payment_type pt on pd.payment_type_id = pt.id left join m_office office on office.id=tr.office_id"
                    + " left join m_account_transfer_transaction fromtran on fromtran.from_loan_transaction_id = tr.id "
                    + " left join m_account_transfer_transaction totran on totran.to_loan_transaction_id = tr.id ";
        }

        @Override
        public LoanTransactionData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            final CurrencyData currencyData = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);

            final Long id = rs.getLong("id");
            final Long loanId = rs.getLong("loanId");
            final String externalLoanIdStr = rs.getString("externalLoanId");
            final ExternalId externalLoanId = ExternalIdFactory.produce(externalLoanIdStr);
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final int transactionTypeInt = JdbcSupport.getInteger(rs, "transactionType");
            final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(transactionTypeInt);
            final boolean manuallyReversed = rs.getBoolean("manuallyReversed");

            PaymentDetailData paymentDetailData = null;

            final Long paymentTypeId = JdbcSupport.getLong(rs, "paymentType");
            if (paymentTypeId != null) {
                final String typeName = rs.getString("paymentTypeName");
                final PaymentTypeData paymentType = PaymentTypeData.instance(paymentTypeId, typeName);
                final String accountNumber = rs.getString("accountNumber");
                final String checkNumber = rs.getString("checkNumber");
                final String routingCode = rs.getString("routingCode");
                final String receiptNumber = rs.getString("receiptNumber");
                final String bankNumber = rs.getString("bankNumber");
                paymentDetailData = new PaymentDetailData(id, paymentType, accountNumber, checkNumber, routingCode, receiptNumber,
                        bankNumber);
            }

            final LocalDate date = JdbcSupport.getLocalDate(rs, "date");
            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final BigDecimal totalAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "total");
            final BigDecimal principalPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principal");
            final BigDecimal interestPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interest");
            final BigDecimal feeChargesPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "fees");
            final BigDecimal penaltyChargesPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penalties");
            final BigDecimal overPaymentPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "overpayment");
            final BigDecimal unrecognizedIncomePortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "unrecognizedIncome");
            final BigDecimal outstandingLoanBalance = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "outstandingLoanBalance");
            final String externalIdStr = rs.getString("externalId");
            final ExternalId externalId = ExternalIdFactory.produce(externalIdStr);
            final String reversalExternalIdStr = rs.getString("reversalExternalId");
            final ExternalId reversalExternalId = ExternalIdFactory.produce(reversalExternalIdStr);
            final LocalDate reversedOnDate = JdbcSupport.getLocalDate(rs, "reversedOnDate");

            final BigDecimal netDisbursalAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "netDisbursalAmount");

            AccountTransferData transfer = null;
            final Long fromTransferId = JdbcSupport.getLong(rs, "fromTransferId");
            final Long toTransferId = JdbcSupport.getLong(rs, "toTransferId");
            if (fromTransferId != null) {
                final LocalDate fromTransferDate = JdbcSupport.getLocalDate(rs, "fromTransferDate");
                final BigDecimal fromTransferAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "fromTransferAmount");
                final boolean fromTransferReversed = rs.getBoolean("fromTransferReversed");
                final String fromTransferDescription = rs.getString("fromTransferDescription");

                transfer = AccountTransferData.transferBasicDetails(fromTransferId, currencyData, fromTransferAmount, fromTransferDate,
                        fromTransferDescription, fromTransferReversed);
            } else if (toTransferId != null) {
                final LocalDate toTransferDate = JdbcSupport.getLocalDate(rs, "toTransferDate");
                final BigDecimal toTransferAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "toTransferAmount");
                final boolean toTransferReversed = rs.getBoolean("toTransferReversed");
                final String toTransferDescription = rs.getString("toTransferDescription");

                transfer = AccountTransferData.transferBasicDetails(toTransferId, currencyData, toTransferAmount, toTransferDate,
                        toTransferDescription, toTransferReversed);
            }

            return new LoanTransactionData(id, officeId, officeName, transactionType, paymentDetailData, currencyData, date, totalAmount,
                    netDisbursalAmount, principalPortion, interestPortion, feeChargesPortion, penaltyChargesPortion, overPaymentPortion,
                    unrecognizedIncomePortion, externalId, transfer, null, outstandingLoanBalance, submittedOnDate, manuallyReversed,
                    reversalExternalId, reversedOnDate, loanId, externalLoanId);
        }
    }

    @Override
    public LoanAccountData retrieveLoanProductDetailsTemplate(final Long productId, final Long clientId, final Long groupId) {

        this.context.authenticatedUser();

        final LoanProductData loanProduct = this.loanProductReadPlatformService.retrieveLoanProduct(productId);
        final Collection<EnumOptionData> loanTermFrequencyTypeOptions = this.loanDropdownReadPlatformService
                .retrieveLoanTermFrequencyTypeOptions();
        final Collection<EnumOptionData> repaymentFrequencyTypeOptions = this.loanDropdownReadPlatformService
                .retrieveRepaymentFrequencyTypeOptions();
        final Collection<EnumOptionData> repaymentFrequencyNthDayTypeOptions = this.loanDropdownReadPlatformService
                .retrieveRepaymentFrequencyOptionsForNthDayOfMonth();
        final Collection<EnumOptionData> repaymentFrequencyDaysOfWeekTypeOptions = this.loanDropdownReadPlatformService
                .retrieveRepaymentFrequencyOptionsForDaysOfWeek();
        final Collection<EnumOptionData> interestRateFrequencyTypeOptions = this.loanDropdownReadPlatformService
                .retrieveInterestRateFrequencyTypeOptions();
        final Collection<EnumOptionData> amortizationTypeOptions = this.loanDropdownReadPlatformService
                .retrieveLoanAmortizationTypeOptions();
        Collection<EnumOptionData> interestTypeOptions = null;
        if (loanProduct.isLinkedToFloatingInterestRates()) {
            interestTypeOptions = List.of(interestType(InterestMethod.DECLINING_BALANCE));
        } else {
            interestTypeOptions = this.loanDropdownReadPlatformService.retrieveLoanInterestTypeOptions();
        }
        final Collection<EnumOptionData> interestCalculationPeriodTypeOptions = this.loanDropdownReadPlatformService
                .retrieveLoanInterestRateCalculatedInPeriodOptions();
        final Collection<FundData> fundOptions = this.fundReadPlatformService.retrieveAllFunds();
        final Collection<TransactionProcessingStrategyData> repaymentStrategyOptions = this.loanDropdownReadPlatformService
                .retrieveTransactionProcessingStrategies();
        final Collection<CodeValueData> loanPurposeOptions = this.codeValueReadPlatformService.retrieveCodeValuesByCode("LoanPurpose");
        final Collection<CodeValueData> loanCollateralOptions = this.codeValueReadPlatformService
                .retrieveCodeValuesByCode("LoanCollateral");
        Collection<ChargeData> chargeOptions = null;
        if (loanProduct.getMultiDisburseLoan()) {
            chargeOptions = this.chargeReadPlatformService.retrieveLoanProductApplicableCharges(productId,
                    new ChargeTimeType[] { ChargeTimeType.OVERDUE_INSTALLMENT });
        } else {
            chargeOptions = this.chargeReadPlatformService.retrieveLoanProductApplicableCharges(productId,
                    new ChargeTimeType[] { ChargeTimeType.OVERDUE_INSTALLMENT, ChargeTimeType.TRANCHE_DISBURSEMENT });
        }

        Integer loanCycleCounter = null;
        if (loanProduct.isUseBorrowerCycle()) {
            if (clientId == null) {
                loanCycleCounter = retriveLoanCounter(groupId, AccountType.GROUP.getValue(), loanProduct.getId());
            } else {
                loanCycleCounter = retriveLoanCounter(clientId, loanProduct.getId());
            }
        }

        Collection<LoanAccountSummaryData> activeLoanOptions = null;
        if (loanProduct.isCanUseForTopup() && clientId != null) {
            activeLoanOptions = this.accountDetailsReadPlatformService.retrieveClientActiveLoanAccountSummary(clientId);
        } else if (loanProduct.isCanUseForTopup() && groupId != null) {
            activeLoanOptions = this.accountDetailsReadPlatformService.retrieveGroupActiveLoanAccountSummary(groupId);
        }

        return new LoanAccountData().withProductData(loanProduct, loanCycleCounter) //
                .setTermFrequencyTypeOptions(loanTermFrequencyTypeOptions) //
                .setRepaymentFrequencyTypeOptions(repaymentFrequencyTypeOptions) //
                .setRepaymentFrequencyNthDayTypeOptions(repaymentFrequencyNthDayTypeOptions) //
                .setRepaymentFrequencyDaysOfWeekTypeOptions(repaymentFrequencyDaysOfWeekTypeOptions) //
                .setTransactionProcessingStrategyOptions(repaymentStrategyOptions) //
                .setInterestRateFrequencyTypeOptions(interestRateFrequencyTypeOptions) //
                .setAmortizationTypeOptions(amortizationTypeOptions) //
                .setInterestTypeOptions(interestTypeOptions) //
                .setInterestCalculationPeriodTypeOptions(interestCalculationPeriodTypeOptions) //
                .setFundOptions(fundOptions) //
                .setChargeOptions(chargeOptions) //
                .setLoanPurposeOptions(loanPurposeOptions) //
                .setLoanCollateralOptions(loanCollateralOptions) //
                .setClientActiveLoanOptions(activeLoanOptions) //
                .setLoanScheduleTypeOptions(LoanScheduleType.getValuesAsEnumOptionDataList()) //
                .setLoanScheduleProcessingTypeOptions(LoanScheduleProcessingType.getValuesAsEnumOptionDataList()); //
    }

    @Override
    public Collection<CalendarData> retrieveCalendars(final Long groupId) {
        Collection<CalendarData> calendarsData = new ArrayList<>();
        calendarsData.addAll(
                this.calendarReadPlatformService.retrieveParentCalendarsByEntity(groupId, CalendarEntityType.GROUPS.getValue(), null));
        calendarsData
                .addAll(this.calendarReadPlatformService.retrieveCalendarsByEntity(groupId, CalendarEntityType.GROUPS.getValue(), null));
        calendarsData = this.calendarReadPlatformService.updateWithRecurringDates(calendarsData);
        return calendarsData;
    }

    @Override
    public Collection<StaffData> retrieveAllowedLoanOfficers(final Long selectedOfficeId, final boolean staffInSelectedOfficeOnly) {
        if (selectedOfficeId == null) {
            return null;
        }

        Collection<StaffData> allowedLoanOfficers = null;

        if (staffInSelectedOfficeOnly) {
            // only bring back loan officers in selected branch/office
            allowedLoanOfficers = this.staffReadPlatformService.retrieveAllLoanOfficersInOfficeById(selectedOfficeId);
        } else {
            // by default bring back all loan officers in selected
            // branch/office as well as loan officers in officer above
            // this office
            final boolean restrictToLoanOfficersOnly = true;
            allowedLoanOfficers = this.staffReadPlatformService.retrieveAllStaffInOfficeAndItsParentOfficeHierarchy(selectedOfficeId,
                    restrictToLoanOfficersOnly);
        }

        return allowedLoanOfficers;
    }

    @Override
    public Collection<OverdueLoanScheduleData> retrieveAllLoansWithOverdueInstallments(final Long penaltyWaitPeriod,
            final Boolean backdatePenalties) {
        final MusoniOverdueLoanScheduleMapper rm = new MusoniOverdueLoanScheduleMapper();

        final StringBuilder sqlBuilder = new StringBuilder(400);
        sqlBuilder.append("select ").append(rm.schema())
                .append(" where " + sqlGenerator.subDate(sqlGenerator.currentBusinessDate(), "?", "day") + " > ls.duedate ")
                .append(" and ls.completed_derived <> true and mc.charge_applies_to_enum =1 ")
                .append(" and ls.recalculated_interest_component <> true ")
                .append(" and mc.charge_time_enum = 9 and ml.loan_status_id = 300 ");

        if (backdatePenalties) {
            return this.jdbcTemplate.query(sqlBuilder.toString(), rm, penaltyWaitPeriod);
        }
        // Only apply for duedate = yesterday (so that we don't apply
        // penalties on the duedate itself)
        sqlBuilder.append(" and ls.duedate >= " + sqlGenerator.subDate(sqlGenerator.currentBusinessDate(), "(? + 1)", "day"));

        return this.jdbcTemplate.query(sqlBuilder.toString(), rm, penaltyWaitPeriod, penaltyWaitPeriod);
    }

    @Override
    public Collection<OverdueLoanScheduleData> retrieveAllOverdueInstallmentsForLoan(final Loan loan) {
        Collection<OverdueLoanScheduleData> list = new ArrayList<>();

        if (!loan.isOpen()) {
            return list;
        }

        Optional<Charge> optPenaltyCharge = loan.getLoanProduct().getCharges().stream()
                .filter((e) -> ChargeTimeType.OVERDUE_INSTALLMENT.getValue().equals(e.getChargeTimeType()) && e.isLoanCharge()).findFirst();

        if (optPenaltyCharge.isEmpty()) {
            return list;
        }
        final Charge penaltyCharge = optPenaltyCharge.get();

        final Long penaltyWaitPeriod = configurationDomainService.retrievePenaltyWaitPeriod();
        final boolean backdatePenalties = configurationDomainService.isBackdatePenaltiesEnabled();

        for (LoanRepaymentScheduleInstallment installment : loan.getRepaymentScheduleInstallments()) {
            if (installment.isObligationsMet() || installment.isRecalculatedInterestComponent()) {
                continue;
            }

            boolean isPenaltyDue = installment.isOverdueOn(DateUtils.getBusinessLocalDate().minusDays(penaltyWaitPeriod).plusDays(1));
            boolean isDueToday = installment.getDueDate().equals(DateUtils.getBusinessLocalDate().minusDays(penaltyWaitPeriod));

            if (isPenaltyDue) {
                if (!backdatePenalties && !isDueToday) {
                    continue;
                }

                list.add(new OverdueLoanScheduleData(loan.getId(), penaltyCharge.getId(),
                        DateUtils.DEFAULT_DATE_FORMATTER.format(installment.getDueDate()), penaltyCharge.getAmount(),
                        DateUtils.DEFAULT_DATE_FORMAT, Locale.ENGLISH.toLanguageTag(),
                        installment.getPrincipalOutstanding(loan.getCurrency()).getAmount(),
                        installment.getInterestOutstanding(loan.getCurrency()).getAmount(), installment.getInstallmentNumber()));
            }
        }
        return list;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Integer retriveLoanCounter(final Long groupId, final Integer loanType, Long productId) {
        final String sql = "Select MAX(l.loan_product_counter) from m_loan l where l.group_id = ?  and l.loan_type_enum = ? and l.product_id=?";
        return this.jdbcTemplate.queryForObject(sql, new Object[] { groupId, loanType, productId }, Integer.class);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Integer retriveLoanCounter(final Long clientId, Long productId) {
        final String sql = "Select MAX(l.loan_product_counter) from m_loan l where l.client_id = ? and l.product_id=?";
        return this.jdbcTemplate.queryForObject(sql, new Object[] { clientId, productId }, Integer.class);
    }

    @Override
    public Collection<DisbursementData> retrieveLoanDisbursementDetails(final Long loanId) {
        final LoanDisbursementDetailMapper rm = new LoanDisbursementDetailMapper(sqlGenerator);
        final String sql = "select " + rm.schema()
                + " where dd.loan_id=? and dd.is_reversed=false group by dd.id, lc.amount_waived_derived order by dd.expected_disburse_date,dd.disbursedon_date,dd.id";
        return this.jdbcTemplate.query(sql, rm, loanId); // NOSONAR
    }

    private static final class LoanDisbursementDetailMapper implements RowMapper<DisbursementData> {

        private final DatabaseSpecificSQLGenerator sqlGenerator;

        LoanDisbursementDetailMapper(DatabaseSpecificSQLGenerator sqlGenerator) {
            this.sqlGenerator = sqlGenerator;
        }

        public String schema() {
            return "dd.id as id,dd.expected_disburse_date as expectedDisbursementdate, dd.disbursedon_date as actualDisbursementdate,dd.principal as principal,dd.net_disbursal_amount as netDisbursalAmount,sum(lc.amount) chargeAmount, lc.amount_waived_derived waivedAmount, "
                    + sqlGenerator.groupConcat("lc.id") + " loanChargeId "
                    + "from m_loan l inner join m_loan_disbursement_detail dd on dd.loan_id = l.id left join m_loan_tranche_disbursement_charge tdc on tdc.disbursement_detail_id=dd.id "
                    + "left join m_loan_charge lc on  lc.id=tdc.loan_charge_id and lc.is_active=true";
        }

        @Override
        public DisbursementData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final LocalDate expectedDisbursementdate = JdbcSupport.getLocalDate(rs, "expectedDisbursementdate");
            final LocalDate actualDisbursementdate = JdbcSupport.getLocalDate(rs, "actualDisbursementdate");
            final BigDecimal principal = rs.getBigDecimal("principal");
            final String loanChargeId = rs.getString("loanChargeId");
            final BigDecimal netDisbursalAmount = rs.getBigDecimal("netDisbursalAmount");
            BigDecimal chargeAmount = rs.getBigDecimal("chargeAmount");
            final BigDecimal waivedAmount = rs.getBigDecimal("waivedAmount");
            if (chargeAmount != null && waivedAmount != null) {
                chargeAmount = chargeAmount.subtract(waivedAmount);
            }
            return new DisbursementData(id, expectedDisbursementdate, actualDisbursementdate, principal, netDisbursalAmount, loanChargeId,
                    chargeAmount, waivedAmount);
        }

    }

    @Override
    public DisbursementData retrieveLoanDisbursementDetail(Long loanId, Long disbursementId) {
        final LoanDisbursementDetailMapper rm = new LoanDisbursementDetailMapper(sqlGenerator);
        final String sql = "select " + rm.schema() + " where dd.loan_id=? and dd.id=? group by dd.id, lc.amount_waived_derived";
        return this.jdbcTemplate.queryForObject(sql, rm, loanId, disbursementId); // NOSONAR
    }

    @Override
    public LoanTransactionData retrieveRecoveryPaymentTemplate(Long loanId) {
        final Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId, true);
        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.RECOVERY_REPAYMENT);
        final Collection<PaymentTypeData> paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
        BigDecimal outstandingLoanBalance = null;
        final BigDecimal unrecognizedIncomePortion = null;
        return new LoanTransactionData(null, null, null, transactionType, null, null, null, loan.getTotalWrittenOff(),
                loan.getNetDisbursalAmount(), null, null, null, null, null, unrecognizedIncomePortion, paymentOptions, ExternalId.empty(),
                null, null, outstandingLoanBalance, false, loanId, loan.getExternalId());

    }

    @Override
    public LoanTransactionData retrieveLoanWriteoffTemplate(final Long loanId) {

        final LoanAccountData loan = this.retrieveOne(loanId);
        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.WRITEOFF);
        final BigDecimal totalOutstanding = loan.getSummary() != null ? loan.getSummary().getTotalOutstanding() : null;
        final List<CodeValueData> writeOffReasonOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(LoanApiConstants.WRITEOFFREASONS));
        LoanTransactionData loanTransactionData = new LoanTransactionData(null, null, null, transactionType, null, loan.getCurrency(),
                DateUtils.getBusinessLocalDate(), totalOutstanding, loan.getNetDisbursalAmount(), null, null, null, null, null,
                ExternalId.empty(), null, null, null, null, false, loanId, loan.getExternalId());
        loanTransactionData.setWriteOffReasonOptions(writeOffReasonOptions);
        return loanTransactionData;
    }

    @Override
    public LoanTransactionData retrieveLoanChargeOffTemplate(final Long loanId) {

        final LoanAccountData loan = this.retrieveOne(loanId);
        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.CHARGE_OFF);
        final BigDecimal totalOutstanding = loan.getSummary() != null ? loan.getSummary().getTotalOutstanding() : null;
        final BigDecimal totalPrincipalOutstanding = loan.getSummary() != null ? loan.getSummary().getPrincipalOutstanding() : null;
        final BigDecimal totalInterestOutstanding = loan.getSummary() != null ? loan.getSummary().getInterestOutstanding() : null;
        final BigDecimal totalFeeOutstanding = loan.getSummary() != null ? loan.getSummary().getFeeChargesOutstanding() : null;
        final BigDecimal totalPenaltyOutstanding = loan.getSummary() != null ? loan.getSummary().getPenaltyChargesOutstanding() : null;
        final List<CodeValueData> chargeOffReasonOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(LoanApiConstants.CHARGE_OFF_REASONS));
        LoanTransactionData loanTransactionData = new LoanTransactionData(null, null, null, transactionType, null, loan.getCurrency(),
                DateUtils.getBusinessLocalDate(), totalOutstanding, loan.getNetDisbursalAmount(), totalPrincipalOutstanding,
                totalInterestOutstanding, totalFeeOutstanding, totalPenaltyOutstanding, null, ExternalId.empty(), null, null, null, null,
                false, loanId, loan.getExternalId());
        loanTransactionData.setChargeOffReasonOptions(chargeOffReasonOptions);
        return loanTransactionData;
    }

    @Override
    public Collection<Long> fetchLoansForInterestRecalculation() {
        final String sql = """
                SELECT l.id
                FROM m_loan l
                INNER JOIN m_loan_repayment_schedule mr ON mr.loan_id = l.id
                LEFT JOIN m_loan_disbursement_detail dd ON dd.loan_id=l.id AND dd.disbursedon_date IS NULL
                -- for past due interest recalculation
                LEFT JOIN m_loan_recalculation_details rcd ON rcd.loan_id = l.id
                -- For Floating rate changes
                LEFT JOIN m_product_loan_floating_rates pfr
                    ON l.product_id = pfr.loan_product_id AND l.is_floating_interest_rate = TRUE
                LEFT JOIN m_floating_rates fr ON pfr.floating_rates_id = fr.id
                LEFT JOIN m_floating_rates_periods frp ON fr.id = frp.floating_rates_id
                LEFT JOIN m_loan_reschedule_request lrr ON lrr.loan_id = l.id
                -- this is to identify the applicable rates when base rate is changed
                LEFT JOIN m_floating_rates bfr ON bfr.is_base_lending_rate = TRUE
                LEFT JOIN m_floating_rates_periods bfrp ON bfr.id = bfrp.floating_rates_id AND bfrp.created_date >= ?
                WHERE l.loan_status_id = ?
                  AND l.is_npa = FALSE
                  AND l.is_charged_off = FALSE
                  AND dd.is_reversed = FALSE
                  AND (
                        (l.interest_recalculation_enabled = TRUE
                            AND (l.interest_recalcualated_on IS NULL OR l.interest_recalcualated_on <> ?)
                            AND ((mr.completed_derived IS FALSE AND mr.duedate < ?) OR dd.expected_disburse_date < ?)
                            AND rcd.disallow_interest_calc_on_past_due = FALSE)
                       OR
                        -- float rate changes
                        (fr.is_active = TRUE
                            AND frp.is_active = TRUE
                            AND (frp.created_date >= ?
                                 OR (bfrp.id IS NOT NULL
                                     AND frp.is_differential_to_base_lending_rate = TRUE
                                     AND frp.from_date >= bfrp.from_date))
                            AND lrr.loan_id IS NULL)
                  )
                GROUP BY l.id
                """;
        try {
            LocalDate currentdate = DateUtils.getBusinessLocalDate();
            // will look only for yesterday modified rates
            LocalDate yesterday = DateUtils.getBusinessLocalDate().minusDays(1);
            return this.jdbcTemplate.queryForList(sql, Long.class, yesterday, LoanStatus.ACTIVE.getValue(), currentdate, currentdate,
                    currentdate, yesterday);
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Long> fetchLoansForInterestRecalculation(Integer pageSize, Long maxLoanIdInList, String officeHierarchy) {
        LocalDate currentdate = DateUtils.getBusinessLocalDate();
        // will look only for yesterday modified rates
        LocalDate yesterday = DateUtils.getBusinessLocalDate().minusDays(1);
        final String sql = """
                SELECT l.id
                FROM m_loan l
                LEFT JOIN m_client c ON c.id = l.client_id
                LEFT JOIN m_office o ON c.office_id = o.id
                INNER JOIN m_loan_repayment_schedule rps ON rps.loan_id = l.id
                LEFT JOIN m_loan_disbursement_detail dd
                    ON dd.loan_id=l.id AND dd.disbursedon_date IS NULL AND dd.is_reversed = FALSE
                -- for past due interest recalculation
                LEFT JOIN m_loan_recalculation_details rcd ON rcd.loan_id = l.id
                -- For Floating rate changes
                LEFT JOIN m_product_loan_floating_rates pfr
                    ON l.product_id = pfr.loan_product_id AND l.is_floating_interest_rate = TRUE
                LEFT JOIN m_floating_rates fr ON pfr.floating_rates_id = fr.id
                LEFT JOIN m_floating_rates_periods frp ON fr.id = frp.floating_rates_id
                LEFT JOIN m_loan_reschedule_request lrr ON lrr.loan_id = l.id
                -- this is to identify the applicable rates when base rate is changed
                LEFT JOIN m_floating_rates bfr ON bfr.is_base_lending_rate = TRUE
                LEFT JOIN m_floating_rates_periods bfrp ON bfr.id = bfrp.floating_rates_id AND bfrp.created_date >= ?
                WHERE l.loan_status_id = ?
                    AND l.is_npa = FALSE
                    AND l.is_charged_off = FALSE
                    AND (
                         (l.interest_recalculation_enabled = TRUE
                             AND (l.interest_recalcualated_on IS NULL OR l.interest_recalcualated_on <> ?)
                             AND ((rps.completed_derived IS FALSE AND rps.duedate < ?) OR dd.expected_disburse_date < ?)
                             AND rcd.disallow_interest_calc_on_past_due = FALSE)
                        OR
                         (fr.is_active = TRUE
                             AND frp.is_active = TRUE
                             AND (frp.created_date >= ?
                                  OR (bfrp.id IS NOT NULL
                                      AND frp.is_differential_to_base_lending_rate = TRUE
                                      AND frp.from_date >= bfrp.from_date))
                             AND lrr.loan_id IS NULL)
                    )
                    AND l.id >= ?
                    AND o.hierarchy like ?
                GROUP BY l.id
                LIMIT ?
                """;
        try {
            return Collections.synchronizedList(this.jdbcTemplate.queryForList(sql, Long.class, yesterday, LoanStatus.ACTIVE.getValue(),
                    currentdate, currentdate, currentdate, yesterday, maxLoanIdInList, officeHierarchy, pageSize));
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean isGuaranteeRequired(final Long loanId) {
        final String sql = "select pl.hold_guarantee_funds from m_loan ml inner join m_product_loan pl on pl.id = ml.product_id where ml.id=?";
        return TRUE.equals(this.jdbcTemplate.queryForObject(sql, Boolean.class, loanId));
    }

    @Override
    public LocalDate retrieveMinimumDateOfRepaymentTransaction(Long loanId) {
        return this.jdbcTemplate.queryForObject(
                "select min(transaction_date) from m_loan_transaction where loan_id=? and transaction_type_enum=2", LocalDate.class,
                loanId);
    }

    @Override
    public PaidInAdvanceData retrieveTotalPaidInAdvance(Long loanId) {
        try {
            final String sql = "  select (SUM(COALESCE(mr.principal_completed_derived, 0))"
                    + " + SUM(COALESCE(mr.interest_completed_derived, 0)) " + " + SUM(COALESCE(mr.fee_charges_completed_derived, 0)) "
                    + " + SUM(COALESCE(mr.penalty_charges_completed_derived, 0))) as total_in_advance_derived "
                    + " from m_loan ml INNER JOIN m_loan_repayment_schedule mr on mr.loan_id = ml.id "
                    + " where ml.id=? and  mr.duedate >= " + sqlGenerator.currentBusinessDate() + " group by ml.id having "
                    + " (SUM(COALESCE(mr.principal_completed_derived, 0))  " + " + SUM(COALESCE(mr.interest_completed_derived, 0)) "
                    + " + SUM(COALESCE(mr.fee_charges_completed_derived, 0)) "
                    + "+  SUM(COALESCE(mr.penalty_charges_completed_derived, 0))) > 0";
            BigDecimal bigDecimal = this.jdbcTemplate.queryForObject(sql, BigDecimal.class, loanId); // NOSONAR
            return new PaidInAdvanceData(bigDecimal);
        } catch (DataAccessException e) {
            return new PaidInAdvanceData(new BigDecimal(0));
        }
    }

    @Override
    public LoanTransactionData retrieveRefundByCashTemplate(Long loanId) {
        this.context.authenticatedUser();

        final Collection<PaymentTypeData> paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
        final Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId, true);
        return retrieveRefundTemplate(loanId, LoanTransactionType.REFUND_FOR_ACTIVE_LOAN, paymentOptions, loan.getCurrency(),
                retrieveTotalPaidInAdvance(loan.getId()).getPaidInAdvance(), loan.getNetDisbursalAmount(), loan.getExternalId());
    }

    @Override
    public LoanTransactionData retrieveCreditBalanceRefundTemplate(Long loanId) {
        this.context.authenticatedUser();

        final Collection<PaymentTypeData> paymentOptions = null;
        final BigDecimal netDisbursal = null;
        final Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId, true);
        return retrieveRefundTemplate(loanId, LoanTransactionType.CREDIT_BALANCE_REFUND, paymentOptions, loan.getCurrency(),
                loan.getTotalOverpaid(), netDisbursal, loan.getExternalId());

    }

    private LoanTransactionData retrieveRefundTemplate(Long loanId, LoanTransactionType loanTransactionType,
            Collection<PaymentTypeData> paymentOptions, MonetaryCurrency currency, BigDecimal transactionAmount, BigDecimal netDisbursal,
            ExternalId externalLoanId) {

        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(currency);

        final CurrencyData currencyData = applicationCurrency.toData();

        final LocalDate currentDate = LocalDate.now(DateUtils.getDateTimeZoneOfTenant());

        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(loanTransactionType);
        return new LoanTransactionData(null, null, null, transactionType, null, currencyData, currentDate, transactionAmount, null,
                netDisbursal, null, null, null, null, null, paymentOptions, ExternalId.empty(), null, null, null, false, loanId,
                externalLoanId);
    }

    @Override
    public Collection<InterestRatePeriodData> retrieveLoanInterestRatePeriodData(LoanAccountData loanData) {
        this.context.authenticatedUser();

        if (loanData.isLoanProductLinkedToFloatingRate()) {
            final Collection<InterestRatePeriodData> intRatePeriodData = new ArrayList<>();
            final Collection<InterestRatePeriodData> intRates = this.floatingRatesReadPlatformService
                    .retrieveInterestRatePeriods(loanData.getLoanProductId());
            for (final InterestRatePeriodData rate : intRates) {
                if (loanData.getTimeline() == null) {
                    continue;
                }
                boolean isAfterDisbursement = DateUtils.isAfter(rate.getFromDate(), loanData.getTimeline().getDisbursementDate());
                if (isAfterDisbursement && loanData.isFloatingInterestRate()) {
                    updateInterestRatePeriodData(rate, loanData);
                    intRatePeriodData.add(rate);
                } else if (!isAfterDisbursement) {
                    updateInterestRatePeriodData(rate, loanData);
                    intRatePeriodData.add(rate);
                    break;
                }
            }

            return intRatePeriodData;
        }
        return null;
    }

    private void updateInterestRatePeriodData(InterestRatePeriodData rate, LoanAccountData loan) {
        LoanProductData loanProductData = loanProductReadPlatformService.retrieveLoanProductFloatingDetails(loan.getLoanProductId());
        rate.setLoanProductDifferentialInterestRate(loanProductData.getInterestRateDifferential());
        rate.setLoanDifferentialInterestRate(loan.getInterestRateDifferential());

        BigDecimal effectiveInterestRate = BigDecimal.ZERO;
        effectiveInterestRate = effectiveInterestRate.add(rate.getLoanDifferentialInterestRate());
        effectiveInterestRate = effectiveInterestRate.add(rate.getLoanProductDifferentialInterestRate());
        effectiveInterestRate = effectiveInterestRate.add(rate.getInterestRate());
        if (rate.getBlrInterestRate() != null && rate.isDifferentialToBLR()) {
            effectiveInterestRate = effectiveInterestRate.add(rate.getBlrInterestRate());
        }
        rate.setEffectiveInterestRate(effectiveInterestRate);

        if (loan.getTimeline() != null && DateUtils.isBefore(rate.getFromDate(), loan.getTimeline().getDisbursementDate())) {
            rate.setFromDate(loan.getTimeline().getDisbursementDate());
        }
    }

    @Override
    public Collection<Long> retrieveLoanIdsWithPendingIncomePostingTransactions() {
        LocalDate currentdate = DateUtils.getBusinessLocalDate();
        StringBuilder sqlBuilder = new StringBuilder().append(" select distinct loan.id from m_loan as loan ").append(
                " inner join m_loan_recalculation_details as recdet on (recdet.loan_id = loan.id and recdet.is_compounding_to_be_posted_as_transaction is not null and recdet.is_compounding_to_be_posted_as_transaction = true) ")
                .append(" inner join m_loan_repayment_schedule as repsch on repsch.loan_id = loan.id ")
                .append(" inner join m_loan_interest_recalculation_additional_details as adddet on adddet.loan_repayment_schedule_id = repsch.id ")
                .append(" left join m_loan_transaction as trans on (trans.is_reversed <> true and trans.transaction_type_enum = 19 and trans.loan_id = loan.id and trans.transaction_date = adddet.effective_date) ")
                .append(" where loan.loan_status_id = 300 ").append(" and loan.is_npa = false and loan.is_charged_off = false ")
                .append(" and adddet.effective_date is not null ").append(" and trans.transaction_date is null ")
                .append(" and adddet.effective_date < ? ");
        try {
            return this.jdbcTemplate.queryForList(sqlBuilder.toString(), Long.class, new Object[] { currentdate });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public LoanTransactionData retrieveLoanForeclosureTemplate(final Long loanId, final LocalDate transactionDate) {
        this.context.authenticatedUser();

        final Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId, true);
        loanForeclosureValidator.validateForForeclosure(loan, transactionDate);
        final MonetaryCurrency currency = loan.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(currency);

        final CurrencyData currencyData = applicationCurrency.toData();

        final LocalDate earliestUnpaidInstallmentDate = DateUtils.getBusinessLocalDate();

        final LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment = loanBalanceService.fetchLoanForeclosureDetail(loan,
                transactionDate);
        BigDecimal unrecognizedIncomePortion = null;
        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.REPAYMENT);
        final Collection<PaymentTypeData> paymentTypeOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
        final BigDecimal outstandingLoanBalance = loanRepaymentScheduleInstallment.getPrincipalOutstanding(currency).getAmount();
        final Boolean isManuallyReversed = false;

        final Money outStandingAmount = loanRepaymentScheduleInstallment.getTotalOutstanding(currency);

        return new LoanTransactionData(null, null, null, transactionType, null, currencyData, earliestUnpaidInstallmentDate,
                outStandingAmount.getAmount(), loan.getNetDisbursalAmount(),
                loanRepaymentScheduleInstallment.getPrincipalOutstanding(currency).getAmount(),
                loanRepaymentScheduleInstallment.getInterestOutstanding(currency).getAmount(),
                loanRepaymentScheduleInstallment.getFeeChargesOutstanding(currency).getAmount(),
                loanRepaymentScheduleInstallment.getPenaltyChargesOutstanding(currency).getAmount(), null, unrecognizedIncomePortion,
                paymentTypeOptions, ExternalId.empty(), null, null, outstandingLoanBalance, isManuallyReversed, loanId,
                loan.getExternalId());
    }

    private static final class CurrencyMapper implements RowMapper<CurrencyData> {

        @Override
        public CurrencyData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            return new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf, currencyDisplaySymbol, currencyNameCode);
        }

    }

    private static final class RepaymentTransactionTemplateMapper implements RowMapper<LoanTransactionData> {

        private final DatabaseSpecificSQLGenerator sqlGenerator;
        private final CurrencyMapper currencyMapper = new CurrencyMapper();

        RepaymentTransactionTemplateMapper(DatabaseSpecificSQLGenerator sqlGenerator) {
            this.sqlGenerator = sqlGenerator;
        }

        public String schema() {
            // TODO: investigate whether it can be refactored to be more efficient
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("(CASE ");
            sqlBuilder.append(
                    "WHEN (select max(tr.transaction_date) as transaction_date from m_loan_transaction tr where tr.loan_id = l.id AND tr.transaction_type_enum in (?,?) AND tr.is_reversed = false) > ls.dueDate ");
            sqlBuilder.append(
                    "THEN (select max(tr.transaction_date) as transaction_date from m_loan_transaction tr where tr.loan_id = l.id AND tr.transaction_type_enum in (?,?) AND tr.is_reversed = false) ");
            sqlBuilder.append("ELSE ls.dueDate END) as transactionDate, ");
            sqlBuilder.append(
                    "ls.principal_amount - coalesce(ls.principal_writtenoff_derived, 0) - coalesce(ls.principal_completed_derived, 0) as principalDue, ");
            sqlBuilder.append(
                    "ls.interest_amount - coalesce(ls.interest_completed_derived, 0) - coalesce(ls.interest_waived_derived, 0) - coalesce(ls.interest_writtenoff_derived, 0) as interestDue, ");
            sqlBuilder.append(
                    "ls.fee_charges_amount - coalesce(ls.fee_charges_completed_derived, 0) - coalesce(ls.fee_charges_writtenoff_derived, 0) - coalesce(ls.fee_charges_waived_derived, 0) as feeDue, ");
            sqlBuilder.append(
                    "ls.penalty_charges_amount - coalesce(ls.penalty_charges_completed_derived, 0) - coalesce(ls.penalty_charges_writtenoff_derived, 0) - coalesce(ls.penalty_charges_waived_derived, 0) as penaltyDue, ");
            sqlBuilder.append(
                    "l.currency_code as currencyCode, l.currency_digits as currencyDigits, l.currency_multiplesof as inMultiplesOf, l.net_disbursal_amount as netDisbursalAmount, ");
            sqlBuilder.append("rc." + sqlGenerator.escape("name")
                    + " as currencyName, rc.display_symbol as currencyDisplaySymbol, rc.internationalized_name_code as currencyNameCode ");
            sqlBuilder.append("FROM m_loan l ");
            sqlBuilder.append("JOIN m_currency rc on rc." + sqlGenerator.escape("code") + " = l.currency_code ");
            sqlBuilder.append("JOIN m_loan_repayment_schedule ls ON ls.loan_id = l.id AND ls.completed_derived = false ");
            sqlBuilder.append(
                    "JOIN((SELECT ls.loan_id, ls.duedate as datedue FROM m_loan_repayment_schedule ls WHERE ls.loan_id = ? and ls.completed_derived = false ORDER BY ls.duedate LIMIT 1)) asq on asq.loan_id = ls.loan_id ");
            sqlBuilder.append("AND asq.datedue = ls.duedate ");
            sqlBuilder.append("WHERE l.id = ? LIMIT 1");
            return sqlBuilder.toString();
        }

        @Override
        public LoanTransactionData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.REPAYMENT);
            final CurrencyData currencyData = this.currencyMapper.mapRow(rs, rowNum);
            final LocalDate date = JdbcSupport.getLocalDate(rs, "transactionDate");
            final BigDecimal principalPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalDue");
            final BigDecimal interestDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestDue");
            final BigDecimal feeDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeDue");
            final BigDecimal penaltyDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyDue");
            final BigDecimal totalDue = principalPortion.add(interestDue).add(feeDue).add(penaltyDue);
            final BigDecimal outstandingLoanBalance = null;
            final BigDecimal unrecognizedIncomePortion = null;
            final BigDecimal overPaymentPortion = null;
            final BigDecimal netDisbursalAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "netDisbursalAmount");
            final Long id = null;
            final Long loanId = null;
            final Long officeId = null;
            final String officeName = null;
            boolean manuallyReversed = false;
            final PaymentDetailData paymentDetailData = null;
            final AccountTransferData transfer = null;
            final BigDecimal fixedEmiAmount = null;
            return new LoanTransactionData(id, officeId, officeName, transactionType, paymentDetailData, currencyData, date, totalDue,
                    netDisbursalAmount, principalPortion, interestDue, feeDue, penaltyDue, overPaymentPortion, ExternalId.empty(), transfer,
                    fixedEmiAmount, outstandingLoanBalance, unrecognizedIncomePortion, manuallyReversed, loanId, ExternalId.empty());
        }

    }

    @Override
    public Long retrieveLoanIdByAccountNumber(String loanAccountNumber) {
        try {
            return this.jdbcTemplate.queryForObject("select l.id from m_loan l where l.account_no = ?", Long.class, loanAccountNumber);

        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public String retrieveAccountNumberByAccountId(Long accountId) {
        try {
            final String sql = "select loan.account_no from m_loan loan where loan.id = ?";
            return this.jdbcTemplate.queryForObject(sql, String.class, accountId);
        } catch (final EmptyResultDataAccessException e) {
            throw new LoanNotFoundException(accountId, e);
        }
    }

    @Override
    public Integer retrieveNumberOfActiveLoans() {
        final String sql = "select count(*) from m_loan";
        return this.jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public Long retrieveLoanTransactionIdByExternalId(ExternalId externalId) {
        return loanTransactionRepository.findIdByExternalId(externalId);
    }

    @Override
    public Long retrieveLoanIdByExternalId(ExternalId externalId) {
        return loanRepositoryWrapper.findIdByExternalId(externalId);
    }

    @Override
    public List<Long> retrieveLoanIdsByExternalIds(List<ExternalId> externalIds) {
        return loanRepositoryWrapper.findIdByExternalIds(externalIds);
    }

    @Override
    public boolean existsByLoanId(Long loanId) {
        return loanRepositoryWrapper.existsByLoanId(loanId);
    }

    @Override
    public LoanTransactionData retrieveManualInterestRefundTemplate(final Long loanId, final Long targetTransactionId) {
        final Loan loan = loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);
        final LoanTransaction targetTxn = loan.getLoanTransaction(txn -> txn.getId() != null && txn.getId().equals(targetTransactionId));
        if (targetTxn == null) {
            throw new LoanTransactionNotFoundException(targetTransactionId);
        }
        if (!(targetTxn.isMerchantIssuedRefund() || targetTxn.isPayoutRefund())) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.transaction.not.refund.type", "Only for refund transactions");
        }
        if (targetTxn.isReversed()) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.transaction.reversed", "Refund transaction is reversed");
        }
        final boolean alreadyExists = loan.getLoanTransactions().stream().anyMatch(txn -> txn.isInterestRefund() && !txn
                .getLoanTransactionRelations(rel -> rel.getToTransaction() != null && rel.getToTransaction().equals(targetTxn)).isEmpty());
        if (alreadyExists) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.transaction.interest.refund.already.exists",
                    "Interest Refund already exists for this refund");
        }

        final InterestRefundService interestRefundService = interestRefundServiceDelegate.lookupInterestRefundService(loan);
        final Money totalInterest = interestRefundService.totalInterestByTransactions(null, loan.getId(), targetTxn.getTransactionDate(),
                List.of(), loan.getLoanTransactions().stream().map(AbstractPersistableCustom::getId).toList());
        final Money newTotalInterest = interestRefundService.totalInterestByTransactions(null, loan.getId(), targetTxn.getTransactionDate(),
                List.of(targetTxn), loan.getLoanTransactions().stream().map(AbstractPersistableCustom::getId).toList());
        final BigDecimal interestRefundAmount = totalInterest.minus(newTotalInterest).getAmount();
        final Collection<PaymentTypeData> paymentTypeOptions = paymentTypeReadPlatformService.retrieveAllPaymentTypes();
        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.INTEREST_REFUND);

        return LoanTransactionData.loanTransactionDataForCreditTemplate(transactionType, targetTxn.getTransactionDate(),
                interestRefundAmount, paymentTypeOptions, loan.getCurrency().toData());
    }

    @Override
    public Long getResolvedLoanId(final ExternalId loanExternalId) {
        loanExternalId.throwExceptionIfEmpty();

        final Long resolvedLoanId = retrieveLoanIdByExternalId(loanExternalId);

        if (resolvedLoanId == null) {
            throw new LoanNotFoundException(loanExternalId);
        }

        return resolvedLoanId;
    }

    private LoanTransaction deriveDefaultInterestWaiverTransaction(final Loan loan) {
        final Money totalInterestOutstanding = loan.getTotalInterestOutstandingOnLoan();
        Money possibleInterestToWaive = totalInterestOutstanding.copy();
        LocalDate transactionDate = DateUtils.getBusinessLocalDate();

        if (totalInterestOutstanding.isGreaterThanZero()) {
            // find earliest known instance of overdue interest and default to
            // that
            List<LoanRepaymentScheduleInstallment> installments = loan.getRepaymentScheduleInstallments();
            for (final LoanRepaymentScheduleInstallment scheduledRepayment : installments) {

                final Money outstandingForPeriod = scheduledRepayment.getInterestOutstanding(loan.getCurrency());
                if (scheduledRepayment.isOverdueOn(DateUtils.getBusinessLocalDate()) && scheduledRepayment.isNotFullyPaidOff()
                        && outstandingForPeriod.isGreaterThanZero()) {
                    transactionDate = scheduledRepayment.getDueDate();
                    possibleInterestToWaive = outstandingForPeriod;
                    break;
                }
            }
        }

        return LoanTransaction.waiver(loan.getOffice(), loan, possibleInterestToWaive, transactionDate, possibleInterestToWaive,
                possibleInterestToWaive.zero(), ExternalId.empty());
    }

}
