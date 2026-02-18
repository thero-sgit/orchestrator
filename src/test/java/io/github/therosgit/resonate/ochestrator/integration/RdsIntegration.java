package io.github.therosgit.resonate.ochestrator.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
public class RdsIntegration {
    @Autowired
    private DataSource dataSource;

    @Test
    void connectionEstablished() {
        assertThat(dataSource).isNotNull();
    }
}
