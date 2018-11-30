package com.bol.game;

import com.bol.game.pojos.Game;
import com.bol.game.pojos.Movement;
import com.bol.game.pojos.Player;
import com.bol.game.repositories.GameRepository;
import com.bol.game.repositories.PlayerRepository;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameControllerTest {


    @Autowired
    WebTestClient webTestClient;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    GameRepository gameRepository;


    Player firstPlayer;
    Player secondPlayer;
    Game game;

    @Test
    public void testJoinGame_successFlow() {

        createTwoPlayers();

        webTestClient.get().uri("/game/" + firstPlayer.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody()
                .jsonPath("$.message").isEqualTo("should wait for an opponent");

        webTestClient.get().uri("/game/" + secondPlayer.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()

                .expectBody()

                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.winner").isEmpty()
                .jsonPath("$.turnOfWithId").isEqualTo(firstPlayer.getId())
                .jsonPath("$.over").isEqualTo(false)

                .jsonPath("$.firstPlayer.id").isEqualTo(firstPlayer.getId())
                .jsonPath("$.firstPlayer.name").isEqualTo(firstPlayer.getName())
                .jsonPath("$.firstPlayer.pits[0]").isEqualTo(6)
                .jsonPath("$.firstPlayer.pits[1]").isEqualTo(6)
                .jsonPath("$.firstPlayer.pits[2]").isEqualTo(6)
                .jsonPath("$.firstPlayer.pits[3]").isEqualTo(6)
                .jsonPath("$.firstPlayer.pits[4]").isEqualTo(6)
                .jsonPath("$.firstPlayer.pits[5]").isEqualTo(6)
                .jsonPath("$.firstPlayer.pits[6]").isEqualTo(0)

                .jsonPath("$.secondPlayer.id").isEqualTo(secondPlayer.getId())
                .jsonPath("$.secondPlayer.name").isEqualTo(secondPlayer.getName())
                .jsonPath("$.secondPlayer.pits[0]").isEqualTo(6)
                .jsonPath("$.secondPlayer.pits[1]").isEqualTo(6)
                .jsonPath("$.secondPlayer.pits[2]").isEqualTo(6)
                .jsonPath("$.secondPlayer.pits[3]").isEqualTo(6)
                .jsonPath("$.secondPlayer.pits[4]").isEqualTo(6)
                .jsonPath("$.secondPlayer.pits[5]").isEqualTo(6)
                .jsonPath("$.secondPlayer.pits[6]").isEqualTo(0);

        clearRepos();

    }


    @Test
    public void testJoinGame_playerNotFound() {

        webTestClient.get().uri("/game/notFound")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

    }

    @Test
    public void testJoinGame_threadSafety() throws Exception {

        clearRepos();

        ExecutorService service =
                Executors.newCachedThreadPool();

        List<String> ids = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            Player createdPlayer = playerRepository.save(new Player("player" + i));
            ids.add(createdPlayer.getId());
        }

        Collection<Future> futures = new ArrayList<>(ids.size());

        ids.forEach(
                id -> futures.add(
                        service.submit( () ->
                                webTestClient.get().uri("/game/" + id)
                                        .accept(MediaType.APPLICATION_JSON)
                                        .exchange()
                                        .expectStatus()
                                        .is2xxSuccessful())));


        for (Future future: futures) {
            future.get();
        }


        List<Game> games = gameRepository.findAll();

        assertThat(games.size(), is(100));

        ids.stream().forEach(id -> assertTrue(
                games
                .stream()
                .filter(game -> game.getSecondPlayer().getId().equals(id)
                            || game.getFirstPlayer().getId().equals(id))
                .count() == 1));

        clearRepos();

    }

    @Test
    public void testMakeMovement_basicFlow() {

        createInitializedGame();

        Movement movement = new Movement(game.getId(), firstPlayer.getId(), 0);

        webTestClient.post().uri("/game")
                .body(Mono.just(movement), Movement.class)
                .exchange()
                .expectStatus().isOk()

                .expectBody()

                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.winner").isEmpty()
                .jsonPath("$.turnOfWithId").isEqualTo(firstPlayer.getId())
                .jsonPath("$.over").isEqualTo(false)

                .jsonPath("$.firstPlayer.id").isEqualTo(firstPlayer.getId())
                .jsonPath("$.firstPlayer.name").isEqualTo(firstPlayer.getName())
                .jsonPath("$.firstPlayer.pits[0]").isEqualTo(0)
                .jsonPath("$.firstPlayer.pits[1]").isEqualTo(7)
                .jsonPath("$.firstPlayer.pits[2]").isEqualTo(7)
                .jsonPath("$.firstPlayer.pits[3]").isEqualTo(7)
                .jsonPath("$.firstPlayer.pits[4]").isEqualTo(7)
                .jsonPath("$.firstPlayer.pits[5]").isEqualTo(7)
                .jsonPath("$.firstPlayer.pits[6]").isEqualTo(1)

                .jsonPath("$.secondPlayer.id").isEqualTo(secondPlayer.getId())
                .jsonPath("$.secondPlayer.name").isEqualTo(secondPlayer.getName())
                .jsonPath("$.secondPlayer.pits[0]").isEqualTo(6)
                .jsonPath("$.secondPlayer.pits[1]").isEqualTo(6)
                .jsonPath("$.secondPlayer.pits[2]").isEqualTo(6)
                .jsonPath("$.secondPlayer.pits[3]").isEqualTo(6)
                .jsonPath("$.secondPlayer.pits[4]").isEqualTo(6)
                .jsonPath("$.secondPlayer.pits[5]").isEqualTo(6)
                .jsonPath("$.secondPlayer.pits[6]").isEqualTo(0);

        clearRepos();

    }


    @Test
    public void testMakeMovement_notPlayersTurn() {

        createInitializedGame();

        Movement movement = new Movement(game.getId(), secondPlayer.getId(), 0);

        webTestClient.post().uri("/game")
                .body(Mono.just(movement), Movement.class)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
                .expectBody()
                .jsonPath("$.message").isEqualTo("it's your opponent's turn or game is over");


        clearRepos();

    }

    @Test
    public void testMakeMovement_inexistentGame() {

        Movement movement = new Movement("inexistent", "inexistent", 0);

        webTestClient.post().uri("/game")
                .body(Mono.just(movement), Movement.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("game does not exist in application domain");

        clearRepos();

    }

    @Test
    public void testMakeMovement_illegalMovement_emptyPit() {
        createInitializedGame();
        game.getFirstPlayer().setPits(0, 6, 6, 6, 6, 6, 0);
        gameRepository.save(game);
        Movement movement = new Movement(game.getId(), firstPlayer.getId(), 0);

        webTestClient.post().uri("/game")
                .body(Mono.just(movement), Movement.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("can't sow stones from this pit ID");

        clearRepos();

    }

    @Test
    public void testMakeMovement_badRequest_invalidPitId() {

        Movement movement = new Movement("game", "firstPlayer", 6);

        webTestClient.post().uri("/game")
                .body(Mono.just(movement), Movement.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testMakeMovement_badRequest_emptyGameId() {

        Movement movement = new Movement("", "firstPlayer", 0);

        webTestClient.post().uri("/game")
                .body(Mono.just(movement), Movement.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    private void clearRepos() {
        playerRepository.deleteAll();
        gameRepository.deleteAll();
    }

    private void createTwoPlayers() {
        firstPlayer = new Player();
        firstPlayer.setName("Van Gogh");
        firstPlayer.setPits(6, 6, 6, 6, 6, 6, 0);

        secondPlayer = new Player();
        secondPlayer.setName("Anne Frank");
        secondPlayer.setPits(6, 6, 6, 6, 6, 6, 0);

        firstPlayer = playerRepository.save(firstPlayer);
        secondPlayer = playerRepository.save(secondPlayer);
    }

    private void createInitializedGame() {
        createTwoPlayers();
        game = new Game();
        game.setFirstPlayer(firstPlayer);
        game.setSecondPlayer(secondPlayer);
        game.setOver(false);
        game.setTurnOfWithId(firstPlayer.getId());

        game = gameRepository.save(game);
    }


}
