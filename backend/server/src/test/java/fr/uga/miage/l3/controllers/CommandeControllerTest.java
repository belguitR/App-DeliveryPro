package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.models.ClientEntity;
import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.enums.EtatCommande;
import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.repositories.ClientRepository;
import fr.uga.miage.l3.repositories.CommandeRepository;
import fr.uga.miage.l3.repositories.LigneRepository;
import fr.uga.miage.l3.repositories.TourneeRepository;
import fr.uga.miage.l3.responses.CommandeResponse;
import fr.uga.miage.l3.requests.CommandePatchRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CommandeControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LigneRepository ligneRepository;

    @Autowired
    private TourneeRepository tourneeRepository;

    @AfterEach
    void tearDown() {
        ligneRepository.deleteAll();
        commandeRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    void getAllCommandes_returnsList() {
        // Given
        CommandeEntity commande = new CommandeEntity();
        commande.setReference("CMD1");
        commande.setEtat(EtatCommande.OUVERTE);
        commande.setDateDeCreation(LocalDateTime.now());
        commandeRepository.save(commande);

        // When
        ResponseEntity<CommandeResponse[]> response = restTemplate.getForEntity(
                "/api/commandes", CommandeResponse[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(1);
        assertThat(response.getBody()[0].getReference()).isEqualTo("CMD1");
    }

    @Test
    void patchCommande_setEtatLivreeWithoutDate_shouldReturn400() {
        // Given
        CommandeEntity commande = new CommandeEntity();
        commande.setReference("CMDLIVREE");
        commande.setEtat(EtatCommande.EN_LIVRAISON);
        commandeRepository.save(commande);

        CommandePatchRequest patch = new CommandePatchRequest();
        patch.setEtat("LIVREE");
        patch.setDateDeLivraison(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CommandePatchRequest> httpEntity = new HttpEntity<>(patch, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/commandes/CMDLIVREE",
                HttpMethod.PATCH,
                httpEntity,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("dateDeLivraison est requise quand l'etat est LIVREE");
    }

    @Test
    void patchCommande_setEtatLivreeWithDate_shouldSucceed() {
        // Given
        CommandeEntity commande = new CommandeEntity();
        commande.setReference("CMDLIVREE2");
        commande.setEtat(EtatCommande.EN_LIVRAISON);
        commandeRepository.save(commande);

        CommandePatchRequest patch = new CommandePatchRequest();
        patch.setEtat("LIVREE");
        patch.setDateDeLivraison(LocalDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CommandePatchRequest> httpEntity = new HttpEntity<>(patch, headers);

        // When
        ResponseEntity<CommandeResponse> response = restTemplate.exchange(
                "/api/commandes/CMDLIVREE2",
                HttpMethod.PATCH,
                httpEntity,
                CommandeResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEtat()).isEqualTo("LIVREE");
        assertThat(response.getBody().getDateDeLivraison()).isNotNull();
    }

    @Test
    void patchCommande_setEtatPlanifieeWithNullDate_shouldSucceed() {
        // Given
        CommandeEntity commande = new CommandeEntity();
        commande.setReference("CMDPLANIF");
        commande.setEtat(EtatCommande.OUVERTE);
        commandeRepository.save(commande);

        CommandePatchRequest patch = new CommandePatchRequest();
        patch.setEtat("PLANIFIEE");
        patch.setDateDeLivraison(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CommandePatchRequest> httpEntity = new HttpEntity<>(patch, headers);

        // When
        ResponseEntity<CommandeResponse> response = restTemplate.exchange(
                "/api/commandes/CMDPLANIF",
                HttpMethod.PATCH,
                httpEntity,
                CommandeResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEtat()).isEqualTo("PLANIFIEE");
        assertThat(response.getBody().getDateDeLivraison()).isNull();
    }


    @Test
    void patchCommande_invalidEtat_shouldReturn400() {
        // Given
        CommandeEntity commande = new CommandeEntity();
        commande.setReference("CMDINVALID");
        commande.setEtat(EtatCommande.OUVERTE);
        commandeRepository.save(commande);

        CommandePatchRequest patch = new CommandePatchRequest();
        patch.setEtat("INVALID_ETAT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CommandePatchRequest> httpEntity = new HttpEntity<>(patch, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/commandes/CMDINVALID",
                HttpMethod.PATCH,
                httpEntity,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Valeur Invalide pour etat :");
    }

    @Test
    void patchCommande_noFieldsSet_shouldSucceedAndNotChangeAnything() {
        CommandeEntity commande = new CommandeEntity();
        commande.setReference("CMDNOCHANGE");
        commande.setEtat(EtatCommande.OUVERTE);
        commande.setDateDeLivraison(null);
        commandeRepository.save(commande);

        CommandePatchRequest patch = new CommandePatchRequest();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CommandePatchRequest> httpEntity = new HttpEntity<>(patch, headers);

        ResponseEntity<CommandeResponse> response = restTemplate.exchange(
                "/api/commandes/CMDNOCHANGE",
                HttpMethod.PATCH,
                httpEntity,
                CommandeResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEtat()).isEqualTo("OUVERTE");
        assertThat(response.getBody().getDateDeLivraison()).isNull();
    }
    @Test
    void patchCommande_onlyDateDeLivraison_shouldSucceed() {
        CommandeEntity commande = new CommandeEntity();
        commande.setReference("CMDDATEONLY");
        commande.setEtat(EtatCommande.OUVERTE);
        commandeRepository.save(commande);

        CommandePatchRequest patch = new CommandePatchRequest();
        patch.setDateDeLivraison(LocalDateTime.now().plusDays(2));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CommandePatchRequest> httpEntity = new HttpEntity<>(patch, headers);

        ResponseEntity<CommandeResponse> response = restTemplate.exchange(
                "/api/commandes/CMDDATEONLY",
                HttpMethod.PATCH,
                httpEntity,
                CommandeResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDateDeLivraison()).isEqualTo(patch.getDateDeLivraison());
    }
    @Test
    void patchCommande_livreeWithPastDate_shouldSucceed() {
        CommandeEntity commande = new CommandeEntity();
        commande.setReference("CMDPASTDATE");
        commande.setEtat(EtatCommande.EN_LIVRAISON);
        commandeRepository.save(commande);

        CommandePatchRequest patch = new CommandePatchRequest();
        patch.setEtat("LIVREE");
        patch.setDateDeLivraison(LocalDateTime.now().minusDays(1));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CommandePatchRequest> httpEntity = new HttpEntity<>(patch, headers);

        ResponseEntity<CommandeResponse> response = restTemplate.exchange(
                "/api/commandes/CMDPASTDATE",
                HttpMethod.PATCH,
                httpEntity,
                CommandeResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEtat()).isEqualTo("LIVREE");
        assertThat(response.getBody().getDateDeLivraison()).isEqualTo(patch.getDateDeLivraison());
    }
    @Test
    void patchCommande_nonExistentCommande_shouldReturn404() {
        CommandePatchRequest patch = new CommandePatchRequest();
        patch.setEtat("LIVREE");
        patch.setDateDeLivraison(LocalDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CommandePatchRequest> httpEntity = new HttpEntity<>(patch, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/commandes/DOESNOTEXIST",
                HttpMethod.PATCH,
                httpEntity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    @Test
    void patchCommande_nullEtatAndDate_shouldSucceed() {
        CommandeEntity commande = new CommandeEntity();
        commande.setReference("CMDNULLS");
        commande.setEtat(EtatCommande.OUVERTE);
        commande.setDateDeLivraison(null);
        commandeRepository.save(commande);

        CommandePatchRequest patch = new CommandePatchRequest();
        patch.setEtat(null);
        patch.setDateDeLivraison(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CommandePatchRequest> httpEntity = new HttpEntity<>(patch, headers);

        ResponseEntity<CommandeResponse> response = restTemplate.exchange(
                "/api/commandes/CMDNULLS",
                HttpMethod.PATCH,
                httpEntity,
                CommandeResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEtat()).isEqualTo("OUVERTE");
        assertThat(response.getBody().getDateDeLivraison()).isNull();
    }


    @Test
    void patchCommande_updatesEtatAndDateDeLivraison() {
        // Given
        CommandeEntity commande = new CommandeEntity();
        commande.setReference("CMD3");
        commande.setEtat(EtatCommande.OUVERTE);
        commande.setDateDeCreation(LocalDateTime.now());
        commandeRepository.save(commande);

        CommandePatchRequest patch = new CommandePatchRequest();
        patch.setEtat("LIVREE");
        patch.setDateDeLivraison(LocalDateTime.now().plusDays(1));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CommandePatchRequest> httpEntity = new HttpEntity<>(patch, headers);

        // When
        ResponseEntity<CommandeResponse> response = restTemplate.exchange(
                "/api/commandes/CMD3",
                HttpMethod.PATCH,
                httpEntity,
                CommandeResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEtat()).isEqualTo("LIVREE");
        assertThat(response.getBody().getDateDeLivraison()).isEqualTo(patch.getDateDeLivraison());
    }

    @Test
    void patchCommande_notFound_returns404() {
        CommandePatchRequest patch = new CommandePatchRequest();
        patch.setEtat("LIVREE");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CommandePatchRequest> httpEntity = new HttpEntity<>(patch, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/commandes/NOTFOUND",
                HttpMethod.PATCH,
                httpEntity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }



    @Test
    void getCommandesByTourneeReference_returnsList() {
        TourneeEntity tournee = new TourneeEntity();
        tournee.setReference("T001");
        tourneeRepository.save(tournee);

        CommandeEntity c1 = new CommandeEntity();
        c1.setReference("CMD1");
        c1.setEtat(EtatCommande.OUVERTE);
        c1.setTournee(tournee);

        CommandeEntity c2 = new CommandeEntity();
        c2.setReference("CMD2");
        c2.setEtat(EtatCommande.EN_LIVRAISON);
        c2.setTournee(tournee);

        commandeRepository.saveAll(List.of(c1, c2));

        // When
        ResponseEntity<CommandeResponse[]> response = restTemplate.getForEntity(
                "/api/commandes/tournee/T001", CommandeResponse[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(2);
        assertThat(
                java.util.Arrays.stream(response.getBody())
                        .anyMatch(c -> "CMD1".equals(c.getReference()))
        ).isTrue();
        assertThat(
                java.util.Arrays.stream(response.getBody())
                        .anyMatch(c -> "CMD2".equals(c.getReference()))
        ).isTrue();
    }

    @Test
    void getCommandesByTourneeReference_notFound_returns404() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/commandes/tournee/NOTFOUND", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
