package fr.uga.miage.l3.repository;

import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivreurRepository extends JpaRepository<LivreurEntity, String> {
    Optional<LivreurEntity> findByEmail(String email);

    List<LivreurEntity> findAllByIdEmployeIn(List<String> ids);


}
