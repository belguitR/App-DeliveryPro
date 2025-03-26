package fr.uga.miage.l3.models;


import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;

import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "camion")
public class CamionEntity {
    @Id
    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String immatriculation;

    @ManyToOne
    @JoinColumn(name = "entrepot_id")
    private EntrepotEntity entrepot;

    @ManyToOne
    @JoinColumn(name = "type_camion_id")
    private TypeCamionEntity typeCamion;

}

