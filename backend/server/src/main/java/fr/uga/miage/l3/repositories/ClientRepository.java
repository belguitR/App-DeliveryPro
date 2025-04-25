package fr.uga.miage.l3.repositories;

import fr.uga.miage.l3.models.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<ClientEntity, String> {
}
