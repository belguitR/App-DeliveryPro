package fr.uga.miage.l3.endpoints;

import fr.uga.miage.l3.responses.LivreurDTO;
import fr.uga.miage.l3.exceptions.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Livreur", description = "Endpoints pour gérer les livreurs")
@RequestMapping("/api/livreur")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4300", "https://main-bvxea6i-jenwo4in7lt44.fr-3.platformsh.site"})
public interface LivreurEndpoints {

    @Operation(summary = "Récupère tous les livreurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des livreurs récupérée avec succès")
    })
    @GetMapping
    ResponseEntity<List<LivreurDTO>> getAllLivreurs();

    @Operation(summary = "Crée un nouveau livreur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Livreur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Livreur invalide ou déjà existant",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    ResponseEntity<LivreurDTO> createLivreur(@RequestBody LivreurDTO livreurDTO);

    @Operation(summary = "Met à jour un livreur existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livreur mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Livreur introuvable",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides (âge < 18, etc.)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{idLivreur}")
    ResponseEntity<LivreurDTO> updateLivreur(@PathVariable String idLivreur,
                                             @RequestBody LivreurDTO livreurDTO);
}
