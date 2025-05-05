package fr.uga.miage.l3.services;

import fr.uga.miage.l3.components.EquipeLivreursComponent;
import fr.uga.miage.l3.enums.StatusEquipeLivreurs;
import fr.uga.miage.l3.exceptions.technical.BusinessRuleException;
import fr.uga.miage.l3.exceptions.technical.ResourceNotFoundException;
import fr.uga.miage.l3.mappers.EquipeLivreursMapper;
import fr.uga.miage.l3.models.EquipeLivreursEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repositories.LivreurRepository;
import fr.uga.miage.l3.responses.EquipeLivreursResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EquipeLivreursServiceTest {

    @Mock
    private EquipeLivreursComponent equipeComponent;

    @Mock
    private LivreurRepository livreurRepository;

    @InjectMocks
    private EquipeLivreursService equipeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllEquipes_shouldReturnList() {
        EquipeLivreursEntity entity = new EquipeLivreursEntity();
        entity.setIdEquipe(1);
        entity.setStatus(StatusEquipeLivreurs.PRET);

        LivreurEntity livreur = new LivreurEntity();
        livreur.setTrigramme("TRI");
        entity.setLivreurs(Set.of(livreur));

        when(equipeComponent.findAll()).thenReturn(List.of(entity));

        List<EquipeLivreursResponse> result = equipeService.getAllEquipes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdEquipe()).isEqualTo(1);
        assertThat(result.get(0).getStatus()).isEqualTo("PRET");
        assertThat(result.get(0).getLivreurIds()).contains("TRI");
        verify(equipeComponent).findAll();

        EquipeLivreursResponse resp = EquipeLivreursMapper.toResponse(entity);
        assertThat(resp.getIdEquipe()).isEqualTo(1);
        assertThat(resp.getStatus()).isEqualTo("PRET");
        assertThat(resp.getLivreurIds()).contains("TRI");

        assertThat(EquipeLivreursMapper.toResponse(null)).isNull();
        assertThat(EquipeLivreursMapper.toEntity(null)).isNull();

        EquipeLivreursResponse response = new EquipeLivreursResponse(2, "PRET", List.of("TRI"));
        EquipeLivreursEntity entity2 = EquipeLivreursMapper.toEntity(response);
        assertThat(entity2.getIdEquipe()).isEqualTo(2);
        assertThat(entity2.getStatus()).isEqualTo(StatusEquipeLivreurs.PRET);
    }

    @Test
    void createEquipe_shouldSaveAndReturnResponse() {
        LivreurEntity l1 = new LivreurEntity();
        l1.setTrigramme("A");
        l1.setAPermis(true);
        when(livreurRepository.findById("A")).thenReturn(Optional.of(l1));

        EquipeLivreursResponse input = new EquipeLivreursResponse();
        input.setStatus("PRET");
        input.setLivreurIds(List.of("A"));

        EquipeLivreursEntity saved = new EquipeLivreursEntity();
        saved.setStatus(StatusEquipeLivreurs.PRET);
        saved.setLivreurs(Set.of(l1));

        when(equipeComponent.save(any())).thenReturn(saved);

        EquipeLivreursResponse result = equipeService.createEquipe(input);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("PRET");
        assertThat(result.getLivreurIds()).containsExactly("A");
        verify(equipeComponent).save(any());
    }


    @Test
    void createEquipe_zeroLivreurs_shouldFail() {
        EquipeLivreursResponse req = new EquipeLivreursResponse();
        req.setStatus("PRET");
        req.setLivreurIds(List.of());

        assertThrows(BusinessRuleException.class, () -> equipeService.createEquipe(req));
    }

    @Test
    void createEquipe_oneLivreurWithPermis_shouldSucceed() {
        LivreurEntity l1 = new LivreurEntity(); l1.setTrigramme("A"); l1.setAPermis(true);
        when(livreurRepository.findById("A")).thenReturn(Optional.of(l1));
        when(equipeComponent.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EquipeLivreursResponse req = new EquipeLivreursResponse();
        req.setStatus("PRET");
        req.setLivreurIds(List.of("A"));

        EquipeLivreursResponse result = equipeService.createEquipe(req);
        assertThat(result).isNotNull();
        assertThat(result.getLivreurIds()).containsExactly("A");
    }

    @Test
    void createEquipe_oneLivreurNoPermis_shouldFail() {
        LivreurEntity l1 = new LivreurEntity(); l1.setTrigramme("A"); l1.setAPermis(false);
        when(livreurRepository.findById("A")).thenReturn(Optional.of(l1));

        EquipeLivreursResponse req = new EquipeLivreursResponse();
        req.setStatus("PRET");
        req.setLivreurIds(List.of("A"));

        assertThrows(BusinessRuleException.class, () -> equipeService.createEquipe(req));
    }
    @Test
    void createEquipe_twoLivreursBothPermis_shouldSucceed() {
        LivreurEntity l1 = new LivreurEntity(); l1.setTrigramme("A"); l1.setAPermis(true);
        LivreurEntity l2 = new LivreurEntity(); l2.setTrigramme("B"); l2.setAPermis(true);
        when(livreurRepository.findById("A")).thenReturn(Optional.of(l1));
        when(livreurRepository.findById("B")).thenReturn(Optional.of(l2));
        when(equipeComponent.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EquipeLivreursResponse req = new EquipeLivreursResponse();
        req.setStatus("PRET");
        req.setLivreurIds(List.of("A", "B"));

        EquipeLivreursResponse result = equipeService.createEquipe(req);
        assertThat(result).isNotNull();
        assertThat(result.getLivreurIds()).containsExactlyInAnyOrder("A", "B");
    }
    @Test
    void createEquipe_twoLivreursOnePermis_shouldSucceed() {
        LivreurEntity l1 = new LivreurEntity(); l1.setTrigramme("A"); l1.setAPermis(false);
        LivreurEntity l2 = new LivreurEntity(); l2.setTrigramme("B"); l2.setAPermis(true);
        when(livreurRepository.findById("A")).thenReturn(Optional.of(l1));
        when(livreurRepository.findById("B")).thenReturn(Optional.of(l2));
        when(equipeComponent.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EquipeLivreursResponse req = new EquipeLivreursResponse();
        req.setStatus("PRET");
        req.setLivreurIds(List.of("A", "B"));

        EquipeLivreursResponse result = equipeService.createEquipe(req);
        assertThat(result).isNotNull();
        assertThat(result.getLivreurIds()).containsExactlyInAnyOrder("A", "B");
    }
    @Test
    void createEquipe_twoLivreursNoPermis_shouldFail() {
        LivreurEntity l1 = new LivreurEntity(); l1.setTrigramme("A"); l1.setAPermis(false);
        LivreurEntity l2 = new LivreurEntity(); l2.setTrigramme("B"); l2.setAPermis(false);
        when(livreurRepository.findById("A")).thenReturn(Optional.of(l1));
        when(livreurRepository.findById("B")).thenReturn(Optional.of(l2));

        EquipeLivreursResponse req = new EquipeLivreursResponse();
        req.setStatus("PRET");
        req.setLivreurIds(List.of("A", "B"));

        assertThrows(BusinessRuleException.class, () -> equipeService.createEquipe(req));
    }
    @Test
    void createEquipe_threeLivreurs_shouldFail() {
        LivreurEntity l1 = new LivreurEntity(); l1.setTrigramme("A"); l1.setAPermis(true);
        LivreurEntity l2 = new LivreurEntity(); l2.setTrigramme("B"); l2.setAPermis(true);
        LivreurEntity l3 = new LivreurEntity(); l3.setTrigramme("C"); l3.setAPermis(true);
        when(livreurRepository.findById("A")).thenReturn(Optional.of(l1));
        when(livreurRepository.findById("B")).thenReturn(Optional.of(l2));
        when(livreurRepository.findById("C")).thenReturn(Optional.of(l3));

        EquipeLivreursResponse req = new EquipeLivreursResponse();
        req.setStatus("PRET");
        req.setLivreurIds(List.of("A", "B", "C"));

        assertThrows(BusinessRuleException.class, () -> equipeService.createEquipe(req));
    }
    @Test
    void createEquipe_livreurAlreadyInEquipe_shouldFail() {
        LivreurEntity l1 = new LivreurEntity(); l1.setTrigramme("A"); l1.setAPermis(true);
        l1.setEquipeLivreurs(new EquipeLivreursEntity());
        LivreurEntity l2 = new LivreurEntity(); l2.setTrigramme("B"); l2.setAPermis(true);
        when(livreurRepository.findById("A")).thenReturn(Optional.of(l1));
        when(livreurRepository.findById("B")).thenReturn(Optional.of(l2));

        EquipeLivreursResponse req = new EquipeLivreursResponse();
        req.setStatus("PRET");
        req.setLivreurIds(List.of("A", "B"));

        assertThrows(BusinessRuleException.class, () -> equipeService.createEquipe(req));
    }



    @Test
    void deleteEquipe_shouldDeleteIfExists() {
        EquipeLivreursEntity equipe = new EquipeLivreursEntity();
        when(equipeComponent.findById(1)).thenReturn(Optional.of(equipe));
        when(livreurRepository.findByEquipeLivreurs(equipe)).thenReturn(List.of());

        equipeService.deleteEquipe(1);

        verify(equipeComponent).deleteById(1);
    }


    @Test
    void deleteEquipe_shouldThrowIfNotFound() {
        when(equipeComponent.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> equipeService.deleteEquipe(1));
        verify(equipeComponent, never()).deleteById(anyInt());
    }

    @Test
    void patchEquipeStatus_shouldUpdateStatus() {
        EquipeLivreursEntity entity = new EquipeLivreursEntity();
        when(equipeComponent.findById(1)).thenReturn(Optional.of(entity));
        when(equipeComponent.save(entity)).thenReturn(entity);

        EquipeLivreursResponse result = equipeService.patchEquipeStatus(1, "PRET");

        assertThat(entity.getStatus()).isEqualTo(StatusEquipeLivreurs.PRET);
        assertThat(result).isNotNull();
    }

    @Test
    void patchEquipeStatus_shouldThrowIfNotFound() {
        when(equipeComponent.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> equipeService.patchEquipeStatus(1, "PRET"));
    }

    @Test
    void patchEquipeStatus_shouldThrowIfStatusInvalid() {
        EquipeLivreursEntity entity = new EquipeLivreursEntity();
        when(equipeComponent.findById(1)).thenReturn(Optional.of(entity));
        assertThrows(IllegalArgumentException.class, () -> equipeService.patchEquipeStatus(1, "INVALID_STATUS"));
    }

    @Test
    void deleteEquipe_shouldRemoveLivreurEquipeReference() {
        EquipeLivreursEntity equipe = new EquipeLivreursEntity();
        equipe.setIdEquipe(1);
        LivreurEntity l1 = new LivreurEntity();
        l1.setEquipeLivreurs(equipe);
        LivreurEntity l2 = new LivreurEntity();
        l2.setEquipeLivreurs(equipe);

        when(equipeComponent.findById(1)).thenReturn(Optional.of(equipe));
        when(livreurRepository.findByEquipeLivreurs(equipe)).thenReturn(List.of(l1, l2));

        equipeService.deleteEquipe(1);

        assertThat(l1.getEquipeLivreurs()).isNull();
        assertThat(l2.getEquipeLivreurs()).isNull();
        verify(equipeComponent).deleteById(1);
    }

}
