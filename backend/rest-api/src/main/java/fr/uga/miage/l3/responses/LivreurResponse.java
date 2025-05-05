package fr.uga.miage.l3.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LivreurResponse {
    private String trigramme;
    private String nom;
    private String prenom;
    private String email;
    private String photo;
    private String telephone;
    private boolean affecte;
    private boolean aPermis;
    private Integer equipeId;
    private String entrepotNom;
}
