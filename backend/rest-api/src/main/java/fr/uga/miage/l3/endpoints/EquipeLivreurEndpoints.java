package fr.uga.miage.l3.endpoints;

import fr.uga.miage.l3.responses.EquipeLivreurDTO;
import fr.uga.miage.l3.responses.LivreurDTO;
import fr.uga.miage.l3.exceptions.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
            @ApiResponse(responseCode = "404", description = "Aucune équipe trouvée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    ResponseEntity<List<EquipeLivreurDTO>> getAllEquipes();

    @Operation(summary = "Crée une nouvelle équipe de livreurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Équipe créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide ou règle métier non respectée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    ResponseEntity<EquipeLivreurDTO> createEquipe(@RequestBody List<LivreurDTO> livreursDTO);

    @Operation(summary = "Supprime une équipe de livreurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Équipe supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Équipe non trouvée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Règle métier non respectée (ex. équipe en livraison)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{idEquipe}")
    ResponseEntity<Void> supprimerEquipe(@PathVariable Integer idEquipe);
}
