package io.github.therosgit.resonate.ochestrator.integration;

import io.github.therosgit.resonate.ochestrator.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers@SpringBootTest(properties = {
        "spring.cloud.aws.region.static=us-east-1",
        "spring.cloud.aws.credentials.access-key=noop",
        "spring.cloud.aws.credentials.secret-key=noop"
})
public class TestS3Integration {
    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
            .withServices("s3");

    private S3Client s3Client;

    @BeforeEach
    void setUp() {
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

    @Test
    void shouldUpload() {
        Storage s3Storage = new Storage(s3Client);

        s3Storage.upload(
                "test-bucket",
                "test-key",
                "data".getBytes(StandardCharsets.UTF_8)
        );

        assertThat(s3Storage.listObjects("test-bucket").contents()).hasSize(1);
    }
}
