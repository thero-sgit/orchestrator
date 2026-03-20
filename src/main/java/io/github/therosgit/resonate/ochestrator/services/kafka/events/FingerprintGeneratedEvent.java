package io.github.therosgit.resonate.ochestrator.services.kafka.events;

import java.util.List;
import java.util.UUID;

public record FingerprintGeneratedEvent(
    UUID songId,
    List<List<Long>> fingerprints
) {}
