package fr.uga.miage.l3.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "planificateur")
public class PlanificateurEntity extends EmployeEntity {

    @OneToMany(mappedBy = "planificateur")
    private Set<TourneeEntity> tournees;
}
