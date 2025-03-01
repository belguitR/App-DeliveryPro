package fr.uga.miage.l3.repository;

import fr.uga.miage.l3.models.CommandeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<CommandeEntity, Integer> {

    @Query("SELECT c FROM CommandeEntity c WHERE c.reference IN :ids")
    List<CommandeEntity> findAllById(@Param("ids") List<Integer> ids);

    List<CommandeEntity> findByTourneeNumTournee(Integer numTournee);
}
