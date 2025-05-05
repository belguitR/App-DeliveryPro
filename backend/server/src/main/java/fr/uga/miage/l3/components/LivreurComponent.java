package fr.uga.miage.l3.components;

import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repositories.LivreurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LivreurComponent {
    private final LivreurRepository livreurRepository;

    public List<LivreurEntity> findAll() {
        return livreurRepository.findAll();
    }

    public Optional<LivreurEntity> findByEmail(String email) {
        return livreurRepository.findByEmail(email);
    }

    public LivreurEntity save(LivreurEntity entity) {
        return livreurRepository.save(entity);
    }
}
