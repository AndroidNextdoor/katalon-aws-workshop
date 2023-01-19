package com.androidnextdoor.aws

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class AwsUtilsSpec {

    private final AwsUtils awsUtils = new AwsUtils();

//    @Test
//    void testBearerToken() {
//        String profile = "primary"
//        String jsonAuth = "test-user1.json"
//        String bearerToken = awsUtils.getBearerToken(profile,jsonAuth)
//
//        assertNotNull(bearerToken);
//    }

    @Test
    void createGridUrl() {
        String profile = "admin"
        String region = "us-west-2"
        String secretName = "reddit/web";
        String secretKey = "Reddit-DeviceFarmARN";
        String gridArn = awsUtils.retrieveSecret(profile, region, secretName, secretKey)
        String gridUrl = awsUtils.getGridUrl(profile,gridArn,"60")

        assertNotNull(gridUrl);
    }

    @Test
    void retrieveSecret() {
        String profile = "admin"
        String secretName = "reddit/web"
        String secretKey = "Reddit-DeviceFarmARN"
        String region = "us-west-2"
        String secretValue = awsUtils.retrieveSecret(profile, region, secretName, secretKey)

        assertNotNull(secretValue);
    }

}
