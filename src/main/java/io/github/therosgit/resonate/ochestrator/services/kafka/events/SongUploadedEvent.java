package io.github.therosgit.resonate.ochestrator.services.kafka.events;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record SongUploadedEvent(
        @JsonProperty("song_id") UUID songId,
        @JsonProperty("s3_key") String s3Key
) {}
