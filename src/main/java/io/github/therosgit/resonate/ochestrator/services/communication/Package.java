package io.github.therosgit.resonate.ochestrator.services.communication;

import org.springframework.util.MultiValueMap;

public interface Package {
    MultiValueMap<String, Object> audioResource();
}
