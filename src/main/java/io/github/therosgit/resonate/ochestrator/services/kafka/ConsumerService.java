package io.github.therosgit.resonate.ochestrator.services.kafka;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

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

    private final ConcurrentHashMap<String, FingerprintGeneratedEvent> activeEvents = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<FingerprintChunk>> buffers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CompletableFuture<Void>> futures = new ConcurrentHashMap<>();


    @KafkaListener(topics = "fingerprint_generated", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(FingerprintGeneratedEvent event) {
        String songId = event.songId();
        System.out.println(">>> Event Received: " + event.songId());

        // add event to active events
        if (activeEvents.putIfAbsent(songId, event) != null) return;

        // create new empty buffer
        buffers.put(songId, Collections.synchronizedList(new ArrayList<>()));

        // add new future to futures
        CompletableFuture<Void> future = new CompletableFuture<>();
        futures.put(songId, future);

        // collect and save chunks
        future.thenRun(() -> {
            List<FingerprintChunk> chunks = buffers.get(songId);
            List<Fingerprint> fingerprints = flattenChunks(songId, chunks);

            // persist chunks
            repository.saveAll(fingerprints);

            // clean up
            buffers.remove(songId);
            futures.remove(songId);
        });
    }

    @KafkaListener(topics = "fingerprint_chunk", groupId = "${spring.kafka.consumer.group-id}")
    public void collectChunk(FingerprintChunk chunk) {
        String songId = chunk.song_id();
        System.out.println(">>> Chunk Received: " + chunk.song_id());

        // ignore chunks for songs we're not tracking
        if ( !activeEvents.containsKey(songId) ) return;
        FingerprintGeneratedEvent event = activeEvents.get(songId);

        // get buffer
        List<FingerprintChunk> buffer = buffers.get(songId);

        // clean up after all chunks collected
        synchronized (buffer) {
            // add chunk to buffer
            buffer.add(chunk);

            if (buffer.size() == event.total_chunks()) {
                buffer.sort(Comparator.comparingInt(FingerprintChunk::index));
                activeEvents.remove(songId);

                // triggers thenRun
                futures.get(songId).complete(null);
            }
        }
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