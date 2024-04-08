package com.mikolaj.e_library.DTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Map;

public class ResponseUtil {
    public static ResponseEntity<Response> idNotFoundResponse(Class objectClass){
        return ResponseEntity.badRequest().body(
                Response.builder()
                        .message("id of class: " + objectClass.getSimpleName() + " not found")
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build()
        );
    }

    public static ResponseEntity<Response> emailNotFoundResponse(String email){
        return ResponseEntity.badRequest().body(
                Response.builder()
                        .message("account with email address: " + email + " not found")
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build()
        );
    }

    public static ResponseEntity<Response> somethingWentWrongResponse(String message){
        return ResponseEntity.ok(
                Response.builder()
                        .message("something went wrong" + message)
                        .timestamp(LocalDateTime.now())
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build()
        );
    }

    public static ResponseEntity<Response> badRequestResponse(String message){
        return ResponseEntity.badRequest().body(
                Response.builder()
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build()
        );

    }
    public static ResponseEntity<Response> okResponse(String message, String dataName, Object data){
        return ResponseEntity.ok(
                Response.builder()
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .httpStatus(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .data(Map.of(dataName, data))
                        .build()
        );
    }


}
