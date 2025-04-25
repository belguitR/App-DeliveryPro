package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.models.TypeCamionEntity;
import fr.uga.miage.l3.responses.TypeCamionResponse;

public class TypeCamionMapper {
    public static TypeCamionResponse toResponse(TypeCamionEntity entity) {
        if (entity == null) return null;
        return TypeCamionResponse.builder()
                .nom(entity.getNom())
                .volume(entity.getVolume())
                .prixAuKm(entity.getPrixAuKm())
                .image(entity.getImage())
                .build();
    }
}