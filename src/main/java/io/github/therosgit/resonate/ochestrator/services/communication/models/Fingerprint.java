package io.github.therosgit.resonate.ochestrator.services.communication.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Fingerprint(
        @JsonProperty("hash") long hash,
        @JsonProperty("frame_index") int frameIndex
) {}