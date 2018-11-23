package com.bol.game.controllers;


import com.bol.game.pojos.Player;
import com.bol.game.repositories.PlayerRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PlayerControllerTest {

    public static final String PLAYER_NAME = "playerName";
    public static final String PLAYER_ID = "playerId";
    PlayerController playerController;

    @Mock
    PlayerRepository playerRepository;

    @Before
    public void setup() {
        playerController = new PlayerController(playerRepository);
    }

    @Test
    public void testCreatePlayer() {
        Player player = new Player();
        player.setName(PLAYER_NAME);

        Mockito.when(playerRepository.save(player)).thenReturn(
                new Player(PLAYER_ID, player.getName()));

        Player createdPlayer = playerController.createPlayer(player);

        assertThat(createdPlayer.getId(), is(PLAYER_ID));
        assertThat(createdPlayer.getName(), is(player.getName()));
    }
}
