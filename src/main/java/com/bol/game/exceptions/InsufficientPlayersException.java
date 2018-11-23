package com.bol.game.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.ACCEPTED, reason = "should wait for an opponent")
public class InsufficientPlayersException extends RuntimeException {
}
