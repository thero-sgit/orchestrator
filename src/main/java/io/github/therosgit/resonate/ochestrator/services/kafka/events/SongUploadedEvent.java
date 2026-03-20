package io.github.therosgit.resonate.ochestrator.services.kafka.events;
import java.util.UUID;

public record SongUploadedEvent(
        UUID songId,
        String s3key,
        String timeUploaded
) {}
