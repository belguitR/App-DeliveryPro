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
public interface JourneeEndpoint {

    @Operation(summary = "Get all journees")
    @ApiResponse(responseCode = "200", description = "List of journees")
    @GetMapping
    ResponseEntity<List<JourneeResponse>> getAllJournees();

    @Operation(summary = "Get a journee by reference")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Journee found"),
            @ApiResponse(responseCode = "404", description = "Journee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{reference}")
    ResponseEntity<JourneeResponse> getJourneeByReference(@PathVariable String reference);

    @Operation(summary = "Create a new journee")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Journee created"),
            @ApiResponse(responseCode = "400", description = "Invalid request or business rule violation",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    ResponseEntity<JourneeResponse> createJournee(@RequestBody JourneeRequest request);

    @Operation(summary = "Delete a journee by reference")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Journee deleted"),
            @ApiResponse(responseCode = "404", description = "Journee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{reference}")
    ResponseEntity<Void> deleteJournee(@PathVariable String reference);
}
