package com.androidnextdoor.db

import com.androidnextdoor.aws.AwsUtils
import org.junit.jupiter.api.Test

import java.sql.Connection

import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertNull

class DataSourceUtilsSpec {

    private final DataSourceUtils dsUtils = new DataSourceUtils();

    // Resources are looked up in secrets manager

    private final String profile = "admin"
    private final String region = "us-west-2"
    private final String secretName = "mysql"
    // Passing the secret keys all under the secretName
    private final String connStrKey = "connectionString"
    private final String userKey = "dbUser"
    private final String passwordKey = "dbPassword"
    Connection conn = null
    AwsUtils awsUtils = new AwsUtils()

    def connectionString = awsUtils.retrieveSecret(profile, region, secretName, connStrKey)
    def user = awsUtils.retrieveSecret(profile, region, secretName, userKey)
    def password = awsUtils.retrieveSecret(profile, region, secretName, passwordKey)

    @Test
    void testDBConnection() {
        conn = this.getLocalConnection()
        assertNotNull(conn)

        conn = dsUtils.closeDatabaseConnection(conn)
        assertNull(conn)
    }

    Connection getLocalConnection() {
        conn = dsUtils.connectDB(secretName, connectionString, user, password)
        return conn
    }

}
