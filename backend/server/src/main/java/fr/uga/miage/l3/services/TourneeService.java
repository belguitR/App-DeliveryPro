package fr.uga.miage.l3.services;

import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.repository.TourneeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourneeService {

    private final TourneeRepository tourneeRepository;

    public List<TourneeEntity> getAllTournees() {
        return tourneeRepository.findAll();
    }

    @Transactional
    public TourneeEntity createTournee(TourneeEntity tourneeEntity, Integer equipeLivreurId, List<Integer> commandesIds) {
        // Logique existante pour créer une tournée, par exemple :
        // associer l'équipe, lister et ordonner les commandes, etc.
        return tourneeRepository.save(tourneeEntity);
    }

    @Transactional
    public void deleteTournee(Integer idTournee) {
        TourneeEntity tournee = tourneeRepository.findByNumTournee(idTournee)
                .orElseThrow(() -> new EntityNotFoundException("Tournée non trouvée avec l'ID : " + idTournee));
        tourneeRepository.delete(tournee);
    }

    public TourneeEntity getTourneeDuJourParLivreur(String emailLivreur) {
        // Implémentez la logique pour récupérer la tournée du jour
        throw new UnsupportedOperationException("Méthode non implémentée");
    }
}
