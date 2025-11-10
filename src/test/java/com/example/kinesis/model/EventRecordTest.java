package com.example.kinesis.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EventRecordTest {

    @Test
    void testEventRecordBuilder() {
        // Given
        String id = "test-123";
        String eventType = "TEST_EVENT";
        String data = "{\"message\":\"test\"}";
        Long timestamp = System.currentTimeMillis();
        String status = "PROCESSED";

        // When
        EventRecord record = EventRecord.builder()
                .id(id)
                .eventType(eventType)
                .data(data)
                .timestamp(timestamp)
                .status(status)
                .build();

        // Then
        assertNotNull(record);
        assertEquals(id, record.getId());
        assertEquals(eventType, record.getEventType());
        assertEquals(data, record.getData());
        assertEquals(timestamp, record.getTimestamp());
        assertEquals(status, record.getStatus());
    }

    @Test
    void testFromJsonWithValidJson() {
        // Given
        String json = "{\"id\":\"test-456\",\"eventType\":\"USER_ACTION\",\"message\":\"test message\"}";

        // When
        EventRecord record = EventRecord.fromJson(json);

        // Then
        assertNotNull(record);
        assertEquals("test-456", record.getId());
        assertEquals("USER_ACTION", record.getEventType());
        assertNotNull(record.getData());
        assertTrue(record.getData().contains("test message"));
        assertNotNull(record.getTimestamp());
        assertEquals("PROCESSED", record.getStatus());
    }

    @Test
    void testFromJsonWithoutId() {
        // Given
        String json = "{\"eventType\":\"NO_ID_EVENT\",\"message\":\"test\"}";

        // When
        EventRecord record = EventRecord.fromJson(json);

        // Then
        assertNotNull(record);
        assertNotNull(record.getId()); // Should generate UUID
        assertEquals("NO_ID_EVENT", record.getEventType());
        assertEquals("PROCESSED", record.getStatus());
    }

    @Test
    void testFromJsonWithoutEventType() {
        // Given
        String json = "{\"id\":\"test-789\",\"message\":\"test\"}";

        // When
        EventRecord record = EventRecord.fromJson(json);

        // Then
        assertNotNull(record);
        assertEquals("test-789", record.getId());
        assertEquals("KINESIS_EVENT", record.getEventType()); // Default value
        assertEquals("PROCESSED", record.getStatus());
    }

    @Test
    void testFromJsonWithInvalidJson() {
        // Given
        String invalidJson = "not a valid json";

        // When
        EventRecord record = EventRecord.fromJson(invalidJson);

        // Then
        assertNotNull(record);
        assertNotNull(record.getId()); // Should generate UUID
        assertEquals("KINESIS_EVENT", record.getEventType());
        assertEquals(invalidJson, record.getData());
        assertEquals("PROCESSED", record.getStatus());
    }

    @Test
    void testFromJsonWithEmptyJson() {
        // Given
        String emptyJson = "{}";

        // When
        EventRecord record = EventRecord.fromJson(emptyJson);

        // Then
        assertNotNull(record);
        assertNotNull(record.getId());
        assertEquals("KINESIS_EVENT", record.getEventType());
        assertEquals("PROCESSED", record.getStatus());
    }

    @Test
    void testEventRecordEquality() {
        // Given
        Long timestamp = System.currentTimeMillis();
        EventRecord record1 = EventRecord.builder()
                .id("test-123")
                .eventType("TEST")
                .data("data")
                .timestamp(timestamp)
                .status("PROCESSED")
                .build();

        EventRecord record2 = EventRecord.builder()
                .id("test-123")
                .eventType("TEST")
                .data("data")
                .timestamp(timestamp)
                .status("PROCESSED")
                .build();

        // Then
        assertEquals(record1, record2);
        assertEquals(record1.hashCode(), record2.hashCode());
    }

    @Test
    void testEventRecordToString() {
        // Given
        EventRecord record = EventRecord.builder()
                .id("test-123")
                .eventType("TEST")
                .data("test data")
                .timestamp(123456789L)
                .status("PROCESSED")
                .build();

        // When
        String toString = record.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("test-123"));
        assertTrue(toString.contains("TEST"));
        assertTrue(toString.contains("PROCESSED"));
    }
}
