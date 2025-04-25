package fr.uga.miage.l3.components;

import fr.uga.miage.l3.models.JourneeEntity;
import fr.uga.miage.l3.repositories.JourneeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JourneeComponentTest {

    @Mock
    private JourneeRepository journeeRepository;

    @InjectMocks
    private JourneeComponent journeeComponent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnList() {
        JourneeEntity j1 = new JourneeEntity();
        j1.setReference("J001");
        JourneeEntity j2 = new JourneeEntity();
        j2.setReference("J002");
        when(journeeRepository.findAll()).thenReturn(List.of(j1, j2));

        List<JourneeEntity> result = journeeComponent.findAll();

        assertThat(result).containsExactly(j1, j2);
        verify(journeeRepository).findAll();
    }

    @Test
    void findByReference_shouldReturnJournee() {
        JourneeEntity j1 = new JourneeEntity();
        j1.setReference("J001");
        when(journeeRepository.findById("J001")).thenReturn(Optional.of(j1));

        Optional<JourneeEntity> result = journeeComponent.findByReference("J001");

        assertThat(result).contains(j1);
        verify(journeeRepository).findById("J001");
    }

    @Test
    void findByReference_shouldReturnEmptyOptional() {
        when(journeeRepository.findById("J002")).thenReturn(Optional.empty());

        Optional<JourneeEntity> result = journeeComponent.findByReference("J002");

        assertThat(result).isEmpty();
        verify(journeeRepository).findById("J002");
    }

    @Test
    void save_shouldReturnSavedJournee() {
        JourneeEntity j1 = new JourneeEntity();
        j1.setReference("J003");
        j1.setDate(LocalDate.now());
        when(journeeRepository.save(j1)).thenReturn(j1);

        JourneeEntity result = journeeComponent.save(j1);

        assertThat(result).isSameAs(j1);
        verify(journeeRepository).save(j1);
    }

    @Test
    void deleteByReference_shouldCallRepository() {
        journeeComponent.deleteByReference("J004");
        verify(journeeRepository).deleteById("J004");
    }
}
