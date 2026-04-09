package io.github.therosgit.resonate.ochestrator.services;

import io.github.therosgit.resonate.ochestrator.services.communication.Package;
import io.github.therosgit.resonate.ochestrator.services.communication.models.LookupResponse;

import java.util.List;

public interface Resonate {
    boolean isUp();
    LookupResponse lookup(Package audio);
}