package fr.uga.miage.l3.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "livreur")
public class LivreurEntity extends EmployeEntity {

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateExpirationPermis;

    @OneToOne
    @JoinColumn(name = "disponibilite_id")
    private AgendaEntity disponibilite;

    @ManyToOne
    @JoinColumn(name = "entrepot_id")
    private EntrepotEntity entrepot;

    @ManyToOne
    @JoinColumn(name = "equipe_livreur_id")
    private EquipeLivreurEntity equipeLivreur;
}
