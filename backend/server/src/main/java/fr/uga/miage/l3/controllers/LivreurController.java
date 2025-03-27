package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.endpoints.LivreurEndpoints;
import fr.uga.miage.l3.mappers.LivreurMapper;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.responses.LivreurDTO;
import fr.uga.miage.l3.services.LivreurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class LivreurController implements LivreurEndpoints {

    private final LivreurService livreurService;
    private final LivreurMapper livreurMapper;

    @Override
    public ResponseEntity<List<LivreurDTO>> getAllLivreurs() {
        List<LivreurEntity> livreurs = livreurService.getAllLivreurs();
        List<LivreurDTO> dtos = livreurs.stream()
                .map(livreurMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<LivreurDTO> createLivreur(LivreurDTO livreurDTO) {
        // Conversion DTO -> Entity
        LivreurEntity entity = livreurMapper.fromDTO(livreurDTO);
        // Appel du service
        LivreurEntity saved = livreurService.createLivreur(entity);
        // Conversion Entity -> DTO
        LivreurDTO savedDTO = livreurMapper.toDTO(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDTO);
    }

    @Override
    public ResponseEntity<LivreurDTO> updateLivreur(String idLivreur, LivreurDTO livreurDTO) {
        // Conversion
        LivreurEntity entity = livreurMapper.fromDTO(livreurDTO);
        // Appel du service
        LivreurEntity updated = livreurService.updateLivreur(idLivreur, entity);
        // Conversion
        LivreurDTO updatedDTO = livreurMapper.toDTO(updated);
        return ResponseEntity.ok(updatedDTO);
    }
}
