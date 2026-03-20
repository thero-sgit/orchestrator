package io.github.therosgit.resonate.ochestrator.services.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class FingerprintClientConfig {

    @Value("${services.rust.base-url}")
    private String rustBaseUrl;

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient rustRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(rustBaseUrl)
                .build();
    }
}