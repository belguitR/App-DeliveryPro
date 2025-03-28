package fr.uga.miage.l3.endpoints;

import fr.uga.miage.l3.responses.TourneeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tournee", description = "Endpoints pour gérer les tournées")
@RequestMapping("/api/tournee")
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:4300"})
public interface TourneeEndpoints {

    @Operation(summary = "Récupère toutes les tournées")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des tournées récupérée avec succès")
    })
    @GetMapping
    ResponseEntity<List<TourneeDTO>> getAllTournees();

    @Operation(summary = "Crée une nouvelle tournée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tournée créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    ResponseEntity<TourneeDTO> createTournee(@RequestBody TourneeDTO tournee);

    @Operation(summary = "Supprime une tournée existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tournée supprimée avec succès (pas de contenu)"),
            @ApiResponse(responseCode = "404", description = "Tournée non trouvée")
    })
    @DeleteMapping("/{idTournee}")
    ResponseEntity<Void> deleteTournee(@PathVariable Integer idTournee);

    @Operation(summary = "Récupère la tournée du jour pour un livreur donné")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tournée du jour récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune tournée trouvée pour aujourd'hui ou le livreur n'est pas affecté")
    })
    @GetMapping("/livreur/{email}")
        ResponseEntity<TourneeDTO> getTourneeDuJour(@PathVariable String email);


    @Operation(summary = "Met à jour une tournée existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tournée mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide"),
            @ApiResponse(responseCode = "404", description = "Tournée ou éléments associés non trouvés")
    })
    @PutMapping
    ResponseEntity<TourneeDTO> updateTournee(@RequestBody TourneeDTO tournee);

}
