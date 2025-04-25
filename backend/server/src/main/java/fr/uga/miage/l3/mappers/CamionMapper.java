package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.models.CamionEntity;
import fr.uga.miage.l3.responses.CamionResponse;

public class CamionMapper {
    public static CamionResponse toResponse(CamionEntity entity) {
        if (entity == null) return null;
        return CamionResponse.builder()
                .code(entity.getCode())
                .immatriculation(entity.getImmatriculation())
                .entrepotNom(entity.getEntrepot() != null ? entity.getEntrepot().getNom() : null)
                .typeCamion(TypeCamionMapper.toResponse(entity.getTypeCamion()))
                .build();
    }
}
