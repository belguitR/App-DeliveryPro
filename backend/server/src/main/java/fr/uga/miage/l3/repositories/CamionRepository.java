package fr.uga.miage.l3.repositories;

import fr.uga.miage.l3.models.CamionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CamionRepository extends JpaRepository<CamionEntity, String> {
}
