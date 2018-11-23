package com.bol.game.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "player does not exist in application domain")
public class InexistentPlayerException extends RuntimeException {
}
