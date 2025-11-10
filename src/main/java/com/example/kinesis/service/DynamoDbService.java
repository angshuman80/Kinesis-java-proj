package com.example.kinesis.service;

import com.example.kinesis.model.EventRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Slf4j
@Service
public class DynamoDbService {

    private final DynamoDbTable<EventRecord> eventTable;

    public DynamoDbService(DynamoDbClient dynamoDbClient,
                          @Value("${aws.dynamodb.table-name}") String tableName) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        this.eventTable = enhancedClient.table(tableName, TableSchema.fromBean(EventRecord.class));
    }

    public void saveEvent(EventRecord event) {
        try {
            eventTable.putItem(event);
            log.info("Successfully saved event to DynamoDB: {}", event.getId());
        } catch (Exception e) {
            log.error("Error saving event to DynamoDB: {}", event.getId(), e);
            throw new RuntimeException("Failed to save event to DynamoDB", e);
        }
    }

    public void updateEvent(EventRecord event) {
        try {
            eventTable.updateItem(event);
            log.info("Successfully updated event in DynamoDB: {}", event.getId());
        } catch (Exception e) {
            log.error("Error updating event in DynamoDB: {}", event.getId(), e);
            throw new RuntimeException("Failed to update event in DynamoDB", e);
        }
    }

    public EventRecord getEvent(String id, Long timestamp) {
        try {
            EventRecord key = EventRecord.builder()
                    .id(id)
                    .timestamp(timestamp)
                    .build();
            return eventTable.getItem(key);
        } catch (Exception e) {
            log.error("Error retrieving event from DynamoDB: {}", id, e);
            throw new RuntimeException("Failed to retrieve event from DynamoDB", e);
        }
    }
}
