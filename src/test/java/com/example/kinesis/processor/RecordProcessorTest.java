package com.example.kinesis.processor;

import com.example.kinesis.model.EventRecord;
import com.example.kinesis.service.DynamoDbService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.kinesis.lifecycle.events.*;
import software.amazon.kinesis.processor.RecordProcessorCheckpointer;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecordProcessorTest {

    @Mock
    private DynamoDbService dynamoDbService;

    @Mock
    private InitializationInput initializationInput;

    @Mock
    private ProcessRecordsInput processRecordsInput;

    @Mock
    private RecordProcessorCheckpointer checkpointer;

    @Mock
    private ShardEndedInput shardEndedInput;

    @Mock
    private ShutdownRequestedInput shutdownRequestedInput;

    @Mock
    private LeaseLostInput leaseLostInput;

    private RecordProcessor recordProcessor;

    @BeforeEach
    void setUp() {
        recordProcessor = new RecordProcessor(dynamoDbService);
    }

    @Test
    void testInitialize() {
        // Given
        String shardId = "shardId-000000000001";
        when(initializationInput.shardId()).thenReturn(shardId);

        // When
        recordProcessor.initialize(initializationInput);

        // Then
        verify(initializationInput).shardId();
    }

    @Test
    void testProcessRecordsWithValidData() throws Exception {
        // Given
        String jsonData = "{\"id\":\"test-123\",\"eventType\":\"TEST\",\"message\":\"Hello\"}";
        KinesisClientRecord record = createKinesisRecord(jsonData, "partition-key-1", "seq-001");
        List<KinesisClientRecord> records = Arrays.asList(record);

        when(processRecordsInput.records()).thenReturn(records);
        when(processRecordsInput.checkpointer()).thenReturn(checkpointer);

        // When
        recordProcessor.processRecords(processRecordsInput);

        // Then
        ArgumentCaptor<EventRecord> eventCaptor = ArgumentCaptor.forClass(EventRecord.class);
        verify(dynamoDbService).saveEvent(eventCaptor.capture());
        verify(checkpointer).checkpoint();

        EventRecord capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent);
        assertNotNull(capturedEvent.getId());
        assertNotNull(capturedEvent.getData());
        assertTrue(capturedEvent.getData().contains("test-123"));
    }

    @Test
    void testProcessRecordsWithMultipleRecords() throws Exception {
        // Given
        KinesisClientRecord record1 = createKinesisRecord("{\"id\":\"1\"}", "key-1", "seq-001");
        KinesisClientRecord record2 = createKinesisRecord("{\"id\":\"2\"}", "key-2", "seq-002");
        KinesisClientRecord record3 = createKinesisRecord("{\"id\":\"3\"}", "key-3", "seq-003");
        List<KinesisClientRecord> records = Arrays.asList(record1, record2, record3);

        when(processRecordsInput.records()).thenReturn(records);
        when(processRecordsInput.checkpointer()).thenReturn(checkpointer);

        // When
        recordProcessor.processRecords(processRecordsInput);

        // Then
        verify(dynamoDbService, times(3)).saveEvent(any(EventRecord.class));
        verify(checkpointer).checkpoint();
    }

    @Test
    void testProcessRecordsWithEmptyList() throws Exception {
        // Given
        List<KinesisClientRecord> emptyRecords = Arrays.asList();
        when(processRecordsInput.records()).thenReturn(emptyRecords);
        when(processRecordsInput.checkpointer()).thenReturn(checkpointer);

        // When
        recordProcessor.processRecords(processRecordsInput);

        // Then
        verify(dynamoDbService, never()).saveEvent(any(EventRecord.class));
        verify(checkpointer).checkpoint();
    }

    @Test
    void testProcessRecordsWithDynamoDbException() throws Exception {
        // Given
        KinesisClientRecord record = createKinesisRecord("{\"id\":\"test\"}", "key", "seq");
        List<KinesisClientRecord> records = Arrays.asList(record);

        when(processRecordsInput.records()).thenReturn(records);
        when(processRecordsInput.checkpointer()).thenReturn(checkpointer);
        doThrow(new RuntimeException("DynamoDB error")).when(dynamoDbService).saveEvent(any(EventRecord.class));

        // When
        recordProcessor.processRecords(processRecordsInput);

        // Then - Should still checkpoint even if processing fails
        verify(dynamoDbService).saveEvent(any(EventRecord.class));
        verify(checkpointer).checkpoint();
    }

    @Test
    void testProcessRecordsWithCheckpointException() throws Exception {
        // Given
        KinesisClientRecord record = createKinesisRecord("{\"id\":\"test\"}", "key", "seq");
        List<KinesisClientRecord> records = Arrays.asList(record);

        when(processRecordsInput.records()).thenReturn(records);
        when(processRecordsInput.checkpointer()).thenReturn(checkpointer);
        doThrow(new RuntimeException("Checkpoint error")).when(checkpointer).checkpoint();

        // When
        recordProcessor.processRecords(processRecordsInput);

        // Then - Should process records even if checkpoint fails
        verify(dynamoDbService).saveEvent(any(EventRecord.class));
        verify(checkpointer).checkpoint();
    }

    @Test
    void testShardEnded() throws Exception {
        // Given
        when(shardEndedInput.checkpointer()).thenReturn(checkpointer);

        // When
        recordProcessor.shardEnded(shardEndedInput);

        // Then
        verify(checkpointer).checkpoint();
    }

    @Test
    void testShutdownRequested() throws Exception {
        // Given
        when(shutdownRequestedInput.checkpointer()).thenReturn(checkpointer);

        // When
        recordProcessor.shutdownRequested(shutdownRequestedInput);

        // Then
        verify(checkpointer).checkpoint();
    }

    @Test
    void testLeaseLost() {
        // When
        recordProcessor.leaseLost(leaseLostInput);

        // Then - Should not throw exception
        verifyNoInteractions(dynamoDbService);
    }

    @Test
    void testProcessRecordsWithInvalidJson() throws Exception {
        // Given
        String invalidJson = "not a valid json {{{";
        KinesisClientRecord record = createKinesisRecord(invalidJson, "key", "seq");
        List<KinesisClientRecord> records = Arrays.asList(record);

        when(processRecordsInput.records()).thenReturn(records);
        when(processRecordsInput.checkpointer()).thenReturn(checkpointer);

        // When
        recordProcessor.processRecords(processRecordsInput);

        // Then - Should still save the record (EventRecord.fromJson handles invalid JSON)
        verify(dynamoDbService).saveEvent(any(EventRecord.class));
        verify(checkpointer).checkpoint();
    }

    // Helper method to create KinesisClientRecord
    private KinesisClientRecord createKinesisRecord(String data, String partitionKey, String sequenceNumber) {
        ByteBuffer buffer = ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8));
        
        return KinesisClientRecord.builder()
                .data(buffer)
                .partitionKey(partitionKey)
                .sequenceNumber(sequenceNumber)
                .approximateArrivalTimestamp(java.time.Instant.now())
                .build();
    }
}
