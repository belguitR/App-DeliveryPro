package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.models.EquipeLivreursEntity;
import fr.uga.miage.l3.responses.EquipeLivreursResponse;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

public class EquipeLivreursMapper {

    public static EquipeLivreursResponse toResponse(EquipeLivreursEntity entity) {
        if (entity == null) return null;
        List<String> livreurIds = null;
        if (entity.getLivreurs() != null) {
            livreurIds = entity.getLivreurs().stream()
                    .map(l -> l.getTrigramme())
                    .collect(Collectors.toList());
        }
        return new EquipeLivreursResponse(
                entity.getIdEquipe(),
                entity.getStatus() != null ? entity.getStatus().name() : null,
                livreurIds
        );
    }

    public static EquipeLivreursEntity toEntity(EquipeLivreursResponse resp) {
        if (resp == null) return null;
        EquipeLivreursEntity entity = new EquipeLivreursEntity();
        entity.setIdEquipe(resp.getIdEquipe());
        if (resp.getStatus() != null) {
            entity.setStatus(fr.uga.miage.l3.enums.StatusEquipeLivreurs.valueOf(resp.getStatus()));
        }
        return entity;
    }
}