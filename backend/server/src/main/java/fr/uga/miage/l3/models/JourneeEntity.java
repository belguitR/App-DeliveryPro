package fr.uga.miage.l3.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "journee")
public class JourneeEntity {

    @Id
    private String reference;

    private LocalDate date;

    @ManyToOne
    private PlanificateurEntity planificateur;

    private Double coutTotal;

    @OneToMany(mappedBy = "jour")
    private List<TourneeEntity> tournees;
}