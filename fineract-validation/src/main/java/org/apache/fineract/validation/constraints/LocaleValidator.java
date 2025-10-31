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
package org.apache.fineract.validation.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public class LocaleValidator implements ConstraintValidator<Locale, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return false; // empty string is invalid
        }

        // Normalize input to use BCP 47 format (e.g., "en-US")
        String languageTag = value.replace('_', '-');

        java.util.Locale inputLocale = java.util.Locale.forLanguageTag(languageTag);

        // If language is empty, it's not a valid locale
        if (inputLocale.getLanguage().isEmpty()) {
            return false;
        }

        // Check if it matches any available locale
        return Arrays.stream(java.util.Locale.getAvailableLocales())
                .anyMatch(available -> available.toLanguageTag().equalsIgnoreCase(inputLocale.toLanguageTag()));
    }
}
