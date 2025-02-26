package fr.uga.miage.l3.repository;

import fr.uga.miage.l3.models.TourneeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourneeRepository extends JpaRepository<TourneeEntity,Integer> {
}
