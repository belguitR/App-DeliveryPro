package fr.uga.miage.l3.models;


import fr.uga.miage.l3.enums.EtatTournee;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tournee")
public class TourneeEntity {
    @Id
    private String reference;

    @Enumerated(EnumType.STRING)
    private EtatTournee etat;

    private String lettre;
    @Column(columnDefinition = "DOUBLE PRECISION DEFAULT 0.0")
    private double distanceAParcourir;
    private Integer tempsAssemblageReel;

    @ManyToOne
    private PlanificateurEntity planificateur;

    @ManyToOne
    private EquipeLivreursEntity equipeLivreurs;

    @ManyToOne
    private CamionEntity camion;

    @ManyToOne
    private JourneeEntity jour;

    @OneToMany(mappedBy = "tournee")
    @OrderColumn(name = "position")
    private List<CommandeEntity> commandes;

}