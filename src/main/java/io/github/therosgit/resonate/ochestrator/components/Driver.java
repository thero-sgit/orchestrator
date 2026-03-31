package io.github.therosgit.resonate.ochestrator.components;

import io.github.therosgit.resonate.ochestrator.repository.SongRepository;
import io.github.therosgit.resonate.ochestrator.services.Producer;
import io.github.therosgit.resonate.ochestrator.services.Resonate;
import io.github.therosgit.resonate.ochestrator.services.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class Driver {
    @Autowired
    private Resonate resonate;

    @Autowired
    private Storage storage;

    @Autowired
    private Producer producer;

    @Autowired
    SongRepository songRepository;

    public void handleUpload(MultipartFile audio) {

    }


}
