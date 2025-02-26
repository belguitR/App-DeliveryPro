package fr.uga.miage.l3.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

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
