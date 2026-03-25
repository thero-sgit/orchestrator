package io.github.therosgit.resonate.ochestrator.services.communication.models;

import java.util.List;

public record LookupResponse(List<Fingerprint> fingerprints) {}
