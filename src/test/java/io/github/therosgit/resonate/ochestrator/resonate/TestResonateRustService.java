package io.github.therosgit.resonate.ochestrator.resonate;

import io.github.therosgit.resonate.ochestrator.integration.IntegrationTests;
import io.github.therosgit.resonate.ochestrator.services.Resonate;
import io.github.therosgit.resonate.ochestrator.services.communication.Package;
import io.github.therosgit.resonate.ochestrator.services.communication.implementations.AudioPackage;
import io.github.therosgit.resonate.ochestrator.services.communication.models.LookupResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class TestResonateRustService extends IntegrationTests {
    @Autowired
    Resonate resonateService;

    static byte[] audioBytes = getAudioBytes("assets/aud.mp3");

    @BeforeEach
    void checkResonate() {
        if ( !resonateService.isUp() ) {
            fail("Resonate health check fail!");
        }
    }

    @Test
    void testLookUp() {
        Package audio = new AudioPackage(audioBytes);

        // send request
        LookupResponse response = resonateService.lookup(audio);

        assertThat(response.fingerprints()).hasSizeGreaterThan(0);
    }


}
