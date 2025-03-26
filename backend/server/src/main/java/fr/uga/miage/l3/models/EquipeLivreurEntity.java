package fr.uga.miage.l3.models;


import fr.uga.miage.l3.enums.StatusEquipeLivreur;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
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
@Table(name = "equipe_livreur")
public class EquipeLivreurEntity {
    @Id
    @Column(nullable = false)
    private Integer numEquipe;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEquipeLivreur status;



    @ElementCollection
    private Set<LocalDate> datesAffectes;



    @OneToMany(mappedBy = "equipeLivreur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LivreurEntity> livreurs;


    @OneToMany(mappedBy = "equipeLivreur")
    private List<TourneeEntity> tournees;


    public void addLivreur(LivreurEntity livreur) {
        if (!livreurs.contains(livreur)) {
            livreurs.add(livreur);
            livreur.setEquipeLivreur(this);
        }
    }

    public void removeLivreur(LivreurEntity livreur) {
        if (livreurs.remove(livreur)) {
            livreur.setEquipeLivreur(null);
        }
    }

}