package fr.uga.miage.l3.components;

import fr.uga.miage.l3.models.CamionEntity;
import fr.uga.miage.l3.repositories.CamionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CamionComponent {

    private final CamionRepository camionRepository;

    public List<CamionEntity> findAll() {
        return camionRepository.findAll();
    }
}
