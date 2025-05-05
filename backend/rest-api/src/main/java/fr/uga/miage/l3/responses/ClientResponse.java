package fr.uga.miage.l3.responses;

import fr.uga.miage.l3.dataTypes.Adresse;
import fr.uga.miage.l3.dataTypes.GeoPosition;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponse {
    private String code;
    private String email;
    private String nom;
    private String prenom;
    private Adresse adresse;
    private GeoPosition coords;
    private String etat;
}
