package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.models.ClientEntity;
import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.models.LigneEntity;
import fr.uga.miage.l3.responses.ClientResponse;
import fr.uga.miage.l3.responses.CommandeResponse;

import java.util.Objects;
import java.util.stream.Collectors;

public class CommandeMapper {
    public static CommandeResponse toResponse(CommandeEntity entity) {
        if (entity == null) return null;
        return CommandeResponse.builder()
                .reference(entity.getReference())
                .etat(entity.getEtat() != null ? entity.getEtat().name() : null)
                .dateDeCreation(entity.getDateDeCreation())
                .note(entity.getNote())
                .tdmTheorique(entity.getTdmTheorique())
                .commentaire(entity.getCommentaire())
                .dateDeLivraison(entity.getDateDeLivraison())
                .client(entity.getClient() != null ? toClientResponse(entity.getClient()) : null)
                .ligneReferences(entity.getLignes() != null
                        ? entity.getLignes().stream().filter(Objects::nonNull).map(LigneEntity::getReference).collect(Collectors.toSet())
                        : null)
                .build();
    }

    public static ClientResponse toClientResponse(ClientEntity client) {
        if (client == null) return null;
        return ClientResponse.builder()
                .code(client.getCode())
                .email(client.getEmail())
                .nom(client.getNom())
                .prenom(client.getPrenom())
                .adresse(client.getAdresse())
                .coords(client.getCoords())
                .etat(client.getEtat() != null ? client.getEtat().name() : null)
                .build();
    }


}
