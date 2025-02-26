package fr.uga.miage.l3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "Représente un livreur pour l'API")
public class LivreurDTO {

    @Schema(description = "Identifiant unique du livreur")
    private String idEmploye;

    @Schema(description = "Nom du livreur")
    private String nom;

    @Schema(description = "Prénom du livreur")
    private String prenom;

    @Schema(description = "Date de naissance du livreur")
    private Date dateNaissance;

    @Schema(description = "Date d'expiration du permis")
    private Date dateExpirationPermis;


}


