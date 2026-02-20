package io.github.therosgit.resonate.ochestrator.kafka;
import java.util.UUID;

public record SongUploadedEvent(
        UUID songId,
        String s3key,
        String timeUploaded
) {}
