package fr.uga.miage.l3.services;

import fr.uga.miage.l3.components.TourneeComponent;
import fr.uga.miage.l3.enums.EtatTournee;
import fr.uga.miage.l3.exceptions.technical.BusinessRuleException;
import fr.uga.miage.l3.exceptions.technical.ResourceNotFoundException;
import fr.uga.miage.l3.mappers.TourneeMapper;
import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.requests.TourneePatchEtatRequest;
import fr.uga.miage.l3.responses.TourneeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TourneeService {

    private final TourneeComponent tourneeComponent;

    public TourneeResponse getLastTourneeByLivreurEmail(String email) {
        return tourneeComponent.findLastTourneeByLivreurEmail(email)
                .map(TourneeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune tournée trouvée pour le livreur : " + email));
    }

    public TourneeResponse patchTourneeEtat(String reference, TourneePatchEtatRequest request) {
        TourneeEntity tournee = tourneeComponent.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Tournee not found: " + reference));

        if (request.getEtat() == null) {
            throw new BusinessRuleException("Etat is required");
        }

        try {
            tournee.setEtat(EtatTournee.valueOf(request.getEtat().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Invalid etat: " + request.getEtat());
        }

        tourneeComponent.save(tournee);
        return TourneeMapper.toResponse(tournee);
    }

}
