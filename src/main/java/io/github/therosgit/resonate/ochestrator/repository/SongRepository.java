package io.github.therosgit.resonate.ochestrator.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.therosgit.resonate.ochestrator.domain.Song;

public interface SongRepository extends JpaRepository<Song, UUID> {

}
