package fr.uga.miage.l3.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CamionResponse {
    private String code;
    private String immatriculation;
    private String entrepotNom;
    private Long kilometrage;
    private TypeCamionResponse typeCamion;
}