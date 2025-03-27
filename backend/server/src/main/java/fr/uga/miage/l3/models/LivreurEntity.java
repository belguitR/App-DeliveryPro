package fr.uga.miage.l3.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "livreur")
public class LivreurEntity extends EmployeEntity {



    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "entrepot_id")
    private EntrepotEntity entrepot;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "equipe_livreur_id")
    private EquipeLivreurEntity equipeLivreur;

    private boolean affecte;
    private boolean aPermis;
}
