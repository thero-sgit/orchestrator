package io.github.therosgit.resonate.ochestrator.resonate;

import io.github.therosgit.resonate.ochestrator.integration.IntegrationTests;
import io.github.therosgit.resonate.ochestrator.repository.FingerprintRepository;
import io.github.therosgit.resonate.ochestrator.services.core.StorageImplementation;
import io.github.therosgit.resonate.ochestrator.services.kafka.ProducerService;
import io.github.therosgit.resonate.ochestrator.services.kafka.events.SongUploadedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class TestKafkaCommunication extends IntegrationTests {
    @Autowired
    private ProducerService producer;

    @Autowired
    private StorageImplementation storage;

    @Autowired
    private FingerprintRepository repository;

    @Value("${app.s3-bucket-name}")
    private String bucketName;

    private static final byte[] audioBytes = getAudioBytes("assets/aud.mp3");

    @BeforeEach
    void uploadSongToS3Bucket() {
        storage.upload(
                bucketName,
                "audio-123.mp3",
                audioBytes
        );
    }

    @Test
    void testResonateKafkaCommunication() throws InterruptedException, IOException {
        assertThat(storage.listObjects(bucketName).contents()).hasSize(1);

        // send Song Uploaded Event
        SongUploadedEvent event = new SongUploadedEvent(
                UUID.randomUUID(),
                "audio-123.mp3"
        );
        producer.sendSongUploaded(event);
        Thread.sleep(2000);

        assertThat(resonateContainer.isRunning()).isTrue();

        // check if Consumer received and stored fingerprints in db
        await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> assertThat(repository.findAll()).isNotEmpty());

    }

    private MultipartFile createMultipartFile() {
        return new MockMultipartFile (
                "aud",
                "aud.mp3",
                "audio/mpeg",
                audioBytes
        );
    }

}
