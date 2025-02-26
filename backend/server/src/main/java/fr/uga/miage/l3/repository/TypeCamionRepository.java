package fr.uga.miage.l3.repository;

import fr.uga.miage.l3.models.TypeCamionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeCamionRepository extends JpaRepository<TypeCamionEntity,Integer> {
}
