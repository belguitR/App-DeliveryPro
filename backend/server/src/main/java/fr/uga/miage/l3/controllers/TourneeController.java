package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.endpoints.TourneeEndpoint;
import fr.uga.miage.l3.responses.TourneeResponse;
import fr.uga.miage.l3.services.TourneeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TourneeController implements TourneeEndpoint {

    private final TourneeService tourneeService;

    @Override
    public ResponseEntity<TourneeResponse> getTodayTourneeByEquipeLivreurs(Integer equipeId) {
        return ResponseEntity.ok(tourneeService.getTodayTourneeByEquipe(equipeId));
    }
}
