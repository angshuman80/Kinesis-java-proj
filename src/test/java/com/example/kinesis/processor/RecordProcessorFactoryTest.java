package com.example.kinesis.processor;

import com.example.kinesis.service.DynamoDbService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.kinesis.processor.ShardRecordProcessor;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecordProcessorFactoryTest {

    @Mock
    private DynamoDbService dynamoDbService;

    private RecordProcessorFactory factory;

    @BeforeEach
    void setUp() {
        factory = new RecordProcessorFactory(dynamoDbService);
    }

    @Test
    void testShardRecordProcessorCreation() {
        // When
        ShardRecordProcessor processor = factory.shardRecordProcessor();

        // Then
        assertNotNull(processor);
        assertTrue(processor instanceof RecordProcessor);
    }

    @Test
    void testMultipleProcessorCreation() {
        // When
        ShardRecordProcessor processor1 = factory.shardRecordProcessor();
        ShardRecordProcessor processor2 = factory.shardRecordProcessor();

        // Then
        assertNotNull(processor1);
        assertNotNull(processor2);
        assertNotSame(processor1, processor2); // Should create new instances
    }

    @Test
    void testFactoryWithNullService() {
        // Given
        RecordProcessorFactory nullFactory = new RecordProcessorFactory(null);

        // When - Create processor with null service
        ShardRecordProcessor processor = nullFactory.shardRecordProcessor();
        
        // Then - Processor is created but will fail when used
        assertNotNull(processor);
    }
}
