package io.github.therosgit.resonate.ochestrator.driver;

import io.github.therosgit.resonate.ochestrator.components.Driver;
import io.github.therosgit.resonate.ochestrator.domain.Song;
import io.github.therosgit.resonate.ochestrator.domain.SongStatus;
import io.github.therosgit.resonate.ochestrator.integration.IntegrationTests;
import io.github.therosgit.resonate.ochestrator.repository.FingerprintRepository;
import io.github.therosgit.resonate.ochestrator.repository.SongRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static io.github.therosgit.resonate.ochestrator.driver.TestDriverIntegration.createMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;

public class TestVotingIntegration extends IntegrationTests {
    @Autowired
    Driver driver;

    @Autowired
    SongRepository songRepository;

    @Autowired
    FingerprintRepository fingerprintRepository;

    @BeforeEach
    void setUp() {
        try {
            try {
                uploadSongs();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearUp() {
        songRepository.deleteAll();
        fingerprintRepository.deleteAll();
    }

    @Test
    void testCanIdentifySong() throws IOException, URISyntaxException {
        String name = "001930";
        byte[] bytes = getAudioBytes("assets/voting/001/" + name + ".mp3");
        bytes = Arrays.copyOfRange(bytes, (bytes.length - 1) / 2, bytes.length - 1 );

        System.out.println("Bytes ready for lookup!");
        System.out.println("Looking up!");

        Song identifiedSong = driver.lookup(
                createMultipartFile(bytes, name)
        );

        System.out.println("Lookup done!");

        assertThat(identifiedSong).isNotNull();
        assertThat(identifiedSong.getTitle()).isEqualTo(name);
    }

    private void uploadSongs() throws URISyntaxException, IOException, InterruptedException {
        Path path = Paths.get(getClass().getClassLoader().getResource("assets/voting/001").toURI());

        List<Path> files;
        try (Stream<Path> stream = Files.list(path)) {
            files = stream.toList(); // collect first so we know the count
        }

        int expectedCount = files.size();

        for (Path file : files) {
            String name = file.getFileName().toString();
            byte[] bytes = getAudioBytes("assets/voting/001/" + name);
            System.out.println(">>>> DOING: " + name);
            driver.handleUpload(createMultipartFile(bytes, name.split("\\.")[0]));
        }

        // wait until ALL songs are saved and indexed
        while (true) {
            long indexed = songRepository.countByStatus(SongStatus.INDEXED);
            System.out.println("Indexed: " + indexed + "/" + expectedCount);
            if (indexed >= expectedCount) break;
            Thread.sleep(500);
        }

        System.out.println("Done Uploading!");
    }
}
