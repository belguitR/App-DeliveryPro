package fr.uga.miage.l3.repository;

import fr.uga.miage.l3.models.EquipeLivreurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipeLivreurRepository extends JpaRepository<EquipeLivreurEntity,Integer> {



    Optional<EquipeLivreurEntity> findByNumEquipe(Integer numEquipe);

}
