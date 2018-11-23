package com.bol.game.repositories;

import com.bol.game.pojos.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<Game, String> {
}
