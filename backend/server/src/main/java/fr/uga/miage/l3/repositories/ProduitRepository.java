package fr.uga.miage.l3.repositories;

import fr.uga.miage.l3.models.ProduitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduitRepository extends JpaRepository<ProduitEntity, String> {
}
