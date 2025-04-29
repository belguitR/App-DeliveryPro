package fr.uga.miage.l3.components;

import fr.uga.miage.l3.enums.EtatTournee;
import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.repositories.TourneeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TourneeComponentTest {

    @Mock
    private TourneeRepository tourneeRepository;

    @InjectMocks
    private TourneeComponent tourneeComponent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTournee() {
        TourneeEntity tournee1 = TourneeEntity
                .builder()
                .reference("T001")
                .etat(EtatTournee.EN_COURS)
                .build();
        TourneeEntity tournee2 = TourneeEntity
                .builder()
                .reference("T002")
                .etat(EtatTournee.EN_COURS)
                .build();

        when(tourneeRepository.findAll()).thenReturn(List.of(tournee1, tournee2));

        Set<TourneeEntity> result = tourneeComponent.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(tournee1, tournee2);
        verify(tourneeRepository).findAll();
    }

    @Test
    void testGetTourneeByReference() {
        TourneeEntity tournee = TourneeEntity
                .builder()
                .reference("T003")
                .etat(EtatTournee.EN_COURS)
                .build();

        when(tourneeRepository.findById("T003")).thenReturn(Optional.of(tournee));

        Optional<TourneeEntity> result = tourneeComponent.findByReference("T003");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(tournee);
        verify(tourneeRepository).findById("T003");
    }

    @Test
    void testFindByReference_shouldReturnEmptyIfNotFound() {
        when(tourneeRepository.findById("NOTFOUND")).thenReturn(Optional.empty());

        Optional<TourneeEntity> result = tourneeComponent.findByReference("NOTFOUND");

        assertThat(result).isEmpty();
        verify(tourneeRepository).findById("NOTFOUND");
    }

    @Test
    void testSaveTournee() {
        TourneeEntity tournee = TourneeEntity
                .builder()
                .reference("T004")
                .etat(EtatTournee.PLANIFIEE)
                .build();

        when(tourneeRepository.save(tournee)).thenReturn(tournee);

        TourneeEntity result = tourneeComponent.save(tournee);

        assertThat(result).isEqualTo(tournee);
        verify(tourneeRepository).save(tournee);
    }

    @Test
    void testFindByEquipeLivreursIdAndDate_shouldReturnTournee() {
        Integer equipeLivreursId = 1;
        LocalDate date = LocalDate.now();
        TourneeEntity tournee = TourneeEntity
                .builder()
                .reference("T002")
                .etat(EtatTournee.EN_COURS)
                .build();

        when(tourneeRepository.findByEquipeLivreurs_IdEquipeAndJour_Date(equipeLivreursId, date)).thenReturn(Optional.of(tournee));

        Optional<TourneeEntity> result = tourneeComponent.findByEquipeLivreursIdAndDate(equipeLivreursId, date);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(tournee);
        verify(tourneeRepository).findByEquipeLivreurs_IdEquipeAndJour_Date(equipeLivreursId, date);
    }

    @Test
    void testFindByEquipeLivreursIdAndDate_shouldReturnEmptyIfNotFound() {
        Integer equipeLivreursId = 1;
        LocalDate date = LocalDate.now();

        when(tourneeRepository.findByEquipeLivreurs_IdEquipeAndJour_Date(equipeLivreursId, date)).thenReturn(Optional.empty());

        Optional<TourneeEntity> result = tourneeComponent.findByEquipeLivreursIdAndDate(equipeLivreursId, date);

        assertThat(result).isEmpty();
        verify(tourneeRepository).findByEquipeLivreurs_IdEquipeAndJour_Date(equipeLivreursId, date);
    }
}
