package fr.uga.miage.l3.services;

import fr.uga.miage.l3.components.TourneeComponent;
import fr.uga.miage.l3.enums.EtatTournee;
import fr.uga.miage.l3.exceptions.ResourceNotFoundException;
import fr.uga.miage.l3.mappers.TourneeMapper;
import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.responses.TourneeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
    void getLastTourneeByLivreurEmail_shouldReturnTourneeResponse() {
        String email = "test@livreur.com";
        TourneeEntity tournee = TourneeEntity.builder()
                .reference("T001")
                .etat(EtatTournee.EN_COURS)
                .build();

        TourneeResponse expectedResponse = TourneeMapper.toResponse(tournee);

        when(tourneeComponent.findLastTourneeByLivreurEmail(email)).thenReturn(Optional.of(tournee));

        TourneeResponse result = tourneeService.getLastTourneeByLivreurEmail(email);

        assertThat(result.getReference()).isEqualTo("T001");
        assertThat(result.getReference()).isEqualTo(expectedResponse.getReference());
        verify(tourneeComponent).findLastTourneeByLivreurEmail(email);
    }

    @Test
    void getLastTourneeByLivreurEmail_shouldThrowIfTourneeNotFound() {
        String email = "notfound@livreur.com";
        when(tourneeComponent.findLastTourneeByLivreurEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tourneeService.getLastTourneeByLivreurEmail(email));
    }
}
