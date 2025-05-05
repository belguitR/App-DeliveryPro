package fr.uga.miage.l3.components;

import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repositories.LivreurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LivreurComponentTest {

    @Mock
    private LivreurRepository livreurRepository;

    @InjectMocks
    private LivreurComponent livreurComponent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnList() {
        LivreurEntity entity1 = new LivreurEntity();
        LivreurEntity entity2 = new LivreurEntity();
        when(livreurRepository.findAll()).thenReturn(List.of(entity1, entity2));

        List<LivreurEntity> result = livreurComponent.findAll();

        assertThat(result).containsExactly(entity1, entity2);
        verify(livreurRepository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList() {
        when(livreurRepository.findAll()).thenReturn(List.of());

        List<LivreurEntity> result = livreurComponent.findAll();

        assertThat(result).isEmpty();
        verify(livreurRepository).findAll();
    }

    @Test
    void findByEmail_shouldReturnLivreur() {
        LivreurEntity entity = new LivreurEntity();
        when(livreurRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(entity));

        Optional<LivreurEntity> result = livreurComponent.findByEmail("test@mail.com");

        assertThat(result).contains(entity);
        verify(livreurRepository).findByEmail("test@mail.com");
    }

    @Test
    void findByEmail_shouldReturnEmptyOptional() {
        when(livreurRepository.findByEmail("notfound@mail.com")).thenReturn(Optional.empty());

        Optional<LivreurEntity> result = livreurComponent.findByEmail("notfound@mail.com");

        assertThat(result).isEmpty();
        verify(livreurRepository).findByEmail("notfound@mail.com");
    }

    @Test
    void save_shouldReturnSavedEntity() {
        LivreurEntity entity = new LivreurEntity();
        when(livreurRepository.save(entity)).thenReturn(entity);

        LivreurEntity result = livreurComponent.save(entity);

        assertThat(result).isSameAs(entity);
        verify(livreurRepository).save(entity);
    }
}
