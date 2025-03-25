package fr.uga.miage.l3.repository;

import fr.uga.miage.l3.models.TourneeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TourneeRepository extends JpaRepository<TourneeEntity,Integer> {

    @Query("SELECT t FROM TourneeEntity t LEFT JOIN FETCH t.commandes WHERE t.numTournee = :numTournee")
    TourneeEntity findByNumTourneeWithCommandes(@Param("numTournee") Integer numTournee);

    TourneeEntity save(TourneeEntity tourneeEntity);
    Optional<TourneeEntity> findByNumTournee(Integer numTournee);


}
