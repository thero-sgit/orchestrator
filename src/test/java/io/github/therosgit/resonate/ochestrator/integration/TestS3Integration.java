package io.github.therosgit.resonate.ochestrator.integration;

import io.github.therosgit.resonate.ochestrator.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class TestS3Integration extends IntegrationTests {
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
