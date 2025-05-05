package fr.uga.miage.l3.models;


import fr.uga.miage.l3.dataTypes.Adresse;
import fr.uga.miage.l3.dataTypes.GeoPosition;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "entrepot")
public class EntrepotEntity {
    @Id
    private String nom;

    private String lettre;

    private String photo;

    @Embedded
    private Adresse adresse;

    @Embedded
    private GeoPosition coords;

    @OneToMany(mappedBy = "entrepot")
    private List<CamionEntity> camions;


}