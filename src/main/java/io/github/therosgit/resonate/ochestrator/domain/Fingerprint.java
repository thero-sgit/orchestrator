package io.github.therosgit.resonate.ochestrator.domain;

import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "fingerprints")
public class Fingerprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "song_id")
    private UUID songId;

    @Column(name = "hash_value")
    private Integer hash;

    @Column(name = "time_offset")
    private Integer timeOffset;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getSongId() { return songId; }
    public void setSongId(UUID songId) { this.songId = songId; }

    public Integer getHash() { return hash; }
    public void setHash(Integer hash) { this.hash = hash; }

    public Integer getTimeOffset() { return timeOffset; }
    public void setTimeOffset(Integer timeOffset) { this.timeOffset = timeOffset; }
}
