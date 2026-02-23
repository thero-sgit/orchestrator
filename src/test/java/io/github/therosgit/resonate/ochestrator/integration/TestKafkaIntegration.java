package io.github.therosgit.resonate.ochestrator.integration;

import io.github.therosgit.resonate.ochestrator.kafka.ProducerService;
import io.github.therosgit.resonate.ochestrator.kafka.SongUploadedEvent;
import io.github.therosgit.resonate.ochestrator.kafka.SongsUploadedConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public class TestKafkaIntegration extends IntegrationTests {
    @Autowired
    private KafkaTemplate<String, SongUploadedEvent> template;

    @Autowired
    private SongsUploadedConsumer consumer;

    private ProducerService producerService;

    @BeforeEach
    void setUp() {
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
