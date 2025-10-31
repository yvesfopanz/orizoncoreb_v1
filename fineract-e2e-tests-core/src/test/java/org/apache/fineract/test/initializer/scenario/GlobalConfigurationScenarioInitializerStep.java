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
package org.apache.fineract.test.initializer.scenario;

import static org.apache.fineract.test.initializer.global.GlobalConfigurationGlobalInitializerStep.CONFIG_KEY_ENABLE_ADDRESS;
import static org.apache.fineract.test.initializer.global.GlobalConfigurationGlobalInitializerStep.CONFIG_KEY_ENABLE_BUSINESS_DATE;
import static org.apache.fineract.test.initializer.global.GlobalConfigurationGlobalInitializerStep.CONFIG_KEY_ENABLE_RECALCULATE_COB_DATE;

import lombok.RequiredArgsConstructor;
import org.apache.fineract.test.helper.GlobalConfigurationHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GlobalConfigurationScenarioInitializerStep implements FineractScenarioInitializerStep {

    private final GlobalConfigurationHelper globalConfigurationHelper;

    @Override
    public void initializeForScenario() throws Exception {
        /**
         * Enable-address set to false
         */
        globalConfigurationHelper.disableGlobalConfiguration(CONFIG_KEY_ENABLE_ADDRESS, 0L);

        /**
         * Enable business date and COB date
         */
        globalConfigurationHelper.enableGlobalConfiguration(CONFIG_KEY_ENABLE_BUSINESS_DATE, 0L);
        globalConfigurationHelper.enableGlobalConfiguration(CONFIG_KEY_ENABLE_RECALCULATE_COB_DATE, 0L);
    }
}
