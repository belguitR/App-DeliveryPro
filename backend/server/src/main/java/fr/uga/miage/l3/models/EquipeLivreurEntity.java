package fr.uga.miage.l3.models;


import fr.uga.miage.l3.enums.StatusEquipeLivreur;
import jakarta.persistence.*;

import java.util.List;

import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "equipe_livreur")
public class EquipeLivreurEntity {
    @Id
    @Column(nullable = false)
    private Integer numEquipe;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEquipeLivreur status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "agenda_id")
    private AgendaEntity agenda;

    @ManyToMany
    @JoinTable(name = "equipe_livreur_livreur",
            joinColumns = @JoinColumn(name = "equipe_livreur_id"),
            inverseJoinColumns = @JoinColumn(name = "livreur_id"))
    private List<LivreurEntity> livreurs;

    @OneToMany(mappedBy = "equipeLivreur")
    private List<TourneeEntity> tournees;


}