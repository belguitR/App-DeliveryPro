package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.dto.CommandeDTO;
import fr.uga.miage.l3.models.CommandeEntity;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-25T06:03:26+0100",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class CommandeMapperImpl implements CommandeMapper {

    @Autowired
    private ClientMapper clientMapper;

    @Override
    public CommandeDTO toDTO(CommandeEntity entity) {
        if ( entity == null ) {
            return null;
        }

        CommandeDTO commandeDTO = new CommandeDTO();

        commandeDTO.setReference( entity.getReference() );
        commandeDTO.setEtat( entity.getEtat() );
        commandeDTO.setDate( entity.getDate() );
        commandeDTO.setHorairePreferable( entity.getHorairePreferable() );
        commandeDTO.setDateLivre( entity.getDateLivre() );
        commandeDTO.setDateLivraisonEstimee( entity.getDateLivraisonEstimee() );
        commandeDTO.setMontantTotal( entity.getMontantTotal() );
        commandeDTO.setClient( clientMapper.toDTO( entity.getClient() ) );

        return commandeDTO;
    }

    @Override
    public CommandeEntity fromDTO(CommandeDTO dto) {
        if ( dto == null ) {
            return null;
        }

        CommandeEntity.CommandeEntityBuilder commandeEntity = CommandeEntity.builder();

        commandeEntity.reference( dto.getReference() );
        commandeEntity.etat( dto.getEtat() );
        commandeEntity.date( dto.getDate() );
        commandeEntity.horairePreferable( dto.getHorairePreferable() );
        commandeEntity.dateLivre( dto.getDateLivre() );
        commandeEntity.dateLivraisonEstimee( dto.getDateLivraisonEstimee() );
        commandeEntity.montantTotal( dto.getMontantTotal() );
        commandeEntity.client( clientMapper.fromDTO( dto.getClient() ) );

        return commandeEntity.build();
    }
}
