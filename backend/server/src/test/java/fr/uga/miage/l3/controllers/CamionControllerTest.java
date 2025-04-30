package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.models.CamionEntity;
import fr.uga.miage.l3.models.TypeCamionEntity;
import fr.uga.miage.l3.repositories.CamionRepository;
import fr.uga.miage.l3.repositories.TypeCamionRepository;
import fr.uga.miage.l3.responses.CamionResponse;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CamionControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CamionRepository camionRepository;

    @Autowired
    private TypeCamionRepository typeCamionRepository;

    @BeforeEach
    void setup() {
        // Clean DB before each test
        camionRepository.deleteAll();
        typeCamionRepository.deleteAll();

        // Setup RestTemplate for PATCH/PUT support
        CloseableHttpClient httpClient = HttpClients.createDefault();
        restTemplate.getRestTemplate().setRequestFactory(
                new HttpComponentsClientHttpRequestFactory(httpClient)
        );
    }

    @AfterEach
    void tearDown() {
        camionRepository.deleteAll();
        typeCamionRepository.deleteAll();
    }

    @Test
    void getAllCamions_returnsList_whenCamionsExist() {
        // Given
        TypeCamionEntity typeCamion = TypeCamionEntity.builder()
                .nom("Fourgon")
                .volume(20.0)
                .prixAuKm(1.5)
                .image("fourgon.png")
                .build();
        typeCamion = typeCamionRepository.save(typeCamion);

        CamionEntity camion = CamionEntity.builder()
                .code("C001")
                .immatriculation("AB-123-CD")
                .kilometrage(50000L)
                .typeCamion(typeCamion)
                .build();
        camionRepository.save(camion);

        // When
        ResponseEntity<CamionResponse[]> response = restTemplate.getForEntity(
                "/api/camions", CamionResponse[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(
                java.util.Arrays.stream(response.getBody())
                        .anyMatch(c -> "C001".equals(c.getCode()) &&
                                c.getTypeCamion() != null &&
                                "Fourgon".equals(c.getTypeCamion().getNom()))
        ).isTrue();
    }

    @Test
    void getAllCamions_returnsEmptyList_whenNoCamionsExist() {
        // Given:

        // When
        ResponseEntity<CamionResponse[]> response = restTemplate.getForEntity(
                "/api/camions", CamionResponse[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(0);
    }
}
