package fr.uga.miage.l3.endpoints;

import fr.uga.miage.l3.dto.EquipeLivreurDTO;
import fr.uga.miage.l3.dto.LivreurDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "EquipeLivreur", description = "Endpoints pour gérer les équipes de livreurs")
@RequestMapping("/api/equipes")
@CrossOrigin(origins = "http://localhost:4200")
public interface EquipeLivreurEndpoints {

    /**
     * Récupère la liste de toutes les équipes de livreurs.
     */
    @GetMapping
    ResponseEntity<List<EquipeLivreurDTO>> getAllEquipes();

    /**
     * Crée une nouvelle équipe de livreurs.
     */
    @PostMapping
    ResponseEntity<EquipeLivreurDTO> createEquipe(@RequestBody List<LivreurDTO> livreurs, @RequestParam Integer numEquipe);

    /**
     * Affecte une tournée à une équipe livreur.
     */
    @PutMapping("/{idEquipe}/affecter-tournee")
    ResponseEntity<Void> affecterTournee(@PathVariable Integer idEquipe);

    /**
     * Libère une équipe livreur.
     */
    @PutMapping("/{idEquipe}/liberer")
    ResponseEntity<Void> libererEquipe(@PathVariable Integer idEquipe);
}
