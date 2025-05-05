package fr.uga.miage.l3.components;

import fr.uga.miage.l3.models.CamionEntity;
import fr.uga.miage.l3.repositories.CamionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;

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

}