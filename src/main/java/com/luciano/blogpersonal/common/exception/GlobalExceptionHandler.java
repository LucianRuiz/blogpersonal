package com.luciano.blogpersonal.common.exception;

import com.luciano.blogpersonal.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;

@ControllerAdvice

public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

    @ExceptionHandler (ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException (ResourceNotFoundException exception){
        ApiResponse apiResponse = ApiResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }


   @ExceptionHandler (BlogApiException.class)
   public ResponseEntity<ApiResponse> handleBlogApiException (BlogApiException exception){
        ApiResponse apiResponse = ApiResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .build();

        return new ResponseEntity<>(apiResponse, exception.getStatus());
   }



   @ExceptionHandler (AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException (AccessDeniedException exception){
       ApiResponse apiResponse = ApiResponse.builder()
               .success(false)
               .message("No tiene permisos para realizar esta acción")
               .build();

       return new ResponseEntity<> (apiResponse,HttpStatus.FORBIDDEN);
   }

   @ExceptionHandler (Exception.class)
    public ResponseEntity<ApiResponse> handleException (Exception exception){
        ApiResponse apiResponse = ApiResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
   }

}
