package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.endpoints.EquipeLivreurEndpoints;
import fr.uga.miage.l3.exceptions.ErrorResponse;
import fr.uga.miage.l3.mappers.EquipeLivreurMapper;
import fr.uga.miage.l3.mappers.LivreurMapper;
import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.responses.EquipeLivreurDTO;
import fr.uga.miage.l3.services.EquipeLivreurService;
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
@AllArgsConstructor
public class EquipeLivreurController implements EquipeLivreurEndpoints {

    private final EquipeLivreurService equipeLivreurService;
    private final EquipeLivreurMapper equipeLivreurMapper;
    private final LivreurMapper livreurMapper;

    @Override
    @GetMapping
    public ResponseEntity<List<EquipeLivreurDTO>> getAllEquipes() {
        try {
            List<EquipeLivreurEntity> equipes = equipeLivreurService.getAllEquipes();
            List<EquipeLivreurDTO> dtos = equipes.stream()
                    .map(equipeLivreurMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (EntityNotFoundException ex) {
            // 404 Not Found : "Aucune équipe trouvée."
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    /**
     * POST /api/equipes
     * Ex: ["jdd5", "jdd6"]
     */
    @Override
    @PostMapping
    public ResponseEntity<?> createEquipe(@RequestBody List<String> idEmployes) {
        try {
            EquipeLivreurEntity equipe = equipeLivreurService.createEquipeLivreur(idEmployes);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(equipeLivreurMapper.toDTO(equipe));
        } catch (IllegalArgumentException ex) {
            // ex: "le livreur jdd5 est déjà affecté" ou "Un ou plusieurs livreurs n'existent pas dans la base."
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("BAD_REQUEST", ex.getMessage()));
        }
    }


    @Override
    @DeleteMapping("/{idEquipe}")
    public ResponseEntity<Void> supprimerEquipe(@PathVariable Integer idEquipe) {
        try {
            equipeLivreurService.supprimerEquipe(idEquipe);
            return ResponseEntity.noContent().build();

        } catch (EntityNotFoundException ex) {
            // 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (IllegalArgumentException ex) {
            // 400 Bad Request
            return ResponseEntity.badRequest().build();
        }
    }
}
