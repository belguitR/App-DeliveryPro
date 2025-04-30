package fr.uga.miage.l3.repositories;

import fr.uga.miage.l3.models.EquipeLivreursEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipeLivreursRepository extends JpaRepository<EquipeLivreursEntity, Integer> {
    Optional<EquipeLivreursEntity> findByLivreursContaining(LivreurEntity livreur);
}
