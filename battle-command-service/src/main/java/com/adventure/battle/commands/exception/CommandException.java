package com.adventure.battle.commands.exception;

/**
 * CustomException Handler
 */
public class CommandException extends RuntimeException{
    public int statusCode;
    public CommandException(int statusCode,String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
