package fr.uga.miage.l3.endpoints;

import fr.uga.miage.l3.responses.LivreurDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Livreur", description = "Endpoints pour gérer les livreurs")
@RequestMapping("/api/livreur")
@CrossOrigin(origins = "http://localhost:4200")
public interface LivreurEndpoints {

    /**
     * Récupère la liste de tous les livreurs.
     */
    @Operation(summary = "Récupère tous les livreurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des livreurs récupérée avec succès")
    })
    @GetMapping
    ResponseEntity<List<LivreurDTO>> getAllLivreurs();

    /**
     * Crée un nouveau livreur.
     */
    @Operation(summary = "Crée un nouveau livreur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Livreur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données du livreur invalides")
    })
    @PostMapping
    ResponseEntity<LivreurDTO> createLivreur(@RequestBody LivreurDTO livreur);

    /**
     * Met à jour un livreur existant identifié par son identifiant.
     */
    @Operation(summary = "Met à jour un livreur existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livreur mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Livreur introuvable"),
            @ApiResponse(responseCode = "400", description = "Données du livreur invalides")
    })
    @PutMapping("/{idLivreur}")
    ResponseEntity<LivreurDTO> updateLivreur(@PathVariable String idLivreur, @RequestBody LivreurDTO livreur);
}
