package fr.uga.miage.l3.requests;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourneeRequest {
    private String reference;
    private String etat;
    private String lettre;
    private Double distanceAParcourir;
    private Integer tempsAssemblageReel;
    private Integer equipeLivreursId;
    private String camionCode;
    private String journeeReference;
    private List<String> commandeReferences;
}
