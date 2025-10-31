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
package org.apache.fineract.infrastructure.event.external.service.serialization.serializer.loan;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.avro.generic.GenericContainer;
import org.apache.fineract.avro.generator.ByteBufferSerializable;
import org.apache.fineract.avro.loan.v1.LoanProductDataV1;
import org.apache.fineract.infrastructure.event.business.domain.BusinessEvent;
import org.apache.fineract.infrastructure.event.business.domain.loan.product.LoanProductBusinessEvent;
import org.apache.fineract.infrastructure.event.external.service.serialization.mapper.loan.LoanProductDataMapper;
import org.apache.fineract.infrastructure.event.external.service.serialization.serializer.AbstractBusinessEventWithCustomDataSerializer;
import org.apache.fineract.infrastructure.event.external.service.serialization.serializer.ExternalEventCustomDataSerializer;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoanProductBusinessEventSerializer extends AbstractBusinessEventWithCustomDataSerializer<LoanProductBusinessEvent> {

    private final LoanProductReadPlatformService service;
    private final LoanProductDataMapper mapper;
    private final List<ExternalEventCustomDataSerializer<LoanProductBusinessEvent>> externalEventCustomDataSerializers;

    @Override
    public <T> boolean canSerialize(BusinessEvent<T> event) {
        return event instanceof LoanProductBusinessEvent;
    }

    @Override
    public <T> ByteBufferSerializable toAvroDTO(BusinessEvent<T> rawEvent) {
        LoanProductBusinessEvent event = (LoanProductBusinessEvent) rawEvent;
        LoanProductData data = service.retrieveLoanProduct(event.get().getId());
        final LoanProductDataV1 loanProductDataV1 = mapper.map(data);
        loanProductDataV1.setCustomData(collectCustomData(event));
        return loanProductDataV1;
    }

    @Override
    public Class<? extends GenericContainer> getSupportedSchema() {
        return LoanProductDataV1.class;
    }

    @Override
    protected List<ExternalEventCustomDataSerializer<LoanProductBusinessEvent>> getExternalEventCustomDataSerializers() {
        return externalEventCustomDataSerializers;
    }
}
