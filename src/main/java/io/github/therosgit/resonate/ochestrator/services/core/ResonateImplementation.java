package io.github.therosgit.resonate.ochestrator.services.core;

import io.github.therosgit.resonate.ochestrator.services.Resonate;
import io.github.therosgit.resonate.ochestrator.services.client.FingerprintClient;
import io.github.therosgit.resonate.ochestrator.services.communication.Package;
import io.github.therosgit.resonate.ochestrator.services.communication.models.LookupResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResonateImplementation implements Resonate {
    @Autowired
    FingerprintClient client;

    @Override
    public boolean isUp() {
        String response = client.health();
        return response.equals("healthy");
    }

    @Override
    public LookupResponse lookup(Package audio) {
        return client.lookup(audio);
    }
}
