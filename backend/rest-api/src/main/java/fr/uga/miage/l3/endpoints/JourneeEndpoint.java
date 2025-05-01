package fr.uga.miage.l3.endpoints;

import fr.uga.miage.l3.responses.ErrorResponse;
import fr.uga.miage.l3.responses.JourneeResponse;
import fr.uga.miage.l3.requests.JourneeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/journees")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4300"})
public interface JourneeEndpoint {

    @Operation(summary = "Get all journees")
    @ApiResponse(responseCode = "200", description = "Liste des journées")
    @GetMapping
    ResponseEntity<List<JourneeResponse>> getAllJournees();

    @Operation(summary = "Get a journee by reference")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Journee trouvée"),
            @ApiResponse(responseCode = "404", description = "Journee non trouvée",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{reference}")
    ResponseEntity<JourneeResponse> getJourneeByReference(@PathVariable String reference);

    @Operation(summary = "Create a new journee")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Journée créée"),
            @ApiResponse(responseCode = "400", description = "Invalid request or business rule violation",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    ResponseEntity<JourneeResponse> createJournee(@RequestBody JourneeRequest request);

    @Operation(summary = "Delete a journee by reference")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Journee suprrimée"),
            @ApiResponse(responseCode = "404", description = "Journee non trouvée",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{reference}")
    ResponseEntity<Void> deleteJournee(@PathVariable String reference);
}
