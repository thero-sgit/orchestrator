package io.github.therosgit.resonate.ochestrator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.therosgit.resonate.ochestrator.domain.Fingerprint;

public interface FingerprintRepository extends JpaRepository<Fingerprint, Long> {
    List<Fingerprint> findByHashIn(List<Long> hashes);
}
