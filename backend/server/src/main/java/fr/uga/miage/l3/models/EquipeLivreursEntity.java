package fr.uga.miage.l3.models;

import fr.uga.miage.l3.enums.StatusEquipeLivreurs;
import jakarta.persistence.*;
import java.util.Set;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "equipe_livreur")
public class EquipeLivreursEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idEquipe;

    @Enumerated(EnumType.STRING)
    private StatusEquipeLivreurs status;

    @OneToMany(mappedBy = "equipeLivreurs", fetch = FetchType.EAGER)
    private Set<LivreurEntity> livreurs;

    @OneToMany(mappedBy = "equipeLivreurs")
    private Set<TourneeEntity> tournees;

}