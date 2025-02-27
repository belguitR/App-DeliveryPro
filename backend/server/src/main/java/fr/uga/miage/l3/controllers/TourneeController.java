package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.services.TourneeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournee")
public class TourneeController {

    private final TourneeService tourneeService;

    public TourneeController(TourneeService tourneeService) {
        this.tourneeService = tourneeService;
    }

    // Récupère toutes les tournées
    @GetMapping
    public ResponseEntity<List<TourneeEntity>> getAllTournees() {
        return ResponseEntity.ok(tourneeService.getAllTournees());
    }

    // Crée une nouvelle tournée
    @PostMapping
    public ResponseEntity<TourneeEntity> createTournee(@RequestBody TourneeEntity tournee) {
        TourneeEntity saved = tourneeService.createTournee(tournee);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Met à jour une tournée existante identifiée par son id
    @PutMapping("/{idTournee}")
    public ResponseEntity<TourneeEntity> updateTournee(@PathVariable Integer idTournee, @RequestBody TourneeEntity tournee) {
        try {
            TourneeEntity updated = tourneeService.updateTournee(idTournee, tournee);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // Récupère la tournée du jour pour un livreur donné
    @GetMapping("/livreur/{email}")
    public ResponseEntity<TourneeEntity> getTourneeDuJour(@PathVariable String email) {
        try {
            TourneeEntity tournee = tourneeService.getTourneeDuJourParLivreur(email);
            return ResponseEntity.ok(tournee);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}