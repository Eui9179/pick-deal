package com.leui.storeservice.testcommon.postgres;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgreSQLTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        @SuppressWarnings("resource")
        PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
                DockerImageName
                        .parse("postgis/postgis:15-3.4")
                        .asCompatibleSubstituteFor("postgres"))
                .withDatabaseName("testdb")
                .withUsername("postgres")
                .withPassword("postgres");

        postgres.start();

        TestPropertyValues.of(
                "spring.datasource.url=" + postgres.getJdbcUrl(),
                "spring.datasource.username=" + postgres.getUsername(),
                "spring.datasource.password=" + postgres.getPassword()
        ).applyTo(context.getEnvironment());
    }
}
