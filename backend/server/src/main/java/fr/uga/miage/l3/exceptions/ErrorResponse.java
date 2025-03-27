package fr.uga.miage.l3.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String error;   // ex : BAD_REQUEST, NOT_FOUND, ...
    private String message; // Le message d'erreur
}
