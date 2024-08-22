package com.zunigatomas.spring_mongo_hotel_api.exception;

public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
