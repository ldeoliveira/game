package com.bol.game.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED, reason = "it's your opponent's turn or game is over")
public class MovementNotAllowedException extends RuntimeException {
}
