package io.github.therosgit.resonate.ochestrator.components.entities;

import java.util.UUID;

public record VoteKey(
        UUID id,
        Long delta
) {
}
