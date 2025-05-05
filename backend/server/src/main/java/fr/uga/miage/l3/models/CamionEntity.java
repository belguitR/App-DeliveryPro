package fr.uga.miage.l3.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "camion")
public class CamionEntity {

    @Id
    private String code;

    private String immatriculation;

    private Long kilometrage;

    @ManyToOne
    @JoinColumn(name = "entrepot")
    private EntrepotEntity entrepot;

    @OneToMany(mappedBy = "camion")
    private List<TourneeEntity> tournees;


    @ManyToOne
    @JoinColumn(name = "type_camion_id")
    private TypeCamionEntity typeCamion;
}