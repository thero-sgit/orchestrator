package io.github.therosgit.resonate.ochestrator.kafka.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.therosgit.resonate.ochestrator.services.kafka.events.SongUploadedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class SongsUploadedConsumer {
    private CountDownLatch latch = new CountDownLatch(1);
    private SongUploadedEvent payload;

    @KafkaListener(topics = "song_uploaded", groupId = "resonate-consumer-for-test-test-group")
    public void consume(SongUploadedEvent payload) {
        this.payload = payload;
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public SongUploadedEvent getPayload() throws JsonProcessingException {
        return payload;
    }
}
