package fr.uga.miage.l3.models;


import fr.uga.miage.l3.enums.TypeCreneau;
import jakarta.persistence.*;

import java.util.Date;

import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CreneauEntity {
    @Id
    @Column(nullable = false)
    private String idCreneau;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDebut;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeCreneau type;

    @ManyToOne
    @JoinColumn(name = "agenda_id")
    private AgendaEntity agenda;

}
