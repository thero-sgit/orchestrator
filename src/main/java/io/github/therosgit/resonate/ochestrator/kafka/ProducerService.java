package io.github.therosgit.resonate.ochestrator.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {
    private final KafkaTemplate<String, SongUploadedEvent> kafkaTemplate;

    public ProducerService(KafkaTemplate<String, SongUploadedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

    }

    public void sendSongUploaded(SongUploadedEvent event) {
        kafkaTemplate.send(
            "song_uploaded",
            event.songId().toString(),
            event
        );
    }
}
