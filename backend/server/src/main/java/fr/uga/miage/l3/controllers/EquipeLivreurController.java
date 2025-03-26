package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.repository.EquipeLivreurRepository;
import fr.uga.miage.l3.responses.EquipeLivreurDTO;
import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.services.EquipeLivreurService;
import fr.uga.miage.l3.mappers.EquipeLivreurMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/equipes")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "EquipeLivreur", description = "Endpoints pour gérer les équipes de livreurs")
@AllArgsConstructor
public class EquipeLivreurController {

    private final EquipeLivreurService equipeLivreurService;
    private final EquipeLivreurMapper equipeLivreurMapper;
    private final EquipeLivreurRepository equipeLivreurRepository;


    // Récupère toutes les équipes de livreurs
    @GetMapping
    public ResponseEntity<List<EquipeLivreurDTO>> getAllEquipes() {
        List<EquipeLivreurEntity> entities = equipeLivreurService.getAllEquipes();
        List<EquipeLivreurDTO> dtos = entities.stream()
                .map(equipeLivreurMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Crée une nouvelle équipe de livreurs
    @PostMapping
    public ResponseEntity<EquipeLivreurDTO> createEquipe(@RequestBody EquipeLivreurEntity equipeLivreur) {
        EquipeLivreurEntity saved = equipeLivreurService.createEquipeLivreur(
                equipeLivreur.getLivreurs(),
                equipeLivreur.getNumEquipe()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(equipeLivreurMapper.toDTO(saved));
    }

    @PutMapping("/{idEquipe}")
    public ResponseEntity<EquipeLivreurDTO> modifierEquipe(@PathVariable Integer idEquipe,
                                                           @RequestBody EquipeLivreurDTO equipeLivreurDTO) {
        try {
            EquipeLivreurEntity updated = equipeLivreurService.modifierEquipe(idEquipe, equipeLivreurMapper.fromDTO(equipeLivreurDTO));
            return ResponseEntity.ok(equipeLivreurMapper.toDTO(updated));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    @DeleteMapping("/{idEquipe}")
    public ResponseEntity<Void> supprimerEquipe(@PathVariable Integer idEquipe) {
        try {
            equipeLivreurService.supprimerEquipe(idEquipe);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
