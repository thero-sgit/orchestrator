package io.github.therosgit.resonate.ochestrator;

import io.github.therosgit.resonate.ochestrator.integration.IntegrationTests;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

@ImportTestcontainers(IntegrationTests.class)
class ResonateOchestratorApplicationTests extends IntegrationTests {

    public static void main(String[] args) {
        SpringApplication
                .from(ResonateOchestratorApplication::main)
                .with(ResonateOchestratorApplicationTests.class)
                .run(args);
    }

}
