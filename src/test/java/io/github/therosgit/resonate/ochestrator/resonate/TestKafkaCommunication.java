package io.github.therosgit.resonate.ochestrator.resonate;

import io.github.therosgit.resonate.ochestrator.integration.IntegrationTests;
import io.github.therosgit.resonate.ochestrator.services.core.StorageImplementation;
import io.github.therosgit.resonate.ochestrator.services.kafka.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;

public class TestKafkaCommunication extends IntegrationTests {
    @Autowired
    ProducerService producer;

    @Autowired
    static StorageImplementation storage;


}
