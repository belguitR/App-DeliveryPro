package fr.uga.miage.l3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Représente un entrepot pour l'API")
public class EntrepotDTO {

    @Schema(description = "Nom de l'entrepôt")
    private String nom;

    // pour maintenant ca suffit
}
