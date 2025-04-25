package fr.uga.miage.l3.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "type_camion")
public class TypeCamionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private Double volume;
    private Double prixAuKm;
    private String image;

    @OneToMany(mappedBy = "typeCamion")
    private Set<CamionEntity> camions;
}
