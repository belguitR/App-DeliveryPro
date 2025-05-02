package fr.uga.miage.l3.services;

import fr.uga.miage.l3.components.LivreurComponent;
import fr.uga.miage.l3.exceptions.technical.ResourceNotFoundException;
import fr.uga.miage.l3.mappers.LivreurMapper;
import fr.uga.miage.l3.models.EntrepotEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.models.EquipeLivreursEntity;
import fr.uga.miage.l3.repositories.EquipeLivreursRepository;
import fr.uga.miage.l3.repositories.EntrepotRepository;
import fr.uga.miage.l3.responses.LivreurResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LivreurServiceTest {

    @Mock
    private LivreurComponent livreurComponent;

    @Mock
    private EquipeLivreursRepository equipeRepo;

    @Mock
    private EntrepotRepository entrepotRepository;

    @InjectMocks
    private LivreurService livreurService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllLivreurs_shouldReturnList() {
        LivreurEntity entity = new LivreurEntity();
        entity.setTrigramme("TRI");
        entity.setNom("Nom");
        entity.setPrenom("Prenom");
        entity.setEmail("mail@mail.com");
        entity.setPhoto("photo.png");
        entity.setTelephone("123456789");
        entity.setAffecte(true);
        entity.setAPermis(true);

        EquipeLivreursEntity equipe = new EquipeLivreursEntity();
        equipe.setIdEquipe(42);
        entity.setEquipeLivreurs(equipe);

        EntrepotEntity entrepot = new EntrepotEntity();
        entrepot.setNom("Entrepot");
        entity.setEntrepot(entrepot);

        when(livreurComponent.findAll()).thenReturn(List.of(entity));

        List<LivreurResponse> result = livreurService.getAllLivreurs();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTrigramme()).isEqualTo("TRI");
        assertThat(result.get(0).getEquipeId()).isEqualTo(42);
        assertThat(result.get(0).getEntrepotNom()).isEqualTo("Entrepot");
        verify(livreurComponent).findAll();

        LivreurResponse resp = LivreurMapper.toResponse(entity);
        assertThat(resp.getTrigramme()).isEqualTo("TRI");
        assertThat(resp.getEquipeId()).isEqualTo(42);
        assertThat(resp.getEntrepotNom()).isEqualTo("Entrepot");

        assertThat(LivreurMapper.toResponse(null)).isNull();

        LivreurResponse response = new LivreurResponse("TRI", "Nom", "Prenom", "mail@mail.com", "photo.png", "123456789", true, true, 42, "Entrepot");
        LivreurEntity entity2 = LivreurMapper.toEntity(response, equipe, entrepot);
        assertThat(entity2.getTrigramme()).isEqualTo("TRI");
        assertThat(entity2.getEquipeLivreurs()).isEqualTo(equipe);
        assertThat(entity2.getEntrepot()).isEqualTo(entrepot);

        assertThat(LivreurMapper.toEntity(null, null, null)).isNull();
    }


    @Test
    void getAllLivreurs_shouldReturnEmptyList() {
        when(livreurComponent.findAll()).thenReturn(List.of());

        List<LivreurResponse> result = livreurService.getAllLivreurs();

        assertThat(result).isEmpty();
        verify(livreurComponent).findAll();
    }

    @Test
    void getLivreurByEmail_shouldReturnLivreur() {
        LivreurEntity entity = new LivreurEntity();
        entity.setEmail("test@mail.com");
        when(livreurComponent.findByEmail("test@mail.com")).thenReturn(Optional.of(entity));

        LivreurResponse result = livreurService.getLivreurByEmail("test@mail.com");

        assertThat(result.getEmail()).isEqualTo("test@mail.com");
        verify(livreurComponent).findByEmail("test@mail.com");
    }

    @Test
    void getLivreurByEmail_shouldThrowIfNotFound() {
        when(livreurComponent.findByEmail("notfound@mail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> livreurService.getLivreurByEmail("notfound@mail.com"));
        verify(livreurComponent).findByEmail("notfound@mail.com");
    }

    @Test
    void updateLivreur_shouldUpdateAllFields() {
        LivreurEntity entity = new LivreurEntity();
        entity.setTrigramme("EBD");
        entity.setEmail("test@mail.com");
        entity.setNom("OldNom");
        entity.setPrenom("OldPrenom");
        entity.setAffecte(false);
        entity.setAPermis(false);
        entity.setPhoto("oldphoto.png");
        entity.setTelephone("000000000");
        when(livreurComponent.findByEmail("test@mail.com")).thenReturn(Optional.of(entity));

        LivreurResponse update = new LivreurResponse();
        update.setTrigramme("EBD");
        update.setNom("NewNom");
        update.setPrenom("NewPrenom");
        update.setAffecte(true);
        update.setAPermis(true);
        update.setPhoto("photo.png");
        update.setTelephone("123456789");
        update.setEquipeId(42);
        update.setEntrepotNom("Grenis");
        update.setEmail("test@mail.com");

        EquipeLivreursEntity equipe = new EquipeLivreursEntity();
        equipe.setIdEquipe(42);
        when(equipeRepo.findById(42)).thenReturn(Optional.of(equipe));

        EntrepotEntity entrepot = new EntrepotEntity();
        entrepot.setNom("Grenis");
        when(entrepotRepository.findById("Grenis")).thenReturn(Optional.of(entrepot));

        when(livreurComponent.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        LivreurResponse result = livreurService.updateLivreur("test@mail.com", update);

        assertThat(result).isNotNull();
        assertThat(result.getNom()).isEqualTo("NewNom");
        assertThat(result.getPrenom()).isEqualTo("NewPrenom");
        assertThat(result.isAffecte()).isTrue();
        assertThat(result.isAPermis()).isTrue();
        assertThat(result.getPhoto()).isEqualTo("photo.png");
        assertThat(result.getTelephone()).isEqualTo("123456789");
        assertThat(result.getEquipeId()).isEqualTo(42);
        assertThat(result.getEntrepotNom()).isEqualTo("Grenis");
        verify(livreurComponent).save(any());
    }

    @Test
    void updateLivreur_shouldSetEquipeAndEntrepotToNullIfNotProvided() {
        LivreurEntity entity = new LivreurEntity();
        entity.setEmail("test@mail.com");
        entity.setEquipeLivreurs(new EquipeLivreursEntity());
        entity.setEntrepot(new EntrepotEntity());
        when(livreurComponent.findByEmail("test@mail.com")).thenReturn(Optional.of(entity));

        LivreurResponse update = new LivreurResponse();
        update.setEquipeId(null);
        update.setEntrepotNom(null);

        when(livreurComponent.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        LivreurResponse result = livreurService.updateLivreur("test@mail.com", update);

        assertThat(result.getEquipeId()).isNull();
        assertThat(result.getEntrepotNom()).isNull();
        verify(livreurComponent).save(any());
    }

    @Test
    void updateLivreur_shouldThrowIfNotFound() {
        when(livreurComponent.findByEmail("notfound@mail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> livreurService.updateLivreur("notfound@mail.com", new LivreurResponse()));
        verify(livreurComponent).findByEmail("notfound@mail.com");
    }
}
