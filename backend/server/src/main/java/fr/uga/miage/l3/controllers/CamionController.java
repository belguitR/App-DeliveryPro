package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.endpoints.CamionEndpoint;
import fr.uga.miage.l3.responses.CamionResponse;
import fr.uga.miage.l3.services.CamionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CamionController implements CamionEndpoint {

    private final CamionService camionService;

    @Override
    public ResponseEntity<List<CamionResponse>> getAllCamions() {
        return ResponseEntity.ok(camionService.getAllCamions());
    }
}