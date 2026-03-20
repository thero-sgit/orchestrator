package io.github.therosgit.resonate.ochestrator.kafka;

import io.github.therosgit.resonate.ochestrator.integration.IntegrationTests;
import io.github.therosgit.resonate.ochestrator.services.kafka.ProducerService;
import io.github.therosgit.resonate.ochestrator.services.kafka.events.SongUploadedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    private ProducerService producerService;

    @BeforeEach
    void instantiateProducer() {
        producerService = new ProducerService(template);
    }
    
    @Test
    void testSimpleSend() {
        UUID songId = UUID.randomUUID();
        String time = Instant.now().toString();

        SongUploadedEvent event = new SongUploadedEvent(songId, "test-s3Key", time);
        producerService.sendSongUploaded(event);

        try {
            boolean messageReceived = consumer.getLatch().await(5, TimeUnit.SECONDS);

            await()
                    .atMost(10, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                        assertThat(consumer.getPayload()).isNotNull();

                        assertThat(consumer.getPayload().songId()).isEqualTo(songId);
                        assertThat(consumer.getPayload().s3key()).isEqualTo("test-s3Key");
                        assertThat(consumer.getPayload().timeUploaded()).isEqualTo(time);
                    });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}