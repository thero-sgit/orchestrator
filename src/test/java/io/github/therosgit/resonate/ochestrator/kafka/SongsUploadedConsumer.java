package io.github.therosgit.resonate.ochestrator.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.therosgit.resonate.ochestrator.services.kafka.SongUploadedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class SongsUploadedConsumer {
    private CountDownLatch latch = new CountDownLatch(1);
    private String payload;

    @KafkaListener(topics = "song_uploaded", groupId = "test-group")
    public void consume(String payload) {
        this.payload = payload;
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public SongUploadedEvent getPayload() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(payload, SongUploadedEvent.class);
    }
}
