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

    private UUID songId;

    private Integer hash;

    private Integer offset;

    public Long getId() {
        return id;
    }

    public UUID getSongId() {
        return songId;
    }

    public Integer getHash() {
        return hash;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSongId(UUID songId) {
        this.songId = songId;
    }

    public void setHash(Integer hash) {
        this.hash = hash;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    
}
