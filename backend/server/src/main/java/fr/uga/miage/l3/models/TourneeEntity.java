package fr.uga.miage.l3.models;


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
@EqualsAndHashCode
public class TourneeEntity {
    @Id
    @Column(nullable = false)
    private Integer numTournee;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    private Integer distanceAParcourir;
    private Integer montant;
    private Integer tempsDeMontageTheorique;
    private Integer tempsDeMontageEffectif;

    @ManyToOne
    @JoinColumn(name = "planificateur_id")
    private PlanificateurEntity planificateur;

    @ManyToOne
    @JoinColumn(name = "equipe_livreur_id")
    private EquipeLivreurEntity equipeLivreur;

    @OneToMany(mappedBy = "tournee")
    private Set<CommandeEntity> commandes;

    @ManyToMany
    @JoinTable(
            name = "tournee_client",
            joinColumns = @JoinColumn(name = "tournee_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private List<ClientEntity> clients;
}
