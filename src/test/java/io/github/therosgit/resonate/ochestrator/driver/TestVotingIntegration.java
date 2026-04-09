package io.github.therosgit.resonate.ochestrator.driver;

import io.github.therosgit.resonate.ochestrator.components.Driver;
import io.github.therosgit.resonate.ochestrator.integration.IntegrationTests;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public class TestVotingIntegration extends IntegrationTests {
    @Autowired
    Driver driver;

    @BeforeEach
    void setUp() {

    }
}
