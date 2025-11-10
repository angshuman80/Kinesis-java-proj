package com.example.kinesis;

import com.example.kinesis.processor.RecordProcessorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.common.ConfigsBuilder;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.retrieval.polling.PollingConfig;

import java.util.UUID;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class KinesisDynamoDbApplication implements CommandLineRunner {

    private final RecordProcessorFactory recordProcessorFactory;
    private final KinesisAsyncClient kinesisAsyncClient;
    private final DynamoDbAsyncClient dynamoDbAsyncClient;
    private final CloudWatchAsyncClient cloudWatchAsyncClient;
    private final Region region;

    @Value("${aws.kinesis.stream-name}")
    private String streamName;

    @Value("${aws.kinesis.application-name}")
    private String applicationName;

    public static void main(String[] args) {
        SpringApplication.run(KinesisDynamoDbApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting Kinesis Consumer Application");
        log.info("Stream Name: {}", streamName);
        log.info("Application Name: {}", applicationName);
        log.debug("Starting new Kinesis consumer");

        ConfigsBuilder configsBuilder = new ConfigsBuilder(
                streamName,
                applicationName,
                kinesisAsyncClient,
                dynamoDbAsyncClient,
                cloudWatchAsyncClient,
                UUID.randomUUID().toString(),
                recordProcessorFactory
        );

        Scheduler scheduler = new Scheduler(
                configsBuilder.checkpointConfig(),
                configsBuilder.coordinatorConfig(),
                configsBuilder.leaseManagementConfig(),
                configsBuilder.lifecycleConfig(),
                configsBuilder.metricsConfig(),
                configsBuilder.processorConfig(),
                configsBuilder.retrievalConfig().retrievalSpecificConfig(
                        new PollingConfig(streamName, kinesisAsyncClient)
                )
        );

        Thread schedulerThread = new Thread(scheduler);
        schedulerThread.setDaemon(true);
        schedulerThread.start();

        log.info("Kinesis Consumer started successfully");

        // Keep the application running
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down Kinesis Consumer");
            scheduler.shutdown();
        }));
    }
}
