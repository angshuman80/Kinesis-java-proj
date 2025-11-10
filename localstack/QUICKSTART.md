# LocalStack Quick Start Guide

## Step-by-Step Setup

### 1. Start LocalStack

```bash
cd localstack
start-localstack.bat
```

This will:
- Start LocalStack container
- Automatically create Kinesis stream `my-stream`
- Automatically create DynamoDB table `my-table`
- Wait 30 seconds for initialization

### 2. Verify Resources

```bash
test-resources.bat
```

You should see:
- Kinesis stream details
- DynamoDB table details
- A test record sent to Kinesis

### 3. Run Your Application

From the project root:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Or set environment variable and run:

```bash
set SPRING_PROFILES_ACTIVE=local
mvn spring-boot:run
```

### 4. Send Test Data

```bash
aws kinesis put-record ^
    --stream-name my-stream ^
    --partition-key test-key ^
    --data "{\"id\":\"test-123\",\"eventType\":\"TEST\",\"message\":\"Hello\"}" ^
    --endpoint-url http://localhost:4566 ^
    --region us-east-1
```

### 5. Verify Data in DynamoDB

```bash
aws dynamodb scan ^
    --table-name my-table ^
    --endpoint-url http://localhost:4566 ^
    --region us-east-1
```

### 6. Stop LocalStack

```bash
stop-localstack.bat
```

## Troubleshooting

### Port Already in Use
If port 4566 is already in use:
```bash
# Find process using port
netstat -ano | findstr :4566

# Kill the process (replace PID)
taskkill /PID <PID> /F
```

### Resources Not Created
Check logs:
```bash
docker-compose logs -f
```

Manually run init script:
```bash
docker exec localstack-kinesis-dynamodb /etc/localstack/init/ready.d/init-aws.sh
```

### Application Can't Connect
1. Verify LocalStack is running: `docker ps`
2. Check endpoint in `application-local.yml` is `http://localhost:4566`
3. Ensure you're using the `local` profile

## What's Created

- **Kinesis Stream**: `my-stream` with 1 shard
- **DynamoDB Table**: `my-table` with:
  - Partition Key: `id` (String)
  - Sort Key: `timestamp` (Number)

## Useful Commands

```bash
# View logs
docker-compose logs -f

# Restart
docker-compose restart

# Complete reset
docker-compose down -v
docker-compose up -d

# Check health
curl http://localhost:4566/_localstack/health
```
