package fr.uga.miage.l3.models;


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
public class LivreurEntity {
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

    @OneToOne
    @JoinColumn(name = "disponibilite_id")
    private AgendaEntity disponibilite;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateExpirationPermis;

    @ManyToOne
    @JoinColumn(name = "entrepot_id")
    private EntrepotEntity entrepot;

    @ManyToOne
    @JoinColumn(name = "equipe_livreur_id")
    private EquipeLivreurEntity equipeLivreur;
}
