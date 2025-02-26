package fr.uga.miage.l3.models;


import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PlanificateurEntity {
    @Id
    @Column(nullable = false)
    private String idEmploye;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    private String photoURL;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateNaissance;

    @OneToMany(mappedBy = "planificateur")
    private Set<TourneeEntity> tournees;


}
