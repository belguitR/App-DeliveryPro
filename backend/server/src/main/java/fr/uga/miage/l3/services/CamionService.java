package fr.uga.miage.l3.services;

import fr.uga.miage.l3.components.CamionComponent;
import fr.uga.miage.l3.mappers.CamionMapper;
import fr.uga.miage.l3.repositories.CamionRepository;
import fr.uga.miage.l3.responses.CamionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CamionService {

    private final CamionComponent camionComponent;

    public List<CamionResponse> getAllCamions() {
        return camionComponent.findAll()
                .stream()
                .map(CamionMapper::toResponse)
                .collect(Collectors.toList());
    }
}