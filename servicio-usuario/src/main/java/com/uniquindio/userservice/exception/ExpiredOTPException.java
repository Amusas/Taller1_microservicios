package com.uniquindio.userservice.exception;

public class ExpiredOTPException extends  RuntimeException{
        public ExpiredOTPException(String message) { super(message); }

}
