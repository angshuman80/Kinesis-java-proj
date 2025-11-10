package com.example.kinesis.processor;

import com.example.kinesis.service.DynamoDbService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;

@Component
@RequiredArgsConstructor
public class RecordProcessorFactory implements ShardRecordProcessorFactory {

    private final DynamoDbService dynamoDbService;

    @Override
    public ShardRecordProcessor shardRecordProcessor() {
        return new RecordProcessor(dynamoDbService);
    }
}
