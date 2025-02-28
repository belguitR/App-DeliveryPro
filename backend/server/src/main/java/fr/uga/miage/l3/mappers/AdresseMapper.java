package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.dto.AdresseDTO;
import fr.uga.miage.l3.models.AdresseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {Collectors.class, Collections.class})
public interface AdresseMapper {
    AdresseMapper INSTANCE = Mappers.getMapper(AdresseMapper.class);

    AdresseDTO toDTO(AdresseEntity entity);

    AdresseEntity fromDTO(AdresseDTO dto);
}
