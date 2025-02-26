package fr.uga.miage.l3.controller;

import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.repository.CommandeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {

    private final CommandeRepository commandeRepository;

    public CommandeController(CommandeRepository commandeRepository) {
        this.commandeRepository = commandeRepository;
    }

    // Récupère toutes les commandes
    @GetMapping
    public ResponseEntity<List<CommandeEntity>> getAllCommandes() {
        return ResponseEntity.ok(commandeRepository.findAll());
    }

    // Récupère les commandes associées à une tournée donnée
    @GetMapping("/tournee/{idTournee}")
    public ResponseEntity<List<CommandeEntity>> getCommandesByTournee(@PathVariable Integer idTournee) {
        // On suppose ici que la méthode findByTourneeNumTournee est définie dans CommandeRepository
        List<CommandeEntity> commandes = commandeRepository.findByTourneeNumTournee(idTournee);
        return ResponseEntity.ok(commandes);
    }
}
