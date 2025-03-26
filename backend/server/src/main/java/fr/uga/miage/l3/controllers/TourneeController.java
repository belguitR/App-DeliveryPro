package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.mappers.TourneeMapper;
import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.responses.TourneeDTO;
import fr.uga.miage.l3.services.TourneeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tournee")
@CrossOrigin(origins = "http://localhost:4200")
public class TourneeController {

    private final TourneeService tourneeService;
    private final TourneeMapper tourneeMapper;

    public TourneeController(TourneeService tourneeService, TourneeMapper tourneeMapper) {
        this.tourneeService = tourneeService;
        this.tourneeMapper = tourneeMapper;
    }

    @GetMapping
    public ResponseEntity<List<TourneeDTO>> getAllTournees() {
        List<TourneeEntity> entities = tourneeService.getAllTournees();
        List<TourneeDTO> dtos = entities.stream()
                .map(tourneeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<TourneeDTO> createTournee(@RequestBody TourneeDTO tourneeDTO) {
        TourneeEntity tourneeEntity = tourneeMapper.fromDTO(tourneeDTO);
        Integer equipeLivreurId = tourneeDTO.getEquipeLivreurId();
        List<Integer> commandesIds = tourneeDTO.getCommandesIds();
        TourneeEntity saved = tourneeService.createTournee(tourneeEntity, equipeLivreurId, commandesIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(tourneeMapper.toDTO(saved));
    }

    @DeleteMapping("/{idTournee}")
    public ResponseEntity<Void> deleteTournee(@PathVariable Integer idTournee) {
        try {
            tourneeService.deleteTournee(idTournee);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }
    }

    @GetMapping("/livreur/{email}")
    public ResponseEntity<TourneeDTO> getTourneeDuJour(@PathVariable String email) {
        try {
            TourneeEntity tourneeEntity = tourneeService.getTourneeDuJourParLivreur(email);
            return ResponseEntity.ok(tourneeMapper.toDTO(tourneeEntity));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
