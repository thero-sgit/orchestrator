package io.github.therosgit.resonate.ochestrator.services.client;

import io.github.therosgit.resonate.ochestrator.services.communication.Package;
import io.github.therosgit.resonate.ochestrator.services.communication.models.LookupResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class FingerprintClient {
    private final RestClient restClient;

    public FingerprintClient(@Qualifier("rustRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public LookupResponse lookup(Package audio) {
        // send request
        return restClient.post()
                .uri("/fingerprint")
                .body(audio.audioResource())
                .retrieve()
                .body(LookupResponse.class);
    }

    public String health() {
        return restClient.get()
                .uri("/health")
                .retrieve()
                .body(String.class);
    }
}
