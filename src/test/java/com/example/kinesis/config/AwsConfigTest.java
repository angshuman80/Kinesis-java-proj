package com.example.kinesis.config;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;

import static org.junit.jupiter.api.Assertions.*;

class AwsConfigTest {

    @Test
    void testRegionCreation() {
        // When
        Region testRegion = Region.of("us-west-2");
        
        // Then
        assertNotNull(testRegion);
        assertEquals("us-west-2", testRegion.id());
    }

    @Test
    void testUsEast1Region() {
        // When
        Region usEast1 = Region.US_EAST_1;
        
        // Then
        assertNotNull(usEast1);
        assertEquals("us-east-1", usEast1.id());
    }

    @Test
    void testAwsConfigInstantiation() {
        // When & Then
        assertNotNull(new AwsConfig());
    }
}
