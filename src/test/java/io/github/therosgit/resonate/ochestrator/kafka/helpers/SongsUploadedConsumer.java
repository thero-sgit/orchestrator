package io.github.therosgit.resonate.ochestrator.kafka.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.therosgit.resonate.ochestrator.domain.Song;
import io.github.therosgit.resonate.ochestrator.services.kafka.events.SongUploadedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class SongsUploadedConsumer {
    private CountDownLatch latch = new CountDownLatch(1);
    private SongUploadedEvent payload;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "song_uploaded", groupId = "resonate-consumer-for-test-test-group")
    public void consume(String payload) throws JsonProcessingException {
        this.payload = objectMapper.readValue(payload, SongUploadedEvent.class);
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public SongUploadedEvent getPayload() throws JsonProcessingException {
        return payload;
    }
}
