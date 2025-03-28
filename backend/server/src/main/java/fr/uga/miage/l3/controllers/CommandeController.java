package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.responses.CommandeDTO;
import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.services.CommandeService;
import fr.uga.miage.l3.mappers.CommandeMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/commandes")
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:4300"})
@Tag(name = "Commande", description = "Endpoints pour gérer les commandes")
public class CommandeController {

    private final CommandeService commandeService;
    private final CommandeMapper commandeMapper;

    public CommandeController(CommandeService commandeService, CommandeMapper commandeMapper) {
        this.commandeService = commandeService;
        this.commandeMapper = commandeMapper;
    }

    @GetMapping
    public ResponseEntity<List<CommandeDTO>> getAllCommandes() {
        List<CommandeEntity> commandes = commandeService.getAllCommandes();
        List<CommandeDTO> dtos = commandes.stream()
                .map(commandeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/tournee/{idTournee}")
    public ResponseEntity<List<CommandeDTO>> getCommandesByTournee(@PathVariable Integer idTournee) {
        List<CommandeEntity> commandes = commandeService.getCommandesByTournee(idTournee);
        List<CommandeDTO> dtos = commandes.stream()
                .map(commandeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    @PutMapping("/{reference}")
    public ResponseEntity<?> updateCommande(@PathVariable Integer reference,
                                            @RequestBody CommandeDTO commandeDTO) {
        try {
            CommandeEntity entity = commandeMapper.fromDTO(commandeDTO);
            CommandeEntity updated = commandeService.updateCommande(reference, entity);
            return ResponseEntity.ok(commandeMapper.toDTO(updated));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la mise à jour");
        }
    }

}
