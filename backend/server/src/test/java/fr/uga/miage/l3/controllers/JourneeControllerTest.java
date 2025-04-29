package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.models.JourneeEntity;
import fr.uga.miage.l3.models.PlanificateurEntity;
import fr.uga.miage.l3.repositories.JourneeRepository;
import fr.uga.miage.l3.repositories.PlanificateurRepository;
import fr.uga.miage.l3.requests.JourneeRequest;
import fr.uga.miage.l3.requests.TourneeRequest;
import fr.uga.miage.l3.responses.JourneeResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class JourneeControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JourneeRepository journeeRepository;

    @Autowired
    private PlanificateurRepository planificateurRepository;

    @AfterEach
    void tearDown() {
        journeeRepository.deleteAll();
        planificateurRepository.deleteAll();
    }

    @Test
    void getAllJournees_returnsList() {
        JourneeEntity entity = new JourneeEntity();
        entity.setReference("J001");
        entity.setDate(LocalDate.now());
        journeeRepository.save(entity);

        ResponseEntity<JourneeResponse[]> response = restTemplate.getForEntity(
                "/api/journees", JourneeResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(1);
        assertThat(response.getBody()[0].getReference()).isEqualTo("J001");
    }

    @Test
    void getJourneeByReference_returnsJournee() {
        JourneeEntity entity = new JourneeEntity();
        entity.setReference("J002");
        entity.setDate(LocalDate.now());
        journeeRepository.save(entity);

        ResponseEntity<JourneeResponse> response = restTemplate.getForEntity(
                "/api/journees/J002", JourneeResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getReference()).isEqualTo("J002");
    }

    @Test
    void getJourneeByReference_notFound_returns404() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/journees/NOTFOUND", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createJournee_returnsCreated() {
        PlanificateurEntity planificateur = new PlanificateurEntity();
        planificateur.setTrigramme("PL001");
        planificateur.setEmail("pl001@orga.com");
        planificateurRepository.save(planificateur);

        JourneeRequest request = new JourneeRequest();
        request.setReference("J003");
        request.setDate(LocalDate.now());
        request.setPlanificateurEmail("pl001@orga.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<JourneeRequest> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<JourneeResponse> response = restTemplate.postForEntity(
                "/api/journees", httpEntity, JourneeResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getReference()).isEqualTo("J003");
        assertThat(response.getBody().getPlanificateurEmail()).isEqualTo("pl001@orga.com");
    }


    @Test
    void createJournee_duplicateReference_returns400() {
        JourneeEntity entity = new JourneeEntity();
        entity.setReference("J004");
        entity.setDate(LocalDate.now());
        journeeRepository.save(entity);

        JourneeRequest request = new JourneeRequest();
        request.setReference("J004");
        request.setDate(LocalDate.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<JourneeRequest> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/journees", httpEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Journee reference must be unique");
    }

    @Test
    void deleteJournee_removesJournee() {
        JourneeEntity entity = new JourneeEntity();
        entity.setReference("J005");
        entity.setDate(LocalDate.now());
        journeeRepository.save(entity);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/journees/J005",
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(journeeRepository.findById("J005")).isEmpty();
    }
}
