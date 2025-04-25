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
@CrossOrigin(origins = "http://localhost:4200")
public interface LivreurEndpoint {

    @Operation(summary = "Get all livreurs")
    @ApiResponse(responseCode = "200", description = "List of livreurs")
    @GetMapping
    ResponseEntity<List<LivreurResponse>> getAllLivreurs();

    @Operation(summary = "Get a livreur by email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livreur found"),
            @ApiResponse(responseCode = "404", description = "Livreur not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{email}")
    ResponseEntity<LivreurResponse> getLivreurByEmail(@PathVariable String email);

    @Operation(summary = "Update a livreur by email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livreur updated"),
            @ApiResponse(responseCode = "404", description = "Livreur not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{email}")
    ResponseEntity<LivreurResponse> updateLivreur(@PathVariable String email, @RequestBody LivreurResponse livreur);
}
