package io.github.therosgit.resonate.ochestrator.services.kafka;

import java.util.List;
import java.util.UUID;

public record FingerprintGeneratedEvent(
    UUID songId,
    List<List<Integer>> fingerprints,
    long genetaratedAt
) {}
