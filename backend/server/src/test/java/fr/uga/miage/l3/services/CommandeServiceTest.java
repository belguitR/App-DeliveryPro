package fr.uga.miage.l3.services;

import fr.uga.miage.l3.components.CommandeComponent;
import fr.uga.miage.l3.dataTypes.Adresse;
import fr.uga.miage.l3.enums.EtatCommande;
import fr.uga.miage.l3.exceptions.technical.ResourceNotFoundException;
import fr.uga.miage.l3.mappers.CommandeMapper;
import fr.uga.miage.l3.models.ClientEntity;
import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.models.LigneEntity;
import fr.uga.miage.l3.requests.CommandePatchRequest;
import fr.uga.miage.l3.responses.ClientResponse;
import fr.uga.miage.l3.responses.CommandeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        when(commandeComponent.findByReference("NON TROUVé")).thenReturn(Optional.empty());

        CommandePatchRequest patch = new CommandePatchRequest();
        patch.setEtat("LIVREE");

        assertThrows(ResourceNotFoundException.class, () -> commandeService.patchCommande("NON TROUVé", patch));
    }

    @Test
    void getCommandesByTourneeReference_shouldReturnCommandeResponses() {
        String tourneeRef = "T001";
        CommandeEntity c1 = new CommandeEntity();
        c1.setReference("CMD1");
        CommandeEntity c2 = new CommandeEntity();
        c2.setReference("CMD2");

        when(commandeComponent.findByTourneeReference(tourneeRef)).thenReturn(List.of(c1, c2));

        List<CommandeResponse> result = commandeService.getCommandesByTourneeReference(tourneeRef);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CommandeResponse::getReference)
                .containsExactlyInAnyOrder("CMD1", "CMD2");
        verify(commandeComponent).findByTourneeReference(tourneeRef);
    }

    @Test
    void getCommandesByTourneeReference_shouldThrowIfNoneFound() {
        String tourneeRef = "T404";
        when(commandeComponent.findByTourneeReference(tourneeRef)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> commandeService.getCommandesByTourneeReference(tourneeRef));
        verify(commandeComponent).findByTourneeReference(tourneeRef);
    }
    @Test
    void getAllCommandes_shouldReturnList() {
        CommandeEntity entity = new CommandeEntity();
        entity.setReference("CMD1");
        entity.setDateDeCreation(LocalDateTime.now());
        entity.setNote(5);
        entity.setTdmTheorique(42);
        entity.setCommentaire("commentaire");
        entity.setDateDeLivraison(LocalDateTime.now().plusDays(1));

        ClientEntity client = new ClientEntity();
        client.setCode("C001");
        client.setNom("Nom");
        client.setPrenom("Prenom");
        client.setEmail("mail@mail.com");
        client.setEtat(null);
        entity.setClient(client);

        LigneEntity ligne = new LigneEntity();
        ligne.setReference("L001");
        entity.setLignes(Set.of(ligne));

        when(commandeComponent.findAll()).thenReturn(List.of(entity));

        List<CommandeResponse> result = commandeService.getAllCommandes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReference()).isEqualTo("CMD1");
        assertThat(result.get(0).getClient().getCode()).isEqualTo("C001");
        assertThat(result.get(0).getLigneReferences()).contains("L001");
        verify(commandeComponent).findAll();

        CommandeResponse mapped = CommandeMapper.toResponse(entity);
        assertThat(mapped.getReference()).isEqualTo(entity.getReference());
        assertThat(mapped.getClient().getCode()).isEqualTo("C001");
        assertThat(mapped.getLigneReferences()).contains("L001");

        assertThat(CommandeMapper.toResponse(null)).isNull();

        CommandeEntity entityNoClient = new CommandeEntity();
        entityNoClient.setReference("CMD2");
        entityNoClient.setClient(null);
        CommandeResponse mappedNoClient = CommandeMapper.toResponse(entityNoClient);
        assertThat(mappedNoClient.getClient()).isNull();

        CommandeEntity entityNoLignes = new CommandeEntity();
        entityNoLignes.setReference("CMD3");
        entityNoLignes.setLignes(null);
        CommandeResponse mappedNoLignes = CommandeMapper.toResponse(entityNoLignes);
        assertThat(mappedNoLignes.getLigneReferences()).isNull();

        ClientResponse clientResp = CommandeMapper.toClientResponse(client);
        assertThat(clientResp.getCode()).isEqualTo("C001");
        assertThat(CommandeMapper.toClientResponse(null)).isNull();
    }
    @Test
    void testAdressePojo() {
        Adresse a = new Adresse();
        a.setAdresse("rue des taillées");
        a.setCodePostal("38400");
        a.setVille("Grenoble");
        assertEquals("rue des taillées", a.getAdresse());
        assertEquals("38400", a.getCodePostal());
        assertEquals("Grenoble", a.getVille());
    }



}
