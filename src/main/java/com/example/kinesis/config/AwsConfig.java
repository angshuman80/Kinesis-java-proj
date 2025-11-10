package com.example.kinesis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClientBuilder;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClientBuilder;

import java.net.URI;

@Configuration
public class AwsConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.endpoint:#{null}}")
    private String awsEndpoint;

    @Bean
    public Region region() {
        return Region.of(awsRegion);
    }

    @Bean
    public DynamoDbClient dynamoDbClient(Region region) {
        DynamoDbClientBuilder builder = DynamoDbClient.builder()
                .region(region);
        
        if (awsEndpoint != null && !awsEndpoint.isEmpty()) {
            // LocalStack configuration
            builder.endpointOverride(URI.create(awsEndpoint))
                   .credentialsProvider(StaticCredentialsProvider.create(
                       AwsBasicCredentials.create("test", "test")));
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }
        
        return builder.build();
    }

    @Bean
    public DynamoDbAsyncClient dynamoDbAsyncClient(Region region) {
        DynamoDbAsyncClientBuilder builder = DynamoDbAsyncClient.builder()
                .region(region);
        
        if (awsEndpoint != null && !awsEndpoint.isEmpty()) {
            // LocalStack configuration
            builder.endpointOverride(URI.create(awsEndpoint))
                   .credentialsProvider(StaticCredentialsProvider.create(
                       AwsBasicCredentials.create("test", "test")));
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }
        
        return builder.build();
    }

    @Bean
    public KinesisAsyncClient kinesisAsyncClient(Region region) {
        KinesisAsyncClientBuilder builder = KinesisAsyncClient.builder()
                .region(region);
        
        if (awsEndpoint != null && !awsEndpoint.isEmpty()) {
            // LocalStack configuration
            builder.endpointOverride(URI.create(awsEndpoint))
                   .credentialsProvider(StaticCredentialsProvider.create(
                       AwsBasicCredentials.create("test", "test")));
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }
        
        return builder.build();
    }

    @Bean
    public CloudWatchAsyncClient cloudWatchAsyncClient(Region region) {
        CloudWatchAsyncClientBuilder builder = CloudWatchAsyncClient.builder()
                .region(region);
        
        if (awsEndpoint != null && !awsEndpoint.isEmpty()) {
            // LocalStack configuration
            builder.endpointOverride(URI.create(awsEndpoint))
                   .credentialsProvider(StaticCredentialsProvider.create(
                       AwsBasicCredentials.create("test", "test")));
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }
        
        return builder.build();
    }
}
