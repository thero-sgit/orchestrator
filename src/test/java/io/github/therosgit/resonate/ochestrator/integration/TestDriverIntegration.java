package io.github.therosgit.resonate.ochestrator.integration;

import io.github.therosgit.resonate.ochestrator.components.Driver;
import io.github.therosgit.resonate.ochestrator.repository.FingerprintRepository;
import io.github.therosgit.resonate.ochestrator.repository.SongRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class TestDriverIntegration extends IntegrationTests {
    @Autowired
    private Driver driver;

    @Autowired
    private FingerprintRepository fingerprintRepository;

    @Autowired
    private SongRepository songRepository;

    private static final byte[] audioBytes = getAudioBytes("assets/aud.mp3");

    @Test
    void testUploadPipeline() throws IOException {
        driver.handleUpload(createMultipartFile());

        assertThat(resonateContainer.isRunning()).isTrue();

        // check if Consumer received and stored fingerprints in db
        await()
            .atMost(Duration.ofSeconds(60))
            .pollInterval(Duration.ofMillis(500))
            .untilAsserted(() -> {
                assertThat(fingerprintRepository.findAll()).isNotEmpty();
                assertThat(songRepository.findAll()).isNotEmpty();

            });
    }

    private MultipartFile createMultipartFile() {
        return new MockMultipartFile(
                "aud",
                "aud.mp3",
                "audio/mpeg",
                audioBytes
        );
    }
}
