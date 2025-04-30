package fr.uga.miage.l3.components;

import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.repositories.CommandeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CommandeComponentTest {

    @Mock
    private CommandeRepository commandeRepository;

    @InjectMocks
    private CommandeComponent commandeComponent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnList() {
        CommandeEntity c1 = CommandeEntity
                .builder()
                .reference("CMD1")
                .build();
        CommandeEntity c2 = CommandeEntity
                .builder()
                .reference("CMD2")
                .build();
        when(commandeRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CommandeEntity> result = commandeComponent.findAll();

        assertThat(result).containsExactly(c1, c2);
        verify(commandeRepository).findAll();
    }

    @Test
    void findByReference_shouldReturnCommande() {
        CommandeEntity c1 = new CommandeEntity();
        c1.setReference("CMD1");
        when(commandeRepository.findById("CMD1")).thenReturn(Optional.of(c1));

        Optional<CommandeEntity> result = commandeComponent.findByReference("CMD1");

        assertThat(result).contains(c1);
        verify(commandeRepository).findById("CMD1");
    }

    @Test
    void findByReference_shouldReturnEmptyOptional() {
        when(commandeRepository.findById("CMD2")).thenReturn(Optional.empty());

        Optional<CommandeEntity> result = commandeComponent.findByReference("CMD2");

        assertThat(result).isEmpty();
        verify(commandeRepository).findById("CMD2");
    }

    @Test
    void save_shouldReturnSavedCommande() {
        CommandeEntity c1 = new CommandeEntity();
        c1.setReference("CMD1");
        when(commandeRepository.save(c1)).thenReturn(c1);

        CommandeEntity result = commandeComponent.save(c1);

        assertThat(result).isSameAs(c1);
        verify(commandeRepository).save(c1);
    }

    @Test
    void findByTourneeReference_shouldReturnCommandes() {
        String tourneeRef = "T001";
        CommandeEntity c1 = CommandeEntity.builder().reference("CMD1").build();
        CommandeEntity c2 = CommandeEntity.builder().reference("CMD2").build();

        when(commandeRepository.findByTournee_Reference(tourneeRef)).thenReturn(List.of(c1, c2));

        List<CommandeEntity> result = commandeComponent.findByTourneeReference(tourneeRef);

        assertThat(result).containsExactlyInAnyOrder(c1, c2);
        verify(commandeRepository).findByTournee_Reference(tourneeRef);
    }


    @Test
    void findByTourneeReference_shouldReturnEmptyListIfNoneFound() {
        String tourneeRef = "T404";
        when(commandeRepository.findByTournee_Reference(tourneeRef)).thenReturn(List.of());

        List<CommandeEntity> result = commandeComponent.findByTourneeReference(tourneeRef);

        assertThat(result).isEmpty();
        verify(commandeRepository).findByTournee_Reference(tourneeRef);
    }

}
