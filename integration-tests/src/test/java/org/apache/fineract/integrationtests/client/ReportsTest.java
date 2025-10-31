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
package org.apache.fineract.integrationtests.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.apache.fineract.client.models.RunReportsResponse;
import org.apache.fineract.client.util.CallFailedRuntimeException;
import org.apache.fineract.integrationtests.common.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

/**
 * Integration Test for /runreports/ API.
 *
 * @author Michael Vorburger.ch
 */
public class ReportsTest extends IntegrationTest {

    @BeforeEach
    public void setup() {
        Utils.initializeRESTAssured();
    }

    @Test
    void listReports() {
        assertThat(ok(fineractClient().reports.retrieveReportList())).hasSize(128);
    }

    @Test
    void runClientListingTableReport() {
        assertThat(ok(fineractClient().reportsRun.runReportGetData("Client Listing", Map.of("R_officeId", "1"), false)).getColumnHeaders()
                .get(0).getColumnName()).isEqualTo("Office/Branch");
    }

    @Test
    void runClientListingTableReportCSV() throws IOException {
        Response<ResponseBody> result = okR(
                fineractClient().reportsRun.runReportGetFile("Client Listing", Map.of("R_officeId", "1", "exportCSV", "true"), false));
        assertThat(result.body().contentType()).isEqualTo(MediaType.parse("text/csv"));
        assertThat(result.body().string()).contains("Office/Branch");
    }

    @Test // see FINERACT-1306
    void runReportCategory() throws IOException {
        // Using raw OkHttp instead of Retrofit API here, because /runreports/reportCategoryList returns JSON Array -
        // but runReportGetData() expects columnHeaders/data JSON.
        var req = new Request.Builder().url(fineractClient().baseURL().resolve(
                "/fineract-provider/api/v1/runreports/reportCategoryList?R_reportCategory=Fund&genericResultSet=false&parameterType=true&tenantIdentifier=default"))
                .build();
        try (var response = fineractClient().okHttpClient().newCall(req).execute()) {
            assertThat(response.code()).isEqualTo(200);
        }
    }

    @Test
    void runExpectedPaymentsPentahoReportWithoutPlugin() {
        CallFailedRuntimeException exception = assertThrows(CallFailedRuntimeException.class,
                () -> ok(fineractClient().reportsRun.runReportGetFile("Expected Payments By Date - Formatted", Map.of("R_endDate",
                        "2013-04-30", "R_loanOfficerId", "-1", "R_officeId", "1", "R_startDate", "2013-04-16", "output-type", "PDF"),
                        false)));
        assertEquals(503, exception.getResponse().code());
    }

    @Test
    @Disabled
    void runExpectedPaymentsPentahoReport() {
        ResponseBody r = ok(fineractClient().reportsRun.runReportGetFile("Expected Payments By Date - Formatted", Map.of("R_endDate",
                "2013-04-30", "R_loanOfficerId", "-1", "R_officeId", "1", "R_startDate", "2013-04-16", "output-type", "PDF"), false));
        assertThat(r.contentType()).isEqualTo(MediaType.get("application/pdf"));
    }

    @Test
    void testTrialBalanceTableReportRunsSuccessfully() {
        Response<RunReportsResponse> response = okR(fineractClient().reportsRun.runReportGetData("Trial Balance Table",
                Map.of("R_endDate", "2013-04-30", "R_officeId", "1", "R_startDate", "2013-04-16"), false));
        assertEquals(200, response.code());
    }

    @Test
    void testIncomeStatementTableReportRunsSuccessfully() {
        Response<RunReportsResponse> response = okR(fineractClient().reportsRun.runReportGetData("Income Statement Table",
                Map.of("R_endDate", "2013-04-30", "R_officeId", "1", "R_startDate", "2013-04-16"), false));
        assertEquals(200, response.code());
    }

    @Test
    void testGeneralLedgerReportTableReportRunsSuccessfully() {
        Response<RunReportsResponse> response = okR(fineractClient().reportsRun.runReportGetData("GeneralLedgerReport Table",
                Map.of("R_endDate", "2013-04-30", "R_officeId", "1", "R_startDate", "2013-04-16", "R_GLAccountNO", "1"), false));
        assertEquals(200, response.code());
    }

    @Test
    void testBalanceSheetTableReportRunsSuccessfully() {
        Response<RunReportsResponse> response = okR(fineractClient().reportsRun.runReportGetData("Balance Sheet Table",
                Map.of("R_endDate", "2013-04-30", "R_officeId", "1"), false));
        assertEquals(200, response.code());
    }
}
