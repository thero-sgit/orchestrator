package io.github.therosgit.resonate.ochestrator.controller;

import io.github.therosgit.resonate.ochestrator.services.Resonate;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("resonateHealth")
public class HealthController implements HealthIndicator {
    @Autowired
    Resonate resonate;

    @Override
    public @Nullable Health health() {
        try {
            // check resonate service
            boolean isResonateUp = resonate.isUp();

            if (isResonateUp) {
                return Health.up()
                        .withDetail("resonate-server", "Connected")
                        .build();
            }

            return Health.down()
                    .withDetail("reason", "Resonate server unreachable")
                    .build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
