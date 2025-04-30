package fr.uga.miage.l3.endpoints;

import fr.uga.miage.l3.responses.CommandeResponse;
import fr.uga.miage.l3.requests.CommandePatchRequest;
import fr.uga.miage.l3.responses.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/commandes")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4300"})
public interface CommandeEndpoint {

    @Operation(summary = "Get all commandes")
    @ApiResponse(responseCode = "200", description = "List of commandes")
    @GetMapping
    ResponseEntity<List<CommandeResponse>> getAllCommandes();

    @Operation(summary = "Get all commandes of a tournee by tournee reference")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of commandes for the tournee"),
            @ApiResponse(responseCode = "404", description = "Tournee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/tournee/{tourneeReference}")
    ResponseEntity<List<CommandeResponse>> getCommandesByTourneeReference(@PathVariable String tourneeReference);

    @Operation(summary = "Patch a commande (etat and dateDeLivraison only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Commande patched"),
            @ApiResponse(responseCode = "404", description = "Commande not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{reference}")
    ResponseEntity<CommandeResponse> patchCommande(
            @PathVariable String reference,
            @RequestBody CommandePatchRequest request
    );
}
