package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.responses.EquipeLivreurDTO;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-26T20:28:54+0100",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
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
        Set<LocalDate> set = entity.getDatesAffectes();
        if ( set != null ) {
            equipeLivreurDTO.setDatesAffectes( new LinkedHashSet<LocalDate>( set ) );
        }

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
        Set<LocalDate> set = dto.getDatesAffectes();
        if ( set != null ) {
            equipeLivreurEntity.datesAffectes( new LinkedHashSet<LocalDate>( set ) );
        }

        return equipeLivreurEntity.build();
    }
}
