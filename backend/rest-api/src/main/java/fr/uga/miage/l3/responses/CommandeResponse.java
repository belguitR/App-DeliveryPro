package fr.uga.miage.l3.responses;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeResponse {
    private String reference;
    private String etat;
    private LocalDateTime dateDeCreation;
    private Integer note;
    private Integer tdmTheorique;
    private String commentaire;
    private LocalDateTime dateDeLivraison;
    private ClientResponse client;
    private Set<String> ligneReferences;
}
