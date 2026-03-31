package io.github.therosgit.resonate.ochestrator.services;

import io.github.therosgit.resonate.ochestrator.services.kafka.events.SongUploadedEvent;

public interface Producer {
    void sendSongUploaded(SongUploadedEvent event);
}
