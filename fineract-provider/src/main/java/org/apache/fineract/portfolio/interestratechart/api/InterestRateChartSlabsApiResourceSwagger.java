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
package org.apache.fineract.portfolio.interestratechart.api;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by Chirag Gupta on 08/12/17.
 */
final class InterestRateChartSlabsApiResourceSwagger {

    private InterestRateChartSlabsApiResourceSwagger() {}

    @Schema(description = "PostInterestRateChartsChartIdChartSlabsRequest")
    public static final class PostInterestRateChartsChartIdChartSlabsRequest {

        private PostInterestRateChartsChartIdChartSlabsRequest() {}

        static final class PostInterestRateChartsChartIdChartSlabsIncentives {

            private PostInterestRateChartsChartIdChartSlabsIncentives() {}

            @Schema(example = "2")
            public Integer entityType;
            @Schema(example = "2")
            public Integer attributeName;
            @Schema(example = "2")
            public Integer conditionType;
            @Schema(example = "11")
            public String attributeValue;
            @Schema(example = "2")
            public Integer incentiveType;
            @Schema(example = "-1")
            public BigDecimal amount;
        }

        @Schema(example = "0")
        public Integer periodType;
        @Schema(example = "1")
        public Integer fromPeriod;
        @Schema(example = "180")
        public Integer toPeriod;
        @Schema(example = "5")
        public Double annualInterestRate;
        @Schema(example = "5% interest from 1 day till 180 days of deposit")
        public String description;
        @Schema(example = "en")
        public String locale;
        public Set<PostInterestRateChartsChartIdChartSlabsIncentives> incentives;
    }

    @Schema(description = "PostInterestRateChartsChartIdChartSlabsResponse")
    public static final class PostInterestRateChartsChartIdChartSlabsResponse {

        private PostInterestRateChartsChartIdChartSlabsResponse() {}

        @Schema(example = "1")
        public Long resourceId;
    }

    @Schema(description = "PutInterestRateChartsChartIdChartSlabsChartSlabIdRequest")
    public static final class PutInterestRateChartsChartIdChartSlabsChartSlabIdRequest {

        private PutInterestRateChartsChartIdChartSlabsChartSlabIdRequest() {}

        @Schema(example = "6")
        public Double annualInterestRate;
        @Schema(example = "Interest rate changed to 6%")
        public String description;
    }

    @Schema(description = "PutInterestRateChartsChartIdChartSlabsChartSlabIdResponse")
    public static final class PutInterestRateChartsChartIdChartSlabsChartSlabIdResponse {

        private PutInterestRateChartsChartIdChartSlabsChartSlabIdResponse() {}

        @Schema(example = "1")
        public Long resourceId;
        public PutInterestRateChartsChartIdChartSlabsChartSlabIdRequest changes;
    }

    @Schema(description = "DeleteInterestRateChartsChartIdChartSlabsResponse")
    public static final class DeleteInterestRateChartsChartIdChartSlabsResponse {

        private DeleteInterestRateChartsChartIdChartSlabsResponse() {}

        @Schema(example = "1")
        public Long resourceId;
    }
}
