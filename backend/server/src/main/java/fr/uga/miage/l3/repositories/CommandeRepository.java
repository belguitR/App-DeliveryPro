package fr.uga.miage.l3.repositories;

import fr.uga.miage.l3.models.CommandeEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommandeRepository extends JpaRepository<CommandeEntity, String> {

    @EntityGraph(attributePaths = {"lignes", "client"})
    List<CommandeEntity> findAll();

}
