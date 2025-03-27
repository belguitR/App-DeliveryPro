package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.mappers.TourneeMapper;
import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.responses.TourneeDTO;
import fr.uga.miage.l3.services.TourneeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tournee")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Tournée", description = "Endpoints pour gérer les Tournées")
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
    public ResponseEntity<?> createTournee(@RequestBody TourneeDTO tourneeDTO) {
        try {
            TourneeEntity tourneeEntity = new TourneeEntity();
            tourneeEntity.setDate(tourneeDTO.getDate());
            tourneeEntity.setEtat(tourneeDTO.getEtat());

            TourneeEntity saved = tourneeService.createTournee(
                    tourneeEntity,
                    tourneeDTO.getEquipeLivreurId(),
                    tourneeDTO.getCommandesIds()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(tourneeMapper.toDTO(saved));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création de la tournée");
        }
    }




    @DeleteMapping("/{idTournee}")
    public ResponseEntity<?> deleteTournee(@PathVariable Integer idTournee) {
        try {
            tourneeService.deleteTournee(idTournee);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tournée non trouvée");
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @GetMapping("/livreur/{email}")
    public ResponseEntity<List<TourneeDTO>> getTourneesParLivreur(@PathVariable String email) {
        try {
            List<TourneeEntity> list = tourneeService.getTourneesParLivreur(email);
            return ResponseEntity.ok(list.stream().map(tourneeMapper::toDTO).collect(Collectors.toList()));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping
    public ResponseEntity<?> updateTournee(@RequestBody TourneeDTO tourneeDTO) {
        try {
            TourneeEntity tourneeEntity = tourneeMapper.fromDTO(tourneeDTO);
            Integer equipeLivreurId = tourneeDTO.getEquipeLivreurId();
            List<Integer> commandesIds = tourneeDTO.getCommandes()
                    .stream()
                    .map(c -> c.getReference())
                    .collect(Collectors.toList());


            TourneeEntity updated = tourneeService.updateTournee(tourneeEntity, equipeLivreurId, commandesIds);
            System.out.println("PUT: TourneeDTO ID = " + tourneeDTO.getNumTournee());

            return ResponseEntity.ok(tourneeMapper.toDTO(updated));

        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la mise à jour de la tournée");
        }
    }


}