package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.enums.EtatTournee;
import fr.uga.miage.l3.models.EquipeLivreursEntity;
import fr.uga.miage.l3.models.JourneeEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.repositories.EquipeLivreursRepository;
import fr.uga.miage.l3.repositories.JourneeRepository;
import fr.uga.miage.l3.repositories.LivreurRepository;
import fr.uga.miage.l3.repositories.TourneeRepository;
import fr.uga.miage.l3.responses.TourneeResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TourneeControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private JourneeRepository journeeRepository;
    @Autowired
    private TourneeRepository tourneeRepository;
    @Autowired
    private LivreurRepository livreurRepository;
    @Autowired
    private EquipeLivreursRepository equipeLivreursRepository;

    @AfterEach
    void tearDown() {
        tourneeRepository.deleteAll();
        livreurRepository.deleteAll();
        equipeLivreursRepository.deleteAll();
        journeeRepository.deleteAll();
    }

    @Test
    void getLastTourneeByLivreurEmail_returnsTourneeResponse() {
        // Given
        String email = "livreur@example.com";

        EquipeLivreursEntity equipe = new EquipeLivreursEntity();
        equipeLivreursRepository.save(equipe);

        LivreurEntity livreur = new LivreurEntity();
        livreur.setTrigramme("LIV");
        livreur.setEmail(email);
        livreur.setEquipeLivreurs(equipe);
        livreurRepository.save(livreur);

        JourneeEntity journee = new JourneeEntity();
        journee.setReference("J001");
        journee.setDate(LocalDate.now());
        journeeRepository.save(journee);

        TourneeEntity tournee = new TourneeEntity();
        tournee.setReference("T001");
        tournee.setEtat(EtatTournee.EN_COURS);
        tournee.setEquipeLivreurs(equipe);
        tournee.setJour(journee);
        tourneeRepository.save(tournee);

        // When
        ResponseEntity<TourneeResponse> response = restTemplate.getForEntity(
                "/api/tournees/last/by-livreur/" + email, TourneeResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getReference()).isEqualTo("T001");
    }

    @Test
    void getLastTourneeByLivreurEmail_notFound_returns404() {
        // Given
        String email = "quiestce@gmail.com";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/tournees/last/by-livreur/" + email, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void patchTourneeEtat_returnsTourneeResponse() {
        // Given

        TourneeEntity tournee = new TourneeEntity();
        tournee.setReference("T001");
        tournee.setEtat(EtatTournee.EN_COURS);
        tourneeRepository.save(tournee);

        TourneeResponse tourneeUpdate = new TourneeResponse();
        tourneeUpdate.setReference("T001");
        tourneeUpdate.setEtat("COMPLETEE");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TourneeResponse> httpEntity = new HttpEntity<>(tourneeUpdate, headers);

        // When
        ResponseEntity<TourneeResponse> response = restTemplate.exchange(
                "/api/tournees/T001/etat",
                HttpMethod.PATCH,
                httpEntity,
                TourneeResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEtat()).isEqualTo("COMPLETEE");
    }

    @Test
    void patchTourneeEtat_notFound_returns404() {
        // Given
        TourneeResponse tourneeUpdate = new TourneeResponse();
        tourneeUpdate.setReference("T002");
        tourneeUpdate.setEtat("NOTFOUND");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TourneeResponse> httpEntity = new HttpEntity<>(tourneeUpdate, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/tournees/T002/etat",
                HttpMethod.PATCH,
                httpEntity,
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void patchTourneeEtat_invalidEtat_returns400() {
        // Given

        TourneeEntity tournee = new TourneeEntity();
        tournee.setReference("T002");
        tournee.setEtat(EtatTournee.EN_COURS);
        tourneeRepository.save(tournee);

        TourneeResponse tourneeUpdate = new TourneeResponse();
        tourneeUpdate.setReference("T002");
        tourneeUpdate.setEtat("INVALID");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TourneeResponse> httpEntity = new HttpEntity<>(tourneeUpdate, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/tournees/T002/etat",
                HttpMethod.PATCH,
                httpEntity,
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}

