package fr.uga.miage.l3.endpoints;

import fr.uga.miage.l3.dto.TourneeDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tournee", description = "Endpoints pour gérer les tournées")
@RequestMapping("/api/tournee")
public interface TourneeEndpoints {

    /**
     * Récupère la liste de toutes les tournées.
     */
    @GetMapping
    ResponseEntity<List<TourneeDTO>> getAllTournees();
    /**
     * Crée une nouvelle tournée.
     */
    @PostMapping
    ResponseEntity<TourneeDTO> createTournee(@RequestBody TourneeDTO tournee);

    /**
     * Met à jour une tournée existante.
     */
    @PutMapping("/{idTournee}")
    ResponseEntity<TourneeDTO> updateTournee(@PathVariable Integer idTournee, @RequestBody TourneeDTO tournee);
}
