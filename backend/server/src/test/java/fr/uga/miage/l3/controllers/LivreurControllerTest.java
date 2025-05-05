package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repositories.LivreurRepository;
import fr.uga.miage.l3.responses.LivreurResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LivreurControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LivreurRepository livreurRepository;

    @AfterEach
    void tearDown() {
        livreurRepository.deleteAll();
    }

    @Test
    void getAllLivreurs_returnsList() {
        // Given
        LivreurEntity entity = new LivreurEntity();
        entity.setTrigramme("EBD");
        entity.setNom("Berand");
        entity.setPrenom("Elisa");
        entity.setEmail("ebd@mobilis.fr");
        livreurRepository.save(entity);

        // When
        ResponseEntity<LivreurResponse[]> response = restTemplate.getForEntity(
                "/api/livreurs", LivreurResponse[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(1);
        assertThat(List.of(response.getBody()))
                .anyMatch(l -> "ebd@mobilis.fr".equals(l.getEmail()));
    }

    @Test
    void getLivreurByEmail_returnsLivreur() {
        // Given
        LivreurEntity entity = new LivreurEntity();
        entity.setTrigramme("EBD");
        entity.setNom("Berand");
        entity.setPrenom("Elisa");
        entity.setEmail("ebd@mobilis.fr");
        livreurRepository.save(entity);

        // When
        ResponseEntity<LivreurResponse> response = restTemplate.getForEntity(
                "/api/livreurs/ebd@mobilis.fr", LivreurResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("ebd@mobilis.fr");
    }

    @Test
    void getLivreurByEmail_notFound_returns404() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/livreurs/notfound@mail.com", String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateLivreur_updatesLivreur() {
        // Given
        LivreurEntity entity = new LivreurEntity();
        entity.setTrigramme("EBD");
        entity.setNom("Berand");
        entity.setPrenom("Elisa");
        entity.setEmail("ebd@mobilis.fr");
        livreurRepository.save(entity);

        LivreurResponse update = new LivreurResponse();
        update.setTrigramme("EBD");
        update.setNom("BerandUpdated");
        update.setPrenom("Elisa");
        update.setEmail("ebd@mobilis.fr");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LivreurResponse> httpEntity = new HttpEntity<>(update, headers);

        // When
        ResponseEntity<LivreurResponse> response = restTemplate.exchange(
                "/api/livreurs/ebd@mobilis.fr",
                HttpMethod.PUT,
                httpEntity,
                LivreurResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getNom()).isEqualTo("BerandUpdated");
    }

    @Test
    void updateLivreur_notFound_returns404() {
        LivreurResponse update = new LivreurResponse();
        update.setTrigramme("NOTFOUND");
        update.setEmail("notfound@mail.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LivreurResponse> httpEntity = new HttpEntity<>(update, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/livreurs/notfound@mail.com",
                HttpMethod.PUT,
                httpEntity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
