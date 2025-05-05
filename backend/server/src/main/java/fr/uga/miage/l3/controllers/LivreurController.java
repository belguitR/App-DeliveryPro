package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.endpoints.LivreurEndpoint;
import fr.uga.miage.l3.responses.LivreurResponse;
import fr.uga.miage.l3.services.LivreurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4300"})
public class LivreurController implements LivreurEndpoint {
    private final LivreurService livreurService;

    @Override
    public ResponseEntity<List<LivreurResponse>> getAllLivreurs() {
        return ResponseEntity.ok(livreurService.getAllLivreurs());
    }

    @Override
    public ResponseEntity<LivreurResponse> getLivreurByEmail(String email) {
        return ResponseEntity.ok(livreurService.getLivreurByEmail(email));
    }

    @Override
    public ResponseEntity<LivreurResponse> updateLivreur(String email, LivreurResponse livreur) {
        return ResponseEntity.ok(livreurService.updateLivreur(email, livreur));
    }
}