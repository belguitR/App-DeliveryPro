package fr.uga.miage.l3.repositories;

import fr.uga.miage.l3.models.EquipeLivreursEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LivreurRepository extends JpaRepository<LivreurEntity, String> {
    Optional<LivreurEntity> findByEmail(String email);

    List<LivreurEntity> findByEquipeLivreurs(EquipeLivreursEntity equipe);
}