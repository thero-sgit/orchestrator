package io.github.therosgit.resonate.ochestrator.services.communication.implementations;

import io.github.therosgit.resonate.ochestrator.services.communication.Package;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class AudioPackage implements Package {
    private final byte[] audio;

    public AudioPackage(byte[] audio) {
        this.audio = audio;
    }

    @Override
    public MultiValueMap<String, Object> audioResource() {
        // build body (audio to send)
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource resource = new ByteArrayResource(audio) {
            @Override
            public String getFilename() {
                return "audio.mp3";
            }
        };

        body.add("file", resource);

        return body;
    }
}
