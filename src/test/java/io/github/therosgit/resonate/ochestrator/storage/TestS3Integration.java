package io.github.therosgit.resonate.ochestrator.storage;

import io.github.therosgit.resonate.ochestrator.integration.IntegrationTests;
import io.github.therosgit.resonate.ochestrator.services.Storage;
import io.github.therosgit.resonate.ochestrator.services.core.StorageImplementation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class TestS3Integration extends IntegrationTests {
    @Value("${app.s3-bucket-name}")
    private String bucketName;

    @Test
    void shouldUpload() {
        Storage s3Storage = new StorageImplementation(s3Client);
        String key = "test-key";
        byte[] data = "data".getBytes(StandardCharsets.UTF_8);

        s3Storage.upload(
                bucketName,
                key,
                data
        );

        assertThat(s3Storage.listObjects(bucketName).contents()).isNotEmpty();
        assertThat(s3Storage.download(bucketName, key)).isEqualTo(data);
    }
}
