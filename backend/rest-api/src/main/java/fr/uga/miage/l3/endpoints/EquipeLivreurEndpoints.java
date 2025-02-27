package fr.uga.miage.l3.endpoints;

import fr.uga.miage.l3.dto.EquipeLivreurDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "EquipeLivreur", description = "Endpoints pour gérer les équipes de livreurs")
@RequestMapping("/api/equipe-livreur")
public interface EquipeLivreurEndpoints {

    /**
     * Récupère la liste de toutes les équipes de livreurs.
     */
    @GetMapping
    List<EquipeLivreurDTO> getAllEquipes();

    /**
     * Crée une nouvelle équipe de livreurs.
     */
    @PostMapping
    EquipeLivreurDTO createEquipe(@RequestBody EquipeLivreurDTO equipeLivreur);

    /**
     * Affecte une tournée à une équipe livreur.
     */
    @PostMapping("/{numEquipe}/affecter-tournee")
    void affecterTournee(@PathVariable Integer numEquipe);

    /**
     * Libère une équipe livreur.
     */
    @PostMapping("/{numEquipe}/liberer")
    void libererEquipe(@PathVariable Integer numEquipe);
}
