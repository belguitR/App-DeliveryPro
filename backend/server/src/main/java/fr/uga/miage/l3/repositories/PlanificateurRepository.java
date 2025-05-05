package fr.uga.miage.l3.repositories;

import fr.uga.miage.l3.models.PlanificateurEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanificateurRepository extends JpaRepository<PlanificateurEntity, String> {
    Optional<PlanificateurEntity> findByEmail(String email);
}
