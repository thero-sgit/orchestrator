package io.github.therosgit.resonate.ochestrator.components;

import io.github.therosgit.resonate.ochestrator.controller.exception.InvalidFileException;
import io.github.therosgit.resonate.ochestrator.domain.Song;
import io.github.therosgit.resonate.ochestrator.repository.SongRepository;
import io.github.therosgit.resonate.ochestrator.services.Producer;
import io.github.therosgit.resonate.ochestrator.services.Resonate;
import io.github.therosgit.resonate.ochestrator.services.Storage;
import io.github.therosgit.resonate.ochestrator.services.kafka.events.SongUploadedEvent;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;

@Component
public class Driver {
    @Autowired
    private Resonate resonate;

    @Autowired
    private Storage storage;

    @Autowired
    private Producer producer;

    @Autowired
    SongRepository songRepository;

    @Value("${app.s3-bucket-name}")
    private String bucketName;

    public void handleUpload(MultipartFile audio) throws IOException {
        Song song = toSong(audio);

        // upload to s3
        storage.upload(bucketName, song.getS3key(), audio.getBytes());

        // save to db
        songRepository.save(song);

        // publish song_uploaded event
        SongUploadedEvent event = new SongUploadedEvent(song.getId(), song.getS3key());
        producer.sendSongUploaded(event);
    }

    private Song toSong(MultipartFile audio) throws IOException {
        // create song object
        String title = extractTitle(audio);
        String extension = getExtension(audio);
        UUID id = UUID.randomUUID();
        String s3Key = title + "-" + id + extension;
        Instant time = Instant.now();

        Song song = new Song();
        song.setId(id);
        song.setS3key(s3Key);
        song.setUploadedAt(time);

        return song;
    }

    private String extractTitle(MultipartFile file) throws IOException {
        Tika tika = new Tika();
        Metadata metadata = new Metadata();

        try (InputStream stream = file.getInputStream()) {
            tika.parse(stream, metadata);
        }

        String title = metadata.get("dc:title");
        return (title != null) ? title : file.getOriginalFilename();
    }

    private String getExtension(MultipartFile file) throws IOException {
        Tika tika = new Tika();
        String mimeType = tika.detect(file.getInputStream());
        return switch (mimeType) {
            case "audio/mpeg" -> ".mp3";
            case "audio/flac" -> ".flac";
            case "audio/wav", "audio/x-wav" -> ".wav";
            case "audio/aac" -> ".aac";
            case "audio/ogg" -> ".ogg";
            default -> throw new InvalidFileException("Unsupported audio format: " + mimeType);
        };
    }
}
