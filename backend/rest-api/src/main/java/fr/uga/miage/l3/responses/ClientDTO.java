package fr.uga.miage.l3.responses;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO {
    private String code;
    private String email;
    private String prenom;
    private String nom;
    private String telephone;
    private AdresseDTO adresse;
}
