package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.repository.EquipeLivreurRepository;
import fr.uga.miage.l3.responses.EquipeLivreurDTO;
import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.services.EquipeLivreurService;
import fr.uga.miage.l3.mappers.EquipeLivreurMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/equipes")
@CrossOrigin(origins = "http://localhost:4200")
public class EquipeLivreurController {

    private final EquipeLivreurService equipeLivreurService;
    private final EquipeLivreurMapper equipeLivreurMapper;
    private final EquipeLivreurRepository equipeLivreurRepository;

    public EquipeLivreurController(EquipeLivreurService equipeLivreurService, EquipeLivreurMapper equipeLivreurMapper, EquipeLivreurRepository equipeLivreurRepository) {
        this.equipeLivreurService = equipeLivreurService;
        this.equipeLivreurMapper = equipeLivreurMapper;
        this.equipeLivreurRepository = equipeLivreurRepository;
    }

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
    public ResponseEntity<Void> suprimeEquipe(@PathVariable Integer idEquipe) {
        // 1. Vérifier l'existence de l'équipe
        if (!equipeLivreurRepository.existsById(idEquipe)) {
            // Si l'équipe n'existe pas, renvoyer 404
            return ResponseEntity.notFound().build();
        }
        // 2. Supprimer l'équipe
        equipeLivreurRepository.deleteById(idEquipe);

        // 3. Retourner un code 204  en cas de réussite
        return ResponseEntity.noContent().build();
    }
}
