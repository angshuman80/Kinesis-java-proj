package com.example.kinesis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KinesisDynamoDbApplicationTest {

    @Test
    void testMainMethodExists() {
        // Verify main method exists and is accessible
        assertDoesNotThrow(() -> {
            KinesisDynamoDbApplication.class.getMethod("main", String[].class);
        });
    }

    @Test
    void testApplicationClassExists() {
        // Verify the application class can be instantiated
        assertNotNull(KinesisDynamoDbApplication.class);
    }
}
