package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.endpoints.EquipeLivreursEndpoint;
import fr.uga.miage.l3.exceptions.InvalidStatusRestException;
import fr.uga.miage.l3.responses.EquipeLivreursResponse;
import fr.uga.miage.l3.requests.EquipeStatusPatchRequest;
import fr.uga.miage.l3.enums.StatusEquipeLivreurs;
import fr.uga.miage.l3.services.EquipeLivreursService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EquipeLivreursController implements EquipeLivreursEndpoint {
    private final EquipeLivreursService equipeLivreursService;

    @Override
    public ResponseEntity<List<EquipeLivreursResponse>> getAllEquipes() {
        return ResponseEntity.ok(equipeLivreursService.getAllEquipes());
    }
    @Override
    public ResponseEntity<EquipeLivreursResponse> createEquipe(EquipeLivreursResponse equipe) {
        EquipeLivreursResponse saved = equipeLivreursService.createEquipe(equipe);
        return ResponseEntity.status(201).body(saved);
    }

    @Override
    public ResponseEntity<Void> deleteEquipe(Integer id) {
        equipeLivreursService.deleteEquipe(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<EquipeLivreursResponse> patchEquipeStatus(Integer id, EquipeStatusPatchRequest request) {
        String statusStr = request.getStatus();
        try {
            StatusEquipeLivreurs.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusRestException("Invalid status: " + statusStr);
        }
        EquipeLivreursResponse updated = equipeLivreursService.patchEquipeStatus(id, statusStr);
        return ResponseEntity.ok(updated);
    }
}