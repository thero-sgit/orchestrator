package io.github.therosgit.resonate.ochestrator.repository;

import java.util.List;

import org.hibernate.query.TypedParameterValue;
import org.springframework.data.jpa.repository.JpaRepository;

import io.github.therosgit.resonate.ochestrator.domain.Fingerprint;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FingerprintRepository extends JpaRepository<Fingerprint, Long> {
    List<Fingerprint> findByHashIn(List<Long> hashes);
}
