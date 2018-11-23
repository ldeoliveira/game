package com.bol.game.repositories;

import com.bol.game.pojos.Player;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface PlayerRepository extends MongoRepository<Player, String> {
}
