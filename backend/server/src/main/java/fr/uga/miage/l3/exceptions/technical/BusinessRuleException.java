package fr.uga.miage.l3.exceptions.technical;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}