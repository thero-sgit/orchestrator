package io.github.therosgit.resonate.ochestrator.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

public class TestPostgresIntegration extends IntegrationTests {
    @Autowired
    private DataSource dataSource;

    @Test
    void connectionEstablished() {
        assertThat(dataSource).isNotNull();
    }

    @Test
    void fingerprintsTableExists() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        String sql = """
            SELECT count(*)\s
            FROM information_schema.tables\s
            WHERE table_schema = 'public'
              AND table_name = ?
       \s""";

        String tableName = "fingerprints";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
        assertThat(count).as("Table 'fingerprints' should exist in schema 'public'").isGreaterThan(0);
    }

    @Test
    void songsTableExists() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        String sql = """
            SELECT count(*)\s
            FROM information_schema.tables\s
            WHERE table_schema = 'public'
              AND table_name = ?
       \s""";

        String tableName = "songs";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
        assertThat(count).as("Table 'songs' should exist in schema 'public'").isGreaterThan(0);
    }
}
