package fr.uga.miage.l3.repository;

import fr.uga.miage.l3.models.CommandeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<CommandeEntity, Integer> {

    List<CommandeEntity> findByTourneeNumTournee(Integer numTournee);
}
