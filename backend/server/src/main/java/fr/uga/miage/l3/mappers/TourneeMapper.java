package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.responses.TourneeResponse;

import java.util.stream.Collectors;

public class TourneeMapper {
    public static TourneeResponse toResponse(TourneeEntity entity) {
        if (entity == null) return null;
        return TourneeResponse.builder()
                .reference(entity.getReference())
                .etat(entity.getEtat() != null ? entity.getEtat().name() : null)
                .lettre(entity.getLettre())
                .distanceAParcourir(entity.getDistanceAParcourir())
                .tempsAssemblageReel(entity.getTempsAssemblageReel())
                .equipeLivreursId(entity.getEquipeLivreurs() != null ? entity.getEquipeLivreurs().getIdEquipe() != null ? entity.getEquipeLivreurs().getIdEquipe().toString() : null : null)
                .camion(entity.getCamion() != null ? CamionMapper.toResponse(entity.getCamion()) : null)
                .journeeReference(entity.getJour() != null ? entity.getJour().getReference() : null)
                .commandeReferences(entity.getCommandes() != null
                        ? entity.getCommandes().stream().map(CommandeEntity::getReference).collect(Collectors.toList())
                        : null)
                .build();
    }
}
