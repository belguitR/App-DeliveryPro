package fr.uga.miage.l3.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeCamionResponse {
    private String nom;
    private Double volume;
    private Double prixAuKm;
    private String image;
}
