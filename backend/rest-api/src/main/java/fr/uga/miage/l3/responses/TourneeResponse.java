package fr.uga.miage.l3.responses;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourneeResponse {
    private String reference;
    private String etat;
    private String lettre;
    private Double distanceAParcourir;
    private Integer tempsAssemblageReel;
    private String equipeLivreursId;
    private CamionResponse camion;
    private Double dureeTheorique;
    private Double cout;
    private String journeeReference;
    private List<String> commandeReferences;
}
