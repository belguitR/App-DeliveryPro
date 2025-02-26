package fr.uga.miage.l3.controller;

import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.repository.EquipeLivreurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipes")
public class EquipeLivreurController {

    private final EquipeLivreurRepository equipeLivreurRepository;

    public EquipeLivreurController(EquipeLivreurRepository equipeLivreurRepository) {
        this.equipeLivreurRepository = equipeLivreurRepository;
    }

    // Récupère toutes les équipes de livreurs
    @GetMapping
    public ResponseEntity<List<EquipeLivreurEntity>> getAllEquipes() {
        return ResponseEntity.ok(equipeLivreurRepository.findAll());
    }

    // Crée une nouvelle équipe de livreurs
    @PostMapping
    public ResponseEntity<EquipeLivreurEntity> createEquipe(@RequestBody EquipeLivreurEntity equipe) {
        EquipeLivreurEntity saved = equipeLivreurRepository.save(equipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Met à jour une équipe existante identifiée par son id
    @PutMapping("/{idEquipe}")
    public ResponseEntity<EquipeLivreurEntity> updateEquipe(@PathVariable Integer idEquipe, @RequestBody EquipeLivreurEntity equipe) {
        if (!equipeLivreurRepository.existsById(idEquipe)) {
            return ResponseEntity.notFound().build();
        }
        equipe.setNumEquipe(idEquipe);
        return ResponseEntity.ok(equipeLivreurRepository.save(equipe));
    }
}
