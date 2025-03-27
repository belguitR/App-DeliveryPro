package fr.uga.miage.l3.repositories;

import fr.uga.miage.l3.enums.EtatCommande;
import fr.uga.miage.l3.enums.EtatTournee;
import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.repository.CommandeRepository;
import fr.uga.miage.l3.repository.TourneeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
public class TourneeRepositoryTest {
    @Autowired
    private TourneeRepository tourneeRepository;
    @Autowired
    private CommandeRepository commandeRepository;

    @Test
    void testFindByNumTourneeWithCommandes() {
        // GIVEN - Création et sauvegarde d'une tournée avec des commandes
        TourneeEntity tournee = TourneeEntity.builder()
                .date(new Date())
                .distanceAParcourir(100)
                .montant(500)
                .tempsDeMontageTheorique(60)
                .tempsDeMontageEffectif(65)
                .etat(EtatTournee.EN_COURS)
                .build();

        tournee = tourneeRepository.save(tournee);

        CommandeEntity commande1 = CommandeEntity.builder().reference(101).tournee(tournee).etat(EtatCommande.EN_LIVRAISON).build();
        CommandeEntity commande2 = CommandeEntity.builder().reference(102).tournee(tournee).etat(EtatCommande.EN_LIVRAISON).build();

        // ajouter les commandes à la tournée avant de sauvegarder la tournée
        tournee.setCommandes(Arrays.asList(commande1, commande2));
        commande1.setTournee(tournee);
        commande2.setTournee(tournee);
        tournee = tourneeRepository.save(tournee);

        // WHEN: Recherche de la tournée avec ses commandes
        TourneeEntity foundTournee = tourneeRepository.findByNumTourneeWithCommandes(tournee.getNumTournee());

        // THEN: Vérification des résultats
        assertThat(foundTournee).isNotNull();
        assertThat(foundTournee.getNumTournee()).isEqualTo(tournee.getNumTournee());
        assertThat(foundTournee.getCommandes()).isNotNull();
        assertThat(foundTournee.getCommandes().size()).isEqualTo(2);
    }
}
