# LocalStack Setup for Kinesis and DynamoDB

This folder contains Docker Compose configuration and scripts to run LocalStack locally for testing the Kinesis to DynamoDB consumer application.

## Prerequisites

- Docker Desktop installed and running
- AWS CLI installed (for testing)

## Quick Start

### Windows

1. **Start LocalStack**:
   ```bash
   cd localstack
   start-localstack.bat
   ```

2. **Test Resources**:
   ```bash
   test-resources.bat
   ```

3. **Stop LocalStack**:
   ```bash
   stop-localstack.bat
   ```

### Linux/Mac

1. **Start LocalStack**:
   ```bash
   cd localstack
   docker-compose up -d
   ```

2. **View Logs**:
   ```bash
   docker-compose logs -f
   ```

3. **Stop LocalStack**:
   ```bash
   docker-compose down
   ```

## What Gets Created

The initialization script (`init-aws.sh`) automatically creates:

1. **Kinesis Stream**: `my-stream`
   - 1 shard
   - Region: us-east-1

2. **DynamoDB Table**: `my-table`
   - Partition Key: `id` (String)
   - Sort Key: `timestamp` (Number)
   - Billing Mode: PAY_PER_REQUEST

## Configuration

### LocalStack Endpoint

All AWS services are accessible at: `http://localhost:4566`

### AWS CLI Configuration

To use AWS CLI with LocalStack:

```bash
# Windows
set AWS_ENDPOINT_URL=http://localhost:4566
set AWS_ACCESS_KEY_ID=test
set AWS_SECRET_ACCESS_KEY=test
set AWS_DEFAULT_REGION=us-east-1

# Linux/Mac
export AWS_ENDPOINT_URL=http://localhost:4566
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1
```

## Testing with AWS CLI

### List Kinesis Streams
```bash
aws kinesis list-streams --endpoint-url http://localhost:4566 --region us-east-1
```

### Describe Kinesis Stream
```bash
aws kinesis describe-stream --stream-name my-stream --endpoint-url http://localhost:4566 --region us-east-1
```

### Put Record to Kinesis
```bash
aws kinesis put-record \
    --stream-name my-stream \
    --partition-key test-key \
    --data "{\"id\":\"test-123\",\"eventType\":\"TEST\",\"message\":\"Hello\"}" \
    --endpoint-url http://localhost:4566 \
    --region us-east-1
```

### List DynamoDB Tables
```bash
aws dynamodb list-tables --endpoint-url http://localhost:4566 --region us-east-1
```

### Scan DynamoDB Table
```bash
aws dynamodb scan --table-name my-table --endpoint-url http://localhost:4566 --region us-east-1
```

### Get Item from DynamoDB
```bash
aws dynamodb get-item \
    --table-name my-table \
    --key "{\"id\":{\"S\":\"test-123\"},\"timestamp\":{\"N\":\"1234567890\"}}" \
    --endpoint-url http://localhost:4566 \
    --region us-east-1
```

## Application Configuration

Update your `application.yml` to use LocalStack:

```yaml
aws:
  region: us-east-1
  kinesis:
    stream-name: my-stream
    application-name: kinesis-dynamodb-consumer
  dynamodb:
    table-name: my-table

# Add LocalStack endpoint override
cloud:
  aws:
    endpoint: http://localhost:4566
```

Or set environment variables:

```bash
# Windows
set AWS_ENDPOINT_URL=http://localhost:4566
set AWS_ACCESS_KEY_ID=test
set AWS_SECRET_ACCESS_KEY=test

# Linux/Mac
export AWS_ENDPOINT_URL=http://localhost:4566
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
```

## Troubleshooting

### LocalStack not starting
- Ensure Docker Desktop is running
- Check if port 4566 is available
- View logs: `docker-compose logs -f`

### Resources not created
- Check initialization logs: `docker-compose logs localstack`
- Manually run init script: `docker exec localstack-kinesis-dynamodb /etc/localstack/init/ready.d/init-aws.sh`

### Connection refused
- Verify LocalStack is running: `docker ps`
- Check endpoint URL is correct: `http://localhost:4566`

## Useful Commands

```bash
# View LocalStack logs
docker-compose logs -f

# Restart LocalStack
docker-compose restart

# Remove all data and restart fresh
docker-compose down -v
docker-compose up -d

# Execute commands inside LocalStack container
docker exec -it localstack-kinesis-dynamodb bash

# Check LocalStack health
curl http://localhost:4566/_localstack/health
```

## Data Persistence

LocalStack data is stored in a Docker volume named `localstack-data`. To completely reset:

```bash
docker-compose down -v
docker-compose up -d
```

## Notes

- LocalStack runs with `SERVICES=kinesis,dynamodb,cloudwatch`
- Credentials are dummy values (`test`/`test`) - LocalStack doesn't validate them
- All data is ephemeral unless you configure persistent storage
- The initialization script runs automatically when LocalStack starts
