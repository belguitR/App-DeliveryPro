package fr.uga.miage.l3.responses;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdresseDTO {
    private Long id;
    private String adressePostal;
    private String complementAdresse;
    private String ville;
    private Integer codePostal;
    private double latitude;
    private double longitude;
}
