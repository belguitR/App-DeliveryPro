package fr.uga.miage.l3.endpoints;

import fr.uga.miage.l3.responses.LivreurResponse;
import fr.uga.miage.l3.responses.ErrorResponse;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/livreurs")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4300"})
public interface LivreurEndpoint {

    @Operation(summary = "Get all livreurs")
    @ApiResponse(responseCode = "200", description = "liste des livreurs")
    @GetMapping
    ResponseEntity<List<LivreurResponse>> getAllLivreurs();

    @Operation(summary = "Get a livreur by email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livreur trouvé"),
            @ApiResponse(responseCode = "404", description = "Livreur non trouvé", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{email}")
    ResponseEntity<LivreurResponse> getLivreurByEmail(@PathVariable String email);

    @Operation(summary = "Update a livreur by email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livreur mis a jour"),
            @ApiResponse(responseCode = "404", description = "Livreur non trouvé", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{email}")
    ResponseEntity<LivreurResponse> updateLivreur(@PathVariable String email, @RequestBody LivreurResponse livreur);
}
