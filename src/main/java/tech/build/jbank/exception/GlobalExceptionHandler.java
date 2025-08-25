package tech.build.jbank.exception;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JbankException.class)
    public ProblemDetail handleJbankException(JbankException e) {
        return e.toProblemDetail();
    }
}
