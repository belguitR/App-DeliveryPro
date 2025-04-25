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
@CrossOrigin(origins = "http://localhost:4200")
public interface CommandeEndpoint {

    @Operation(summary = "Get all commandes")
    @ApiResponse(responseCode = "200", description = "List of commandes")
    @GetMapping
    ResponseEntity<List<CommandeResponse>> getAllCommandes();

    @Operation(summary = "Get a commande by reference")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Commande found"),
            @ApiResponse(responseCode = "404", description = "Commande not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{reference}")
    ResponseEntity<CommandeResponse> getCommandeByReference(@PathVariable String reference);

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
