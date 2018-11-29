package com.bol.game.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Document(collection = "players")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Player {

    @Id
    private String id;

    @NotBlank
    private String name;

    public Player(String id, @NotBlank String name) {
        this.id = id;
        this.name = name;
    }

    public Player(@NotBlank String name) {
        this.name = name;
    }

    public Player() {
    }

    private int[] pits;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getPits() {
        return pits;
    }

    public void setPits(int... pits) {
        this.pits = pits;
    }

}

