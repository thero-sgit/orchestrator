package io.github.therosgit.resonate.ochestrator.storage;

import io.github.therosgit.resonate.ochestrator.integration.IntegrationTests;
import io.github.therosgit.resonate.ochestrator.services.Storage;
import io.github.therosgit.resonate.ochestrator.services.core.StorageImplementation;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class TestS3Integration extends IntegrationTests {
    @Test
    void shouldUpload() {
        Storage s3Storage = new StorageImplementation();

        s3Storage.upload(
                "test-bucket",
                "test-key",
                "data".getBytes(StandardCharsets.UTF_8)
        );

        assertThat(s3Storage.listObjects("test-bucket").contents()).hasSize(1);
    }
}
