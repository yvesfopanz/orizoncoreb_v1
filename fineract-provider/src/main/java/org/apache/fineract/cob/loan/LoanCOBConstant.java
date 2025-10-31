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

import org.apache.fineract.cob.COBConstant;

public final class LoanCOBConstant extends COBConstant {

    public static final String JOB_NAME = "LOAN_COB";
    public static final String JOB_HUMAN_READABLE_NAME = "Loan COB";
    public static final String LOAN_COB_JOB_NAME = "LOAN_CLOSE_OF_BUSINESS";
    public static final String LOAN_COB_PARAMETER = "loanCobParameter";
    public static final String LOAN_COB_WORKER_STEP = "loanCOBWorkerStep";

    public static final String INLINE_LOAN_COB_JOB_NAME = "INLINE_LOAN_COB";
    public static final String LOAN_IDS_PARAMETER_NAME = "LoanIds";

    public static final String LOAN_COB_PARTITIONER_STEP = "Loan COB partition - Step";

    private LoanCOBConstant() {

    }
}
