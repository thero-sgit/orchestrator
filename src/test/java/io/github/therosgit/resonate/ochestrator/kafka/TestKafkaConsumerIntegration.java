package io.github.therosgit.resonate.ochestrator.kafka;

import io.github.therosgit.resonate.ochestrator.integration.IntegrationTests;
import io.github.therosgit.resonate.ochestrator.repository.FingerprintRepository;
import io.github.therosgit.resonate.ochestrator.services.kafka.events.FingerprintGeneratedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.UUID;


public class TestKafkaConsumerIntegration extends IntegrationTests {
    @Autowired
    private KafkaTemplate<String, FingerprintGeneratedEvent> template;

    @Autowired
    private FingerprintRepository repository;

    @BeforeEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void testReceivesAndPersistsFingerprint() {
        // create event
        UUID songId = UUID.randomUUID();

        // to-do
    }
}
