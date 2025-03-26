package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.responses.LivreurDTO;
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
    @Mapping(source = "photoURL", target = "photoURL")
    @Mapping(source = "telephone", target = "telephone")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "affecte", target = "affecte")
    @Mapping(source = "APermis", target = "APermis")
    @Mapping(source = "entrepot", target = "entrepot")
    @Mapping(source = "equipeLivreur", target = "equipeLivreur")
    LivreurDTO toDTO(LivreurEntity entity);

    @Mapping(source = "idEmploye", target = "idEmploye")
    @Mapping(source = "nom", target = "nom")
    @Mapping(source = "prenom", target = "prenom")
    @Mapping(source = "dateNaissance", target = "dateNaissance")
    @Mapping(source = "dateExpirationPermis", target = "dateExpirationPermis")
    @Mapping(source = "photoURL", target = "photoURL")
    @Mapping(source = "telephone", target = "telephone")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "affecte", target = "affecte")
    @Mapping(source = "APermis", target = "APermis")
    @Mapping(source = "entrepot", target = "entrepot")
    LivreurEntity fromDTO(LivreurDTO dto);
}


