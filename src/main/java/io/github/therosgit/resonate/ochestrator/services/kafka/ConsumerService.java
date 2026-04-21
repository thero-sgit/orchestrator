package io.github.therosgit.resonate.ochestrator.services.kafka;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.therosgit.resonate.ochestrator.domain.SongStatus;
import io.github.therosgit.resonate.ochestrator.repository.SongRepository;
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

    @Autowired
    SongRepository songRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ConcurrentHashMap<String, Integer> chunkCount = new ConcurrentHashMap<>();

    @KafkaListener(topics = "fingerprint_generated", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String payload) throws JsonProcessingException, InterruptedException {
        FingerprintGeneratedEvent event = objectMapper.readValue(payload, FingerprintGeneratedEvent.class);
        System.out.println(">>> Event Received: " + event.songId());

        if (chunkCount.containsKey(event.songId())) {

            // wait for chunks to be completed
            while (chunkCount.get(event.songId()) < event.total_chunks()) Thread.sleep(500);

            // mark song as done
            songRepository.findById(UUID.fromString(event.songId())).ifPresent(song -> {
                song.setStatus(SongStatus.INDEXED);
                songRepository.save(song);
            });
        }
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

        System.out.println(chunk.song_id() + " Saved to DB");

        // increment chunk count
        chunkCount.merge(chunk.song_id(), 1, Integer::sum);
    }
}