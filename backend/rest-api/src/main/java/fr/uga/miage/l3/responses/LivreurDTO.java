package fr.uga.miage.l3.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Date;

@Data
@Schema(description = "Représente un livreur pour l'API")
public class LivreurDTO {

    @Schema(description = "Identifiant unique du livreur (employé) commance par JDD", example = "jdd1")
    private String idEmploye;

    @NotNull
    @Schema(description = "Nom du livreur", example = "Dupont")
    private String nom;

    @NotNull
    @Schema(description = "Prénom du livreur", example = "Jean")
    private String prenom;

    @NotNull
    @Schema(description = "Date de naissance du livreur", example = "1985-06-15")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateNaissance;


    @NotNull
    @Schema(description = "URL de la photo du livreur", example = "http://example.com/photos/dupont.jpg")
    private String photoURL;

    @NotNull
    @Schema(description = "Numéro de téléphone du livreur", example = "0601020304")
    private String telephone;

    @NotNull
    @Schema(description = "Email du livreur", example = "jean.dupont@example.com")
    private String email;

    @Schema(description = "Indique si le livreur est affecté à une tournée", example = "true")
    private boolean affecte;

    @Schema(description = "Indique si le livreur possède un permis valide", example = "true")
    private boolean APermis;

    @Schema(description = "Entrepôt de rattachement du livreur")
    private EntrepotDTO entrepot;

    @Schema(description = "Equipe associée au livreur")
    private EquipeLivreurDTO equipeLivreur;

}
