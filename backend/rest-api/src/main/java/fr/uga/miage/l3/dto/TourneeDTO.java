package fr.uga.miage.l3.dto;


import fr.uga.miage.l3.enums.EtatTournee;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Schema(description = "Représente une tournée pour l'API")
public class TourneeDTO {

    @Schema(description = "Numéro unique de la tournée")
    private Integer numTournee;

    @Schema(description = "Date de la tournée")
    private Date date;

    @Schema(description = "Distance à parcourir en kilomètres")
    private Integer distanceAParcourir;

    @Schema(description = "Montant de la tournée")
    private Integer montant;

    @Schema(description = "Temps de montage théorique en minutes")
    private Integer tempsDeMontageTheorique;

    @Schema(description = "Temps de montage effectif en minutes")
    private Integer tempsDeMontageEffectif;

    @Schema(description = "État de la tournée")
    private EtatTournee etat;

    @Schema(description = "Numéro de l'équipe livreur associée")
    private Integer equipeLivreurId;

    @Schema(description = "Liste des références des commandes de la tournée")
    private List<Integer> commandes;
}
