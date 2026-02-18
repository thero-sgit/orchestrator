package io.github.therosgit.resonate.ochestrator.domain;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fingerprints")
public class Fingerprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID song_id;

    private Integer hash;

    private Integer time_offset;

    public Long getId() {
        return id;
    }

    public UUID getSongId() {
        return song_id;
    }

    public Integer getHash() {
        return hash;
    }

    public Integer getOffset() {
        return time_offset;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSongId(UUID song_id) {
        this.song_id = song_id;
    }

    public void setHash(Integer hash) {
        this.hash = hash;
    }

    public void setOffset(Integer time_offset) {
        this.time_offset = time_offset;
    }
}
