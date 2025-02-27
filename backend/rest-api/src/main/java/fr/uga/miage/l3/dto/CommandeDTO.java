package fr.uga.miage.l3.dto;



import fr.uga.miage.l3.enums.EtatCommande;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Représente une commande pour l'API")
public class CommandeDTO {

    @Schema(description = "Référence unique de la commande")
    private String reference;

    @Schema(description = "État actuel de la commande")
    private EtatCommande etat;

    @Schema(description = "Date de la commande")
    private String date;

    @Schema(description = "Horaire préférée pour la livraison")
    private String horairePreferable;

    @Schema(description = "Date de livraison effective")
    private String dateLivre;

    @Schema(description = "Date de livraison estimée")
    private String dateLivraisonEstimee;

    @Schema(description = "Montant total de la commande")
    private Double montantTotal;

    @Schema(description = "Identifiant du client")
    private Integer clientId;

    @Schema(description = "Numéro de la tournée associée")
    private Integer tourneeId;
}
