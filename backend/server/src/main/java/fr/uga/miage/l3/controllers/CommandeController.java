package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.endpoints.CommandeEndpoint;
import fr.uga.miage.l3.requests.CommandePatchRequest;
import fr.uga.miage.l3.responses.CommandeResponse;
import fr.uga.miage.l3.services.CommandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CommandeController implements CommandeEndpoint {
    private final CommandeService commandeService;

    @Override
    public ResponseEntity<List<CommandeResponse>> getAllCommandes() {
        return ResponseEntity.ok(commandeService.getAllCommandes());
    }

    @Override
    public ResponseEntity<CommandeResponse> getCommandeByReference(String reference) {
        return ResponseEntity.ok(commandeService.getCommandeByReference(reference));
    }

    @Override
    public ResponseEntity<CommandeResponse> patchCommande(String reference, CommandePatchRequest request) {
        return ResponseEntity.ok(commandeService.patchCommande(reference, request));
    }
}
