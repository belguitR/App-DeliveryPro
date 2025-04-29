package fr.uga.miage.l3.services;

import fr.uga.miage.l3.components.EquipeLivreursComponent;
import fr.uga.miage.l3.enums.StatusEquipeLivreurs;
import fr.uga.miage.l3.exceptions.BusinessRuleException;
import fr.uga.miage.l3.exceptions.ResourceNotFoundException;
import fr.uga.miage.l3.mappers.EquipeLivreursMapper;
import fr.uga.miage.l3.models.EquipeLivreursEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repositories.LivreurRepository;
import fr.uga.miage.l3.responses.EquipeLivreursResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.HashSet;
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
        EquipeLivreursEntity entity = EquipeLivreursEntity
                .builder()
                .status(StatusEquipeLivreurs.PRET)
                .build();
        when(equipeComponent.findAll()).thenReturn(List.of(entity));

        List<EquipeLivreursResponse> result = equipeService.getAllEquipes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("PRET");
        verify(equipeComponent).findAll();
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


}
