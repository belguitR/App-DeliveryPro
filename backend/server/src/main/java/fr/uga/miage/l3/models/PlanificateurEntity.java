package fr.uga.miage.l3.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "planificateur")
public class PlanificateurEntity extends EmployeEntity {



//    @OneToMany(mappedBy = "planificateur")
//    private List<TourneeEntity> tournees;
//
//    @OneToMany(mappedBy = "planificateur")
//    private Set<JourneeEntity> jours;

}