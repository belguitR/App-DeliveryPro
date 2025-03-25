package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.dto.EquipeLivreurDTO;
import fr.uga.miage.l3.models.EquipeLivreurEntity;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-25T01:58:40+0100",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 20.0.2 (Oracle Corporation)"
)
@Component
public class EquipeLivreurMapperImpl implements EquipeLivreurMapper {

    @Override
    public EquipeLivreurDTO toDTO(EquipeLivreurEntity entity) {
        if ( entity == null ) {
            return null;
        }

        EquipeLivreurDTO equipeLivreurDTO = new EquipeLivreurDTO();

        equipeLivreurDTO.setNumEquipe( entity.getNumEquipe() );
        equipeLivreurDTO.setStatus( entity.getStatus() );

        equipeLivreurDTO.setLivreursIds( entity.getLivreurs() == null ? null : entity.getLivreurs().stream().map(l -> l.getIdEmploye()).collect(Collectors.toList()) );
        equipeLivreurDTO.setTourneesIds( entity.getTournees() == null ? null : entity.getTournees().stream().map(t -> t.getNumTournee()).collect(Collectors.toList()) );

        return equipeLivreurDTO;
    }

    @Override
    public EquipeLivreurEntity fromDTO(EquipeLivreurDTO dto) {
        if ( dto == null ) {
            return null;
        }

        EquipeLivreurEntity.EquipeLivreurEntityBuilder equipeLivreurEntity = EquipeLivreurEntity.builder();

        equipeLivreurEntity.numEquipe( dto.getNumEquipe() );
        equipeLivreurEntity.status( dto.getStatus() );

        return equipeLivreurEntity.build();
    }
}
