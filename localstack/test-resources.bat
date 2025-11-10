@echo off
echo Testing LocalStack Resources...
echo.

set AWS_ENDPOINT_URL=http://localhost:4566
set AWS_ACCESS_KEY_ID=test
set AWS_SECRET_ACCESS_KEY=test
set AWS_DEFAULT_REGION=us-east-1

echo Checking Kinesis Stream: my-stream
aws kinesis describe-stream --stream-name my-stream --endpoint-url http://localhost:4566 --region us-east-1

echo.
echo ========================================
echo.

echo Checking DynamoDB Table: my-table
aws dynamodb describe-table --table-name my-table --endpoint-url http://localhost:4566 --region us-east-1

echo.
echo ========================================
echo.

echo Sending test record to Kinesis...
aws kinesis put-record --stream-name my-stream --partition-key test-key --data "{\"id\":\"test-123\",\"eventType\":\"TEST\",\"message\":\"Hello LocalStack\"}" --endpoint-url http://localhost:4566 --region us-east-1

echo.
echo Test complete!
echo.
