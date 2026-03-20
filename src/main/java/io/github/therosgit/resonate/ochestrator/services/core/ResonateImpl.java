package io.github.therosgit.resonate.ochestrator.services.core;

import io.github.therosgit.resonate.ochestrator.services.Resonate;
import io.github.therosgit.resonate.ochestrator.services.client.FingerprintClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResonateImpl implements Resonate {
    @Autowired
    FingerprintClient client;

    @Override
    public boolean isUp() {
        String response = client.health();
        return response.equals("healthy");
    }
}
