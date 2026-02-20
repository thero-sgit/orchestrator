package io.github.therosgit.resonate.ochestrator.kafka;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;

import io.github.therosgit.resonate.ochestrator.domain.Fingerprint;
import io.github.therosgit.resonate.ochestrator.repository.FingerprintRepository;

public class ConsumerService {
    private FingerprintRepository repository;

    public ConsumerService(FingerprintRepository repository) {
        this.repository = repository;
    }

    @KafkaListener
    public void consume(FingerprintGeneratedEvent event) {
        List<Fingerprint> entities = event.fingerprints()
            .stream()
            .map(pair -> {
                Fingerprint fingerprint = new Fingerprint();
                fingerprint.setSongId(event.songId());
                fingerprint.setHash(pair.get(0));
                fingerprint.setTimeOffset(pair.get(1));

                return fingerprint;
            })
            .toList();

        repository.saveAll(entities);
    }
}
