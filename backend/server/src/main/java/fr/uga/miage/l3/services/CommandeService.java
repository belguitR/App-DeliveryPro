package fr.uga.miage.l3.services;

import fr.uga.miage.l3.components.CommandeComponent;
import fr.uga.miage.l3.enums.EtatCommande;
import fr.uga.miage.l3.exceptions.BusinessRuleException;
import fr.uga.miage.l3.exceptions.ResourceNotFoundException;
import fr.uga.miage.l3.mappers.CommandeMapper;
import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.requests.CommandePatchRequest;
import fr.uga.miage.l3.responses.CommandeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandeService {
    private final CommandeComponent commandeComponent;

    public List<CommandeResponse> getAllCommandes() {
        List<CommandeEntity> commandes = commandeComponent.findAll();
        return commandes.stream()
                .map(c -> {
                    try {
                        return CommandeMapper.toResponse(c);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }



    public CommandeResponse getCommandeByReference(String reference) {
        CommandeEntity entity = commandeComponent.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Commande not found: " + reference));
        return CommandeMapper.toResponse(entity);
    }

    public CommandeResponse patchCommande(String reference, CommandePatchRequest request) {
        CommandeEntity entity = commandeComponent.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Commande not found: " + reference));
        if (request.getEtat() != null) {
            EtatCommande etat = EtatCommande.valueOf(request.getEtat());
            if (etat == EtatCommande.LIVREE && request.getDateDeLivraison() == null) {
                throw new BusinessRuleException("dateDeLivraison is required when etat is LIVREE");
            }
            entity.setEtat(etat);
        }
        if (request.getDateDeLivraison() != null) {
            entity.setDateDeLivraison(request.getDateDeLivraison());
        }
        CommandeEntity saved = commandeComponent.save(entity);
        return CommandeMapper.toResponse(saved);
    }

}
