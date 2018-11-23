package com.bol.game.pojos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Movement {

    @NotBlank
    private String gameId;

    @NotBlank
    private String playerId;

    @NotNull
    @Max(5)
    @Min(0)
    private Integer pitId;

    public Movement(@NotBlank String gameId, @NotBlank String playerId, @NotNull @Max(5) @Min(0) Integer pitId) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.pitId = pitId;
    }

    public Movement() {
    }

    public String getGameId() {
        return gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public Integer getPitId() {
        return pitId;
    }

}
