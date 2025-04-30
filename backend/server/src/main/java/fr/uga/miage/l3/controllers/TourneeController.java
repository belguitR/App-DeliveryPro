package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.endpoints.TourneeEndpoint;
import fr.uga.miage.l3.requests.TourneePatchEtatRequest;
import fr.uga.miage.l3.responses.TourneeResponse;
import fr.uga.miage.l3.services.TourneeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4300"})
public class TourneeController implements TourneeEndpoint {

    private final TourneeService tourneeService;

    @Override
    public ResponseEntity<TourneeResponse> getLastTourneeByLivreurEmail(String email) {
        return ResponseEntity.ok(tourneeService.getLastTourneeByLivreurEmail(email));
    }

    @Override
    public ResponseEntity<TourneeResponse> patchTourneeEtat(String reference, TourneePatchEtatRequest request) {
        return ResponseEntity.ok(tourneeService.patchTourneeEtat(reference, request));
    }

}
