package fr.uga.miage.l3.repositories;

import fr.uga.miage.l3.models.JourneeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JourneeRepository extends JpaRepository<JourneeEntity, String> {
}
