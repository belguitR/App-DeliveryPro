package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.responses.LivreurDTO;
import fr.uga.miage.l3.endpoints.LivreurEndpoints;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.services.LivreurService;
import fr.uga.miage.l3.mappers.LivreurMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/livreur")
@CrossOrigin(origins = "http://localhost:4200")
public class LivreurController implements LivreurEndpoints {

    private final LivreurService livreurService;
    private final LivreurMapper livreurMapper;

    public LivreurController(LivreurService livreurService, LivreurMapper livreurMapper) {
        this.livreurService = livreurService;
        this.livreurMapper = livreurMapper;
    }

    /**
     * Récupère tous les livreurs
     */
    @GetMapping
    public ResponseEntity<List<LivreurDTO>> getAllLivreurs() {
        List<LivreurEntity> livreurs = livreurService.getAllLivreurs();
        List<LivreurDTO> livreurDTOs = livreurs.stream()
                .map(livreurMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(livreurDTOs);
    }

    /**
     * Crée un nouveau livreur
     */
    @PostMapping
    public ResponseEntity<LivreurDTO> createLivreur(@RequestBody LivreurDTO livreurDTO) {
        LivreurEntity livreurEntity = livreurMapper.fromDTO(livreurDTO);
        LivreurEntity saved = livreurService.createLivreur(livreurEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(livreurMapper.toDTO(saved));
    }

    /**
     * Met à jour un livreur existant identifié par son id
     */
    @PutMapping("/{idLivreur}")
    public ResponseEntity<LivreurDTO> updateLivreur(@PathVariable String idLivreur, @RequestBody LivreurDTO livreurDTO) {
        try {
            LivreurEntity livreurEntity = livreurMapper.fromDTO(livreurDTO);
            LivreurEntity updated = livreurService.updateLivreur(idLivreur, livreurEntity);
            return ResponseEntity.ok(livreurMapper.toDTO(updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}



