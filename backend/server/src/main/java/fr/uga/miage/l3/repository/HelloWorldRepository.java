package fr.uga.miage.l3.repository;

import fr.uga.miage.l3.models.HelloWorldEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HelloWorldRepository extends JpaRepository<HelloWorldEntity,Long> {

    HelloWorldEntity findByName(String name);
}
