package com.bol.game.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "game does not exist in application domain")
public class InexistentGameException extends RuntimeException {
}