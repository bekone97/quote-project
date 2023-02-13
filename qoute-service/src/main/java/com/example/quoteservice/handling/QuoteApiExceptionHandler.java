package com.example.quoteservice.handling;

import com.example.quoteservice.exception.NotUniqueResourceException;
import com.example.quoteservice.exception.NotValidCredentialsException;
import com.example.quoteservice.exception.NotValidTokenException;
import com.example.quoteservice.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class QuoteApiExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ApiErrorResponse resourceNotFoundExceptionHandler(HttpServletRequest request,
                                                             ResourceNotFoundException exception) {
        log.error("The {}. There is no object in database : {}.Url of request : {}",
                exception.getClass().getSimpleName(), exception.getMessage(), request.getRequestURL());
        return new ApiErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(value = NotUniqueResourceException.class)
    @ResponseStatus(BAD_REQUEST)
    public ApiErrorResponse notUniqueResourceExceptionHandler(HttpServletRequest request,
                                                              NotUniqueResourceException exception){
        log.error("The {}. There is an object with the same fields in database : {}.Url of request : {}",
                exception.getClass().getSimpleName(), exception.getMessage(), request.getRequestURL());
        return new ApiErrorResponse(exception.getMessage());
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ValidationErrorResponse methodArgumentNotValidExceptionHandler(HttpServletRequest request,
                                                                          MethodArgumentNotValidException exception) {

        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse(
                exception.getBindingResult().getAllErrors().stream()
                        .map(FieldError.class::cast)
                        .map(fieldError -> new ValidationMessage(fieldError.getField(), fieldError.getDefaultMessage()))
                        .collect(Collectors.toList())
        );

        log.warn("The {}. Validation messages : {} .Url of request : {}",
                exception.getClass().getSimpleName(), validationErrorResponse.getMessage(), request.getRequestURL());
        return validationErrorResponse;
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ValidationErrorResponse constraintViolationExceptionHandler(HttpServletRequest request,
                                                                       ConstraintViolationException exception) {
        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse(
                exception.getConstraintViolations().stream()
                        .map(violation -> new ValidationMessage(violation.getPropertyPath().toString(), violation.getMessage()))
                        .collect(Collectors.toList())
        );
        log.warn("The {}. Validation messages : {} .Url of request : {}",
                exception.getClass().getSimpleName(), validationErrorResponse.getMessage(), request.getRequestURL());

        return validationErrorResponse;
    }

    @ExceptionHandler(value = NotValidCredentialsException.class)
    @ResponseStatus(FORBIDDEN)
    public ApiErrorResponse notValidCredentialsExceptionHandler(HttpServletRequest request
            , NotValidCredentialsException exception) {
        log.error("The {}. There is no object in database : {}.Url of request : {}",
                exception.getClass().getSimpleName(), exception.getMessage(), request.getRequestURL());
        return new ApiErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(value = NotValidTokenException.class)
    @ResponseStatus(FORBIDDEN)
    public ApiErrorResponse notValidTokenExceptionHandler(HttpServletRequest request
            , NotValidCredentialsException exception) {
        log.error("The {}. There is no object in database : {}.Url of request : {}",
                exception.getClass().getSimpleName(), exception.getMessage(), request.getRequestURL());
        return new ApiErrorResponse(exception.getMessage());
    }

}
