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
package org.apache.fineract.integrationtests.useradministration.users;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import java.util.HashMap;
import java.util.List;
import org.apache.fineract.client.models.GetOfficesResponse;
import org.apache.fineract.client.models.PostUsersRequest;
import org.apache.fineract.client.models.PostUsersResponse;
import org.apache.fineract.client.util.JSON;
import org.apache.fineract.integrationtests.common.OfficeHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.useradministration.roles.RolesHelper;
import org.junit.jupiter.api.Assertions;

public final class UserHelper {

    private static final String CREATE_USER_URL = "/fineract-provider/api/v1/users?" + Utils.TENANT_IDENTIFIER;
    private static final String USER_URL = "/fineract-provider/api/v1/users";
    private static final Gson GSON = new JSON().getGson();
    private static final String REPAYMENT_LOAN_PERMISSION = "REPAYMENT_LOAN";
    private static final String READ_LOAN_PERMISSION = "READ_LOAN";

    public static final String SIMPLE_USER_NAME = Utils.uniqueRandomStringGenerator("NotificationUser", 4);
    public static final String SIMPLE_USER_PASSWORD = "QwE!5rTy#9uP0";
    private static boolean SIMPLE_USER_CREATED = false;

    private UserHelper() {}

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static Integer createUser(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, int roleId,
            int staffId) {
        return Utils.performServerPost(requestSpec, responseSpec, CREATE_USER_URL, getTestCreateUserAsJSON(roleId, staffId), "resourceId");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static Object createUser(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, int roleId,
            int staffId, String username, String attribute) {
        return Utils.performServerPost(requestSpec, responseSpec, CREATE_USER_URL, getTestCreateUserAsJSON(roleId, staffId, username),
                attribute);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static Object createUser(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, int roleId,
            int staffId, String username, String password, String attribute) {
        return Utils.performServerPost(requestSpec, responseSpec, CREATE_USER_URL,
                getTestCreateUserAsJSON(roleId, staffId, username, password), attribute);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static PostUsersResponse createUser(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            PostUsersRequest request) {
        String requestBody = GSON.toJson(request);
        String response = Utils.performServerPost(requestSpec, responseSpec, CREATE_USER_URL, requestBody);
        return GSON.fromJson(response, PostUsersResponse.class);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static JsonObject createUserWithJsonResponse(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            PostUsersRequest request) {
        String requestBody = GSON.toJson(request);
        String jsonResponse = Utils.performServerPost(requestSpec, responseSpec, CREATE_USER_URL, requestBody);
        return JsonParser.parseString(jsonResponse).getAsJsonObject();
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static Object createUserForSelfService(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            int roleId, int staffId, int clientId, String attribute) {
        return Utils.performServerPost(requestSpec, responseSpec, CREATE_USER_URL,
                getTestCreateUserAsJSONForSelfService(roleId, staffId, clientId), attribute);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static Integer getUserId(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, String userName) {
        String json = Utils.performServerGet(requestSpec, responseSpec, CREATE_USER_URL, null);
        Assertions.assertNotNull(json);
        List<HashMap<String, Object>> userList = JsonPath.from(json).getList("$");

        for (HashMap<String, Object> user : userList) {
            if (user.get("username").equals(userName)) {
                return (Integer) user.get("id");
            }
        }

        return null;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static String getTestCreateUserAsJSON(int roleId, int staffId) {
        return "{ \"username\": \"" + Utils.uniqueRandomStringGenerator("User_Name_", 3)
                + "\", \"firstname\": \"Test\", \"lastname\": \"User\", \"email\": \"whatever@mifos.org\","
                + " \"officeId\": \"1\", \"staffId\": " + "\"" + staffId + "\",\"roles\": [\"" + roleId
                + "\"], \"sendPasswordToEmail\": false}";
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private static String getTestCreateUserAsJSON(int roleId, int staffId, String username) {
        return "{ \"username\": \"" + username + "\", \"firstname\": \"Test\", \"lastname\": \"User\", \"email\": \"whatever@mifos.org\","
                + " \"officeId\": \"1\", \"staffId\": " + "\"" + staffId + "\",\"roles\": [\"" + roleId
                + "\"], \"sendPasswordToEmail\": false}";
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private static String getTestCreateUserAsJSON(int roleId, int staffId, String username, String password) {
        return "{ \"username\": \"" + username + "\", \"firstname\": \"Test\", \"lastname\": \"User\", \"email\": \"whatever@mifos.org\","
                + " \"officeId\": \"1\", \"staffId\": " + "\"" + staffId + "\",\"roles\": [\"" + roleId
                + "\"], \"sendPasswordToEmail\": false,     \"password\": \"" + password + "\"," + "    \"repeatPassword\": \"" + password
                + "\"}";
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private static String getTestUpdateUserAsJSON(String username) {
        return "{ \"username\": \"" + username + "\", \"firstname\": \"Test\", \"lastname\": \"User\", \"email\": \"whatever@mifos.org\","
                + " \"officeId\": \"1\"}";
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static String getTestCreateUserAsJSONForSelfService(int roleId, int staffId, int clientId) {
        return "{ \"username\": \"" + Utils.uniqueRandomStringGenerator("User_Name_", 3)
                + "\", \"firstname\": \"Test\", \"lastname\": \"User\", \"email\": \"whatever@mifos.org\","
                + " \"officeId\": \"1\", \"staffId\": " + "\"" + staffId + "\",\"roles\": [\"" + roleId
                + "\"], \"sendPasswordToEmail\": false," + "\"isSelfServiceUser\" : true," + "\"clients\" : [\"" + clientId + "\"]}";
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static Integer deleteUser(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer userId) {
        return Utils.performServerDelete(requestSpec, responseSpec, createRoleOperationURL(userId), "resourceId");
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static Object updateUser(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, int userId,
            String username, String attribute) {
        return Utils.performServerPut(requestSpec, responseSpec, createRoleOperationURL(userId), getTestUpdateUserAsJSON(username),
                attribute);
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private static String createRoleOperationURL(final Integer userId) {
        return USER_URL + "/" + userId + "?" + Utils.TENANT_IDENTIFIER;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static RequestSpecification getSimpleUserWithoutBypassPermission(final RequestSpecification requestSpec,
            final ResponseSpecification responseSpec) {
        String password = SIMPLE_USER_PASSWORD;
        if (!SIMPLE_USER_CREATED) {
            GetOfficesResponse headOffice = OfficeHelper.getHeadOffice(requestSpec, responseSpec);
            String simpleRoleId = createSimpleRole(requestSpec, responseSpec);
            PostUsersRequest createUserRequest = new PostUsersRequest().username(SIMPLE_USER_NAME)
                    .firstname(Utils.randomStringGenerator("NotificationFN", 4)).lastname(Utils.randomStringGenerator("NotificationLN", 4))
                    .email("whatever@mifos.org").password(password).repeatPassword(password).sendPasswordToEmail(false)
                    .roles(List.of(Long.valueOf(simpleRoleId))).officeId(headOffice.getId());

            PostUsersResponse userCreationResponse = UserHelper.createUser(requestSpec, responseSpec, createUserRequest);
            Assertions.assertNotNull(userCreationResponse.getResourceId());
            SIMPLE_USER_CREATED = true;
        }
        RequestSpecification responseRequestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        responseRequestSpec.header("Authorization",
                "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey(SIMPLE_USER_NAME, password));
        return responseRequestSpec;
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    public static PostUsersRequest buildUserRequest(ResponseSpecification responseSpec, RequestSpecification requestSpec, String password) {
        Integer roleId = RolesHelper.createRole(requestSpec, responseSpec);
        String uniqueUsername = Utils.uniqueRandomStringGenerator("TestUser", 4);
        GetOfficesResponse headOffice = OfficeHelper.getHeadOffice(requestSpec, responseSpec);

        return new PostUsersRequest().username(uniqueUsername).firstname("Test").lastname("User").email("testuser@example.com")
                .password(password).repeatPassword(password).sendPasswordToEmail(false).officeId(headOffice.getId())
                .roles(List.of(roleId.longValue()));
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private static String createSimpleRole(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        Integer roleId = RolesHelper.createRole(requestSpec, responseSpec);
        addRepaymentPermissionToRole(requestSpec, responseSpec, roleId);
        return roleId.toString();
    }

    // TODO: Rewrite to use fineract-client instead!
    // Example: org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper.disburseLoan(java.lang.Long,
    // org.apache.fineract.client.models.PostLoansLoanIdRequest)
    @Deprecated(forRemoval = true)
    private static void addRepaymentPermissionToRole(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            Integer roleId) {
        HashMap<String, Boolean> permissionMap = new HashMap<>();
        permissionMap.put(REPAYMENT_LOAN_PERMISSION, true);
        permissionMap.put(READ_LOAN_PERMISSION, true);
        permissionMap.put("READ_RESCHEDULELOAN", true);
        permissionMap.put("CREATE_RESCHEDULELOAN", true);
        permissionMap.put("REJECT_RESCHEDULELOAN", true);
        permissionMap.put("APPROVE_RESCHEDULELOAN", true);
        RolesHelper.addPermissionsToRole(requestSpec, responseSpec, roleId, permissionMap);
    }
}
