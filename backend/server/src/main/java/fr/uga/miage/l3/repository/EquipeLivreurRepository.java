package fr.uga.miage.l3.repository;

import fr.uga.miage.l3.models.EquipeLivreurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipeLivreurRepository extends JpaRepository<EquipeLivreurEntity,Integer> {

    @Query("SELECT e FROM EquipeLivreurEntity e LEFT JOIN FETCH e.livreurs")
    List<EquipeLivreurEntity> findAllWithLivreurs();

    Optional<EquipeLivreurEntity> findByNumEquipe(Integer numEquipe);

}
