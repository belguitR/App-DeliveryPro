package fr.uga.miage.l3.responses;


import fr.uga.miage.l3.enums.StatusEquipeLivreur;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Représente une équipe de livreurs pour l'API")
public class EquipeLivreurDTO {

    @Schema(description = "Numéro unique de l'équipe livreur")
    private Integer numEquipe;

    @Schema(description = "Statut de l'équipe livreur")
    private StatusEquipeLivreur status;

    @Schema(description = "Liste des identifiants des livreurs de l'équipe")
    private List<String> livreursIds;

    @Schema(description = "Liste des numéros des tournées assignées")
    private List<Integer> tourneesIds;
}
