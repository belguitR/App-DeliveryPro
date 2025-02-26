package fr.uga.miage.l3.models;


import fr.uga.miage.l3.enums.EtatCommande;
import jakarta.persistence.*;

import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CommandeEntity {
    @Id
    @Column(nullable = false)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatCommande etat;

    private String date;
    private String horairePreferable;
    private String dateLivre;
    private String dateLivraisonEstimee;
    private Double montantTotal;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @ManyToOne
    @JoinColumn(name = "tournee_id")
    private TourneeEntity tournee;

    @ManyToOne
    @JoinColumn(name = "entrepot_id")
    private EntrepotEntity entrepot;


}
