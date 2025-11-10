# Unit Tests Documentation

This directory contains comprehensive unit tests for the Kinesis to DynamoDB consumer application.

## Test Structure

```
src/test/
├── java/com/example/kinesis/
│   ├── model/
│   │   └── EventRecordTest.java              # Tests for EventRecord entity
│   ├── service/
│   │   └── DynamoDbServiceTest.java          # Tests for DynamoDB service
│   ├── processor/
│   │   ├── RecordProcessorTest.java          # Tests for Kinesis record processor
│   │   └── RecordProcessorFactoryTest.java   # Tests for processor factory
│   ├── config/
│   │   └── AwsConfigTest.java                # Tests for AWS configuration
│   └── KinesisDynamoDbApplicationTest.java   # Application context tests
└── resources/
    └── application-test.yml                   # Test configuration
```

## Test Coverage

### 1. EventRecordTest
Tests the `EventRecord` model class:
- ✅ Builder pattern functionality
- ✅ JSON parsing with valid data
- ✅ JSON parsing with missing fields (id, eventType)
- ✅ Handling invalid JSON
- ✅ Handling empty JSON
- ✅ Equality and hashCode
- ✅ toString() method

**Key Test Cases:**
```java
testFromJsonWithValidJson()      // Valid JSON with all fields
testFromJsonWithoutId()           // Auto-generates UUID
testFromJsonWithInvalidJson()     // Graceful error handling
```

### 2. RecordProcessorTest
Tests the Kinesis record processing logic:
- ✅ Initialization with shard ID
- ✅ Processing single record
- ✅ Processing multiple records
- ✅ Processing empty record list
- ✅ Error handling for DynamoDB failures
- ✅ Error handling for checkpoint failures
- ✅ Shard lifecycle events (ended, shutdown, lease lost)
- ✅ Invalid JSON handling

**Key Test Cases:**
```java
testProcessRecordsWithValidData()           // Normal processing flow
testProcessRecordsWithMultipleRecords()     // Batch processing
testProcessRecordsWithDynamoDbException()   // Error resilience
```

### 3. RecordProcessorFactoryTest
Tests the processor factory:
- ✅ Processor creation
- ✅ Multiple processor instances
- ✅ Null service handling

### 4. DynamoDbServiceTest
Tests DynamoDB service logic:
- ✅ Event record validation
- ✅ Key construction (partition + sort key)
- ✅ Null value handling
- ✅ Multiple events with same ID but different timestamps

### 5. AwsConfigTest
Tests AWS configuration:
- ✅ Region bean creation
- ✅ Configuration instantiation
- ✅ Spring context loading

### 6. KinesisDynamoDbApplicationTest
Tests Spring Boot application:
- ✅ Context loads successfully
- ✅ Main method exists

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=EventRecordTest
mvn test -Dtest=RecordProcessorTest
```

### Run with Coverage
```bash
mvn test jacoco:report
```

Coverage report will be generated at: `target/site/jacoco/index.html`

### Run Tests in IDE
- **IntelliJ IDEA**: Right-click on test class → Run
- **Eclipse**: Right-click on test class → Run As → JUnit Test
- **VS Code**: Click the play button next to test methods

## Test Configuration

Tests use `application-test.yml` with test-specific properties:
```yaml
aws:
  region: us-east-1
  kinesis:
    stream-name: test-stream
  dynamodb:
    table-name: test-table
```

## Integration Testing with LocalStack

For integration tests with actual AWS services (using LocalStack):

1. **Start LocalStack**:
   ```bash
   cd localstack
   start-localstack.bat
   ```

2. **Run integration tests**:
   ```bash
   mvn verify -Pintegration-test
   ```

## Mocking Strategy

- **Mockito** is used for mocking dependencies
- **@ExtendWith(MockitoExtension.class)** for JUnit 5 integration
- Mock AWS SDK clients to avoid actual AWS calls
- Focus on testing business logic, not AWS SDK behavior

## Best Practices Followed

1. **Arrange-Act-Assert (AAA)** pattern
2. **Descriptive test names** (testMethodName_Scenario_ExpectedBehavior)
3. **One assertion per test** (where practical)
4. **Test isolation** - each test is independent
5. **Mock external dependencies** - no actual AWS calls
6. **Edge case coverage** - null values, empty data, invalid input

## Adding New Tests

When adding new functionality:

1. **Create test class** in corresponding test package
2. **Follow naming convention**: `ClassNameTest.java`
3. **Use JUnit 5** annotations (@Test, @BeforeEach, etc.)
4. **Mock dependencies** with Mockito
5. **Test happy path and error cases**
6. **Update this README** with new test coverage

## Test Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

Includes:
- JUnit 5
- Mockito
- AssertJ
- Hamcrest
- Spring Test

## Continuous Integration

Tests are automatically run in CI/CD pipeline:
- On every pull request
- On push to main/develop branches
- See `.github/workflows/windsurf-review.yml`

## Troubleshooting

### Tests Failing Locally

1. **Clean and rebuild**:
   ```bash
   mvn clean test
   ```

2. **Check Java version**:
   ```bash
   java -version  # Should be Java 17+
   ```

3. **Update dependencies**:
   ```bash
   mvn clean install -U
   ```

### Mock Issues

If mocks aren't working:
- Ensure `@ExtendWith(MockitoExtension.class)` is present
- Verify `@Mock` annotations on fields
- Check that mocked methods are actually called

### Spring Context Issues

If context won't load:
- Check `application-test.yml` has all required properties
- Verify `@SpringBootTest` configuration
- Use `spring.main.lazy-initialization=true` for faster tests

## Code Coverage Goals

Target coverage metrics:
- **Line Coverage**: > 80%
- **Branch Coverage**: > 70%
- **Class Coverage**: > 90%

Current coverage can be viewed after running:
```bash
mvn test jacoco:report
open target/site/jacoco/index.html
```

## Future Enhancements

- [ ] Add integration tests with LocalStack
- [ ] Add performance tests for record processing
- [ ] Add contract tests for DynamoDB schema
- [ ] Add mutation testing with PIT
- [ ] Add property-based testing with jqwik
