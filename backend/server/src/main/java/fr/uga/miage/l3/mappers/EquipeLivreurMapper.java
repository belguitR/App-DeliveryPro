package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.dto.EquipeLivreurDTO;
import fr.uga.miage.l3.models.EquipeLivreurEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.stream.Collectors;
import java.util.Collections;

@Mapper(componentModel = "spring", imports = {Collectors.class, Collections.class})
public interface EquipeLivreurMapper {

    @Mapping(source = "numEquipe", target = "numEquipe")
    @Mapping(source = "status", target = "status")
    @Mapping(expression = "java(entity.getLivreurs() == null ? null : entity.getLivreurs().stream().map(l -> l.getIdEmploye()).collect(Collectors.toList()))", target = "livreursIds")
    @Mapping(expression = "java(entity.getTournees() == null ? null : entity.getTournees().stream().map(t -> t.getNumTournee()).collect(Collectors.toList()))", target = "tourneesIds")
    EquipeLivreurDTO toDTO(EquipeLivreurEntity entity);

    @Mapping(target = "livreurs", ignore = true)
    @Mapping(target = "tournees", ignore = true)
    @Mapping(target = "agenda", ignore = true)
    EquipeLivreurEntity fromDTO(EquipeLivreurDTO dto);
}

