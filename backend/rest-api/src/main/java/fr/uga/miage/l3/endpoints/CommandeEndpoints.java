package fr.uga.miage.l3.endpoints;

import fr.uga.miage.l3.responses.CommandeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Commande", description = "Endpoints pour gérer les commandes")
@RequestMapping("/api/commandes")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4300", "https://main-bvxea6i-cblr3dirpybii.eu-5.platformsh.site"})
public interface CommandeEndpoints {

    /**
     * Récupère la liste de toutes les commandes.
     */
    @Operation(summary = "Récupère la liste de toutes les commandes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Toutes les commandes ont été récupérées"),
            @ApiResponse(responseCode = "404", description = "Aucune commande trouvée (ou autre erreur)")
    })
    @GetMapping
    ResponseEntity<List<CommandeDTO>> getAllCommandes();

    /**
     * Récupère la liste des commandes d'une tournée.
     */
    @Operation(summary = "Récupère la liste des commandes d'une tournée donnée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Les commandes de la tournée ont été récupérées"),
            @ApiResponse(responseCode = "404", description = "Aucune commande trouvée pour cette tournée")
    })
    @GetMapping("/tournee/{idTournee}")
    ResponseEntity<List<CommandeDTO>> getCommandesByTournee(@PathVariable Integer idTournee);

    /**
     * Met à jour une commande existante.
     */
    @Operation(summary = "Met à jour une commande existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La commande a été mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "La commande n'a pas été trouvée")
    })
    @PutMapping("/{reference}")
    ResponseEntity<CommandeDTO> updateCommande(@PathVariable String reference, @RequestBody CommandeDTO commande);

    /**
     * Récupère la liste des commandes non livrées, triées par date de création croissante.
     */
    @Operation(summary = "Récupère la liste des commandes non livrées triées par date croissante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Les commandes non livrées ont été récupérées"),
            @ApiResponse(responseCode = "404", description = "Aucune commande non livrée trouvée")
    })
    @GetMapping("/non-livrees")
    ResponseEntity<List<CommandeDTO>> getCommandesNonLivrees();
}
