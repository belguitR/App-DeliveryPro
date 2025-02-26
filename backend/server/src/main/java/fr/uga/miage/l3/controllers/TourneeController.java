package fr.uga.miage.l3.controller;

import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.repository.TourneeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournees")
public class TourneeController {

    private final TourneeRepository tourneeRepository;

    public TourneeController(TourneeRepository tourneeRepository) {
        this.tourneeRepository = tourneeRepository;
    }

    // Récupère toutes les tournées
    @GetMapping
    public ResponseEntity<List<TourneeEntity>> getAllTournees() {
        return ResponseEntity.ok(tourneeRepository.findAll());
    }

    // Crée une nouvelle tournée
    @PostMapping
    public ResponseEntity<TourneeEntity> createTournee(@RequestBody TourneeEntity tournee) {
        TourneeEntity saved = tourneeRepository.save(tournee);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Met à jour une tournée existante identifiée par son id
    @PutMapping("/{idTournee}")
    public ResponseEntity<TourneeEntity> updateTournee(@PathVariable Integer idTournee, @RequestBody TourneeEntity tournee) {
        if (!tourneeRepository.existsById(idTournee)) {
            return ResponseEntity.notFound().build();
        }
        tournee.setNumTournee(idTournee);
        return ResponseEntity.ok(tourneeRepository.save(tournee));
    }
}
