package fr.uga.miage.l3.services;

import fr.uga.miage.l3.components.TourneeComponent;
import fr.uga.miage.l3.exceptions.ResourceNotFoundException;
import fr.uga.miage.l3.mappers.TourneeMapper;
import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.responses.TourneeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TourneeService {

    private final TourneeComponent tourneeComponent;

    public TourneeResponse getLastTourneeByLivreurEmail(String email) {
        return tourneeComponent.findLastTourneeByLivreurEmail(email)
                .map(TourneeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune tournée trouvée pour le livreur : " + email));
    }
}
