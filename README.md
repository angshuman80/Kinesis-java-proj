# Kinesis to DynamoDB Consumer

A Spring Boot application that reads records from an AWS Kinesis stream and updates DynamoDB.

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- **For Local Development**: Docker Desktop (for LocalStack)
- **For AWS**: AWS Account with:
  - Kinesis Stream created
  - DynamoDB Table created
  - Appropriate IAM permissions

## Quick Start with LocalStack (Local Development)

For local testing without AWS account:

1. **Start LocalStack**:
   ```bash
   cd localstack
   start-localstack.bat
   ```

2. **Run the application with local profile**:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

3. **Test by sending data**:
   ```bash
   cd localstack
   test-resources.bat
   ```

See [localstack/README.md](localstack/README.md) for detailed LocalStack setup and usage.

## AWS Configuration

### Required IAM Permissions

Your AWS credentials need the following permissions:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "kinesis:DescribeStream",
        "kinesis:GetRecords",
        "kinesis:GetShardIterator",
        "kinesis:ListShards",
        "kinesis:SubscribeToShard"
      ],
      "Resource": "arn:aws:kinesis:*:*:stream/*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "dynamodb:PutItem",
        "dynamodb:GetItem",
        "dynamodb:UpdateItem",
        "dynamodb:CreateTable",
        "dynamodb:DescribeTable"
      ],
      "Resource": "arn:aws:dynamodb:*:*:table/*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "cloudwatch:PutMetricData"
      ],
      "Resource": "*"
    }
  ]
}
```

### DynamoDB Table Structure

Create a DynamoDB table with:
- **Partition Key**: `id` (String)
- **Sort Key**: `timestamp` (Number)

Example AWS CLI command:
```bash
aws dynamodb create-table \
    --table-name your-dynamodb-table-name \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
        AttributeName=timestamp,AttributeType=N \
    --key-schema \
        AttributeName=id,KeyType=HASH \
        AttributeName=timestamp,KeyType=RANGE \
    --billing-mode PAY_PER_REQUEST
```

### Kinesis Stream

Create a Kinesis stream:
```bash
aws kinesis create-stream \
    --stream-name your-kinesis-stream-name \
    --shard-count 1
```

## Configuration

Update `src/main/resources/application.yml`:

```yaml
aws:
  region: us-east-1  # Your AWS region
  kinesis:
    stream-name: your-kinesis-stream-name
    application-name: kinesis-dynamodb-consumer
  dynamodb:
    table-name: your-dynamodb-table-name
```

## AWS Credentials

Set up AWS credentials using one of these methods:

1. **AWS CLI Configuration** (Recommended):
   ```bash
   aws configure
   ```

2. **Environment Variables**:
   ```bash
   set AWS_ACCESS_KEY_ID=your_access_key
   set AWS_SECRET_ACCESS_KEY=your_secret_key
   set AWS_REGION=us-east-1
   ```

3. **IAM Role** (if running on EC2/ECS)

## Build and Run

### Build the project:
```bash
mvn clean package
```

### Run the application:
```bash
mvn spring-boot:run
```

Or run the JAR:
```bash
java -jar target/kinesis-dynamodb-consumer-1.0.0.jar
```

## Testing

### Send test data to Kinesis:
```bash
aws kinesis put-record \
    --stream-name your-kinesis-stream-name \
    --partition-key test-key \
    --data "{\"id\":\"test-123\",\"eventType\":\"TEST_EVENT\",\"message\":\"Hello from Kinesis\"}"
```

### Verify data in DynamoDB:
```bash
aws dynamodb scan --table-name your-dynamodb-table-name
```

## Project Structure

```
src/
└── main/
    ├── java/com/example/kinesis/
    │   ├── KinesisDynamoDbApplication.java    # Main application
    │   ├── config/
    │   │   └── AwsConfig.java                 # AWS clients configuration
    │   ├── model/
    │   │   └── EventRecord.java               # DynamoDB entity
    │   ├── processor/
    │   │   ├── RecordProcessor.java           # Kinesis record processor
    │   │   └── RecordProcessorFactory.java    # Processor factory
    │   └── service/
    │       └── DynamoDbService.java           # DynamoDB operations
    └── resources/
        └── application.yml                     # Configuration
```

## Customization

### Modify Event Processing

Edit `EventRecord.fromJson()` in `EventRecord.java` to match your data structure:

```java
public static EventRecord fromJson(String json) {
    ObjectMapper mapper = new ObjectMapper();
    try {
        JsonNode node = mapper.readTree(json);
        return EventRecord.builder()
                .id(node.get("id").asText())
                .eventType(node.get("type").asText())
                .data(json)
                .timestamp(Instant.now().toEpochMilli())
                .status("PROCESSED")
                .build();
    } catch (Exception e) {
        throw new RuntimeException("Failed to parse JSON", e);
    }
}
```

## Monitoring

The application uses AWS CloudWatch for monitoring:
- Kinesis metrics (GetRecords, PutRecords)
- DynamoDB metrics (Read/Write capacity)
- Application logs

## Troubleshooting

### Common Issues

1. **AWS Credentials Not Found**
   - Ensure AWS credentials are properly configured
   - Check IAM permissions

2. **Stream Not Found**
   - Verify stream name in configuration
   - Ensure stream exists in the correct region

3. **DynamoDB Access Denied**
   - Check IAM permissions for DynamoDB
   - Verify table name is correct

4. **Checkpoint Errors**
   - KCL uses DynamoDB for checkpointing
   - Ensure application has permissions to create/access checkpoint table

## Architecture

```
Kinesis Stream → KCL (Kinesis Client Library) → Record Processor → DynamoDB
                                                        ↓
                                                  CloudWatch Metrics
```

## Key Features

- **Automatic Checkpointing**: KCL handles checkpointing automatically
- **Shard Management**: Automatic load balancing across shards
- **Error Handling**: Robust error handling with logging
- **CloudWatch Integration**: Metrics and monitoring
- **Spring Boot**: Easy configuration and dependency injection

## License

MIT License
