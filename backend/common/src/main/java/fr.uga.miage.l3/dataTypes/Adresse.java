package fr.uga.miage.l3.dataTypes;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Adresse {
    private String adresse;
    private String codePostal;
    private String ville;

}