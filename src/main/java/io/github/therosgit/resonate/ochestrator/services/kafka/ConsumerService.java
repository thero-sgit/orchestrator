package io.github.therosgit.resonate.ochestrator.services.kafka;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.therosgit.resonate.ochestrator.services.communication.models.Print;
import io.github.therosgit.resonate.ochestrator.services.kafka.events.FingerprintGeneratedEvent;
import io.github.therosgit.resonate.ochestrator.services.kafka.events.payload.FingerprintChunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import io.github.therosgit.resonate.ochestrator.domain.Fingerprint;
import io.github.therosgit.resonate.ochestrator.repository.FingerprintRepository;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {
    @Autowired
    private FingerprintRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentHashMap<String, List<FingerprintChunk>> buffers = new ConcurrentHashMap<>();


    @KafkaListener(topics = "fingerprint_generated", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String payload) throws JsonProcessingException {
        FingerprintGeneratedEvent event = objectMapper.readValue(payload, FingerprintGeneratedEvent.class);
        System.out.println(">>> Event Received: " + event.songId());

        // get song id
        String songId = event.songId();

        // collect and save chunks
        List<FingerprintChunk> chunks = buffers.get(event.songId());
        List<Fingerprint> flattenedChunks = flattenChunks(songId, chunks);
        repository.saveAll(flattenedChunks);
    }

    @KafkaListener(topics = "fingerprint_chunk", groupId = "${spring.kafka.consumer.group-id}")
    public void collectChunks(String payload) throws JsonProcessingException {
        // deserialize
        FingerprintChunk chunk = objectMapper.readValue(payload, FingerprintChunk.class);
        System.out.println(">>> Chunk Received: " + chunk.song_id());

        String songId = chunk.song_id();

        // create buffer list if not exists
        buffers.computeIfAbsent(songId, k -> Collections.synchronizedList(new ArrayList<>()));

        // get buffer and add chunk to the buffer
        List<FingerprintChunk> buffer = buffers.get(songId);
        buffer.add(chunk);
    }

    private List<Fingerprint> flattenChunks(String songId, List<FingerprintChunk> chunks) {
        List<Fingerprint> flattenedChunks = new ArrayList<>();

        for (FingerprintChunk chunk: chunks) {
            for (Print print: chunk.data()) {
                // create fingerprint
                Fingerprint fingerprint = new Fingerprint();
                fingerprint.setSongId(UUID.fromString(songId));
                fingerprint.setHash(print.hash());
                fingerprint.setTimeOffset(print.frameIndex());

                flattenedChunks.add(fingerprint);
            }
        }

        return flattenedChunks;
    }
}