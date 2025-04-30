package fr.uga.miage.l3.endpoints;

import fr.uga.miage.l3.responses.EquipeLivreursResponse;
import fr.uga.miage.l3.requests.EquipeStatusPatchRequest;
import fr.uga.miage.l3.responses.ErrorResponse;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/equipes")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4300"})
public interface EquipeLivreursEndpoint {

    @Operation(
            summary = "Get all équipes",
            description = "Returns a list of all équipes"
    )
    @ApiResponse(responseCode = "200", description = "List of équipes")
    @GetMapping
    ResponseEntity<List<EquipeLivreursResponse>> getAllEquipes();
    @Operation(summary = "Create a new équipe")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Equipe created"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    ResponseEntity<EquipeLivreursResponse> createEquipe(@RequestBody EquipeLivreursResponse equipe);

    @Operation(summary = "Delete an équipe by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Equipe deleted"),
            @ApiResponse(responseCode = "404", description = "Equipe not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteEquipe(@PathVariable Integer id);

    @Operation(summary = "Patch équipe status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Equipe status updated"),
            @ApiResponse(responseCode = "404", description = "Equipe not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}/status")
    ResponseEntity<EquipeLivreursResponse> patchEquipeStatus(@PathVariable Integer id, @RequestBody EquipeStatusPatchRequest request);
}
