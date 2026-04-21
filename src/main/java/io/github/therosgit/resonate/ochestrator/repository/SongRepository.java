package io.github.therosgit.resonate.ochestrator.repository;

import java.util.UUID;

import io.github.therosgit.resonate.ochestrator.domain.SongStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import io.github.therosgit.resonate.ochestrator.domain.Song;

public interface SongRepository extends JpaRepository<Song, UUID> {
    boolean existsByStatusNot(SongStatus status);

    long countByStatus(SongStatus songStatus);
}
