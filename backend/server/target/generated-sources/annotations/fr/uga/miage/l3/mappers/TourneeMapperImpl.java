package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.responses.TourneeDTO;
import java.util.stream.Collectors;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-26T03:55:09+0100",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class TourneeMapperImpl implements TourneeMapper {

    @Override
    public TourneeDTO toDTO(TourneeEntity entity) {
        if ( entity == null ) {
            return null;
        }

        TourneeDTO tourneeDTO = new TourneeDTO();

        tourneeDTO.setNumTournee( entity.getNumTournee() );
        tourneeDTO.setDate( entity.getDate() );
        tourneeDTO.setDistanceAParcourir( entity.getDistanceAParcourir() );
        tourneeDTO.setMontant( entity.getMontant() );
        tourneeDTO.setTempsDeMontageTheorique( entity.getTempsDeMontageTheorique() );
        tourneeDTO.setTempsDeMontageEffectif( entity.getTempsDeMontageEffectif() );
        tourneeDTO.setEtat( entity.getEtat() );

        tourneeDTO.setEquipeLivreurId( entity.getEquipeLivreur() == null ? null : entity.getEquipeLivreur().getNumEquipe() );
        tourneeDTO.setCommandesIds( entity.getCommandes() == null ? null : entity.getCommandes().stream().map(c -> c.getReference()).collect(Collectors.toList()) );

        return tourneeDTO;
    }

    @Override
    public TourneeEntity fromDTO(TourneeDTO dto) {
        if ( dto == null ) {
            return null;
        }

        TourneeEntity.TourneeEntityBuilder tourneeEntity = TourneeEntity.builder();

        tourneeEntity.numTournee( dto.getNumTournee() );
        tourneeEntity.date( dto.getDate() );
        tourneeEntity.distanceAParcourir( dto.getDistanceAParcourir() );
        tourneeEntity.montant( dto.getMontant() );
        tourneeEntity.tempsDeMontageTheorique( dto.getTempsDeMontageTheorique() );
        tourneeEntity.tempsDeMontageEffectif( dto.getTempsDeMontageEffectif() );
        tourneeEntity.etat( dto.getEtat() );

        return tourneeEntity.build();
    }
}
