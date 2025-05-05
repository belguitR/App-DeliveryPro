package fr.uga.miage.l3.repositories;

import fr.uga.miage.l3.models.LigneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LigneRepository extends JpaRepository<LigneEntity, String> {
}
