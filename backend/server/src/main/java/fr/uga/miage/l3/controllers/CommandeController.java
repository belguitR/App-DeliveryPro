package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.dto.CommandeDTO;
import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.services.CommandeService;
import fr.uga.miage.l3.mappers.CommandeMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {

    private final CommandeService commandeService;
    private final CommandeMapper commandeMapper;

    public CommandeController(CommandeService commandeService, CommandeMapper commandeMapper) {
        this.commandeService = commandeService;
        this.commandeMapper = commandeMapper;
    }

    // Récupère toutes les commandes
    @GetMapping
    public ResponseEntity<List<CommandeDTO>> getAllCommandes() {
        List<CommandeEntity> commandes = commandeService.getAllCommandes();
        List<CommandeDTO> dtos = commandes.stream()
                .map(commandeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Récupère les commandes associées à une tournée donnée
    @GetMapping("/tournee/{idTournee}")
    public ResponseEntity<List<CommandeDTO>> getCommandesByTournee(@PathVariable Integer idTournee) {
        List<CommandeEntity> commandes = commandeService.getCommandesByTournee(idTournee);
        List<CommandeDTO> dtos = commandes.stream()
                .map(commandeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Met à jour une commande existante identifiée par sa référence
    @PutMapping("/{reference}")
    public ResponseEntity<CommandeDTO> updateCommande(@PathVariable String reference,
                                                      @RequestBody CommandeDTO commandeDTO) {
        try {
            CommandeEntity commandeEntity = commandeMapper.fromDTO(commandeDTO);
            CommandeEntity updated = commandeService.updateCommande(reference, commandeEntity);
            return ResponseEntity.ok(commandeMapper.toDTO(updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
