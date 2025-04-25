package fr.uga.miage.l3.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "livreur")
public class LivreurEntity extends EmployeEntity {

    private boolean affecte;
    private boolean aPermis;

    @ManyToOne
    @JoinColumn(name = "equipe_livreur_id")
    private EquipeLivreursEntity equipeLivreurs;

}