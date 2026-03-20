package io.github.therosgit.resonate.ochestrator.controller;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.github.therosgit.resonate.ochestrator.domain.Song;
import io.github.therosgit.resonate.ochestrator.services.kafka.ProducerService;
import io.github.therosgit.resonate.ochestrator.services.kafka.events.SongUploadedEvent;
import io.github.therosgit.resonate.ochestrator.repository.SongRepository;

@RestController
@RequestMapping("/songs")
public class UploadController {
    private final ProducerService kafkaProducerService;
    private final SongRepository songRepository;

    public UploadController(ProducerService kafkaProducerService, SongRepository songRepository) {
        this.kafkaProducerService = kafkaProducerService;
        this.songRepository = songRepository;
    }

    @PostMapping
    public ResponseEntity<?> upload(
        @RequestParam("file")
        MultipartFile file
    ) throws IOException {

        UUID id = UUID.randomUUID();

        Song song = new Song();
        song.setId(id);
        song.setUploadedAt(Instant.now());
        song.setS3key("uploads/" + id + ".mp3");

        songRepository.save(song);

        kafkaProducerService.sendSongUploaded(
            new SongUploadedEvent(
                id,
                song.getS3key(),
                song.getUploadedAt().toString()
            )
        );

        return ResponseEntity.accepted().build();
    }
}
