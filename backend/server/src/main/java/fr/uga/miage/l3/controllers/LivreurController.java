package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.services.LivreurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livreur")
public class LivreurController {

    private final LivreurService livreurService;

    public LivreurController(LivreurService livreurService) {
        this.livreurService = livreurService;
    }

    /**
     * Récupère tous les livreurs
     */
    @GetMapping
    public ResponseEntity<List<LivreurEntity>> getAllLivreurs() {
        List<LivreurEntity> livreurs = livreurService.getAllLivreurs();
        return ResponseEntity.ok(livreurs);
    }

    /**
     * Crée un nouveau livreur
     */
    @PostMapping
    public ResponseEntity<LivreurEntity> createLivreur(@RequestBody LivreurEntity livreur) {
        LivreurEntity saved = livreurService.createLivreur(livreur);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Met à jour un livreur existant identifié par son id
     */
    @PutMapping("/{idLivreur}")
    public ResponseEntity<LivreurEntity> updateLivreur(@PathVariable String idLivreur,
                                                       @RequestBody LivreurEntity livreur) {
        try {
            LivreurEntity updated = livreurService.updateLivreur(idLivreur, livreur);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            // Si le livreur n'existe pas, on renvoie un 404
            return ResponseEntity.notFound().build();
        }
    }
}


