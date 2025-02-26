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

    @OneToOne
    @JoinColumn(name = "agenda_id")
    private AgendaEntity agenda;

    @OneToMany(mappedBy = "equipeLivreur")
    private List<LivreurEntity> livreurs;

    @OneToMany(mappedBy = "equipeLivreur")
    private List<TourneeEntity> tournees;


}