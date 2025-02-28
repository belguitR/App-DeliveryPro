package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.dto.CommandeDTO;
import fr.uga.miage.l3.models.CommandeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {ClientMapper.class})
public interface CommandeMapper {
    CommandeMapper INSTANCE = Mappers.getMapper(CommandeMapper.class);

    @Mapping(source = "reference", target = "reference")
    @Mapping(source = "etat", target = "etat")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "horairePreferable", target = "horairePreferable")
    @Mapping(source = "dateLivre", target = "dateLivre")
    @Mapping(source = "dateLivraisonEstimee", target = "dateLivraisonEstimee")
    @Mapping(source = "montantTotal", target = "montantTotal")
    CommandeDTO toDTO(CommandeEntity entity);

    @Mapping(source = "reference", target = "reference")
    @Mapping(source = "etat", target = "etat")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "horairePreferable", target = "horairePreferable")
    @Mapping(source = "dateLivre", target = "dateLivre")
    @Mapping(source = "dateLivraisonEstimee", target = "dateLivraisonEstimee")
    @Mapping(source = "montantTotal", target = "montantTotal")
    CommandeEntity fromDTO(CommandeDTO dto);
}
