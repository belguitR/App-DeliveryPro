package fr.uga.miage.l3.controllers;



import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repository.LivreurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livreur")
public class LivreurController {

    private final LivreurRepository livreurRepository;

    public LivreurController(LivreurRepository livreurRepository) {
        this.livreurRepository = livreurRepository;
    }

    // Récupère tous les livreurs
    @GetMapping
    public ResponseEntity<List<LivreurEntity>> getAllLivreurs() {
        return ResponseEntity.ok(livreurRepository.findAll());
    }

    // Crée un nouveau livreur
    @PostMapping
    public ResponseEntity<LivreurEntity> createLivreur(@RequestBody LivreurEntity livreur) {
        LivreurEntity saved = livreurRepository.save(livreur);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Met à jour un livreur existant identifié par son id
    @PutMapping("/{idLivreur}")
    public ResponseEntity<LivreurEntity> updateLivreur(@PathVariable String idLivreur, @RequestBody LivreurEntity livreur) {
        if (!livreurRepository.existsById(idLivreur)) {
            return ResponseEntity.notFound().build();
        }
        livreur.setIdEmploye(idLivreur);
        return ResponseEntity.ok(livreurRepository.save(livreur));
    }

}

