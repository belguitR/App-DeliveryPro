package fr.uga.miage.l3.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "Employe")
public abstract class EmployeEntity {
    @Id
    private String trigramme;

    @Column(unique = true)
    private String email;

    private String nom;
    private String prenom;

    private String photo;

    @Column(unique = true)
    private String telephone;

    @ManyToOne
    @JoinColumn(name = "entrepot_nom")
    private EntrepotEntity entrepot;

}