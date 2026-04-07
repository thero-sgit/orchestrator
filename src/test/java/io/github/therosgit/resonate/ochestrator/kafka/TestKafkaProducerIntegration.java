package io.github.therosgit.resonate.ochestrator.kafka;

import io.github.therosgit.resonate.ochestrator.integration.IntegrationTests;
import io.github.therosgit.resonate.ochestrator.kafka.helpers.SongsUploadedConsumer;
import io.github.therosgit.resonate.ochestrator.services.Producer;
import io.github.therosgit.resonate.ochestrator.services.core.StorageImplementation;
import io.github.therosgit.resonate.ochestrator.services.kafka.ProducerService;
import io.github.therosgit.resonate.ochestrator.services.kafka.events.SongUploadedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class TestKafkaProducerIntegration extends IntegrationTests {
    @Autowired
    private KafkaTemplate<String, SongUploadedEvent> template;

    @Autowired
    private SongsUploadedConsumer consumer;

    private Producer producerService;

    @Value("${app.s3-bucket-name}")
    private String bucketName;

    @Autowired
    private StorageImplementation storage;

    private static final byte[] audioBytes = getAudioBytes("assets/aud.mp3");

    @BeforeEach
    void uploadSongToS3Bucket() {
        storage.upload(
                bucketName,
                "audio-124.mp3",
                audioBytes
        );
    }

    @BeforeEach
    void instantiateProducer() {
        producerService = new ProducerService(template);
    }
    
    @Test
    void testSimpleSend() {
        UUID songId = UUID.randomUUID();

        SongUploadedEvent event = new SongUploadedEvent(songId, "audio-124.mp3");
        producerService.sendSongUploaded(event);

        await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(consumer.getPayload()).isNotNull();

                    assertThat(consumer.getPayload().songId()).isEqualTo(songId);
                    assertThat(consumer.getPayload().s3Key()).isEqualTo("audio-124.mp3");
                });
    }
}