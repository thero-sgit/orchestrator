package io.github.therosgit.resonate.ochestrator.services.kafka.events.payload;


import io.github.therosgit.resonate.ochestrator.services.communication.models.Print;

import java.util.List;

public record FingerprintChunk (
        String song_id,
        Integer index,
        List<Print> data
) {
}
