package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.dto.TourneeDTO;
import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.services.TourneeService;
import fr.uga.miage.l3.mappers.TourneeMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tournee")
public class TourneeController {

    private final TourneeService tourneeService;
    private final TourneeMapper tourneeMapper;

    public TourneeController(TourneeService tourneeService, TourneeMapper tourneeMapper) {
        this.tourneeService = tourneeService;
        this.tourneeMapper = tourneeMapper;
    }

    // Récupère toutes les tournées
    @GetMapping
    public ResponseEntity<List<TourneeDTO>> getAllTournees() {
        List<TourneeEntity> entities = tourneeService.getAllTournees();
        List<TourneeDTO> dtos = entities.stream()
                .map(tourneeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Crée une nouvelle tournée
    @PostMapping
    public ResponseEntity<TourneeDTO> createTournee(@RequestBody TourneeDTO tourneeDTO) {
        TourneeEntity tourneeEntity = tourneeMapper.fromDTO(tourneeDTO);
        TourneeEntity saved = tourneeService.createTournee(tourneeEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(tourneeMapper.toDTO(saved));
    }

    // Met à jour une tournée existante identifiée par son id
    @PutMapping("/{idTournee}")
    public ResponseEntity<TourneeDTO> updateTournee(@PathVariable Integer idTournee, @RequestBody TourneeDTO tourneeDTO) {
        try {
            TourneeEntity tourneeEntity = tourneeMapper.fromDTO(tourneeDTO);
            TourneeEntity updated = tourneeService.updateTournee(idTournee, tourneeEntity);
            return ResponseEntity.ok(tourneeMapper.toDTO(updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // Récupère la tournée du jour pour un livreur donné
    @GetMapping("/livreur/{email}")
    public ResponseEntity<TourneeDTO> getTourneeDuJour(@PathVariable String email) {
        try {
            TourneeEntity tourneeEntity = tourneeService.getTourneeDuJourParLivreur(email);
            return ResponseEntity.ok(tourneeMapper.toDTO(tourneeEntity));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
