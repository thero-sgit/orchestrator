package io.github.therosgit.resonate.ochestrator.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class FingerprintClient {

    private final RestClient restClient;

    public FingerprintClient(@Qualifier("rustRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public List<List<Integer>> lookup(byte[] audio) {
        return restClient.post()
                .uri("/lookup")
                .body(audio)
                .retrieve()
                .body(new ParameterizedTypeReference<List<List<Integer>>>() {});
    }
}
