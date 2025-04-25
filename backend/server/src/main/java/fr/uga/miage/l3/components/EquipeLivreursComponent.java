package fr.uga.miage.l3.components;

import fr.uga.miage.l3.models.EquipeLivreursEntity;
import fr.uga.miage.l3.repositories.EquipeLivreursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EquipeLivreursComponent {
    private final EquipeLivreursRepository equipeLivreursRepository;

    public List<EquipeLivreursEntity> findAll() {
        return equipeLivreursRepository.findAll();
    }
    public EquipeLivreursEntity save(EquipeLivreursEntity entity) {
        return equipeLivreursRepository.save(entity);
    }

    public Optional<EquipeLivreursEntity> findById(Integer id) {
        return equipeLivreursRepository.findById(id);
    }

    public void deleteById(Integer id) {
        equipeLivreursRepository.deleteById(id);
    }
}
