package io.github.therosgit.resonate.ochestrator.integration;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public abstract class IntegrationTests {
    protected static S3Client s3Client;
    private static Network network = Network.newNetwork();

    // Set up Kafka
    static KafkaContainer kafkaContainer = new KafkaContainer("apache/kafka-native:3.8.0")
            .withNetwork(network);

    // Set up S3
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
            .withNetwork(network)
            .withServices("s3");

    // Set up Postgres
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("testPostgres")
            .withUsername("testUsername")
            .withPassword("testPassword");

    // Set up Resonate (Rust Fingerprinting Service)
    static GenericContainer<?> resonateContainer;

    static {
        kafkaContainer.start();
        localStackContainer.start();
        postgreSQLContainer.start();

        try {
            resonateContainer = new GenericContainer<>(DockerImageName.parse("ghcr.io/thero-sgit/resonate-fingerprinter:v0.1.5"))
                    .withNetwork(network)
                    .withExposedPorts(8080)
                    .withEnv("KAFKA_BROKERS", kafkaContainer.getBootstrapServers())
                    .withEnv("S3_BUCKET", "test-bucket")
                    .waitingFor(Wait.forHttp("/health").forStatusCode(200));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            resonateContainer.start();
        } catch (Exception e) {
            System.out.println(resonateContainer.getLogs());
            throw e;
        }
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

    @BeforeAll
    static void setUp() {
        s3Client = S3Client
                .builder()
                .endpointOverride(localStackContainer.getEndpoint())
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(localStackContainer.getAccessKey(), localStackContainer.getSecretKey())
                        )
                )
                .region(Region.of(localStackContainer.getRegion()))
                .build();

        s3Client.createBucket(
                CreateBucketRequest.builder()
                        .bucket("test-bucket")
                        .build()
        );
    }
}
