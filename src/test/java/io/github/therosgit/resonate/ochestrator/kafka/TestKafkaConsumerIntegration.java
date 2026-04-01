package io.github.therosgit.resonate.ochestrator.kafka;

import io.github.therosgit.resonate.ochestrator.integration.IntegrationTests;
import io.github.therosgit.resonate.ochestrator.kafka.helpers.SongUploadedProducer;
import io.github.therosgit.resonate.ochestrator.repository.FingerprintRepository;
import io.github.therosgit.resonate.ochestrator.services.communication.models.Print;
import io.github.therosgit.resonate.ochestrator.services.kafka.events.FingerprintGeneratedEvent;
import io.github.therosgit.resonate.ochestrator.services.kafka.events.payload.FingerprintChunk;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;


public class TestKafkaConsumerIntegration extends IntegrationTests {
    @Autowired
    private KafkaTemplate<String, FingerprintGeneratedEvent> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, FingerprintChunk> chunkKafkaTemplate;

    @Autowired
    private FingerprintRepository repository;

    @BeforeEach
    void setUp() {
        SongUploadedProducer resonateProducer = new SongUploadedProducer(kafkaTemplate, chunkKafkaTemplate);

        // send fingerprint_generated event
        String songId = UUID.randomUUID().toString();

        List<FingerprintChunk> chunks = getChunkPayloads(songId);
        resonateProducer.sendFingerprintGenerated(
                new FingerprintGeneratedEvent(songId, (long) chunks.size()),
                chunks
        );
    }

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void testReceivesAndPersistsFingerprint() {
        await()
                .atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    repository.findAll().forEach(System.out::println);

                    assertThat(repository.findAll()).isNotEmpty();
                });
    }

    private List<FingerprintChunk> getChunkPayloads(String songId) {
        List<FingerprintChunk> chunks = new ArrayList<>();

        for (int index = 1; index <= 3; index++) {
            chunks.add(
                    new FingerprintChunk(
                            songId,
                            index,
                            getPrints(index)
                    )
            );
        }

        return chunks;
    }

    private List<Print> getPrints(int start) {
        List<Print> prints = new ArrayList<>();
        for (int index = start; index <= start + 3; index++) {
            prints.add(
                    new Print(
                            123 + start,
                            Long.parseLong(String.valueOf(start))
                    )
            );
        }

        return prints;
    }
}
