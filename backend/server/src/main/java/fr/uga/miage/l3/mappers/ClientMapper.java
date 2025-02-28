package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.dto.ClientDTO;
import fr.uga.miage.l3.models.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {AdresseMapper.class})
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    @Mapping(source = "code", target = "code")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "prenom", target = "prenom")
    @Mapping(source = "nom", target = "nom")
    @Mapping(source = "telephone", target = "telephone")
    @Mapping(source = "adresse", target = "adresse")
    ClientDTO toDTO(ClientEntity entity);

    @Mapping(source = "code", target = "code")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "prenom", target = "prenom")
    @Mapping(source = "nom", target = "nom")
    @Mapping(source = "telephone", target = "telephone")
    @Mapping(source = "adresse", target = "adresse")
    ClientEntity fromDTO(ClientDTO dto);
}
