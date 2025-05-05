package fr.uga.miage.l3.mappers;

import fr.uga.miage.l3.models.EntrepotEntity;
import fr.uga.miage.l3.models.EquipeLivreursEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.responses.LivreurResponse;

public class LivreurMapper {

    public static LivreurResponse toResponse(LivreurEntity entity) {
        if (entity == null) return null;
        return new LivreurResponse(
                entity.getTrigramme(),
                entity.getNom(),
                entity.getPrenom(),
                entity.getEmail(),
                entity.getPhoto(),
                entity.getTelephone(),
                entity.isAffecte(),
                entity.isAPermis(),
                entity.getEquipeLivreurs() != null ? entity.getEquipeLivreurs().getIdEquipe() : null,
                entity.getEntrepot() != null ? entity.getEntrepot().getNom() : null
        );
    }

    public static LivreurEntity toEntity(LivreurResponse resp, EquipeLivreursEntity equipe, EntrepotEntity entrepot) {
        if (resp == null) return null;
        LivreurEntity entity = new LivreurEntity();
        entity.setTrigramme(resp.getTrigramme());
        entity.setNom(resp.getNom());
        entity.setPrenom(resp.getPrenom());
        entity.setEmail(resp.getEmail());
        entity.setPhoto(resp.getPhoto());
        entity.setTelephone(resp.getTelephone());
        entity.setAffecte(resp.isAffecte());
        entity.setAPermis(resp.isAPermis());
        entity.setEquipeLivreurs(equipe);
        entity.setEntrepot(entrepot);
        return entity;
    }
}