package fr.uga.miage.l3.repositories;

import fr.uga.miage.l3.models.TourneeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TourneeRepository extends JpaRepository<TourneeEntity, String> {
    Optional<TourneeEntity> findByEquipeLivreurs_IdEquipeAndJour_Date(Integer equipeLivreursId, LocalDate date);
}
