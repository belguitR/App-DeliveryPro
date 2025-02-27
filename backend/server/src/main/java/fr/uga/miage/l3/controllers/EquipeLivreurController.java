package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.services.EquipeLivreurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipes")
public class EquipeLivreurController {

    private final EquipeLivreurService equipeLivreurService;

    public EquipeLivreurController(EquipeLivreurService equipeLivreurService) {
        this.equipeLivreurService = equipeLivreurService;
    }

    // Récupère toutes les équipes de livreurs
    @GetMapping
    public ResponseEntity<List<EquipeLivreurEntity>> getAllEquipes() {
        return ResponseEntity.ok(equipeLivreurService.getAllEquipes());
    }

    // Crée une nouvelle équipe de livreurs
    @PostMapping
    public ResponseEntity<EquipeLivreurEntity> createEquipe(@RequestBody List<LivreurEntity> livreurs, @RequestParam Integer numEquipe) {
        EquipeLivreurEntity saved = equipeLivreurService.createEquipeLivreur(livreurs, numEquipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Affecte une tournée à une équipe
    @PutMapping("/{idEquipe}/affecter-tournee")
    public ResponseEntity<Void> affecterTournee(@PathVariable Integer idEquipe) {
        equipeLivreurService.affecterTournee(idEquipe);
        return ResponseEntity.ok().build();
    }

    // Libère une équipe de livreurs
    @PutMapping("/{idEquipe}/liberer")
    public ResponseEntity<Void> libererEquipe(@PathVariable Integer idEquipe) {
        equipeLivreurService.libererEquipe(idEquipe);
        return ResponseEntity.ok().build();
    }
}