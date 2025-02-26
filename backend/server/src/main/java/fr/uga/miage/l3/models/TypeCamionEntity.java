package fr.uga.miage.l3.models;


import jakarta.persistence.*;

import java.util.Set;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "type_camion")
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
