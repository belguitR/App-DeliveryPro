package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.models.EntrepotEntity;
import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.responses.EntrepotDTO;
import fr.uga.miage.l3.responses.EquipeLivreurDTO;
import fr.uga.miage.l3.responses.LivreurDTO;
import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-27T07:57:58+0100",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class LivreurMapperImpl implements LivreurMapper {

    @Override
    public LivreurDTO toDTO(LivreurEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String nom = null;
        String prenom = null;
        String photoURL = null;
        String telephone = null;
        String email = null;

        nom = entity.getNom();
        prenom = entity.getPrenom();
        photoURL = entity.getPhotoURL();
        telephone = entity.getTelephone();
        email = entity.getEmail();

        LocalDate dateNaissance = entity.getDateNaissance() == null ? null : ((java.sql.Date) entity.getDateNaissance()).toLocalDate();

        LivreurDTO livreurDTO = new LivreurDTO( nom, prenom, dateNaissance, photoURL, telephone, email );

        livreurDTO.setIdEmploye( entity.getIdEmploye() );
        livreurDTO.setAffecte( entity.isAffecte() );
        livreurDTO.setAPermis( entity.isAPermis() );
        livreurDTO.setEntrepot( entrepotEntityToEntrepotDTO( entity.getEntrepot() ) );
        livreurDTO.setEquipeLivreur( equipeLivreurEntityToEquipeLivreurDTO( entity.getEquipeLivreur() ) );

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
        livreurEntity.setPhotoURL( dto.getPhotoURL() );
        livreurEntity.setTelephone( dto.getTelephone() );
        livreurEntity.setEmail( dto.getEmail() );
        livreurEntity.setAffecte( dto.isAffecte() );
        livreurEntity.setAPermis( dto.isAPermis() );
        livreurEntity.setEntrepot( entrepotDTOToEntrepotEntity( dto.getEntrepot() ) );

        livreurEntity.setDateNaissance( dto.getDateNaissance() == null ? null : java.sql.Date.valueOf(dto.getDateNaissance()) );

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

    protected EquipeLivreurDTO equipeLivreurEntityToEquipeLivreurDTO(EquipeLivreurEntity equipeLivreurEntity) {
        if ( equipeLivreurEntity == null ) {
            return null;
        }

        EquipeLivreurDTO equipeLivreurDTO = new EquipeLivreurDTO();

        equipeLivreurDTO.setNumEquipe( equipeLivreurEntity.getNumEquipe() );
        equipeLivreurDTO.setStatus( equipeLivreurEntity.getStatus() );
        Set<LocalDate> set = equipeLivreurEntity.getDatesAffectes();
        if ( set != null ) {
            equipeLivreurDTO.setDatesAffectes( new LinkedHashSet<LocalDate>( set ) );
        }

        return equipeLivreurDTO;
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
