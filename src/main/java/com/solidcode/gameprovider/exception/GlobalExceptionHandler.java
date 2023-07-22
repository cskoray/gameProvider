package com.solidcode.gameprovider.exception;

import static com.solidcode.gameprovider.exception.ErrorType.DUPLICATE_GAME;
import static com.solidcode.gameprovider.exception.ErrorType.INVALID_FIELD;
import static java.util.Collections.singletonList;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Order(HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<?> duplicateUserExceptionHandling(Exception exception, WebRequest request) {

    return createSingleErrorListResponse(DUPLICATE_GAME, exception, null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> invalidArgsExceptionHandling(MethodArgumentNotValidException exception,
      WebRequest request) {

    String field = exception.getBindingResult().getFieldErrors().get(0).getField();
    return createSingleErrorListResponse(INVALID_FIELD, exception, field);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<?> constraintViolationExceptionHandling(
      ConstraintViolationException exception,
      WebRequest request) {

    Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
    Error error = new Error();
    ConstraintViolation<?> constraintViolation = constraintViolations.stream().toList().get(0);
    error.setField(constraintViolation.getInvalidValue().toString());
    error.setCode("10004");
    error.setDescription(constraintViolation.getMessage());
    return new ResponseEntity<>(createErrorList(singletonList(error)), BAD_REQUEST);
  }

  @ExceptionHandler(GameNotFoundException.class)
  public ResponseEntity<?> invalidParamExceptionHandling(GameNotFoundException exception,
      WebRequest request) {

    return createSingleErrorListResponse(exception.getErrorType(), exception, exception.getField());
  }

  private ResponseEntity<ErrorList> createSingleErrorListResponse(ErrorType errorType,
      Exception exception, String field) {

    Error error = createError(errorType, field);
    return new ResponseEntity<>(createErrorList(singletonList(error)), errorType.getHttpStatus());
  }

  private Error createError(ErrorType errorType, String field) {

    return Error.builder()
        .code(errorType.getCode())
        .description(errorType.getMessage())
        .field(field)
        .build();
  }

  private ErrorList createErrorList(List<Error> errorList) {
    return ErrorList.builder()
        .errors(errorList)
        .build();
  }
}
