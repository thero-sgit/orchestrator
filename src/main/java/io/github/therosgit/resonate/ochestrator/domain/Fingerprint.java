package io.github.therosgit.resonate.ochestrator.domain;

import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(
        name = "fingerprints",
        indexes = {
            @Index(name = "idx_fingerprint_hash", columnList = "hash_value"),
            @Index(name = "idx_fingerprint_song_id", columnList = "song_id")
        }
)
public class Fingerprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "song_id")
    private UUID songId;

    @Column(name = "hash_value")
    private Long hash;

    @Column(name = "time_offset")
    private Long timeOffset;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getSongId() { return songId; }
    public void setSongId(UUID songId) { this.songId = songId; }

    public Long getHash() { return hash; }
    public void setHash(Long hash) { this.hash = hash; }

    public Long getTimeOffset() { return timeOffset; }
    public void setTimeOffset(Long timeOffset) { this.timeOffset = timeOffset; }
}
