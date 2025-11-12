package com.luciano.blogpersonal.common.exception;

import org.springframework.http.HttpStatus;

public class BlogApiException extends RuntimeException {

    private final HttpStatus status;
    private final String message;

    public BlogApiException(HttpStatus status, String message){
        super(message);
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus(){
        return status;
    }
}
