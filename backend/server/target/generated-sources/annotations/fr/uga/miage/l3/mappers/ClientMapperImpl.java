package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.models.ClientEntity;
import fr.uga.miage.l3.responses.ClientDTO;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-26T03:55:09+0100",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class ClientMapperImpl implements ClientMapper {

    @Autowired
    private AdresseMapper adresseMapper;

    @Override
    public ClientDTO toDTO(ClientEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ClientDTO.ClientDTOBuilder clientDTO = ClientDTO.builder();

        clientDTO.code( entity.getCode() );
        clientDTO.email( entity.getEmail() );
        clientDTO.prenom( entity.getPrenom() );
        clientDTO.nom( entity.getNom() );
        clientDTO.telephone( entity.getTelephone() );
        clientDTO.adresse( adresseMapper.toDTO( entity.getAdresse() ) );

        return clientDTO.build();
    }

    @Override
    public ClientEntity fromDTO(ClientDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ClientEntity.ClientEntityBuilder clientEntity = ClientEntity.builder();

        clientEntity.code( dto.getCode() );
        clientEntity.email( dto.getEmail() );
        clientEntity.prenom( dto.getPrenom() );
        clientEntity.nom( dto.getNom() );
        clientEntity.telephone( dto.getTelephone() );
        clientEntity.adresse( adresseMapper.fromDTO( dto.getAdresse() ) );

        return clientEntity.build();
    }
}
