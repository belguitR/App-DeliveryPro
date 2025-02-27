package fr.uga.miage.l3.endpoints;


import fr.uga.miage.l3.dto.CommandeDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Commande", description = "Endpoints pour gérer les commandes")
@RequestMapping("/api/commande")
public interface CommandeEndpoints {

    /**
     * Récupère la liste de toutes les commandes.
     */
    @GetMapping
    List<CommandeDTO> getAllCommandes();

    /**
     * Récupère la liste des commandes d'une tournée.
     */
    @GetMapping("/tournee/{idTournee}")
    List<CommandeDTO> getCommandesByTournee(@PathVariable Integer idTournee);

    /**
     * Met à jour une commande existante.
     */
    @PutMapping("/{reference}")
    CommandeDTO updateCommande(@PathVariable String reference, @RequestBody CommandeDTO commande);
}
