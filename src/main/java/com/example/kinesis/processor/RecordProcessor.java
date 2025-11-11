package com.example.kinesis.processor;

import com.example.kinesis.model.EventRecord;
import com.example.kinesis.service.DynamoDbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.kinesis.exceptions.InvalidStateException;
import software.amazon.kinesis.exceptions.ShutdownException;
import software.amazon.kinesis.lifecycle.events.*;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class RecordProcessor implements ShardRecordProcessor {

    private final DynamoDbService dynamoDbService;
    private String shardId;

    @Override
    public void initialize(InitializationInput initializationInput) {
        shardId = initializationInput.shardId();
        log.info("Initializing record processor for shard: {}", shardId);
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        log.debug("Processing {} records from shard: {}", 
                processRecordsInput.records().size(), shardId);

        log.info("test");

        for (KinesisClientRecord record : processRecordsInput.records()) {
            try {
                processRecord(record);
            } catch (Exception e) {
                log.error("Error processing record: {}", record.sequenceNumber(), e);
                // Depending on your requirements, you might want to:
                // 1. Continue processing other records
                // 2. Send to DLQ (Dead Letter Queue)
                // 3. Retry logic
            }
        }

        try {
            processRecordsInput.checkpointer().checkpoint();
            log.debug("Checkpoint successful for shard: {}", shardId);
        } catch (Exception e) {
            log.error("Error checkpointing for shard: {}", shardId, e);
        }
    }

    private void processRecord(KinesisClientRecord record) {
        String data = StandardCharsets.UTF_8.decode(record.data()).toString();
        log.info("Processing record - Partition Key: {}, Sequence Number: {}, Data: {}", 
                record.partitionKey(), record.sequenceNumber(), data);

        // Parse the record and create EventRecord
        EventRecord eventRecord = EventRecord.fromJson(data);
        
        // Save to DynamoDB
        dynamoDbService.saveEvent(eventRecord);
        
        log.info("Successfully processed and saved record: {}", record.sequenceNumber());
    }

    @Override
    public void leaseLost(LeaseLostInput leaseLostInput) {
        log.warn("Lease lost for shard: {}", shardId);
    }

    @Override
    public void shardEnded(ShardEndedInput shardEndedInput) {
        log.info("Shard ended: {}", shardId);
        try {
            shardEndedInput.checkpointer().checkpoint();
        } catch (ShutdownException | InvalidStateException e) {
            log.error("Error checkpointing at shard end: {}", shardId, e);
        }
    }

    @Override
    public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
        log.info("Shutdown requested for shard: {}", shardId);
        try {
            shutdownRequestedInput.checkpointer().checkpoint();
        } catch (ShutdownException | InvalidStateException e) {
            log.error("Error checkpointing at shutdown: {}", shardId, e);
        }
    }
}
