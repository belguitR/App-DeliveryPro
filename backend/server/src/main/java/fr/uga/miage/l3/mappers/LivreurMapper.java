package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.responses.LivreurDTO;
import fr.uga.miage.l3.models.LivreurEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.time.ZoneId;

@Mapper(componentModel = "spring", imports = {java.sql.Date.class})
public interface LivreurMapper {

    @Mapping(
            target = "dateNaissance",
            expression = "java(entity.getDateNaissance() == null ? null : ((java.sql.Date) entity.getDateNaissance()).toLocalDate())"
    )
    @Mapping(source = "idEmploye", target = "idEmploye")
    @Mapping(source = "nom", target = "nom")
    @Mapping(source = "prenom", target = "prenom")
    @Mapping(source = "photoURL", target = "photoURL")
    @Mapping(source = "telephone", target = "telephone")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "affecte", target = "affecte")
    @Mapping(source = "APermis", target = "APermis")
    @Mapping(source = "entrepot", target = "entrepot")
    @Mapping(source = "equipeLivreur", target = "equipeLivreur")
    LivreurDTO toDTO(LivreurEntity entity);

    @Mapping(
            target = "dateNaissance",
            expression = "java(dto.getDateNaissance() == null ? null : java.sql.Date.valueOf(dto.getDateNaissance()))"
    )
    @Mapping(source = "idEmploye", target = "idEmploye")
    @Mapping(source = "nom", target = "nom")
    @Mapping(source = "prenom", target = "prenom")
    @Mapping(source = "photoURL", target = "photoURL")
    @Mapping(source = "telephone", target = "telephone")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "affecte", target = "affecte")
    @Mapping(source = "APermis", target = "APermis")
    @Mapping(source = "entrepot", target = "entrepot")
    @Mapping(target = "equipeLivreur", ignore = true) // si vous ne souhaitez pas mapper l'équipe
    LivreurEntity fromDTO(LivreurDTO dto);
}
