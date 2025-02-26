package fr.uga.miage.l3.repository;

import fr.uga.miage.l3.models.PlanificateurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanificateurRepository extends JpaRepository<PlanificateurEntity,Integer> {
}
