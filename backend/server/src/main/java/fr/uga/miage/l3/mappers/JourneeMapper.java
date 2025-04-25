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
        System.out.println("Mapping journee: " + entity.getReference());
        System.out.println("  Date: " + entity.getDate());
        System.out.println("  Planificateur: " + (entity.getPlanificateur() != null ? entity.getPlanificateur().getTrigramme() : "null"));
        System.out.println("  Tournees: " + (entity.getTournees() != null ? entity.getTournees().size() : "null"));
        if (entity.getTournees() != null) {
            for (TourneeEntity t : entity.getTournees()) {
                System.out.println("    Tournee: " + (t != null ? t.getReference() : "null"));
            }
        }
        return JourneeResponse.builder()
                .reference(entity.getReference())
                .date(entity.getDate())
                .planificateurTrigramme(entity.getPlanificateur() != null ? entity.getPlanificateur().getTrigramme() : null)
                .tournees(entity.getTournees() != null
                        ? entity.getTournees().stream()
                        .filter(Objects::nonNull)
                        .map(TourneeMapper::toResponse)
                        .collect(Collectors.toList())
                        : null)
                .build();
    }
}
