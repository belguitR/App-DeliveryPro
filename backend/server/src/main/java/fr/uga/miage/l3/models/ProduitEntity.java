package fr.uga.miage.l3.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProduitEntity {
    @Id
    private String reference;

    private String photo;
    private String titre;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String description;
    private double prix;

    private Boolean optionMontage;
    private Integer tdmTheorique;
}