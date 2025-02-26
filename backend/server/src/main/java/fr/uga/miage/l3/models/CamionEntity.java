package fr.uga.miage.l3.models;


import jakarta.persistence.*;

import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
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

