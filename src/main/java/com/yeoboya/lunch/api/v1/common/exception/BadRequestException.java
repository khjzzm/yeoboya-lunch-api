package com.yeoboya.lunch.api.v1.common.exception;

public class BadRequestException extends LunchException {
    public BadRequestException(String message){
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }


}