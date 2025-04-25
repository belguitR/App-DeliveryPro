package fr.uga.miage.l3.requests;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandePatchRequest {
    private String etat;
    private LocalDateTime dateDeLivraison;
}
