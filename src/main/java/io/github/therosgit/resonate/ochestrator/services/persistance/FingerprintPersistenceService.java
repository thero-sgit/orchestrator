package io.github.therosgit.resonate.ochestrator.services.persistance;

import io.github.therosgit.resonate.ochestrator.domain.Fingerprint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FingerprintPersistenceService {
    private static final int BATCH_SIZE = 500;

    @Autowired
    private JdbcTemplate template;

    public void batchInsert(List<Fingerprint> fingerprints) {
        String sql = "INSERT INTO fingerprints (song_id, hash_value, time_offset) VALUES (?, ?, ?)";

        template.batchUpdate(sql, fingerprints, BATCH_SIZE, (preparedStatement, fingerprint) -> {
            preparedStatement.setObject(1, fingerprint.getSongId());
            preparedStatement.setLong(2, fingerprint.getHash());
            preparedStatement.setLong(3, fingerprint.getTimeOffset());
        });
    }
}
