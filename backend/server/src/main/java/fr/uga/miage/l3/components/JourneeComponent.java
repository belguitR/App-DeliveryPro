package fr.uga.miage.l3.components;

import fr.uga.miage.l3.models.JourneeEntity;
import fr.uga.miage.l3.repositories.JourneeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JourneeComponent {
    private final JourneeRepository journeeRepository;

    public List<JourneeEntity> findAll() {
        return journeeRepository.findAll();
    }

    public Optional<JourneeEntity> findByReference(String reference) {
        return journeeRepository.findById(reference);
    }

    public JourneeEntity save(JourneeEntity entity) {
        return journeeRepository.save(entity);
    }

    public void deleteByReference(String reference) {
        journeeRepository.deleteById(reference);
    }
}
