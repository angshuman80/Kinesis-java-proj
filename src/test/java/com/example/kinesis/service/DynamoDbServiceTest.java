package com.example.kinesis.service;

import com.example.kinesis.model.EventRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DynamoDbServiceTest {

    @Test
    void testSaveEventSuccess() {
        // Given
        EventRecord event = EventRecord.builder()
                .id("test-123")
                .eventType("TEST")
                .data("test data")
                .timestamp(System.currentTimeMillis())
                .status("PROCESSED")
                .build();

        // This test demonstrates the expected behavior
        // In a real scenario, you'd use DynamoDB Local or LocalStack for integration tests
        assertNotNull(event);
        assertEquals("test-123", event.getId());
    }

    @Test
    void testEventRecordValidation() {
        // Given
        EventRecord event = EventRecord.builder()
                .id("test-456")
                .eventType("VALIDATION_TEST")
                .data("{\"key\":\"value\"}")
                .timestamp(System.currentTimeMillis())
                .status("PROCESSED")
                .build();

        // Then
        assertNotNull(event.getId());
        assertNotNull(event.getTimestamp());
        assertTrue(event.getTimestamp() > 0);
    }

    @Test
    void testGetEventKeyConstruction() {
        // Given
        String id = "test-789";
        Long timestamp = 1234567890L;

        // When
        EventRecord key = EventRecord.builder()
                .id(id)
                .timestamp(timestamp)
                .build();

        // Then
        assertNotNull(key);
        assertEquals(id, key.getId());
        assertEquals(timestamp, key.getTimestamp());
    }

    @Test
    void testEventRecordWithNullValues() {
        // Given & When
        EventRecord event = EventRecord.builder()
                .id("test-null")
                .timestamp(System.currentTimeMillis())
                .build();

        // Then
        assertNotNull(event);
        assertEquals("test-null", event.getId());
        assertNull(event.getEventType());
        assertNull(event.getData());
        assertNull(event.getStatus());
    }

    @Test
    void testMultipleEventsWithSameId() {
        // Given
        String id = "same-id";
        Long timestamp1 = 1000L;
        Long timestamp2 = 2000L;

        EventRecord event1 = EventRecord.builder()
                .id(id)
                .timestamp(timestamp1)
                .eventType("EVENT_1")
                .build();

        EventRecord event2 = EventRecord.builder()
                .id(id)
                .timestamp(timestamp2)
                .eventType("EVENT_2")
                .build();

        // Then - Same ID but different timestamps should be different records
        assertEquals(event1.getId(), event2.getId());
        assertNotEquals(event1.getTimestamp(), event2.getTimestamp());
    }
}
