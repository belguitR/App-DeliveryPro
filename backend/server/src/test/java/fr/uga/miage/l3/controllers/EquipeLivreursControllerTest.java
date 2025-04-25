package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.enums.StatusEquipeLivreurs;
import fr.uga.miage.l3.models.EquipeLivreursEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repositories.EquipeLivreursRepository;
import fr.uga.miage.l3.repositories.LivreurRepository;
import fr.uga.miage.l3.responses.EquipeLivreursResponse;
import fr.uga.miage.l3.requests.EquipeStatusPatchRequest;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EquipeLivreursControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EquipeLivreursRepository equipeRepo;

    @Autowired
    private LivreurRepository livreurRepository;

    @AfterEach
    void tearDown() {
        livreurRepository.deleteAll();
        equipeRepo.deleteAll();
    }
    @BeforeEach
    void setupRestTemplate() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        restTemplate.getRestTemplate().setRequestFactory(
                new HttpComponentsClientHttpRequestFactory(httpClient)
        );
    }



    @Test
    void getAllEquipes_returnsList() {
        // Given
        EquipeLivreursEntity entity = new EquipeLivreursEntity();
        entity.setStatus(StatusEquipeLivreurs.PRET);
        equipeRepo.save(entity);

        // When
        ResponseEntity<EquipeLivreursResponse[]> response = restTemplate.getForEntity(
                "/api/equipes", EquipeLivreursResponse[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(1);
    }

    @Test
    void createEquipe_returnsBadRequestIfNoLivreurIds() {
        EquipeLivreursResponse request = new EquipeLivreursResponse();
        request.setStatus("PRET");

        ResponseEntity<EquipeLivreursResponse> response = restTemplate.postForEntity(
                "/api/equipes", request, EquipeLivreursResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    void createEquipe_returnsCreatedWithLivreurIds() {
        LivreurEntity livreur1 = new LivreurEntity();
        livreur1.setTrigramme("JLC");
        livreur1.setAPermis(true);
        livreurRepository.save(livreur1);

        LivreurEntity livreur2 = new LivreurEntity();
        livreur2.setTrigramme("SMU");
        livreur2.setAPermis(false);
        livreurRepository.save(livreur2);

        EquipeLivreursResponse request = new EquipeLivreursResponse();
        request.setStatus("PRET");
        request.setLivreurIds(List.of("JLC", "SMU"));

        ResponseEntity<EquipeLivreursResponse> response = restTemplate.postForEntity(
                "/api/equipes", request, EquipeLivreursResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("PRET");
        assertThat(response.getBody().getLivreurIds()).containsExactlyInAnyOrder("JLC", "SMU");
    }



    @Test
    void patchEquipeStatus_updatesStatus() {
        // Given
        EquipeLivreursEntity entity = new EquipeLivreursEntity();
        entity.setStatus(StatusEquipeLivreurs.PRET);
        entity = equipeRepo.save(entity);

        EquipeStatusPatchRequest patchRequest = new EquipeStatusPatchRequest();
        patchRequest.setStatus("PRET");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EquipeStatusPatchRequest> httpEntity = new HttpEntity<>(patchRequest, headers);

        // When
        ResponseEntity<EquipeLivreursResponse> response = restTemplate.exchange(
                "/api/equipes/" + entity.getIdEquipe() + "/status",
                HttpMethod.PATCH,
                httpEntity,
                EquipeLivreursResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("PRET");
    }

    @Test
    void deleteEquipe_removesEquipe() {
        // Given
        EquipeLivreursEntity entity = new EquipeLivreursEntity();
        entity.setStatus(StatusEquipeLivreurs.PRET);
        entity = equipeRepo.save(entity);

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/equipes/" + entity.getIdEquipe(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(equipeRepo.findById(entity.getIdEquipe())).isEmpty();
    }

    @Test
    void patchEquipeStatus_invalidStatus_returnsBadRequest() {
        // Given
        EquipeLivreursEntity entity = new EquipeLivreursEntity();
        entity.setStatus(StatusEquipeLivreurs.PRET);
        entity = equipeRepo.save(entity);

        EquipeStatusPatchRequest patchRequest = new EquipeStatusPatchRequest();
        patchRequest.setStatus("INVALID_STATUS");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EquipeStatusPatchRequest> httpEntity = new HttpEntity<>(patchRequest, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/equipes/" + entity.getIdEquipe() + "/status",
                HttpMethod.PATCH,
                httpEntity,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
