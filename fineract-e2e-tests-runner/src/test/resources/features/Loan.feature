@LoanFeature
Feature: Loan

  @TestRailId:C16 @Smoke
  Scenario: Loan creation functionality in Fineract
    When Admin sets the business date to the actual date
    When Admin creates a client with random data
    When Admin creates a new Loan

  @TestRailId:C17
  Scenario: Loan creation functionality in Fineract
    When Admin sets the business date to "1 July 2022"
    When Admin creates a client with random data
    And Admin successfully creates a new customised Loan submitted on date: "1 July 2022", with Principal: "5000", a loanTermFrequency: 24 months, and numberOfRepayments: 24

  @TestRailId:C42
  Scenario: As a user I would like to see that the loan is not created if the loan submission date is after the business date
    When Admin sets the business date to "25 June 2022"
    When Admin creates a client with random data
    Then Admin fails to create a new customised Loan submitted on date: "1 July 2022", with Principal: "5000", a loanTermFrequency: 24 months, and numberOfRepayments: 24

  @TestRailId:C43
  Scenario: As a user I would like to see that the loan is created if the loan submission date is equal to business date
    When Admin sets the business date to "1 July 2022"
    When Admin creates a client with random data
    And Admin successfully creates a new customised Loan submitted on date: "1 July 2022", with Principal: "5000", a loanTermFrequency: 24 months, and numberOfRepayments: 24

  @TestRailId:C46
  Scenario: As a user I would like to see that the loan is approved at the business date
    When Admin sets the business date to "1 July 2022"
    When Admin creates a client with random data
    And Admin successfully creates a new customised Loan submitted on date: "1 July 2022", with Principal: "5000", a loanTermFrequency: 24 months, and numberOfRepayments: 24
    And Admin successfully approves the loan on "1 July 2022" with "5000" amount and expected disbursement date on "2 July 2022"

  @TestRailId:C30 @single
  Scenario: As a user I would like to see that the loan is cannot be approved with future approval date
    When Admin sets the business date to "1 July 2022"
    When Admin creates a client with random data
    And Admin successfully creates a new customised Loan submitted on date: "1 July 2022", with Principal: "5000", a loanTermFrequency: 24 months, and numberOfRepayments: 24
    Then Admin fails to approve the loan on "2 July 2022" with "5000" amount and expected disbursement date on "2 July 2022" because of wrong date

  @TestRailId:C47 @multi
  Scenario: As a user I would like to see that the loan can be disbursed at the business date
    When Admin sets the business date to "1 July 2022"
    When Admin creates a client with random data
    And Admin successfully creates a new customised Loan submitted on date: "1 July 2022", with Principal: "5000", a loanTermFrequency: 24 months, and numberOfRepayments: 24
    And Admin successfully approves the loan on "1 July 2022" with "5000" amount and expected disbursement date on "2 July 2022"
    When Admin successfully disburse the loan on "1 July 2022" with "5000" EUR transaction amount

  @TestRailId:C31
  Scenario: As a user I would like to see that the loan is cannot be disbursed with future disburse date
    When Admin sets the business date to "1 July 2022"
    When Admin creates a client with random data
    And Admin successfully creates a new customised Loan submitted on date: "1 July 2022", with Principal: "5000", a loanTermFrequency: 24 months, and numberOfRepayments: 24
    And Admin successfully approves the loan on "1 July 2022" with "5000" amount and expected disbursement date on "2 July 2022"
    Then Admin fails to disburse the loan on "2 July 2022" with "5000" EUR transaction amount because of wrong date

  @TestRailId:C64
  Scenario: As a user I would like to see that 50% over applied amount can be approved and disbursed on loan correctly
    When Admin sets the business date to "1 September 2022"
    When Admin creates a client with random data
    When Admin successfully creates a new customised Loan submitted on date: "1 September 2022", with Principal: "1000", a loanTermFrequency: 3 months, and numberOfRepayments: 3
    And Admin successfully approves the loan on "1 September 2022" with "1500" amount and expected disbursement date on "1 September 2022"
    When Admin successfully disburse the loan on "1 September 2022" with "1500" EUR transaction amount

  @TestRailId:C65
  Scenario: As a user I would like to see that 50% over applied amount can be approved but more than 50% cannot be disbursed on loan
    When Admin sets the business date to "1 September 2022"
    When Admin creates a client with random data
    When Admin successfully creates a new customised Loan submitted on date: "1 September 2022", with Principal: "1000", a loanTermFrequency: 3 months, and numberOfRepayments: 3
    And Admin successfully approves the loan on "1 September 2022" with "1500" amount and expected disbursement date on "1 September 2022"
    Then Admin fails to disburse the loan on "1 September 2022" with "1501" EUR transaction amount because of wrong amount

  @TestRailId:C66
  Scenario: As a user I would like to see that more than 50% over applied amount can not be approved on loan
    When Admin sets the business date to "1 September 2022"
    When Admin creates a client with random data
    When Admin successfully creates a new customised Loan submitted on date: "1 September 2022", with Principal: "1000", a loanTermFrequency: 3 months, and numberOfRepayments: 3
    Then Admin fails to approve the loan on "1 September 2022" with "1501" amount and expected disbursement date on "1 September 2022" because of wrong amount

  @TestRailId:C2769
  Scenario: As a user I would like to see that more than 50% over applied amount in total can not be disbursed on loan
    When Admin sets the business date to "1 September 2022"
    When Admin creates a client with random data
    When Admin successfully creates a new customised Loan submitted on date: "1 September 2022", with Principal: "1000", a loanTermFrequency: 3 months, and numberOfRepayments: 3
    And Admin successfully approves the loan on "1 September 2022" with "1500" amount and expected disbursement date on "1 September 2022"
    And Admin successfully disburse the loan on "1 September 2022" with "1400" EUR transaction amount
    Then Admin fails to disburse the loan on "1 September 2022" with "101" EUR transaction amount because of wrong amount

  @TestRailId:C3767
  Scenario: Verify disbursed amount exceeds approved over applied amount for progressive loan with percentage overAppliedCalculationType
    When Admin sets the business date to "1 September 2022"
    When Admin creates a client with random data
    When Admin successfully creates a new customised Loan submitted on date: "1 September 2022", with Principal: "1000", a loanTermFrequency: 3 months, and numberOfRepayments: 3
    And Admin successfully approves the loan on "1 September 2022" with "1300" amount and expected disbursement date on "1 September 2022"
    And Admin successfully disburse the loan on "1 September 2022" with "1200" EUR transaction amount
    Then Admin fails to disburse the loan on "1 September 2022" with "301" EUR transaction amount because of wrong amount

    When Loan Pay-off is made on "1 September 2022"
    Then Loan's all installments have obligations met

  @TestRailId:C3768
  Scenario: Verify approved amount exceeds approved over applied amount for progressive loan with flat overAppliedCalculationType
    When Admin sets the business date to "1 January 2024"
    And Admin creates a client with random data
    And Admin creates a fully customized loan with the following data:
      | LoanProduct                                                                                  | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_RECALC_EMI_360_30_APPROVED_OVER_APPLIED_FLAT_CAPITALIZED_INCOME | 01 January 2024   | 1000           | 7                      | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    Then Admin fails to approve the loan on "1 January 2024" with "2001" amount and expected disbursement date on "1 January 2024" because of wrong amount

    And Admin successfully rejects the loan on "1 January 2024"
    Then Loan status will be "REJECTED"

  @TestRailId:C3769
  Scenario: Verify disbursed amount exceeds approved over applied amount for progressive loan with flat overAppliedCalculationType
    When Admin sets the business date to "1 January 2024"
    And Admin creates a client with random data
    And Admin creates a fully customized loan with the following data:
      | LoanProduct                                                   | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_CUSTOM_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 January 2024   | 1000           | 7                      | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "1 January 2024" with "9000" amount and expected disbursement date on "1 January 2024"
    And Admin successfully disburse the loan on "1 January 2024" with "9900" EUR transaction amount
    Then Admin fails to disburse the loan on "1 January 2024" with "1200" EUR transaction amount because of wrong amount

    When Loan Pay-off is made on "1 January 2024"
    Then Loan's all installments have obligations met

  @TestRailId:C3895
  Scenario: Verify disbursed amount approved over applied amount for progressive loan that expects tranches with percentage overAppliedCalculationType - UC1
    When Admin sets the business date to "1 January 2024"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with disbursement details and following data:
      | LoanProduct                                                                               | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            | 1st_tranche_disb_expected_date |1st_tranche_disb_principal |
      | LP2_PROGRESSIVE_ADV_PYMNT_INTEREST_RECALC_360_30_MULTIDISB_OVER_APPLIED_EXPECTED_TRANCHES | 01 January 2024   | 1000           | 7                      | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION | 01 January 2024                | 1000.0                    |
    And Admin successfully approves the loan on "1 January 2024" with "1200" amount and expected disbursement date on "1 January 2024"
    Then Loan status will be "APPROVED"
    Then Loan Tranche Details tab has the following data:
      | Expected Disbursement On | Disbursed On    | Principal   | Net Disbursal Amount |
      | 01 January 2024          |                 | 1000.0      |                      |
    When Admin sets the business date to "2 January 2024"
    And Admin successfully add disbursement detail to the loan on "5 January 2024" with 200 EUR transaction amount
    Then Loan Tranche Details tab has the following data:
      | Expected Disbursement On | Disbursed On    | Principal   | Net Disbursal Amount |
      | 01 January 2024          |                 | 1000.0      |                      |
      | 05 January 2024          |                 | 200.0       | 1200.0               |
    And Admin checks available disbursement amount 0.0 EUR
    Then Admin fails to disburse the loan on "2 January 2024" with "1600" EUR transaction amount because of wrong amount
    And Admin successfully disburse the loan on "2 January 2024" with "1500" EUR transaction amount
    Then Loan status will be "ACTIVE"
    And Admin checks available disbursement amount 0.0 EUR
    Then Loan Tranche Details tab has the following data:
      | Expected Disbursement On | Disbursed On    | Principal   | Net Disbursal Amount |
      | 01 January 2024          | 02 January 2024 | 1500.0      |                      |
      | 05 January 2024          |                 | 200.0       | 1200.0                |

    When Loan Pay-off is made on "2 January 2024"
    Then Loan's all installments have obligations met

  @TestRailId:C3896
  Scenario: Verify disbursed amount approved over applied amount for progressive loan that expects tranches with percentage overAppliedCalculationType - UC2
    When Admin sets the business date to "1 January 2024"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with disbursement details and following data:
      | LoanProduct                                                                               | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            | 1st_tranche_disb_expected_date |1st_tranche_disb_principal |
      | LP2_PROGRESSIVE_ADV_PYMNT_INTEREST_RECALC_360_30_MULTIDISB_OVER_APPLIED_EXPECTED_TRANCHES | 01 January 2024   | 1000           | 7                      | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION | 01 January 2024                | 1000.0                    |
    And Admin successfully approves the loan on "1 January 2024" with "1200" amount and expected disbursement date on "1 January 2024"
    Then Loan status will be "APPROVED"
    Then Loan Tranche Details tab has the following data:
      | Expected Disbursement On | Disbursed On    | Principal   | Net Disbursal Amount |
      | 01 January 2024          |                 | 1000.0      |                      |
    When Admin sets the business date to "2 January 2024"
    And Admin successfully add disbursement detail to the loan on "5 January 2024" with 200 EUR transaction amount
    Then Loan Tranche Details tab has the following data:
      | Expected Disbursement On | Disbursed On    | Principal   | Net Disbursal Amount |
      | 01 January 2024          |                 | 1000.0      |                      |
      | 05 January 2024          |                 | 200.0       | 1200.0               |
    And Admin checks available disbursement amount 0.0 EUR
    Then Admin fails to disburse the loan on "2 January 2024" with "1600" EUR transaction amount because of wrong amount
    And Admin successfully disburse the loan on "2 January 2024" with "1100" EUR transaction amount
    Then Loan status will be "ACTIVE"
    And Admin checks available disbursement amount 100.0 EUR
    Then Loan Tranche Details tab has the following data:
      | Expected Disbursement On | Disbursed On    | Principal   | Net Disbursal Amount |
      | 01 January 2024          | 02 January 2024 | 1100.0      |                      |
      | 05 January 2024          |                 | 200.0       | 1200.0                |

    When Loan Pay-off is made on "2 January 2024"
    Then Loan's all installments have obligations met

  @TestRailId:C67
  Scenario: As admin I would like to check that amounts are distributed equally in loan repayment schedule
    When Admin sets the business date to "1 September 2022"
    When Admin creates a client with random data
    When Admin successfully creates a new customised Loan submitted on date: "1 September 2022", with Principal: "1000", a loanTermFrequency: 3 months, and numberOfRepayments: 3
    And Admin successfully approves the loan on "1 September 2022" with "1000" amount and expected disbursement date on "1 September 2022"
    Then Amounts are distributed equally in loan repayment schedule in case of total amount 1000
    When Admin successfully disburse the loan on "1 September 2022" with "900" EUR transaction amount
    Then Amounts are distributed equally in loan repayment schedule in case of total amount 900

  @TestRailId:C68
  Scenario: As admin I would like to be sure that approval of on loan can be undone
    When Admin sets the business date to "1 September 2022"
    When Admin creates a client with random data
    When Admin successfully creates a new customised Loan submitted on date: "1 September 2022", with Principal: "1000", a loanTermFrequency: 3 months, and numberOfRepayments: 3
    And Admin successfully approves the loan on "1 September 2022" with "1000" amount and expected disbursement date on "1 September 2022"
    Then Admin can successfully undone the loan approval

  @TestRailId:C69
  Scenario: As admin I would like to be sure that disbursal of on loan can be undone
    When Admin sets the business date to "1 September 2022"
    When Admin creates a client with random data
    When Admin successfully creates a new customised Loan submitted on date: "1 September 2022", with Principal: "1000", a loanTermFrequency: 3 months, and numberOfRepayments: 3
    And Admin successfully approves the loan on "1 September 2022" with "1000" amount and expected disbursement date on "1 September 2022"
    When Admin successfully disburse the loan on "1 September 2022" with "1000" EUR transaction amount
    Then Admin can successfully undone the loan disbursal
    Then Admin can successfully undone the loan approval
    And Admin successfully approves the loan on "1 September 2022" with "1000" amount and expected disbursement date on "1 September 2022"

  @TestRailId:C70
  Scenario: As admin I would like to be sure that submitted on date can be edited on loan
    When Admin sets the business date to "1 September 2022"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 September 2022"
    Then Admin can successfully modify the loan and changes the submitted on date to "31 August 2022"

  @TestRailId:C2454 @fraud
  Scenario: As admin I would like to set Fraud flag to a loan
    When Admin sets the business date to "1 September 2022"
    When Admin creates a client with random data
    When Admin successfully creates a new customised Loan submitted on date: "1 September 2022", with Principal: "1000", a loanTermFrequency: 3 months, and numberOfRepayments: 3
    And Admin successfully approves the loan on "1 September 2022" with "1000" amount and expected disbursement date on "1 September 2022"
    When Admin successfully disburse the loan on "1 September 2022" with "1000" EUR transaction amount
    Then Admin can successfully set Fraud flag to the loan

  @TestRailId:C2455 @fraud
  Scenario: As admin I would like to unset Fraud flag to a loan
    When Admin sets the business date to "1 September 2022"
    When Admin creates a client with random data
    When Admin successfully creates a new customised Loan submitted on date: "1 September 2022", with Principal: "1000", a loanTermFrequency: 3 months, and numberOfRepayments: 3
    And Admin successfully approves the loan on "1 September 2022" with "1000" amount and expected disbursement date on "1 September 2022"
    When Admin successfully disburse the loan on "1 September 2022" with "1000" EUR transaction amount
    Then Admin can successfully set Fraud flag to the loan
    Then Admin can successfully unset Fraud flag to the loan


  @TestRailId:C2456 @fraud
  Scenario: As admin I would like to try to add fraud flag on a not active loan
    When Admin sets the business date to "25 October 2022"
    When Admin creates a client with random data
    When Admin successfully creates a new customised Loan submitted on date: "25 October 2022", with Principal: "1000", a loanTermFrequency: 3 months, and numberOfRepayments: 3
    And Admin successfully approves the loan on "25 October 2022" with "1000" amount and expected disbursement date on "25 October 2022"
    Then Admin can successfully unset Fraud flag to the loan

  @TestRailId:C2473 @idempotency
  Scenario: As admin I would like to verify that idempotency APIs can be called with the Idempotency-Key header
    When Admin sets the business date to "1 November 2022"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 November 2022"
    And Admin successfully approves the loan on "1 November 2022" with "1000" amount and expected disbursement date on "1 November 2022"
    When Admin successfully disburse the loan on "1 November 2022" with "1000" EUR transaction amount
    When Admin sets the business date to "15 November 2022"
    When Customer makes "REPAYMENT" transaction with "AUTOPAY" payment type on "15 November 2022" with 200 EUR transaction amount and self-generated Idempotency key
    Then Loan has 1 "DISBURSEMENT" transactions on Transactions tab
    Then Loan has 1 "REPAYMENT" transactions on Transactions tab

  @TestRailId:C2474 @idempotency
  Scenario: As admin I would like to verify that idempotency APIs can be called without the Idempotency-Key header
    When Admin sets the business date to "1 November 2022"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 November 2022"
    And Admin successfully approves the loan on "1 November 2022" with "1000" amount and expected disbursement date on "1 November 2022"
    When Admin successfully disburse the loan on "1 November 2022" with "1000" EUR transaction amount
    When Admin sets the business date to "15 November 2022"
    When Customer makes "REPAYMENT" transaction with "AUTOPAY" payment type on "15 November 2022" with 200 EUR transaction amount and system-generated Idempotency key
    Then Loan has 1 "DISBURSEMENT" transactions on Transactions tab
    Then Loan has 1 "REPAYMENT" transactions on Transactions tab

  @TestRailId:C2475 @idempotency
  Scenario: As admin I would like to verify that idempotency applies correctly in a happy path scenario in case of REPAYMENT transaction
    When Admin sets the business date to "1 November 2022"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 November 2022"
    And Admin successfully approves the loan on "1 November 2022" with "1000" amount and expected disbursement date on "1 November 2022"
    When Admin successfully disburse the loan on "1 November 2022" with "1000" EUR transaction amount
    When Admin sets the business date to "15 November 2022"
    When Customer makes "REPAYMENT" transaction with "AUTOPAY" payment type on "15 November 2022" with 200 EUR transaction amount and self-generated Idempotency key
    And Customer makes "REPAYMENT" transaction with "AUTOPAY" payment type on "15 November 2022" with 300 EUR transaction amount with the same Idempotency key as previous transaction
    Then Transaction response has boolean value in header "x-served-from-cache": "true"
    Then Transaction response has 200 EUR value for transaction amount
    Then Transaction response has the correct clientId and the loanId of the first transaction
    Then Loan has 1 "REPAYMENT" transactions on Transactions tab

  @TestRailId:C2476 @idempotency
  Scenario: As admin I would like to verify that idempotency applies correctly in a happy path scenario in case of GOODWILL_CREDIT transaction
    When Admin sets the business date to "1 November 2022"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 November 2022"
    And Admin successfully approves the loan on "1 November 2022" with "1000" amount and expected disbursement date on "1 November 2022"
    When Admin successfully disburse the loan on "1 November 2022" with "1000" EUR transaction amount
    When Admin sets the business date to "15 November 2022"
    When Customer makes "REPAYMENT" transaction with "AUTOPAY" payment type on "15 November 2022" with 1000 EUR transaction amount and system-generated Idempotency key
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "15 November 2022" with 200 EUR transaction amount and self-generated Idempotency key
    And Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "15 November 2022" with 300 EUR transaction amount with the same Idempotency key as previous transaction
    Then Transaction response has boolean value in header "x-served-from-cache": "true"
    Then Transaction response has 200 EUR value for transaction amount
    Then Transaction response has the correct clientId and the loanId of the first transaction
    Then Loan has 1 "GOODWILL_CREDIT" transactions on Transactions tab

  @TestRailId:C2477 @idempotency
  Scenario: As admin I would like to verify that idempotency applies correctly in a happy path scenario in case of PAYOUT_REFUND transaction
    When Admin sets the business date to "1 November 2022"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 November 2022"
    And Admin successfully approves the loan on "1 November 2022" with "1000" amount and expected disbursement date on "1 November 2022"
    When Admin successfully disburse the loan on "1 November 2022" with "1000" EUR transaction amount
    When Admin sets the business date to "15 November 2022"
    When Customer makes "PAYOUT_REFUND" transaction with "AUTOPAY" payment type on "15 November 2022" with 200 EUR transaction amount and self-generated Idempotency key
    And Customer makes "PAYOUT_REFUND" transaction with "AUTOPAY" payment type on "15 November 2022" with 300 EUR transaction amount with the same Idempotency key as previous transaction
    Then Transaction response has boolean value in header "x-served-from-cache": "true"
    Then Transaction response has 200 EUR value for transaction amount
    Then Transaction response has the correct clientId and the loanId of the first transaction
    Then Loan has 1 "PAYOUT_REFUND" transactions on Transactions tab

  @TestRailId:C2478 @idempotency
  Scenario: As admin I would like to verify that idempotency applies correctly in a happy path scenario in case of MERCHANT_ISSUED_REFUND transaction
    When Admin sets the business date to "1 November 2022"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 November 2022"
    And Admin successfully approves the loan on "1 November 2022" with "1000" amount and expected disbursement date on "1 November 2022"
    When Admin successfully disburse the loan on "1 November 2022" with "1000" EUR transaction amount
    When Admin sets the business date to "15 November 2022"
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "15 November 2022" with 200 EUR transaction amount and self-generated Idempotency key
    And Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "15 November 2022" with 300 EUR transaction amount with the same Idempotency key as previous transaction
    Then Transaction response has boolean value in header "x-served-from-cache": "true"
    Then Transaction response has 200 EUR value for transaction amount
    Then Transaction response has the correct clientId and the loanId of the first transaction
    Then Loan has 1 "MERCHANT_ISSUED_REFUND" transactions on Transactions tab

  @TestRailId:C2482 @idempotency
  Scenario: As admin I would like to verify that idempotency applies correctly in case of client calls the same idempotency key on a second loan
    When Admin sets the business date to "1 November 2022"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 November 2022"
    And Admin successfully approves the loan on "1 November 2022" with "1000" amount and expected disbursement date on "1 November 2022"
    When Admin successfully disburse the loan on "1 November 2022" with "1000" EUR transaction amount
    When Admin crates a second default loan with date: "1 November 2022"
    And Admin successfully approves the second loan on "1 November 2022" with "1000" amount and expected disbursement date on "1 November 2022"
    When Admin successfully disburse the second loan on "1 November 2022" with "1000" EUR transaction amount
    When Admin sets the business date to "15 November 2022"
    When Customer makes "REPAYMENT" transaction with "AUTOPAY" payment type on "15 November 2022" with 200 EUR transaction amount and self-generated Idempotency key
    And Customer makes "REPAYMENT" transaction on the second loan with "AUTOPAY" payment type on "15 November 2022" with 300 EUR transaction amount with the same Idempotency key as previous transaction
    Then Transaction response has boolean value in header "x-served-from-cache": "true"
    Then Transaction response has 200 EUR value for transaction amount
    Then Transaction response has the correct clientId and the loanId of the first transaction
    Then Loan has 1 "REPAYMENT" transactions on Transactions tab
    Then Second loan has 0 "REPAYMENT" transactions on Transactions tab

#  TODO unskip and check when PS-1106 is done
  @Skip @TestRailId:C2483 @idempotency
  Scenario: As admin I would like to verify that idempotency applies correctly in case of a second client calls the same idempotency key on a second loan
    When Admin sets the business date to "1 November 2022"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 November 2022"
    And Admin successfully approves the loan on "1 November 2022" with "1000" amount and expected disbursement date on "1 November 2022"
    When Admin successfully disburse the loan on "1 November 2022" with "1000" EUR transaction amount
    When Admin creates a second client with random data
    When Admin crates a second default loan for the second client with date: "1 November 2022"
    And Admin successfully approves the second loan on "1 November 2022" with "1000" amount and expected disbursement date on "1 November 2022"
    When Admin successfully disburse the second loan on "1 November 2022" with "1000" EUR transaction amount
    When Admin sets the business date to "15 November 2022"
    When Customer makes "REPAYMENT" transaction with "AUTOPAY" payment type on "15 November 2022" with 200 EUR transaction amount and self-generated Idempotency key
    And Customer makes "REPAYMENT" transaction on the second loan with "AUTOPAY" payment type on "15 November 2022" with 300 EUR transaction amount with the same Idempotency key as previous transaction
    Then Transaction response has boolean value in header "x-served-from-cache": "true"
    Then Transaction response has 300 EUR value for transaction amount
    Then Transaction response has the clientId for the second client and the loanId of the second transaction
    Then Loan has 1 "REPAYMENT" transactions on Transactions tab
    Then Second loan has 1 "REPAYMENT" transactions on Transactions tab

  @TestRailId:C2479
  Scenario: As admin I would like to be sure that goodwill credit transaction is working properly
    When Admin sets the business date to "1 November 2022"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 November 2022"
    And Admin successfully approves the loan on "1 November 2022" with "1000" amount and expected disbursement date on "1 November 2022"
    When Admin successfully disburse the loan on "1 November 2022" with "1000" EUR transaction amount
    When Admin sets the business date to "15 November 2022"
    And Customer makes "AUTOPAY" repayment on "15 November 2022" with 1000 EUR transaction amount
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "15 November 2022" with 200 EUR transaction amount and self-generated Idempotency key
    Then Loan has 1 "GOODWILL_CREDIT" transactions on Transactions tab

  @TestRailId:C2480
  Scenario: As admin I would like to be sure that payout refund transaction is working properly
    When Admin sets the business date to "1 November 2022"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 November 2022"
    And Admin successfully approves the loan on "1 November 2022" with "1000" amount and expected disbursement date on "1 November 2022"
    When Admin successfully disburse the loan on "1 November 2022" with "1000" EUR transaction amount
    When Admin sets the business date to "15 November 2022"
    And Customer makes "AUTOPAY" repayment on "15 November 2022" with 1000 EUR transaction amount
    When Customer makes "PAYOUT_REFUND" transaction with "AUTOPAY" payment type on "15 November 2022" with 200 EUR transaction amount and self-generated Idempotency key
    Then Loan has 1 "PAYOUT_REFUND" transactions on Transactions tab

  @TestRailId:C2481
  Scenario: As admin I would like to be sure that  merchant issued refund transaction is working properly
    When Admin sets the business date to "1 November 2022"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 November 2022"
    And Admin successfully approves the loan on "1 November 2022" with "1000" amount and expected disbursement date on "1 November 2022"
    When Admin successfully disburse the loan on "1 November 2022" with "1000" EUR transaction amount
    When Admin sets the business date to "15 November 2022"
    And Customer makes "AUTOPAY" repayment on "15 November 2022" with 1000 EUR transaction amount
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "15 November 2022" with 200 EUR transaction amount and self-generated Idempotency key
    Then Loan has 1 "MERCHANT_ISSUED_REFUND" transactions on Transactions tab

  @TestRailId:C2488
  Scenario: As admin I would like to be sure that no multiple status change event got raised during transaction replaying
    When Admin sets the business date to "1 November 2022"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 November 2022"
    And Admin successfully approves the loan on "1 November 2022" with "1000" amount and expected disbursement date on "1 November 2022"
    Then Loan status has changed to "Approved"
    When Admin successfully disburse the loan on "1 November 2022" with "1000" EUR transaction amount
    Then Loan status has changed to "Active"
    When Admin sets the business date to "2 November 2022"
    And Customer makes "AUTOPAY" repayment on "2 November 2022" with 500 EUR transaction amount
    When Admin sets the business date to "3 November 2022"
    And Customer makes "AUTOPAY" repayment on "3 November 2022" with 100 EUR transaction amount
    When Admin sets the business date to "4 November 2022"
    And Customer makes "AUTOPAY" repayment on "4 November 2022" with 600 EUR transaction amount
    Then Loan status has changed to "Overpaid"
    When Customer undo "2"th repayment on "4 November 2022"
    Then No new event with type "LoanStatusChangedEvent" has been raised for the loan
    When Customer undo "1"th repayment on "4 November 2022"
    Then Loan status has changed to "Active"

  @TestRailId:C2489
  Scenario: As admin I would like to charge-off a loan and be sure the event was triggered
    When Admin sets the business date to "1 November 2022"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 November 2022"
    And Admin successfully approves the loan on "1 November 2022" with "1000" amount and expected disbursement date on "1 November 2022"
    When Admin successfully disburse the loan on "1 November 2022" with "1000" EUR transaction amount
    When Admin sets the business date to "2 November 2022"
    And Customer makes "AUTOPAY" repayment on "2 November 2022" with 500 EUR transaction amount
    When Admin sets the business date to "3 November 2022"
    And Admin does charge-off the loan on "3 November 2022"
    Then Loan marked as charged-off on "03 November 2022"

  @TestRailId:C2491
  Scenario: As a user I would like to do multiple repayment, overpay the loan and reverse-replaying transactions and check outstanding balance
    When Admin sets the business date to "01 November 2022"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "01 November 2022"
    And Admin successfully approves the loan on "01 November 2022" with "1000" amount and expected disbursement date on "01 November 2022"
    When Admin successfully disburse the loan on "01 November 2022" with "1000" EUR transaction amount
    Then Loan has 1000 outstanding amount
    When Admin sets the business date to "02 November 2022"
    And Customer makes "AUTOPAY" repayment on "02 November 2022" with 500 EUR transaction amount
    Then Loan Transactions tab has a transaction with date: "02 November 2022", and with the following data:
      | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | Repayment        | 500.0  | 500.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan has 500 outstanding amount
    When Admin sets the business date to "03 November 2022"
    And Customer makes "AUTOPAY" repayment on "03 November 2022" with 10 EUR transaction amount
    Then Loan Transactions tab has a transaction with date: "03 November 2022", and with the following data:
      | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | Repayment        | 10.0   | 10.0      | 0.0      | 0.0  | 0.0       | 490.0        |
    Then Loan has 490 outstanding amount
    When Admin sets the business date to "04 November 2022"
    And Customer makes "AUTOPAY" repayment on "04 November 2022" with 400 EUR transaction amount
    Then Loan Transactions tab has a transaction with date: "04 November 2022", and with the following data:
      | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | Repayment        | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 90.0         |
    Then Loan has 90 outstanding amount
    When Admin sets the business date to "05 November 2022"
    And Customer makes "AUTOPAY" repayment on "05 November 2022" with 390 EUR transaction amount
    Then Loan Transactions tab has a transaction with date: "05 November 2022", and with the following data:
      | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | Repayment        | 390.0  | 90.0      | 0.0      | 0.0  | 0.0       | 0.0          |
    Then Loan has 300 overpaid amount
    When Customer undo "2"th repayment on "04 November 2022"
    Then In Loan Transactions the "3"th Transaction has Transaction type="Repayment" and is reverted
    Then Loan Transactions tab has a transaction with date: "04 November 2022", and with the following data:
      | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | Repayment        | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 100.0        |
    Then Loan Transactions tab has a transaction with date: "05 November 2022", and with the following data:
      | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | Repayment        | 390.0  | 100.0     | 0.0      | 0.0  | 0.0       | 0.0          |
    Then Loan has 290 overpaid amount
    When Customer undo "1"th repayment on "04 November 2022"
    Then In Loan Transactions the "2"th Transaction has Transaction type="Repayment" and is reverted
    Then In Loan Transactions the "3"th Transaction has Transaction type="Repayment" and is reverted
    Then Loan Transactions tab has a transaction with date: "04 November 2022", and with the following data:
      | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | Repayment        | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 600.0        |
    Then Loan Transactions tab has a transaction with date: "05 November 2022", and with the following data:
      | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | Repayment        | 390.0  | 390.0     | 0.0      | 0.0  | 0.0       | 210.0        |
    Then Loan has 210 outstanding amount
    And Customer makes "AUTOPAY" repayment on "02 November 2022" with 500 EUR transaction amount
    Then Loan has 290 overpaid amount

  @TestRailId:C2502
  Scenario: Verify that Loan status goes from active to overpaid in case of Goodwill credit transaction when transaction amount is greater than balance
    When Admin sets the business date to "1 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 January 2023"
    And Admin successfully approves the loan on "1 January 2023" with "1000" amount and expected disbursement date on "1 January 2023"
    When Admin successfully disburse the loan on "1 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "3 January 2023"
    And Customer makes "AUTOPAY" repayment on "3 January 2023" with 450 EUR transaction amount
    When Admin sets the business date to "5 January 2023"
    And Customer makes "AUTOPAY" repayment on "5 January 2023" with 250 EUR transaction amount
    Then Loan status will be "ACTIVE"
    Then Loan has 300 outstanding amount
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "5 January 2023" with 400 EUR transaction amount and system-generated Idempotency key
    Then Loan status will be "OVERPAID"
    Then Loan has 0 outstanding amount
    Then Loan has 100 overpaid amount
    Then Loan Transactions tab has a "DISBURSEMENT" transaction with date "01 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit  | Credit |
      | ASSET     | 112601       | Loans Receivable          | 1000.0 |        |
      | LIABILITY | 145023       | Suspense/Clearing account |        | 1000.0 |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "03 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 450.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 450.0 |        |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "05 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 250.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 250.0 |        |
    Then Loan Transactions tab has a "GOODWILL_CREDIT" transaction with date "05 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name             | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable         |       | 300.0  |
      | LIABILITY | l1           | Overpayment account      |       | 100.0  |
      | EXPENSE   | 744003       | Goodwill Expense Account | 400.0 |        |

  @TestRailId:C2503
  Scenario: Verify that Loan status goes from active to overpaid in case of Backdated 3rd repayment when transaction amount is greater than balance
    When Admin sets the business date to "1 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 January 2023"
    And Admin successfully approves the loan on "1 January 2023" with "1000" amount and expected disbursement date on "1 January 2023"
    When Admin successfully disburse the loan on "1 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "3 January 2023"
    And Customer makes "AUTOPAY" repayment on "3 January 2023" with 450 EUR transaction amount
    When Admin sets the business date to "5 January 2023"
    And Customer makes "AUTOPAY" repayment on "5 January 2023" with 250 EUR transaction amount
    Then Loan status will be "ACTIVE"
    Then Loan has 300 outstanding amount
    And Customer makes "AUTOPAY" repayment on "2 January 2023" with 400 EUR transaction amount
    Then Loan status will be "OVERPAID"
    Then Loan has 0 outstanding amount
    Then Loan has 100 overpaid amount
    Then Loan Transactions tab has a "DISBURSEMENT" transaction with date "01 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit  | Credit |
      | ASSET     | 112601       | Loans Receivable          | 1000.0 |        |
      | LIABILITY | 145023       | Suspense/Clearing account |        | 1000.0 |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "02 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 400.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 400.0 |        |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "03 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 450.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 450.0 |        |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "05 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 150.0  |
      | LIABILITY | l1           | Overpayment account       |       | 100.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 250.0 |        |


  @TestRailId:C2504
  Scenario: Verify that Loan status goes from overpaid to active in case of Chargeback transaction when transaction amount is greater than overpaid amount
    When Admin sets the business date to "1 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 January 2023"
    And Admin successfully approves the loan on "1 January 2023" with "1000" amount and expected disbursement date on "1 January 2023"
    When Admin successfully disburse the loan on "1 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "3 January 2023"
    And Customer makes "AUTOPAY" repayment on "3 January 2023" with 450 EUR transaction amount
    When Admin sets the business date to "5 January 2023"
    And Customer makes "AUTOPAY" repayment on "5 January 2023" with 450 EUR transaction amount
    And Customer makes "AUTOPAY" repayment on "5 January 2023" with 300 EUR transaction amount
    Then Loan status will be "OVERPAID"
    Then Loan has 0 outstanding amount
    Then Loan has 200 overpaid amount
    When Admin makes "REPAYMENT_ADJUSTMENT_CHARGEBACK" chargeback with 300 EUR transaction amount
    Then Loan status will be "ACTIVE"
    Then Loan has 100 outstanding amount
    Then Loan Transactions tab has a "DISBURSEMENT" transaction with date "01 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit  | Credit |
      | ASSET     | 112601       | Loans Receivable          | 1000.0 |        |
      | LIABILITY | 145023       | Suspense/Clearing account |        | 1000.0 |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "03 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 450.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 450.0 |        |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "05 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 450.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 450.0 |        |
      | ASSET     | 112601       | Loans Receivable          |       | 100.0  |
      | LIABILITY | l1           | Overpayment account       |       | 200.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 300.0 |        |
    Then Loan Transactions tab has a "CHARGEBACK" transaction with date "05 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          | 100.0 |        |
      | LIABILITY | 145023       | Suspense/Clearing account |       | 300.0  |
      | LIABILITY | l1           | Overpayment account       | 200.0 |        |

  @TestRailId:C2506
  Scenario: Verify that Loan status goes from overpaid to active in case of 1st repayment is undone
    When Admin sets the business date to "1 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 January 2023"
    And Admin successfully approves the loan on "1 January 2023" with "1000" amount and expected disbursement date on "1 January 2023"
    When Admin successfully disburse the loan on "1 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "3 January 2023"
    And Customer makes "AUTOPAY" repayment on "3 January 2023" with 450 EUR transaction amount
    When Admin sets the business date to "5 January 2023"
    And Customer makes "AUTOPAY" repayment on "5 January 2023" with 450 EUR transaction amount
    And Customer makes "AUTOPAY" repayment on "5 January 2023" with 300 EUR transaction amount
    Then Loan status will be "OVERPAID"
    Then Loan has 0 outstanding amount
    Then Loan has 200 overpaid amount
    When Customer undo "1"th repayment on "3 January 2023"
    Then Loan status will be "ACTIVE"
    Then Loan has 250 outstanding amount
    Then Loan Transactions tab has a "DISBURSEMENT" transaction with date "01 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit  | Credit |
      | ASSET     | 112601       | Loans Receivable          | 1000.0 |        |
      | LIABILITY | 145023       | Suspense/Clearing account |        | 1000.0 |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "03 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 450.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 450.0 |        |
      | ASSET     | 112601       | Loans Receivable          | 450.0 |        |
      | LIABILITY | 145023       | Suspense/Clearing account |       | 450.0  |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "05 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 450.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 450.0 |        |
      | ASSET     | 112601       | Loans Receivable          |       | 300.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 300.0 |        |

  @TestRailId:C2507
  Scenario: Verify that Loan status goes from active to closed in case of Goodwill credit transaction when transaction amount equals balance
    When Admin sets the business date to "1 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 January 2023"
    And Admin successfully approves the loan on "1 January 2023" with "1000" amount and expected disbursement date on "1 January 2023"
    When Admin successfully disburse the loan on "1 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "3 January 2023"
    And Customer makes "AUTOPAY" repayment on "3 January 2023" with 450 EUR transaction amount
    When Admin sets the business date to "5 January 2023"
    And Customer makes "AUTOPAY" repayment on "5 January 2023" with 250 EUR transaction amount
    Then Loan status will be "ACTIVE"
    Then Loan has 300 outstanding amount
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "5 January 2023" with 300 EUR transaction amount and system-generated Idempotency key
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan has 0 outstanding amount
    Then Loan Transactions tab has a "DISBURSEMENT" transaction with date "01 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit  | Credit |
      | ASSET     | 112601       | Loans Receivable          | 1000.0 |        |
      | LIABILITY | 145023       | Suspense/Clearing account |        | 1000.0 |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "03 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 450.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 450.0 |        |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "05 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 250.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 250.0 |        |
    Then Loan Transactions tab has a "GOODWILL_CREDIT" transaction with date "05 January 2023" which has the following Journal entries:
      | Type    | Account code | Account name             | Debit | Credit |
      | ASSET   | 112601       | Loans Receivable         |       | 300.0  |
      | EXPENSE | 744003       | Goodwill Expense Account | 300.0 |        |

  @TestRailId:C2508
  Scenario: Verify that Loan status goes from active to closed in case of Backdated 3rd repayment when transaction amount equals balance
    When Admin sets the business date to "1 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 January 2023"
    And Admin successfully approves the loan on "1 January 2023" with "1000" amount and expected disbursement date on "1 January 2023"
    When Admin successfully disburse the loan on "1 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "3 January 2023"
    And Customer makes "AUTOPAY" repayment on "3 January 2023" with 450 EUR transaction amount
    When Admin sets the business date to "5 January 2023"
    And Customer makes "AUTOPAY" repayment on "5 January 2023" with 250 EUR transaction amount
    Then Loan status will be "ACTIVE"
    Then Loan has 300 outstanding amount
    And Customer makes "AUTOPAY" repayment on "2 January 2023" with 300 EUR transaction amount
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan has 0 outstanding amount
    Then Loan Transactions tab has a "DISBURSEMENT" transaction with date "01 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit  | Credit |
      | ASSET     | 112601       | Loans Receivable          | 1000.0 |        |
      | LIABILITY | 145023       | Suspense/Clearing account |        | 1000.0 |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "02 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 300.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 300.0 |        |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "03 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 450.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 450.0 |        |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "05 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 250.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 250.0 |        |

  @TestRailId:C2509
  Scenario: Verify that Loan status goes from closed to overpaid in case of Goodwill credit transaction
    When Admin sets the business date to "1 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 January 2023"
    And Admin successfully approves the loan on "1 January 2023" with "1000" amount and expected disbursement date on "1 January 2023"
    When Admin successfully disburse the loan on "1 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "3 January 2023"
    And Customer makes "AUTOPAY" repayment on "3 January 2023" with 500 EUR transaction amount
    When Admin sets the business date to "5 January 2023"
    And Customer makes "AUTOPAY" repayment on "5 January 2023" with 300 EUR transaction amount
    When Admin sets the business date to "7 January 2023"
    And Customer makes "AUTOPAY" repayment on "7 January 2023" with 200 EUR transaction amount
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan has 0 outstanding amount
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "7 January 2023" with 100 EUR transaction amount and system-generated Idempotency key
    Then Loan status will be "OVERPAID"
    Then Loan has 0 outstanding amount
    Then Loan has 100 overpaid amount
    Then Loan Transactions tab has a "DISBURSEMENT" transaction with date "01 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit  | Credit |
      | ASSET     | 112601       | Loans Receivable          | 1000.0 |        |
      | LIABILITY | 145023       | Suspense/Clearing account |        | 1000.0 |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "03 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 500.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 500.0 |        |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "05 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 300.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 300.0 |        |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "07 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 200.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 200.0 |        |
    Then Loan Transactions tab has a "GOODWILL_CREDIT" transaction with date "07 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name             | Debit | Credit |
      | LIABILITY | l1           | Overpayment account      |       | 100.0  |
      | EXPENSE   | 744003       | Goodwill Expense Account | 100.0 |        |


  @TestRailId:C2510
  Scenario: Verify that Loan status goes from overpaid to closed in case of Chargeback transaction when transaction amount equals overpaid amount
    When Admin sets the business date to "1 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 January 2023"
    And Admin successfully approves the loan on "1 January 2023" with "1000" amount and expected disbursement date on "1 January 2023"
    When Admin successfully disburse the loan on "1 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "3 January 2023"
    And Customer makes "AUTOPAY" repayment on "3 January 2023" with 450 EUR transaction amount
    When Admin sets the business date to "5 January 2023"
    And Customer makes "AUTOPAY" repayment on "5 January 2023" with 450 EUR transaction amount
    And Customer makes "AUTOPAY" repayment on "5 January 2023" with 300 EUR transaction amount
    Then Loan status will be "OVERPAID"
    Then Loan has 0 outstanding amount
    Then Loan has 200 overpaid amount
    When Admin makes "REPAYMENT_ADJUSTMENT_CHARGEBACK" chargeback with 200 EUR transaction amount
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan has 0 outstanding amount
    Then Loan Transactions tab has a "DISBURSEMENT" transaction with date "01 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit  | Credit |
      | ASSET     | 112601       | Loans Receivable          | 1000.0 |        |
      | LIABILITY | 145023       | Suspense/Clearing account |        | 1000.0 |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "03 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 450.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 450.0 |        |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "05 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 450.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 450.0 |        |
      | ASSET     | 112601       | Loans Receivable          |       | 100.0  |
      | LIABILITY | l1           | Overpayment account       |       | 200.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 300.0 |        |
    Then Loan Transactions tab has a "CHARGEBACK" transaction with date "05 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | LIABILITY | l1           | Overpayment account       | 200.0 |        |
      | LIABILITY | 145023       | Suspense/Clearing account |       | 200.0  |


  @TestRailId:C2512
  Scenario: Verify that Loan status goes from overpaid to closed in case of 1st repayment is undone
    When Admin sets the business date to "1 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 January 2023"
    And Admin successfully approves the loan on "1 January 2023" with "1000" amount and expected disbursement date on "1 January 2023"
    When Admin successfully disburse the loan on "1 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "3 January 2023"
    And Customer makes "AUTOPAY" repayment on "3 January 2023" with 200 EUR transaction amount
    When Admin sets the business date to "5 January 2023"
    And Customer makes "AUTOPAY" repayment on "5 January 2023" with 700 EUR transaction amount
    And Customer makes "AUTOPAY" repayment on "5 January 2023" with 300 EUR transaction amount
    Then Loan status will be "OVERPAID"
    Then Loan has 0 outstanding amount
    Then Loan has 200 overpaid amount
    When Customer undo "1"th repayment on "3 January 2023"
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan has 0 outstanding amount
    Then Loan Transactions tab has a "DISBURSEMENT" transaction with date "01 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit  | Credit |
      | ASSET     | 112601       | Loans Receivable          | 1000.0 |        |
      | LIABILITY | 145023       | Suspense/Clearing account |        | 1000.0 |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "03 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 200.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 200.0 |        |
      | ASSET     | 112601       | Loans Receivable          | 200.0 |        |
      | LIABILITY | 145023       | Suspense/Clearing account |       | 200.0  |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "05 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 700.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 700.0 |        |
      | ASSET     | 112601       | Loans Receivable          |       | 300.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 300.0 |        |

  @TestRailId:C2513
  Scenario: Verify that Loan status goes from closed to active in case of Chargeback transaction
    When Admin sets the business date to "1 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 January 2023"
    And Admin successfully approves the loan on "1 January 2023" with "1000" amount and expected disbursement date on "1 January 2023"
    When Admin successfully disburse the loan on "1 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "3 January 2023"
    And Customer makes "AUTOPAY" repayment on "3 January 2023" with 500 EUR transaction amount
    When Admin sets the business date to "5 January 2023"
    And Customer makes "AUTOPAY" repayment on "5 January 2023" with 300 EUR transaction amount
    When Admin sets the business date to "7 January 2023"
    And Customer makes "AUTOPAY" repayment on "7 January 2023" with 200 EUR transaction amount
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan has 0 outstanding amount
    When Admin makes "REPAYMENT_ADJUSTMENT_CHARGEBACK" chargeback with 200 EUR transaction amount
    Then Loan status will be "ACTIVE"
    Then Loan has 200 outstanding amount
    Then Loan Transactions tab has a "DISBURSEMENT" transaction with date "01 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit  | Credit |
      | ASSET     | 112601       | Loans Receivable          | 1000.0 |        |
      | LIABILITY | 145023       | Suspense/Clearing account |        | 1000.0 |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "03 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 500.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 500.0 |        |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "05 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 300.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 300.0 |        |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "07 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 200.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 200.0 |        |
    Then Loan Transactions tab has a "CHARGEBACK" transaction with date "07 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          | 200.0 |        |
      | LIABILITY | 145023       | Suspense/Clearing account |       | 200.0  |

  @TestRailId:C2514
  Scenario: Verify that Loan status goes from closed to active in case of 1st repayment is undone
    When Admin sets the business date to "1 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 January 2023"
    And Admin successfully approves the loan on "1 January 2023" with "1000" amount and expected disbursement date on "1 January 2023"
    When Admin successfully disburse the loan on "1 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "3 January 2023"
    And Customer makes "AUTOPAY" repayment on "3 January 2023" with 200 EUR transaction amount
    When Admin sets the business date to "5 January 2023"
    And Customer makes "AUTOPAY" repayment on "5 January 2023" with 600 EUR transaction amount
    When Admin sets the business date to "7 January 2023"
    And Customer makes "AUTOPAY" repayment on "7 January 2023" with 200 EUR transaction amount
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan has 0 outstanding amount
    When Customer undo "1"th repayment on "3 January 2023"
    Then Loan status will be "ACTIVE"
    Then Loan has 200 outstanding amount
    Then Loan Transactions tab has a "DISBURSEMENT" transaction with date "01 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit  | Credit |
      | ASSET     | 112601       | Loans Receivable          | 1000.0 |        |
      | LIABILITY | 145023       | Suspense/Clearing account |        | 1000.0 |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "03 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 200.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 200.0 |        |
      | ASSET     | 112601       | Loans Receivable          | 200.0 |        |
      | LIABILITY | 145023       | Suspense/Clearing account |       | 200.0  |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "05 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 600.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 600.0 |        |
    Then Loan Transactions tab has a "REPAYMENT" transaction with date "07 January 2023" which has the following Journal entries:
      | Type      | Account code | Account name              | Debit | Credit |
      | ASSET     | 112601       | Loans Receivable          |       | 200.0  |
      | LIABILITY | 145023       | Suspense/Clearing account | 200.0 |        |


  @TestRailId:C2539
  Scenario: Verify that loan overdue calculation is updated upon Goodwill credit transaction
    When Admin sets the business date to "1 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 January 2023"
    And Admin successfully approves the loan on "1 January 2023" with "1000" amount and expected disbursement date on "1 January 2023"
    When Admin successfully disburse the loan on "1 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "1 March 2023"
    When Admin runs inline COB job for Loan
    Then Admin checks that delinquency range is: "RANGE_3" and has delinquentDate "2023-02-03"
    Then Loan status will be "ACTIVE"
    Then Loan has 1000 outstanding amount
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "1 March 2023" with 1000 EUR transaction amount and system-generated Idempotency key
    Then Admin checks that delinquency range is: "NO_DELINQUENCY" and has delinquentDate ""
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan has 0 outstanding amount

  @TestRailId:C2540
  Scenario: Verify that loan overdue calculation is updated upon Payout refund transaction
    When Admin sets the business date to "1 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 January 2023"
    And Admin successfully approves the loan on "1 January 2023" with "1000" amount and expected disbursement date on "1 January 2023"
    When Admin successfully disburse the loan on "1 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "1 March 2023"
    When Admin runs inline COB job for Loan
    Then Admin checks that delinquency range is: "RANGE_3" and has delinquentDate "2023-02-03"
    Then Loan status will be "ACTIVE"
    Then Loan has 1000 outstanding amount
    When Customer makes "PAYOUT_REFUND" transaction with "AUTOPAY" payment type on "1 March 2023" with 1000 EUR transaction amount and system-generated Idempotency key
    Then Admin checks that delinquency range is: "NO_DELINQUENCY" and has delinquentDate ""
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan has 0 outstanding amount

  @TestRailId:C2541
  Scenario: Verify that loan overdue calculation is updated upon Merchant issued refund transaction
    When Admin sets the business date to "1 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 January 2023"
    And Admin successfully approves the loan on "1 January 2023" with "1000" amount and expected disbursement date on "1 January 2023"
    When Admin successfully disburse the loan on "1 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "1 March 2023"
    When Admin runs inline COB job for Loan
    Then Admin checks that delinquency range is: "RANGE_3" and has delinquentDate "2023-02-03"
    Then Loan status will be "ACTIVE"
    Then Loan has 1000 outstanding amount
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "1 March 2023" with 1000 EUR transaction amount and system-generated Idempotency key
    Then Admin checks that delinquency range is: "NO_DELINQUENCY" and has delinquentDate ""
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan has 0 outstanding amount

  @TestRailId:C2552
  Scenario: Verify that delinquency event contains the correct delinquentDate in case of one repayment is overdue
    When Admin sets the business date to "1 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "1 January 2023"
    And Admin successfully approves the loan on "1 January 2023" with "1000" amount and expected disbursement date on "1 January 2023"
    When Admin successfully disburse the loan on "1 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "5 March 2023"
    When Admin runs inline COB job for Loan
    Then Admin checks that delinquency range is: "RANGE_3" and has delinquentDate "2023-02-03"

  @TestRailId:C2553
  Scenario: Verify that delinquency event contains the correct delinquentDate in case of multiple repayments are overdue
    When Admin sets the business date to "1 January 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy                        |
      | LP1         | 1 January 2023    | 1000           | 0                      | DECLINING_BALANCE | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | PENALTIES_FEES_INTEREST_PRINCIPAL_ORDER |
    And Admin successfully approves the loan on "1 January 2023" with "1000" amount and expected disbursement date on "1 January 2023"
    When Admin successfully disburse the loan on "1 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "5 April 2023"
    When Admin runs inline COB job for Loan
    Then Admin checks that delinquency range is: "RANGE_30" and has delinquentDate "2023-02-04"

  @TestRailId:C2583
  Scenario: Verify last payment related fields when retrieving loan details with 1 repayment
    When Admin sets the business date to "01 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "01 January 2023"
    And Admin successfully approves the loan on "01 January 2023" with "1000" amount and expected disbursement date on "01 January 2023"
    When Admin successfully disburse the loan on "01 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "03 January 2023"
    And Customer makes "AUTOPAY" repayment on "03 January 2023" with 200 EUR transaction amount
    Then Loan details has the following last payment related data:
      | lastPaymentAmount | lastPaymentDate | lastRepaymentAmount | lastRepaymentDate |
      | 200.0             | 03 January 2023 | 200.0               | 03 January 2023   |

  @TestRailId:C2586
  Scenario: Verify last payment related fields when retrieving loan details with 2 repayments on different day
    When Admin sets the business date to "01 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "01 January 2023"
    And Admin successfully approves the loan on "01 January 2023" with "1000" amount and expected disbursement date on "01 January 2023"
    When Admin successfully disburse the loan on "01 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "03 January 2023"
    And Customer makes "AUTOPAY" repayment on "03 January 2023" with 200 EUR transaction amount
    When Admin sets the business date to "05 January 2023"
    And Customer makes "AUTOPAY" repayment on "05 January 2023" with 300 EUR transaction amount
    Then Loan details has the following last payment related data:
      | lastPaymentAmount | lastPaymentDate | lastRepaymentAmount | lastRepaymentDate |
      | 300.0             | 05 January 2023 | 300.0               | 05 January 2023   |

  @TestRailId:C2587
  Scenario: Verify last payment related fields when retrieving loan details with 2 repayments on the same day
    When Admin sets the business date to "01 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "01 January 2023"
    And Admin successfully approves the loan on "01 January 2023" with "1000" amount and expected disbursement date on "01 January 2023"
    When Admin successfully disburse the loan on "01 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "03 January 2023"
    And Customer makes "AUTOPAY" repayment on "03 January 2023" with 200 EUR transaction amount
    And Customer makes "AUTOPAY" repayment on "03 January 2023" with 300 EUR transaction amount
    Then Loan details has the following last payment related data:
      | lastPaymentAmount | lastPaymentDate | lastRepaymentAmount | lastRepaymentDate |
      | 300.0             | 03 January 2023 | 300.0               | 03 January 2023   |

  @TestRailId:C2588
  Scenario: Verify last payment related fields when retrieving loan details with 2 repayments on different day then the second repayment reversed
    When Admin sets the business date to "01 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "01 January 2023"
    And Admin successfully approves the loan on "01 January 2023" with "1000" amount and expected disbursement date on "01 January 2023"
    When Admin successfully disburse the loan on "01 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "03 January 2023"
    And Customer makes "AUTOPAY" repayment on "03 January 2023" with 200 EUR transaction amount
    When Admin sets the business date to "05 January 2023"
    And Customer makes "AUTOPAY" repayment on "05 January 2023" with 300 EUR transaction amount
    Then Loan details has the following last payment related data:
      | lastPaymentAmount | lastPaymentDate | lastRepaymentAmount | lastRepaymentDate |
      | 300.0             | 05 January 2023 | 300.0               | 05 January 2023   |
    When Customer undo "1"th transaction made on "05 January 2023"
    Then Loan details has the following last payment related data:
      | lastPaymentAmount | lastPaymentDate | lastRepaymentAmount | lastRepaymentDate |
      | 200.0             | 03 January 2023 | 200.0               | 03 January 2023   |

  @TestRailId:C2589
  Scenario: Verify last payment related fields when retrieving loan details with 1 repayment and 1 goodwill credit transaction
    When Admin sets the business date to "01 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "01 January 2023"
    And Admin successfully approves the loan on "01 January 2023" with "1000" amount and expected disbursement date on "01 January 2023"
    When Admin successfully disburse the loan on "01 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "03 January 2023"
    And Customer makes "AUTOPAY" repayment on "03 January 2023" with 200 EUR transaction amount
    When Admin sets the business date to "05 January 2023"
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "5 January 2023" with 400 EUR transaction amount and system-generated Idempotency key
    Then Loan details has the following last payment related data:
      | lastPaymentAmount | lastPaymentDate | lastRepaymentAmount | lastRepaymentDate |
      | 400.0             | 05 January 2023 | 200.0               | 03 January 2023   |

  @TestRailId:C2590
  Scenario: Verify last payment related fields when retrieving loan details with 1 repayment, 1 goodwill credit transaction and 1 more repayment then the second repayment reversed
    When Admin sets the business date to "01 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "01 January 2023"
    And Admin successfully approves the loan on "01 January 2023" with "1000" amount and expected disbursement date on "01 January 2023"
    When Admin successfully disburse the loan on "01 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "03 January 2023"
    And Customer makes "AUTOPAY" repayment on "03 January 2023" with 200 EUR transaction amount
    When Admin sets the business date to "05 January 2023"
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "5 January 2023" with 400 EUR transaction amount and system-generated Idempotency key
    When Admin sets the business date to "07 January 2023"
    And Customer makes "AUTOPAY" repayment on "07 January 2023" with 300 EUR transaction amount
    Then Loan details has the following last payment related data:
      | lastPaymentAmount | lastPaymentDate | lastRepaymentAmount | lastRepaymentDate |
      | 300.0             | 07 January 2023 | 300.0               | 07 January 2023   |
    When Customer undo "1"th transaction made on "07 January 2023"
    Then Loan details has the following last payment related data:
      | lastPaymentAmount | lastPaymentDate | lastRepaymentAmount | lastRepaymentDate |
      | 400.0             | 05 January 2023 | 200.0               | 03 January 2023   |

  @TestRailId:C2678
  Scenario: Verify that after loan is closed loan details and event has last repayment date and amount
    When Admin sets the business date to "01 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "01 January 2023"
    And Admin successfully approves the loan on "01 January 2023" with "1000" amount and expected disbursement date on "01 January 2023"
    Then Loan status has changed to "Approved"
    When Admin successfully disburse the loan on "01 January 2023" with "1000" EUR transaction amount
    Then Loan status has changed to "Active"
    When Admin sets the business date to "02 January 2023"
    And Customer makes "AUTOPAY" repayment on "02 January 2023" with 1000 EUR transaction amount
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan details and event has the following last repayment related data:
      | lastPaymentAmount | lastPaymentDate | lastRepaymentAmount | lastRepaymentDate |
      | 1000.0            | 02 January 2023 | 1000.0              | 02 January 2023   |

  @TestRailId:C2679
  Scenario: Verify that after loan is overpaid loan details and event has last repayment date and amount
    When Admin sets the business date to "01 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "01 January 2023"
    And Admin successfully approves the loan on "01 January 2023" with "1000" amount and expected disbursement date on "01 January 2023"
    Then Loan status has changed to "Approved"
    When Admin successfully disburse the loan on "01 January 2023" with "1000" EUR transaction amount
    Then Loan status has changed to "Active"
    When Admin sets the business date to "02 January 2023"
    And Customer makes "AUTOPAY" repayment on "02 January 2023" with 1100 EUR transaction amount
    Then Loan status will be "OVERPAID"
    Then Loan details and event has the following last repayment related data:
      | lastPaymentAmount | lastPaymentDate | lastRepaymentAmount | lastRepaymentDate |
      | 1100.0            | 02 January 2023 | 1100.0              | 02 January 2023   |

  @TestRailId:C2687 @fraud
  Scenario: Verify that closed loan can be marked as Fraud
    When Admin sets the business date to "01 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "01 January 2023"
    And Admin successfully approves the loan on "01 January 2023" with "1000" amount and expected disbursement date on "01 January 2023"
    When Admin successfully disburse the loan on "01 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "15 January 2023"
    And Customer makes "AUTOPAY" repayment on "15 January 2023" with 1000 EUR transaction amount
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Admin can successfully set Fraud flag to the loan

  @TestRailId:C2688 @fraud
  Scenario: Verify that overpaid loan can be marked as Fraud
    When Admin sets the business date to "01 January 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "01 January 2023"
    And Admin successfully approves the loan on "01 January 2023" with "1000" amount and expected disbursement date on "01 January 2023"
    When Admin successfully disburse the loan on "01 January 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "15 January 2023"
    And Customer makes "AUTOPAY" repayment on "15 January 2023" with 1100 EUR transaction amount
    Then Loan status will be "OVERPAID"
    Then Admin can successfully set Fraud flag to the loan

  @TestRailId:C2690
  Scenario: Verify that the repayment schedule is correct when the loan has a fee and multi disbursement happens
    When Admin sets the business date to "1 May 2023"
    When Admin creates a client with random data
    And Admin successfully creates a new customised Loan submitted on date: "1 May 2023", with Principal: "1000", a loanTermFrequency: 1 months, and numberOfRepayments: 1
    And Admin successfully approves the loan on "1 May 2023" with "1000" amount and expected disbursement date on "1 May 2023"
    And Admin successfully disburse the loan on "1 May 2023" with "750" EUR transaction amount
    Then Loan has 750 outstanding amount
    When Admin adds "LOAN_SNOOZE_FEE" due date charge with "1 May 2023" due date and 8 EUR transaction amount
    Then Loan Repayment schedule has 1 periods, with the following data for periods:
      | Nr | Days | Date         | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 May 2023  |           | 750.0           |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 31   | 01 June 2023 |           | 0.0             | 750.0         | 0.0      | 8.0  | 0.0       | 758.0 | 0.0  | 0.0        | 0.0  | 758.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due | Paid | In advance | Late | Outstanding |
      | 750           | 0        | 8    | 0         | 758 | 0    | 0          | 0    | 758         |
    And Admin successfully disburse the loan on "1 May 2023" with "750" EUR transaction amount
    Then Loan Repayment schedule has 1 periods, with the following data for periods:
      | Nr | Days | Date         | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      |    |      | 01 May 2023  |           | 750.0           |               |          | 0.0  |           | 0.0    | 0.0  |            |      |             |
      |    |      | 01 May 2023  |           | 750.0           |               |          | 0.0  |           | 0.0    | 0.0  |            |      |             |
      | 1  | 31   | 01 June 2023 |           | 0.0             | 1500.0        | 0.0      | 8.0  | 0.0       | 1508.0 | 0.0  | 0.0        | 0.0  | 1508.0      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due  | Paid | In advance | Late | Outstanding |
      | 1500          | 0        | 8    | 0         | 1508 | 0    | 0          | 0    | 1508        |

  @TestRailId:C2691
  Scenario: As an admin I would like to do a chargeback for Goodwill Credit
    When Admin sets the business date to "8 May 2023"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy                        |
      | LP1         | 8 May 2023        | 1000           | 0                      | DECLINING_BALANCE | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | PENALTIES_FEES_INTEREST_PRINCIPAL_ORDER |
    And Admin successfully approves the loan on "8 May 2023" with "1000" amount and expected disbursement date on "8 May 2023"
    And Admin successfully disburse the loan on "8 May 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "9 May 2023"
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "9 May 2023" with 300 EUR transaction amount and system-generated Idempotency key
    When Admin sets the business date to "10 May 2023"
    And Admin makes "REPAYMENT_ADJUSTMENT_CHARGEBACK" chargeback with 300 EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date           | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 08 May 2023    |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 31   | 08 June 2023   |           | 667.0           | 633.0         | 0.0      | 0.0  | 0.0       | 633.0 | 300.0 | 300.0      | 0.0  | 333.0       |
      | 2  | 30   | 08 July 2023   |           | 334.0           | 333.0         | 0.0      | 0.0  | 0.0       | 333.0 | 0.0   | 0.0        | 0.0  | 333.0       |
      | 3  | 31   | 08 August 2023 |           | 0.0             | 334.0         | 0.0      | 0.0  | 0.0       | 334.0 | 0.0   | 0.0        | 0.0  | 334.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due  | Paid | In advance | Late | Outstanding |
      | 1300          | 0        | 0    | 0         | 1300 | 300  | 300        | 0    | 1000        |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 08 May 2023      | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 09 May 2023      | Goodwill Credit  | 300.0  | 300.0     | 0.0      | 0.0  | 0.0       | 700.0        |
      | 10 May 2023      | Chargeback       | 300.0  | 300.0     | 0.0      | 0.0  | 0.0       | 1000.0       |

  @TestRailId:C2692
  Scenario: As an admin I would like to do a chargeback for Payout Refund
    When Admin sets the business date to "8 May 2023"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy                        |
      | LP1         | 8 May 2023        | 1000           | 0                      | DECLINING_BALANCE | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | PENALTIES_FEES_INTEREST_PRINCIPAL_ORDER |
    And Admin successfully approves the loan on "8 May 2023" with "1000" amount and expected disbursement date on "8 May 2023"
    And Admin successfully disburse the loan on "8 May 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "9 May 2023"
    When Customer makes "PAYOUT_REFUND" transaction with "AUTOPAY" payment type on "9 May 2023" with 300 EUR transaction amount and system-generated Idempotency key
    When Admin sets the business date to "10 May 2023"
    And Admin makes "REPAYMENT_ADJUSTMENT_CHARGEBACK" chargeback with 300 EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date           | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 08 May 2023    |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 31   | 08 June 2023   |           | 667.0           | 633.0         | 0.0      | 0.0  | 0.0       | 633.0 | 300.0 | 300.0      | 0.0  | 333.0       |
      | 2  | 30   | 08 July 2023   |           | 334.0           | 333.0         | 0.0      | 0.0  | 0.0       | 333.0 | 0.0   | 0.0        | 0.0  | 333.0       |
      | 3  | 31   | 08 August 2023 |           | 0.0             | 334.0         | 0.0      | 0.0  | 0.0       | 334.0 | 0.0   | 0.0        | 0.0  | 334.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due  | Paid | In advance | Late | Outstanding |
      | 1300          | 0        | 0    | 0         | 1300 | 300  | 300        | 0    | 1000        |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 08 May 2023      | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 09 May 2023      | Payout Refund    | 300.0  | 300.0     | 0.0      | 0.0  | 0.0       | 700.0        |
      | 10 May 2023      | Chargeback       | 300.0  | 300.0     | 0.0      | 0.0  | 0.0       | 1000.0       |

  @TestRailId:C2693
  Scenario: As an admin I would like to do a chargeback for Merchant Issued Refund
    When Admin sets the business date to "8 May 2023"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy                        |
      | LP1         | 8 May 2023        | 1000           | 0                      | DECLINING_BALANCE | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | PENALTIES_FEES_INTEREST_PRINCIPAL_ORDER |
    And Admin successfully approves the loan on "8 May 2023" with "1000" amount and expected disbursement date on "8 May 2023"
    And Admin successfully disburse the loan on "8 May 2023" with "1000" EUR transaction amount
    When Admin sets the business date to "9 May 2023"
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "9 May 2023" with 300 EUR transaction amount and system-generated Idempotency key
    When Admin sets the business date to "10 May 2023"
    And Admin makes "REPAYMENT_ADJUSTMENT_CHARGEBACK" chargeback with 300 EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date           | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 08 May 2023    |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 31   | 08 June 2023   |           | 667.0           | 633.0         | 0.0      | 0.0  | 0.0       | 633.0 | 300.0 | 300.0      | 0.0  | 333.0       |
      | 2  | 30   | 08 July 2023   |           | 334.0           | 333.0         | 0.0      | 0.0  | 0.0       | 333.0 | 0.0   | 0.0        | 0.0  | 333.0       |
      | 3  | 31   | 08 August 2023 |           | 0.0             | 334.0         | 0.0      | 0.0  | 0.0       | 334.0 | 0.0   | 0.0        | 0.0  | 334.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due  | Paid | In advance | Late | Outstanding |
      | 1300          | 0        | 0    | 0         | 1300 | 300  | 300        | 0    | 1000        |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 08 May 2023      | Disbursement           | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 09 May 2023      | Merchant Issued Refund | 300.0  | 300.0     | 0.0      | 0.0  | 0.0       | 700.0        |
      | 10 May 2023      | Chargeback             | 300.0  | 300.0     | 0.0      | 0.0  | 0.0       | 1000.0       |

  @TestRailId:C2770
  Scenario: As an admin I would like to do two merchant issued refund and charge adjustment to close the loan
    When Global config "charge-accrual-date" value set to "submitted-date"
    When Admin sets the business date to "14 May 2023"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy                                                             |
      | LP1         | 14 May 2023       | 1000           | 0                      | DECLINING_BALANCE | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 30                | DAYS                  | 30             | DAYS                   | 1                  | 0                       | 0                      | 0                    | DUE_PENALTY_FEE_INTEREST_PRINCIPAL_IN_ADVANCE_PRINCIPAL_PENALTY_FEE_INTEREST |
    And Admin successfully approves the loan on "14 May 2023" with "127.95" amount and expected disbursement date on "14 May 2023"
    And Admin successfully disburse the loan on "14 May 2023" with "127.95" EUR transaction amount
    When Admin sets the business date to "11 June 2023"
    When Batch API call with steps: rescheduleLoan from "13 June 2023" to "13 July 2023" submitted on date: "11 June 2023", approveReschedule on date: "11 June 2023" runs with enclosingTransaction: "true"
    When Admin adds "LOAN_SNOOZE_FEE" due date charge with "13 July 2023" due date and 3.65 EUR transaction amount
    When Admin sets the business date to "12 June 2023"
    When Admin runs inline COB job for Loan
    Then Loan Repayment schedule has 1 periods, with the following data for periods:
      | Nr | Days | Date         | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 14 May 2023  |           | 127.95          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 60   | 13 July 2023 |           | 0.0             | 127.95        | 0.0      | 3.65 | 0.0       | 131.6 | 0.0  | 0.0        | 0.0  | 131.6       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 127.95        | 0        | 3.65 | 0         | 131.60 | 0    | 0          | 0    | 131.60      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 14 May 2023      | Disbursement     | 127.95 | 0.0       | 0.0      | 0.0  | 0.0       | 127.95       |
      | 11 June 2023     | Accrual          | 3.65   | 0.0       | 0.0      | 3.65 | 0.0       | 0.0          |
    When Admin sets the business date to "17 June 2023"
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "17 June 2023" with 125 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 1 periods, with the following data for periods:
      | Nr | Days | Date         | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 14 May 2023  |           | 127.95          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 60   | 13 July 2023 |           | 0.0             | 127.95        | 0.0      | 3.65 | 0.0       | 131.6 | 125.0 | 125.0      | 0.0  | 6.6         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 127.95        | 0        | 3.65 | 0         | 131.6 | 125.0 | 125.0      | 0    | 6.60        |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 14 May 2023      | Disbursement           | 127.95 | 0.0       | 0.0      | 0.0  | 0.0       | 127.95       |
      | 11 June 2023     | Accrual                | 3.65   | 0.0       | 0.0      | 3.65 | 0.0       | 0.0          |
      | 17 June 2023     | Merchant Issued Refund | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 2.95         |
    When Admin makes a charge adjustment for the last "LOAN_SNOOZE_FEE" type charge which is due on "13 July 2023" with 3.65 EUR transaction amount and externalId ""
    Then Loan Repayment schedule has 1 periods, with the following data for periods:
      | Nr | Days | Date         | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid   | In advance | Late | Outstanding |
      |    |      | 14 May 2023  |           | 127.95          |               |          | 0.0  |           | 0.0   | 0.0    |            |      |             |
      | 1  | 60   | 13 July 2023 |           | 0.0             | 127.95        | 0.0      | 3.65 | 0.0       | 131.6 | 128.65 | 128.65     | 0.0  | 2.95        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid   | In advance | Late | Outstanding |
      | 127.95        | 0        | 3.65 | 0         | 131.6 | 128.65 | 128.65     | 0    | 2.95        |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 14 May 2023      | Disbursement           | 127.95 | 0.0       | 0.0      | 0.0  | 0.0       | 127.95       |
      | 11 June 2023     | Accrual                | 3.65   | 0.0       | 0.0      | 3.65 | 0.0       | 0.0          |
      | 17 June 2023     | Merchant Issued Refund | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 2.95         |
      | 17 June 2023     | Charge Adjustment      | 3.65   | 2.95      | 0.0      | 0.7  | 0.0       | 0.0          |
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "17 June 2023" with 2.95 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 1 periods, with the following data for periods:
      | Nr | Days | Date         | Paid date    | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 14 May 2023  |              | 127.95          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 60   | 13 July 2023 | 17 June 2023 | 0.0             | 127.95        | 0.0      | 3.65 | 0.0       | 131.6 | 131.6 | 131.6      | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 127.95        | 0        | 3.65 | 0         | 131.6 | 131.6 | 131.6      | 0    | 0           |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 14 May 2023      | Disbursement           | 127.95 | 0.0       | 0.0      | 0.0  | 0.0       | 127.95       |
      | 11 June 2023     | Accrual                | 3.65   | 0.0       | 0.0      | 3.65 | 0.0       | 0.0          |
      | 17 June 2023     | Merchant Issued Refund | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 2.95         |
      | 17 June 2023     | Charge Adjustment      | 3.65   | 2.95      | 0.0      | 0.7  | 0.0       | 0.0          |
      | 17 June 2023     | Merchant Issued Refund | 2.95   | 0.0       | 0.0      | 2.95 | 0.0       | 0.0          |
    When Global config "charge-accrual-date" value set to "due-date"

  @TestRailId:C2776
  Scenario: Verify that maturity date is updated on repayment reversal
    When Admin sets the business date to "01 June 2023"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "01 June 2023"
    And Admin successfully approves the loan on "01 June 2023" with "1000" amount and expected disbursement date on "01 June 2023"
    When Admin successfully disburse the loan on "01 June 2023" with "1000" EUR transaction amount
    Then Loan status will be "ACTIVE"
    Then Loan has the following maturity data:
      | actualMaturityDate | expectedMaturityDate |
      | 01 July 2023       | 01 July 2023         |
    When Admin sets the business date to "20 June 2023"
    And Customer makes "AUTOPAY" repayment on "20 June 2023" with 1000 EUR transaction amount
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan has the following maturity data:
      | actualMaturityDate | expectedMaturityDate |
      | 20 June 2023       | 01 July 2023         |
    When Admin sets the business date to "20 June 2023"
    When Customer undo "1"th "Repayment" transaction made on "20 June 2023"
    Then Loan status will be "ACTIVE"
    Then Loan has the following maturity data:
      | actualMaturityDate | expectedMaturityDate |
      | 01 July 2023       | 01 July 2023         |

  @TestRailId:C3202
  Scenario: Verify that closed date is updated on repayment reversal
    When Admin sets the business date to "01 June 2024"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "01 June 2024"
    And Admin successfully approves the loan on "01 June 2024" with "1000" amount and expected disbursement date on "01 June 2024"
    When Admin successfully disburse the loan on "01 June 2024" with "1000" EUR transaction amount
    Then Loan status will be "ACTIVE"
    When Admin sets the business date to "20 June 2024"
    And Customer makes "AUTOPAY" repayment on "20 June 2024" with 1000 EUR transaction amount
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan closedon_date is "20 June 2024"
    When Admin sets the business date to "21 June 2024"
    When Customer undo "1"th "Repayment" transaction made on "20 June 2024"
    Then Loan status will be "ACTIVE"
    Then Loan closedon_date is "null"

  @TestRailId:C2777
  Scenario: As an admin I would like to delete a loan using external id
    When Admin sets the business date to the actual date
    And Admin creates a client with random data
    When Admin creates a new Loan
    Then Admin successfully deletes the loan with external id

  @TestRailId:C2778
  Scenario: As an admin I would like to verify that deleting loan using incorrect external id gives error
    When Admin sets the business date to the actual date
    And Admin creates a client with random data
    When Admin creates a new Loan
    Then Admin fails to delete the loan with incorrect external id

  @TestRailId:C2784
  Scenario: As a user I would like to do multiple repayment after reverse transactions and check the order of transactions
    When Admin sets the business date to "01 November 2022"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy                                                             |
      | LP1         | 01 November 2022  | 1000           | 0                      | DECLINING_BALANCE | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 30                | DAYS                  | 30             | DAYS                   | 1                  | 0                       | 0                      | 0                    | DUE_PENALTY_FEE_INTEREST_PRINCIPAL_IN_ADVANCE_PRINCIPAL_PENALTY_FEE_INTEREST |
    And Admin successfully approves the loan on "01 November 2022" with "1000" amount and expected disbursement date on "01 November 2022"
    When Admin successfully disburse the loan on "01 November 2022" with "1000" EUR transaction amount
    Then Loan has 1000 outstanding amount
    When Admin adds "LOAN_NSF_FEE" due date charge with "2 November 2022" due date and 10 EUR transaction amount
    When Admin sets the business date to "02 November 2022"
    And Customer makes "AUTOPAY" repayment on "02 November 2022" with 9 EUR transaction amount
    And Customer makes "AUTOPAY" repayment on "02 November 2022" with 8 EUR transaction amount
    And Customer makes "AUTOPAY" repayment on "02 November 2022" with 7 EUR transaction amount
    Then Loan Transactions tab has a transaction with date: "02 November 2022", and with the following data:
      | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | Repayment        | 9.0    | 0.0       | 0.0      | 0.0  | 9.0       | 1000.0       |
      | Repayment        | 8.0    | 7.0       | 0.0      | 0.0  | 1.0       | 993.0        |
      | Repayment        | 7.0    | 7.0       | 0.0      | 0.0  | 0.0       | 986.0        |
    When Customer undo "1"th repayment on "02 November 2022"
    Then Loan Transactions tab has a transaction with date: "02 November 2022", and with the following data:
      | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | Repayment        | 9.0    | 0.0       | 0.0      | 0.0  | 9.0       | 1000.0       |
      | Repayment        | 8.0    | 0.0       | 0.0      | 0.0  | 8.0       | 1000.0       |
      | Repayment        | 7.0    | 5.0       | 0.0      | 0.0  | 2.0       | 993.0        |
    When Customer undo "2"th repayment on "02 November 2022"
    Then Loan Transactions tab has a transaction with date: "02 November 2022", and with the following data:
      | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | Repayment        | 9.0    | 0.0       | 0.0      | 0.0  | 9.0       | 1000.0       |
      | Repayment        | 8.0    | 0.0       | 0.0      | 0.0  | 8.0       | 1000.0       |
      | Repayment        | 7.0    | 0.0       | 0.0      | 0.0  | 7.0       | 1000.0       |

  @TestRailId:C2783
  Scenario: As an admin I would like to verify that only one active repayment schedule exits for loan multiple disbursement
    When Admin sets the business date to "07 July 2023"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy                        |
      | LP1         | 07 July 2023      | 1000           | 0                      | DECLINING_BALANCE | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 30                | DAYS                  | 30             | DAYS                   | 1                  | 0                       | 0                      | 0                    | PENALTIES_FEES_INTEREST_PRINCIPAL_ORDER |
    And Admin successfully approves the loan on "07 July 2023" with "1000" amount and expected disbursement date on "07 July 2023"
    And Admin successfully disburse the loan on "07 July 2023" with "370.55" EUR transaction amount
    When Admin sets the business date to "12 July 2023"
    When Admin adds "LOAN_SNOOZE_FEE" due date charge with "11 July 2023" due date and 5.15 EUR transaction amount
    When Admin runs inline COB job for Loan
    Then Loan Repayment schedule has 1 periods, with the following data for periods:
      | Nr | Days | Date           | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 07 July 2023   |           | 370.55          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 30   | 06 August 2023 |           | 0.0             | 370.55        | 0.0      | 5.15 | 0.0       | 375.7 | 0.0  | 0.0        | 0.0  | 375.7       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 370.55        | 0        | 5.15 | 0         | 375.70 | 0    | 0          | 0    | 375.70      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 07 July 2023     | Disbursement     | 370.55 | 0.0       | 0.0      | 0.0  | 0.0       | 370.55       |
      | 11 July 2023     | Accrual          | 5.15   | 0.0       | 0.0      | 5.15 | 0.0       | 0.0          |
    When Admin sets the business date to "21 July 2023"
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "21 July 2023" with 167.4 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 1 periods, with the following data for periods:
      | Nr | Days | Date           | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 07 July 2023   |           | 370.55          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 30   | 06 August 2023 |           | 0.0             | 370.55        | 0.0      | 5.15 | 0.0       | 375.7 | 167.4 | 167.4      | 0.0  | 208.3       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 370.55        | 0        | 5.15 | 0         | 375.7 | 167.4 | 167.4      | 0    | 208.3       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 07 July 2023     | Disbursement           | 370.55 | 0.0       | 0.0      | 0.0  | 0.0       | 370.55       |
      | 11 July 2023     | Accrual                | 5.15   | 0.0       | 0.0      | 5.15 | 0.0       | 0.0          |
      | 21 July 2023     | Merchant Issued Refund | 167.4  | 162.25    | 0.0      | 5.15 | 0.0       | 208.3        |
    When Admin runs inline COB job for Loan
    When Admin sets the business date to "24 July 2023"
    And Admin successfully disburse the loan on "24 July 2023" with "18" EUR transaction amount
    Then Loan Repayment schedule has 1 periods, with the following data for periods:
      | Nr | Days | Date           | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 07 July 2023   |           | 370.55          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      |    |      | 24 July 2023   |           | 18.0            |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 30   | 06 August 2023 |           | 0.0             | 388.55        | 0.0      | 5.15 | 0.0       | 393.7 | 167.4 | 167.4      | 0.0  | 226.3       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 388.55        | 0        | 5.15 | 0         | 393.7 | 167.4 | 167.4      | 0    | 226.3       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 07 July 2023     | Disbursement           | 370.55 | 0.0       | 0.0      | 0.0  | 0.0       | 370.55       |
      | 11 July 2023     | Accrual                | 5.15   | 0.0       | 0.0      | 5.15 | 0.0       | 0.0          |
      | 21 July 2023     | Merchant Issued Refund | 167.4  | 162.25    | 0.0      | 5.15 | 0.0       | 208.3        |
      | 24 July 2023     | Disbursement           | 18.0   | 0.0       | 0.0      | 0.0  | 0.0       | 226.3        |

  @TestRailId:C2842 @AdvancedPaymentAllocation
  Scenario: As an admin I would like to verify that simple payments are working with advanced payment allocation (UC1)
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin sets the business date to "01 January 2023"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 January 2023   | 500            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2023" with "500" amount and expected disbursement date on "01 January 2023"
    Then Loan status has changed to "Approved"
    And Admin successfully disburse the loan on "01 January 2023" with "500" EUR transaction amount
    Then Loan status has changed to "Active"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  |                 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 125.0 | 0          | 0    | 375.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
    When Admin sets the business date to "16 January 2023"
    And Customer makes "AUTOPAY" repayment on "16 January 2023" with 125 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  | 16 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 250.0 | 0          | 0    | 250.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
      | 16 January 2023  | Repayment        | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 250.0        |
    When Admin sets the business date to "31 January 2023"
    And Customer makes "AUTOPAY" repayment on "31 January 2023" with 125 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  | 16 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 31 January 2023  | 31 January 2023 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 375.0 | 0          | 0    | 125.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
      | 16 January 2023  | Repayment        | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 250.0        |
      | 31 January 2023  | Repayment        | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 125.0        |
    When Admin sets the business date to "15 February 2023"
    And Customer makes "AUTOPAY" repayment on "15 February 2023" with 125 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                  | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023  | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  | 16 January 2023  | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 31 January 2023  | 31 January 2023  | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 4  | 15   | 15 February 2023 | 15 February 2023 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 500.0 | 0          | 0    | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
      | 16 January 2023  | Repayment        | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 250.0        |
      | 31 January 2023  | Repayment        | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 125.0        |
      | 15 February 2023 | Repayment        | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 0.0          |
    Then Loan status has changed to "Closed (obligations met)"

  @TestRailId:C2843 @AdvancedPaymentAllocation
  Scenario: As an admin I would like to verify that simple payments and overpayment of the installment (goes to next installment) are working with advanced payment allocation (UC2)
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin sets the business date to "01 January 2023"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 January 2023   | 500            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2023" with "500" amount and expected disbursement date on "01 January 2023"
    Then Loan status has changed to "Approved"
    And Admin successfully disburse the loan on "01 January 2023" with "500" EUR transaction amount
    Then Loan status has changed to "Active"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  |                 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 125.0 | 0          | 0    | 375.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
    When Admin sets the business date to "16 January 2023"
    And Customer makes "AUTOPAY" repayment on "16 January 2023" with 150 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  | 16 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 25.0  | 25.0       | 0.0  | 100.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 275.0 | 25.0       | 0    | 225.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
      | 16 January 2023  | Repayment        | 150.0  | 150.0     | 0.0      | 0.0  | 0.0       | 225.0        |
    When Admin sets the business date to "31 January 2023"
    And Customer makes "AUTOPAY" repayment on "31 January 2023" with 125 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  | 16 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 31 January 2023  | 31 January 2023 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 25.0       | 0.0  | 0.0         |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 25.0  | 25.0       | 0.0  | 100.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 400.0 | 50.0       | 0    | 100.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
      | 16 January 2023  | Repayment        | 150.0  | 150.0     | 0.0      | 0.0  | 0.0       | 225.0        |
      | 31 January 2023  | Repayment        | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 100.0        |
    When Admin sets the business date to "15 February 2023"
    And Customer makes "AUTOPAY" repayment on "15 February 2023" with 100 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                  | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023  | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  | 16 January 2023  | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 31 January 2023  | 31 January 2023  | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 25.0       | 0.0  | 0.0         |
      | 4  | 15   | 15 February 2023 | 15 February 2023 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 25.0       | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 500.0 | 50.0       | 0    | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
      | 16 January 2023  | Repayment        | 150.0  | 150.0     | 0.0      | 0.0  | 0.0       | 225.0        |
      | 31 January 2023  | Repayment        | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 100.0        |
      | 15 February 2023 | Repayment        | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 0.0          |
    Then Loan status has changed to "Closed (obligations met)"

  @TestRailId:C2844 @AdvancedPaymentAllocation
  Scenario: As an admin I would like to verify that simple payments and overpayment of the installment (goes to last installment) are working with advanced payment allocation (UC3)
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin sets the business date to "01 January 2023"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 January 2023   | 500            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2023" with "500" amount and expected disbursement date on "01 January 2023"
    Then Loan status has changed to "Approved"
    And Admin successfully disburse the loan on "01 January 2023" with "500" EUR transaction amount
    Then Loan status has changed to "Active"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  |                 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 125.0 | 0          | 0    | 375.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
    When Admin sets the business date to "16 January 2023"
    And Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "16 January 2023" with 150 EUR transaction amount and self-generated Idempotency key
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  | 16 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 25.0  | 25.0       | 0.0  | 100.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 275.0 | 25.0       | 0    | 225.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
      | 16 January 2023  | Goodwill Credit  | 150.0  | 150.0     | 0.0      | 0.0  | 0.0       | 225.0        |
    When Admin sets the business date to "31 January 2023"
    And Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "31 January 2023" with 125 EUR transaction amount and self-generated Idempotency key
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  | 16 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 31 January 2023  | 31 January 2023 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 25.0  | 25.0       | 0.0  | 100.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 400.0 | 25.0       | 0    | 100.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
      | 16 January 2023  | Goodwill Credit  | 150.0  | 150.0     | 0.0      | 0.0  | 0.0       | 225.0        |
      | 31 January 2023  | Goodwill Credit  | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 100.0        |
    When Admin sets the business date to "15 February 2023"
    And Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "15 February 2023" with 100 EUR transaction amount and self-generated Idempotency key
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                  | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023  | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  | 16 January 2023  | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 31 January 2023  | 31 January 2023  | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 4  | 15   | 15 February 2023 | 15 February 2023 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 25.0       | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 500.0 | 25.0       | 0    | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
      | 16 January 2023  | Goodwill Credit  | 150.0  | 150.0     | 0.0      | 0.0  | 0.0       | 225.0        |
      | 31 January 2023  | Goodwill Credit  | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 100.0        |
      | 15 February 2023 | Goodwill Credit  | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 0.0          |
    Then Loan status has changed to "Closed (obligations met)"

  @TestRailId:C2845 @AdvancedPaymentAllocation
  Scenario: As an admin I would like to verify that simple payments are working after some of them failed with advanced payment allocation (UC4)
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin sets the business date to "01 January 2023"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 January 2023   | 500            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2023" with "500" amount and expected disbursement date on "01 January 2023"
    Then Loan status has changed to "Approved"
    And Admin successfully disburse the loan on "01 January 2023" with "500" EUR transaction amount
    Then Loan status has changed to "Active"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  |                 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 125.0 | 0          | 0    | 375.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
    When Customer undo "2"th transaction made on "01 January 2023"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |           | 500.0           |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 January 2023  |           | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 2  | 15   | 16 January 2023  |           | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 3  | 15   | 31 January 2023  |           | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |           | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 0.0  | 0          | 0    | 500.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
    When Admin sets the business date to "16 January 2023"
    And Customer makes "AUTOPAY" repayment on "16 January 2023" with 125 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 January 2023  | 16 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  |                 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0   | 125.0       |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0   | 125.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0   | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 125.0 | 0          | 125.0 | 375.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 16 January 2023  | Repayment        | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | false    |
    When Customer undo "1"th transaction made on "16 January 2023"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |           | 500.0           |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 January 2023  |           | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 2  | 15   | 16 January 2023  |           | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 3  | 15   | 31 January 2023  |           | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |           | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 0.0  | 0          | 0    | 500.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 16 January 2023  | Repayment        | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
    When Admin sets the business date to "20 January 2023"
    And Customer makes "AUTOPAY" repayment on "20 January 2023" with 100 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |           | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 January 2023  |           | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 100.0 | 0.0        | 100.0 | 25.0        |
      | 2  | 15   | 16 January 2023  |           | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0   | 125.0       |
      | 3  | 15   | 31 January 2023  |           | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0   | 125.0       |
      | 4  | 15   | 15 February 2023 |           | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0   | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 100.0 | 0          | 100.0 | 400.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 16 January 2023  | Repayment        | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 20 January 2023  | Repayment        | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 400.0        | false    |
    When Admin sets the business date to "31 January 2023"
    And Customer makes "AUTOPAY" repayment on "31 January 2023" with 40 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 January 2023  | 31 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  |                 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 15.0  | 0.0        | 15.0  | 110.0       |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0   | 125.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0   | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 140.0 | 0          | 140.0 | 360.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 16 January 2023  | Repayment        | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 20 January 2023  | Repayment        | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 400.0        | false    |
      | 31 January 2023  | Repayment        | 40.0   | 40.0      | 0.0      | 0.0  | 0.0       | 360.0        | false    |
    When Admin sets the business date to "15 February 2023"
    And Customer makes "AUTOPAY" repayment on "15 February 2023" with 360 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                  | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 January 2023  | 31 January 2023  | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  | 15 February 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 3  | 15   | 31 January 2023  | 15 February 2023 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 4  | 15   | 15 February 2023 | 15 February 2023 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0   | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 500.0 | 0          | 375.0 | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 16 January 2023  | Repayment        | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 20 January 2023  | Repayment        | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 400.0        | false    |
      | 31 January 2023  | Repayment        | 40.0   | 40.0      | 0.0      | 0.0  | 0.0       | 360.0        | false    |
      | 15 February 2023 | Repayment        | 360.0  | 360.0     | 0.0      | 0.0  | 0.0       | 0.0          | false    |
    Then Loan status has changed to "Closed (obligations met)"

  @TestRailId:C2846 @AdvancedPaymentAllocation
  Scenario: As an admin I would like to verify that Merchant issued refund with reamortization works with advanced payment allocation (UC05)
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin sets the business date to "01 January 2023"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 January 2023   | 500            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2023" with "500" amount and expected disbursement date on "01 January 2023"
    Then Loan status has changed to "Approved"
    And Admin successfully disburse the loan on "01 January 2023" with "500" EUR transaction amount
    Then Loan status has changed to "Active"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  |                 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 125.0 | 0          | 0    | 375.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
    When Customer undo "2"th transaction made on "01 January 2023"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |           | 500.0           |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 January 2023  |           | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 2  | 15   | 16 January 2023  |           | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 3  | 15   | 31 January 2023  |           | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |           | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 0.0  | 0          | 0    | 500.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
    When Admin sets the business date to "08 January 2023"
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "08 January 2023" with 300 EUR transaction amount and self-generated Idempotency key
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 January 2023  | 08 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  |                 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 58.33 | 58.33      | 0.0   | 66.67       |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 58.33 | 58.33      | 0.0   | 66.67       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 58.34 | 58.34      | 0.0   | 66.66       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 300.0 | 175.0      | 125.0 | 200.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 08 January 2023  | Merchant Issued Refund | 300.0  | 300.0     | 0.0      | 0.0  | 0.0       | 200.0        | false    |
    When Admin sets the business date to "16 January 2023"
    And Customer makes "AUTOPAY" repayment on "16 January 2023" with 201 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 January 2023  | 08 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  | 16 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 58.33      | 0.0   | 0.0         |
      | 3  | 15   | 31 January 2023  | 16 January 2023 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 125.0      | 0.0   | 0.0         |
      | 4  | 15   | 15 February 2023 | 16 January 2023 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 125.0      | 0.0   | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 500.0 | 308.33     | 125.0 | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Overpayment |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    | 0.0         |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     | 0.0         |
      | 08 January 2023  | Merchant Issued Refund | 300.0  | 300.0     | 0.0      | 0.0  | 0.0       | 200.0        | false    | 0.0         |
      | 16 January 2023  | Repayment              | 201.0  | 200.0     | 0.0      | 0.0  | 0.0       | 0.0          | false    | 1.0         |
    Then Loan status has changed to "Overpaid"

  @TestRailId:C2847 @AdvancedPaymentAllocation
  Scenario: As an admin I would like to verify that Merchant issued refund with reamortization on due date works with advanced payment allocation (UC07)
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin sets the business date to "01 January 2023"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 January 2023   | 500            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2023" with "500" amount and expected disbursement date on "01 January 2023"
    Then Loan status has changed to "Approved"
    And Admin successfully disburse the loan on "01 January 2023" with "500" EUR transaction amount
    Then Loan status has changed to "Active"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  |                 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 125.0 | 0          | 0    | 375.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
    When Admin sets the business date to "16 January 2023"
    And Customer makes "AUTOPAY" repayment on "16 January 2023" with 125 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  | 16 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 250.0 | 0.0        | 0.0  | 250.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
      | 16 January 2023  | Repayment        | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 250.0        |
    When Admin sets the business date to "16 January 2023"
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "16 January 2023" with 200 EUR transaction amount and self-generated Idempotency key
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  | 16 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 100.0 | 100.0      | 0.0  | 25.0        |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 100.0 | 100.0      | 0.0  | 25.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 450.0 | 200.0      | 0.0  | 50.0        |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
      | 16 January 2023  | Repayment              | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 250.0        |
      | 16 January 2023  | Merchant Issued Refund | 200.0  | 200.0     | 0.0      | 0.0  | 0.0       | 50.0         |
    When Admin sets the business date to "31 January 2023"
    And Customer makes "AUTOPAY" repayment on "31 January 2023" with 25 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  | 16 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 31 January 2023  | 31 January 2023 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 100.0      | 0.0  | 0.0         |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 100.0 | 100.0      | 0.0  | 25.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 475.0 | 200.0      | 0.0  | 25.0        |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
      | 16 January 2023  | Repayment              | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 250.0        |
      | 16 January 2023  | Merchant Issued Refund | 200.0  | 200.0     | 0.0      | 0.0  | 0.0       | 50.0         |
      | 31 January 2023  | Repayment              | 25.0   | 25.0      | 0.0      | 0.0  | 0.0       | 25.0         |
    When Admin sets the business date to "15 February 2023"
    And Customer makes "AUTOPAY" repayment on "15 February 2023" with 25 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                  | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023  | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  | 16 January 2023  | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 31 January 2023  | 31 January 2023  | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 100.0      | 0.0  | 0.0         |
      | 4  | 15   | 15 February 2023 | 15 February 2023 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 100.0      | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 500.0 | 200.0      | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
      | 16 January 2023  | Repayment              | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 250.0        |
      | 16 January 2023  | Merchant Issued Refund | 200.0  | 200.0     | 0.0      | 0.0  | 0.0       | 50.0         |
      | 31 January 2023  | Repayment              | 25.0   | 25.0      | 0.0      | 0.0  | 0.0       | 25.0         |
      | 15 February 2023 | Repayment              | 25.0   | 25.0      | 0.0      | 0.0  | 0.0       | 0.0          |
    Then Loan status has changed to "Closed (obligations met)"

  @TestRailId:C2848 @AdvancedPaymentAllocation
  Scenario: As an admin I would like to verify that Merchant issued refund with reamortization past due date works with advanced payment allocation (UC08)
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin sets the business date to "01 January 2023"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 January 2023   | 500            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2023" with "500" amount and expected disbursement date on "01 January 2023"
    Then Loan status has changed to "Approved"
    And Admin successfully disburse the loan on "01 January 2023" with "500" EUR transaction amount
    Then Loan status has changed to "Active"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  |                 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 125.0 | 0          | 0    | 375.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
    When Customer undo "2"th transaction made on "01 January 2023"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |           | 500.0           |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 January 2023  |           | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 2  | 15   | 16 January 2023  |           | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 3  | 15   | 31 January 2023  |           | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |           | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 0.0  | 0          | 0    | 500.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
    When Admin sets the business date to "16 January 2023"
    And Customer makes "AUTOPAY" repayment on "16 January 2023" with 125 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 January 2023  | 16 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  |                 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0   | 125.0       |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0   | 125.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0   | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 125.0 | 0          | 125.0 | 375.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 16 January 2023  | Repayment        | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | false    |
    When Customer undo "1"th transaction made on "16 January 2023"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |           | 500.0           |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 January 2023  |           | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 2  | 15   | 16 January 2023  |           | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 3  | 15   | 31 January 2023  |           | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |           | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 0.0  | 0          | 0    | 500.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 16 January 2023  | Repayment        | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
    When Admin sets the business date to "17 January 2023"
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "17 January 2023" with 300 EUR transaction amount and self-generated Idempotency key
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 January 2023  | 17 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  | 17 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 25.0  | 25.0       | 0.0   | 100.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 25.0  | 25.0       | 0.0   | 100.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 300.0 | 50.0       | 250.0 | 200.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 16 January 2023  | Repayment              | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 17 January 2023  | Merchant Issued Refund | 300.0  | 300.0     | 0.0      | 0.0  | 0.0       | 200.0        | false    |
    When Admin sets the business date to "31 January 2023"
    And Customer makes "AUTOPAY" repayment on "31 January 2023" with 100 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 January 2023  | 17 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  | 17 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 3  | 15   | 31 January 2023  | 31 January 2023 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 25.0       | 0.0   | 0.0         |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 25.0  | 25.0       | 0.0   | 100.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 400.0 | 50.0       | 250.0 | 100.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 16 January 2023  | Repayment              | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 17 January 2023  | Merchant Issued Refund | 300.0  | 300.0     | 0.0      | 0.0  | 0.0       | 200.0        | false    |
      | 31 January 2023  | Repayment              | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 100.0        | false    |
    When Admin sets the business date to "15 February 2023"
    And Customer makes "AUTOPAY" repayment on "15 February 2023" with 100 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                  | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 January 2023  | 17 January 2023  | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  | 17 January 2023  | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 3  | 15   | 31 January 2023  | 31 January 2023  | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 25.0       | 0.0   | 0.0         |
      | 4  | 15   | 15 February 2023 | 15 February 2023 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 25.0       | 0.0   | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 500.0 | 50.0       | 250.0 | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 16 January 2023  | Repayment              | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 17 January 2023  | Merchant Issued Refund | 300.0  | 300.0     | 0.0      | 0.0  | 0.0       | 200.0        | false    |
      | 31 January 2023  | Repayment              | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 100.0        | false    |
      | 15 February 2023 | Repayment              | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 0.0          | false    |
    Then Loan status has changed to "Closed (obligations met)"

  @TestRailId:C2849 @AdvancedPaymentAllocation
  Scenario: As an admin I would like to verify that full refund with CBR (UC17)
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin sets the business date to "01 January 2023"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 January 2023   | 500            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2023" with "500" amount and expected disbursement date on "01 January 2023"
    Then Loan status has changed to "Approved"
    And Admin successfully disburse the loan on "01 January 2023" with "500" EUR transaction amount
    Then Loan status has changed to "Active"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  |                 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 125.0 | 0          | 0    | 375.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
    When Admin sets the business date to "08 January 2023"
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "08 January 2023" with 500 EUR transaction amount and self-generated Idempotency key
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  | 08 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 125.0      | 0.0  | 0.0         |
      | 3  | 15   | 31 January 2023  | 08 January 2023 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 125.0      | 0.0  | 0.0         |
      | 4  | 15   | 15 February 2023 | 08 January 2023 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 125.0      | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 500.0 | 375.0      | 0    | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Overpayment |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | 0.0         |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | 0.0         |
      | 08 January 2023  | Merchant Issued Refund | 500.0  | 375.0     | 0.0      | 0.0  | 0.0       | 0.0          | 125.0       |
    Then Loan status has changed to "Overpaid"
    When Admin sets the business date to "09 January 2023"
    When Admin makes Credit Balance Refund transaction on "09 January 2023" with 125 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  | 08 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 125.0      | 0.0  | 0.0         |
      | 3  | 15   | 31 January 2023  | 08 January 2023 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 125.0      | 0.0  | 0.0         |
      | 4  | 15   | 15 February 2023 | 08 January 2023 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 125.0      | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 500.0 | 375.0      | 0    | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
      | 08 January 2023  | Merchant Issued Refund | 500.0  | 375.0     | 0.0      | 0.0  | 0.0       | 0.0          |
      | 09 January 2023  | Credit Balance Refund  | 125.0  | 0.0       | 0.0      | 0.0  | 0.0       | 0.0          |
    Then Loan status has changed to "Closed (obligations met)"

  @TestRailId:C2850 @AdvancedPaymentAllocation
  Scenario: As an admin I would like to verify that reverse-replay works with advanced payment allocation(UC24)
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin sets the business date to "01 January 2023"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 January 2023   | 500            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2023" with "500" amount and expected disbursement date on "01 January 2023"
    Then Loan status has changed to "Approved"
    And Admin successfully disburse the loan on "01 January 2023" with "500" EUR transaction amount
    Then Loan status has changed to "Active"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  |                 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 125.0 | 0          | 0    | 375.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
    When Customer undo "2"th transaction made on "01 January 2023"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |           | 500.0           |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 January 2023  |           | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 2  | 15   | 16 January 2023  |           | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 3  | 15   | 31 January 2023  |           | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |           | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 0.0  | 0          | 0    | 500.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
    When Admin sets the business date to "02 January 2023"
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "02 January 2023" with 400 EUR transaction amount and self-generated Idempotency key
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 January 2023  | 02 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  |                 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 91.67 | 91.67      | 0.0   | 33.33       |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 91.67 | 91.67      | 0.0   | 33.33       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 91.66 | 91.66      | 0.0   | 33.34       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 400.0 | 275.0      | 125.0 | 100.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
      | 02 January 2023  | Merchant Issued Refund | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 100.0        |
    When Admin sets the business date to "04 January 2023"
    And Customer makes "AUTOPAY" repayment on "04 January 2023" with 50 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid   | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0    |            |       |             |
      | 1  | 0    | 01 January 2023  | 02 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0  | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  | 04 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0  | 125.0      | 0.0   | 0.0         |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 108.34 | 108.34     | 0.0   | 16.66       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 91.66  | 91.66      | 0.0   | 33.34       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 450.0 | 325.0      | 125.0 | 50.0        |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
      | 02 January 2023  | Merchant Issued Refund | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 100.0        |
      | 04 January 2023  | Repayment              | 50.0   | 50.0      | 0.0      | 0.0  | 0.0       | 50.0         |
    When Admin sets the business date to "16 January 2023"
    And Customer makes "AUTOPAY" repayment on "16 January 2023" with 125 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 January 2023  | 02 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  | 04 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 125.0      | 0.0   | 0.0         |
      | 3  | 15   | 31 January 2023  | 16 January 2023 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 125.0      | 0.0   | 0.0         |
      | 4  | 15   | 15 February 2023 | 16 January 2023 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 125.0      | 0.0   | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 500.0 | 375.0      | 125.0 | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Overpayment | Reverted |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | 0.0         | false    |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | 0.0         | true     |
      | 02 January 2023  | Merchant Issued Refund | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 100.0        | 0.0         | false    |
      | 04 January 2023  | Repayment              | 50.0   | 50.0      | 0.0      | 0.0  | 0.0       | 50.0         | 0.0         | false    |
      | 16 January 2023  | Repayment              | 125.0  | 50.0      | 0.0      | 0.0  | 0.0       | 0.0          | 75.0        | false    |
    Then Loan status has changed to "Overpaid"
    When Admin sets the business date to "18 January 2023"
    When Admin makes Credit Balance Refund transaction on "18 January 2023" with 75 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 January 2023  | 02 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  | 04 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 125.0      | 0.0   | 0.0         |
      | 3  | 15   | 31 January 2023  | 16 January 2023 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 125.0      | 0.0   | 0.0         |
      | 4  | 15   | 15 February 2023 | 16 January 2023 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 125.0      | 0.0   | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 500.0 | 375.0      | 125.0 | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Overpayment | Reverted |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | 0.0         | false    |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | 0.0         | true     |
      | 02 January 2023  | Merchant Issued Refund | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 100.0        | 0.0         | false    |
      | 04 January 2023  | Repayment              | 50.0   | 50.0      | 0.0      | 0.0  | 0.0       | 50.0         | 0.0         | false    |
      | 16 January 2023  | Repayment              | 125.0  | 50.0      | 0.0      | 0.0  | 0.0       | 0.0          | 75.0        | false    |
      | 18 January 2023  | Credit Balance Refund  | 75.0   | 0.0       | 0.0      | 0.0  | 0.0       | 0.0          | 75.0        | false    |
    Then Loan status has changed to "Closed (obligations met)"
    When Admin sets the business date to "20 January 2023"
    When Customer undo "1"th transaction made on "02 January 2023"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 January 2023  | 16 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  |                 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 50.0  | 0.0        | 0.0   | 75.0        |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 200.0         | 0.0      | 0.0  | 0.0       | 200.0 | 0.0   | 0.0        | 0.0   | 200.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0   | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 575.0         | 0        | 0.0  | 0         | 575.0 | 175.0 | 0.0        | 125.0 | 400.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Overpayment | Reverted |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | 0.0         | false    |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | 0.0         | true     |
      | 02 January 2023  | Merchant Issued Refund | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 100.0        | 0.0         | true     |
      | 04 January 2023  | Repayment              | 50.0   | 50.0      | 0.0      | 0.0  | 0.0       | 450.0        | 0.0         | false    |
      | 16 January 2023  | Repayment              | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 325.0        | 0.0         | false    |
      | 18 January 2023  | Credit Balance Refund  | 75.0   | 75.0      | 0.0      | 0.0  | 0.0       | 400.0        | 0.0         | false    |
    Then Loan status has changed to "Active"
    When Admin sets the business date to "31 January 2023"
    And Customer makes "AUTOPAY" repayment on "31 January 2023" with 275 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 January 2023  | 16 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  | 31 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 75.0  | 0.0         |
      | 3  | 15   | 31 January 2023  | 31 January 2023 | 125.0           | 200.0         | 0.0      | 0.0  | 0.0       | 200.0 | 200.0 | 0.0        | 0.0   | 0.0         |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0   | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 575.0         | 0        | 0.0  | 0         | 575.0 | 450.0 | 0.0        | 200.0 | 125.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Overpayment | Reverted |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | 0.0         | false    |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | 0.0         | true     |
      | 02 January 2023  | Merchant Issued Refund | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 100.0        | 0.0         | true     |
      | 04 January 2023  | Repayment              | 50.0   | 50.0      | 0.0      | 0.0  | 0.0       | 450.0        | 0.0         | false    |
      | 16 January 2023  | Repayment              | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 325.0        | 0.0         | false    |
      | 18 January 2023  | Credit Balance Refund  | 75.0   | 75.0      | 0.0      | 0.0  | 0.0       | 400.0        | 0.0         | false    |
      | 31 January 2023  | Repayment              | 275.0  | 275.0     | 0.0      | 0.0  | 0.0       | 125.0        | 0.0         | false    |
    When Admin sets the business date to "15 February 2023"
    And Customer makes "AUTOPAY" repayment on "15 February 2023" with 125 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                  | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 January 2023  | 16 January 2023  | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  | 31 January 2023  | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 75.0  | 0.0         |
      | 3  | 15   | 31 January 2023  | 31 January 2023  | 125.0           | 200.0         | 0.0      | 0.0  | 0.0       | 200.0 | 200.0 | 0.0        | 0.0   | 0.0         |
      | 4  | 15   | 15 February 2023 | 15 February 2023 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0   | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 575.0         | 0        | 0.0  | 0         | 575.0 | 575.0 | 0.0        | 200.0 | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Overpayment | Reverted |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | 0.0         | false    |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | 0.0         | true     |
      | 02 January 2023  | Merchant Issued Refund | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 100.0        | 0.0         | true     |
      | 04 January 2023  | Repayment              | 50.0   | 50.0      | 0.0      | 0.0  | 0.0       | 450.0        | 0.0         | false    |
      | 16 January 2023  | Repayment              | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 325.0        | 0.0         | false    |
      | 18 January 2023  | Credit Balance Refund  | 75.0   | 75.0      | 0.0      | 0.0  | 0.0       | 400.0        | 0.0         | false    |
      | 31 January 2023  | Repayment              | 275.0  | 275.0     | 0.0      | 0.0  | 0.0       | 125.0        | 0.0         | false    |
      | 15 February 2023 | Repayment              | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 0.0          | 0.0         | false    |
    Then Loan status has changed to "Closed (obligations met)"

  @TestRailId:C2851 @AdvancedPaymentAllocation
  Scenario: As an admin I would like to verify that reamortization works with uneven balances with advanced payment allocation(UC25)
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin sets the business date to "01 January 2023"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 January 2023   | 500            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2023" with "500" amount and expected disbursement date on "01 January 2023"
    Then Loan status has changed to "Approved"
    And Admin successfully disburse the loan on "01 January 2023" with "500" EUR transaction amount
    Then Loan status has changed to "Active"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  |                 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0   | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 125.0 | 0          | 0    | 375.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
    When Customer undo "2"th transaction made on "01 January 2023"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |           | 500.0           |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 January 2023  |           | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 2  | 15   | 16 January 2023  |           | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 3  | 15   | 31 January 2023  |           | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |           | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 0.0  | 0.0        | 0.0  | 125.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 0.0  | 0          | 0    | 500.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
    When Admin sets the business date to "02 January 2023"
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "02 January 2023" with 400 EUR transaction amount and self-generated Idempotency key
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 January 2023  | 02 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  |                 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 91.67 | 91.67      | 0.0   | 33.33       |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 91.67 | 91.67      | 0.0   | 33.33       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 91.66 | 91.66      | 0.0   | 33.34       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 400.0 | 275.0      | 125.0 | 100.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 02 January 2023  | Merchant Issued Refund | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 100.0        | false    |
    When Admin sets the business date to "04 January 2023"
    And Customer makes "AUTOPAY" repayment on "04 January 2023" with 50 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid   | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0    |            |       |             |
      | 1  | 0    | 01 January 2023  | 02 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0  | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  | 04 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0  | 125.0      | 0.0   | 0.0         |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 108.34 | 108.34     | 0.0   | 16.66       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 91.66  | 91.66      | 0.0   | 33.34       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 450.0 | 325.0      | 125.0 | 50.0        |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 02 January 2023  | Merchant Issued Refund | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 100.0        | false    |
      | 04 January 2023  | Repayment              | 50.0   | 50.0      | 0.0      | 0.0  | 0.0       | 50.0         | false    |
    When Admin sets the business date to "06 January 2023"
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "06 January 2023" with 40 EUR transaction amount and self-generated Idempotency key
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 January 2023  | 02 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 125.0 | 0.0         |
      | 2  | 15   | 16 January 2023  | 04 January 2023 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 125.0      | 0.0   | 0.0         |
      | 3  | 15   | 31 January 2023  | 06 January 2023 | 125.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 125.0      | 0.0   | 0.0         |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 115.0 | 115.0      | 0.0   | 10.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 500.0         | 0        | 0.0  | 0         | 500.0 | 490.0 | 365.0      | 125.0 | 10.0        |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2023  | Disbursement           | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    |
      | 01 January 2023  | Down Payment           | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        | true     |
      | 02 January 2023  | Merchant Issued Refund | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 100.0        | false    |
      | 04 January 2023  | Repayment              | 50.0   | 50.0      | 0.0      | 0.0  | 0.0       | 50.0         | false    |
      | 06 January 2023  | Merchant Issued Refund | 40.0   | 40.0      | 0.0      | 0.0  | 0.0       | 10.0         | false    |

  @TestRailId:C2860 @AdvancedPaymentAllocation
  Scenario: Verify advanced payment allocation - future installments: NEXT_INSTALLMENT
    When Admin sets the business date to "01 September 2023"
    And Admin creates a client with random data
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 September 2023 | 400            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "400" amount and expected disbursement date on "01 September 2023"
    And Admin successfully disburse the loan on "01 September 2023" with "400" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 300.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 |                   | 200.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 3  | 15   | 01 October 2023   |                   | 100.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 400.0         | 0        | 0.0  | 0         | 400.0 | 100.0 | 0.0        | 0.0  | 300.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 01 September 2023 | Down Payment     | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 150 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 300.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 200.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 100.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 50.0  | 50.0       | 0.0  | 50.0        |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 400.0         | 0        | 0.0  | 0         | 400.0 | 250.0 | 50.0       | 0.0  | 150.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 01 September 2023 | Down Payment     | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
      | 16 September 2023 | Repayment        | 150.0  | 150.0     | 0.0      | 0.0  | 0.0       | 150.0        |
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule

  @TestRailId:C2861 @AdvancedPaymentAllocation
  Scenario: Verify advanced payment allocation - future installments: LAST_INSTALLMENT, payment on due date
    When Admin sets the business date to "01 September 2023"
    And Admin creates a client with random data
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "LAST_INSTALLMENT" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 September 2023 | 400            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "400" amount and expected disbursement date on "01 September 2023"
    And Admin successfully disburse the loan on "01 September 2023" with "400" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 300.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 |                   | 200.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 3  | 15   | 01 October 2023   |                   | 100.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 400.0         | 0        | 0.0  | 0         | 400.0 | 100.0 | 0.0        | 0.0  | 300.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 01 September 2023 | Down Payment     | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 150 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 300.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 200.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 100.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 50.0  | 50.0       | 0.0  | 50.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 400.0         | 0        | 0.0  | 0         | 400.0 | 250.0 | 50.0       | 0.0  | 150.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 01 September 2023 | Down Payment     | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
      | 16 September 2023 | Repayment        | 150.0  | 150.0     | 0.0      | 0.0  | 0.0       | 150.0        |
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule

  @TestRailId:C2862 @AdvancedPaymentAllocation
  Scenario: Verify advanced payment allocation - future installments: LAST_INSTALLMENT, payment before due date
    When Admin sets the business date to "01 September 2023"
    And Admin creates a client with random data
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "LAST_INSTALLMENT" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 September 2023 | 400            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "400" amount and expected disbursement date on "01 September 2023"
    And Admin successfully disburse the loan on "01 September 2023" with "400" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 300.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 |                   | 200.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 3  | 15   | 01 October 2023   |                   | 100.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 400.0         | 0        | 0.0  | 0         | 400.0 | 100.0 | 0.0        | 0.0  | 300.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 01 September 2023 | Down Payment     | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
    When Admin sets the business date to "10 September 2023"
    And Customer makes "AUTOPAY" repayment on "10 September 2023" with 150 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 300.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 |                   | 200.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 3  | 15   | 01 October 2023   |                   | 100.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 50.0  | 50.0       | 0.0  | 50.0        |
      | 4  | 15   | 16 October 2023   | 10 September 2023 | 0.0             | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 100.0      | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 400.0         | 0        | 0.0  | 0         | 400.0 | 250.0 | 150.0      | 0.0  | 150.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 01 September 2023 | Down Payment     | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
      | 10 September 2023 | Repayment        | 150.0  | 150.0     | 0.0      | 0.0  | 0.0       | 150.0        |
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule

  @TestRailId:C2863 @AdvancedPaymentAllocation
  Scenario: Verify advanced payment allocation - future installments: REAMORTIZATION, payment on due date
    When Admin sets the business date to "01 September 2023"
    And Admin creates a client with random data
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "REAMORTIZATION" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 September 2023 | 400            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "400" amount and expected disbursement date on "01 September 2023"
    And Admin successfully disburse the loan on "01 September 2023" with "400" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 300.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 |                   | 200.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 3  | 15   | 01 October 2023   |                   | 100.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 400.0         | 0        | 0.0  | 0         | 400.0 | 100.0 | 0.0        | 0.0  | 300.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 01 September 2023 | Down Payment     | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 150 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 300.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 200.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 100.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 25.0  | 25.0       | 0.0  | 75.0        |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 25.0  | 25.0       | 0.0  | 75.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 400.0         | 0        | 0.0  | 0         | 400.0 | 250.0 | 50.0       | 0.0  | 150.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 01 September 2023 | Down Payment     | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
      | 16 September 2023 | Repayment        | 150.0  | 150.0     | 0.0      | 0.0  | 0.0       | 150.0        |
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule

  @TestRailId:C2864 @AdvancedPaymentAllocation
  Scenario: Verify advanced payment allocation - future installments: REAMORTIZATION, payment before due date
    When Admin sets the business date to "01 September 2023"
    And Admin creates a client with random data
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "REAMORTIZATION" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 September 2023 | 400            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "400" amount and expected disbursement date on "01 September 2023"
    And Admin successfully disburse the loan on "01 September 2023" with "400" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 300.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 |                   | 200.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 3  | 15   | 01 October 2023   |                   | 100.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 400.0         | 0        | 0.0  | 0         | 400.0 | 100.0 | 0.0        | 0.0  | 300.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 01 September 2023 | Down Payment     | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
    When Admin sets the business date to "10 September 2023"
    And Customer makes "AUTOPAY" repayment on "10 September 2023" with 150 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 300.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 |                   | 200.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 50.0  | 50.0       | 0.0  | 50.0        |
      | 3  | 15   | 01 October 2023   |                   | 100.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 50.0  | 50.0       | 0.0  | 50.0        |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 50.0  | 50.0       | 0.0  | 50.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 400.0         | 0        | 0.0  | 0         | 400.0 | 250.0 | 150.0      | 0.0  | 150.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 01 September 2023 | Down Payment     | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
      | 10 September 2023 | Repayment        | 150.0  | 150.0     | 0.0      | 0.0  | 0.0       | 150.0        |
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule

  @TestRailId:C2865 @AdvancedPaymentAllocation
  Scenario: Verify advanced payment allocation - future installments: REAMORTIZATION, partial payment due date, payment before next due date
    When Admin sets the business date to "01 September 2023"
    And Admin creates a client with random data
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "REAMORTIZATION" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 September 2023 | 400            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "400" amount and expected disbursement date on "01 September 2023"
    And Admin successfully disburse the loan on "01 September 2023" with "400" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 300.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 |                   | 200.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 3  | 15   | 01 October 2023   |                   | 100.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 400.0         | 0        | 0.0  | 0         | 400.0 | 100.0 | 0.0        | 0.0  | 300.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 01 September 2023 | Down Payment     | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 80 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 300.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 |                   | 200.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 80.0  | 0.0        | 0.0  | 20.0        |
      | 3  | 15   | 01 October 2023   |                   | 100.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 400.0         | 0        | 0.0  | 0         | 400.0 | 180.0 | 0.0        | 0.0  | 220.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 01 September 2023 | Down Payment     | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
      | 16 September 2023 | Repayment        | 80.0   | 80.0      | 0.0      | 0.0  | 0.0       | 220.0        |
    When Admin sets the business date to "20 September 2023"
    And Customer makes "AUTOPAY" repayment on "20 September 2023" with 180 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 300.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 20 September 2023 | 200.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 20.0 | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 100.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 80.0  | 80.0       | 0.0  | 20.0        |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 80.0  | 80.0       | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 400.0         | 0        | 0.0  | 0         | 400.0 | 360.0 | 160.0      | 20.0 | 40.0        |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 01 September 2023 | Down Payment     | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
      | 16 September 2023 | Repayment        | 80.0   | 80.0      | 0.0      | 0.0  | 0.0       | 220.0        |
      | 20 September 2023 | Repayment        | 180.0  | 180.0     | 0.0      | 0.0  | 0.0       | 40.0         |
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule

  @TestRailId:C2897 @AdvancedPaymentAllocation
  Scenario: Verify advanced payment allocation - future installments: LAST_INSTALLMENT, payment after due date
    When Admin sets the business date to "01 September 2023"
    And Admin creates a client with random data
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "LAST_INSTALLMENT" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 September 2023 | 400            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "400" amount and expected disbursement date on "01 September 2023"
    And Admin successfully disburse the loan on "01 September 2023" with "400" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 300.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 |                   | 200.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 3  | 15   | 01 October 2023   |                   | 100.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 400.0         | 0        | 0.0  | 0         | 400.0 | 100.0 | 0.0        | 0.0  | 300.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 01 September 2023 | Down Payment     | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
    When Admin sets the business date to "20 September 2023"
    And Customer makes "AUTOPAY" repayment on "20 September 2023" with 150 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 September 2023 |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 300.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 0.0   | 0.0         |
      | 2  | 15   | 16 September 2023 | 20 September 2023 | 200.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 100.0 | 0.0        | 100.0 | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 100.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0   | 100.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 50.0  | 50.0       | 0.0   | 50.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 400.0         | 0        | 0.0  | 0         | 400.0 | 250.0 | 50.0       | 100.0 | 150.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 01 September 2023 | Down Payment     | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
      | 20 September 2023 | Repayment        | 150.0  | 150.0     | 0.0      | 0.0  | 0.0       | 150.0        |
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule

  @TestRailId:C2922
  @ProgressiveLoanSchedule
  @AdvancedPaymentAllocation
  Scenario: Verify advanced payment allocation with progressive loan schedule with multi disbursement and with overpaid installment
    When Admin sets the business date to "01 May 2023"
    And Admin creates a client with random data
    When Admin set "LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 May 2023       | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 60                | DAYS                  | 15             | DAYS                   | 4                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 May 2023" with "1000" amount and expected disbursement date on "01 May 2023"
    And Admin successfully disburse the loan on "01 May 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date         | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 May 2023  |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 May 2023  |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 May 2023  |           | 562.5           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 0.0  | 0.0        | 0.0  | 187.5       |
      | 3  | 15   | 31 May 2023  |           | 375.0           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 0.0  | 0.0        | 0.0  | 187.5       |
      | 4  | 15   | 15 June 2023 |           | 187.5           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 0.0  | 0.0        | 0.0  | 187.5       |
      | 5  | 15   | 30 June 2023 |           | 0.0             | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 0.0  | 0.0        | 0.0  | 187.5       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0        | 0.0  | 0         | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 May 2023      | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    When Admin sets the business date to "06 May 2023"
    And Customer makes "AUTOPAY" repayment on "06 May 2023" with 650 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date         | Paid date   | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 May 2023  |             | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 May 2023  | 06 May 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 250.0 | 0.0         |
      | 2  | 15   | 16 May 2023  | 06 May 2023 | 562.5           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 187.5 | 187.5      | 0.0   | 0.0         |
      | 3  | 15   | 31 May 2023  | 06 May 2023 | 375.0           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 187.5 | 187.5      | 0.0   | 0.0         |
      | 4  | 15   | 15 June 2023 |             | 187.5           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 25.0  | 25.0       | 0.0   | 162.5       |
      | 5  | 15   | 30 June 2023 |             | 0.0             | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 0.0   | 0.0        | 0.0   | 187.5       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late  | Outstanding |
      | 1000.0        | 0        | 0.0  | 0         | 1000.0 | 650.0 | 400.0      | 250.0 | 350.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 May 2023      | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 06 May 2023      | Repayment        | 650.0  | 650.0     | 0.0      | 0.0  | 0.0       | 350.0        |
    When Admin sets the business date to "25 May 2023"
    And Admin successfully disburse the loan on "25 May 2023" with "250" EUR transaction amount
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date         | Paid date   | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 May 2023  |             | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 May 2023  | 06 May 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 250.0 | 0.0         |
      | 2  | 15   | 16 May 2023  | 06 May 2023 | 562.5           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 187.5 | 187.5      | 0.0   | 0.0         |
      |    |      | 25 May 2023  |             | 250.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 3  | 0    | 25 May 2023  |             | 750.0           | 62.5          | 0.0      | 0.0  | 0.0       | 62.5  | 0.0   | 0.0        | 0.0   | 62.5        |
      | 4  | 15   | 31 May 2023  |             | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 187.5 | 187.5      | 0.0   | 62.5        |
      | 5  | 15   | 15 June 2023 |             | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 25.0  | 25.0       | 0.0   | 225.0       |
      | 6  | 15   | 30 June 2023 |             | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0   | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late  | Outstanding |
      | 1250.0        | 0        | 0.0  | 0         | 1250.0 | 650.0 | 400.0      | 250.0 | 600.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 May 2023      | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 06 May 2023      | Repayment        | 650.0  | 650.0     | 0.0      | 0.0  | 0.0       | 350.0        |
      | 25 May 2023      | Disbursement     | 250.0  | 0.0       | 0.0      | 0.0  | 0.0       | 600.0        |
    When Admin sets the business date to "12 June 2023"
    And Admin successfully disburse the loan on "12 June 2023" with "250" EUR transaction amount
    Then Loan Repayment schedule has 7 periods, with the following data for periods:
      | Nr | Days | Date         | Paid date   | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 May 2023  |             | 1000.0          |               |          | 0.0  |           | 0.0    | 0.0   |            |       |             |
      | 1  | 0    | 01 May 2023  | 06 May 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0  | 250.0 | 0.0        | 250.0 | 0.0         |
      | 2  | 15   | 16 May 2023  | 06 May 2023 | 562.5           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5  | 187.5 | 187.5      | 0.0   | 0.0         |
      |    |      | 25 May 2023  |             | 250.0           |               |          | 0.0  |           | 0.0    | 0.0   |            |       |             |
      | 3  | 0    | 25 May 2023  |             | 750.0           | 62.5          | 0.0      | 0.0  | 0.0       | 62.5   | 0.0   | 0.0        | 0.0   | 62.5        |
      | 4  | 15   | 31 May 2023  |             | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0  | 187.5 | 187.5      | 0.0   | 62.5        |
      |    |      | 12 June 2023 |             | 250.0           |               |          | 0.0  |           | 0.0    | 0.0   |            |       |             |
      | 5  | 0    | 12 June 2023 |             | 687.5           | 62.5          | 0.0      | 0.0  | 0.0       | 62.5   | 0.0   | 0.0        | 0.0   | 62.5        |
      | 6  | 15   | 15 June 2023 |             | 343.75          | 343.75        | 0.0      | 0.0  | 0.0       | 343.75 | 25.0  | 25.0       | 0.0   | 318.75      |
      | 7  | 15   | 30 June 2023 |             | 0.0             | 343.75        | 0.0      | 0.0  | 0.0       | 343.75 | 0.0   | 0.0        | 0.0   | 343.75      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late  | Outstanding |
      | 1500.0        | 0        | 0.0  | 0         | 1500.0 | 650.0 | 400.0      | 250.0 | 850.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 May 2023      | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 06 May 2023      | Repayment        | 650.0  | 650.0     | 0.0      | 0.0  | 0.0       | 350.0        |
      | 25 May 2023      | Disbursement     | 250.0  | 0.0       | 0.0      | 0.0  | 0.0       | 600.0        |
      | 12 June 2023     | Disbursement     | 250.0  | 0.0       | 0.0      | 0.0  | 0.0       | 850.0        |
    When Admin set "LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule

  @TestRailId:C2937
  @ProgressiveLoanSchedule
  @AdvancedPaymentAllocation
  Scenario: Verify advanced payment allocation with progressive loan schedule with multi disbursement and reschedule
    When Admin sets the business date to "01 May 2023"
    And Admin creates a client with random data
    When Admin set "LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 May 2023       | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 60                | DAYS                  | 15             | DAYS                   | 4                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 May 2023" with "1000" amount and expected disbursement date on "01 May 2023"
    And Admin successfully disburse the loan on "01 May 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date         | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 May 2023  |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 May 2023  |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 May 2023  |           | 562.5           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 0.0  | 0.0        | 0.0  | 187.5       |
      | 3  | 15   | 31 May 2023  |           | 375.0           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 0.0  | 0.0        | 0.0  | 187.5       |
      | 4  | 15   | 15 June 2023 |           | 187.5           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 0.0  | 0.0        | 0.0  | 187.5       |
      | 5  | 15   | 30 June 2023 |           | 0.0             | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 0.0  | 0.0        | 0.0  | 187.5       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0        | 0.0  | 0         | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 May 2023      | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    When Admin sets the business date to "06 May 2023"
    And Customer makes "AUTOPAY" repayment on "06 May 2023" with 650 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date         | Paid date   | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 May 2023  |             | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 May 2023  | 06 May 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 250.0 | 0.0         |
      | 2  | 15   | 16 May 2023  | 06 May 2023 | 562.5           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 187.5 | 187.5      | 0.0   | 0.0         |
      | 3  | 15   | 31 May 2023  | 06 May 2023 | 375.0           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 187.5 | 187.5      | 0.0   | 0.0         |
      | 4  | 15   | 15 June 2023 |             | 187.5           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 25.0  | 25.0       | 0.0   | 162.5       |
      | 5  | 15   | 30 June 2023 |             | 0.0             | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 0.0   | 0.0        | 0.0   | 187.5       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late  | Outstanding |
      | 1000.0        | 0        | 0.0  | 0         | 1000.0 | 650.0 | 400.0      | 250.0 | 350.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 May 2023      | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 06 May 2023      | Repayment        | 650.0  | 650.0     | 0.0      | 0.0  | 0.0       | 350.0        |
    When Admin sets the business date to "25 May 2023"
    When Batch API call with steps: rescheduleLoan from "15 June 2023" to "13 July 2023" submitted on date: "25 May 2023", approveReschedule on date: "25 May 2023" runs with enclosingTransaction: "true"
    And Admin successfully disburse the loan on "25 May 2023" with "250" EUR transaction amount
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date         | Paid date   | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 May 2023  |             | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 May 2023  | 06 May 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 250.0 | 0.0         |
      | 2  | 15   | 16 May 2023  | 06 May 2023 | 562.5           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 187.5 | 187.5      | 0.0   | 0.0         |
      |    |      | 25 May 2023  |             | 250.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 3  | 0    | 25 May 2023  |             | 750.0           | 62.5          | 0.0      | 0.0  | 0.0       | 62.5  | 0.0   | 0.0        | 0.0   | 62.5        |
      | 4  | 15   | 31 May 2023  |             | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 187.5 | 187.5      | 0.0   | 62.5        |
      | 5  | 43   | 13 July 2023 |             | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 25.0  | 25.0       | 0.0   | 225.0       |
      | 6  | 15   | 28 July 2023 |             | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0   | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late  | Outstanding |
      | 1250.0        | 0        | 0.0  | 0         | 1250.0 | 650.0 | 400.0      | 250.0 | 600.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 May 2023      | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 06 May 2023      | Repayment        | 650.0  | 650.0     | 0.0      | 0.0  | 0.0       | 350.0        |
      | 25 May 2023      | Disbursement     | 250.0  | 0.0       | 0.0      | 0.0  | 0.0       | 600.0        |
    When Admin sets the business date to "15 July 2023"
    And Admin successfully disburse the loan on "15 July 2023" with "250" EUR transaction amount
    Then Loan Repayment schedule has 7 periods, with the following data for periods:
      | Nr | Days | Date         | Paid date   | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 May 2023  |             | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 May 2023  | 06 May 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 250.0 | 0.0         |
      | 2  | 15   | 16 May 2023  | 06 May 2023 | 562.5           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 187.5 | 187.5      | 0.0   | 0.0         |
      |    |      | 25 May 2023  |             | 250.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 3  | 0    | 25 May 2023  |             | 750.0           | 62.5          | 0.0      | 0.0  | 0.0       | 62.5  | 0.0   | 0.0        | 0.0   | 62.5        |
      | 4  | 15   | 31 May 2023  |             | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 187.5 | 187.5      | 0.0   | 62.5        |
      | 5  | 43   | 13 July 2023 |             | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 25.0  | 25.0       | 0.0   | 225.0       |
      |    |      | 15 July 2023 |             | 250.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 6  | 0    | 15 July 2023 |             | 437.5           | 62.5          | 0.0      | 0.0  | 0.0       | 62.5  | 0.0   | 0.0        | 0.0   | 62.5        |
      | 7  | 15   | 28 July 2023 |             | 0.0             | 437.5         | 0.0      | 0.0  | 0.0       | 437.5 | 0.0   | 0.0        | 0.0   | 437.5       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late  | Outstanding |
      | 1500.0        | 0        | 0.0  | 0         | 1500.0 | 650.0 | 400.0      | 250.0 | 850.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 May 2023      | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 06 May 2023      | Repayment        | 650.0  | 650.0     | 0.0      | 0.0  | 0.0       | 350.0        |
      | 25 May 2023      | Disbursement     | 250.0  | 0.0       | 0.0      | 0.0  | 0.0       | 600.0        |
      | 15 July 2023     | Disbursement     | 250.0  | 0.0       | 0.0      | 0.0  | 0.0       | 850.0        |
    When Batch API call with steps: rescheduleLoan from "13 July 2023" to "13 August 2023" submitted on date: "15 July 2023", approveReschedule on date: "15 July 2023" runs with enclosingTransaction: "true"
    Then Loan Repayment schedule has 7 periods, with the following data for periods:
      | Nr | Days | Date           | Paid date   | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 May 2023    |             | 1000.0          |               |          | 0.0  |           | 0.0    | 0.0   |            |       |             |
      | 1  | 0    | 01 May 2023    | 06 May 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0  | 250.0 | 0.0        | 250.0 | 0.0         |
      | 2  | 15   | 16 May 2023    | 06 May 2023 | 562.5           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5  | 187.5 | 187.5      | 0.0   | 0.0         |
      |    |      | 25 May 2023    |             | 250.0           |               |          | 0.0  |           | 0.0    | 0.0   |            |       |             |
      | 3  | 0    | 25 May 2023    |             | 750.0           | 62.5          | 0.0      | 0.0  | 0.0       | 62.5   | 0.0   | 0.0        | 0.0   | 62.5        |
      | 4  | 15   | 31 May 2023    |             | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0  | 187.5 | 187.5      | 0.0   | 62.5        |
      |    |      | 15 July 2023   |             | 250.0           |               |          | 0.0  |           | 0.0    | 0.0   |            |       |             |
      | 5  | 0    | 15 July 2023   |             | 687.5           | 62.5          | 0.0      | 0.0  | 0.0       | 62.5   | 0.0   | 0.0        | 0.0   | 62.5        |
      | 6  | 74   | 13 August 2023 |             | 343.75          | 343.75        | 0.0      | 0.0  | 0.0       | 343.75 | 25.0  | 25.0       | 0.0   | 318.75      |
      | 7  | 15   | 28 August 2023 |             | 0.0             | 343.75        | 0.0      | 0.0  | 0.0       | 343.75 | 0.0   | 0.0        | 0.0   | 343.75      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late  | Outstanding |
      | 1500.0        | 0        | 0.0  | 0         | 1500.0 | 650.0 | 400.0      | 250.0 | 850.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 May 2023      | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 06 May 2023      | Repayment        | 650.0  | 650.0     | 0.0      | 0.0  | 0.0       | 350.0        |
      | 25 May 2023      | Disbursement     | 250.0  | 0.0       | 0.0      | 0.0  | 0.0       | 600.0        |
      | 15 July 2023     | Disbursement     | 250.0  | 0.0       | 0.0      | 0.0  | 0.0       | 850.0        |
    When Admin set "LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule

  @TestRailId:C2940 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-horizontal, charge after maturity, loan fully paid in advance
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#  Loan got fully paid in advance
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 1020 EUR transaction amount
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan has 0 outstanding amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 01 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 250.0      | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   | 01 September 2023 | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 250.0      | 0.0  | 0.0         |
      | 4  | 15   | 16 October 2023   | 01 September 2023 | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 250.0      | 0.0  | 0.0         |
      | 5  | 1    | 17 October 2023   | 01 September 2023 | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 20.0  | 20.0       | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 1020.0 | 770.0      | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 1020.0 | 1000.0    | 0.0      | 0.0  | 20.0      | 0.0          |
      | 01 September 2023 | Accrual          | 20.0   | 0.0       | 0.0      | 0.0  | 20.0      | 0.0          |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 20.0 | 0.0    | 0.0         |

  @TestRailId:C2941 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-vertical, charge after maturity, loan fully paid in advance
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_VERTICAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#  Loan got fully paid in advance
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 1020 EUR transaction amount
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan has 0 outstanding amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 01 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 250.0      | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   | 01 September 2023 | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 250.0      | 0.0  | 0.0         |
      | 4  | 15   | 16 October 2023   | 01 September 2023 | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 250.0      | 0.0  | 0.0         |
      | 5  | 1    | 17 October 2023   | 01 September 2023 | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 20.0  | 20.0       | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 1020.0 | 770.0      | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 1020.0 | 1000.0    | 0.0      | 0.0  | 20.0      | 0.0          |
      | 01 September 2023 | Accrual          | 20.0   | 0.0       | 0.0      | 0.0  | 20.0      | 0.0          |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 20.0 | 0.0    | 0.0         |

  @TestRailId:C2942 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-horizontal, charge after maturity, loan overpaid in advance
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#  Loan got overpaid in advance
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 1120 EUR transaction amount
    Then Loan status will be "OVERPAID"
    Then Loan has 0 outstanding amount
    Then Loan has 100 overpaid amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 01 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 250.0      | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   | 01 September 2023 | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 250.0      | 0.0  | 0.0         |
      | 4  | 15   | 16 October 2023   | 01 September 2023 | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 250.0      | 0.0  | 0.0         |
      | 5  | 1    | 17 October 2023   | 01 September 2023 | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 20.0  | 20.0       | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 1020.0 | 770.0      | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 1120.0 | 1000.0    | 0.0      | 0.0  | 20.0      | 0.0          |
      | 01 September 2023 | Accrual          | 20.0   | 0.0       | 0.0      | 0.0  | 20.0      | 0.0          |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 20.0 | 0.0    | 0.0         |

  @TestRailId:C2943 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-vertical, charge after maturity, loan overpaid in advance
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_VERTICAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#  Loan got overpaid in advance
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 1120 EUR transaction amount
    Then Loan status will be "OVERPAID"
    Then Loan has 0 outstanding amount
    Then Loan has 100 overpaid amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 01 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 250.0      | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   | 01 September 2023 | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 250.0      | 0.0  | 0.0         |
      | 4  | 15   | 16 October 2023   | 01 September 2023 | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 250.0      | 0.0  | 0.0         |
      | 5  | 1    | 17 October 2023   | 01 September 2023 | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 20.0  | 20.0       | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 1020.0 | 770.0      | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 1120.0 | 1000.0    | 0.0      | 0.0  | 20.0      | 0.0          |
      | 01 September 2023 | Accrual          | 20.0   | 0.0       | 0.0      | 0.0  | 20.0      | 0.0          |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 20.0 | 0.0    | 0.0         |

  @TestRailId:C2944 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-horizontal, charge after maturity, in advanced repayment (future installment type: NEXT_INSTALLMENT)
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make due date repayments
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 250 EUR transaction amount
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 500.0 | 0.0        | 0.0  | 520.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make charges for the next installments
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 September 2023" due date and 20 EUR transaction amount
    When Admin adds "LOAN_NSF_FEE" due date charge with "16 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 500.0 | 0.0        | 0.0  | 560.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make in advance payment (future installment type: NEXT_INSTALLMENT)
    When Admin sets the business date to "17 September 2023"
    And Customer makes "AUTOPAY" repayment on "17 September 2023" with 100 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 100.0 | 100.0      | 0.0  | 170.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 600.0 | 100.0      | 0.0  | 460.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
      | 17 September 2023 | Repayment        | 100.0  | 80.0      | 0.0      | 0.0  | 20.0      | 420.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 20.0 | 0.0    | 0.0         |

  @TestRailId:C2945 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-vertical, charge after maturity, in advanced repayment (future installment type: NEXT_INSTALLMENT)
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_VERTICAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make due date repayments
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 250 EUR transaction amount
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 500.0 | 0.0        | 0.0  | 520.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make charges for the next installments
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 September 2023" due date and 20 EUR transaction amount
    When Admin adds "LOAN_NSF_FEE" due date charge with "16 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 500.0 | 0.0        | 0.0  | 560.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make in advance payment (future installment type: NEXT_INSTALLMENT)
    When Admin sets the business date to "17 September 2023"
    And Customer makes "AUTOPAY" repayment on "17 September 2023" with 100 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 60.0  | 60.0       | 0.0  | 210.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 20.0  | 20.0       | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   | 17 September 2023 | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 20.0  | 20.0       | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 600.0 | 100.0      | 0.0  | 460.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
      | 17 September 2023 | Repayment        | 100.0  | 40.0      | 0.0      | 0.0  | 60.0      | 460.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 20.0 | 0.0    | 0.0         |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 20.0 | 0.0    | 0.0         |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 20.0 | 0.0    | 0.0         |

  @TestRailId:C2946 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-horizontal, charge after maturity, late Goodwill credit transaction (future installment type: LAST_INSTALLMENT) - amount < past due charge
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make due date repayments
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 250 EUR transaction amount
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 500.0 | 0.0        | 0.0  | 520.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make charges for the next installments
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 September 2023" due date and 20 EUR transaction amount
    When Admin adds "LOAN_NSF_FEE" due date charge with "16 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 500.0 | 0.0        | 0.0  | 560.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make late Goodwill credit transaction (future installment type: LAST_INSTALLMENT)
    When Admin sets the business date to "17 October 2023"
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "17 October 2023" with 15 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 15.0  | 0.0        | 15.0 | 255.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 515.0 | 0.0        | 15.0 | 545.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
      | 17 October 2023   | Goodwill Credit  | 15.0   | 0.0       | 0.0      | 0.0  | 15.0      | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 15.0 | 0.0    | 5.0         |

  @TestRailId:C2947 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-vertical, charge after maturity, late Goodwill credit transaction (future installment type: LAST_INSTALLMENT) - amount < past due charge
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_VERTICAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make due date repayments
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 250 EUR transaction amount
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 500.0 | 0.0        | 0.0  | 520.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make charges for the next installments
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 September 2023" due date and 20 EUR transaction amount
    When Admin adds "LOAN_NSF_FEE" due date charge with "16 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 500.0 | 0.0        | 0.0  | 560.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make late Goodwill credit transaction (future installment type: LAST_INSTALLMENT)
    When Admin sets the business date to "17 October 2023"
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "17 October 2023" with 15 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 15.0  | 0.0        | 15.0 | 255.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 515.0 | 0.0        | 15.0 | 545.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
      | 17 October 2023   | Goodwill Credit  | 15.0   | 0.0       | 0.0      | 0.0  | 15.0      | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 15.0 | 0.0    | 5.0         |

  @TestRailId:C2948 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-horizontal, charge after maturity, late Goodwill credit transaction (future installment type: LAST_INSTALLMENT) - amount > first past due charge
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make due date repayments
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 250 EUR transaction amount
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 500.0 | 0.0        | 0.0  | 520.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make charges for the next installments
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 September 2023" due date and 20 EUR transaction amount
    When Admin adds "LOAN_NSF_FEE" due date charge with "16 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 500.0 | 0.0        | 0.0  | 560.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make late Goodwill credit transaction (future installment type: LAST_INSTALLMENT)
    When Admin sets the business date to "17 October 2023"
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "17 October 2023" with 35 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 35.0  | 0.0        | 35.0 | 235.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 535.0 | 0.0        | 35.0 | 525.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
      | 17 October 2023   | Goodwill Credit  | 35.0   | 15.0      | 0.0      | 0.0  | 20.0      | 485.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 20.0 | 0.0    | 0.0         |

  @TestRailId:C2949 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-vertical, charge after maturity, late Goodwill credit transaction (future installment type: LAST_INSTALLMENT) - amount > first past due charge
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_VERTICAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make due date repayments
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 250 EUR transaction amount
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 500.0 | 0.0        | 0.0  | 520.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make charges for the next installments
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 September 2023" due date and 20 EUR transaction amount
    When Admin adds "LOAN_NSF_FEE" due date charge with "16 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 500.0 | 0.0        | 0.0  | 560.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make late Goodwill credit transaction (future installment type: LAST_INSTALLMENT)
    When Admin sets the business date to "17 October 2023"
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "17 October 2023" with 35 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 20.0  | 0.0        | 20.0 | 250.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 15.0  | 0.0        | 15.0 | 255.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 535.0 | 0.0        | 35.0 | 525.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
      | 17 October 2023   | Goodwill Credit  | 35.0   | 0.0       | 0.0      | 0.0  | 35.0      | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 15.0 | 0.0    | 5.0         |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 20.0 | 0.0    | 0.0         |

  @TestRailId:C2950 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-horizontal, charge after maturity, late Goodwill credit transaction (future installment type: LAST_INSTALLMENT) - amount > sum past due charges
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make due date repayments
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 250 EUR transaction amount
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 500.0 | 0.0        | 0.0  | 520.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make charges for the next installments
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 September 2023" due date and 20 EUR transaction amount
    When Admin adds "LOAN_NSF_FEE" due date charge with "16 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 500.0 | 0.0        | 0.0  | 560.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make late Goodwill credit transaction (future installment type: LAST_INSTALLMENT)
    When Admin sets the business date to "17 October 2023"
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "17 October 2023" with 350 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0   | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0   | 0.0         |
      | 3  | 15   | 01 October 2023   | 17 October 2023   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 270.0 | 0.0        | 270.0 | 0.0         |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 80.0  | 0.0        | 80.0  | 190.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0   | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late  | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 850.0 | 0.0        | 350.0 | 210.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
      | 17 October 2023   | Goodwill Credit  | 350.0  | 310.0     | 0.0      | 0.0  | 40.0      | 190.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 20.0 | 0.0    | 0.0         |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 20.0 | 0.0    | 0.0         |

  @TestRailId:C2951 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-vertical, charge after maturity, late Goodwill credit transaction (future installment type: LAST_INSTALLMENT) - amount > sum past due charges
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_VERTICAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make due date repayments
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 250 EUR transaction amount
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 500.0 | 0.0        | 0.0  | 520.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make charges for the next installments
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 September 2023" due date and 20 EUR transaction amount
    When Admin adds "LOAN_NSF_FEE" due date charge with "16 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 500.0 | 0.0        | 0.0  | 560.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make late Goodwill credit transaction (future installment type: LAST_INSTALLMENT)
    When Admin sets the business date to "17 October 2023"
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "17 October 2023" with 350 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0   | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0   | 0.0         |
      | 3  | 15   | 01 October 2023   | 17 October 2023   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 270.0 | 0.0        | 270.0 | 0.0         |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 80.0  | 0.0        | 80.0  | 190.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0   | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late  | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 850.0 | 0.0        | 350.0 | 210.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
      | 17 October 2023   | Goodwill Credit  | 350.0  | 310.0     | 0.0      | 0.0  | 40.0      | 190.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 20.0 | 0.0    | 0.0         |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 20.0 | 0.0    | 0.0         |

  @TestRailId:C2952 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-horizontal, charge after maturity, in advanced Goodwill credit transaction on first charge due date (future installment type: LAST_INSTALLMENT) - amount > due date charge
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make due date repayments
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 250 EUR transaction amount
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 500.0 | 0.0        | 0.0  | 520.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make charges for the next installments
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 September 2023" due date and 20 EUR transaction amount
    When Admin adds "LOAN_NSF_FEE" due date charge with "16 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 500.0 | 0.0        | 0.0  | 560.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make in advanced Goodwill credit transaction (future installment type: LAST_INSTALLMENT)
    When Admin sets the business date to "17 September 2023"
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "17 September 2023" with 35 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 15.0  | 15.0       | 0.0  | 255.0       |
      | 5  | 1    | 17 October 2023   | 17 September 2023 | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 20.0  | 20.0       | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 535.0 | 35.0       | 0.0  | 525.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
      | 17 September 2023 | Goodwill Credit  | 35.0   | 0.0       | 0.0      | 0.0  | 35.0      | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 20.0 | 0.0    | 0.0         |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 15.0 | 0.0    | 5.0         |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |

  @TestRailId:C2953 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-vertical, charge after maturity, in advanced Goodwill credit transaction on first charge due date (future installment type: LAST_INSTALLMENT) - amount > due date charge
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_VERTICAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make due date repayments
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 250 EUR transaction amount
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 500.0 | 0.0        | 0.0  | 520.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make charges for the next installments
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 September 2023" due date and 20 EUR transaction amount
    When Admin adds "LOAN_NSF_FEE" due date charge with "16 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 500.0 | 0.0        | 0.0  | 560.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make in advanced Goodwill credit transaction (future installment type: LAST_INSTALLMENT)
    When Admin sets the business date to "17 September 2023"
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "17 September 2023" with 35 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 15.0  | 15.0       | 0.0  | 255.0       |
      | 5  | 1    | 17 October 2023   | 17 September 2023 | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 20.0  | 20.0       | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 535.0 | 35.0       | 0.0  | 525.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
      | 17 September 2023 | Goodwill Credit  | 35.0   | 0.0       | 0.0      | 0.0  | 35.0      | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 20.0 | 0.0    | 0.0         |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 15.0 | 0.0    | 5.0         |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |

  @TestRailId:C2954 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-horizontal, charge after maturity, in advanced Goodwill credit transaction before first charge due date (future installment type: LAST_INSTALLMENT) - amount > first and second charge
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make due date repayments
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 250 EUR transaction amount
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 500.0 | 0.0        | 0.0  | 520.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make charges for the next installments
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 September 2023" due date and 20 EUR transaction amount
    When Admin adds "LOAN_NSF_FEE" due date charge with "16 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 500.0 | 0.0        | 0.0  | 560.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
    When Admin sets the business date to "17 September 2023"
  #    Make in advanced Goodwill credit transaction (future installment type: LAST_INSTALLMENT)
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "17 September 2023" with 35 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 15.0  | 15.0       | 0.0  | 255.0       |
      | 5  | 1    | 17 October 2023   | 17 September 2023 | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 20.0  | 20.0       | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 535.0 | 35.0       | 0.0  | 525.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
      | 17 September 2023 | Goodwill Credit  | 35.0   | 0.0       | 0.0      | 0.0  | 35.0      | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 20.0 | 0.0    | 0.0         |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 15.0 | 0.0    | 5.0         |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |

  @TestRailId:C2955 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-vertical, charge after maturity, in advanced Goodwill credit transaction before first charge due date (future installment type: LAST_INSTALLMENT) - amount > first and second charge
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_VERTICAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make due date repayments
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 250 EUR transaction amount
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 500.0 | 0.0        | 0.0  | 520.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make charges for the next installments
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 September 2023" due date and 20 EUR transaction amount
    When Admin adds "LOAN_NSF_FEE" due date charge with "16 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 500.0 | 0.0        | 0.0  | 560.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
    When Admin sets the business date to "17 September 2023"
#    Make in advanced Goodwill credit transaction (future installment type: LAST_INSTALLMENT)
    When Customer makes "GOODWILL_CREDIT" transaction with "AUTOPAY" payment type on "17 September 2023" with 35 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 15.0  | 15.0       | 0.0  | 255.0       |
      | 5  | 1    | 17 October 2023   | 17 September 2023 | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 20.0  | 20.0       | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 535.0 | 35.0       | 0.0  | 525.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
      | 17 September 2023 | Goodwill Credit  | 35.0   | 0.0       | 0.0      | 0.0  | 35.0      | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 20.0 | 0.0    | 0.0         |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 15.0 | 0.0    | 5.0         |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |

  @TestRailId:C2956 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-horizontal, charge after maturity, in advanced Merchant issued refund transaction on first charge due date (future installment type: REAMORTIZATION) - amount < sum all charges
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make due date repayments
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 250 EUR transaction amount
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 500.0 | 0.0        | 0.0  | 520.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make charges for the next installments
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 September 2023" due date and 20 EUR transaction amount
    When Admin adds "LOAN_NSF_FEE" due date charge with "16 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 500.0 | 0.0        | 0.0  | 560.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make in advanced Merchant issued refund transaction (future installment type: REAMORTIZATION)
    When Admin sets the business date to "17 September 2023"
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "17 September 2023" with 30 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 10.0  | 10.0       | 0.0  | 260.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 10.0  | 10.0       | 0.0  | 260.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 10.0  | 10.0       | 0.0  | 10.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 530.0 | 30.0       | 0.0  | 530.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement           | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment              | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment              | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
      | 17 September 2023 | Merchant Issued Refund | 30.0   | 0.0       | 0.0      | 0.0  | 30.0      | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 10.0 | 0.0    | 10.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 10.0 | 0.0    | 10.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 10.0 | 0.0    | 10.0        |

  @TestRailId:C2957 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-vertical, charge after maturity, in advanced Merchant issued refund transaction on first charge due date (future installment type: REAMORTIZATION) - amount < sum all charges
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_VERTICAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make due date repayments
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 250 EUR transaction amount
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 500.0 | 0.0        | 0.0  | 520.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make charges for the next installments
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 September 2023" due date and 20 EUR transaction amount
    When Admin adds "LOAN_NSF_FEE" due date charge with "16 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 500.0 | 0.0        | 0.0  | 560.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make in advanced Merchant issued refund transaction (future installment type: REAMORTIZATION)
    When Admin sets the business date to "17 September 2023"
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "17 September 2023" with 30 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 10.0  | 10.0       | 0.0  | 260.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 10.0  | 10.0       | 0.0  | 260.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 10.0  | 10.0       | 0.0  | 10.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 530.0 | 30.0       | 0.0  | 530.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement           | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment              | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment              | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
      | 17 September 2023 | Merchant Issued Refund | 30.0   | 0.0       | 0.0      | 0.0  | 30.0      | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 10.0 | 0.0    | 10.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 10.0 | 0.0    | 10.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 10.0 | 0.0    | 10.0        |

  @TestRailId:C2958 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-horizontal, charge after maturity, in advanced Merchant issued refund transaction before first charge due date (future installment type: REAMORTIZATION) - amount < sum all charges
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make due date repayments
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 250 EUR transaction amount
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 500.0 | 0.0        | 0.0  | 520.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make charges for the next installments
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 September 2023" due date and 20 EUR transaction amount
    When Admin adds "LOAN_NSF_FEE" due date charge with "16 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 500.0 | 0.0        | 0.0  | 560.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
    When Admin sets the business date to "17 September 2023"
#    Make in advanced Merchant issued refund transaction (future installment type: REAMORTIZATION)
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "17 September 2023" with 30 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 10.0  | 10.0       | 0.0  | 260.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 10.0  | 10.0       | 0.0  | 260.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 10.0  | 10.0       | 0.0  | 10.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 530.0 | 30.0       | 0.0  | 530.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement           | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment              | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment              | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
      | 17 September 2023 | Merchant Issued Refund | 30.0   | 0.0       | 0.0      | 0.0  | 30.0      | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 10.0 | 0.0    | 10.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 10.0 | 0.0    | 10.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 10.0 | 0.0    | 10.0        |

  @TestRailId:C2959 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify AdvancedPaymentAllocation behaviour: loanScheduleProcessingType-vertical, charge after maturity, in advanced Merchant issued refund transaction before first charge due date (future installment type: REAMORTIZATION) - amount < sum all charges
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_VERTICAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
#    Add charge after maturity
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 September 2023 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 01 October 2023   |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |           | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0  | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 0.0  | 0.0        | 0.0  | 1020.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make due date repayments
    And Customer makes "AUTOPAY" repayment on "01 September 2023" with 250 EUR transaction amount
    When Admin sets the business date to "16 September 2023"
    And Customer makes "AUTOPAY" repayment on "16 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 20.0      | 1020.0 | 500.0 | 0.0        | 0.0  | 520.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of       | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
#    Make charges for the next installments
    When Admin adds "LOAN_NSF_FEE" due date charge with "17 September 2023" due date and 20 EUR transaction amount
    When Admin adds "LOAN_NSF_FEE" due date charge with "16 October 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 0.0   | 0.0        | 0.0  | 270.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 500.0 | 0.0        | 0.0  | 560.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment        | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 0.0  | 0.0    | 20.0        |
    When Admin sets the business date to "17 September 2023"
#    Make in advanced Merchant issued refund transaction (future installment type: REAMORTIZATION)
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "17 September 2023" with 30 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 September 2023 | 16 September 2023 | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 01 October 2023   |                   | 250.0           | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 10.0  | 10.0       | 0.0  | 260.0       |
      | 4  | 15   | 16 October 2023   |                   | 0.0             | 250.0         | 0.0      | 0.0  | 20.0      | 270.0 | 10.0  | 10.0       | 0.0  | 260.0       |
      | 5  | 1    | 17 October 2023   |                   | 0.0             | 0.0           | 0.0      | 0.0  | 20.0      | 20.0  | 10.0  | 10.0       | 0.0  | 10.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 60.0      | 1060.0 | 530.0 | 30.0       | 0.0  | 530.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 September 2023 | Disbursement           | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 September 2023 | Repayment              | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 16 September 2023 | Repayment              | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 500.0        |
      | 17 September 2023 | Merchant Issued Refund | 30.0   | 0.0       | 0.0      | 0.0  | 30.0      | 500.0        |
    Then Loan Charges tab has the following data:
      | Name    | isPenalty | Payment due at     | Due as of         | Calculation type | Due  | Paid | Waived | Outstanding |
      | NSF fee | true      | Specified due date | 17 October 2023   | Flat             | 20.0 | 10.0 | 0.0    | 10.0        |
      | NSF fee | true      | Specified due date | 16 October 2023   | Flat             | 20.0 | 10.0 | 0.0    | 10.0        |
      | NSF fee | true      | Specified due date | 17 September 2023 | Flat             | 20.0 | 10.0 | 0.0    | 10.0        |

  @TestRailId:C2960 @AdvancedPaymentAllocation
  Scenario: Verify that rounding in case of multiple installments works properly
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy                        |
      | LP1         | 01 September 2023 | 1000           | 0                      | DECLINING_BALANCE | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | PENALTIES_FEES_INTEREST_PRINCIPAL_ORDER |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 30   | 01 October 2023   |           | 833.0           | 167.0         | 0.0      | 0.0  | 0.0       | 167.0 | 0.0  | 0.0        | 0.0  | 167.0       |
      | 2  | 31   | 01 November 2023  |           | 666.0           | 167.0         | 0.0      | 0.0  | 0.0       | 167.0 | 0.0  | 0.0        | 0.0  | 167.0       |
      | 3  | 30   | 01 December 2023  |           | 499.0           | 167.0         | 0.0      | 0.0  | 0.0       | 167.0 | 0.0  | 0.0        | 0.0  | 167.0       |
      | 4  | 31   | 01 January 2024   |           | 332.0           | 167.0         | 0.0      | 0.0  | 0.0       | 167.0 | 0.0  | 0.0        | 0.0  | 167.0       |
      | 5  | 31   | 01 February 2024  |           | 165.0           | 167.0         | 0.0      | 0.0  | 0.0       | 167.0 | 0.0  | 0.0        | 0.0  | 167.0       |
      | 6  | 29   | 01 March 2024     |           | 0.0             | 165.0         | 0.0      | 0.0  | 0.0       | 165.0 | 0.0  | 0.0        | 0.0  | 165.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |

  @TestRailId:C2976 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify that disbursement amount only distributed only to future installments (2nd and 3rd installments)
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 30   | 01 October 2023   |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 31   | 01 November 2023  |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 30   | 01 December 2023  |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    When Admin adds "LOAN_SNOOZE_FEE" due date charge with "1 September 2023" due date and 50 EUR transaction amount
    And Customer makes "AUTOPAY" repayment on "1 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 30   | 01 October 2023   |                   | 500.0           | 250.0         | 0.0      | 50.0 | 0.0       | 300.0 | 0.0   | 0.0        | 0.0  | 300.0       |
      | 3  | 31   | 01 November 2023  |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 30   | 01 December 2023  |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 50.0 | 0.0       | 1050.0 | 250.0 | 0.0        | 0.0  | 800.0       |
    When Admin sets the business date to "01 October 2023"
    When Admin successfully disburse the loan on "01 October 2023" with "400" EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 30   | 01 October 2023   |                   | 500.0           | 250.0         | 0.0      | 50.0 | 0.0       | 300.0 | 0.0   | 0.0        | 0.0  | 300.0       |
      |    |      | 01 October 2023   |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 3  | 0    | 01 October 2023   |                   | 800.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 4  | 31   | 01 November 2023  |                   | 400.0           | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 0.0   | 0.0        | 0.0  | 400.0       |
      | 5  | 30   | 01 December 2023  |                   | 0.0             | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 0.0   | 0.0        | 0.0  | 400.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1400.0        | 0.0      | 50.0 | 0.0       | 1450.0 | 250.0 | 0.0        | 0.0  | 1200.0      |

  @TestRailId:C2977 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify that disbursement amount only distributed only to future installments (1st, 2nd and 3rd installments)
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 30   | 01 October 2023   |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 31   | 01 November 2023  |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 30   | 01 December 2023  |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    When Admin adds "LOAN_SNOOZE_FEE" due date charge with "1 September 2023" due date and 50 EUR transaction amount
    And Customer makes "AUTOPAY" repayment on "1 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 30   | 01 October 2023   |                   | 500.0           | 250.0         | 0.0      | 50.0 | 0.0       | 300.0 | 0.0   | 0.0        | 0.0  | 300.0       |
      | 3  | 31   | 01 November 2023  |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 30   | 01 December 2023  |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 50.0 | 0.0       | 1050.0 | 250.0 | 0.0        | 0.0  | 800.0       |
    When Admin successfully disburse the loan on "01 September 2023" with "400" EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      |    |      | 01 September 2023 |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 1150.0          | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 0    | 01 September 2023 |                   | 1050.0          | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 3  | 30   | 01 October 2023   |                   | 700.0           | 350.0         | 0.0      | 50.0 | 0.0       | 400.0 | 0.0   | 0.0        | 0.0  | 400.0       |
      | 4  | 31   | 01 November 2023  |                   | 350.0           | 350.0         | 0.0      | 0.0  | 0.0       | 350.0 | 0.0   | 0.0        | 0.0  | 350.0       |
      | 5  | 30   | 01 December 2023  |                   | 0.0             | 350.0         | 0.0      | 0.0  | 0.0       | 350.0 | 0.0   | 0.0        | 0.0  | 350.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1400.0        | 0.0      | 50.0 | 0.0       | 1450.0 | 250.0 | 0.0        | 0.0  | 1200.0      |

  @TestRailId:C2978 @AdvancedPaymentAllocation @ProgressiveLoanSchedule
  Scenario: Verify that multiple disbursement amount only distributed only to future installments (2nd and 3rd installments)
    When Admin sets the business date to "01 September 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 September 2023 | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 September 2023" with "1000" amount and expected disbursement date on "01 September 2023"
    When Admin successfully disburse the loan on "01 September 2023" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 September 2023 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 30   | 01 October 2023   |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 31   | 01 November 2023  |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 30   | 01 December 2023  |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    When Admin adds "LOAN_SNOOZE_FEE" due date charge with "1 September 2023" due date and 50 EUR transaction amount
    And Customer makes "AUTOPAY" repayment on "1 September 2023" with 250 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 30   | 01 October 2023   |                   | 500.0           | 250.0         | 0.0      | 50.0 | 0.0       | 300.0 | 0.0   | 0.0        | 0.0  | 300.0       |
      | 3  | 31   | 01 November 2023  |                   | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
      | 4  | 30   | 01 December 2023  |                   | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0   | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 50.0 | 0.0       | 1050.0 | 250.0 | 0.0        | 0.0  | 800.0       |
    When Admin sets the business date to "01 October 2023"
    When Admin successfully disburse the loan on "01 October 2023" with "400" EUR transaction amount
    When Admin successfully disburse the loan on "01 October 2023" with "80" EUR transaction amount
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 September 2023 |                   | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 September 2023 | 01 September 2023 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 30   | 01 October 2023   |                   | 500.0           | 250.0         | 0.0      | 50.0 | 0.0       | 300.0 | 0.0   | 0.0        | 0.0  | 300.0       |
      |    |      | 01 October 2023   |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      |    |      | 01 October 2023   |                   | 80.0            |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 3  | 0    | 01 October 2023   |                   | 880.0           | 100.0         | 0.0      | 0.0  | 0.0       | 100.0 | 0.0   | 0.0        | 0.0  | 100.0       |
      | 4  | 0    | 01 October 2023   |                   | 860.0           | 20.0          | 0.0      | 0.0  | 0.0       | 20.0  | 0.0   | 0.0        | 0.0  | 20.0        |
      | 5  | 31   | 01 November 2023  |                   | 430.0           | 430.0         | 0.0      | 0.0  | 0.0       | 430.0 | 0.0   | 0.0        | 0.0  | 430.0       |
      | 6  | 30   | 01 December 2023  |                   | 0.0             | 430.0         | 0.0      | 0.0  | 0.0       | 430.0 | 0.0   | 0.0        | 0.0  | 430.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1480.0        | 0.0      | 50.0 | 0.0       | 1530.0 | 250.0 | 0.0        | 0.0  | 1280.0      |

  @TestRailId:C2986 @AdvancedPaymentAllocation
  Scenario: As an admin I would like to verify that refund is working with advanced payment allocation
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin sets the business date to "01 January 2023"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 January 2023   | 500            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2023" with "500" amount and expected disbursement date on "01 January 2023"
    Then Loan status has changed to "Approved"
    And Admin successfully disburse the loan on "01 January 2023" with "500" EUR transaction amount
    Then Loan status has changed to "Active"
    When Admin sets the business date to "11 January 2023"
    When Admin adds "LOAN_SNOOZE_FEE" due date charge with "11 January 2023" due date and 20 EUR transaction amount
    When Admin adds "LOAN_SNOOZE_FEE" due date charge with "21 January 2023" due date and 20 EUR transaction amount
    When Admin adds "LOAN_SNOOZE_FEE" due date charge with "11 February 2023" due date and 20 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  |                 | 250.0           | 125.0         | 0.0      | 20.0 | 0.0       | 145.0 | 0.0   | 0.0        | 0.0  | 145.0       |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 20.0 | 0.0       | 145.0 | 0.0   | 0.0        | 0.0  | 145.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 20.0 | 0.0       | 145.0 | 0.0   | 0.0        | 0.0  | 145.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 60.0 | 0         | 560.0 | 125.0 | 0          | 0    | 435.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       |
    When Admin sets the business date to "16 January 2023"
    And Customer makes "AUTOPAY" repayment on "16 January 2023" with 315 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  | 16 January 2023 | 250.0           | 125.0         | 0.0      | 20.0 | 0.0       | 145.0 | 145.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 31 January 2023  | 16 January 2023 | 125.0           | 125.0         | 0.0      | 20.0 | 0.0       | 145.0 | 145.0 | 145.0      | 0.0  | 0.0         |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 20.0 | 0.0       | 145.0 | 25.0  | 25.0       | 0.0  | 120.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 60.0 | 0         | 560.0 | 440.0 | 170        | 0    | 120.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       |
      | 16 January 2023  | Repayment        | 315.0  | 255.0     | 0.0      | 60.0 | 0.0       |
    When Admin sets the business date to "17 January 2023"
    And Admin makes "REFUND_BY_CASH" transaction with "AUTOPAY" payment type on "17 January 2023" with 150 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2023  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2023  | 01 January 2023 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2023  | 16 January 2023 | 250.0           | 125.0         | 0.0      | 20.0 | 0.0       | 145.0 | 145.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 31 January 2023  |                 | 125.0           | 125.0         | 0.0      | 20.0 | 0.0       | 145.0 | 20.0  | 20.0       | 0.0  | 125.0       |
      | 4  | 15   | 15 February 2023 |                 | 0.0             | 125.0         | 0.0      | 20.0 | 0.0       | 145.0 | 0.0   | 0.0        | 0.0  | 145.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 500.0         | 0        | 60.0 | 0         | 560.0 | 290.0 | 20         | 0    | 270.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties |
      | 01 January 2023  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       |
      | 01 January 2023  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       |
      | 16 January 2023  | Repayment        | 315.0  | 255.0     | 0.0      | 60.0 | 0.0       |
      | 17 January 2023  | Refund           | 150.0  | 130.0     | 0.0      | 20.0 | 0.0       |

  @TestRailId:C3042 @AdvancedPaymentAllocation
  Scenario: Verify that second disbursement is working on overpaid accounts in case of NEXT_INSTALLMENT future installment allocation rule
    When Admin sets the business date to "01 January 2024"
    When Admin creates a client with random data
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 January 2024   | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2024" with "1000" amount and expected disbursement date on "01 January 2024"
    When Admin successfully disburse the loan on "01 January 2024" with "500" EUR transaction amount
    When Admin sets the business date to "16 January 2024"
    And Customer makes "AUTOPAY" repayment on "16 January 2024" with 500 EUR transaction amount
    Then Loan status will be "OVERPAID"
    Then Loan has 125 overpaid amount
    When Admin successfully disburse the loan on "16 January 2024" with "500" EUR transaction amount
    Then Loan status will be "ACTIVE"
    Then Loan has 375 outstanding amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2024  | 01 January 2024 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2024  | 16 January 2024 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      |    |      | 16 January 2024  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 3  | 0    | 16 January 2024  | 16 January 2024 | 625.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 4  | 15   | 31 January 2024  |                 | 313.0           | 312.0         | 0.0      | 0.0  | 0.0       | 312.0 | 125.0 | 125.0      | 0.0  | 187.0       |
      | 5  | 15   | 15 February 2024 |                 | 0.0             | 313.0         | 0.0      | 0.0  | 0.0       | 313.0 | 125.0 | 125.0      | 0.0  | 188.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0        | 0.0  | 0         | 1000.0 | 625.0 | 250.0      | 0    | 375.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2024  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2024  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
      | 16 January 2024  | Repayment        | 500.0  | 375.0     | 0.0      | 0.0  | 0.0       | 0.0          |
      | 16 January 2024  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 375.0        |
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule

  @TestRailId:C3043 @AdvancedPaymentAllocation
  Scenario: Verify that second disbursement is working on overpaid accounts in case of REAMORTIZATION future installment allocation rule
    When Admin sets the business date to "01 January 2024"
    When Admin creates a client with random data
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "REAMORTIZATION" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 January 2024   | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2024" with "1000" amount and expected disbursement date on "01 January 2024"
    When Admin successfully disburse the loan on "01 January 2024" with "500" EUR transaction amount
    When Admin sets the business date to "16 January 2024"
    When Customer makes "PAYOUT_REFUND" transaction with "AUTOPAY" payment type on "16 January 2024" with 500 EUR transaction amount and system-generated Idempotency key
    Then Loan status will be "OVERPAID"
    Then Loan has 125 overpaid amount
    When Admin successfully disburse the loan on "16 January 2024" with "500" EUR transaction amount
    Then Loan status will be "ACTIVE"
    Then Loan has 375 outstanding amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 January 2024  | 01 January 2024 | 375.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 16 January 2024  | 16 January 2024 | 250.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      |    |      | 16 January 2024  |                 | 500.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 3  | 0    | 16 January 2024  | 16 January 2024 | 625.0           | 125.0         | 0.0      | 0.0  | 0.0       | 125.0 | 125.0 | 0.0        | 0.0  | 0.0         |
      | 4  | 15   | 31 January 2024  |                 | 313.0           | 312.0         | 0.0      | 0.0  | 0.0       | 312.0 | 125.0 | 125.0      | 0.0  | 187.0       |
      | 5  | 15   | 15 February 2024 |                 | 0.0             | 313.0         | 0.0      | 0.0  | 0.0       | 313.0 | 125.0 | 125.0      | 0.0  | 188.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1000.0        | 0        | 0.0  | 0         | 1000.0 | 625.0 | 250.0      | 0    | 375.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2024  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        |
      | 01 January 2024  | Down Payment     | 125.0  | 125.0     | 0.0      | 0.0  | 0.0       | 375.0        |
      | 16 January 2024  | Payout Refund    | 500.0  | 375.0     | 0.0      | 0.0  | 0.0       | 0.0          |
      | 16 January 2024  | Disbursement     | 500.0  | 0.0       | 0.0      | 0.0  | 0.0       | 375.0        |
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule

  @TestRailId:C3046 @AdvancedPaymentAllocation
  Scenario: Verify that principal due is correct in case of second disbursement on overpaid loan
    When Admin sets the business date to "01 February 2024"
    When Admin creates a client with random data
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "REAMORTIZATION" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 February 2024  | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 February 2024" with "1000" amount and expected disbursement date on "01 February 2024"
    When Admin successfully disburse the loan on "01 February 2024" with "200" EUR transaction amount
    When Admin sets the business date to "02 February 2024"
    When Customer makes "PAYOUT_REFUND" transaction with "AUTOPAY" payment type on "02 February 2024" with 200 EUR transaction amount and system-generated Idempotency key
    Then Loan status will be "OVERPAID"
    Then Loan has 50 overpaid amount
    When Admin successfully disburse the loan on "02 February 2024" with "160" EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due  | Paid  | In advance | Late | Outstanding |
      |    |      | 01 February 2024 |                  | 200.0           |               |          | 0.0  |           | 0.0  | 0.0   |            |      |             |
      | 1  | 0    | 01 February 2024 | 01 February 2024 | 150.0           | 50.0          | 0.0      | 0.0  | 0.0       | 50.0 | 50.0  | 0.0        | 0.0  | 0.0         |
      |    |      | 02 February 2024 |                  | 160.0           |               |          | 0.0  |           | 0.0  | 0.0   |            |      |             |
      | 2  | 0    | 02 February 2024 | 02 February 2024 | 270.0           | 40.0          | 0.0      | 0.0  | 0.0       | 40.0 | 40.0  | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 16 February 2024 |                  | 180.0           | 90.0          | 0.0      | 0.0  | 0.0       | 90.0 | 53.33 | 53.33      | 0.0  | 36.67       |
      | 4  | 15   | 02 March 2024    |                  | 90.0            | 90.0          | 0.0      | 0.0  | 0.0       | 90.0 | 53.33 | 53.33      | 0.0  | 36.67       |
      | 5  | 15   | 17 March 2024    |                  | 0.0             | 90.0          | 0.0      | 0.0  | 0.0       | 90.0 | 53.34 | 53.34      | 0.0  | 36.66       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 360.0         | 0        | 0.0  | 0         | 360.0 | 250.0 | 160.0      | 0    | 110.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 February 2024 | Disbursement     | 200.0  | 0.0       | 0.0      | 0.0  | 0.0       | 200.0        |
      | 01 February 2024 | Down Payment     | 50.0   | 50.0      | 0.0      | 0.0  | 0.0       | 150.0        |
      | 02 February 2024 | Payout Refund    | 200.0  | 150.0     | 0.0      | 0.0  | 0.0       | 0.0          |
      | 02 February 2024 | Disbursement     | 160.0  | 0.0       | 0.0      | 0.0  | 0.0       | 110.0        |
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule

  @TestRailId:C3049 @AdvancedPaymentAllocation
  Scenario: Verify that second disbursement on overpaid loan is correct when disbursement amount is lower than overpayment amount
    When Admin sets the business date to "01 February 2024"
    When Admin creates a client with random data
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "REAMORTIZATION" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 February 2024  | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 February 2024" with "1000" amount and expected disbursement date on "01 February 2024"
    When Admin successfully disburse the loan on "01 February 2024" with "200" EUR transaction amount
    When Admin sets the business date to "02 February 2024"
    When Customer makes "PAYOUT_REFUND" transaction with "AUTOPAY" payment type on "02 February 2024" with 200 EUR transaction amount and system-generated Idempotency key
    Then Loan status will be "OVERPAID"
    Then Loan has 50 overpaid amount
    When Admin successfully disburse the loan on "02 February 2024" with "28" EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due  | Paid | In advance | Late | Outstanding |
      |    |      | 01 February 2024 |                  | 200.0           |               |          | 0.0  |           | 0.0  | 0.0  |            |      |             |
      | 1  | 0    | 01 February 2024 | 01 February 2024 | 150.0           | 50.0          | 0.0      | 0.0  | 0.0       | 50.0 | 50.0 | 0.0        | 0.0  | 0.0         |
      |    |      | 02 February 2024 |                  | 28.0            |               |          | 0.0  |           | 0.0  | 0.0  |            |      |             |
      | 2  | 0    | 02 February 2024 | 02 February 2024 | 171.0           | 7.0           | 0.0      | 0.0  | 0.0       | 7.0  | 7.0  | 0.0        | 0.0  | 0.0         |
      | 3  | 15   | 16 February 2024 | 02 February 2024 | 114.0           | 57.0          | 0.0      | 0.0  | 0.0       | 57.0 | 57.0 | 57.0       | 0.0  | 0.0         |
      | 4  | 15   | 02 March 2024    | 02 February 2024 | 57.0            | 57.0          | 0.0      | 0.0  | 0.0       | 57.0 | 57.0 | 57.0       | 0.0  | 0.0         |
      | 5  | 15   | 17 March 2024    | 02 February 2024 | 0.0             | 57.0          | 0.0      | 0.0  | 0.0       | 57.0 | 57.0 | 57.0       | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 228.0         | 0        | 0.0  | 0         | 228.0 | 228.0 | 171.0      | 0    | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 February 2024 | Disbursement     | 200.0  | 0.0       | 0.0      | 0.0  | 0.0       | 200.0        |
      | 01 February 2024 | Down Payment     | 50.0   | 50.0      | 0.0      | 0.0  | 0.0       | 150.0        |
      | 02 February 2024 | Payout Refund    | 200.0  | 150.0     | 0.0      | 0.0  | 0.0       | 0.0          |
      | 02 February 2024 | Disbursement     | 28.0   | 0.0       | 0.0      | 0.0  | 0.0       | 0.0          |
    Then Loan status will be "OVERPAID"
    Then Loan has 22 overpaid amount
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule

  @TestRailId:C3068 @AdvancedPaymentAllocation
  Scenario: Verify that Fraud flag can be applied on loan is its every status
    When Admin sets the business date to "01 February 2024"
    When Admin creates a client with random data
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 February 2024  | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    Then Loan status will be "SUBMITTED_AND_PENDING_APPROVAL"
    Then Admin can successfully set Fraud flag to the loan
    Then Admin can successfully unset Fraud flag to the loan
    And Admin successfully approves the loan on "01 February 2024" with "1000" amount and expected disbursement date on "01 February 2024"
    Then Loan status will be "APPROVED"
    Then Admin can successfully set Fraud flag to the loan
    Then Admin can successfully unset Fraud flag to the loan
    When Admin successfully disburse the loan on "01 February 2024" with "1000" EUR transaction amount
    Then Loan status will be "ACTIVE"
    Then Admin can successfully set Fraud flag to the loan
    Then Admin can successfully unset Fraud flag to the loan
    When Admin sets the business date to "02 February 2024"
    And Customer makes "AUTOPAY" repayment on "02 February 2024" with 100 EUR transaction amount
    Then Loan status will be "ACTIVE"
    Then Admin can successfully set Fraud flag to the loan
    Then Admin can successfully unset Fraud flag to the loan
    When Admin sets the business date to "03 February 2024"
    And Customer makes "AUTOPAY" repayment on "03 February 2024" with 750 EUR transaction amount
    Then Loan status will be "OVERPAID"
    Then Loan has 100 overpaid amount
    Then Admin can successfully set Fraud flag to the loan
    Then Admin can successfully unset Fraud flag to the loan
    When Admin sets the business date to "04 February 2024"
    When Customer undo "1"th "Repayment" transaction made on "02 February 2024"
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Admin can successfully set Fraud flag to the loan
    Then Admin can successfully unset Fraud flag to the loan

  @TestRailId:C3090
  Scenario: Verify that disbursement can be done on overpaid loan in case of cummulative loan schedule
    When Admin sets the business date to "01 February 2024"
    When Admin creates a client with random data
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct          | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy                                                             |
      | LP2_DOWNPAYMENT_AUTO | 01 February 2024  | 1000           | 0                      | DECLINING_BALANCE | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | DUE_PENALTY_INTEREST_PRINCIPAL_FEE_IN_ADVANCE_PENALTY_INTEREST_PRINCIPAL_FEE |
    And Admin successfully approves the loan on "01 February 2024" with "1000" amount and expected disbursement date on "01 February 2024"
    When Admin successfully disburse the loan on "01 February 2024" with "1000" EUR transaction amount
    When Admin sets the business date to "02 February 2024"
    And Customer makes "AUTOPAY" repayment on "02 February 2024" with 800 EUR transaction amount
    Then Loan status will be "OVERPAID"
    Then Loan has 50 overpaid amount
    When Admin sets the business date to "03 February 2024"
    When Admin successfully disburse the loan on "03 February 2024" with "20" EUR transaction amount
    Then Loan status will be "OVERPAID"
    Then Loan has 30 overpaid amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 February 2024 |                  | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 February 2024 | 01 February 2024 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      |    |      | 03 February 2024 |                  | 20.0            |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 2  | 0    | 03 February 2024 | 02 February 2024 | 765.0           | 5.0           | 0.0      | 0.0  | 0.0       | 5.0   | 5.0   | 5.0        | 0.0  | 0.0         |
      | 3  | 29   | 01 March 2024    | 02 February 2024 | 510.0           | 255.0         | 0.0      | 0.0  | 0.0       | 255.0 | 255.0 | 255.0      | 0.0  | 0.0         |
      | 4  | 31   | 01 April 2024    | 02 February 2024 | 255.0           | 255.0         | 0.0      | 0.0  | 0.0       | 255.0 | 255.0 | 255.0      | 0.0  | 0.0         |
      | 5  | 30   | 01 May 2024      | 02 February 2024 | 0.0             | 255.0         | 0.0      | 0.0  | 0.0       | 255.0 | 255.0 | 255.0      | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      | 1020.0        | 0        | 0.0  | 0         | 1020.0 | 1020.0 | 770.0      | 0    | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 February 2024 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 February 2024 | Down Payment     | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 02 February 2024 | Repayment        | 800.0  | 770.0     | 0.0      | 0.0  | 0.0       | 0.0          |
      | 03 February 2024 | Disbursement     | 20.0   | 0.0       | 0.0      | 0.0  | 0.0       | 0.0          |
    When Admin sets the business date to "04 February 2024"
    When Admin successfully disburse the loan on "04 February 2024" with "30" EUR transaction amount
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 February 2024 |                  | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 February 2024 | 01 February 2024 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      |    |      | 03 February 2024 |                  | 20.0            |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 2  | 0    | 03 February 2024 | 02 February 2024 | 765.0           | 5.0           | 0.0      | 0.0  | 0.0       | 5.0   | 5.0   | 5.0        | 0.0  | 0.0         |
      |    |      | 04 February 2024 |                  | 30.0            |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 3  | 0    | 04 February 2024 | 02 February 2024 | 787.0           | 8.0           | 0.0      | 0.0  | 0.0       | 8.0   | 8.0   | 8.0        | 0.0  | 0.0         |
      | 4  | 29   | 01 March 2024    | 02 February 2024 | 524.0           | 263.0         | 0.0      | 0.0  | 0.0       | 263.0 | 263.0 | 263.0      | 0.0  | 0.0         |
      | 5  | 31   | 01 April 2024    | 02 February 2024 | 261.0           | 263.0         | 0.0      | 0.0  | 0.0       | 263.0 | 263.0 | 263.0      | 0.0  | 0.0         |
      | 6  | 30   | 01 May 2024      | 02 February 2024 | 0.0             | 261.0         | 0.0      | 0.0  | 0.0       | 261.0 | 261.0 | 261.0      | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      | 1050.0        | 0        | 0.0  | 0         | 1050.0 | 1050.0 | 800.0      | 0    | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 February 2024 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 February 2024 | Down Payment     | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 02 February 2024 | Repayment        | 800.0  | 800.0     | 0.0      | 0.0  | 0.0       | 0.0          |
      | 03 February 2024 | Disbursement     | 20.0   | 0.0       | 0.0      | 0.0  | 0.0       | 0.0          |
      | 04 February 2024 | Disbursement     | 30.0   | 0.0       | 0.0      | 0.0  | 0.0       | 0.0          |
    When Admin sets the business date to "05 February 2024"
    When Admin successfully disburse the loan on "05 February 2024" with "40" EUR transaction amount
    Then Loan status will be "ACTIVE"
    Then Loan has 30 outstanding amount
    Then Loan Repayment schedule has 7 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 February 2024 |                  | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 0    | 01 February 2024 | 01 February 2024 | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 250.0 | 0.0        | 0.0  | 0.0         |
      |    |      | 03 February 2024 |                  | 20.0            |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 2  | 0    | 03 February 2024 | 02 February 2024 | 765.0           | 5.0           | 0.0      | 0.0  | 0.0       | 5.0   | 5.0   | 5.0        | 0.0  | 0.0         |
      |    |      | 04 February 2024 |                  | 30.0            |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 3  | 0    | 04 February 2024 | 02 February 2024 | 787.0           | 8.0           | 0.0      | 0.0  | 0.0       | 8.0   | 8.0   | 8.0        | 0.0  | 0.0         |
      |    |      | 05 February 2024 |                  | 40.0            |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 4  | 0    | 05 February 2024 | 02 February 2024 | 817.0           | 10.0          | 0.0      | 0.0  | 0.0       | 10.0  | 10.0  | 10.0       | 0.0  | 0.0         |
      | 5  | 29   | 01 March 2024    | 02 February 2024 | 544.0           | 273.0         | 0.0      | 0.0  | 0.0       | 273.0 | 273.0 | 273.0      | 0.0  | 0.0         |
      | 6  | 31   | 01 April 2024    | 02 February 2024 | 271.0           | 273.0         | 0.0      | 0.0  | 0.0       | 273.0 | 273.0 | 273.0      | 0.0  | 0.0         |
      | 7  | 30   | 01 May 2024      |                  | 0.0             | 271.0         | 0.0      | 0.0  | 0.0       | 271.0 | 241.0 | 241.0      | 0.0  | 30.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      | 1090.0        | 0        | 0.0  | 0         | 1090.0 | 1060.0 | 810.0      | 0    | 30.0        |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 February 2024 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
      | 01 February 2024 | Down Payment     | 250.0  | 250.0     | 0.0      | 0.0  | 0.0       | 750.0        |
      | 02 February 2024 | Repayment        | 800.0  | 800.0     | 0.0      | 0.0  | 0.0       | 0.0          |
      | 03 February 2024 | Disbursement     | 20.0   | 0.0       | 0.0      | 0.0  | 0.0       | 0.0          |
      | 04 February 2024 | Disbursement     | 30.0   | 0.0       | 0.0      | 0.0  | 0.0       | 0.0          |
      | 05 February 2024 | Disbursement     | 40.0   | 0.0       | 0.0      | 0.0  | 0.0       | 40.0         |
      | 05 February 2024 | Down Payment     | 10.0   | 10.0      | 0.0      | 0.0  | 0.0       | 30.0         |

  @TestRailId:C3103
  Scenario: Verify that fixed length in loan product is inherited by loan account
    When Admin sets the business date to "01 February 2024"
    When Admin creates a client with random data
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_FIXED_LENGTH | 01 February 2024  | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 February 2024" with "1000" amount and expected disbursement date on "01 February 2024"
    When Admin successfully disburse the loan on "01 February 2024" with "1000" EUR transaction amount
    Then LoanDetails has fixedLength field with int value: 90

  @TestRailId:C3104
  Scenario: Verify that fixed length in loan product can be overwrote upon loan account creation
    When Admin sets the business date to "01 February 2024"
    When Admin creates a client with random data
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin creates a fully customized loan with fixed length 60 and with the following data:
      | LoanProduct                                | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_FIXED_LENGTH | 01 February 2024  | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 February 2024" with "1000" amount and expected disbursement date on "01 February 2024"
    When Admin successfully disburse the loan on "01 February 2024" with "1000" EUR transaction amount
    Then LoanDetails has fixedLength field with int value: 60

  @TestRailId:C3119
  Scenario: Verify fixed length loan account Loan schedule - UC1: loan account with fixed length of 40 days (loanTermFrequency: 45 days)
    When Admin sets the business date to "01 February 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with fixed length 40 and with the following data:
      | LoanProduct                                | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_FIXED_LENGTH | 01 February 2024  | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 February 2024" with "1000" amount and expected disbursement date on "01 February 2024"
    When Admin successfully disburse the loan on "01 February 2024" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 February 2024 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 February 2024 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 February 2024 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 02 March 2024    |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 10   | 12 March 2024    |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0        | 0.0  | 0         | 1000.0 | 0.0  | 0.0        | 0    | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 February 2024 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |

  @TestRailId:C3120
  Scenario: Verify fixed length loan account Loan schedule - UC2: loan account with fixed length of 50 days (loanTermFrequency: 45 days)
    When Admin sets the business date to "01 February 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with fixed length 50 and with the following data:
      | LoanProduct                                | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_FIXED_LENGTH | 01 February 2024  | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 February 2024" with "1000" amount and expected disbursement date on "01 February 2024"
    When Admin successfully disburse the loan on "01 February 2024" with "1000" EUR transaction amount
    Then LoanDetails has fixedLength field with int value: 50
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 February 2024 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 February 2024 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 February 2024 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 02 March 2024    |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 20   | 22 March 2024    |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0        | 0.0  | 0         | 1000.0 | 0.0  | 0.0        | 0    | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 February 2024 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |

  @TestRailId:C3121
  Scenario: Verify fixed length loan account Loan schedule - UC3: loan account with fixed length of 5 weeks (loanTermFrequency: 6 weeks)
    When Admin sets the business date to "01 February 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with fixed length 5 and with the following data:
      | LoanProduct                                | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_FIXED_LENGTH | 01 February 2024  | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 6                 | WEEKS                 | 2              | WEEKS                  | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 February 2024" with "1000" amount and expected disbursement date on "01 February 2024"
    When Admin successfully disburse the loan on "01 February 2024" with "1000" EUR transaction amount
    Then LoanDetails has fixedLength field with int value: 5
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 February 2024 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 February 2024 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 14   | 15 February 2024 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 14   | 29 February 2024 |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 7    | 07 March 2024    |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0        | 0.0  | 0         | 1000.0 | 0.0  | 0.0        | 0    | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 February 2024 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |

  @TestRailId:C3122
  Scenario: Verify fixed length loan account Loan schedule - UC4: loan account with fixed length of 7 weeks (loanTermFrequency: 6 weeks)
    When Admin sets the business date to "01 February 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with fixed length 7 and with the following data:
      | LoanProduct                                | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_FIXED_LENGTH | 01 February 2024  | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 6                 | WEEKS                 | 2              | WEEKS                  | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 February 2024" with "1000" amount and expected disbursement date on "01 February 2024"
    When Admin successfully disburse the loan on "01 February 2024" with "1000" EUR transaction amount
    Then LoanDetails has fixedLength field with int value: 7
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 February 2024 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 February 2024 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 14   | 15 February 2024 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 14   | 29 February 2024 |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 21   | 21 March 2024    |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0        | 0.0  | 0         | 1000.0 | 0.0  | 0.0        | 0    | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 February 2024 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |

  @TestRailId:C3123
  Scenario: Verify fixed length loan account Loan schedule - UC5: loan account with fixed length of 5 months (loanTermFrequency: 6 months)
    When Admin sets the business date to "01 February 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with fixed length 5 and with the following data:
      | LoanProduct                                | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_FIXED_LENGTH | 01 February 2024  | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 2              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 February 2024" with "1000" amount and expected disbursement date on "01 February 2024"
    When Admin successfully disburse the loan on "01 February 2024" with "1000" EUR transaction amount
    Then LoanDetails has fixedLength field with int value: 5
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 February 2024 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 February 2024 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 60   | 01 April 2024    |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 61   | 01 June 2024     |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 30   | 01 July 2024     |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0        | 0.0  | 0         | 1000.0 | 0.0  | 0.0        | 0    | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 February 2024 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |

  @TestRailId:C3124
  Scenario: Verify fixed length loan account Loan schedule - UC6: loan account with fixed length of 7 months (loanTermFrequency: 6 months)
    When Admin sets the business date to "01 February 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with fixed length 7 and with the following data:
      | LoanProduct                                | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_FIXED_LENGTH | 01 February 2024  | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 2              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 February 2024" with "1000" amount and expected disbursement date on "01 February 2024"
    When Admin successfully disburse the loan on "01 February 2024" with "1000" EUR transaction amount
    Then LoanDetails has fixedLength field with int value: 7
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 February 2024  |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 February 2024  |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 60   | 01 April 2024     |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 61   | 01 June 2024      |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 92   | 01 September 2024 |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0        | 0.0  | 0         | 1000.0 | 0.0  | 0.0        | 0    | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 February 2024 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |

  @TestRailId:C3125
  Scenario: Verify fixed length loan account Loan schedule - UC7: loan account with fixed length of 5 months but 6 month period / repayment in every 1 month results an ERROR
    When Admin sets the business date to "01 February 2024"
    When Admin creates a client with random data
    When Trying to create a fully customized loan with fixed length 5 and with the following data will result a 403 ERROR:
      | LoanProduct                                | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_FIXED_LENGTH | 01 February 2024  | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |

  @TestRailId:C3126
  Scenario: Verify fixed length loan account Loan schedule - UC8: Reschedule by date a loan account with fixed length of 40 days
    When Admin sets the business date to "01 February 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with fixed length 40 and with the following data:
      | LoanProduct                                | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_FIXED_LENGTH | 01 February 2024  | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 February 2024" with "1000" amount and expected disbursement date on "01 February 2024"
    When Admin successfully disburse the loan on "01 February 2024" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 February 2024 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 February 2024 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 February 2024 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 02 March 2024    |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 10   | 12 March 2024    |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0        | 0.0  | 0         | 1000.0 | 0.0  | 0.0        | 0    | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 February 2024 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    When Admin sets the business date to "02 March 2024"
    When Admin creates and approves Loan reschedule with the following data:
      | rescheduleFromDate | submittedOnDate | adjustedDueDate | graceOnPrincipal | graceOnInterest | extraTerms | newInterestRate |
      | 02 March 2024      | 02 March 2024   | 20 March 2024   |                  |                 |            |                 |
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 February 2024 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 February 2024 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 February 2024 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 33   | 20 March 2024    |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 10   | 30 March 2024    |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0        | 0.0  | 0         | 1000.0 | 0.0  | 0.0        | 0    | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 February 2024 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |

  @TestRailId:C3127
  Scenario: Verify fixed length loan account Loan schedule - UC9: Reschedule by extra terms a loan account with fixed length of 40 days
    When Admin sets the business date to "01 February 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with fixed length 40 and with the following data:
      | LoanProduct                                | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_FIXED_LENGTH | 01 February 2024  | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 February 2024" with "1000" amount and expected disbursement date on "01 February 2024"
    When Admin successfully disburse the loan on "01 February 2024" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 February 2024 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 February 2024 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 February 2024 |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 15   | 02 March 2024    |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 10   | 12 March 2024    |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0        | 0.0  | 0         | 1000.0 | 0.0  | 0.0        | 0    | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 February 2024 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    When Admin sets the business date to "16 February 2024"
    When Admin creates and approves Loan reschedule with the following data:
      | rescheduleFromDate | submittedOnDate  | adjustedDueDate | graceOnPrincipal | graceOnInterest | extraTerms | newInterestRate |
      | 16 February 2024   | 16 February 2024 |                 |                  |                 | 2          |                 |
    # Verify Progressive Loan reschedule behavior - future installments recalculated
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 February 2024 |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 February 2024 |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 15   | 16 February 2024 |           | 600.0           | 150.0         | 0.0      | 0.0  | 0.0       | 150.0 | 0.0  | 0.0        | 0.0  | 150.0       |
      | 3  | 15   | 02 March 2024    |           | 450.0           | 150.0         | 0.0      | 0.0  | 0.0       | 150.0 | 0.0  | 0.0        | 0.0  | 150.0       |
      | 4  | 10   | 12 March 2024    |           | 300.0           | 150.0         | 0.0      | 0.0  | 0.0       | 150.0 | 0.0  | 0.0        | 0.0  | 150.0       |
      | 5  | 15   | 01 April 2024    |           | 150.0           | 150.0         | 0.0      | 0.0  | 0.0       | 150.0 | 0.0  | 0.0        | 0.0  | 150.0       |
      | 6  | 15   | 16 April 2024    |           | 0.0             | 150.0         | 0.0      | 0.0  | 0.0       | 150.0 | 0.0  | 0.0        | 0.0  | 150.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0        | 0.0  | 0         | 1000.0 | 0.0  | 0.0        | 0    | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 February 2024 | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |

  @TestRailId:C3192
  Scenario: Verify that the error message is correct in case of the actual disbursement date is in the past with advanced payment allocation product + submitted on date repaymentStartDateType
    When Admin sets repaymentStartDateType for "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product to "SUBMITTED_ON_DATE"
    When Admin set "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin sets the business date to "01 January 2023"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION | 01 January 2023   | 500            | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2023" with "500" amount and expected disbursement date on "01 January 2023"
    Then Loan status has changed to "Approved"
    Then Admin fails to disburse the loan on "31 December 2022" with "500" EUR transaction amount because disbursement date is earlier than "01 January 2023"
    When Admin sets repaymentStartDateType for "LP2_DOWNPAYMENT_AUTO_ADVANCED_PAYMENT_ALLOCATION" loan product to "DISBURSEMENT_DATE"

  @TestRailId:C3242
  Scenario: Verify that there are no restriction on repayment reversal for overpaid loan for interest bearing product
    When Admin sets the business date to "23 June 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_360_30_INTEREST_RECALCULATION_DAILY_TILL_PRECLOSE | 23 Jun 2024       | 400            | 0                      | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 30                | DAYS                  | 30             | DAYS                   | 1                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "23 June 2024" with "400" amount and expected disbursement date on "23 June 2024"
    When Admin successfully disburse the loan on "23 June 2024" with "400" EUR transaction amount
    And Admin sets the business date to "24 June 2024"
    And Customer makes "AUTOPAY" repayment on "24 June 2024" with 100 EUR transaction amount
    Then Loan Repayment schedule has 1 periods, with the following data for periods:
      | Nr | Days | Date         | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 23 June 2024 |           | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 30   | 23 July 2024 |           | 0.0             | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 100.0 | 100.0      | 0.0  | 300.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 100.0 | 100.0      | 0.0  | 300.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 23 June 2024     | Disbursement     | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 24 June 2024     | Repayment        | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
    And Admin sets the business date to "10 September 2024"
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "10 September 2024" with 400 EUR transaction amount and self-generated Idempotency key
    Then Loan Repayment schedule has 1 periods, with the following data for periods:
      | Nr | Days | Date         | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 23 June 2024 |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 30   | 23 July 2024 | 10 September 2024 | 0.0             | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 400.0 | 100.0      | 300.0 | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 400.0 | 100.0      | 300.0 | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 23 June 2024      | Disbursement           | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 24 June 2024      | Repayment              | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
      | 10 September 2024 | Merchant Issued Refund | 400.0  | 300.0     | 0.0      | 0.0  | 0.0       | 0.0          |
    Then Loan status will be "OVERPAID"
    When Admin makes Credit Balance Refund transaction on "10 September 2024" with 91.21 EUR transaction amount
    Then Loan Repayment schedule has 1 periods, with the following data for periods:
      | Nr | Days | Date         | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 23 June 2024 |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 30   | 23 July 2024 | 10 September 2024 | 0.0             | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 400.0 | 100.0      | 300.0 | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 400.0 | 100.0      | 300.0 | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 23 June 2024      | Disbursement           | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 24 June 2024      | Repayment              | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
      | 10 September 2024 | Merchant Issued Refund | 400.0  | 300.0     | 0.0      | 0.0  | 0.0       | 0.0          |
      | 10 September 2024 | Credit Balance Refund  | 91.21  | 0.0       | 0.0      | 0.0  | 0.0       | 0.0          |
    When Customer undo "1"th repayment on "24 June 2024"
    Then Loan Repayment schedule has 2 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late  | Outstanding |
      |    |      | 23 June 2024      |                   | 400.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |       |             |
      | 1  | 30   | 23 July 2024      | 10 September 2024 | 0.0             | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 400.0 | 0.0        | 400.0 | 0.0         |
      | 2  | 49   | 10 September 2024 |                   | 0.0             | 91.21         | 0.0      | 0.0  | 0.0       | 91.21 | 0.0   | 0.0        | 0.0   | 91.21       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late  | Outstanding |
      | 491.21        | 0.0      | 0.0  | 0.0       | 491.21 | 400.0 | 0.0        | 400.0 | 91.21       |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 23 June 2024      | Disbursement           | 400.0  | 0.0       | 0.0      | 0.0  | 0.0       | 400.0        |
      | 24 June 2024      | Repayment              | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 300.0        |
      | 10 September 2024 | Merchant Issued Refund | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 0.0          |
      | 10 September 2024 | Credit Balance Refund  | 91.21  | 91.21     | 0.0      | 0.0  | 0.0       | 91.21        |
    Then In Loan Transactions the "2"th Transaction has Transaction type="Repayment" and is reverted

  @TestRailId:C3282
  Scenario: Verify reversal of related interest refund transaction after merchant issued refund transactions is reversed
    When Admin sets the business date to "01 January 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                         | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_ACTUAL_ACTUAL_INTEREST_REFUND_FULL | 01 January 2024   | 1000           | 9.9                    | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 4                 | MONTHS                | 1              | MONTHS                 | 4                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2024" with "1000" amount and expected disbursement date on "01 January 2024"
    When Admin successfully disburse the loan on "01 January 2024" with "1000" EUR transaction amount
    When Admin sets the business date to "22 January 2024"
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "22 January 2024" with 100 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |                 | 1000.0          |               |          | 0.0  |           | 0.0    | 0.0    |            |      |             |
      | 1  | 31   | 01 February 2024 |                 | 752.97          | 247.03        | 8.11     | 0.0  | 0.0       | 255.14 | 100.57 | 100.57     | 0.0  | 154.57      |
      | 2  | 29   | 01 March 2024    |                 | 503.74          | 249.23        | 5.91     | 0.0  | 0.0       | 255.14 | 0.0    | 0.0        | 0.0  | 255.14      |
      | 3  | 31   | 01 April 2024    |                 | 252.82          | 250.92        | 4.22     | 0.0  | 0.0       | 255.14	| 0.0    | 0.0        | 0.0  | 255.14      |
      | 4  | 30   | 01 May 2024      |                 | 0.0             | 252.82        | 2.05     | 0.0  | 0.0       | 254.87 | 0.0    | 0.0        | 0.0  | 254.87      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid    | In advance | Late | Outstanding |
      | 1000.0        | 20.29    | 0.0  | 0.0       | 1020.29 | 100.57  | 100.57     | 0.0  | 919.72      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 01 January 2024  | Disbursement           | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
      | 22 January 2024  | Merchant Issued Refund | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 900.0        | false    | false    |
      | 22 January 2024  | Interest Refund        | 0.57   | 0.57      | 0.0      | 0.0  | 0.0       | 899.43       | false    | false    |
    When Customer undo "1"th "Merchant Issued Refund" transaction made on "22 January 2024" with linked "Interest Refund" transaction
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |                 | 1000.0          |               |          | 0.0  |           | 0.0    | 0.0    |            |      |             |
      | 1  | 31   | 01 February 2024 |                 | 753.25          | 246.75        | 8.39     | 0.0  | 0.0       | 255.14 | 0.0    | 0.0        | 0.0  | 255.14      |
      | 2  | 29   | 01 March 2024    |                 | 504.02          | 249.23        | 5.91     | 0.0  | 0.0       | 255.14 | 0.0    | 0.0        | 0.0  | 255.14      |
      | 3  | 31   | 01 April 2024    |                 | 253.11          | 250.91        | 4.23     | 0.0  | 0.0       | 255.14	| 0.0    | 0.0        | 0.0  | 255.14      |
      | 4  | 30   | 01 May 2024      |                 | 0.0             | 253.11        | 2.05     | 0.0  | 0.0       | 255.16 | 0.0    | 0.0        | 0.0  | 255.16      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid    | In advance | Late | Outstanding |
      | 1000.0        | 20.58    | 0.0  | 0.0       | 1020.58 | 0.0     | 0.0        | 0.0  | 1020.58     |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 01 January 2024  | Disbursement           | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
      | 22 January 2024  | Merchant Issued Refund | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 900.0        | true     | false    |
      | 22 January 2024  | Interest Refund        | 0.57   | 0.57      | 0.0      | 0.0  | 0.0       | 899.43       | true     | false    |
    Then In Loan Transactions the "3"th Transaction has Transaction type="Interest Refund" and is reverted

  @TestRailId:C3283
  Scenario: Verify reversal of related interest refund transaction after payout refund transactions is reversed
    When Admin sets the business date to "01 January 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                         | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_ACTUAL_ACTUAL_INTEREST_REFUND_FULL | 01 January 2024   | 1000           | 9.9                    | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 4                 | MONTHS                | 1              | MONTHS                 | 4                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2024" with "1000" amount and expected disbursement date on "01 January 2024"
    When Admin successfully disburse the loan on "01 January 2024" with "1000" EUR transaction amount
    When Admin sets the business date to "22 January 2024"
    When Admin makes "PAYOUT_REFUND" transaction with "AUTOPAY" payment type on "22 January 2024" with 100 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |                 | 1000.0          |               |          | 0.0  |           | 0.0    | 0.0    |            |      |             |
      | 1  | 31   | 01 February 2024 |                 | 752.97          | 247.03        | 8.11     | 0.0  | 0.0       | 255.14 | 100.57 | 100.57     | 0.0  | 154.57      |
      | 2  | 29   | 01 March 2024    |                 | 503.74          | 249.23        | 5.91     | 0.0  | 0.0       | 255.14 | 0.0    | 0.0        | 0.0  | 255.14      |
      | 3  | 31   | 01 April 2024    |                 | 252.82          | 250.92        | 4.22     | 0.0  | 0.0       | 255.14	| 0.0    | 0.0        | 0.0  | 255.14      |
      | 4  | 30   | 01 May 2024      |                 | 0.0             | 252.82        | 2.05     | 0.0  | 0.0       | 254.87 | 0.0    | 0.0        | 0.0  | 254.87      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid    | In advance | Late | Outstanding |
      | 1000.0        | 20.29    | 0.0  | 0.0       | 1020.29 | 100.57  | 100.57     | 0.0  | 919.72      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 01 January 2024  | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
      | 22 January 2024  | Payout Refund    | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 900.0        | false    | false    |
      | 22 January 2024  | Interest Refund  | 0.57   | 0.57      | 0.0      | 0.0  | 0.0       | 899.43       | false    | false    |
    When Customer undo "1"th "Payout Refund" transaction made on "22 January 2024" with linked "Interest Refund" transaction
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |                 | 1000.0          |               |          | 0.0  |           | 0.0    | 0.0    |            |      |             |
      | 1  | 31   | 01 February 2024 |                 | 753.25          | 246.75        | 8.39     | 0.0  | 0.0       | 255.14 | 0.0    | 0.0        | 0.0  | 255.14      |
      | 2  | 29   | 01 March 2024    |                 | 504.02          | 249.23        | 5.91     | 0.0  | 0.0       | 255.14 | 0.0    | 0.0        | 0.0  | 255.14      |
      | 3  | 31   | 01 April 2024    |                 | 253.11          | 250.91        | 4.23     | 0.0  | 0.0       | 255.14	| 0.0    | 0.0        | 0.0  | 255.14      |
      | 4  | 30   | 01 May 2024      |                 | 0.0             | 253.11        | 2.05     | 0.0  | 0.0       | 255.16 | 0.0    | 0.0        | 0.0  | 255.16      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid    | In advance | Late | Outstanding |
      | 1000.0        | 20.58    | 0.0  | 0.0       | 1020.58 | 0.0     | 0.0        | 0.0  | 1020.58     |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 01 January 2024  | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
      | 22 January 2024  | Payout Refund    | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 900.0        | true     | false    |
      | 22 January 2024  | Interest Refund  | 0.57   | 0.57      | 0.0      | 0.0  | 0.0       | 899.43       | true     | false    |
    Then In Loan Transactions the "3"th Transaction has Transaction type="Interest Refund" and is reverted

  @TestRailId:C3324
  Scenario: Verify interest refund transaction after merchant issued refund transactions is forbidden to be reversed
    When Admin sets the business date to "01 January 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                         | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_ACTUAL_ACTUAL_INTEREST_REFUND_FULL | 01 January 2024   | 1000           | 9.9                    | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 4                 | MONTHS                | 1              | MONTHS                 | 4                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2024" with "1000" amount and expected disbursement date on "01 January 2024"
    When Admin successfully disburse the loan on "01 January 2024" with "1000" EUR transaction amount
    When Admin sets the business date to "22 January 2024"
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "22 January 2024" with 100 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |                 | 1000.0          |               |          | 0.0  |           | 0.0    | 0.0    |            |      |             |
      | 1  | 31   | 01 February 2024 |                 | 752.97          | 247.03        | 8.11     | 0.0  | 0.0       | 255.14 | 100.57 | 100.57     | 0.0  | 154.57      |
      | 2  | 29   | 01 March 2024    |                 | 503.74          | 249.23        | 5.91     | 0.0  | 0.0       | 255.14 | 0.0    | 0.0        | 0.0  | 255.14      |
      | 3  | 31   | 01 April 2024    |                 | 252.82          | 250.92        | 4.22     | 0.0  | 0.0       | 255.14	| 0.0    | 0.0        | 0.0  | 255.14      |
      | 4  | 30   | 01 May 2024      |                 | 0.0             | 252.82        | 2.05     | 0.0  | 0.0       | 254.87 | 0.0    | 0.0        | 0.0  | 254.87      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid    | In advance | Late | Outstanding |
      | 1000.0        | 20.29    | 0.0  | 0.0       | 1020.29 | 100.57  | 100.57     | 0.0  | 919.72      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 01 January 2024  | Disbursement           | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
      | 22 January 2024  | Merchant Issued Refund | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 900.0        | false    | false    |
      | 22 January 2024  | Interest Refund        | 0.57   | 0.57      | 0.0      | 0.0  | 0.0       | 899.43       | false    | false    |
    When Customer is forbidden to undo "1"th "Interest Refund" transaction made on "22 January 2024"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |                 | 1000.0          |               |          | 0.0  |           | 0.0    | 0.0    |            |      |             |
      | 1  | 31   | 01 February 2024 |                 | 752.97          | 247.03        | 8.11     | 0.0  | 0.0       | 255.14 | 100.57 | 100.57     | 0.0  | 154.57      |
      | 2  | 29   | 01 March 2024    |                 | 503.74          | 249.23        | 5.91     | 0.0  | 0.0       | 255.14 | 0.0    | 0.0        | 0.0  | 255.14      |
      | 3  | 31   | 01 April 2024    |                 | 252.82          | 250.92        | 4.22     | 0.0  | 0.0       | 255.14	| 0.0    | 0.0        | 0.0  | 255.14      |
      | 4  | 30   | 01 May 2024      |                 | 0.0             | 252.82        | 2.05     | 0.0  | 0.0       | 254.87 | 0.0    | 0.0        | 0.0  | 254.87      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid    | In advance | Late | Outstanding |
      | 1000.0        | 20.29    | 0.0  | 0.0       | 1020.29 | 100.57  | 100.57     | 0.0  | 919.72      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 01 January 2024  | Disbursement           | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
      | 22 January 2024  | Merchant Issued Refund | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 900.0        | false    | false    |
      | 22 January 2024  | Interest Refund        | 0.57   | 0.57      | 0.0      | 0.0  | 0.0       | 899.43       | false    | false    |

  @TestRailId:C3325
  Scenario: Verify interest refund transaction after payout refund transactions is forbidden to be reversed
    When Admin sets the business date to "01 January 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                         | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_ACTUAL_ACTUAL_INTEREST_REFUND_FULL | 01 January 2024   | 1000           | 9.9                    | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 4                 | MONTHS                | 1              | MONTHS                 | 4                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2024" with "1000" amount and expected disbursement date on "01 January 2024"
    When Admin successfully disburse the loan on "01 January 2024" with "1000" EUR transaction amount
    When Admin sets the business date to "22 January 2024"
    When Admin makes "PAYOUT_REFUND" transaction with "AUTOPAY" payment type on "22 January 2024" with 100 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |                 | 1000.0          |               |          | 0.0  |           | 0.0    | 0.0    |            |      |             |
      | 1  | 31   | 01 February 2024 |                 | 752.97          | 247.03        | 8.11     | 0.0  | 0.0       | 255.14 | 100.57 | 100.57     | 0.0  | 154.57      |
      | 2  | 29   | 01 March 2024    |                 | 503.74          | 249.23        | 5.91     | 0.0  | 0.0       | 255.14 | 0.0    | 0.0        | 0.0  | 255.14      |
      | 3  | 31   | 01 April 2024    |                 | 252.82          | 250.92        | 4.22     | 0.0  | 0.0       | 255.14	| 0.0    | 0.0        | 0.0  | 255.14      |
      | 4  | 30   | 01 May 2024      |                 | 0.0             | 252.82        | 2.05     | 0.0  | 0.0       | 254.87 | 0.0    | 0.0        | 0.0  | 254.87      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid    | In advance | Late | Outstanding |
      | 1000.0        | 20.29    | 0.0  | 0.0       | 1020.29 | 100.57  | 100.57     | 0.0  | 919.72      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 01 January 2024  | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
      | 22 January 2024  | Payout Refund    | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 900.0        | false    | false    |
      | 22 January 2024  | Interest Refund  | 0.57   | 0.57      | 0.0      | 0.0  | 0.0       | 899.43       | false    | false    |
    When Customer is forbidden to undo "1"th "Interest Refund" transaction made on "22 January 2024"
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |                 | 1000.0          |               |          | 0.0  |           | 0.0    | 0.0    |            |      |             |
      | 1  | 31   | 01 February 2024 |                 | 752.97          | 247.03        | 8.11     | 0.0  | 0.0       | 255.14 | 100.57 | 100.57     | 0.0  | 154.57      |
      | 2  | 29   | 01 March 2024    |                 | 503.74          | 249.23        | 5.91     | 0.0  | 0.0       | 255.14 | 0.0    | 0.0        | 0.0  | 255.14      |
      | 3  | 31   | 01 April 2024    |                 | 252.82          | 250.92        | 4.22     | 0.0  | 0.0       | 255.14	| 0.0    | 0.0        | 0.0  | 255.14      |
      | 4  | 30   | 01 May 2024      |                 | 0.0             | 252.82        | 2.05     | 0.0  | 0.0       | 254.87 | 0.0    | 0.0        | 0.0  | 254.87      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid    | In advance | Late | Outstanding |
      | 1000.0        | 20.29    | 0.0  | 0.0       | 1020.29 | 100.57  | 100.57     | 0.0  | 919.72      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 01 January 2024  | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
      | 22 January 2024  | Payout Refund    | 100.0  | 100.0     | 0.0      | 0.0  | 0.0       | 900.0        | false    | false    |
      | 22 January 2024  | Interest Refund  | 0.57   | 0.57      | 0.0      | 0.0  | 0.0       | 899.43       | false    | false    |

  @TestRailId:C3300
  Scenario: Early pay-off loan with interest, TILL_PRECLOSE product
    When Admin sets the business date to "01 January 2024"
    When Admin creates a client with random data
    When Admin set "LP2_ADV_PYMNT_INTEREST_DAILY_EMI_360_30_INTEREST_RECALCULATION_DAILY_TILL_PRECLOSE" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                              | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_360_30_INTEREST_RECALCULATION_DAILY_TILL_PRECLOSE | 01 January 2024   | 100            | 7                      | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2024" with "100" amount and expected disbursement date on "01 January 2024"
    When Admin successfully disburse the loan on "01 January 2024" with "100" EUR transaction amount
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |           | 100.0           |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 31   | 01 February 2024 |           | 83.57           | 16.43         | 0.58     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 2  | 29   | 01 March 2024    |           | 67.05           | 16.52         | 0.49     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 3  | 31   | 01 April 2024    |           | 50.43           | 16.62         | 0.39     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 4  | 30   | 01 May 2024      |           | 33.71           | 16.72         | 0.29     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 5  | 31   | 01 June 2024     |           | 16.9            | 16.81         | 0.2      | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 6  | 30   | 01 July 2024     |           | 0.0             | 16.9          | 0.1      | 0.0  | 0.0       | 17.0  | 0.0  | 0.0        | 0.0  | 17.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 100.0         | 2.05     | 0.0  | 0.0       | 102.05 | 0.0  | 0.0        | 0.0  | 102.05      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2024  | Disbursement     | 100.0  | 0.0       | 0.0      | 0.0  | 0.0       | 100.0        | false    |
    When Admin sets the business date to "01 February 2024"
    And Customer makes "AUTOPAY" repayment on "01 February 2024" with 17.01 EUR transaction amount
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |                  | 100.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 31   | 01 February 2024 | 01 February 2024 | 83.57           | 16.43         | 0.58     | 0.0  | 0.0       | 17.01 | 17.01 | 0.0        | 0.0  | 0.0         |
      | 2  | 29   | 01 March 2024    |                  | 67.05           | 16.52         | 0.49     | 0.0  | 0.0       | 17.01 | 0.0   | 0.0        | 0.0  | 17.01       |
      | 3  | 31   | 01 April 2024    |                  | 50.43           | 16.62         | 0.39     | 0.0  | 0.0       | 17.01 | 0.0   | 0.0        | 0.0  | 17.01       |
      | 4  | 30   | 01 May 2024      |                  | 33.71           | 16.72         | 0.29     | 0.0  | 0.0       | 17.01 | 0.0   | 0.0        | 0.0  | 17.01       |
      | 5  | 31   | 01 June 2024     |                  | 16.9            | 16.81         | 0.2      | 0.0  | 0.0       | 17.01 | 0.0   | 0.0        | 0.0  | 17.01       |
      | 6  | 30   | 01 July 2024     |                  | 0.0             | 16.9          | 0.1      | 0.0  | 0.0       | 17.0  | 0.0   | 0.0        | 0.0  | 17.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 100.0         | 2.05     | 0.0  | 0.0       | 102.05 | 17.01 | 0.0        | 0.0  | 85.04       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2024  | Disbursement     | 100.0  | 0.0       | 0.0      | 0.0  | 0.0       | 100.0        | false    |
      | 01 February 2024 | Repayment        | 17.01  | 16.43     | 0.58     | 0.0  | 0.0       | 83.57        | false    |
    When Admin sets the business date to "15 February 2024"
    When Loan Pay-off is made on "15 February 2024"
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |                  | 100.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 31   | 01 February 2024 | 01 February 2024 | 83.57           | 16.43         | 0.58     | 0.0  | 0.0       | 17.01 | 17.01 | 0.0        | 0.0  | 0.0         |
      | 2  | 29   | 01 March 2024    | 15 February 2024 | 66.8            | 16.77         | 0.24     | 0.0  | 0.0       | 17.01 | 17.01 | 17.01      | 0.0  | 0.0         |
      | 3  | 31   | 01 April 2024    | 15 February 2024 | 49.79           | 17.01         | 0.0      | 0.0  | 0.0       | 17.01 | 17.01 | 17.01      | 0.0  | 0.0         |
      | 4  | 30   | 01 May 2024      | 15 February 2024 | 32.78           | 17.01         | 0.0      | 0.0  | 0.0       | 17.01 | 17.01 | 17.01      | 0.0  | 0.0         |
      | 5  | 31   | 01 June 2024     | 15 February 2024 | 15.77           | 17.01         | 0.0      | 0.0  | 0.0       | 17.01 | 17.01 | 17.01      | 0.0  | 0.0         |
      | 6  | 30   | 01 July 2024     | 15 February 2024 | 0.0             | 15.77         | 0.0      | 0.0  | 0.0       | 15.77 | 15.77 | 15.77      | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2024  | Disbursement     | 100.0  | 0.0       | 0.0      | 0.0  | 0.0       | 100.0        | false    |
      | 01 February 2024 | Repayment        | 17.01  | 16.43     | 0.58     | 0.0  | 0.0       | 83.57        | false    |
      | 15 February 2024 | Repayment        | 83.81  | 83.57     | 0.24     | 0.0  | 0.0       | 0.0          | false    |
      | 15 February 2024 | Accrual          | 0.82   |   0.0     | 0.82     | 0.0  | 0.0       | 0.0          | false    |
    Then Loan's all installments have obligations met

  @TestRailId:C3483
  Scenario: Early pay-off loan with interest, TILL_REST_FREQUENCY_DATE product
    When Admin sets the business date to "01 January 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                              | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_360_30_INTEREST_RECALCULATION_DAILY_TILL_REST_FREQUENCY_DATE | 01 January 2024   | 100            | 7                      | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2024" with "100" amount and expected disbursement date on "01 January 2024"
    When Admin successfully disburse the loan on "01 January 2024" with "100" EUR transaction amount
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |           | 100.0           |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 31   | 01 February 2024 |           | 83.57           | 16.43         | 0.58     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 2  | 29   | 01 March 2024    |           | 67.05           | 16.52         | 0.49     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 3  | 31   | 01 April 2024    |           | 50.43           | 16.62         | 0.39     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 4  | 30   | 01 May 2024      |           | 33.71           | 16.72         | 0.29     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 5  | 31   | 01 June 2024     |           | 16.9            | 16.81         | 0.2      | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 6  | 30   | 01 July 2024     |           | 0.0             | 16.9          | 0.1      | 0.0  | 0.0       | 17.0  | 0.0  | 0.0        | 0.0  | 17.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 100.0         | 2.05     | 0.0  | 0.0       | 102.05 | 0.0  | 0.0        | 0.0  | 102.05      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2024  | Disbursement     | 100.0  | 0.0       | 0.0      | 0.0  | 0.0       | 100.0        | false    |
    When Admin sets the business date to "01 February 2024"
    And Customer makes "AUTOPAY" repayment on "01 February 2024" with 17.01 EUR transaction amount
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |                  | 100.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 31   | 01 February 2024 | 01 February 2024 | 83.57           | 16.43         | 0.58     | 0.0  | 0.0       | 17.01 | 17.01 | 0.0        | 0.0  | 0.0         |
      | 2  | 29   | 01 March 2024    |                  | 67.05           | 16.52         | 0.49     | 0.0  | 0.0       | 17.01 | 0.0   | 0.0        | 0.0  | 17.01       |
      | 3  | 31   | 01 April 2024    |                  | 50.43           | 16.62         | 0.39     | 0.0  | 0.0       | 17.01 | 0.0   | 0.0        | 0.0  | 17.01       |
      | 4  | 30   | 01 May 2024      |                  | 33.71           | 16.72         | 0.29     | 0.0  | 0.0       | 17.01 | 0.0   | 0.0        | 0.0  | 17.01       |
      | 5  | 31   | 01 June 2024     |                  | 16.9            | 16.81         | 0.2      | 0.0  | 0.0       | 17.01 | 0.0   | 0.0        | 0.0  | 17.01       |
      | 6  | 30   | 01 July 2024     |                  | 0.0             | 16.9          | 0.1      | 0.0  | 0.0       | 17.0  | 0.0   | 0.0        | 0.0  | 17.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 100.0         | 2.05     | 0.0  | 0.0       | 102.05 | 17.01 | 0.0        | 0.0  | 85.04       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2024  | Disbursement     | 100.0  | 0.0       | 0.0      | 0.0  | 0.0       | 100.0        | false    |
      | 01 February 2024 | Repayment        | 17.01  | 16.43     | 0.58     | 0.0  | 0.0       | 83.57        | false    |
    When Admin sets the business date to "15 February 2024"
    When Loan Pay-off is made on "15 February 2024"
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |                  | 100.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 31   | 01 February 2024 | 01 February 2024 | 83.57           | 16.43         | 0.58     | 0.0  | 0.0       | 17.01 | 17.01 | 0.0        | 0.0  | 0.0         |
      | 2  | 29   | 01 March 2024    | 15 February 2024 | 67.05           | 16.52         | 0.49     | 0.0  | 0.0       | 17.01 | 17.01 | 17.01      | 0.0  | 0.0         |
      | 3  | 31   | 01 April 2024    | 15 February 2024 | 50.04           | 17.01         | 0.0      | 0.0  | 0.0       | 17.01 | 17.01 | 17.01      | 0.0  | 0.0         |
      | 4  | 30   | 01 May 2024      | 15 February 2024 | 33.03           | 17.01         | 0.0      | 0.0  | 0.0       | 17.01 | 17.01 | 17.01      | 0.0  | 0.0         |
      | 5  | 31   | 01 June 2024     | 15 February 2024 | 16.02           | 17.01         | 0.0      | 0.0  | 0.0       | 17.01 | 17.01 | 17.01      | 0.0  | 0.0         |
      | 6  | 30   | 01 July 2024     | 15 February 2024 | 0.0             | 16.02         | 0.0      | 0.0  | 0.0       | 16.02 | 16.02 | 16.02      | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance | Reverted |
      | 01 January 2024  | Disbursement     | 100.0  | 0.0       | 0.0      | 0.0  | 0.0       | 100.0        | false    |
      | 01 February 2024 | Repayment        | 17.01  | 16.43     | 0.58     | 0.0  | 0.0       | 83.57        | false    |
      | 15 February 2024 | Repayment        | 84.06  | 83.57     | 0.49     | 0.0  | 0.0       | 0.0          | false    |
      | 15 February 2024 | Accrual          | 1.07   |   0.0     | 1.07     | 0.0  | 0.0       | 0.0          | false    |
    Then Loan's all installments have obligations met

  @TestRailId:C3484
  Scenario: Interest recalculation - S1 daily for overdue loan
    Given Global configuration "enable-business-date" is enabled
    When Admin sets the business date to "1 January 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                                  | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_360_30_INTEREST_RECALCULATION_DAILY_TILL_PRECLOSE | 01 January 2024   | 100            | 7.0                    | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                 | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "1 January 2024" with "100" amount and expected disbursement date on "1 January 2024"
    When Admin successfully disburse the loan on "1 January 2024" with "100" EUR transaction amount
    When Admin sets the business date to "15 July 2024"
    When Admin runs inline COB job for Loan
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |           | 100.0           |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 31   | 01 February 2024 |           | 83.57           | 16.43         | 0.58     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 2  | 29   | 01 March 2024    |           | 67.14           | 16.43         | 0.58     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 3  | 31   | 01 April 2024    |           | 50.71           | 16.43         | 0.58     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 4  | 30   | 01 May 2024      |           | 34.28           | 16.43         | 0.58     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 5  | 31   | 01 June 2024     |           | 17.85           | 16.43         | 0.58     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 6  | 30   | 01 July 2024     |           | 0.0             | 17.85         | 0.58     | 0.0  | 0.0       | 18.43 | 0.0  | 0.0        | 0.0  | 18.43       |

  @TestRailId:C3485
  Scenario: Interest recalculation - S2 2 overdue
    Given Global configuration "enable-business-date" is enabled
    When Admin sets the business date to "1 January 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                                  | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_360_30_INTEREST_RECALCULATION_DAILY_TILL_PRECLOSE | 01 January 2024   | 100            | 7.0                    | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                 | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "1 January 2024" with "100" amount and expected disbursement date on "1 January 2024"
    When Admin successfully disburse the loan on "1 January 2024" with "100" EUR transaction amount
    When Admin sets the business date to "10 March 2024"
    When Admin runs inline COB job for Loan
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |           | 100.0           |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 31   | 01 February 2024 |           | 83.57           | 16.43         | 0.58     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 2  | 29   | 01 March 2024    |           | 67.14           | 16.43         | 0.58     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 3  | 31   | 01 April 2024    |           | 50.58           | 16.56         | 0.45     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 4  | 30   | 01 May 2024      |           | 33.87           | 16.71         | 0.3      | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 5  | 31   | 01 June 2024     |           | 17.06           | 16.81         | 0.2      | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 6  | 30   | 01 July 2024     |           | 0.0             | 17.06         | 0.1      | 0.0  | 0.0       | 17.16 | 0.0  | 0.0        | 0.0  | 17.16       |

  @TestRailId:C3486
  Scenario: Interest recalculation - S3 1 paid, 1 overdue
    Given Global configuration "enable-business-date" is enabled
    When Admin sets the business date to "1 January 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                                  | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_360_30_INTEREST_RECALCULATION_DAILY_TILL_PRECLOSE | 01 January 2024   | 100            | 7.0                    | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                 | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "1 January 2024" with "100" amount and expected disbursement date on "1 January 2024"
    When Admin successfully disburse the loan on "1 January 2024" with "100" EUR transaction amount
    When Admin sets the business date to "1 February 2024"
    And Customer makes "AUTOPAY" repayment on "01 February 2024" with 17.01 EUR transaction amount
    When Admin sets the business date to "10 March 2024"
    When Admin runs inline COB job for Loan
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |                  | 100.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 31   | 01 February 2024 | 01 February 2024 | 83.57           | 16.43         | 0.58     | 0.0  | 0.0       | 17.01 | 17.01 | 0.0        | 0.0  | 0.0         |
      | 2  | 29   | 01 March 2024    |                  | 67.05           | 16.52         | 0.49     | 0.0  | 0.0       | 17.01 | 0.0   | 0.0        | 0.0  | 17.01       |
      | 3  | 31   | 01 April 2024    |                  | 50.46           | 16.59         | 0.42     | 0.0  | 0.0       | 17.01 | 0.0   | 0.0        | 0.0  | 17.01       |
      | 4  | 30   | 01 May 2024      |                  | 33.74           | 16.72         | 0.29     | 0.0  | 0.0       | 17.01 | 0.0   | 0.0        | 0.0  | 17.01       |
      | 5  | 31   | 01 June 2024     |                  | 16.93           | 16.81         | 0.2      | 0.0  | 0.0       | 17.01 | 0.0   | 0.0        | 0.0  | 17.01       |
      | 6  | 30   | 01 July 2024     |                  | 0.0             | 16.93         | 0.1      | 0.0  | 0.0       | 17.03 | 0.0   | 0.0        | 0.0  | 17.03       |

  @TestRailId:C3487
  Scenario: Loan Details Emi Amount Variations - AssociationsAll
    Given Global configuration "is-interest-to-be-recovered-first-when-greater-than-emi" is enabled
    Given Global configuration "enable-business-date" is enabled
    When Admin sets the business date to "1 January 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with emi and the following data:
      | LoanProduct                                                                                     | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy                        |
      | LP1_INTEREST_DECLINING_BALANCE_SAR_RECALCULATION_SAME_AS_REPAYMENT_COMPOUNDING_NONE_MULTIDISB   | 01 January 2023   | 10000          | 12                     | DECLINING_BALANCE | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | PENALTIES_FEES_INTEREST_PRINCIPAL_ORDER |
    And Admin successfully approves the loan on "1 January 2023" with "100" amount and expected disbursement date on "1 January 2023"
    And Admin successfully disburse the loan on "1 January 2023" with "100" EUR transaction amount and "50" fixed emi amount
    Then Loan emi amount variations has 1 variation, with the following data:
      | Term Type Id | Term Type Code         | Term Type Value | Applicable From | Decimal Value | Date Value      | Is Specific To Installment | Is Processed |
      | 1            | loanTermType.emiAmount | emiAmount       | 01 January 2023 | 50.0          |                 | false                      |              |

  @TestRailId:C3488
  Scenario: Loan Details Loan Term Variations
    Given Global configuration "is-interest-to-be-recovered-first-when-greater-than-emi" is enabled
    Given Global configuration "enable-business-date" is enabled
    When Admin sets the business date to "1 January 2023"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with emi and the following data:
      | LoanProduct                                                                                     | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy                        |
      | LP1_INTEREST_DECLINING_BALANCE_SAR_RECALCULATION_SAME_AS_REPAYMENT_COMPOUNDING_NONE_MULTIDISB   | 01 January 2023   | 10000          | 12                     | DECLINING_BALANCE | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | PENALTIES_FEES_INTEREST_PRINCIPAL_ORDER |
    And Admin successfully approves the loan on "1 January 2023" with "100" amount and expected disbursement date on "1 January 2023"
    And Admin successfully disburse the loan on "1 January 2023" with "100" EUR transaction amount, "50" EUR fixed emi amount and adjust repayment date on "15 January 2023"
    Then Loan term variations has 2 variation, with the following data:
      | Term Type Id | Term Type Code         | Term Type Value | Applicable From  | Decimal Value | Date Value      | Is Specific To Installment | Is Processed |
      | 1            | loanTermType.emiAmount | emiAmount       | 01 January 2023  | 50.0          |                 | false                      |              |
      | 4            | loanTermType.dueDate   | dueDate         | 01 February 2023 | 50.0          | 15 January 2023 | false                      |              |

  @TestRailId:C3489
  Scenario: EMI calculation with 360/30 Early repayment - Last installment strategy - UC1: Multiple early repayment
    When Admin sets the business date to "01 January 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                                              | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_360_30_IR_DAILY_TILL_PRECLOSE_LAST_INSTALLMENT_STRATEGY | 01 January 2024   | 100            | 7.0                    | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2024" with "100" amount and expected disbursement date on "01 January 2024"
    When Admin successfully disburse the loan on "01 January 2024" with "100" EUR transaction amount
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 January 2024  |           | 100.0           |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 31   | 01 February 2024 |           | 83.57           | 16.43         | 0.58     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 2  | 29   | 01 March 2024    |           | 67.05           | 16.52         | 0.49     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 3  | 31   | 01 April 2024    |           | 50.43           | 16.62         | 0.39     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 4  | 30   | 01 May 2024      |           | 33.71           | 16.72         | 0.29     | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 5  | 31   | 01 June 2024     |           | 16.9            | 16.81         | 0.2      | 0.0  | 0.0       | 17.01 | 0.0  | 0.0        | 0.0  | 17.01       |
      | 6  | 30   | 01 July 2024     |           | 0.0             | 16.9          | 0.1      | 0.0  | 0.0       | 17.0  | 0.0  | 0.0        | 0.0  | 17.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 100.0         | 2.05     | 0.0  | 0.0       | 102.05 | 0.0  | 0.0        | 0.0  | 102.05      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2024  | Disbursement     | 100.0  | 0.0       | 0.0      | 0.0  | 0.0       | 100.0        |
#    --- Early repayment on 15 January 2024 ---
    When Admin sets the business date to "15 January 2024"
    And Customer makes "AUTOPAY" repayment on "15 January 2024" with 15.00 EUR transaction amount
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance  | Late | Outstanding |
      |    |      | 01 January 2024  |           | 100.0           |               |          | 0.0  |           | 0.0   | 0.0   |             |      |             |
      | 1  | 31   | 01 February 2024 |           | 83.53           | 16.47         | 0.54     | 0.0  | 0.0       | 17.01 | 0.0   | 0.0         | 0.0  | 17.01       |
      | 2  | 29   | 01 March 2024    |           | 66.92           | 16.61         | 0.4      | 0.0  | 0.0       | 17.01 | 0.0   | 0.0         | 0.0  | 17.01       |
      | 3  | 31   | 01 April 2024    |           | 50.21           | 16.71         | 0.3      | 0.0  | 0.0       | 17.01 | 0.0   | 0.0         | 0.0  | 17.01       |
      | 4  | 30   | 01 May 2024      |           | 33.41           | 16.8          | 0.21     | 0.0  | 0.0       | 17.01 | 0.0   | 0.0         | 0.0  | 17.01       |
      | 5  | 31   | 01 June 2024     |           | 16.51           | 16.9          | 0.11     | 0.0  | 0.0       | 17.01 | 0.0   | 0.0         | 0.0  | 17.01       |
      | 6  | 30   | 01 July 2024     |           | 0.0             | 16.51         | 0.01     | 0.0  | 0.0       | 16.52 | 15.0  | 15.0        | 0.0  | 1.52        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 100.0         | 1.57     | 0.0  | 0.0       | 101.57 | 15.0 | 15.0       | 0.0  | 86.57       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2024  | Disbursement     | 100.0  | 0.0       | 0.0      | 0.0  | 0.0       | 100.0        |
      | 15 January 2024  | Repayment        | 15.0   | 15.0      | 0.0      | 0.0  | 0.0       | 85.0         |
    #    --- Early repayment on 15 January 2024 ---
    And Customer makes "AUTOPAY" repayment on "15 January 2024" with 1.50 EUR transaction amount
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid   | In advance  | Late | Outstanding |
      |    |      | 01 January 2024  |                 | 100.0           |               |          | 0.0  |           | 0.0   | 0.0    |             |      |             |
      | 1  | 31   | 01 February 2024 |                 | 83.52           | 16.48         | 0.53     | 0.0  | 0.0       | 17.01 | 0.0    | 0.0         | 0.0  | 17.01       |
      | 2  | 29   | 01 March 2024    |                 | 66.9            | 16.62         | 0.39     | 0.0  | 0.0       | 17.01 | 0.0    | 0.0         | 0.0  | 17.01       |
      | 3  | 31   | 01 April 2024    |                 | 50.18           | 16.72         | 0.29     | 0.0  | 0.0       | 17.01 | 0.0    | 0.0         | 0.0  | 17.01       |
      | 4  | 30   | 01 May 2024      |                 | 33.37           | 16.81         | 0.2      | 0.0  | 0.0       | 17.01 | 0.0    | 0.0         | 0.0  | 17.01       |
      | 5  | 31   | 01 June 2024     |                 | 16.5            | 16.87         | 0.1      | 0.0  | 0.0       | 16.97 | 0.0    | 0.0         | 0.0  | 16.97       |
      | 6  | 30   | 01 July 2024     | 15 January 2024 | 0.0             | 16.5          | 0.0      | 0.0  | 0.0       | 16.5  | 16.5   | 16.5        | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 100.0         | 1.51     | 0.0  | 0.0       | 101.51 | 16.5 | 16.5       | 0.0  | 85.01       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2024  | Disbursement     | 100.0  | 0.0       | 0.0      | 0.0  | 0.0       | 100.0        |
      | 15 January 2024  | Repayment        | 15.0   | 15.0      | 0.0      | 0.0  | 0.0       | 85.0         |
      | 15 January 2024  | Repayment        | 1.5    | 1.5       | 0.0      | 0.0  | 0.0       | 83.5         |
    #    --- Pay-off on 15 January 2024 ---
    And Customer makes "AUTOPAY" repayment on "15 January 2024" with 83.76 EUR transaction amount
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date       | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid   | In advance  | Late | Outstanding |
      |    |      | 01 January 2024  |                 | 100.0           |               |          | 0.0  |           | 0.0   | 0.0    |             |      |             |
      | 1  | 31   | 01 February 2024 | 15 January 2024 | 84.54           | 15.46         | 0.26     | 0.0  | 0.0       | 15.72 | 15.72  | 15.72       | 0.0  | 0.0         |
      | 2  | 29   | 01 March 2024    | 15 January 2024 | 67.53           | 17.01         | 0.0      | 0.0  | 0.0       | 17.01 | 17.01  | 17.01       | 0.0  | 0.0         |
      | 3  | 31   | 01 April 2024    | 15 January 2024 | 50.52           | 17.01         | 0.0      | 0.0  | 0.0       | 17.01 | 17.01  | 17.01       | 0.0  | 0.0         |
      | 4  | 30   | 01 May 2024      | 15 January 2024 | 33.51           | 17.01         | 0.0      | 0.0  | 0.0       | 17.01 | 17.01  | 17.01       | 0.0  | 0.0         |
      | 5  | 31   | 01 June 2024     | 15 January 2024 | 16.5            | 17.01         | 0.0      | 0.0  | 0.0       | 17.01 | 17.01  | 17.01       | 0.0  | 0.0         |
      | 6  | 30   | 01 July 2024     | 15 January 2024 | 0.0             | 16.5          | 0.0      | 0.0  | 0.0       | 16.5  | 16.5   | 16.5        | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      | 100.0         | 0.26     | 0.0  | 0.0       | 100.26 | 100.26 | 100.26     | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 January 2024  | Disbursement     | 100.0  | 0.0       | 0.0      | 0.0  | 0.0       | 100.0        |
      | 15 January 2024  | Repayment        | 15.0   | 15.0      | 0.0      | 0.0  | 0.0       | 85.0         |
      | 15 January 2024  | Repayment        | 1.5    | 1.5       | 0.0      | 0.0  | 0.0       | 83.5         |
      | 15 January 2024  | Repayment        | 83.76  | 83.5      | 0.26     | 0.0  | 0.0       | 0.0          |
      | 15 January 2024  | Accrual          | 0.26   | 0.0       | 0.26     | 0.0  | 0.0       | 0.0          |
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"

  @TestRailId:C3424
  Scenario: Verify that after maturity date with inline COB the outstanding interest is recognized on repayment schedule
    When Admin sets the business date to "23 December 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                 | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      |LP2_ADV_PYMNT_INTEREST_DAILY_EMI_360_ACTUAL  | 23 December 2024  | 100            | 4                      | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "23 December 2024" with "100" amount and expected disbursement date on "23 December 2024"
    When Admin successfully disburse the loan on "23 December 2024" with "100" EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 23 December 2024 |           | 100.0           |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 31   | 23 January 2025  |           | 66.78           | 33.22         | 0.34     | 0.0  | 0.0       | 33.56 | 0.0  | 0.0        | 0.0  | 33.56       |
      | 2  | 31   | 23 February 2025 |           | 33.45           | 33.33         | 0.23     | 0.0  | 0.0       | 33.56 | 0.0  | 0.0        | 0.0  | 33.56       |
      | 3  | 28   | 23 March 2025    |           | 0.0             | 33.45         | 0.1      | 0.0  | 0.0       | 33.55 | 0.0  | 0.0        | 0.0  | 33.55       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 100.0         | 0.67     | 0.0  | 0.0       | 100.67 | 0.0  | 0.0        | 0.0  | 100.67      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 23 December 2024 | Disbursement     | 100.0  | 0.0       | 0.0      | 0.0  | 0.0       | 100.0        |
    And Admin sets the business date to "23 March 2025"
    When Admin runs inline COB job for Loan
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 23 December 2024 |           | 100.0           |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 31   | 23 January 2025  |           | 66.78           | 33.22         | 0.34     | 0.0  | 0.0       | 33.56 | 0.0  | 0.0        | 0.0  | 33.56       |
      | 2  | 31   | 23 February 2025 |           | 33.45           | 33.33         | 0.23     | 0.0  | 0.0       | 33.56 | 0.0  | 0.0        | 0.0  | 33.56       |
      | 3  | 28   | 23 March 2025    |           | 0.0             | 33.45         | 0.1      | 0.0  | 0.0       | 33.55 | 0.0  | 0.0        | 0.0  | 33.55       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 100.0         | 0.67     | 0.0  | 0.0       | 100.67 | 0.0  | 0.0        | 0.0  | 100.67      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 23 December 2024 | Disbursement     | 100.0  | 0.0       | 0.0      | 0.0  | 0.0       | 100.0        |
      | 22 March 2025    | Accrual          | 0.67   | 0.0       | 0.67     | 0.0  | 0.0       |  0.0         |

  @TestRailId:C3534 @AdvancedPaymentAllocation
  Scenario: Verify advanced payment allocation - future installments: LAST_INSTALLMENT, full Merchant issued Refund on the disbursement date, 2nd disbursement on the same date
    When Admin sets the business date to "11 March 2025"
    And Admin creates a client with random data
    When Admin set "LP2_ADV_PYMNT_INTEREST_DAILY_EMI_ACTUAL_ACTUAL_INTEREST_REFUND_FULL" loan product "MERCHANT_ISSUED_REFUND" transaction type to "LAST_INSTALLMENT" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                         | submitted on date | with Principal | ANNUAL interest rate % | interest type              | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_ACTUAL_ACTUAL_INTEREST_REFUND_FULL | 11 March 2025     | 1000           | 26                     | DECLINING_BALANCE          | DAILY                       | EQUAL_INSTALLMENTS | 12                | MONTHS                | 1              | MONTHS                 | 12                 | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "11 March 2025" with "1000" amount and expected disbursement date on "11 March 2025"
    And Admin successfully disburse the loan on "11 March 2025" with "200" EUR transaction amount
    Then Loan Repayment schedule has 12 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 11 March 2025     |                   | 200.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 31   | 11 April 2025     |                   | 185.3           | 14.7          | 4.42     | 0.0  | 0.0       | 19.12 | 0.0   | 0.0        | 0.0  | 19.12       |
      | 2  | 30   | 11 May 2025       |                   | 170.14          | 15.16         | 3.96     | 0.0  | 0.0       | 19.12 | 0.0   | 0.0        | 0.0  | 19.12       |
      | 3  | 31   | 11 June 2025      |                   | 154.78          | 15.36         | 3.76     | 0.0  | 0.0       | 19.12 | 0.0   | 0.0        | 0.0  | 19.12       |
      | 4  | 30   | 11 July 2025      |                   | 138.97          | 15.81         | 3.31     | 0.0  | 0.0       | 19.12 | 0.0   | 0.0        | 0.0  | 19.12       |
      | 5  | 31   | 11 August 2025    |                   | 122.92          | 16.05         | 3.07     | 0.0  | 0.0       | 19.12 | 0.0   | 0.0        | 0.0  | 19.12       |
      | 6  | 31   | 11 September 2025 |                   | 106.51          | 16.41         | 2.71     | 0.0  | 0.0       | 19.12 | 0.0   | 0.0        | 0.0  | 19.12       |
      | 7  | 30   | 11 October 2025   |                   |  89.67          | 16.84         | 2.28     | 0.0  | 0.0       | 19.12 | 0.0   | 0.0        | 0.0  | 19.12       |
      | 8  | 31   | 11 November 2025  |                   |  72.53          | 17.14         | 1.98     | 0.0  | 0.0       | 19.12 | 0.0   | 0.0        | 0.0  | 19.12       |
      | 9  | 30   | 11 December 2025  |                   |  54.96          | 17.57         | 1.55     | 0.0  | 0.0       | 19.12 | 0.0   | 0.0        | 0.0  | 19.12       |
      | 10 | 31   | 11 January 2026   |                   |  37.05          | 17.91         | 1.21     | 0.0  | 0.0       | 19.12 | 0.0   | 0.0        | 0.0  | 19.12       |
      | 11 | 31   | 11 February 2026  |                   |  18.75          | 18.3          | 0.82     | 0.0  | 0.0       | 19.12 | 0.0   | 0.0        | 0.0  | 19.12       |
      | 12 | 28   | 11 March 2026     |                   |    0.0          | 18.75         | 0.37     | 0.0  | 0.0       | 19.12 | 0.0   | 0.0        | 0.0  | 19.12       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 200.0         | 29.44    | 0.0  | 0         | 229.44 | 0.0   | 0.0        | 0.0  | 229.44      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 11 March 2025     | Disbursement     | 200.0  | 0.0       | 0.0      | 0.0  | 0.0       | 200.0        |
    When Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "11 March 2025" with 200 EUR transaction amount and system-generated Idempotency key
    Then Loan Repayment schedule has 12 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 11 March 2025     |                   | 200.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 31   | 11 April 2025     | 11 March 2025     | 200.0           |   0.0         | 0.0      | 0.0  | 0.0       | 0.0   | 0.0   | 0.0        | 0.0  | 0.0         |
      | 2  | 30   | 11 May 2025       | 11 March 2025     | 191.2           |   8.8         | 0.0      | 0.0  | 0.0       | 8.8   | 8.8   | 8.8        | 0.0  | 0.0         |
      | 3  | 31   | 11 June 2025      | 11 March 2025     | 172.08          | 19.12         | 0.0      | 0.0  | 0.0       | 19.12 | 19.12 | 19.12      | 0.0  | 0.0         |
      | 4  | 30   | 11 July 2025      | 11 March 2025     | 152.96          | 19.12         | 0.0      | 0.0  | 0.0       | 19.12 | 19.12 | 19.12      | 0.0  | 0.0         |
      | 5  | 31   | 11 August 2025    | 11 March 2025     | 133.84          | 19.12         | 0.0      | 0.0  | 0.0       | 19.12 | 19.12 | 19.12      | 0.0  | 0.0         |
      | 6  | 31   | 11 September 2025 | 11 March 2025     | 114.72          | 19.12         | 0.0      | 0.0  | 0.0       | 19.12 | 19.12 | 19.12      | 0.0  | 0.0         |
      | 7  | 30   | 11 October 2025   | 11 March 2025     |  95.6           | 19.12         | 0.0      | 0.0  | 0.0       | 19.12 | 19.12 | 19.12      | 0.0  | 0.0         |
      | 8  | 31   | 11 November 2025  | 11 March 2025     |  76.48          | 19.12         | 0.0      | 0.0  | 0.0       | 19.12 | 19.12 | 19.12      | 0.0  | 0.0         |
      | 9  | 30   | 11 December 2025  | 11 March 2025     |  57.36          | 19.12         | 0.0      | 0.0  | 0.0       | 19.12 | 19.12 | 19.12      | 0.0  | 0.0         |
      | 10 | 31   | 11 January 2026   | 11 March 2025     |  38.24          | 19.12         | 0.0      | 0.0  | 0.0       | 19.12 | 19.12 | 19.12      | 0.0  | 0.0         |
      | 11 | 31   | 11 February 2026  | 11 March 2025     |  19.12          | 19.12         | 0.0      | 0.0  | 0.0       | 19.12 | 19.12 | 19.12      | 0.0  | 0.0         |
      | 12 | 28   | 11 March 2026     | 11 March 2025     |    0.0          | 19.12         | 0.0      | 0.0  | 0.0       | 19.12 | 19.12 | 19.12      | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      | 200.0         | 0        | 0.0  | 0         | 200.0 | 200.0 | 200.0      | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type          | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 11 March 2025     | Disbursement              | 200.0  | 0.0       | 0.0      | 0.0  | 0.0       | 200.0        |
      | 11 March 2025     | Merchant Issued Refund    | 200.0  | 200.0     | 0.0      | 0.0  | 0.0       | 0.0          |
    And Admin successfully disburse the loan on "11 March 2025" with "200" EUR transaction amount
    Then Loan Repayment schedule has 12 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date         | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 11 March 2025     |                   | 200.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      |    |      | 11 March 2025     |                   | 200.0           |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 31   | 11 April 2025     |                   | 366.18          | 33.82         | 4.42     | 0.0  | 0.0       | 38.24 | 0.0   | 0.0        | 0.0  | 38.24       |
      | 2  | 30   | 11 May 2025       |                   | 331.49          | 34.69         | 3.55     | 0.0  | 0.0       | 38.24 | 8.8   | 8.8        | 0.0  | 29.44       |
      | 3  | 31   | 11 June 2025      |                   | 296.35          | 35.14         | 3.1      | 0.0  | 0.0       | 38.24 | 19.12 | 19.12      | 0.0  | 19.12       |
      | 4  | 30   | 11 July 2025      |                   | 260.77          | 35.58         | 2.66     | 0.0  | 0.0       | 38.24 | 19.12 | 19.12      | 0.0  | 19.12       |
      | 5  | 31   | 11 August 2025    |                   | 224.91          | 35.86         | 2.38     | 0.0  | 0.0       | 38.24 | 19.12 | 19.12      | 0.0  | 19.12       |
      | 6  | 31   | 11 September 2025 |                   | 188.68          | 36.23         | 2.01     | 0.0  | 0.0       | 38.24 | 19.12 | 19.12      | 0.0  | 19.12       |
      | 7  | 30   | 11 October 2025   |                   | 152.02          | 36.66         | 1.58     | 0.0  | 0.0       | 38.24 | 19.12 | 19.12      | 0.0  | 19.12       |
      | 8  | 31   | 11 November 2025  |                   | 115.03          | 36.99         | 1.25     | 0.0  | 0.0       | 38.24 | 19.12 | 19.12      | 0.0  | 19.12       |
      | 9  | 30   | 11 December 2025  |                   |  77.61          | 37.42         | 0.82     | 0.0  | 0.0       | 38.24 | 19.12 | 19.12      | 0.0  | 19.12       |
      | 10 | 31   | 11 January 2026   |                   |  39.82          | 37.79         | 0.45     | 0.0  | 0.0       | 38.24 | 19.12 | 19.12      | 0.0  | 19.12       |
      | 11 | 31   | 11 February 2026  |                   |  19.12          | 20.7          | 0.03     | 0.0  | 0.0       | 20.73 | 19.12 | 19.12      | 0.0  | 1.61        |
      | 12 | 28   | 11 March 2026     | 11 March 2025     |    0.0          | 19.12         | 0.0      | 0.0  | 0.0       | 19.12 | 19.12 | 19.12      | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 400.0         | 22.25    | 0.0  | 0         | 422.25 | 200.0 | 200.0      | 0.0  | 222.25      |
    Then Loan Transactions tab has the following data:
      | Transaction date  | Transaction Type          | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 11 March 2025     | Disbursement              | 200.0  | 0.0       | 0.0      | 0.0  | 0.0       | 200.0        |
      | 11 March 2025     | Merchant Issued Refund    | 200.0  | 200.0     | 0.0      | 0.0  | 0.0       | 0.0          |
      | 11 March 2025     | Disbursement              | 200.0  | 0.0       | 0.0      | 0.0  | 0.0       | 200.0        |
    When Admin set "LP2_ADV_PYMNT_INTEREST_DAILY_EMI_ACTUAL_ACTUAL_INTEREST_REFUND_FULL" loan product "MERCHANT_ISSUED_REFUND" transaction type to "NEXT_INSTALLMENT" future installment allocation rule

  @TestRailId:C3570
  Scenario: Verify Loan is fully paid and closed after full Merchant issued refund 1 day after disbursement
    When Admin sets the business date to "29 March 2025"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                   | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_CUSTOM_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 28 March 2025     | 1383           | 12.23                  | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 24                | MONTHS                | 1              | MONTHS                 | 24                 | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "28 March 2025" with "1383" amount and expected disbursement date on "28 March 2025"
    When Admin successfully disburse the loan on "28 March 2025" with "1383" EUR transaction amount
    Then Loan Repayment schedule has 24 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 28 March 2025       |                  | 1383.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 31   | 28 April 2025       |                  | 1331.85         | 51.15         | 14.1     | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 2  | 30   | 28 May 2025         |                  | 1280.17         | 51.68         | 13.57    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 3  | 31   | 28 June 2025        |                  | 1227.97         | 52.2          | 13.05    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 4  | 30   | 28 July 2025        |                  | 1175.24         | 52.73         | 12.52    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 5  | 31   | 28 August 2025      |                  | 1121.97         | 53.27         | 11.98    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 6  | 31   | 28 September 2025   |                  | 1068.15         | 53.82         | 11.43    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 7  | 30   | 28 October 2025     |                  | 1013.79         | 54.36         | 10.89    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 8  | 31   | 28 November 2025    |                  |  958.87         | 54.92         | 10.33    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 9  | 30   | 28 December 2025    |                  |  903.39         | 55.48         |  9.77    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 10 | 31   | 28 January 2026     |                  |  847.35         | 56.04         |  9.21    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 11 | 31   | 28 February 2026    |                  |  790.74         | 56.61         |  8.64    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 12 | 28   | 28 March 2026       |                  |  733.55         | 57.19         |  8.06    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 13 | 31   | 28 April 2026       |                  |  675.78         | 57.77         |  7.48    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 14 | 30   | 28 May 2026         |                  |  617.42         | 58.36         |  6.89    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 15 | 31   | 28 June 2026        |                  |  558.46         | 58.96         |  6.29    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 16 | 30   | 28 July 2026        |                  |  498.9          | 59.56         |  5.69    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 17 | 31   | 28 August 2026      |                  |  438.73         | 60.17         |  5.08    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 18 | 31   | 28 September 2026   |                  |  377.95         | 60.78         |  4.47    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 19 | 30   | 28 October 2026     |                  |  316.55         | 61.4          |  3.85    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 20 | 31   | 28 November 2026    |                  |  254.53         | 62.02         |  3.23    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 21 | 30   | 28 December 2026    |                  |  191.87         | 62.66         |  2.59    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 22 | 31   | 28 January 2027     |                  |  128.58         | 63.29         |  1.96    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 23 | 31   | 28 February 2027    |                  |   64.64         | 63.94         |  1.31    | 0.0  | 0.0       | 65.25 | 0.0   | 0.0        | 0.0  | 65.25       |
      | 24 | 28   | 28 March 2027       |                  |    0.0          | 64.64         |  0.66    | 0.0  | 0.0       | 65.3  | 0.0   | 0.0        | 0.0  | 65.3        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid | In advance | Late | Outstanding |
      | 1383.0        | 183.05   | 0.0  | 0.0       | 1566.05 |  0.0 | 0.0        | 0.0  | 1566.05     |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 28 March 2025    | Disbursement     | 1383.0  | 0.0       | 0.0      | 0.0  | 0.0       | 1383.0       | false    | false    |
    And Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "29 March 2025" with 1383 EUR transaction amount and self-generated Idempotency key
    Then Loan Repayment schedule has 24 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 28 March 2025       |                  | 1383.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 31   | 28 April 2025       |  29 March 2025   | 1383.0          | 0.0           |  0.45    | 0.0  | 0.0       | 0.45  | 0.45  | 0.45       | 0.0  | 0.0         |
      | 2  | 30   | 28 May 2025         |  29 March 2025   | 1383.0          | 0.0           |  0.0     | 0.0  | 0.0       | 0.0   | 0.0   | 0.0        | 0.0  | 0.0         |
      | 3  | 31   | 28 June 2025        |  29 March 2025   | 1370.25         | 12.75         |  0.0     | 0.0  | 0.0       | 12.75 | 12.75 | 12.75      | 0.0  | 0.0         |
      | 4  | 30   | 28 July 2025        |  29 March 2025   | 1305.0          | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 5  | 31   | 28 August 2025      |  29 March 2025   | 1239.75         | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 6  | 31   | 28 September 2025   |  29 March 2025   | 1174.5          | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 7  | 30   | 28 October 2025     |  29 March 2025   | 1109.25         | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 8  | 31   | 28 November 2025    |  29 March 2025   | 1044.0          | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 9  | 30   | 28 December 2025    |  29 March 2025   |  978.75         | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 10 | 31   | 28 January 2026     |  29 March 2025   |  913.5          | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 11 | 31   | 28 February 2026    |  29 March 2025   |  848.25         | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 12 | 28   | 28 March 2026       |  29 March 2025   |  783.0          | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 13 | 31   | 28 April 2026       |  29 March 2025   |  717.75          | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 14 | 30   | 28 May 2026         |  29 March 2025   |  652.5          | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 15 | 31   | 28 June 2026        |  29 March 2025   |  587.25         | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 16 | 30   | 28 July 2026        |  29 March 2025   |  522.0          | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 17 | 31   | 28 August 2026      |  29 March 2025   |  456.75         | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 18 | 31   | 28 September 2026   |  29 March 2025   |  391.5          | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 19 | 30   | 28 October 2026     |  29 March 2025   |  326.25         | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 20 | 31   | 28 November 2026    |  29 March 2025   |  261.0          | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 21 | 30   | 28 December 2026    |  29 March 2025   |  195.75         | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 21 | 30   | 28 December 2026    |  29 March 2025   |  195.75         | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 22 | 31   | 28 January 2027     |  29 March 2025   |  130.5          | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 23 | 31   | 28 February 2027    |  29 March 2025   |   65.25         | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
      | 24 | 28   | 28 March 2027       |  29 March 2025   |    0.0          | 65.25         |  0.0     | 0.0  | 0.0       | 65.25 | 65.25 | 65.25      | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid     | In advance | Late | Outstanding |
      | 1383.0        | 0.45     | 0.0  | 0.0       | 1383.45 |  1383.45 | 1383.45    | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type           | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 28 March 2025    | Disbursement               | 1383.0  | 0.0       | 0.0      | 0.0  | 0.0       | 1383.0       | false    | false    |
      | 29 March 2025    | Merchant Issued Refund     | 1383.0  | 1383.0    | 0.0      | 0.0  | 0.0       | 0.0          | false    | false    |
      | 29 March 2025    | Interest Refund            | 0.45    | 0.0       | 0.45     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 29 March 2025    | Accrual Activity           | 0.45    | 0.0       | 0.45     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 29 March 2025    | Accrual                    | 0.45    | 0.0       | 0.45     | 0.0  | 0.0       | 0.0          | false    | false    |
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan has 0 outstanding amount

  @TestRailId:C3584
  Scenario: Verify 2nd disbursement after loan was fully paid and closed (2 MIR, 1 CBR) - No interest, No interest recalculation
    When Admin sets the business date to "14 March 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_DP_CUSTOM_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 14 March 2024     | 1000.0         | 0.0                    | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "14 March 2024" with "1000.0" amount and expected disbursement date on "14 March 2024"
    When Admin successfully disburse the loan on "14 March 2024" with "487.58" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      |    |      | 14 March 2024       |                  | 487.58          |               |          | 0.0  |           | 0.0    | 0.0   |            |      |             |
      | 1  | 0    | 14 March 2024       | 14 March 2024    | 365.68          | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 29 March 2024       |                  | 243.79          | 121.89        | 0.0      | 0.0  | 0.0       | 121.89 | 0.0   | 0.0        | 0.0  | 121.89      |
      | 3  | 15   | 13 April 2024       |                  | 121.9           | 121.89        | 0.0      | 0.0  | 0.0       | 121.89 | 0.0   | 0.0        | 0.0  | 121.89      |
      | 4  | 15   | 28 April 2024       |                  | 0.0             | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 0.0   | 0.0        | 0.0  | 121.9       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid  | In advance | Late | Outstanding |
      | 487.58        | 0.0      | 0.0  | 0.0       | 487.58  | 121.9 | 0.0        | 0.0  | 365.68      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 14 March 2024    | Disbursement     | 487.58  | 0.0       | 0.0      | 0.0  | 0.0       | 487.58       | false    | false    |
      | 14 March 2024    | Down Payment     | 121.9   | 121.9     | 0.0      | 0.0  | 0.0       | 365.68       | false    | false    |
    When Admin sets the business date to "24 March 2024"
    And Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "24 March 2024" with 201.39 EUR transaction amount and self-generated Idempotency key
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      |    |      | 14 March 2024       |                  | 487.58          |               |          | 0.0  |           | 0.0    | 0.0   |            |      |             |
      | 1  | 0    | 14 March 2024       | 14 March 2024    | 365.68          | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 29 March 2024       |                  | 243.79          | 121.89        | 0.0      | 0.0  | 0.0       | 121.89 | 0.0   | 0.0        | 0.0  | 121.89      |
      | 3  | 15   | 13 April 2024       |                  | 121.9           | 121.89        | 0.0      | 0.0  | 0.0       | 121.89 | 79.49 | 79.49      | 0.0  | 42.4        |
      | 4  | 15   | 28 April 2024       | 24 March 2024    | 0.0             | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9 | 121.9      | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid     | In advance | Late | Outstanding |
      | 487.58        | 0.0      | 0.0  | 0.0       | 487.58  | 323.29   | 201.39     | 0.0  | 164.29      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 14 March 2024    | Disbursement           | 487.58  | 0.0       | 0.0      | 0.0  | 0.0       | 487.58       | false    | false    |
      | 14 March 2024    | Down Payment           | 121.9   | 121.9     | 0.0      | 0.0  | 0.0       | 365.68       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 201.39  | 201.39    | 0.0      | 0.0  | 0.0       | 164.29       | false    | false    |
    Then Loan status will be "ACTIVE"
    Then Loan has 164.29 outstanding amount
    And Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "24 March 2024" with 286.19 EUR transaction amount and self-generated Idempotency key
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid    | In advance | Late | Outstanding |
      |    |      | 14 March 2024       |                  | 487.58          |               |          | 0.0  |           | 0.0    | 0.0     |            |      |             |
      | 1  | 0    | 14 March 2024       | 14 March 2024    | 365.68          | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9   | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 29 March 2024       | 24 March 2024    | 243.79          | 121.89        | 0.0      | 0.0  | 0.0       | 121.89 | 121.89  | 121.89     | 0.0  | 0.0         |
      | 3  | 15   | 13 April 2024       | 24 March 2024    | 121.9           | 121.89        | 0.0      | 0.0  | 0.0       | 121.89 | 121.89  | 121.89     | 0.0  | 0.0         |
      | 4  | 15   | 28 April 2024       | 24 March 2024    | 0.0             | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9   | 121.9      | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid     | In advance | Late | Outstanding |
      | 487.58        | 0.0      | 0.0  | 0.0       | 487.58  | 487.58   | 365.68     | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 14 March 2024    | Disbursement           | 487.58  | 0.0       | 0.0      | 0.0  | 0.0       | 487.58       | false    | false    |
      | 14 March 2024    | Down Payment           | 121.9   | 121.9     | 0.0      | 0.0  | 0.0       | 365.68       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 201.39  | 201.39    | 0.0      | 0.0  | 0.0       | 164.29       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 286.19  | 164.29    | 0.0      | 0.0  | 0.0       | 0.0          | false    | false    |
    Then Loan status will be "OVERPAID"
    Then Loan has 121.9 overpaid amount
    When Admin sets the business date to "25 March 2024"
    When Admin makes Credit Balance Refund transaction on "25 March 2024" with 121.9 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid    | In advance | Late | Outstanding |
      |    |      | 14 March 2024       |                  | 487.58          |               |          | 0.0  |           | 0.0    | 0.0     |            |      |             |
      | 1  | 0    | 14 March 2024       | 14 March 2024    | 365.68          | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9   | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 29 March 2024       | 24 March 2024    | 243.79          | 121.89        | 0.0      | 0.0  | 0.0       | 121.89 | 121.89  | 121.89     | 0.0  | 0.0         |
      | 3  | 15   | 13 April 2024       | 24 March 2024    | 121.9           | 121.89        | 0.0      | 0.0  | 0.0       | 121.89 | 121.89  | 121.89     | 0.0  | 0.0         |
      | 4  | 15   | 28 April 2024       | 24 March 2024    | 0.0             | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9   | 121.9      | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid     | In advance | Late | Outstanding |
      | 487.58        | 0.0      | 0.0  | 0.0       | 487.58  | 487.58   | 365.68     | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 14 March 2024    | Disbursement           | 487.58  | 0.0       | 0.0      | 0.0  | 0.0       | 487.58       | false    | false    |
      | 14 March 2024    | Down Payment           | 121.9   | 121.9     | 0.0      | 0.0  | 0.0       | 365.68       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 201.39  | 201.39    | 0.0      | 0.0  | 0.0       | 164.29       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 286.19  | 164.29    | 0.0      | 0.0  | 0.0       | 0.0          | false    | false    |
      | 25 March 2024    | Credit Balance Refund  | 121.9   | 0.0       | 0.0      | 0.0  | 0.0       | 0.0          | false    | false    |
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan has 0 outstanding amount
    When Admin sets the business date to "01 April 2024"
    When Admin successfully disburse the loan on "01 April 2024" with "312.69" EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid    | In advance | Late | Outstanding |
      |    |      | 14 March 2024       |                  | 487.58          |               |          | 0.0  |           | 0.0    | 0.0     |            |      |             |
      | 1  | 0    | 14 March 2024       | 14 March 2024    | 365.68          | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9   | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 29 March 2024       | 24 March 2024    | 243.79          | 121.89        | 0.0      | 0.0  | 0.0       | 121.89 | 121.89  | 121.89     | 0.0  | 0.0         |
      |    |      | 01 April 2024       |                  | 312.69          |               |          | 0.0  |           | 0.0    | 0.0     |            |      |             |
      | 3  | 0    | 01 April 2024       | 01 April 2024    | 478.31          | 78.17         | 0.0      | 0.0  | 0.0       | 78.17  | 78.17   | 0.0        | 0.0  | 0.0         |
      | 4  | 15   | 13 April 2024       |                  | 239.15          | 239.16        | 0.0      | 0.0  | 0.0       | 239.16 | 121.89  | 121.89     | 0.0  | 117.27      |
      | 5  | 15   | 28 April 2024       |                  | 0.0             | 239.15        | 0.0      | 0.0  | 0.0       | 239.15 | 121.9   | 121.9      | 0.0  | 117.25      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid     | In advance | Late | Outstanding |
      | 800.27        | 0.0      | 0.0  | 0.0       | 800.27  | 565.75   | 365.68     | 0.0  | 234.52      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 14 March 2024    | Disbursement           | 487.58  | 0.0       | 0.0      | 0.0  | 0.0       | 487.58       | false    | false    |
      | 14 March 2024    | Down Payment           | 121.9   | 121.9     | 0.0      | 0.0  | 0.0       | 365.68       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 201.39  | 201.39    | 0.0      | 0.0  | 0.0       | 164.29       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 286.19  | 164.29    | 0.0      | 0.0  | 0.0       | 0.0          | false    | false    |
      | 25 March 2024    | Credit Balance Refund  | 121.9   | 0.0       | 0.0      | 0.0  | 0.0       | 0.0          | false    | false    |
      | 01 April 2024    | Disbursement           | 312.69  | 0.0       | 0.0      | 0.0  | 0.0       | 312.69       | false    | false    |
      | 01 April 2024    | Down Payment           | 78.17   | 78.17     | 0.0      | 0.0  | 0.0       | 234.52       | false    | false    |
    Then Loan status will be "ACTIVE"
    Then Loan has 234.52 outstanding amount
    When Admin sets the business date to "10 April 2024"
    When Loan Pay-off is made on "10 April 2024"
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid    | In advance | Late | Outstanding |
      |    |      | 14 March 2024       |                  | 487.58          |               |          | 0.0  |           | 0.0    | 0.0     |            |      |             |
      | 1  | 0    | 14 March 2024       | 14 March 2024    | 365.68          | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9   | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 29 March 2024       | 24 March 2024    | 243.79          | 121.89        | 0.0      | 0.0  | 0.0       | 121.89 | 121.89  | 121.89     | 0.0  | 0.0         |
      |    |      | 01 April 2024       |                  | 312.69          |               |          | 0.0  |           | 0.0    | 0.0     |            |      |             |
      | 3  | 0    | 01 April 2024       | 01 April 2024    | 478.31          | 78.17         | 0.0      | 0.0  | 0.0       | 78.17  | 78.17   | 0.0        | 0.0  | 0.0         |
      | 4  | 15   | 13 April 2024       | 10 April 2024    | 239.15          | 239.16        | 0.0      | 0.0  | 0.0       | 239.16 | 239.16  | 239.16     | 0.0  | 0.0         |
      | 5  | 15   | 28 April 2024       | 10 April 2024    | 0.0             | 239.15        | 0.0      | 0.0  | 0.0       | 239.15 | 239.15  | 239.15     | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid     | In advance | Late | Outstanding |
      | 800.27        | 0.0      | 0.0  | 0.0       | 800.27  | 800.27   | 600.2      | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 14 March 2024    | Disbursement           | 487.58  | 0.0       | 0.0      | 0.0  | 0.0       | 487.58       | false    | false    |
      | 14 March 2024    | Down Payment           | 121.9   | 121.9     | 0.0      | 0.0  | 0.0       | 365.68       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 201.39  | 201.39    | 0.0      | 0.0  | 0.0       | 164.29       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 286.19  | 164.29    | 0.0      | 0.0  | 0.0       | 0.0          | false    | false    |
      | 25 March 2024    | Credit Balance Refund  | 121.9   | 0.0       | 0.0      | 0.0  | 0.0       | 0.0          | false    | false    |
      | 01 April 2024    | Disbursement           | 312.69  | 0.0       | 0.0      | 0.0  | 0.0       | 312.69       | false    | false    |
      | 01 April 2024    | Down Payment           | 78.17   | 78.17     | 0.0      | 0.0  | 0.0       | 234.52       | false    | false    |
      | 10 April 2024    | Repayment              | 234.52  | 234.52    | 0.0      | 0.0  | 0.0       | 0.0          | false    | false    |
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan has 0 outstanding amount

  @TestRailId:C3585
  Scenario: Verify 2nd disbursement after loan was fully paid and closed (2 MIR, 1 CBR) - 10% interest, No interest recalculation
    When Admin sets the business date to "14 March 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_DP_CUSTOM_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 14 March 2024     | 1000.0         | 10.0                   | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "14 March 2024" with "1000.0" amount and expected disbursement date on "14 March 2024"
    When Admin successfully disburse the loan on "14 March 2024" with "487.58" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      |    |      | 14 March 2024       |                  | 487.58          |               |          | 0.0  |           | 0.0    | 0.0   |            |      |             |
      | 1  | 0    | 14 March 2024       | 14 March 2024    | 365.68          | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 29 March 2024       |                  | 244.29          | 121.39        | 1.5      | 0.0  | 0.0       | 122.89 | 0.0   | 0.0        | 0.0  | 122.89      |
      | 3  | 15   | 13 April 2024       |                  | 122.4           | 121.89        | 1.0      | 0.0  | 0.0       | 122.89 | 0.0   | 0.0        | 0.0  | 122.89      |
      | 4  | 15   | 28 April 2024       |                  | 0.0             | 122.4         | 0.5      | 0.0  | 0.0       | 122.9  | 0.0   | 0.0        | 0.0  | 122.9       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid  | In advance | Late | Outstanding |
      | 487.58        | 3.0      | 0.0  | 0.0       | 490.58  | 121.9 | 0.0        | 0.0  | 368.68      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 14 March 2024    | Disbursement     | 487.58  | 0.0       | 0.0      | 0.0  | 0.0       | 487.58       | false    | false    |
      | 14 March 2024    | Down Payment     | 121.9   | 121.9     | 0.0      | 0.0  | 0.0       | 365.68       | false    | false    |
    When Admin sets the business date to "24 March 2024"
    And Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "24 March 2024" with 201.39 EUR transaction amount and self-generated Idempotency key
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      |    |      | 14 March 2024       |                  | 487.58          |               |          | 0.0  |           | 0.0    | 0.0   |            |      |             |
      | 1  | 0    | 14 March 2024       | 14 March 2024    | 365.68          | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 29 March 2024       |                  | 244.29          | 121.39        | 1.5      | 0.0  | 0.0       | 122.89 | 0.0   | 0.0        | 0.0  | 122.89      |
      | 3  | 15   | 13 April 2024       |                  | 122.4           | 121.89        | 1.0      | 0.0  | 0.0       | 122.89 | 78.49 | 78.49      | 0.0  | 44.4        |
      | 4  | 15   | 28 April 2024       | 24 March 2024    | 0.0             | 122.4         | 0.5      | 0.0  | 0.0       | 122.9  | 122.9 | 122.9      | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid     | In advance | Late | Outstanding |
      | 487.58        | 3.0      | 0.0  | 0.0       | 490.58  | 323.29   | 201.39     | 0.0  | 167.29      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 14 March 2024    | Disbursement           | 487.58  | 0.0       | 0.0      | 0.0  | 0.0       | 487.58       | false    | false    |
      | 14 March 2024    | Down Payment           | 121.9   | 121.9     | 0.0      | 0.0  | 0.0       | 365.68       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 201.39  | 199.89    | 1.5      | 0.0  | 0.0       | 165.79       | false    | false    |
    Then Loan status will be "ACTIVE"
    Then Loan has 167.29 outstanding amount
    And Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "24 March 2024" with 286.19 EUR transaction amount and self-generated Idempotency key
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      |    |      | 14 March 2024       |                  | 487.58          |               |          | 0.0  |           | 0.0    | 0.0    |            |      |             |
      | 1  | 0    | 14 March 2024       | 14 March 2024    | 365.68          | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9  | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 29 March 2024       | 24 March 2024    | 244.29          | 121.39        | 1.5      | 0.0  | 0.0       | 122.89 | 122.89 | 122.89     | 0.0  | 0.0         |
      | 3  | 15   | 13 April 2024       | 24 March 2024    | 122.4           | 121.89        | 1.0      | 0.0  | 0.0       | 122.89 | 122.89 | 122.89     | 0.0  | 0.0         |
      | 4  | 15   | 28 April 2024       | 24 March 2024    | 0.0             | 122.4         | 0.5      | 0.0  | 0.0       | 122.9  | 122.9  | 122.9      | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid     | In advance | Late | Outstanding |
      | 487.58        | 3.0      | 0.0  | 0.0       | 490.58  | 490.58   | 368.68     | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 14 March 2024    | Disbursement           | 487.58  | 0.0       | 0.0      | 0.0  | 0.0       | 487.58       | false    | false    |
      | 14 March 2024    | Down Payment           | 121.9   | 121.9     | 0.0      | 0.0  | 0.0       | 365.68       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 201.39  | 199.89    | 1.5      | 0.0  | 0.0       | 165.79       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 286.19  | 165.79    | 1.5      | 0.0  | 0.0       | 0.0          | false    | false    |
      | 24 March 2024    | Accrual                | 3.0     | 0.0       | 3.0      | 0.0  | 0.0       | 0.0          | false    | false    |
    Then Loan status will be "OVERPAID"
    Then Loan has 118.9 overpaid amount
    When Admin sets the business date to "25 March 2024"
    When Admin makes Credit Balance Refund transaction on "25 March 2024" with 118 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      |    |      | 14 March 2024       |                  | 487.58          |               |          | 0.0  |           | 0.0    | 0.0    |            |      |             |
      | 1  | 0    | 14 March 2024       | 14 March 2024    | 365.68          | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9  | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 29 March 2024       | 24 March 2024    | 244.29          | 121.39        | 1.5      | 0.0  | 0.0       | 122.89 | 122.89 | 122.89     | 0.0  | 0.0         |
      | 3  | 15   | 13 April 2024       | 24 March 2024    | 122.4           | 121.89        | 1.0      | 0.0  | 0.0       | 122.89 | 122.89 | 122.89     | 0.0  | 0.0         |
      | 4  | 15   | 28 April 2024       | 24 March 2024    | 0.0             | 122.4         | 0.5      | 0.0  | 0.0       | 122.9  | 122.9  | 122.9      | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid     | In advance | Late | Outstanding |
      | 487.58        | 3.0      | 0.0  | 0.0       | 490.58  | 490.58   | 368.68     | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 14 March 2024    | Disbursement           | 487.58  | 0.0       | 0.0      | 0.0  | 0.0       | 487.58       | false    | false    |
      | 14 March 2024    | Down Payment           | 121.9   | 121.9     | 0.0      | 0.0  | 0.0       | 365.68       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 201.39  | 199.89    | 1.5      | 0.0  | 0.0       | 165.79       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 286.19  | 165.79    | 1.5      | 0.0  | 0.0       | 0.0          | false    | false    |
      | 24 March 2024    | Accrual                | 3.0     | 0.0       | 3.0      | 0.0  | 0.0       | 0.0          | false    | false    |
      | 25 March 2024    | Credit Balance Refund  | 118.0   | 0.0       | 0.0      | 0.0  | 0.0       | 0.0          | false    | false    |
    Then Loan status will be "OVERPAID"
    Then Loan has 0.9 overpaid amount
    When Admin sets the business date to "01 April 2024"
    When Admin successfully disburse the loan on "01 April 2024" with "312.69" EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid    | In advance | Late | Outstanding |
      |    |      | 14 March 2024       |                  | 487.58          |               |          | 0.0  |           | 0.0    | 0.0    |            |      |             |
      | 1  | 0    | 14 March 2024       | 14 March 2024    | 365.68          | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9  | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 29 March 2024       | 24 March 2024    | 244.29          | 121.39        | 1.5      | 0.0  | 0.0       | 122.89 | 122.89 | 122.89     | 0.0  | 0.0         |
      |    |      | 01 April 2024       |                  | 312.69          |               |          | 0.0  |           | 0.0    | 0.0    |            |      |             |
      | 3  | 0    | 01 April 2024       | 01 April 2024    | 478.81          | 78.17         | 0.0      | 0.0  | 0.0       | 78.17  | 78.17  | 0.0        | 0.0  | 0.0         |
      | 4  | 15   | 13 April 2024       |                  | 239.8           | 239.01        | 1.77     | 0.0  | 0.0       | 240.78 | 122.89 | 122.89     | 0.0  | 117.89      |
      | 5  | 15   | 28 April 2024       |                  | 0.0             | 239.8         | 0.98     | 0.0  | 0.0       | 240.78 | 122.9  | 122.9      | 0.0  | 117.88      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid     | In advance | Late | Outstanding |
      | 800.27        | 4.25     | 0.0  | 0.0       | 804.52  | 568.75   | 368.68     | 0.0  | 235.77      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 14 March 2024    | Disbursement           | 487.58  | 0.0       | 0.0      | 0.0  | 0.0       | 487.58       | false    | false    |
      | 14 March 2024    | Down Payment           | 121.9   | 121.9     | 0.0      | 0.0  | 0.0       | 365.68       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 201.39  | 199.89    | 1.5      | 0.0  | 0.0       | 165.79       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 286.19  | 165.79    | 1.5      | 0.0  | 0.0       | 0.0          | false    | false    |
      | 24 March 2024    | Accrual                | 3.0     | 0.0       | 3.0      | 0.0  | 0.0       | 0.0          | false    | false    |
      | 25 March 2024    | Credit Balance Refund  | 118.0   | 0.0       | 0.0      | 0.0  | 0.0       | 0.0          | false    | false    |
      | 01 April 2024    | Disbursement           | 312.69  | 0.0       | 0.0      | 0.0  | 0.0       | 311.79       | false    | false    |
      | 01 April 2024    | Down Payment           | 77.27   | 77.27     | 0.0      | 0.0  | 0.0       | 234.52       | false    | false    |
    Then Loan status will be "ACTIVE"
    Then Loan has 235.77 outstanding amount
    When Admin sets the business date to "10 April 2024"
    When Loan Pay-off is made on "10 April 2024"
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid    | In advance | Late | Outstanding |
      |    |      | 14 March 2024       |                  | 487.58          |               |          | 0.0  |           | 0.0    | 0.0    |            |      |             |
      | 1  | 0    | 14 March 2024       | 14 March 2024    | 365.68          | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9  | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 29 March 2024       | 24 March 2024    | 244.29          | 121.39        | 1.5      | 0.0  | 0.0       | 122.89 | 122.89 | 122.89     | 0.0  | 0.0         |
      |    |      | 01 April 2024       |                  | 312.69          |               |          | 0.0  |           | 0.0    | 0.0    |            |      |             |
      | 3  | 0    | 01 April 2024       | 01 April 2024    | 478.81          | 78.17         | 0.0      | 0.0  | 0.0       | 78.17  | 78.17  | 0.0        | 0.0  | 0.0         |
      | 4  | 15   | 13 April 2024       | 10 April 2024    | 239.8           | 239.01        | 1.77     | 0.0  | 0.0       | 240.78 | 240.78 | 240.78     | 0.0  | 0.0         |
      | 5  | 15   | 28 April 2024       | 10 April 2024    | 0.0             | 239.8         | 0.98     | 0.0  | 0.0       | 240.78 | 240.78 | 240.78     | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid     | In advance | Late | Outstanding |
      | 800.27        | 4.25     | 0.0  | 0.0       | 804.52  | 804.52   | 604.45     | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 14 March 2024    | Disbursement           | 487.58  | 0.0       | 0.0      | 0.0  | 0.0       | 487.58       | false    | false    |
      | 14 March 2024    | Down Payment           | 121.9   | 121.9     | 0.0      | 0.0  | 0.0       | 365.68       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 201.39  | 199.89    | 1.5      | 0.0  | 0.0       | 165.79       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 286.19  | 165.79    | 1.5      | 0.0  | 0.0       | 0.0          | false    | false    |
      | 24 March 2024    | Accrual                | 3.0     | 0.0       | 3.0      | 0.0  | 0.0       | 0.0          | false    | false    |
      | 25 March 2024    | Credit Balance Refund  | 118.0   | 0.0       | 0.0      | 0.0  | 0.0       | 0.0          | false    | false    |
      | 01 April 2024    | Disbursement           | 312.69  | 0.0       | 0.0      | 0.0  | 0.0       | 311.79       | false    | false    |
      | 01 April 2024    | Down Payment           | 77.27   | 77.27     | 0.0      | 0.0  | 0.0       | 234.52       | false    | false    |
      | 10 April 2024    | Repayment              | 235.77  | 234.52    | 1.25     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 10 April 2024    | Accrual                | 1.25    | 0.0       | 1.25     | 0.0  | 0.0       | 0.0          | false    | false    |
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan has 0 outstanding amount

  @TestRailId:C3586
  Scenario: Verify 2nd disbursement after loan was fully paid and closed (2 MIR, 1 CBR) - 33.33% interest with interest recalculation
    When Admin sets the business date to "14 March 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                         | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_DP_IR_CUSTOM_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 14 March 2024     | 1000.0         | 33.33                  | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 45                | DAYS                  | 15             | DAYS                   | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "14 March 2024" with "1000.0" amount and expected disbursement date on "14 March 2024"
    When Admin successfully disburse the loan on "14 March 2024" with "487.58" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      |    |      | 14 March 2024       |                  | 487.58          |               |          | 0.0  |           | 0.0    | 0.0   |            |      |             |
      | 1  | 0    | 14 March 2024       | 14 March 2024    | 365.68          | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9 | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 29 March 2024       |                  | 245.46          | 120.22        | 5.08     | 0.0  | 0.0       | 125.3  | 0.0   | 0.0        | 0.0  | 125.3       |
      | 3  | 15   | 13 April 2024       |                  | 123.57          | 121.89        | 3.41     | 0.0  | 0.0       | 125.3  | 0.0   | 0.0        | 0.0  | 125.3       |
      | 4  | 15   | 28 April 2024       |                  | 0.0             | 123.57        | 1.72     | 0.0  | 0.0       | 125.29 | 0.0   | 0.0        | 0.0  | 125.29      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid  | In advance | Late | Outstanding |
      | 487.58        | 10.21    | 0.0  | 0.0       | 497.79  | 121.9 | 0.0        | 0.0  | 375.89      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 14 March 2024    | Disbursement     | 487.58  | 0.0       | 0.0      | 0.0  | 0.0       | 487.58       | false    | false    |
      | 14 March 2024    | Down Payment     | 121.9   | 121.9     | 0.0      | 0.0  | 0.0       | 365.68       | false    | false    |
    When Admin sets the business date to "24 March 2024"
    And Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "24 March 2024" with 201.39 EUR transaction amount and self-generated Idempotency key
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      |    |      | 14 March 2024       |                  | 487.58          |               |          | 0.0  |           | 0.0    | 0.0    |            |      |             |
      | 1  | 0    | 14 March 2024       | 14 March 2024    | 365.68          | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9  | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 29 March 2024       |                  | 244.53          | 121.15        | 4.15     | 0.0  | 0.0       | 125.3  | 0.0    | 0.0        | 0.0  | 125.3       |
      | 3  | 15   | 13 April 2024       |                  | 125.29          | 119.24        | 0.6      | 0.0  | 0.0       | 119.84 | 76.1   | 76.1       | 0.0  | 43.74       |
      | 4  | 15   | 28 April 2024       | 24 March 2024    | 0.0             | 125.29        | 0.0      | 0.0  | 0.0       | 125.29 | 125.29 | 125.29     | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid   | In advance | Late | Outstanding |
      | 487.58        | 4.75     | 0.0  | 0.0       | 492.33  | 323.29 | 201.39     | 0.0  | 169.04      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 14 March 2024    | Disbursement           | 487.58  | 0.0       | 0.0      | 0.0  | 0.0       | 487.58       | false    | false    |
      | 14 March 2024    | Down Payment           | 121.9   | 121.9     | 0.0      | 0.0  | 0.0       | 365.68       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 201.39  | 201.39    | 0.0      | 0.0  | 0.0       | 164.29       | false    | false    |
    Then Loan status will be "ACTIVE"
    Then Loan has 169.04 outstanding amount
    And Customer makes "MERCHANT_ISSUED_REFUND" transaction with "AUTOPAY" payment type on "24 March 2024" with 286.19 EUR transaction amount and self-generated Idempotency key
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid    | In advance | Late | Outstanding |
      |    |      | 14 March 2024       |                  | 487.58          |               |          | 0.0  |           | 0.0    | 0.0     |            |      |             |
      | 1  | 0    | 14 March 2024       | 14 March 2024    | 365.68          | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9   | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 29 March 2024       | 24 March 2024    | 250.59          | 115.09        | 3.39     | 0.0  | 0.0       | 118.48 | 118.48  | 118.48     | 0.0  | 0.0         |
      | 3  | 15   | 13 April 2024       | 24 March 2024    | 125.29          | 125.3         | 0.0      | 0.0  | 0.0       | 125.3  | 125.3   | 125.3      | 0.0  | 0.0         |
      | 4  | 15   | 28 April 2024       | 24 March 2024    | 0.0             | 125.29        | 0.0      | 0.0  | 0.0       | 125.29 | 125.29  | 125.29     | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid     | In advance | Late | Outstanding |
      | 487.58        | 3.39     | 0.0  | 0.0       | 490.97  | 490.97   | 369.07     | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 14 March 2024    | Disbursement           | 487.58  | 0.0       | 0.0      | 0.0  | 0.0       | 487.58       | false    | false    |
      | 14 March 2024    | Down Payment           | 121.9   | 121.9     | 0.0      | 0.0  | 0.0       | 365.68       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 201.39  | 201.39    | 0.0      | 0.0  | 0.0       | 164.29       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 286.19  | 164.29    | 3.39     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 24 March 2024    | Accrual                | 3.39    | 0.0       | 3.39     | 0.0  | 0.0       | 0.0          | false    | false    |
    Then Loan status will be "OVERPAID"
    Then Loan has 118.51 overpaid amount
    When Admin makes Credit Balance Refund transaction on "24 March 2024" with 11.9 EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid    | In advance | Late | Outstanding |
      |    |      | 14 March 2024       |                  | 487.58          |               |          | 0.0  |           | 0.0    | 0.0     |            |      |             |
      | 1  | 0    | 14 March 2024       | 14 March 2024    | 365.68          | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9   | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 29 March 2024       | 24 March 2024    | 250.59          | 115.09        | 3.39     | 0.0  | 0.0       | 118.48 | 118.48  | 118.48     | 0.0  | 0.0         |
      | 3  | 15   | 13 April 2024       | 24 March 2024    | 125.29          | 125.3         | 0.0      | 0.0  | 0.0       | 125.3  | 125.3   | 125.3      | 0.0  | 0.0         |
      | 4  | 15   | 28 April 2024       | 24 March 2024    | 0.0             | 125.29        | 0.0      | 0.0  | 0.0       | 125.29 | 125.29  | 125.29     | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid     | In advance | Late | Outstanding |
      | 487.58        | 3.39     | 0.0  | 0.0       | 490.97  | 490.97   | 369.07     | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 14 March 2024    | Disbursement           | 487.58  | 0.0       | 0.0      | 0.0  | 0.0       | 487.58       | false    | false    |
      | 14 March 2024    | Down Payment           | 121.9   | 121.9     | 0.0      | 0.0  | 0.0       | 365.68       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 201.39  | 201.39    | 0.0      | 0.0  | 0.0       | 164.29       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 286.19  | 164.29    | 3.39     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 24 March 2024    | Accrual                | 3.39    | 0.0       | 3.39     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 24 March 2024    | Credit Balance Refund  | 11.9    | 0.0       | 0.0      | 0.0  | 0.0       | 0.0          | false    | false    |
    Then Loan status will be "OVERPAID"
    Then Loan has 106.61 overpaid amount
    When Admin sets the business date to "01 April 2024"
    When Admin successfully disburse the loan on "01 April 2024" with "312.69" EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid    | In advance | Late | Outstanding |
      |    |      | 14 March 2024       |                  | 487.58          |               |          | 0.0  |           | 0.0    | 0.0     |            |      |             |
      | 1  | 0    | 14 March 2024       | 14 March 2024    | 365.68          | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9   | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 29 March 2024       | 24 March 2024    | 250.59          | 115.09        | 3.39     | 0.0  | 0.0       | 118.48 | 118.48  | 118.48     | 0.0  | 0.0         |
      |    |      | 01 April 2024       |                  | 312.69          |               |          | 0.0  |           | 0.0    | 0.0     |            |      |             |
      | 3  | 0    | 01 April 2024       | 01 April 2024    | 485.11          | 78.17         | 0.0      | 0.0  | 0.0       | 78.17  | 78.17   | 0.0        | 0.0  | 0.0         |
      | 4  | 15   | 13 April 2024       |                  | 239.25          | 245.86        | 2.29     | 0.0  | 0.0       | 248.15 | 139.52  | 139.52     | 0.0  | 108.63      |
      | 5  | 15   | 28 April 2024       |                  | 0.0             | 239.25        | 1.39     | 0.0  | 0.0       | 240.64 | 139.51  | 139.51     | 0.0  | 101.13      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid     | In advance | Late | Outstanding |
      | 800.27        | 7.07     | 0.0  | 0.0       | 807.34  | 597.58   | 397.51     | 0.0  | 209.76      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 14 March 2024    | Disbursement           | 487.58  | 0.0       | 0.0      | 0.0  | 0.0       | 487.58       | false    | false    |
      | 14 March 2024    | Down Payment           | 121.9   | 121.9     | 0.0      | 0.0  | 0.0       | 365.68       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 201.39  | 201.39    | 0.0      | 0.0  | 0.0       | 164.29       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 286.19  | 164.29    | 3.39     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 24 March 2024    | Accrual                | 3.39    | 0.0       | 3.39     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 24 March 2024    | Credit Balance Refund  | 11.9    | 0.0       | 0.0      | 0.0  | 0.0       | 0.0          | false    | false    |
      | 01 April 2024    | Disbursement           | 312.69  | 0.0       | 0.0      | 0.0  | 0.0       | 206.08       | false    | false    |
    Then Loan status will be "ACTIVE"
    Then Loan has 209.76 outstanding amount
    When Admin sets the business date to "10 April 2024"
    When Loan Pay-off is made on "10 April 2024"
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date                | Paid date        | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid    | In advance | Late | Outstanding |
      |    |      | 14 March 2024       |                  | 487.58          |               |          | 0.0  |           | 0.0    | 0.0     |            |      |             |
      | 1  | 0    | 14 March 2024       | 14 March 2024    | 365.68          | 121.9         | 0.0      | 0.0  | 0.0       | 121.9  | 121.9   | 0.0        | 0.0  | 0.0         |
      | 2  | 15   | 29 March 2024       | 24 March 2024    | 250.59          | 115.09        | 3.39     | 0.0  | 0.0       | 118.48 | 118.48  | 118.48     | 0.0  | 0.0         |
      |    |      | 01 April 2024       |                  | 312.69          |               |          | 0.0  |           | 0.0    | 0.0     |            |      |             |
      | 3  | 0    | 01 April 2024       | 01 April 2024    | 485.11          | 78.17         | 0.0      | 0.0  | 0.0       | 78.17  | 78.17   | 0.0        | 0.0  | 0.0         |
      | 4  | 15   | 13 April 2024       | 10 April 2024    | 241.69          | 243.42        | 1.72     | 0.0  | 0.0       | 245.14 | 245.14  | 245.14     | 0.0  | 0.0         |
      | 5  | 15   | 28 April 2024       | 10 April 2024    | 0.0             | 241.69        | 0.0      | 0.0  | 0.0       | 241.69 | 241.69  | 241.69     | 0.0  | 0.0         |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid     | In advance | Late | Outstanding |
      | 800.27        | 5.11     | 0.0  | 0.0       | 805.38  | 805.38   | 605.31     | 0.0  | 0.0         |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type       | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 14 March 2024    | Disbursement           | 487.58  | 0.0       | 0.0      | 0.0  | 0.0       | 487.58       | false    | false    |
      | 14 March 2024    | Down Payment           | 121.9   | 121.9     | 0.0      | 0.0  | 0.0       | 365.68       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 201.39  | 201.39    | 0.0      | 0.0  | 0.0       | 164.29       | false    | false    |
      | 24 March 2024    | Merchant Issued Refund | 286.19  | 164.29    | 3.39     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 24 March 2024    | Accrual                | 3.39    | 0.0       | 3.39     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 24 March 2024    | Credit Balance Refund  | 11.9    | 0.0       | 0.0      | 0.0  | 0.0       | 0.0          | false    | false    |
      | 01 April 2024    | Disbursement           | 312.69  | 0.0       | 0.0      | 0.0  | 0.0       | 206.08       | false    | false    |
      | 10 April 2024    | Repayment              | 207.8   | 206.08    | 1.72     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 10 April 2024    | Accrual                | 1.72    | 0.0       | 1.72     | 0.0  | 0.0       | 0.0          | false    | false    |
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
    Then Loan has 0 outstanding amount

  @TestRailId:C3700
  Scenario: Verify repayment schedule and accrual transactions created after penalty is added for paid off loan on maturity date - UC1
    When Admin sets the business date to "08 May 2024"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                              | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_360_30_ACCRUAL_ACTIVITY | 08 May 2024       | 1000           | 12.19                  | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "08 May 2024" with "1000" amount and expected disbursement date on "08 May 2024"
    When Admin successfully disburse the loan on "08 May 2024" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due      | Paid | In advance | Late | Outstanding |
      |    |      | 08 May 2024      |           | 1000.0          |               |          | 0.0  |           | 0.0      | 0.0  |            |      |             |
      | 1  | 31   | 08 June 2024     |           | 670.03          | 329.97        | 10.16    | 0.0  | 0.0       | 340.13   | 0.0  | 0.0        | 0.0  | 340.13      |
      | 2  | 30   | 08 July 2024     |           | 336.71          | 333.32        |  6.81    | 0.0  | 0.0       | 340.13   | 0.0  | 0.0        | 0.0  | 340.13      |
      | 3  | 31   | 08 August 2024   |           | 0.0             | 336.71        |  3.42    | 0.0  | 0.0       | 340.13   | 0.0  | 0.0        | 0.0  | 340.13      |
    And Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid | In advance | Late | Outstanding |
      | 1000.0        | 20.39    | 0.0  | 0.0       | 1020.39 | 0.0  | 0.0        | 0.0  | 1020.39     |
    And Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 08 May 2024      | Disbursement     | 1000.0  | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
    When Admin sets the business date to "08 June 2024"
    And Customer makes "AUTOPAY" repayment on "08 June 2024" with 340.13 EUR transaction amount
    When Admin sets the business date to "08 July 2024"
    And Customer makes "AUTOPAY" repayment on "08 July 2024" with 340.13 EUR transaction amount
    When Admin sets the business date to "08 August 2024"
    And Admin runs inline COB job for Loan
    And Customer makes "AUTOPAY" repayment on "08 August 2024" with 340.13 EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date      | Balance of loan | Principal due | Interest | Fees | Penalties | Due      | Paid   | In advance | Late | Outstanding |
      |    |      | 08 May 2024      |                | 1000.0          |               |          | 0.0  |           | 0.0      | 0.0    |            |      |             |
      | 1  | 31   | 08 June 2024     | 08 June 2024   | 670.03          | 329.97        | 10.16    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 2  | 30   | 08 July 2024     | 08 July 2024   | 336.71          | 333.32        |  6.81    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 3  | 31   | 08 August 2024   | 08 August 2024 | 0.0             | 336.71        |  3.42    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
    And Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid    | In advance | Late | Outstanding |
      | 1000.0        | 20.39    | 0.0  | 0.0       | 1020.39 | 1020.39 | 0.0        | 0.0  | 0.0         |
    And Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 08 May 2024      | Disbursement     | 1000.0  | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
      | 08 June 2024     | Repayment        | 340.13  | 329.97    | 10.16    | 0.0  | 0.0       | 670.03       | false    | false    |
      | 08 June 2024     | Accrual Activity | 10.16   | 0.0       | 10.16    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 July 2024     | Repayment        | 340.13  | 333.32    | 6.81     | 0.0  | 0.0       | 336.71       | false    | false    |
      | 08 July 2024     | Accrual Activity | 6.81    | 0.0       | 6.81     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 07 August 2024   | Accrual          | 20.28   | 0.0       | 20.28    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2024   | Repayment        | 340.13  | 336.71    | 3.42     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2024   | Accrual          | 0.11    | 0.0       | 0.11     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2024   | Accrual Activity | 3.42    | 0.0       | 3.42     | 0.0  | 0.0       | 0.0          | false    | false    |
# --- add penalty for paid off loan on maturity date ---#
    When Admin adds "LOAN_NSF_FEE" due date charge with "08 August 2024" due date and 2.8 EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date      | Balance of loan | Principal due | Interest | Fees | Penalties | Due      | Paid   | In advance | Late | Outstanding |
      |    |      | 08 May 2024      |                | 1000.0          |               |          | 0.0  |           | 0.0      | 0.0    |            |      |             |
      | 1  | 31   | 08 June 2024     | 08 June 2024   | 670.03          | 329.97        | 10.16    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 2  | 30   | 08 July 2024     | 08 July 2024   | 336.71          | 333.32        |  6.81    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 3  | 31   | 08 August 2024   |                | 0.0             | 336.71        |  3.42    | 0.0  | 2.8       | 342.93   | 340.13 | 0.0        | 0.0  | 2.8         |
    And Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid    | In advance | Late | Outstanding |
      | 1000.0        | 20.39    | 0.0  | 2.8       | 1023.19 | 1020.39 | 0.0        | 0.0  | 2.8         |
    And Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 08 May 2024      | Disbursement     | 1000.0  | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
      | 08 June 2024     | Repayment        | 340.13  | 329.97    | 10.16    | 0.0  | 0.0       | 670.03       | false    | false    |
      | 08 June 2024     | Accrual Activity | 10.16   | 0.0       | 10.16    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 July 2024     | Repayment        | 340.13  | 333.32    | 6.81     | 0.0  | 0.0       | 336.71       | false    | false    |
      | 08 July 2024     | Accrual Activity | 6.81    | 0.0       | 6.81     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 07 August 2024   | Accrual          | 20.28   | 0.0       | 20.28    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2024   | Repayment        | 340.13  | 336.71    | 3.42     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2024   | Accrual          | 0.11    | 0.0       | 0.11     | 0.0  | 0.0       | 0.0          | false    | false    |
    When Admin sets the business date to "09 August 2024"
    And Admin runs inline COB job for Loan
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date      | Balance of loan | Principal due | Interest | Fees | Penalties | Due      | Paid   | In advance | Late | Outstanding |
      |    |      | 08 May 2024      |                | 1000.0          |               |          | 0.0  |           | 0.0      | 0.0    |            |      |             |
      | 1  | 31   | 08 June 2024     | 08 June 2024   | 670.03          | 329.97        | 10.16    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 2  | 30   | 08 July 2024     | 08 July 2024   | 336.71          | 333.32        |  6.81    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 3  | 31   | 08 August 2024   |                | 0.0             | 336.71        |  3.42    | 0.0  | 2.8       | 342.93   | 340.13 | 0.0        | 0.0  | 2.8         |
    And Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid    | In advance | Late | Outstanding |
      | 1000.0        | 20.39    | 0.0  | 2.8       | 1023.19 | 1020.39 | 0.0        | 0.0  | 2.8         |
    And Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 08 May 2024      | Disbursement     | 1000.0  | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
      | 08 June 2024     | Repayment        | 340.13  | 329.97    | 10.16    | 0.0  | 0.0       | 670.03       | false    | false    |
      | 08 June 2024     | Accrual Activity | 10.16   | 0.0       | 10.16    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 July 2024     | Repayment        | 340.13  | 333.32    | 6.81     | 0.0  | 0.0       | 336.71       | false    | false    |
      | 08 July 2024     | Accrual Activity | 6.81    | 0.0       | 6.81     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 07 August 2024   | Accrual          | 20.28   | 0.0       | 20.28    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2024   | Repayment        | 340.13  | 336.71    | 3.42     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2024   | Accrual          | 0.11    | 0.0       | 0.11     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2024   | Accrual          | 2.8     | 0.0       | 0.0      | 0.0  | 2.8       | 0.0          | false    | false    |
      | 08 August 2024   | Accrual Activity | 6.22    | 0.0       | 3.42     | 0.0  | 2.8       | 0.0          | false    | false    |
    When Admin sets the business date to "15 August 2024"
    And Admin runs inline COB job for Loan
    And Customer makes "AUTOPAY" repayment on "15 August 2024" with 2.8 EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date      | Balance of loan | Principal due | Interest | Fees | Penalties | Due      | Paid   | In advance | Late | Outstanding |
      |    |      | 08 May 2024      |                | 1000.0          |               |          | 0.0  |           | 0.0      | 0.0    |            |      |             |
      | 1  | 31   | 08 June 2024     | 08 June 2024   | 670.03          | 329.97        | 10.16    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 2  | 30   | 08 July 2024     | 08 July 2024   | 336.71          | 333.32        |  6.81    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 3  | 31   | 08 August 2024   | 15 August 2024 | 0.0             | 336.71        |  3.42    | 0.0  | 2.8       | 342.93   | 342.93 | 0.0        | 2.8  | 0.0         |
    And Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid    | In advance | Late | Outstanding |
      | 1000.0        | 20.39    | 0.0  | 2.8       | 1023.19 | 1023.19 | 0.0        | 2.8  | 0.0         |
    And Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 08 May 2024      | Disbursement     | 1000.0  | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
      | 08 June 2024     | Repayment        | 340.13  | 329.97    | 10.16    | 0.0  | 0.0       | 670.03       | false    | false    |
      | 08 June 2024     | Accrual Activity | 10.16   | 0.0       | 10.16    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 July 2024     | Repayment        | 340.13  | 333.32    | 6.81     | 0.0  | 0.0       | 336.71       | false    | false    |
      | 08 July 2024     | Accrual Activity | 6.81    | 0.0       | 6.81     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 07 August 2024   | Accrual          | 20.28   | 0.0       | 20.28    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2024   | Repayment        | 340.13  | 336.71    | 3.42     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2024   | Accrual          | 0.11    | 0.0       | 0.11     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2024   | Accrual          | 2.8     | 0.0       | 0.0      | 0.0  | 2.8       | 0.0          | false    | false    |
      | 08 August 2024   | Accrual Activity | 6.22    | 0.0       | 3.42     | 0.0  | 2.8       | 0.0          | false    | false    |
      | 15 August 2024   | Repayment        | 2.8     | 0.0       | 0.0      | 0.0  | 2.8       | 0.0          | false    | false    |
    When Admin sets the business date to "16 August 2024"
    And Admin runs inline COB job for Loan
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date      | Balance of loan | Principal due | Interest | Fees | Penalties | Due      | Paid   | In advance | Late | Outstanding |
      |    |      | 08 May 2024      |                | 1000.0          |               |          | 0.0  |           | 0.0      | 0.0    |            |      |             |
      | 1  | 31   | 08 June 2024     | 08 June 2024   | 670.03          | 329.97        | 10.16    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 2  | 30   | 08 July 2024     | 08 July 2024   | 336.71          | 333.32        |  6.81    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 3  | 31   | 08 August 2024   | 15 August 2024 | 0.0             | 336.71        |  3.42    | 0.0  | 2.8       | 342.93   | 342.93 | 0.0        | 2.8  | 0.0         |
    And Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid    | In advance | Late | Outstanding |
      | 1000.0        | 20.39    | 0.0  | 2.8       | 1023.19 | 1023.19 | 0.0        | 2.8  | 0.0         |
    And Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 08 May 2024      | Disbursement     | 1000.0  | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
      | 08 June 2024     | Repayment        | 340.13  | 329.97    | 10.16    | 0.0  | 0.0       | 670.03       | false    | false    |
      | 08 June 2024     | Accrual Activity | 10.16   | 0.0       | 10.16    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 July 2024     | Repayment        | 340.13  | 333.32    | 6.81     | 0.0  | 0.0       | 336.71       | false    | false    |
      | 08 July 2024     | Accrual Activity | 6.81    | 0.0       | 6.81     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 07 August 2024   | Accrual          | 20.28   | 0.0       | 20.28    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2024   | Repayment        | 340.13  | 336.71    | 3.42     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2024   | Accrual          | 0.11    | 0.0       | 0.11     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2024   | Accrual          | 2.8     | 0.0       | 0.0      | 0.0  | 2.8       | 0.0          | false    | false    |
      | 08 August 2024   | Accrual Activity | 6.22    | 0.0       | 3.42     | 0.0  | 2.8       | 0.0          | false    | false    |
      | 15 August 2024   | Repayment        | 2.8     | 0.0       | 0.0      | 0.0  | 2.8       | 0.0          | false    | false    |

  @TestRailId:C3701
  Scenario: Verify repayment schedule and accrual transactions created after penalty is added for paid off loan with accrual activity on maturity date - UC2
    When Admin sets the business date to "08 May 2025"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                                           | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_360_30_INTEREST_RECALCULATION_ZERO_INTEREST_CHARGE_OFF_ACCRUAL_ACTIVITY | 08 May 2025       | 1000           | 12.19                  | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "08 May 2025" with "1000" amount and expected disbursement date on "08 May 2025"
    When Admin successfully disburse the loan on "08 May 2025" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due      | Paid | In advance | Late | Outstanding |
      |    |      | 08 May 2025      |           | 1000.0          |               |          | 0.0  |           | 0.0      | 0.0  |            |      |             |
      | 1  | 31   | 08 June 2025     |           | 670.03          | 329.97        | 10.16    | 0.0  | 0.0       | 340.13   | 0.0  | 0.0        | 0.0  | 340.13      |
      | 2  | 30   | 08 July 2025     |           | 336.71          | 333.32        |  6.81    | 0.0  | 0.0       | 340.13   | 0.0  | 0.0        | 0.0  | 340.13      |
      | 3  | 31   | 08 August 2025   |           | 0.0             | 336.71        |  3.42    | 0.0  | 0.0       | 340.13   | 0.0  | 0.0        | 0.0  | 340.13      |
    And Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid | In advance | Late | Outstanding |
      | 1000.0        | 20.39    | 0.0  | 0.0       | 1020.39 | 0.0  | 0.0        | 0.0  | 1020.39     |
    And Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 08 May 2025      | Disbursement     | 1000.0  | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
    When Admin sets the business date to "08 June 2025"
    And Customer makes "AUTOPAY" repayment on "08 June 2025" with 340.13 EUR transaction amount
    When Admin sets the business date to "08 July 2025"
    And Customer makes "AUTOPAY" repayment on "08 July 2025" with 340.13 EUR transaction amount
    When Admin sets the business date to "08 August 2025"
    And Admin runs inline COB job for Loan
    And Customer makes "AUTOPAY" repayment on "08 August 2025" with 340.13 EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date      | Balance of loan | Principal due | Interest | Fees | Penalties | Due      | Paid   | In advance | Late | Outstanding |
      |    |      | 08 May 2025      |                | 1000.0          |               |          | 0.0  |           | 0.0      | 0.0    |            |      |             |
      | 1  | 31   | 08 June 2025     | 08 June 2025   | 670.03          | 329.97        | 10.16    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 2  | 30   | 08 July 2025     | 08 July 2025   | 336.71          | 333.32        |  6.81    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 3  | 31   | 08 August 2025   | 08 August 2025 | 0.0             | 336.71        |  3.42    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
    And Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid    | In advance | Late | Outstanding |
      | 1000.0        | 20.39    | 0.0  | 0.0       | 1020.39 | 1020.39 | 0.0        | 0.0  | 0.0         |
    And Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 08 May 2025      | Disbursement     | 1000.0  | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
      | 08 June 2025     | Repayment        | 340.13  | 329.97    | 10.16    | 0.0  | 0.0       | 670.03       | false    | false    |
      | 08 June 2025     | Accrual Activity | 10.16   | 0.0       | 10.16    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 July 2025     | Repayment        | 340.13  | 333.32    | 6.81     | 0.0  | 0.0       | 336.71       | false    | false    |
      | 08 July 2025     | Accrual Activity | 6.81    | 0.0       | 6.81     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 07 August 2025   | Accrual          | 20.28   | 0.0       | 20.28    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2025   | Repayment        | 340.13  | 336.71    | 3.42     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2025   | Accrual          | 0.11    | 0.0       | 0.11     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2025   | Accrual Activity | 3.42    | 0.0       | 3.42     | 0.0  | 0.0       | 0.0          | false    | false    |
# --- add penalty for paid off loan on maturity date ---#
    When Admin adds "LOAN_NSF_FEE" due date charge with "08 August 2025" due date and 2.8 EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date      | Balance of loan | Principal due | Interest | Fees | Penalties | Due      | Paid   | In advance | Late | Outstanding |
      |    |      | 08 May 2025      |                | 1000.0          |               |          | 0.0  |           | 0.0      | 0.0    |            |      |             |
      | 1  | 31   | 08 June 2025     | 08 June 2025   | 670.03          | 329.97        | 10.16    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 2  | 30   | 08 July 2025     | 08 July 2025   | 336.71          | 333.32        |  6.81    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 3  | 31   | 08 August 2025   |                | 0.0             | 336.71        |  3.42    | 0.0  | 2.8       | 342.93   | 340.13 | 0.0        | 0.0  | 2.8         |
    And Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid    | In advance | Late | Outstanding |
      | 1000.0        | 20.39    | 0.0  | 2.8       | 1023.19 | 1020.39 | 0.0        | 0.0  | 2.8         |
    And Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 08 May 2025      | Disbursement     | 1000.0  | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
      | 08 June 2025     | Repayment        | 340.13  | 329.97    | 10.16    | 0.0  | 0.0       | 670.03       | false    | false    |
      | 08 June 2025     | Accrual Activity | 10.16   | 0.0       | 10.16    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 July 2025     | Repayment        | 340.13  | 333.32    | 6.81     | 0.0  | 0.0       | 336.71       | false    | false    |
      | 08 July 2025     | Accrual Activity | 6.81    | 0.0       | 6.81     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 07 August 2025   | Accrual          | 20.28   | 0.0       | 20.28    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2025   | Repayment        | 340.13  | 336.71    | 3.42     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2025   | Accrual          | 0.11    | 0.0       | 0.11     | 0.0  | 0.0       | 0.0          | false    | false    |
    When Admin sets the business date to "09 August 2025"
    And Admin runs inline COB job for Loan
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date      | Balance of loan | Principal due | Interest | Fees | Penalties | Due      | Paid   | In advance | Late | Outstanding |
      |    |      | 08 May 2025      |                | 1000.0          |               |          | 0.0  |           | 0.0      | 0.0    |            |      |             |
      | 1  | 31   | 08 June 2025     | 08 June 2025   | 670.03          | 329.97        | 10.16    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 2  | 30   | 08 July 2025     | 08 July 2025   | 336.71          | 333.32        |  6.81    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 3  | 31   | 08 August 2025   |                | 0.0             | 336.71        |  3.42    | 0.0  | 2.8       | 342.93   | 340.13 | 0.0        | 0.0  | 2.8         |
    And Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid    | In advance | Late | Outstanding |
      | 1000.0        | 20.39    | 0.0  | 2.8       | 1023.19 | 1020.39 | 0.0        | 0.0  | 2.8         |
    And Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 08 May 2025      | Disbursement     | 1000.0  | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
      | 08 June 2025     | Repayment        | 340.13  | 329.97    | 10.16    | 0.0  | 0.0       | 670.03       | false    | false    |
      | 08 June 2025     | Accrual Activity | 10.16   | 0.0       | 10.16    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 July 2025     | Repayment        | 340.13  | 333.32    | 6.81     | 0.0  | 0.0       | 336.71       | false    | false    |
      | 08 July 2025     | Accrual Activity | 6.81    | 0.0       | 6.81     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 07 August 2025   | Accrual          | 20.28   | 0.0       | 20.28    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2025   | Repayment        | 340.13  | 336.71    | 3.42     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2025   | Accrual          | 0.11    | 0.0       | 0.11     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2025   | Accrual          | 2.8     | 0.0       | 0.0      | 0.0  | 2.8       | 0.0          | false    | false    |
      | 08 August 2025   | Accrual Activity | 6.22    | 0.0       | 3.42     | 0.0  | 2.8       | 0.0          | false    | false    |
    When Admin sets the business date to "15 August 2025"
    And Admin runs inline COB job for Loan
    And Customer makes "AUTOPAY" repayment on "15 August 2025" with 2.8 EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date      | Balance of loan | Principal due | Interest | Fees | Penalties | Due      | Paid   | In advance | Late | Outstanding |
      |    |      | 08 May 2025      |                | 1000.0          |               |          | 0.0  |           | 0.0      | 0.0    |            |      |             |
      | 1  | 31   | 08 June 2025     | 08 June 2025   | 670.03          | 329.97        | 10.16    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 2  | 30   | 08 July 2025     | 08 July 2025   | 336.71          | 333.32        |  6.81    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 3  | 31   | 08 August 2025   | 15 August 2025 | 0.0             | 336.71        |  3.42    | 0.0  | 2.8       | 342.93   | 342.93 | 0.0        | 2.8  | 0.0         |
    And Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid    | In advance | Late | Outstanding |
      | 1000.0        | 20.39    | 0.0  | 2.8       | 1023.19 | 1023.19 | 0.0        | 2.8  | 0.0         |
    And Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 08 May 2025      | Disbursement     | 1000.0  | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
      | 08 June 2025     | Repayment        | 340.13  | 329.97    | 10.16    | 0.0  | 0.0       | 670.03       | false    | false    |
      | 08 June 2025     | Accrual Activity | 10.16   | 0.0       | 10.16    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 July 2025     | Repayment        | 340.13  | 333.32    | 6.81     | 0.0  | 0.0       | 336.71       | false    | false    |
      | 08 July 2025     | Accrual Activity | 6.81    | 0.0       | 6.81     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 07 August 2025   | Accrual          | 20.28   | 0.0       | 20.28    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2025   | Repayment        | 340.13  | 336.71    | 3.42     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2025   | Accrual          | 0.11    | 0.0       | 0.11     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2025   | Accrual          | 2.8     | 0.0       | 0.0      | 0.0  | 2.8       | 0.0          | false    | false    |
      | 08 August 2025   | Accrual Activity | 6.22    | 0.0       | 3.42     | 0.0  | 2.8       | 0.0          | false    | false    |
      | 08 August 2025   | Accrual          | 2.8     | 0.0       | 0.0      | 0.0  | 2.8       | 0.0          | false    | false    |
    When Admin sets the business date to "16 August 2025"
    And Admin runs inline COB job for Loan
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date      | Balance of loan | Principal due | Interest | Fees | Penalties | Due      | Paid   | In advance | Late | Outstanding |
      |    |      | 08 May 2025      |                | 1000.0          |               |          | 0.0  |           | 0.0      | 0.0    |            |      |             |
      | 1  | 31   | 08 June 2025     | 08 June 2025   | 670.03          | 329.97        | 10.16    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 2  | 30   | 08 July 2025     | 08 July 2025   | 336.71          | 333.32        |  6.81    | 0.0  | 0.0       | 340.13   | 340.13 | 0.0        | 0.0  | 0.0         |
      | 3  | 31   | 08 August 2025   | 15 August 2025 | 0.0             | 336.71        |  3.42    | 0.0  | 2.8       | 342.93   | 342.93 | 0.0        | 2.8  | 0.0         |
    And Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid    | In advance | Late | Outstanding |
      | 1000.0        | 20.39    | 0.0  | 2.8       | 1023.19 | 1023.19 | 0.0        | 2.8  | 0.0         |
    And Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 08 May 2025      | Disbursement     | 1000.0  | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |
      | 08 June 2025     | Repayment        | 340.13  | 329.97    | 10.16    | 0.0  | 0.0       | 670.03       | false    | false    |
      | 08 June 2025     | Accrual Activity | 10.16   | 0.0       | 10.16    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 July 2025     | Repayment        | 340.13  | 333.32    | 6.81     | 0.0  | 0.0       | 336.71       | false    | false    |
      | 08 July 2025     | Accrual Activity | 6.81    | 0.0       | 6.81     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 07 August 2025   | Accrual          | 20.28   | 0.0       | 20.28    | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2025   | Repayment        | 340.13  | 336.71    | 3.42     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2025   | Accrual          | 0.11    | 0.0       | 0.11     | 0.0  | 0.0       | 0.0          | false    | false    |
      | 08 August 2025   | Accrual          | 2.8     | 0.0       | 0.0      | 0.0  | 2.8       | 0.0          | false    | false    |
      | 08 August 2025   | Accrual Activity | 6.22    | 0.0       | 3.42     | 0.0  | 2.8       | 0.0          | false    | false    |
      | 15 August 2025   | Repayment        | 2.8     | 0.0       | 0.0      | 0.0  | 2.8       | 0.0          | false    | false    |

  @TestRailId:C3858
  Scenario: Verify update approved amount for progressive loan - UC1
    When Admin sets the business date to "01 January 2025"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                                                    | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_360_30_INTEREST_RECALCULATION_DAILY_TILL_PRECLOSE_PMT_ALLOC_1 | 01 January 2025   | 1000            | 7                      | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2025" with "1000" amount and expected disbursement date on "01 January 2025"
    Then Update loan approved amount is forbidden with amount "0" due to min allowed amount
    When Admin successfully disburse the loan on "01 January 2025" with "1000" EUR transaction amount

    When Loan Pay-off is made on "1 January 2025"
    Then Loan's all installments have obligations met

  @TestRailId:C3859
  Scenario: Verify update approved amount after undo disbursement for single disb progressive loan - UC3
    When Admin sets the business date to "1 January 2025"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_360_30_INTEREST_RECALCULATION_DAILY_TILL_PRECLOSE | 01 January 2025   | 1000           | 7                      | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "1 January 2025" with "1000" amount and expected disbursement date on "1 January 2025"
    And Admin successfully disburse the loan on "1 January 2025" with "100" EUR transaction amount
    Then Update loan approved amount with new amount "600" value
    When Admin successfully undo disbursal
    Then Admin fails to disburse the loan on "1 January 2025" with "700" EUR transaction amount due to exceed approved amount
    When Admin successfully disburse the loan on "01 January 2025" with "600" EUR transaction amount

    When Loan Pay-off is made on "1 January 2025"
    Then Loan's all installments have obligations met

  @TestRailId:C3860
  Scenario: Verify update approved amount with approved over applied amount for progressive multidisbursal loan with percentage overAppliedCalculationType - UC4
    When Admin sets the business date to "1 January 2025"
    And Admin creates a client with random data
    And Admin creates a fully customized loan with the following data:
      | LoanProduct                                                                                       | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_RECALC_EMI_360_30_MULTIDISB_APPROVED_OVER_APPLIED_CAPITALIZED_INCOME | 01 January 2025   | 1000           | 7                      | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "1 January 2025" with "1000" amount and expected disbursement date on "1 January 2025"
    And Admin successfully disburse the loan on "1 January 2025" with "1100" EUR transaction amount
    Then Update loan approved amount is forbidden with amount "1600" due to exceed applied amount
    Then Update loan approved amount with new amount "1300" value
    And Admin successfully disburse the loan on "1 January 2025" with "400" EUR transaction amount

    When Loan Pay-off is made on "1 January 2025"
    Then Loan's all installments have obligations met

  @TestRailId:C3861
  Scenario: Verify update approved amount with approved over applied amount and capitalized income for progressive loan with percentage overAppliedCalculationType - UC8_1
    When Admin sets the business date to "1 January 2025"
    And Admin creates a client with random data
    And Admin creates a fully customized loan with the following data:
      | LoanProduct                                                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_RECALC_EMI_360_30_APPROVED_OVER_APPLIED_PERCENTAGE_CAPITALIZED_INCOME | 01 January 2025   | 1000           | 7                      | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "1 January 2025" with "1000" amount and expected disbursement date on "1 January 2025"
    And Admin successfully disburse the loan on "1 January 2025" with "1100" EUR transaction amount
    Then Update loan approved amount is forbidden with amount "1600" due to exceed applied amount
    Then Update loan approved amount with new amount "1400" value
    And Admin adds capitalized income with "AUTOPAY" payment type to the loan on "1 January 2025" with "400" EUR transaction amount

    When Loan Pay-off is made on "1 January 2025"
    Then Loan's all installments have obligations met

  @TestRailId:C3862
  Scenario: Verify update approved amount with capitalized income for progressive loan - UC8_2
    When Admin sets the business date to "1 January 2025"
    And Admin creates a client with random data
    And Admin creates a fully customized loan with the following data:
      | LoanProduct                                                                      | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_360_30_INTEREST_RECALC_DAILY_CAPITALIZED_INCOME | 01 January 2025   | 1000           | 7                      | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "1 January 2025" with "900" amount and expected disbursement date on "1 January 2025"
    And Admin successfully disburse the loan on "1 January 2025" with "600" EUR transaction amount
    Then Update loan approved amount is forbidden with amount "500" due to higher principal amount on loan
    And Admin adds capitalized income with "AUTOPAY" payment type to the loan on "1 January 2025" with "200" EUR transaction amount
    Then Update loan approved amount with new amount "1000" value
    And Admin adds capitalized income with "AUTOPAY" payment type to the loan on "1 January 2025" with "200" EUR transaction amount

    When Loan Pay-off is made on "1 January 2025"
    Then Loan's all installments have obligations met

  @TestRailId:C3863
  Scenario: Verify update approved amount with capitalized income for progressive multidisbursal loan - UC8_3
    When Admin sets the business date to "1 January 2025"
    And Admin creates a client with random data
    And Admin creates a fully customized loan with the following data:
      | LoanProduct                                                                                  | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_RECALC_EMI_360_30_MULTIDISB_CAPITALIZED_INCOME_ADJ_CUSTOM_ALLOC | 01 January 2025   | 1000           | 7                      | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "1 January 2025" with "900" amount and expected disbursement date on "1 January 2025"
    And Admin successfully disburse the loan on "1 January 2025" with "600" EUR transaction amount
    Then Update loan approved amount is forbidden with amount "500" due to higher principal amount on loan
    And Admin adds capitalized income with "AUTOPAY" payment type to the loan on "1 January 2025" with "200" EUR transaction amount
    Then Update loan approved amount with new amount "1000" value
    And Admin successfully disburse the loan on "1 January 2025" with "200" EUR transaction amount

    When Loan Pay-off is made on "1 January 2025"
    Then Loan's all installments have obligations met

  @TestRailId:C3864
  Scenario: Verify update approved amount before disbursement for single disb cumulative loan - UC5_1
    When Admin sets the business date to "1 January 2025"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct       | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy                        |
      | LP1_INTEREST_FLAT | 1 January 2025    | 1000           | 0                      | DECLINING_BALANCE | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | PENALTIES_FEES_INTEREST_PRINCIPAL_ORDER |
    And Admin successfully approves the loan on "1 January 2025" with "1000" amount and expected disbursement date on "1 January 2025"
    Then Update loan approved amount with new amount "900" value
    And Admin successfully disburse the loan on "1 January 2025" with "100" EUR transaction amount

    When Loan Pay-off is made on "1 January 2025"
    Then Loan's all installments have obligations met

  @TestRailId:C3865
  Scenario: Verify update approved amount before disbursement for single disb progressive loan - UC5_2
    When Admin sets the business date to "1 January 2025"
    And Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_360_30_INTEREST_RECALCULATION_DAILY_TILL_PRECLOSE | 01 January 2025   | 1000           | 7                      | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "1 January 2025" with "1000" amount and expected disbursement date on "1 January 2025"
    Then Update loan approved amount with new amount "900" value
    And Admin successfully disburse the loan on "1 January 2025" with "100" EUR transaction amount

    When Loan Pay-off is made on "1 January 2025"
    Then Loan's all installments have obligations met

  @TestRailId:C3866
  Scenario: Verify approved amount change for progressive multidisbursal loan that doesn't expect tranches - UC6
    When Admin sets the business date to "01 January 2025"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                                 | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_360_30_INTEREST_RECALC_DAILY_MULTIDISBURSE | 01 January 2025   | 1000           | 7                      | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 January 2025" with "1000" amount and expected disbursement date on "01 January 2025"
    When Admin successfully disburse the loan on "01 January 2025" with "200" EUR transaction amount
    Then Loan Repayment schedule has 6 periods, with the following data for periods:
      | Nr | Days | Date             | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 January 2025  |           | 200.0           |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 31   | 01 February 2025 |           | 167.15          | 32.85         | 1.17     | 0.0  | 0.0       | 34.02 | 0.0  | 0.0        | 0.0  | 34.02       |
      | 2  | 28   | 01 March 2025    |           | 134.11          | 33.04         | 0.98     | 0.0  | 0.0       | 34.02 | 0.0  | 0.0        | 0.0  | 34.02       |
      | 3  | 31   | 01 April 2025    |           | 100.87          | 33.24         | 0.78     | 0.0  | 0.0       | 34.02 | 0.0  | 0.0        | 0.0  | 34.02       |
      | 4  | 30   | 01 May 2025      |           |  67.44          | 33.43         | 0.59     | 0.0  | 0.0       | 34.02 | 0.0  | 0.0        | 0.0  | 34.02       |
      | 5  | 31   | 01 June 2025     |           |  33.81          | 33.63         | 0.39     | 0.0  | 0.0       | 34.02 | 0.0  | 0.0        | 0.0  | 34.02       |
      | 6  | 30   | 01 July 2025     |           |   0.0           | 33.81         | 0.2      | 0.0  | 0.0       | 34.01 | 0.0  | 0.0        | 0.0  | 34.01       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 200.0         | 4.11     | 0.0  | 0.0       | 204.11 | 0.0  | 0.0        | 0.0  | 204.11      |
    Then Update loan approved amount with new amount "600" value
    When Admin successfully disburse the loan on "01 January 2025" with "400" EUR transaction amount
    Then Update loan approved amount is forbidden with amount "500" due to higher principal amount on loan

    When Loan Pay-off is made on "1 January 2025"
    Then Loan's all installments have obligations met

  @TestRailId:C3867
  Scenario: Verify approved amount change with lower value for progressive multidisbursal loan that expects two tranches - UC7_1
    When Admin sets the business date to "01 January 2025"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with three expected disbursements details and following data:
      | LoanProduct                                                                                | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            | 1st_tranche_disb_expected_date | 1st_tranche_disb_principal | 2nd_tranche_disb_expected_date | 2nd_tranche_disb_principal | 3rd_tranche_disb_expected_date | 3rd_tranche_disb_principal |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_360_30_INTEREST_RECALC_DAILY_MULTIDISBURSE_EXPECT_TRANCHE | 01 January 2025   | 1000           | 7                      | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION | 01 January 2025                | 300.0                      | 02 January 2025                | 200.0                      | 03 January 2025                | 500.0                      |
    And Admin successfully approves the loan on "01 January 2025" with "1000" amount and expected disbursement date on "01 January 2025"
    Then Loan Tranche Details tab has the following data:
      | Expected Disbursement On | Disbursed On    | Principal   | Net Disbursal Amount |
      | 01 January 2025          |                 | 300.0       |                      |
      | 02 January 2025          |                 | 200.0       |                      |
      | 03 January 2025          |                 | 500.0       |                      |
#    --- disbursement - 1 January, 2025  ---
    When Admin successfully disburse the loan on "01 January 2025" with "300" EUR transaction amount
    When Admin sets the business date to "03 January 2025"
    Then Update loan approved amount is forbidden with amount "800" due to higher principal amount on loan

    When Admin successfully disburse the loan on "02 January 2025" with "200" EUR transaction amount
    Then Admin fails to disburse the loan on "03 January 2025" with "700" EUR transaction amount due to exceed approved amount
    When Admin successfully disburse the loan on "03 January 2025" with "500" EUR transaction amount
    Then Loan Tranche Details tab has the following data:
      | Expected Disbursement On | Disbursed On    | Principal   | Net Disbursal Amount |
      | 01 January 2025          | 01 January 2025 | 300.0       |                      |
      | 02 January 2025          | 02 January 2025 | 200.0       |                      |
      | 03 January 2025          | 03 January 2025 | 500.0       |                      |
    And Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 01 January 2025  | Disbursement     | 300.0   | 0.0       | 0.0      | 0.0  | 0.0       | 300.0        | false    | false    |
      | 02 January 2025  | Disbursement     | 200.0   | 0.0       | 0.0      | 0.0  | 0.0       | 500.0        | false    | false    |
      | 03 January 2025  | Disbursement     | 500.0   | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       | false    | false    |

    When Loan Pay-off is made on "3 January 2025"
    Then Loan's all installments have obligations met

  @TestRailId:C3868
  Scenario: Verify approved amount change with greater value for progressive multidisbursal loan that expects two tranches - UC7_2
    When Admin sets the business date to "01 January 2025"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with three expected disbursements details and following data:
      | LoanProduct                                                                                | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            | 1st_tranche_disb_expected_date | 1st_tranche_disb_principal | 2nd_tranche_disb_expected_date | 2nd_tranche_disb_principal | 3rd_tranche_disb_expected_date | 3rd_tranche_disb_principal |
      | LP2_ADV_PYMNT_INTEREST_DAILY_EMI_360_30_INTEREST_RECALC_DAILY_MULTIDISBURSE_EXPECT_TRANCHE | 01 January 2025   | 1200           | 7                      | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 6                 | MONTHS                | 1              | MONTHS                 | 6                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION | 01 January 2025                | 300.0                      | 02 January 2025                | 200.0                      | 03 January 2025                | 500.0                      |
    And Admin successfully approves the loan on "01 January 2025" with "1000" amount and expected disbursement date on "01 January 2025"
    Then Loan Tranche Details tab has the following data:
      | Expected Disbursement On | Disbursed On    | Principal   | Net Disbursal Amount |
      | 01 January 2025          |                 | 300.0       |                      |
      | 02 January 2025          |                 | 200.0       |                      |
      | 03 January 2025          |                 | 500.0       |                      |
#    --- disbursement - 1 January, 2025  ---
    When Admin successfully disburse the loan on "01 January 2025" with "300" EUR transaction amount
    When Admin sets the business date to "03 January 2025"
    Then Update loan approved amount with new amount "1200" value

    When Admin successfully disburse the loan on "02 January 2025" with "300" EUR transaction amount
    Then Admin fails to disburse the loan on "03 January 2025" with "700" EUR transaction amount due to exceed approved amount
    When Admin successfully disburse the loan on "03 January 2025" with "600" EUR transaction amount
    Then Loan Tranche Details tab has the following data:
      | Expected Disbursement On | Disbursed On    | Principal   | Net Disbursal Amount |
      | 01 January 2025          | 01 January 2025 | 300.0       |                      |
      | 02 January 2025          | 02 January 2025 | 300.0       |                      |
      | 03 January 2025          | 03 January 2025 | 600.0       |                      |
    And Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount  | Principal | Interest | Fees | Penalties | Loan Balance | Reverted | Replayed |
      | 01 January 2025  | Disbursement     | 300.0   | 0.0       | 0.0      | 0.0  | 0.0       | 300.0        | false    | false    |
      | 02 January 2025  | Disbursement     | 300.0   | 0.0       | 0.0      | 0.0  | 0.0       | 600.0        | false    | false    |
      | 03 January 2025  | Disbursement     | 600.0   | 0.0       | 0.0      | 0.0  | 0.0       | 1200.0       | false    | false    |

    When Loan Pay-off is made on "3 January 2025"
    Then Loan's all installments have obligations met

  @TestRailId:C3963
  Scenario: Verify Progressive Loan reschedule by extending repayment period: Basic scenario without downpayment, flat interest type
    When Admin sets the business date to "01 June 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                            | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP1_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 June 2024      | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 June 2024" with "1000" amount and expected disbursement date on "01 June 2024"
    When Admin successfully disburse the loan on "01 June 2024" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 30   | 01 July 2024      |           | 667.0           | 333.0         | 0.0      | 0.0  | 0.0       | 333.0 | 0.0  | 0.0        | 0.0  | 333.0       |
      | 2  | 31   | 01 August 2024    |           | 334.0           | 333.0         | 0.0      | 0.0  | 0.0       | 333.0 | 0.0  | 0.0        | 0.0  | 333.0       |
      | 3  | 31   | 01 September 2024 |           | 0.0             | 334.0         | 0.0      | 0.0  | 0.0       | 334.0 | 0.0  | 0.0        | 0.0  | 334.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    When Admin sets the business date to "01 July 2024"
    When Admin creates and approves Loan reschedule with the following data:
      | rescheduleFromDate | submittedOnDate | adjustedDueDate | graceOnPrincipal | graceOnInterest | extraTerms | newInterestRate |
      | 01 August 2024     | 01 July 2024    |                 |                  |                 | 1          |                 |
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 30   | 01 July 2024      |           | 667.0           | 333.0         | 0.0      | 0.0  | 0.0       | 333.0 | 0.0  | 0.0        | 0.0  | 333.0       |
      | 2  | 31   | 01 August 2024    |           | 445.0           | 222.0         | 0.0      | 0.0  | 0.0       | 222.0 | 0.0  | 0.0        | 0.0  | 222.0       |
      | 3  | 31   | 01 September 2024 |           | 223.0           | 222.0         | 0.0      | 0.0  | 0.0       | 222.0 | 0.0  | 0.0        | 0.0  | 222.0       |
      | 4  | 30   | 01 October 2024   |           | 0.0             | 223.0         | 0.0      | 0.0  | 0.0       | 223.0 | 0.0  | 0.0        | 0.0  | 223.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |

  @TestRailId:C3964
  Scenario: Verify Progressive Loan reschedule by extending repayment period with downpayment installment, flat interest type
    When Admin sets the business date to "01 June 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                        | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_DOWNPAYMENT_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 June 2024      | 1000           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 June 2024" with "1000" amount and expected disbursement date on "01 June 2024"
    When Admin successfully disburse the loan on "01 June 2024" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 June 2024      |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 30   | 01 July 2024      |           | 500.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 3  | 31   | 01 August 2024    |           | 250.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 4  | 31   | 01 September 2024 |           | 0.0             | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    When Admin sets the business date to "01 July 2024"
    When Admin creates and approves Loan reschedule with the following data:
      | rescheduleFromDate | submittedOnDate | adjustedDueDate | graceOnPrincipal | graceOnInterest | extraTerms | newInterestRate |
      | 01 July 2024       | 01 July 2024    |                 |                  |                 | 1          |                 |
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |           | 1000.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 0    | 01 June 2024      |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0 | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 30   | 01 July 2024      |           | 562.5           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 0.0  | 0.0        | 0.0  | 187.5       |
      | 3  | 31   | 01 August 2024    |           | 375.0           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 0.0  | 0.0        | 0.0  | 187.5       |
      | 4  | 31   | 01 September 2024 |           | 187.5           | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 0.0  | 0.0        | 0.0  | 187.5       |
      | 5  | 30   | 01 October 2024   |           | 0.0             | 187.5         | 0.0      | 0.0  | 0.0       | 187.5 | 0.0  | 0.0        | 0.0  | 187.5       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1000.0        | 0.0      | 0.0  | 0.0       | 1000.0 | 0.0  | 0.0        | 0.0  | 1000.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |

  @TestRailId:C3965
  Scenario: Verify Progressive Loan reschedule by extending repayment period - multiple extra terms, flat interest type
    When Admin sets the business date to "01 June 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                            | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP1_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 June 2024      | 1500           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 June 2024" with "1500" amount and expected disbursement date on "01 June 2024"
    When Admin successfully disburse the loan on "01 June 2024" with "1500" EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |           | 1500.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 30   | 01 July 2024      |           | 1000.0          | 500.0         | 0.0      | 0.0  | 0.0       | 500.0 | 0.0  | 0.0        | 0.0  | 500.0       |
      | 2  | 31   | 01 August 2024    |           | 500.0           | 500.0         | 0.0      | 0.0  | 0.0       | 500.0 | 0.0  | 0.0        | 0.0  | 500.0       |
      | 3  | 31   | 01 September 2024 |           | 0.0             | 500.0         | 0.0      | 0.0  | 0.0       | 500.0 | 0.0  | 0.0        | 0.0  | 500.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1500.0        | 0.0      | 0.0  | 0.0       | 1500.0 | 0.0  | 0.0        | 0.0  | 1500.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1500.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1500.0       |
    When Admin sets the business date to "01 July 2024"
    When Admin creates and approves Loan reschedule with the following data:
      | rescheduleFromDate | submittedOnDate | adjustedDueDate | graceOnPrincipal | graceOnInterest | extraTerms | newInterestRate |
      | 01 July 2024       | 01 July 2024    |                 |                  |                 | 2          |                 |
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |           | 1500.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 30   | 01 July 2024      |           | 1200.0          | 300.0         | 0.0      | 0.0  | 0.0       | 300.0 | 0.0  | 0.0        | 0.0  | 300.0       |
      | 2  | 31   | 01 August 2024    |           | 900.0           | 300.0         | 0.0      | 0.0  | 0.0       | 300.0 | 0.0  | 0.0        | 0.0  | 300.0       |
      | 3  | 31   | 01 September 2024 |           | 600.0           | 300.0         | 0.0      | 0.0  | 0.0       | 300.0 | 0.0  | 0.0        | 0.0  | 300.0       |
      | 4  | 30   | 01 October 2024   |           | 300.0           | 300.0         | 0.0      | 0.0  | 0.0       | 300.0 | 0.0  | 0.0        | 0.0  | 300.0       |
      | 5  | 31   | 01 November 2024  |           | 0.0             | 300.0         | 0.0      | 0.0  | 0.0       | 300.0 | 0.0  | 0.0        | 0.0  | 300.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1500.0        | 0.0      | 0.0  | 0.0       | 1500.0 | 0.0  | 0.0        | 0.0  | 1500.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1500.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1500.0       |

  @TestRailId:C3966
  Scenario: Verify Progressive Loan reschedule by extending repayment period after partial repayment, flat interest type
    When Admin sets the business date to "01 June 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                            | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP1_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 June 2024      | 1200           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 June 2024" with "1200" amount and expected disbursement date on "01 June 2024"
    When Admin successfully disburse the loan on "01 June 2024" with "1200" EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |           | 1200.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 30   | 01 July 2024      |           | 800.0           | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 0.0  | 0.0        | 0.0  | 400.0       |
      | 2  | 31   | 01 August 2024    |           | 400.0           | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 0.0  | 0.0        | 0.0  | 400.0       |
      | 3  | 31   | 01 September 2024 |           | 0.0             | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 0.0  | 0.0        | 0.0  | 400.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1200.0        | 0.0      | 0.0  | 0.0       | 1200.0 | 0.0  | 0.0        | 0.0  | 1200.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1200.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1200.0       |
    When Admin sets the business date to "01 July 2024"
    And Customer makes "AUTOPAY" repayment on "01 July 2024" with 400 EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date    | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |              | 1200.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 30   | 01 July 2024      | 01 July 2024 | 800.0           | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 400.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 31   | 01 August 2024    |              | 400.0           | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 0.0   | 0.0        | 0.0  | 400.0       |
      | 3  | 31   | 01 September 2024 |              | 0.0             | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 0.0   | 0.0        | 0.0  | 400.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1200.0        | 0.0      | 0.0  | 0.0       | 1200.0 | 400.0 | 0.0        | 0.0  | 800.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1200.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1200.0       |
      | 01 July 2024     | Repayment        | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 800.0        |
    When Admin sets the business date to "15 July 2024"
    When Admin creates and approves Loan reschedule with the following data:
      | rescheduleFromDate | submittedOnDate | adjustedDueDate | graceOnPrincipal | graceOnInterest | extraTerms | newInterestRate |
      | 01 August 2024     | 15 July 2024    |                 |                  |                 | 1          |                 |
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date    | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |              | 1200.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 30   | 01 July 2024      | 01 July 2024 | 800.0           | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 400.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 31   | 01 August 2024    |              | 533.0           | 267.0         | 0.0      | 0.0  | 0.0       | 267.0 | 0.0   | 0.0        | 0.0  | 267.0       |
      | 3  | 31   | 01 September 2024 |              | 266.0           | 267.0         | 0.0      | 0.0  | 0.0       | 267.0 | 0.0   | 0.0        | 0.0  | 267.0       |
      | 4  | 30   | 01 October 2024   |              | 0.0             | 266.0         | 0.0      | 0.0  | 0.0       | 266.0 | 0.0   | 0.0        | 0.0  | 266.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1200.0        | 0.0      | 0.0  | 0.0       | 1200.0 | 400.0 | 0.0        | 0.0  | 800.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1200.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1200.0       |
      | 01 July 2024     | Repayment        | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 800.0        |

  @TestRailId:C3967
  Scenario: Verify Progressive Loan reschedule by extending repayment periods after partial repayment and then backdated repayment occurs, flat interest type
    When Admin sets the business date to "01 June 2024"
    When Admin creates a client with random data
    When Admin set "LP1_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL" loan product "DEFAULT" transaction type to "LAST_INSTALLMENT" future installment allocation rule
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                            | submitted on date | with Principal | ANNUAL interest rate % | interest type | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP1_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 June 2024      | 1200           | 0                      | FLAT          | SAME_AS_REPAYMENT_PERIOD    | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 June 2024" with "1200" amount and expected disbursement date on "01 June 2024"
    When Admin successfully disburse the loan on "01 June 2024" with "1200" EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |           | 1200.0          |               |          | 0.0  |           | 0.0   | 0.0  |            |      |             |
      | 1  | 30   | 01 July 2024      |           | 800.0           | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 0.0  | 0.0        | 0.0  | 400.0       |
      | 2  | 31   | 01 August 2024    |           | 400.0           | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 0.0  | 0.0        | 0.0  | 400.0       |
      | 3  | 31   | 01 September 2024 |           | 0.0             | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 0.0  | 0.0        | 0.0  | 400.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      | 1200.0        | 0.0      | 0.0  | 0.0       | 1200.0 | 0.0  | 0.0        | 0.0  | 1200.0      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1200.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1200.0       |
    When Admin sets the business date to "01 July 2024"
    And Customer makes "AUTOPAY" repayment on "01 July 2024" with 400 EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date    | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |              | 1200.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 30   | 01 July 2024      | 01 July 2024 | 800.0           | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 400.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 31   | 01 August 2024    |              | 400.0           | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 0.0   | 0.0        | 0.0  | 400.0       |
      | 3  | 31   | 01 September 2024 |              | 0.0             | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 0.0   | 0.0        | 0.0  | 400.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1200.0        | 0.0      | 0.0  | 0.0       | 1200.0 | 400.0 | 0.0        | 0.0  | 800.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1200.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1200.0       |
      | 01 July 2024     | Repayment        | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 800.0        |
    When Admin sets the business date to "15 July 2024"
    When Admin creates and approves Loan reschedule with the following data:
      | rescheduleFromDate | submittedOnDate | adjustedDueDate | graceOnPrincipal | graceOnInterest | extraTerms | newInterestRate |
      | 01 September 2024  | 15 July 2024    |                 |                  |                 | 2          |                 |
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date    | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |              | 1200.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 30   | 01 July 2024      | 01 July 2024 | 800.0           | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 400.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 31   | 01 August 2024    |              | 400.0           | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 0.0   | 0.0        | 0.0  | 400.0       |
      | 3  | 31   | 01 September 2024 |              | 267.0           | 133.0         | 0.0      | 0.0  | 0.0       | 133.0 | 0.0   | 0.0        | 0.0  | 133.0       |
      | 4  | 30   | 01 October 2024   |              | 134.0           | 133.0         | 0.0      | 0.0  | 0.0       | 133.0 | 0.0   | 0.0        | 0.0  | 133.0       |
      | 5  | 31   | 01 November 2024  |              | 0.0             | 134.0         | 0.0      | 0.0  | 0.0       | 134.0 | 0.0   | 0.0        | 0.0  | 134.0       |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1200.0        | 0.0      | 0.0  | 0.0       | 1200.0 | 400.0 | 0.0        | 0.0  | 800.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1200.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1200.0       |
      | 01 July 2024     | Repayment        | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 800.0        |
    When Admin sets the business date to "15 October 2024"
    And Customer makes "AUTOPAY" repayment on "01 August 2024" with 500 EUR transaction amount
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date      | Balance of loan | Principal due | Interest | Fees | Penalties | Due   | Paid  | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |                | 1200.0          |               |          | 0.0  |           | 0.0   | 0.0   |            |      |             |
      | 1  | 30   | 01 July 2024      | 01 July 2024   | 800.0           | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 400.0 | 0.0        | 0.0  | 0.0         |
      | 2  | 31   | 01 August 2024    | 01 August 2024 | 400.0           | 400.0         | 0.0      | 0.0  | 0.0       | 400.0 | 400.0 | 0.0        | 0.0  | 0.0         |
      | 3  | 31   | 01 September 2024 |                | 267.0           | 133.0         | 0.0      | 0.0  | 0.0       | 133.0 | 0.0   | 0.0        | 0.0  | 133.0       |
      | 4  | 30   | 01 October 2024   |                | 134.0           | 133.0         | 0.0      | 0.0  | 0.0       | 133.0 | 0.0   | 0.0        | 0.0  | 133.0       |
      | 5  | 31   | 01 November 2024  |                | 0.0             | 134.0         | 0.0      | 0.0  | 0.0       | 134.0 | 100.0 | 100.0      | 0.0  | 34.0        |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due    | Paid  | In advance | Late | Outstanding |
      | 1200.0        | 0.0      | 0.0  | 0.0       | 1200.0 | 900.0 | 100.0      | 0.0  | 300.0       |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1200.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1200.0       |
      | 01 July 2024     | Repayment        | 400.0  | 400.0     | 0.0      | 0.0  | 0.0       | 800.0        |
      | 01 August 2024   | Repayment        | 500.0  | 500.0     | 0.0      | 0.0  | 0.0       | 300.0        |
    When Admin set "LP1_ADV_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL" loan product "DEFAULT" transaction type to "NEXT_INSTALLMENT" future installment allocation rule

  @TestRailId:C3987
  Scenario: Verify Progressive Loan reschedule by extending repayment period: Basic scenario without downpayment, declining balance interest type
    When Admin sets the business date to "01 June 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                   | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_CUSTOM_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 June 2024      | 1000           | 12                     | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 June 2024" with "1000" amount and expected disbursement date on "01 June 2024"
    When Admin successfully disburse the loan on "01 June 2024" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |           | 1000.0          |               |          | 0.0  |           | 0.0    | 0.0  |            |      |             |
      | 1  | 30   | 01 July 2024      |           | 669.98          | 330.02        | 10.0     | 0.0  | 0.0       | 340.02 | 0.0  | 0.0        | 0.0  | 340.02      |
      | 2  | 31   | 01 August 2024    |           | 336.66          | 333.32        | 6.7      | 0.0  | 0.0       | 340.02 | 0.0  | 0.0        | 0.0  | 340.02      |
      | 3  | 31   | 01 September 2024 |           | 0.0             | 336.66        | 3.37     | 0.0  | 0.0       | 340.03 | 0.0  | 0.0        | 0.0  | 340.03      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid | In advance | Late | Outstanding |
      | 1000.0        | 20.07    | 0.0  | 0.0       | 1020.07 | 0.0  | 0.0        | 0.0  | 1020.07     |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    When Admin sets the business date to "01 July 2024"
    When Admin creates and approves Loan reschedule with the following data:
      | rescheduleFromDate | submittedOnDate | adjustedDueDate | graceOnPrincipal | graceOnInterest | extraTerms | newInterestRate |
      | 01 August 2024     | 01 July 2024    |                 |                  |                 | 1          |                 |
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |           | 1000.0          |               |          | 0.0  |           | 0.0    | 0.0  |            |      |             |
      | 1  | 30   | 01 July 2024      |           | 669.98          | 330.02        | 10.0     | 0.0  | 0.0       | 340.02 | 0.0  | 0.0        | 0.0  | 340.02      |
      | 2  | 31   | 01 August 2024    |           | 448.87          | 221.11        | 6.7      | 0.0  | 0.0       | 227.81 | 0.0  | 0.0        | 0.0  | 227.81      |
      | 3  | 31   | 01 September 2024 |           | 225.55          | 223.32        | 4.49     | 0.0  | 0.0       | 227.81 | 0.0  | 0.0        | 0.0  | 227.81      |
      | 4  | 30   | 01 October 2024   |           | 0.0             | 225.55        | 2.26     | 0.0  | 0.0       | 227.81 | 0.0  | 0.0        | 0.0  | 227.81      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid | In advance | Late | Outstanding |
      | 1000.0        | 23.45    | 0.0  | 0.0       | 1023.45 | 0.0  | 0.0        | 0.0  | 1023.45     |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |

  @TestRailId:C3989
  Scenario: Verify Progressive Loan reschedule by extending repayment period with downpayment installment, declining balance interest type
    When Admin sets the business date to "01 June 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct              | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy                        |
      | LP2_DOWNPAYMENT_INTEREST | 01 June 2024      | 1000           | 12                     | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | PENALTIES_FEES_INTEREST_PRINCIPAL_ORDER |
    And Admin successfully approves the loan on "01 June 2024" with "1000" amount and expected disbursement date on "01 June 2024"
    When Admin successfully disburse the loan on "01 June 2024" with "1000" EUR transaction amount
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |           | 1000.0          |               |          | 0.0  |           | 0.0    | 0.0  |            |      |             |
      | 1  | 0    | 01 June 2024      |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0  | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 30   | 01 July 2024      |           | 502.4           | 247.6         | 7.4      | 0.0  | 0.0       | 255.0  | 0.0  | 0.0        | 0.0  | 255.0       |
      | 3  | 31   | 01 August 2024    |           | 252.52          | 249.88        | 5.12     | 0.0  | 0.0       | 255.0  | 0.0  | 0.0        | 0.0  | 255.0       |
      | 4  | 31   | 01 September 2024 |           | 0.0             | 252.52        | 2.57     | 0.0  | 0.0       | 255.09 | 0.0  | 0.0        | 0.0  | 255.09      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid | In advance | Late | Outstanding |
      | 1000.0        | 15.09    | 0.0  | 0.0       | 1015.09 | 0.0  | 0.0        | 0.0  | 1015.09     |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |
    When Admin sets the business date to "01 July 2024"
    When Admin creates and approves Loan reschedule with the following data:
      | rescheduleFromDate | submittedOnDate | adjustedDueDate | graceOnPrincipal | graceOnInterest | extraTerms | newInterestRate |
      | 01 July 2024       | 01 July 2024    |                 |                  |                 | 1          |                 |
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |           | 1000.0          |               |          | 0.0  |           | 0.0    | 0.0  |            |      |             |
      | 1  | 0    | 01 June 2024      |           | 750.0           | 250.0         | 0.0      | 0.0  | 0.0       | 250.0  | 0.0  | 0.0        | 0.0  | 250.0       |
      | 2  | 30   | 01 July 2024      |           | 629.4           | 120.6         | 7.4      | 0.0  | 0.0       | 128.0  | 0.0  | 0.0        | 0.0  | 128.0       |
      | 3  | 31   | 01 August 2024    |           | 507.81          | 121.59        | 6.41     | 0.0  | 0.0       | 128.0  | 0.0  | 0.0        | 0.0  | 128.0       |
      | 4  | 31   | 01 September 2024 |           | 384.99          | 122.82        | 5.18     | 0.0  | 0.0       | 128.0  | 0.0  | 0.0        | 0.0  | 128.0       |
      | 5  | 30   | 01 October 2024   |           | 0.0             | 384.99        | 3.8      | 0.0  | 0.0       | 388.79 | 0.0  | 0.0        | 0.0  | 388.79      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid | In advance | Late | Outstanding |
      | 1000.0        | 22.79    | 0.0  | 0.0       | 1022.79 | 0.0  | 0.0        | 0.0  | 1022.79     |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1000.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1000.0       |

  @TestRailId:C3990
  Scenario: Verify Progressive Loan reschedule by extending repayment period - multiple extra terms, declining balance interest type
    When Admin sets the business date to "01 June 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                   | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_CUSTOM_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 June 2024      | 1500           | 12                     | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 June 2024" with "1500" amount and expected disbursement date on "01 June 2024"
    When Admin successfully disburse the loan on "01 June 2024" with "1500" EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |           | 1500.0          |               |          | 0.0  |           | 0.0    | 0.0  |            |      |             |
      | 1  | 30   | 01 July 2024      |           | 1004.97         | 495.03        | 15.0     | 0.0  | 0.0       | 510.03 | 0.0  | 0.0        | 0.0  | 510.03      |
      | 2  | 31   | 01 August 2024    |           | 504.99          | 499.98        | 10.05    | 0.0  | 0.0       | 510.03 | 0.0  | 0.0        | 0.0  | 510.03      |
      | 3  | 31   | 01 September 2024 |           | 0.0             | 504.99        | 5.05     | 0.0  | 0.0       | 510.04 | 0.0  | 0.0        | 0.0  | 510.04      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid | In advance | Late | Outstanding |
      | 1500.0        | 30.1     | 0.0  | 0.0       | 1530.1  | 0.0  | 0.0        | 0.0  | 1530.1      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1500.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1500.0       |
    When Admin sets the business date to "01 July 2024"
    When Admin creates and approves Loan reschedule with the following data:
      | rescheduleFromDate | submittedOnDate | adjustedDueDate | graceOnPrincipal | graceOnInterest | extraTerms | newInterestRate |
      | 01 July 2024       | 01 July 2024    |                 |                  |                 | 2          |                 |
    Then Loan Repayment schedule has 5 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |           | 1500.0          |               |          | 0.0  |           | 0.0    | 0.0  |            |      |             |
      | 1  | 30   | 01 July 2024      |           | 1205.94         | 294.06        | 15.0     | 0.0  | 0.0       | 309.06 | 0.0  | 0.0        | 0.0  | 309.06      |
      | 2  | 31   | 01 August 2024    |           | 908.94          | 297.0         | 12.06    | 0.0  | 0.0       | 309.06 | 0.0  | 0.0        | 0.0  | 309.06      |
      | 3  | 31   | 01 September 2024 |           | 608.97          | 299.97        | 9.09     | 0.0  | 0.0       | 309.06 | 0.0  | 0.0        | 0.0  | 309.06      |
      | 4  | 30   | 01 October 2024   |           | 306.0           | 302.97        | 6.09     | 0.0  | 0.0       | 309.06 | 0.0  | 0.0        | 0.0  | 309.06      |
      | 5  | 31   | 01 November 2024  |           | 0.0             | 306.0         | 3.06     | 0.0  | 0.0       | 309.06 | 0.0  | 0.0        | 0.0  | 309.06      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid | In advance | Late | Outstanding |
      | 1500.0        | 45.3     | 0.0  | 0.0       | 1545.3  | 0.0  | 0.0        | 0.0  | 1545.3      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1500.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1500.0       |

  @TestRailId:C3994
  Scenario: Verify Progressive Loan reschedule by extending repayment period after partial repayment, declining balance interest type
    When Admin sets the business date to "01 June 2024"
    When Admin creates a client with random data
    When Admin creates a fully customized loan with the following data:
      | LoanProduct                                                   | submitted on date | with Principal | ANNUAL interest rate % | interest type     | interest calculation period | amortization type  | loanTermFrequency | loanTermFrequencyType | repaymentEvery | repaymentFrequencyType | numberOfRepayments | graceOnPrincipalPayment | graceOnInterestPayment | interest free period | Payment strategy            |
      | LP2_ADV_CUSTOM_PMT_ALLOC_PROGRESSIVE_LOAN_SCHEDULE_HORIZONTAL | 01 June 2024      | 1200           | 12                     | DECLINING_BALANCE | DAILY                       | EQUAL_INSTALLMENTS | 3                 | MONTHS                | 1              | MONTHS                 | 3                  | 0                       | 0                      | 0                    | ADVANCED_PAYMENT_ALLOCATION |
    And Admin successfully approves the loan on "01 June 2024" with "1200" amount and expected disbursement date on "01 June 2024"
    When Admin successfully disburse the loan on "01 June 2024" with "1200" EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |           | 1200.0          |               |          | 0.0  |           | 0.0    | 0.0  |            |      |             |
      | 1  | 30   | 01 July 2024      |           | 803.97          | 396.03        | 12.0     | 0.0  | 0.0       | 408.03 | 0.0  | 0.0        | 0.0  | 408.03      |
      | 2  | 31   | 01 August 2024    |           | 403.98          | 399.99        | 8.04     | 0.0  | 0.0       | 408.03 | 0.0  | 0.0        | 0.0  | 408.03      |
      | 3  | 31   | 01 September 2024 |           | 0.0             | 403.98        | 4.04     | 0.0  | 0.0       | 408.02 | 0.0  | 0.0        | 0.0  | 408.02      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid | In advance | Late | Outstanding |
      | 1200.0        | 24.08    | 0.0  | 0.0       | 1224.08 | 0.0  | 0.0        | 0.0  | 1224.08     |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1200.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1200.0       |
    When Admin sets the business date to "01 July 2024"
    And Customer makes "AUTOPAY" repayment on "01 July 2024" with 417.03 EUR transaction amount
    Then Loan Repayment schedule has 3 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date    | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |              | 1200.0          |               |          | 0.0  |           | 0.0    | 0.0    |            |      |             |
      | 1  | 30   | 01 July 2024      | 01 July 2024 | 803.97          | 396.03        | 12.0     | 0.0  | 0.0       | 408.03 | 408.03 | 0.0        | 0.0  | 0.0         |
      | 2  | 31   | 01 August 2024    |              | 403.89          | 400.08        | 7.95     | 0.0  | 0.0       | 408.03 | 9.0    | 9.0        | 0.0  | 399.03      |
      | 3  | 31   | 01 September 2024 |              | 0.0             | 403.89        | 4.04     | 0.0  | 0.0       | 407.93 | 0.0    | 0.0        | 0.0  | 407.93      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid   | In advance | Late | Outstanding |
      | 1200.0        | 23.99    | 0.0  | 0.0       | 1223.99 | 417.03 | 9.0        | 0.0  | 806.96      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1200.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1200.0       |
      | 01 July 2024     | Repayment        | 417.03 | 405.03    | 12.0     | 0.0  | 0.0       | 794.97       |
    When Admin sets the business date to "15 July 2024"
    When Admin creates and approves Loan reschedule with the following data:
      | rescheduleFromDate | submittedOnDate | adjustedDueDate | graceOnPrincipal | graceOnInterest | extraTerms | newInterestRate |
      | 01 August 2024     | 15 July 2024    |                 |                  |                 | 1          |                 |
    Then Loan Repayment schedule has 4 periods, with the following data for periods:
      | Nr | Days | Date              | Paid date    | Balance of loan | Principal due | Interest | Fees | Penalties | Due    | Paid   | In advance | Late | Outstanding |
      |    |      | 01 June 2024      |              | 1200.0          |               |          | 0.0  |           | 0.0    | 0.0    |            |      |             |
      | 1  | 30   | 01 July 2024      | 01 July 2024 | 803.97          | 396.03        | 12.0     | 0.0  | 0.0       | 408.03 | 408.03 | 0.0        | 0.0  | 0.0         |
      | 2  | 31   | 01 August 2024    |              | 538.58          | 265.39        | 7.95     | 0.0  | 0.0       | 273.34 | 9.0    | 9.0        | 0.0  | 264.34      |
      | 3  | 31   | 01 September 2024 |              | 270.63          | 267.95        | 5.39     | 0.0  | 0.0       | 273.34 | 0.0    | 0.0        | 0.0  | 273.34      |
      | 4  | 30   | 01 October 2024   |              | 0.0             | 270.63        | 2.71     | 0.0  | 0.0       | 273.34 | 0.0    | 0.0        | 0.0  | 273.34      |
    Then Loan Repayment schedule has the following data in Total row:
      | Principal due | Interest | Fees | Penalties | Due     | Paid   | In advance | Late | Outstanding |
      | 1200.0        | 28.05    | 0.0  | 0.0       | 1228.05 | 417.03 | 9.0        | 0.0  | 811.02      |
    Then Loan Transactions tab has the following data:
      | Transaction date | Transaction Type | Amount | Principal | Interest | Fees | Penalties | Loan Balance |
      | 01 June 2024     | Disbursement     | 1200.0 | 0.0       | 0.0      | 0.0  | 0.0       | 1200.0       |
      | 01 July 2024     | Repayment        | 417.03 | 405.03    | 12.0     | 0.0  | 0.0       | 794.97       |
