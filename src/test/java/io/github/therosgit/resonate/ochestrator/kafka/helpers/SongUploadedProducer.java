package io.github.therosgit.resonate.ochestrator.kafka.helpers;

import io.github.therosgit.resonate.ochestrator.services.kafka.events.FingerprintGeneratedEvent;
import io.github.therosgit.resonate.ochestrator.services.kafka.events.payload.FingerprintChunk;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

public class SongUploadedProducer {
    private final KafkaTemplate<String, FingerprintGeneratedEvent> kafkaTemplate;
    private final KafkaTemplate<String, FingerprintChunk> chunkKafkaTemplate;

    public SongUploadedProducer(
            KafkaTemplate<String, FingerprintGeneratedEvent> kafkaTemplate,
            KafkaTemplate<String, FingerprintChunk> chunkKafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.chunkKafkaTemplate = chunkKafkaTemplate;
    }

    public void sendFingerprintGenerated(FingerprintGeneratedEvent event, List<FingerprintChunk> chunks) {
        sendFingerprintChunks(chunks);

        kafkaTemplate.send(
                "fingerprint_generated",
                event.songId(),
                event
        );
    }

    private void sendFingerprintChunks(List<FingerprintChunk> chunks) {
        for (FingerprintChunk chunk: chunks) {
            chunkKafkaTemplate.send(
                    "fingerprint_chunk",
                    chunk.song_id(),
                    chunk
            );
        }
    }
}
