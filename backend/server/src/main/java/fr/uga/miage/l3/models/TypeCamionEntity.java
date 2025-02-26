package fr.uga.miage.l3.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.Set;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TypeCamionEntity {
    @Id
    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private Double prixKm;

    private String image;

    @OneToMany(mappedBy = "typeCamion")
    private Set<CamionEntity> camions;


}
