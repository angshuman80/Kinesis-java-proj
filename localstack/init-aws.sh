#!/bin/bash

echo "######################################"
echo "# Initializing LocalStack Resources #"
echo "######################################"

# Wait for LocalStack to be ready
echo "Waiting for LocalStack to be ready..."
sleep 5

# Set AWS endpoint
export AWS_ENDPOINT_URL=http://localhost:4566
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1

echo ""
echo "Creating Kinesis Stream: my-stream"
awslocal kinesis create-stream \
    --stream-name my-stream \
    --shard-count 1 \
    --region us-east-1

# Wait for stream to be active
echo "Waiting for Kinesis stream to be active..."
awslocal kinesis wait stream-exists \
    --stream-name my-stream \
    --region us-east-1

echo "✓ Kinesis stream 'my-stream' created successfully"

echo ""
echo "Creating DynamoDB Table: my-table"
awslocal dynamodb create-table \
    --table-name my-table \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
        AttributeName=timestamp,AttributeType=N \
    --key-schema \
        AttributeName=id,KeyType=HASH \
        AttributeName=timestamp,KeyType=RANGE \
    --billing-mode PAY_PER_REQUEST \
    --region us-east-1

# Wait for table to be active
echo "Waiting for DynamoDB table to be active..."
awslocal dynamodb wait table-exists \
    --table-name my-table \
    --region us-east-1

echo "✓ DynamoDB table 'my-table' created successfully"

echo ""
echo "Verifying Kinesis Stream..."
awslocal kinesis describe-stream \
    --stream-name my-stream \
    --region us-east-1 \
    --query 'StreamDescription.{StreamName:StreamName,Status:StreamStatus,Shards:Shards[*].ShardId}' \
    --output table

echo ""
echo "Verifying DynamoDB Table..."
awslocal dynamodb describe-table \
    --table-name my-table \
    --region us-east-1 \
    --query 'Table.{TableName:TableName,Status:TableStatus,KeySchema:KeySchema}' \
    --output table

echo ""
echo "######################################"
echo "# LocalStack Setup Complete!         #"
echo "######################################"
echo ""
echo "Resources created:"
echo "  - Kinesis Stream: my-stream (1 shard)"
echo "  - DynamoDB Table: my-table (id: String, timestamp: Number)"
echo ""
echo "LocalStack endpoint: http://localhost:4566"
echo ""
