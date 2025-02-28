package fr.uga.miage.l3.models;


import jakarta.persistence.*;

import java.util.Set;

import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "client")
public class ClientEntity {
    @Id
    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String nom;

    private String telephone;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "adresse_id")
    private AdresseEntity adresse;


    @OneToMany(mappedBy = "client")
    private Set<CommandeEntity> commandes;

    @ManyToMany(mappedBy = "clients")
    private Set<TourneeEntity> tournees;


}
