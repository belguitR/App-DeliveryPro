package fr.uga.miage.l3.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class EmployeEntity {

    @Id
    @GeneratedValue(generator = "jdd-generator")
    @GenericGenerator(name = "jdd-generator", strategy = "fr.uga.miage.l3.generator.JddStringGenerator")
    private String idEmploye;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    private String photoURL;


    private String telephone;


    private String email;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateNaissance;
}
