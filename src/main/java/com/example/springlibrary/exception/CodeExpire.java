package com.example.springlibrary.exception;

public class CodeExpire extends RuntimeException{
    public CodeExpire(String message){
        super(message);
    }
}
