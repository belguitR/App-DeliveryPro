package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.dto.AdresseDTO;
import fr.uga.miage.l3.models.AdresseEntity;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-25T04:50:57+0100",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class AdresseMapperImpl implements AdresseMapper {

    @Override
    public AdresseDTO toDTO(AdresseEntity entity) {
        if ( entity == null ) {
            return null;
        }

        AdresseDTO.AdresseDTOBuilder adresseDTO = AdresseDTO.builder();

        adresseDTO.adressePostal( entity.getAdressePostal() );
        adresseDTO.complementAdresse( entity.getComplementAdresse() );
        adresseDTO.ville( entity.getVille() );
        adresseDTO.codePostal( entity.getCodePostal() );
        adresseDTO.latitude( entity.getLatitude() );
        adresseDTO.longitude( entity.getLongitude() );

        return adresseDTO.build();
    }

    @Override
    public AdresseEntity fromDTO(AdresseDTO dto) {
        if ( dto == null ) {
            return null;
        }

        AdresseEntity.AdresseEntityBuilder adresseEntity = AdresseEntity.builder();

        adresseEntity.adressePostal( dto.getAdressePostal() );
        adresseEntity.complementAdresse( dto.getComplementAdresse() );
        adresseEntity.ville( dto.getVille() );
        adresseEntity.codePostal( dto.getCodePostal() );
        adresseEntity.latitude( dto.getLatitude() );
        adresseEntity.longitude( dto.getLongitude() );

        return adresseEntity.build();
    }
}
