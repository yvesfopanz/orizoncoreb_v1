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
package org.apache.fineract.infrastructure.event.external.jobs;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.fineract.avro.MessageV1;
import org.apache.fineract.infrastructure.businessdate.domain.BusinessDateType;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.config.FineractProperties;
import org.apache.fineract.infrastructure.core.domain.ActionContext;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.event.external.exception.AcknowledgementTimeoutException;
import org.apache.fineract.infrastructure.event.external.producer.ExternalEventProducer;
import org.apache.fineract.infrastructure.event.external.repository.ExternalEventRepository;
import org.apache.fineract.infrastructure.event.external.repository.domain.ExternalEventView;
import org.apache.fineract.infrastructure.event.external.service.message.MessageFactory;
import org.apache.fineract.infrastructure.event.external.service.support.ByteBufferConverter;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SendAsynchronousEventsTaskletTest {

    @Mock
    private FineractProperties fineractProperties;
    @Mock
    private ExternalEventRepository repository;
    @Mock
    private ExternalEventProducer eventProducer;
    @Mock
    private MessageFactory messageFactory;
    @Mock
    private StepContribution stepContribution;
    @Mock
    private ChunkContext chunkContext;
    @Mock
    private ByteBufferConverter byteBufferConverter;
    @Mock
    private ConfigurationDomainService configurationDomainService;
    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private TransactionStatus transactionStatus;
    private SendAsynchronousEventsTasklet underTest;
    private RepeatStatus resultStatus;

    private Random rnd = new Random();

    @BeforeEach
    public void setUp() {
        ThreadLocalContextUtil.setTenant(new FineractPlatformTenant(1L, "default", "Default", "Asia/Kolkata", null));
        ThreadLocalContextUtil.setActionContext(ActionContext.DEFAULT);
        ThreadLocalContextUtil
                .setBusinessDates(new HashMap<>(Map.of(BusinessDateType.BUSINESS_DATE, LocalDate.now(ZoneId.systemDefault()))));
        configureExternalEventsProducerReadBatchSizeProperty();
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(2);
        taskExecutor.initialize();
        when(transactionTemplate.execute(ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.<TransactionCallback<Boolean>>getArgument(0).doInTransaction(transactionStatus));
        underTest = new SendAsynchronousEventsTasklet(fineractProperties, repository, eventProducer, messageFactory, byteBufferConverter,
                configurationDomainService, transactionTemplate, taskExecutor);
    }

    @AfterEach
    public void tearDown() {
        ThreadLocalContextUtil.reset();
    }

    private void configureExternalEventsProducerReadBatchSizeProperty() {
        FineractProperties.FineractEventsProperties eventsProperties = new FineractProperties.FineractEventsProperties();
        FineractProperties.FineractExternalEventsProperties externalProperties = new FineractProperties.FineractExternalEventsProperties();
        FineractProperties.FineractExternalEventsProducerProperties externalEventsProducerProperties = new FineractProperties.FineractExternalEventsProducerProperties();
        FineractProperties.FineractExternalEventsProducerJmsProperties externalEventsProducerJMSProperties = new FineractProperties.FineractExternalEventsProducerJmsProperties();
        externalEventsProducerJMSProperties.setEnabled(true);
        externalProperties.setEnabled(true);
        externalProperties.setPartitionSize(5000);
        externalEventsProducerProperties.setJms(externalEventsProducerJMSProperties);
        externalProperties.setProducer(externalEventsProducerProperties);
        externalProperties.setThreadPoolCorePoolSize(1);
        externalProperties.setThreadPoolMaxPoolSize(1);
        externalProperties.setThreadPoolQueueCapacity(10);
        eventsProperties.setExternal(externalProperties);
        when(fineractProperties.getEvents()).thenReturn(eventsProperties);
        when(configurationDomainService.retrieveExternalEventBatchSize()).thenReturn(10L);
    }

    @Test
    public void givenBatchSize2WhenTaskExecutionThenSend2Events() throws Exception {
        // given
        List<ExternalEventView> events = Arrays.asList(
                createExternalEventView("aType", "aCategory", "aSchema", new byte[0], "aIdempotencyKey", 1L),
                createExternalEventView("aType", "aCategory", "aSchema", new byte[0], "aIdempotencyKey", 1L));
        // Dummy Message
        MessageV1 dummyMessage = new MessageV1(1L, "aSource", "aType", "nocategory", "aCreateDate", "aBusinessDate", "aTenantId",
                "anidempotencyKey", "aSchema", ByteBuffer.wrap("dummy".getBytes(StandardCharsets.UTF_8)));

        when(repository.findByStatusOrderByBusinessDateAscIdAsc(Mockito.any(), Mockito.any())).thenReturn(events);
        when(messageFactory.createMessage(Mockito.any())).thenReturn(dummyMessage);
        when(byteBufferConverter.convert(Mockito.any(ByteBuffer.class))).thenReturn(new byte[0]);
        // when
        resultStatus = underTest.execute(stepContribution, chunkContext);
        // then
        verify(eventProducer).sendEvents(Mockito.any());
        Awaitility.await().atMost(10L, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(repository).markEventsSent(Mockito.eq(events.stream().map(ExternalEventView::getId).toList()), Mockito.any());
        });
        assertEquals(RepeatStatus.FINISHED, resultStatus);
    }

    @Test
    public void givenBatchSize2WhenEventSendFailsThenExecutionStops() throws Exception {
        // given
        List<ExternalEventView> events = Arrays.asList(
                createExternalEventView("aType", "aCategory", "aSchema", new byte[0], "aIdempotencyKey", 1L),
                createExternalEventView("aType", "aCategory", "aSchema", new byte[0], "aIdempotencyKey", 1L));
        MessageV1 dummyMessage = new MessageV1(1L, "aSource", "aType", "nocategory", "aCreateDate", "aBusinessDate", "aTenantId",
                "anidempotencyKey", "aSchema", ByteBuffer.wrap("dummy".getBytes(StandardCharsets.UTF_8)));
        when(repository.findByStatusOrderByBusinessDateAscIdAsc(Mockito.any(), Mockito.any())).thenReturn(events);
        when(messageFactory.createMessage(Mockito.any())).thenReturn(dummyMessage);
        when(byteBufferConverter.convert(Mockito.any(ByteBuffer.class))).thenReturn(new byte[0]);
        doThrow(new AcknowledgementTimeoutException("Event Send Exception", new RuntimeException())).when(eventProducer)
                .sendEvents(Mockito.any());
        // when
        resultStatus = underTest.execute(stepContribution, chunkContext);
        // then
        verify(repository, times(0)).markEventsSent(Mockito.any(), Mockito.any());
        assertEquals(RepeatStatus.FINISHED, resultStatus);
    }

    @Test
    public void givenOneEventWhenEventSentThenEventStatusUpdates() throws Exception {
        // given
        List<ExternalEventView> events = Arrays
                .asList(createExternalEventView("aType", "aCategory", "aSchema", new byte[0], "aIdempotencyKey", 1L));
        MessageV1 dummyMessage = new MessageV1(1L, "aSource", "aType", "nocategory", "aCreateDate", "aBusinessDate", "aTenantId",
                "anidempotencyKey", "aSchema", ByteBuffer.wrap("dummy".getBytes(StandardCharsets.UTF_8)));
        when(repository.findByStatusOrderByBusinessDateAscIdAsc(Mockito.any(), Mockito.any())).thenReturn(events);
        when(messageFactory.createMessage(Mockito.any())).thenReturn(dummyMessage);
        when(byteBufferConverter.convert(Mockito.any(ByteBuffer.class))).thenReturn(new byte[0]);
        // when
        resultStatus = underTest.execute(stepContribution, chunkContext);
        // then
        verify(messageFactory).createMessage(Mockito.any());
        verify(eventProducer).sendEvents(Mockito.any());
        Awaitility.await().atMost(10L, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(repository).markEventsSent(Mockito.eq(events.stream().map(ExternalEventView::getId).toList()), Mockito.any());
        });
        assertEquals(RepeatStatus.FINISHED, resultStatus);
    }

    @Test
    public void testExecuteShouldHandleNullAggregateId() throws Exception {
        // given
        List<ExternalEventView> events = Arrays
                .asList(createExternalEventView("aType", "aCategory", "aSchema", new byte[0], "aIdempotencyKey", null));
        MessageV1 dummyMessage = new MessageV1(1L, "aSource", "aType", "nocategory", "aCreateDate", "aBusinessDate", "aTenantId",
                "anidempotencyKey", "aSchema", ByteBuffer.wrap("dummy".getBytes(StandardCharsets.UTF_8)));
        when(repository.findByStatusOrderByBusinessDateAscIdAsc(Mockito.any(), Mockito.any())).thenReturn(events);
        when(messageFactory.createMessage(Mockito.any())).thenReturn(dummyMessage);
        byte[] byteMsg = new byte[0];
        when(byteBufferConverter.convert(Mockito.any(ByteBuffer.class))).thenReturn(byteMsg);
        // when
        resultStatus = underTest.execute(stepContribution, chunkContext);
        // then
        verify(messageFactory).createMessage(Mockito.any());
        verify(eventProducer).sendEvents(Map.of(-1L, List.of(byteMsg)));
        Awaitility.await().atMost(10L, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(repository).markEventsSent(Mockito.eq(events.stream().map(ExternalEventView::getId).toList()), Mockito.any());
        });
        assertEquals(RepeatStatus.FINISHED, resultStatus);
    }

    @Test
    public void givenEventBatchSizeIsConfiguredAs10WhenTaskExecutionThenEventReadPageSizeIsCorrect() {
        ArgumentCaptor<Pageable> externalEventPageSizeArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        List<ExternalEventView> events = new ArrayList<>();
        when(repository.findByStatusOrderByBusinessDateAscIdAsc(Mockito.any(), Mockito.any())).thenReturn(events);
        // when
        resultStatus = underTest.execute(stepContribution, chunkContext);
        // then
        verify(repository).findByStatusOrderByBusinessDateAscIdAsc(Mockito.any(), externalEventPageSizeArgumentCaptor.capture());
        assertThat(externalEventPageSizeArgumentCaptor.getValue().getPageSize()).isEqualTo(10);
    }

    private ExternalEventView createExternalEventView(String type, String category, String schema, byte[] data, String idempotencyKey,
            Long aggregateRootId) {
        ExternalEventView result = Mockito.mock(ExternalEventView.class);
        Mockito.when(result.getId()).thenReturn(rnd.nextLong());
        Mockito.when(result.getType()).thenReturn(type);
        Mockito.when(result.getCategory()).thenReturn(category);
        Mockito.when(result.getSchema()).thenReturn(schema);
        Mockito.when(result.getData()).thenReturn(data);
        Mockito.when(result.getIdempotencyKey()).thenReturn(idempotencyKey);
        Mockito.when(result.getAggregateRootId()).thenReturn(aggregateRootId);
        return result;
    }
}
