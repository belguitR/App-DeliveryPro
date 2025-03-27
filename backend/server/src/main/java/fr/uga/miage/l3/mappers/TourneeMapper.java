package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.responses.TourneeDTO;
import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.mappers.CommandeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = CommandeMapper.class)
public interface TourneeMapper {

    @Mapping(source = "numTournee", target = "numTournee")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "distanceAParcourir", target = "distanceAParcourir")
    @Mapping(source = "montant", target = "montant")
    @Mapping(source = "tempsDeMontageTheorique", target = "tempsDeMontageTheorique")
    @Mapping(source = "tempsDeMontageEffectif", target = "tempsDeMontageEffectif")
    @Mapping(source = "etat", target = "etat")
    @Mapping(source = "equipeLivreur.numEquipe", target = "equipeLivreurId")
    @Mapping(source = "commandes", target = "commandes")
    TourneeDTO toDTO(TourneeEntity entity);

    @Mapping(source = "numTournee", target = "numTournee")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "distanceAParcourir", target = "distanceAParcourir")
    @Mapping(source = "montant", target = "montant")
    @Mapping(source = "tempsDeMontageTheorique", target = "tempsDeMontageTheorique")
    @Mapping(source = "tempsDeMontageEffectif", target = "tempsDeMontageEffectif")
    @Mapping(source = "etat", target = "etat")
    @Mapping(target = "equipeLivreur", ignore = true)
    @Mapping(target = "commandes", ignore = true)
    @Mapping(target = "clients", ignore = true)
    TourneeEntity fromDTO(TourneeDTO dto);
}
