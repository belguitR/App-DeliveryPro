package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.endpoints.JourneeEndpoint;
import fr.uga.miage.l3.requests.JourneeRequest;
import fr.uga.miage.l3.responses.JourneeResponse;
import fr.uga.miage.l3.services.JourneeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class JourneeController implements JourneeEndpoint {
    private final JourneeService journeeService;

    @Override
    public ResponseEntity<List<JourneeResponse>> getAllJournees() {
        return ResponseEntity.ok(journeeService.getAllJournees());
    }

    @Override
    public ResponseEntity<JourneeResponse> getJourneeByReference(String reference) {
        return ResponseEntity.ok(journeeService.getJourneeByReference(reference));
    }

    @Override
    public ResponseEntity<JourneeResponse> createJournee(JourneeRequest request) {
        return ResponseEntity.status(201).body(journeeService.createJournee(request));
    }

    @Override
    public ResponseEntity<Void> deleteJournee(String reference) {
        journeeService.deleteJournee(reference);
        return ResponseEntity.noContent().build();
    }
}
