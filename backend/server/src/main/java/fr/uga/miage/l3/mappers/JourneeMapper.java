package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.models.JourneeEntity;
import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.responses.JourneeResponse;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JourneeMapper {
    public static JourneeResponse toResponse(JourneeEntity entity) {
        if (entity == null) return null;
        return JourneeResponse.builder()
                .reference(entity.getReference())
                .date(entity.getDate())
                .planificateurEmail(entity.getPlanificateur() != null ? entity.getPlanificateur().getEmail() : null)
                .tournees(entity.getTournees() != null
                        ? entity.getTournees().stream()
                        .filter(Objects::nonNull)
                        .map(TourneeMapper::toResponse)
                        .collect(Collectors.toList())
                        : null)
                .build();
    }
}
