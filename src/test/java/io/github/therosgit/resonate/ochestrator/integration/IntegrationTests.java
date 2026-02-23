package io.github.therosgit.resonate.ochestrator.integration;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
public abstract class IntegrationTests {

    // Set up Kafka
    static KafkaContainer kafkaContainer = new KafkaContainer("apache/kafka-native:3.8.0");

    // Set up S3
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
            .withServices("s3");

    // Set up Postgres
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("testPostgres")
            .withUsername("testUsername")
            .withPassword("testPassword");

    static {
        kafkaContainer.start();
        localStackContainer.start();
        postgreSQLContainer.start();
    }


    // Override properties
    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        // Kafka
        registry.add("spring.kafka.bootstrap-servers", () -> kafkaContainer.getBootstrapServers());

        // S3
        registry.add("spring.cloud.aws.s3.endpoint", () -> localStackContainer.getEndpoint().toString());

        // Postgres
        registry.add("spring.datasource.url", () -> postgreSQLContainer.getJdbcUrl());
    }
}
