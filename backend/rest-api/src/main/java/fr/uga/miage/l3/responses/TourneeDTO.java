package fr.uga.miage.l3.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.uga.miage.l3.enums.EtatTournee;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Schema(description = "Représente une tournée pour l'API")
public class TourneeDTO {

    private Integer numTournee;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Schema(description = "Date de la tournée", example = "2025-03-26T07:00:00.000Z")
    private Date date;

    @Schema(description = "Distance à parcourir en kilomètres", example = "12")
    private Integer distanceAParcourir;

    @Schema(description = "Montant de la tournée", example = "200")
    private Integer montant;

    @Schema(description = "Temps de montage théorique en minutes", example = "30")
    private Integer tempsDeMontageTheorique;

    @Schema(description = "Temps de montage effectif en minutes", example = "35")
    private Integer tempsDeMontageEffectif;

    @Schema(description = "État de la tournée", example = "EN_COURS")
    private EtatTournee etat;

    @Schema(description = "Numéro de l'équipe livreur associée", example = "1")
    private Integer equipeLivreurId;

    @Schema(description = "Liste des références des commandes de la tournée")
    private List<Integer> commandesIds;
}
