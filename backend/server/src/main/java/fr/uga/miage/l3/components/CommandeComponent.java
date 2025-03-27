package fr.uga.miage.l3.components;

import fr.uga.miage.l3.exceptions.technicals.NotFoundCommandeEntityException;
import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.repository.CommandeRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandeComponent {

    private final CommandeRepository commandeRepository;

    public CommandeComponent(CommandeRepository commandeRepository) {
        this.commandeRepository = commandeRepository;
    }

    // Récupère toutes les commandes
    public List<CommandeEntity> getAllCommandes() {
        return commandeRepository.findAll();
    }

    // Récupère une commande par son ID
    public CommandeEntity getCommandeById(Integer id) throws NotFoundCommandeEntityException {
        return commandeRepository.findById(id).orElseThrow(()-> new NotFoundCommandeEntityException("La commande [" + id + "] n'a pas été trouvée"));
    }

    // Supprime une commande par son ID
    public void deleteCommande(Integer id) {
        commandeRepository.deleteById(id);
    }
}