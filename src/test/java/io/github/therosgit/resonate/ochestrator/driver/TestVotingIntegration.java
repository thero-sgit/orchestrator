package io.github.therosgit.resonate.ochestrator.driver;

import io.github.therosgit.resonate.ochestrator.components.Driver;
import io.github.therosgit.resonate.ochestrator.domain.Song;
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
import java.time.Duration;
import java.util.stream.Stream;

import static io.github.therosgit.resonate.ochestrator.driver.TestDriverIntegration.createMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

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
            uploadSongs();
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
        await()
                .atMost(Duration.ofSeconds(120))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    assertThat(fingerprintRepository.findAll()).isNotEmpty();

                    String name = "001736";
                    byte[] bytes = getAudioBytes("assets/voting/001/" + name + ".mp3");

                    Song identifiedSong = driver.lookup(
                            createMultipartFile(bytes, name)
                    );

                    assertThat(identifiedSong).isNotNull();
                    assertThat(identifiedSong.getTitle()).isEqualTo(name);
                });
    }

    private void uploadSongs() throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getClassLoader().getResource("assets/voting/001").toURI());


        try (Stream<Path> stream = Files.list(path)) {
            stream.forEach(
                    file -> {

                        //get audio data
                        String name = file.getFileName().toString();
                        byte[] bytes = getAudioBytes("assets/voting/001/" + name);

                        System.out.println(">>>> DOING:" + name);

                        // upload audio
                        try {
                            driver.handleUpload(
                                    createMultipartFile(
                                            bytes,
                                            name.split("\\.")[0]
                                    )
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }
    }
}
