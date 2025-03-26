package fr.uga.miage.l3.responses;

import fr.uga.miage.l3.enums.EtatCommande;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Data
@Schema(description = "Représente une commande pour l'API")
public class CommandeDTO {

    @NotNull
    @Schema(description = "Référence unique de la commande (entier, obligatoire)", example = "123", required = true)
    private Integer reference;

    @Schema(description = "Etat de la commande", example = "OUVERTE")
    private EtatCommande etat;

    @Schema(description = "Date de création de la commande", example = "2023-03-01T10:00:00Z")
    private Date date;

    @Schema(description = "Horaire préféré pour la livraison", example = "2023-03-01T12:00:00Z")
    private Date horairePreferable;

    @Schema(description = "Date à laquelle la commande a été livrée", example = "2023-03-01T14:00:00Z")
    private Date dateLivre;

    @Schema(description = "Date estimée de livraison", example = "2023-03-01T13:00:00Z")
    private Date dateLivraisonEstimee;

    @Schema(description = "Montant total de la commande", example = "150.75")
    private Double montantTotal;

    @Schema(description = "Détails du client associé à la commande")
    private ClientDTO client;
}
