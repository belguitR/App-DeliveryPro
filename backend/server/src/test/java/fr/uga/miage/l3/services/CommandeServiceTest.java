package fr.uga.miage.l3.services;

import fr.uga.miage.l3.components.CommandeComponent;
import fr.uga.miage.l3.enums.EtatCommande;
import fr.uga.miage.l3.exceptions.ResourceNotFoundException;
import fr.uga.miage.l3.mappers.CommandeMapper;
import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.requests.CommandePatchRequest;
import fr.uga.miage.l3.responses.CommandeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandeServiceTest {

    @Mock
    private CommandeComponent commandeComponent;

    @InjectMocks
    private CommandeService commandeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCommandes_shouldReturnList() {
        CommandeEntity entity = new CommandeEntity();
        entity.setReference("CMD1");
        when(commandeComponent.findAll()).thenReturn(List.of(entity));

        List<CommandeResponse> result = commandeService.getAllCommandes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReference()).isEqualTo("CMD1");
        verify(commandeComponent).findAll();
    }

    @Test
    void getCommandeByReference_shouldReturnCommande() {
        CommandeEntity entity = new CommandeEntity();
        entity.setReference("CMD2");
        when(commandeComponent.findByReference("CMD2")).thenReturn(Optional.of(entity));

        CommandeResponse result = commandeService.getCommandeByReference("CMD2");

        assertThat(result.getReference()).isEqualTo("CMD2");
        verify(commandeComponent).findByReference("CMD2");
    }

    @Test
    void getCommandeByReference_shouldThrowIfNotFound() {
        when(commandeComponent.findByReference("NOTFOUND")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commandeService.getCommandeByReference("NOTFOUND"));
    }

    @Test
    void patchCommande_shouldUpdateEtatAndDateDeLivraison() {
        CommandeEntity entity = new CommandeEntity();
        entity.setReference("CMD3");
        entity.setEtat(EtatCommande.OUVERTE);

        when(commandeComponent.findByReference("CMD3")).thenReturn(Optional.of(entity));
        when(commandeComponent.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CommandePatchRequest patch = new CommandePatchRequest();
        patch.setEtat("LIVREE");
        patch.setDateDeLivraison(LocalDateTime.now().plusDays(1));

        CommandeResponse result = commandeService.patchCommande("CMD3", patch);

        assertThat(result.getEtat()).isEqualTo("LIVREE");
        assertThat(result.getDateDeLivraison()).isEqualTo(patch.getDateDeLivraison());
        verify(commandeComponent).save(entity);
    }

    @Test
    void patchCommande_shouldThrowIfNotFound() {
        when(commandeComponent.findByReference("NOTFOUND")).thenReturn(Optional.empty());

        CommandePatchRequest patch = new CommandePatchRequest();
        patch.setEtat("LIVREE");

        assertThrows(ResourceNotFoundException.class, () -> commandeService.patchCommande("NOTFOUND", patch));
    }
}
