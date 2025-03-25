package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.dto.EntrepotDTO;
import fr.uga.miage.l3.dto.LivreurDTO;
import fr.uga.miage.l3.models.EntrepotEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import java.util.stream.Collectors;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-25T04:24:46+0100",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class LivreurMapperImpl implements LivreurMapper {

    @Override
    public LivreurDTO toDTO(LivreurEntity entity) {
        if ( entity == null ) {
            return null;
        }

        LivreurDTO livreurDTO = new LivreurDTO();

        livreurDTO.setIdEmploye( entity.getIdEmploye() );
        livreurDTO.setNom( entity.getNom() );
        livreurDTO.setPrenom( entity.getPrenom() );
        livreurDTO.setDateNaissance( entity.getDateNaissance() );
        livreurDTO.setDateExpirationPermis( entity.getDateExpirationPermis() );
        livreurDTO.setPhotoURL( entity.getPhotoURL() );
        livreurDTO.setTelephone( entity.getTelephone() );
        livreurDTO.setEmail( entity.getEmail() );
        livreurDTO.setAffecte( entity.isAffecte() );
        livreurDTO.setAPermis( entity.isAPermis() );
        livreurDTO.setEntrepot( entrepotEntityToEntrepotDTO( entity.getEntrepot() ) );

        return livreurDTO;
    }

    @Override
    public LivreurEntity fromDTO(LivreurDTO dto) {
        if ( dto == null ) {
            return null;
        }

        LivreurEntity livreurEntity = new LivreurEntity();

        livreurEntity.setIdEmploye( dto.getIdEmploye() );
        livreurEntity.setNom( dto.getNom() );
        livreurEntity.setPrenom( dto.getPrenom() );
        livreurEntity.setDateNaissance( dto.getDateNaissance() );
        livreurEntity.setDateExpirationPermis( dto.getDateExpirationPermis() );
        livreurEntity.setPhotoURL( dto.getPhotoURL() );
        livreurEntity.setTelephone( dto.getTelephone() );
        livreurEntity.setEmail( dto.getEmail() );
        livreurEntity.setAffecte( dto.isAffecte() );
        livreurEntity.setAPermis( dto.isAPermis() );
        livreurEntity.setEntrepot( entrepotDTOToEntrepotEntity( dto.getEntrepot() ) );

        return livreurEntity;
    }

    protected EntrepotDTO entrepotEntityToEntrepotDTO(EntrepotEntity entrepotEntity) {
        if ( entrepotEntity == null ) {
            return null;
        }

        EntrepotDTO entrepotDTO = new EntrepotDTO();

        entrepotDTO.setNom( entrepotEntity.getNom() );

        return entrepotDTO;
    }

    protected EntrepotEntity entrepotDTOToEntrepotEntity(EntrepotDTO entrepotDTO) {
        if ( entrepotDTO == null ) {
            return null;
        }

        EntrepotEntity.EntrepotEntityBuilder entrepotEntity = EntrepotEntity.builder();

        entrepotEntity.nom( entrepotDTO.getNom() );

        return entrepotEntity.build();
    }
}
