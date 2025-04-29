package fr.uga.miage.l3.components;

import fr.uga.miage.l3.dataTypes.Adresse;
import fr.uga.miage.l3.dataTypes.GeoPosition;
import fr.uga.miage.l3.enums.EtatClient;
import fr.uga.miage.l3.models.ClientEntity;
import fr.uga.miage.l3.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ClientComponentTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientComponent clientComponent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllClient() {
        Adresse adresse = new Adresse("123 Rue", "75000", "Paris");
        GeoPosition geoPosition = new GeoPosition(48.8566, 2.3522);
        ClientEntity client1 = ClientEntity
                .builder()
                .code("C001")
                .nom("Martin")
                .prenom("Luc")
                .etat(EtatClient.INSCRIT)
                .adresse(adresse)
                .coords(geoPosition)
                .build();

        ClientEntity client2 = ClientEntity
                .builder()
                .code("C002")
                .nom("Alex")
                .prenom("Terieur")
                .etat(EtatClient.INSCRIT)
                .adresse(adresse)
                .coords(geoPosition)
                .build();

        when(clientRepository.findAll()).thenReturn(List.of(client1, client2));

        List<ClientEntity> result = clientComponent.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(client1, client2);
        verify(clientRepository).findAll();
    }

    @Test
    void testGetClientByCode() {
        Adresse adresse = new Adresse("123 Rue", "75000", "Paris");
        GeoPosition geoPosition = new GeoPosition(48.8566, 2.3522);
        ClientEntity client = new ClientEntity("C001", "client1@example.com", "John", "Doe", adresse, geoPosition, EtatClient.LIVRE, null);

        when(clientRepository.findById("C001")).thenReturn(Optional.of(client));

        Optional<ClientEntity> result = clientComponent.findByCode("C001");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(client);
        verify(clientRepository).findById("C001");
    }

    @Test
    void testFindByCode_shouldReturnEmptyIfNotFound() {
        when(clientRepository.findById("NOTFOUND")).thenReturn(Optional.empty());

        Optional<ClientEntity> result = clientComponent.findByCode("NOTFOUND");

        assertThat(result).isEmpty();
        verify(clientRepository).findById("NOTFOUND");
    }

    @Test
    void testSaveClient() {
        Adresse adresse = new Adresse("123 Rue", "75000", "Paris");
        GeoPosition geoPosition = new GeoPosition(48.8566, 2.3522);
        ClientEntity client = new ClientEntity("C001", "client1@example.com", "John", "Doe", adresse, geoPosition, EtatClient.A_LIVRER, null);

        when(clientRepository.save(client)).thenReturn(client);

        ClientEntity result = clientComponent.save(client);

        assertThat(result).isEqualTo(client);
        verify(clientRepository).save(client);
    }
}
