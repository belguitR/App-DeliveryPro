package fr.uga.miage.l3.components;

import fr.uga.miage.l3.models.CamionEntity;
import fr.uga.miage.l3.models.TypeCamionEntity;
import fr.uga.miage.l3.repositories.CamionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CamionComponentTest {

    @Mock
    private CamionRepository camionRepository;

    @InjectMocks
    private CamionComponent camionComponent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCamions() {
        CamionEntity camion1 = CamionEntity
                .builder()
                .code("C001")
                .immatriculation("123ABC")
                .kilometrage(1000L)
                .entrepot(null)
                .tournees(null)
                .build();

        CamionEntity camion2 = CamionEntity
                .builder()
                .code("C002")
                .immatriculation("123DEF")
                .kilometrage(1000L)
                .entrepot(null)
                .tournees(null)
                .build();

        when(camionRepository.findAll()).thenReturn(List.of(camion1, camion2));

        List<CamionEntity> result = camionComponent.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(camion1, camion2);
        verify(camionRepository).findAll();
    }

    @Test
    void testGetCamionByCode() {
        CamionEntity camion = CamionEntity
                .builder()
                .code("C001")
                .immatriculation("123ABC")
                .kilometrage(1000L)
                .entrepot(null)
                .tournees(null)
                .build();
        when(camionRepository.findById("C001")).thenReturn(Optional.of(camion));

        Optional<CamionEntity> result = camionComponent.findByCode("C001");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(camion);
        verify(camionRepository).findById("C001");
    }

    @Test
    void testFindByCode_shouldReturnEmptyIfNotFound() {
        when(camionRepository.findById("NOTFOUND")).thenReturn(Optional.empty());

        Optional<CamionEntity> result = camionComponent.findByCode("NOTFOUND");

        assertThat(result).isEmpty();
        verify(camionRepository).findById("NOTFOUND");
    }

    @Test
    void testSaveCamion() {
        CamionEntity camion = CamionEntity
                .builder()
                .code("C001")
                .immatriculation("123ABC")
                .kilometrage(1000L)
                .entrepot(null)
                .tournees(null)
                .build();

        when(camionRepository.save(camion)).thenReturn(camion);

        CamionEntity result = camionComponent.save(camion);

        assertThat(result).isEqualTo(camion);
        verify(camionRepository).save(camion);
    }
}
