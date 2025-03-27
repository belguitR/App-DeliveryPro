package fr.uga.miage.l3.repositories;

import fr.uga.miage.l3.enums.StatusEquipeLivreur;
import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.repository.EquipeLivreurRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
public class EquipeLivreurRepositoryTest {

    @Autowired
    private EquipeLivreurRepository equipeLivreurRepository;

    @Test
        //Ce test vérifie que la méthode findByNumEquipe fonctionne correctement en trouvant une équipe par son numéro.
    void testFindByNumEquipe(){
        // GIVEN
        EquipeLivreurEntity equipeLivreurEntity = EquipeLivreurEntity
                .builder()
                .numEquipe(1)
                .status(StatusEquipeLivreur.PRET)
                .datesAffectes(Set.of(LocalDate.now()))
                .build();

        equipeLivreurRepository.save(equipeLivreurEntity);

        // WHEN
        Optional<EquipeLivreurEntity> equipeTrouver = equipeLivreurRepository.findByNumEquipe(1);

        // THEN
        assertThat(equipeTrouver).isPresent();
        assertThat(equipeTrouver.get().getNumEquipe()).isEqualTo(1);
        assertThat(equipeTrouver.get().getStatus()).isEqualTo(StatusEquipeLivreur.PRET);
    }

    @Test
        // Ce test vérifie que la méthode save fonctionne correctement en sauvegardant une nouvelle équipe et en vérifiant ses propriétés.
    void testSaveAndFindEquipeLivreur() {
        // GIVEN
        EquipeLivreurEntity equipeLivreur = EquipeLivreurEntity.builder()
                .numEquipe(2)
                .status(StatusEquipeLivreur.PRET)
                .datesAffectes(Set.of(LocalDate.now()))
                .build();

        // WHEN
        EquipeLivreurEntity savedEquipe = equipeLivreurRepository.save(equipeLivreur);

        // THEN
        assertThat(savedEquipe).isNotNull();
        assertThat(savedEquipe.getNumEquipe()).isEqualTo(2);
        assertThat(savedEquipe.getStatus()).isEqualTo(StatusEquipeLivreur.PRET);
    }

    @Test
        // Ce test vérifie que la méthode deleteById fonctionne correctement en supprimant une équipe par son ID.
    void testDeleteEquipeLivreur() {
        // GIVEN
        EquipeLivreurEntity equipeLivreur = EquipeLivreurEntity.builder()
                .numEquipe(3)
                .status(StatusEquipeLivreur.NON_DISPONIBLE)
                .datesAffectes(Set.of(LocalDate.now()))
                .build();

        equipeLivreurRepository.save(equipeLivreur);

        // WHEN
        equipeLivreurRepository.deleteById(3);

        // THEN
        Optional<EquipeLivreurEntity> deletedEquipe = equipeLivreurRepository.findById(3);
        assertThat(deletedEquipe).isEmpty();
    }
}