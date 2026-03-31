package io.github.therosgit.resonate.ochestrator.services.kafka;

import io.github.therosgit.resonate.ochestrator.services.Producer;
import io.github.therosgit.resonate.ochestrator.services.kafka.events.SongUploadedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService implements Producer {
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
