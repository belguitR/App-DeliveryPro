package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.services.CommandeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    // Récupère toutes les commandes
    @GetMapping
    public ResponseEntity<List<CommandeEntity>> getAllCommandes() {
        List<CommandeEntity> commandes = commandeService.getAllCommandes();
        return ResponseEntity.ok(commandes);
    }

    // Récupère les commandes associées à une tournée donnée
    @GetMapping("/tournee/{idTournee}")
    public ResponseEntity<List<CommandeEntity>> getCommandesByTournee(@PathVariable Integer idTournee) {
        List<CommandeEntity> commandes = commandeService.getCommandesByTournee(idTournee);
        return ResponseEntity.ok(commandes);
    }

    // Met à jour une commande existante identifiée par sa référence
    @PutMapping("/{reference}")
    public ResponseEntity<CommandeEntity> updateCommande(@PathVariable String reference,
                                                         @RequestBody CommandeEntity commande) {
        try {
            CommandeEntity updated = commandeService.updateCommande(reference, commande);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}