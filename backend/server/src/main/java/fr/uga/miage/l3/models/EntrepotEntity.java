package fr.uga.miage.l3.models;


import jakarta.persistence.*;

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
@Table(name = "entrepot")
public class EntrepotEntity {
    @Id
    @Column(nullable = false)
    private String nom;

    @OneToMany(mappedBy = "entrepot")
    private Set<LivreurEntity> livreurs;

    @OneToMany(mappedBy = "entrepot")
    private Set<CommandeEntity> commandes;

    @OneToMany(mappedBy = "entrepot")
    private Set<CamionEntity> camions;


}
