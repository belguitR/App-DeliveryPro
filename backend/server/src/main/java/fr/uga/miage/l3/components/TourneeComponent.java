package fr.uga.miage.l3.components;

import fr.uga.miage.l3.models.EquipeLivreursEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.repositories.EquipeLivreursRepository;
import fr.uga.miage.l3.repositories.LivreurRepository;
import fr.uga.miage.l3.repositories.TourneeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TourneeComponent {

    private final TourneeRepository tourneeRepository;
    private final LivreurRepository livreurRepository;
    private final EquipeLivreursRepository equipeLivreursRepository;

    public Optional<TourneeEntity> findByReference(String reference) {
        return tourneeRepository.findById(reference);
    }

    public TourneeEntity save(TourneeEntity entity) {
        return tourneeRepository.save(entity);
    }
    public Optional<TourneeEntity> findLastTourneeByLivreurEmail(String email) {
        LivreurEntity livreur = livreurRepository.findByEmail(email).orElse(null);
        if (livreur == null) return Optional.empty();

        EquipeLivreursEntity equipe = equipeLivreursRepository.findByLivreursContaining(livreur).orElse(null);
        if (equipe == null) return Optional.empty();

        return tourneeRepository.findFirstByEquipeLivreursOrderByJour_DateDesc(equipe);
    }
}
