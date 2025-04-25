package fr.uga.miage.l3.models;


import fr.uga.miage.l3.dataTypes.Adresse;
import fr.uga.miage.l3.dataTypes.GeoPosition;
import fr.uga.miage.l3.enums.EtatClient;
import jakarta.persistence.*;

import java.util.List;

import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "client")
public class ClientEntity {

    @Id
    private String code;

    private String email;

    private String prenom;
    private String nom;

    @Embedded
    private Adresse adresse;

    @Embedded
    private GeoPosition coords;

    @Enumerated(EnumType.STRING)
    private EtatClient etat;

    @OneToMany(mappedBy = "client")
    private List<CommandeEntity> commandes;


}