package com.bol.game;

import com.bol.game.pojos.Player;
import com.bol.game.repositories.PlayerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlayerControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    PlayerRepository playerRepository;

    @Test
    public void testCreatePlayer() {

        Player player = new Player();
        player.setName("Van Gogh");

        webTestClient.post().uri("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(player), Player.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Van Gogh")
                .jsonPath("$.id").isNotEmpty();

        System.out.println(player.getId());

        playerRepository.deleteAll();
    }


}
