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

    @Operation(summary = "Récupère toutes les équipes de livreurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des équipes récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune équipe trouvée")
    })
    @GetMapping
    ResponseEntity<List<EquipeLivreurDTO>> getAllEquipes();

    @Operation(summary = "Crée une nouvelle équipe de livreurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Équipe créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide ou règle métier non respectée")
    })
    @PostMapping
    ResponseEntity<EquipeLivreurDTO> createEquipe(@RequestBody List<LivreurDTO> livreursDTO);

    /*@Operation(summary = "Modifie une équipe de livreurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Équipe modifiée avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide ou règle métier non respectée"),
            @ApiResponse(responseCode = "404", description = "Équipe introuvable")
    })
    @PutMapping("/{idEquipe}")
    ResponseEntity<EquipeLivreurDTO> modifierEquipe(@PathVariable Integer idEquipe,
                                                    @RequestBody List<LivreurDTO> livreursDTO);
*/
    @Operation(summary = "Supprime une équipe de livreurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Équipe supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Équipe introuvable")
    })
    @DeleteMapping("/{idEquipe}")
    ResponseEntity<Void> supprimerEquipe(@PathVariable Integer idEquipe);
}
