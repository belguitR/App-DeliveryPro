package fr.uga.miage.l3.endpoints;

import fr.uga.miage.l3.responses.EquipeLivreurDTO;
import fr.uga.miage.l3.responses.LivreurDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "EquipeLivreur", description = "Endpoints pour gérer les équipes de livreurs")
@RequestMapping("/api/equipes")
@CrossOrigin(origins = "http://localhost:4200")
public interface EquipeLivreurEndpoints {

    /**
     * Récupère la liste de toutes les équipes de livreurs.
     */
    @Operation(summary = "Récupère toutes les équipes de livreurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des équipes récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune équipe trouvée (si applicable)")
    })
    @GetMapping
    ResponseEntity<List<EquipeLivreurDTO>> getAllEquipes();

    /**
     * Crée une nouvelle équipe de livreurs.
     */
    @Operation(summary = "Crée une nouvelle équipe de livreurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Équipe créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide ou règle métier non respectée"),
            @ApiResponse(responseCode = "409", description = "Conflit si l'équipe existe déjà (optionnel)")
    })
    @PostMapping
    ResponseEntity<EquipeLivreurDTO> createEquipe(@RequestBody List<LivreurDTO> livreurs,
                                                  @RequestParam Integer numEquipe);

    /**
     * Affecte une tournée à une équipe livreur.
     */
    @Operation(summary = "Affecte une tournée à une équipe livreur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tournée affectée avec succès"),
            @ApiResponse(responseCode = "404", description = "Équipe introuvable")
    })
    @PutMapping("/{idEquipe}/affecter-tournee")
    ResponseEntity<Void> affecterTournee(@PathVariable Integer idEquipe);

    /**
     * Libère une équipe livreur.
     */
    @Operation(summary = "Libère une équipe livreur (met son statut à PRÊT)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Équipe libérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Équipe introuvable")
    })
    @PutMapping("/{idEquipe}/liberer")
    ResponseEntity<Void> libererEquipe(@PathVariable Integer idEquipe);

    /**
     * Modifie une équipe de livreurs (changement de livreurs, etc.).
     */
    @Operation(summary = "Modifie une équipe de livreurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Équipe modifiée avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide ou règle métier non respectée"),
            @ApiResponse(responseCode = "404", description = "Équipe introuvable")
    })
    @PutMapping("/{idEquipe}")
    ResponseEntity<EquipeLivreurDTO> modifierEquipe(@PathVariable Integer idEquipe,
                                                    @RequestBody List<LivreurDTO> livreurs);


    /**
     * Supprime une équipe livreur.
     */
    @Operation(summary = "Supprime une équipe de livreurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Équipe supprimée avec succès (pas de contenu)"),
            @ApiResponse(responseCode = "404", description = "Équipe introuvable")
    })
    @DeleteMapping("/{idEquipe}")
    ResponseEntity<Void> suprimeEquipe(@PathVariable Integer idEquipe);
}
