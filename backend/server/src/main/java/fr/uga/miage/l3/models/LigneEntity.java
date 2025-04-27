package fr.uga.miage.l3.models;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Lignes")
public class LigneEntity {
    @Id
    private String reference;

    private Integer quantite;

    private Boolean optionAssemblage;

    private Integer tdmTheorique;

    private double montant;

    @ManyToOne
    private ProduitEntity produit;

    @ManyToOne
    private CommandeEntity commande;


    public void setMontant(){
        this.montant = this.quantite*this.produit.getPrix();
    }
}