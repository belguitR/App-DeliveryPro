package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.responses.TourneeDTO;
import fr.uga.miage.l3.models.TourneeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {Collectors.class})
public interface TourneeMapper {

    @Mapping(source = "numTournee", target = "numTournee")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "distanceAParcourir", target = "distanceAParcourir")
    @Mapping(source = "montant", target = "montant")
    @Mapping(source = "tempsDeMontageTheorique", target = "tempsDeMontageTheorique")
    @Mapping(source = "tempsDeMontageEffectif", target = "tempsDeMontageEffectif")
    @Mapping(source = "etat", target = "etat")
    @Mapping(expression = "java(entity.getEquipeLivreur() == null ? null : entity.getEquipeLivreur().getNumEquipe())", target = "equipeLivreurId")
    @Mapping(expression = "java(entity.getCommandes() == null ? null : entity.getCommandes().stream().map(c -> c.getReference()).collect(Collectors.toList()))", target = "commandesIds")
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
