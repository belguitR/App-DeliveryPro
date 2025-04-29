package fr.uga.miage.l3.components;

import fr.uga.miage.l3.models.TourneeEntity;
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

    public TourneeEntity save(TourneeEntity tournee) {
        return tourneeRepository.save(tournee);
    }

    public Optional<TourneeEntity> findByReference(String reference) {
        return tourneeRepository.findById(reference);
    }

    public Optional<TourneeEntity> findByEquipeLivreursIdAndDate(Integer equipeLivreursId, LocalDate date) {
        return tourneeRepository.findByEquipeLivreurs_IdEquipeAndJour_Date(equipeLivreursId, date);
    }
    public Set<TourneeEntity> findAll() {
        return tourneeRepository.findAll().stream().collect(Collectors.toSet());
    }
}
