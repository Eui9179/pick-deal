package com.leui.storeservice.testcommon.postgres;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgreSQLTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final PostgreSQLContainer<?> POSTGRES;

    static {
        //noinspection resource
        POSTGRES = new PostgreSQLContainer<>(
                DockerImageName
                        .parse("postgis/postgis:15-3.4")
                        .asCompatibleSubstituteFor("postgres"))
                .withDatabaseName("testdb")
                .withUsername("postgres")
                .withPassword("postgres");
        POSTGRES.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        TestPropertyValues.of(
                "spring.datasource.url=" + POSTGRES.getJdbcUrl(),
                "spring.datasource.username=" + POSTGRES.getUsername(),
                "spring.datasource.password=" + POSTGRES.getPassword()
        ).applyTo(context.getEnvironment());
    }
}
