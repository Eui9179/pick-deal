package com.leui.storeservice.testcommon.db;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgreSQLTestContainer {
    public static PostgreSQLContainer<?> getContainer() {
        //noinspection resource
        return new PostgreSQLContainer<>(
                DockerImageName
                        .parse("postgis/postgis:15-3.4")
                        .asCompatibleSubstituteFor("postgres"))
                .withDatabaseName("testdb")
                .withUsername("postgres")
                .withPassword("postgres");
    }
}
