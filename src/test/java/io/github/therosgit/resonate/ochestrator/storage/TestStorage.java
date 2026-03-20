package io.github.therosgit.resonate.ochestrator.storage;

import io.github.therosgit.resonate.ochestrator.services.core.Storage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TestStorage {
    @Mock
    private S3Client s3Client;

    @InjectMocks
    private Storage storage;

    @Test
    void shouldUploadFile() {
        storage.upload("test-bucket", "test-key", "test-data".getBytes(StandardCharsets.UTF_8));

        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}
