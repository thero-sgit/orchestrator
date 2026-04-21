package io.github.therosgit.resonate.ochestrator.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "songs")
public class Song {
    @Id
    private UUID id;

    private String title;

    private String s3key;

    private Instant uploadedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SongStatus status = SongStatus.PENDING;

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getS3key() {
        return s3key;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(SongStatus status) {
        this.status = status;
    }

    public void setS3key(String s3key) {
        this.s3key = s3key;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }    
}
