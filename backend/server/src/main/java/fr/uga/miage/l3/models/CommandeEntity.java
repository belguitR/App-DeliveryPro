package fr.uga.miage.l3.models;


import fr.uga.miage.l3.enums.EtatCommande;
import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "commande")
public class CommandeEntity {
    @Id
    private String reference;

    @Enumerated(EnumType.STRING)
    private EtatCommande etat;

    private LocalDateTime dateDeCreation;

    private Integer note;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String commentaire;

    private LocalDateTime dateDeLivraison;

    @ManyToOne(fetch = FetchType.LAZY)
    private ClientEntity client;

    @OneToMany(mappedBy = "commande", fetch = FetchType.LAZY)
    private Set<LigneEntity> lignes;

    @ManyToOne
    private TourneeEntity tournee;

}