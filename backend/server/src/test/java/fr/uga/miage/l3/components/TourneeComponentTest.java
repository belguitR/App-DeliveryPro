package fr.uga.miage.l3.components;

import fr.uga.miage.l3.enums.EtatTournee;
import fr.uga.miage.l3.models.*;
import fr.uga.miage.l3.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TourneeComponentTest {

    @Mock
    private TourneeRepository tourneeRepository;
    @Mock
    private LivreurRepository livreurRepository;
    @Mock
    private EquipeLivreursRepository equipeLivreursRepository;

    @InjectMocks
    private TourneeComponent tourneeComponent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findLastTourneeByLivreurEmail_shouldReturnTournee() {
        String email = "test@livreur.com";
        LivreurEntity livreur = new LivreurEntity();
        livreur.setEmail(email);

        EquipeLivreursEntity equipe = new EquipeLivreursEntity();
        equipe.setLivreurs(Set.of(livreur));

        TourneeEntity tournee = TourneeEntity.builder()
                .reference("T002")
                .etat(EtatTournee.EN_COURS)
                .build();

        when(livreurRepository.findByEmail(email)).thenReturn(Optional.of(livreur));
        when(equipeLivreursRepository.findByLivreursContaining(livreur)).thenReturn(Optional.of(equipe));
        when(tourneeRepository.findFirstByEquipeLivreursOrderByJour_DateDesc(equipe)).thenReturn(Optional.of(tournee));

        Optional<TourneeEntity> result = tourneeComponent.findLastTourneeByLivreurEmail(email);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(tournee);

        verify(livreurRepository).findByEmail(email);
        verify(equipeLivreursRepository).findByLivreursContaining(livreur);
        verify(tourneeRepository).findFirstByEquipeLivreursOrderByJour_DateDesc(equipe);
    }

    @Test
    void findLastTourneeByLivreurEmail_shouldReturnEmptyIfLivreurNotFound() {
        String email = "notfound@livreur.com";
        when(livreurRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<TourneeEntity> result = tourneeComponent.findLastTourneeByLivreurEmail(email);

        assertThat(result).isEmpty();
        verify(livreurRepository).findByEmail(email);
        verifyNoInteractions(equipeLivreursRepository, tourneeRepository);
    }

    @Test
    void findLastTourneeByLivreurEmail_shouldReturnEmptyIfEquipeNotFound() {
        String email = "test@livreur.com";
        LivreurEntity livreur = new LivreurEntity();
        livreur.setEmail(email);

        when(livreurRepository.findByEmail(email)).thenReturn(Optional.of(livreur));
        when(equipeLivreursRepository.findByLivreursContaining(livreur)).thenReturn(Optional.empty());

        Optional<TourneeEntity> result = tourneeComponent.findLastTourneeByLivreurEmail(email);

        assertThat(result).isEmpty();
        verify(livreurRepository).findByEmail(email);
        verify(equipeLivreursRepository).findByLivreursContaining(livreur);
        verifyNoInteractions(tourneeRepository);
    }

    @Test
    void findLastTourneeByLivreurEmail_shouldReturnEmptyIfNoTourneeFound() {
        String email = "test@livreur.com";
        LivreurEntity livreur = new LivreurEntity();
        livreur.setEmail(email);

        EquipeLivreursEntity equipe = new EquipeLivreursEntity();
        equipe.setLivreurs(Set.of(livreur));

        when(livreurRepository.findByEmail(email)).thenReturn(Optional.of(livreur));
        when(equipeLivreursRepository.findByLivreursContaining(livreur)).thenReturn(Optional.of(equipe));
        when(tourneeRepository.findFirstByEquipeLivreursOrderByJour_DateDesc(equipe)).thenReturn(Optional.empty());

        Optional<TourneeEntity> result = tourneeComponent.findLastTourneeByLivreurEmail(email);

        assertThat(result).isEmpty();
        verify(livreurRepository).findByEmail(email);
        verify(equipeLivreursRepository).findByLivreursContaining(livreur);
        verify(tourneeRepository).findFirstByEquipeLivreursOrderByJour_DateDesc(equipe);
    }
}
