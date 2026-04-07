package io.github.therosgit.resonate.ochestrator.integration;

import io.github.therosgit.resonate.ochestrator.resonate.TestResonateRustService;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterAll;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public abstract class IntegrationTests {
    protected static S3Client s3Client;
    private static Network network = Network.newNetwork();

    // Set up Kafka
    static final KafkaContainer kafkaContainer = new KafkaContainer("apache/kafka:3.7.0")
            .withNetwork(network)
            .withNetworkAliases("kafka")
            .withListener("kafka:19092");

    // Set up S3
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
            .withNetwork(network)
            .withNetworkAliases("localstack")
            .withServices("s3");

    // Set up Postgres
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("testPostgres")
            .withUsername("testUsername")
            .withPassword("testPassword");

    // Set up Resonate (Rust Fingerprinting Service)
    public static GenericContainer<?> resonateContainer;

    // start all containers
    static {
        // start kafka container and create topics
        kafkaContainer.start();
        createTopics();

        localStackContainer.start();
        postgreSQLContainer.start();

        try {
            resonateContainer = new GenericContainer<>(DockerImageName.parse("ghcr.io/thero-sgit/resonate-fingerprinter:v0.1.8"))
                    .withNetwork(network)
                    .withExposedPorts(8080)
                    .withEnv("RUST_LOG", "info")

                    // set kafka brokers env variable for kafka
                    .withEnv("KAFKA_BROKERS", "kafka:19092")

                    // set aws (local stack) env variable for s3
                    .withEnv("S3_BUCKET", "test-bucket")
                    .withEnv("AWS_ACCESS_KEY_ID", localStackContainer.getAccessKey())
                    .withEnv("AWS_SECRET_ACCESS_KEY", localStackContainer.getSecretKey())
                    .withEnv("AWS_REGION", localStackContainer.getRegion())
                    .withEnv("AWS_ENDPOINT_URL", "http://localstack:4566")
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

        // Rust service
        registry.add("services.rust.base-url", () -> "http://" + resonateContainer.getHost() + ":" + resonateContainer.getMappedPort(8080));
    }

    static {

        // create s3 bucket
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

    protected static byte[] getAudioBytes(String filePath) {
        InputStream inputStream = IntegrationTests.class.getClassLoader().getResourceAsStream(filePath);
        if (inputStream == null) {
            try {
                throw new IOException("File not found in resources: " + filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createTopics() {
        try (AdminClient admin = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers()
        ))) {
            List<NewTopic> topics = List.of(
                    new NewTopic("song_uploaded", 1, (short) 1),
                    new NewTopic("fingerprint_generated", 1, (short) 1),
                    new NewTopic("fingerprint_chunk", 1, (short) 1)
            );
            try {
                admin.createTopics(topics).all().get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
