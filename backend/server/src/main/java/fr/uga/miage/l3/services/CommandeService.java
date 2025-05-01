package fr.uga.miage.l3.services;

import fr.uga.miage.l3.components.CommandeComponent;
import fr.uga.miage.l3.enums.EtatCommande;
import fr.uga.miage.l3.exceptions.technical.BusinessRuleException;
import fr.uga.miage.l3.exceptions.technical.ResourceNotFoundException;
import fr.uga.miage.l3.mappers.CommandeMapper;
import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.repositories.TourneeRepository;
import fr.uga.miage.l3.requests.CommandePatchRequest;
import fr.uga.miage.l3.responses.CommandeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandeService {
    private final CommandeComponent commandeComponent;
    private final TourneeRepository tourneeRepository;

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



    public List<CommandeResponse> getCommandesByTourneeReference(String tourneeReference) {
        List<CommandeEntity> commandes = commandeComponent.findByTourneeReference(tourneeReference);
        if (commandes.isEmpty()) {
            throw new ResourceNotFoundException("Tournée non trouvée: " + tourneeReference);
        }
        return commandes.stream()
                .map(CommandeMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    public CommandeResponse patchCommande(String reference, CommandePatchRequest request) {
        CommandeEntity entity = commandeComponent.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée: " + reference));
        if (request.getEtat() != null) {
            EtatCommande etat;
            try {
                etat = EtatCommande.valueOf(request.getEtat());
            } catch (IllegalArgumentException e) {
                throw new BusinessRuleException("Valeur Invalide pour etat : " + request.getEtat());
            }
            if (etat == EtatCommande.LIVREE && request.getDateDeLivraison() == null) {
                throw new BusinessRuleException("dateDeLivraison est requise quand l'etat est LIVREE");
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
