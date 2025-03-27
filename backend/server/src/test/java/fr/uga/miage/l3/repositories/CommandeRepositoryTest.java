package fr.uga.miage.l3.repositories;

import fr.uga.miage.l3.enums.EtatCommande;
import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.repository.CommandeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
public class CommandeRepositoryTest {
    @Autowired
    private CommandeRepository commandeRepository;

    @Test
    void testFindCommandeById() {
        // GIVEN
        CommandeEntity commande = CommandeEntity.builder()
                .reference(1)
                .etat(EtatCommande.OUVERTE)
                .build();

        commandeRepository.save(commande);

        // WHEN
        Optional<CommandeEntity> foundCommande = commandeRepository.findById(1);

        // THEN
        assertThat(foundCommande).isPresent();
        assertThat(foundCommande.get().getReference()).isEqualTo(commande.getReference());
        assertThat(foundCommande.get().getEtat()).isEqualTo(EtatCommande.OUVERTE);
    }

    @Test
    void testFindAllById() {
        // GIVEN
        CommandeEntity commande1 = CommandeEntity.builder()
                .reference(2)
                .etat(EtatCommande.LIVREE)
                .build();

        CommandeEntity commande2 = CommandeEntity.builder()
                .reference(3)
                .etat(EtatCommande.LIVREE)
                .build();

        commandeRepository.save(commande1);
        commandeRepository.save(commande2);

        // WHEN
        List<CommandeEntity> foundCommandes = commandeRepository.findAllById(List.of(2, 3));

        // THEN
        assertThat(foundCommandes).hasSize(2);
        assertThat(foundCommandes.get(0).getReference()).isEqualTo(2);
        assertThat(foundCommandes.get(1).getReference()).isEqualTo(3);
    }
}