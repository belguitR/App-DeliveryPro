package fr.uga.miage.l3.exceptions.rest;

public class InvalidStatusRestException extends RuntimeException {
    public InvalidStatusRestException(String message) {
        super(message);
    }
}
