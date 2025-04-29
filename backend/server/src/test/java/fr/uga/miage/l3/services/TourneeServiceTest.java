package fr.uga.miage.l3.services;

import fr.uga.miage.l3.components.TourneeComponent;
import fr.uga.miage.l3.enums.EtatTournee;
import fr.uga.miage.l3.exceptions.ResourceNotFoundException;
import fr.uga.miage.l3.mappers.TourneeMapper;
import fr.uga.miage.l3.models.CamionEntity;
import fr.uga.miage.l3.models.EntrepotEntity;
import fr.uga.miage.l3.models.EquipeLivreursEntity;
import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.responses.TourneeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TourneeServiceTest {

    @Mock
    private TourneeComponent tourneeComponent;

    @InjectMocks
    private TourneeService tourneeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTodayTourneeByEquipe_shouldReturnTourneeResponse() {
        Integer equipeId = 1;
        LocalDate today = LocalDate.now();

        CamionEntity camion = CamionEntity
                .builder()
                .immatriculation("1234-AB-56")
                .build();

        EquipeLivreursEntity el = EquipeLivreursEntity
                .builder()
                .idEquipe(equipeId)
                .build();
        TourneeEntity tournee = TourneeEntity
                .builder()
                .reference("T001")
                .etat(EtatTournee.EN_COURS)
                .camion(camion)
                .distanceAParcourir(100.0)
                .tempsAssemblageReel(20)
                .lettre("A")
                .build();

        tournee.setEquipeLivreurs(el);

        when(tourneeComponent.findByEquipeLivreursIdAndDate(equipeId, today)).thenReturn(Optional.of(tournee));
        TourneeResponse expectedResponse = TourneeMapper.toResponse(tournee);
        TourneeResponse result = tourneeService.getTodayTourneeByEquipe(equipeId);

        assertThat(result.getReference()).isEqualTo("T001");
        assertThat(result.getReference()).isEqualTo(expectedResponse.getReference());
        verify(tourneeComponent).findByEquipeLivreursIdAndDate(equipeId, today);
    }

    @Test
    void getTodayTourneeByEquipe_shouldThrowIfTourneeNotFound() {
        Integer equipeId = 1;
        LocalDate today = LocalDate.now();

        when(tourneeComponent.findByEquipeLivreursIdAndDate(equipeId, today)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tourneeService.getTodayTourneeByEquipe(equipeId));
    }
}
