package io.github.therosgit.resonate.ochestrator.services.kafka;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.therosgit.resonate.ochestrator.services.communication.models.Print;
import io.github.therosgit.resonate.ochestrator.services.kafka.events.FingerprintGeneratedEvent;
import io.github.therosgit.resonate.ochestrator.services.kafka.events.payload.FingerprintChunk;
import io.github.therosgit.resonate.ochestrator.services.persistance.FingerprintPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import io.github.therosgit.resonate.ochestrator.domain.Fingerprint;
import io.github.therosgit.resonate.ochestrator.repository.FingerprintRepository;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {
    @Autowired
    private FingerprintPersistenceService fingerprintPersistenceService;

    @Autowired
    FingerprintRepository fingerprintRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @KafkaListener(topics = "fingerprint_generated", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String payload) throws JsonProcessingException {
        FingerprintGeneratedEvent event = objectMapper.readValue(payload, FingerprintGeneratedEvent.class);
        System.out.println(">>> Event Received: " + event.songId());
    }

    @KafkaListener(topics = "fingerprint_chunk", groupId = "${spring.kafka.consumer.group-id}")
    public void collectChunks(String payload) throws JsonProcessingException {
        // deserialize
        FingerprintChunk chunk = objectMapper.readValue(payload, FingerprintChunk.class);
        System.out.println(">>> Chunk Received: " + chunk.song_id());

        String songId = chunk.song_id();

        // save chunk
        saveChunk(songId, chunk);
    }

    private void saveChunk(String songId, FingerprintChunk chunk) {
        for (Print print: chunk.data()) {
            // construct fingerprint
            Fingerprint fingerprint = new Fingerprint();
            fingerprint.setSongId(UUID.fromString(songId));
            fingerprint.setHash(print.hash());
            fingerprint.setTimeOffset(print.frameIndex());

            fingerprintRepository.save(fingerprint);
        }
    }
}