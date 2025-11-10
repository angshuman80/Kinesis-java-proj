package com.example.kinesis.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class EventRecord {

    private String id;
    private String eventType;
    private String data;
    private Long timestamp;
    private String status;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    @DynamoDbSortKey
    public Long getTimestamp() {
        return timestamp;
    }

    public static EventRecord fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(json);
            
            // Extract fields from JSON if they exist, otherwise use defaults
            String id = node.has("id") ? node.get("id").asText() : java.util.UUID.randomUUID().toString();
            String eventType = node.has("eventType") ? node.get("eventType").asText() : "KINESIS_EVENT";
            
            return EventRecord.builder()
                    .id(id)
                    .eventType(eventType)
                    .data(json)
                    .timestamp(Instant.now().toEpochMilli())
                    .status("PROCESSED")
                    .build();
        } catch (Exception e) {
            // If JSON parsing fails, create a basic record
            return EventRecord.builder()
                    .id(java.util.UUID.randomUUID().toString())
                    .eventType("KINESIS_EVENT")
                    .data(json)
                    .timestamp(Instant.now().toEpochMilli())
                    .status("PROCESSED")
                    .build();
        }
    }
}
