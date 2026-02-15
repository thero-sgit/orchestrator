package io.github.therosgit.resonate.ochestrator.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class FingerprintClient {
    private final WebClient webClient;

    public FingerprintClient(
        @Value("${fingerprint.service.url}")
        String baseUrl
    ) {
        
        webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
    }

    public Mono<List<List<Integer>>> lookup(byte[] audio) {
        return webClient.post()
            .uri("/lookup")
            .bodyValue(audio)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<>() {});
    }    
}
