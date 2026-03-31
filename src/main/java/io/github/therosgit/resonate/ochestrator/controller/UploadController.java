package io.github.therosgit.resonate.ochestrator.controller;

import java.io.IOException;

import io.github.therosgit.resonate.ochestrator.components.Driver;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/songs")
public class UploadController {
    @Autowired
    private Driver driver;

    @PostMapping
    public ResponseEntity<?> upload(
        @RequestParam("file")
        MultipartFile file
    ) throws IOException {

        if ( !isAudioFile(file) ) {

        }

        driver.handleUpload(file);

        return ResponseEntity.accepted().build();
    }

    private boolean isAudioFile(MultipartFile file) throws IOException {
        Tika tika = new Tika();
        String mimeType = tika.detect(file.getInputStream());
        return mimeType.startsWith("audio/");
    }
}
