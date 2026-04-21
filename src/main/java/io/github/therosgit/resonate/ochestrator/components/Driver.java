package io.github.therosgit.resonate.ochestrator.components;

import io.github.therosgit.resonate.ochestrator.components.entities.SongMatch;
import io.github.therosgit.resonate.ochestrator.controller.exception.InvalidFileException;
import io.github.therosgit.resonate.ochestrator.domain.Fingerprint;
import io.github.therosgit.resonate.ochestrator.domain.Song;
import io.github.therosgit.resonate.ochestrator.repository.FingerprintRepository;
import io.github.therosgit.resonate.ochestrator.repository.SongRepository;
import io.github.therosgit.resonate.ochestrator.services.Producer;
import io.github.therosgit.resonate.ochestrator.services.Resonate;
import io.github.therosgit.resonate.ochestrator.services.Storage;
import io.github.therosgit.resonate.ochestrator.services.communication.Package;
import io.github.therosgit.resonate.ochestrator.services.communication.implementations.AudioPackage;
import io.github.therosgit.resonate.ochestrator.services.communication.models.LookupResponse;
import io.github.therosgit.resonate.ochestrator.services.communication.models.Print;
import io.github.therosgit.resonate.ochestrator.services.kafka.events.SongUploadedEvent;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;

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

    @Autowired
    FingerprintRepository fingerprintRepository;

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

    public Song lookup(MultipartFile audio) throws IOException {
        Package audioPackage = new AudioPackage(audio.getBytes());

        LookupResponse response = resonate.lookup(audioPackage);
        List<Print> prints = response.fingerprints();

        List<Long> hashes = prints.stream()
                .map(Print::hash)
                .toList();

        List<Fingerprint> queriedMatches = partitionHashes(hashes, 30000)
                .stream().flatMap(
                        chunk -> fingerprintRepository.findByHashIn(chunk).stream()
                )
                .toList();

        return bestMatch(queriedMatches, prints);
    }

    // Vote on (songId, delta) pairs — song with the most consistent delta wins
    private Song bestMatch(List<Fingerprint> queriedMatches, List<Print> prints) {
        // (songId, delta) -> vote count
        Map<UUID, Map<Long, Long>> voteMap = new HashMap<>();

        for (Print print : prints) {
            for (Fingerprint match : queriedMatches) {
                long delta = match.getTimeOffset() - print.frameIndex();

                voteMap
                        .computeIfAbsent(match.getSongId(), id -> new HashMap<>())
                        .merge(delta, 1L, Long::sum);
            }
        }

        // for each song, find its best delta vote count
        UUID electedId = null;
        long highestVotes = 0;

        for (Map.Entry<UUID, Map<Long, Long>> entry : voteMap.entrySet()) {
            long bestDeltaVotes = entry.getValue().values().stream()
                    .mapToLong(Long::longValue)
                    .max()
                    .orElse(0);

            if (bestDeltaVotes > highestVotes) {
                highestVotes = bestDeltaVotes;
                electedId = entry.getKey();
            }
        }

        if (electedId != null) {
            return songRepository.findById(electedId).orElse(null);
        }

        return null;
    }

    private List<List<Long>> partitionHashes(List<Long> hashes, int chunkSize) {
        List<List<Long>> chunks = new ArrayList<>();

        for (int index = 0; index < hashes.size(); index += chunkSize) {
            chunks.add(hashes.subList(index, Math.min(index + chunkSize, hashes.size())));
        }

        return chunks;
    }

    private Song toSong(MultipartFile audio) throws IOException {
        // create song object
        String title = extractTitle(audio);
        String extension = getExtension(audio);
        UUID id = UUID.randomUUID();
        String s3Key = title + "-" + id + extension;
        Instant time = Instant.now();

        Song song = new Song();
        song.setTitle(title.split("\\.")[0]);
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
