package fr.uga.miage.l3.models;


import fr.uga.miage.l3.enums.EtatCommande;
import jakarta.persistence.*;

import lombok.*;

import java.util.Date;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "commande")
public class CommandeEntity {
    @Id
    @Column(nullable = false)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatCommande etat;

    private Date date;
    private Date horairePreferable;
    private Date dateLivre;
    private Date dateLivraisonEstimee;
    private Double montantTotal;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @ManyToOne
    @JoinColumn(name = "tournee_id")
    private TourneeEntity tournee;

    @ManyToOne
    @JoinColumn(name = "entrepot_id")
    private EntrepotEntity entrepot;


}
