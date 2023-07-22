package com.solidcode.gameprovider.exception;


import lombok.Getter;

@Getter
public class GameNotFoundException extends RuntimeException {

  private ErrorType errorType;
  private String field;

  public GameNotFoundException(ErrorType errorType, String field) {
    super(errorType.getMessage());
    this.errorType = errorType;
    this.field = field;
  }
}
