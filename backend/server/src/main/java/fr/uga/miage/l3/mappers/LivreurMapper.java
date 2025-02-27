package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.dto.LivreurDTO;
import fr.uga.miage.l3.models.LivreurEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {Collectors.class})
public interface LivreurMapper {

    @Mapping(source = "idEmploye", target = "idEmploye")
    @Mapping(source = "nom", target = "nom")
    @Mapping(source = "prenom", target = "prenom")
    @Mapping(source = "dateNaissance", target = "dateNaissance")
    @Mapping(source = "dateExpirationPermis", target = "dateExpirationPermis")
    LivreurDTO toDTO(LivreurEntity entity);

    @Mapping(source = "idEmploye", target = "idEmploye")
    @Mapping(source = "nom", target = "nom")
    @Mapping(source = "prenom", target = "prenom")
    @Mapping(source = "dateNaissance", target = "dateNaissance")
    @Mapping(source = "dateExpirationPermis", target = "dateExpirationPermis")
    LivreurEntity fromDTO(LivreurDTO dto);
}


