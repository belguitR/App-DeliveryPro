package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.mappers.EquipeLivreurMapper;
import fr.uga.miage.l3.mappers.LivreurMapper;
import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.responses.EquipeLivreurDTO;
import fr.uga.miage.l3.responses.LivreurDTO;
import fr.uga.miage.l3.services.EquipeLivreurService;
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
@AllArgsConstructor
@Tag(name = "Equipe Livreurs", description = "Endpoints pour gérer les Equipes des livreurs ")
public class EquipeLivreurController {

    private final EquipeLivreurService equipeLivreurService;
    private final EquipeLivreurMapper equipeLivreurMapper;
    private final LivreurMapper livreurMapper;

    @GetMapping
    public ResponseEntity<List<EquipeLivreurDTO>> getAllEquipes() {
        List<EquipeLivreurEntity> equipes = equipeLivreurService.getAllEquipes();
        List<EquipeLivreurDTO> dtos = equipes.stream()
                .map(equipeLivreurMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<EquipeLivreurDTO> createEquipe(@RequestBody List<LivreurDTO> livreursDTO) {
        if (livreursDTO.size() != 2) {
            return ResponseEntity.badRequest().build();
        }

        List<LivreurEntity> livreurs = livreursDTO.stream()
                .map(livreurMapper::fromDTO)
                .collect(Collectors.toList());


            EquipeLivreurEntity equipe = equipeLivreurService.createEquipeLivreur(livreurs);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(equipeLivreurMapper.toDTO(equipe));

    }


    /*@PutMapping("/{idEquipe}")
    public ResponseEntity<EquipeLivreurDTO> modifierEquipe(@PathVariable Integer idEquipe,
                                                           @RequestBody List<LivreurDTO> livreursDTO) {
        if (livreursDTO.size() != 2) {
            return ResponseEntity.badRequest().build();
        }

        List<LivreurEntity> livreurs = livreursDTO.stream()
                .map(livreurMapper::fromDTO)
                .collect(Collectors.toList());
        try {
            EquipeLivreurEntity equipe = equipeLivreurService.modifierEquipe(idEquipe, livreurs);
            return ResponseEntity.ok(equipeLivreurMapper.toDTO(equipe));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }*/

    @DeleteMapping("/{idEquipe}")
    public ResponseEntity<Void> supprimerEquipe(@PathVariable Integer idEquipe) {
        equipeLivreurService.supprimerEquipe(idEquipe);
        return ResponseEntity.noContent().build();
    }


}
