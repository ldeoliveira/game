package com.bol.game.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "can't sow stones from this pit ID")
public class IllegalMovementException extends RuntimeException {
}
