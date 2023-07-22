package com.solidcode.gameprovider.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ErrorType {

  GAME_NOT_FOUND("10001", "Game cannot be found.", BAD_REQUEST),
  DUPLICATE_GAME("10002", "Game exists.", BAD_REQUEST),
  INVALID_FIELD("10003", "Invalid field.", BAD_REQUEST);

  private String code;
  private String message;
  private HttpStatus httpStatus;
}
