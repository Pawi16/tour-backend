package dev.pawin.tour_pro.common;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import dev.pawin.tour_pro.common.exception.EntityNotFound;

@RestControllerAdvice
public class ResponseAdviser extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResponseAdviser.class);

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var text = "Invalid arguments request";
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                text);
        Map<String, Object> properties = new HashMap<>();
        var invalidArgumentList = e.getBindingResult().getFieldErrors();
        for (var oe : invalidArgumentList) {
            properties.put(oe.getField(), oe.getDefaultMessage());
        }

        detail.setProperty("arguments", properties);
        logger.error("Invalid arguments request: {}", detail);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
    }

    // Global handling
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> globalExceptionHandling(Exception e) {

        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                e.getMessage());
        logger.error(e.getMessage(), e);
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(detail);
    }

    // EntityNotFound
    @ExceptionHandler(EntityNotFound.class)
    protected ResponseEntity<Object> globalExceptionHandling(EntityNotFound e) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                e.getMessage());
        logger.error("EntityNotFound: {}", detail);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detail);
    }

}
