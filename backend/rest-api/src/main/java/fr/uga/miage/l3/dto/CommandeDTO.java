package fr.uga.miage.l3.dto;



import fr.uga.miage.l3.enums.EtatCommande;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "Représente une commande pour l'API")
public class CommandeDTO {

    private Integer reference;
    private EtatCommande etat;
    private Date date;
    private Date horairePreferable;
    private Date dateLivre;
    private Date dateLivraisonEstimee;
    private Double montantTotal;
    private ClientDTO client;

}
