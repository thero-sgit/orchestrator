package io.github.therosgit.resonate.ochestrator.services.kafka.events;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record FingerprintGeneratedEvent(
    String songId,
    Long total_chunks
) {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FingerprintGeneratedEvent(String id, Long totalChunks))) return false;
        return Objects.equals(songId, id) && Objects.equals(total_chunks, totalChunks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(songId, total_chunks);
    }
}
