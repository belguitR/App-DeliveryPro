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
