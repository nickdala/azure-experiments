package com.nickthecloudguy.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@SpringBootApplication
public class IdentityApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdentityApplication.class, args);
	}

    // Controller advice to handle exceptions
    @ControllerAdvice
    public class ExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler {
        @ExceptionHandler(Exception.class)
        public ProblemDetail exceptionHandler(Exception ex) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
            return pd;
        }
    }
}
