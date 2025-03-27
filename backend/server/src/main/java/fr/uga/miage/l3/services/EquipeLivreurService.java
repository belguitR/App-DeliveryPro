package fr.uga.miage.l3.services;

import fr.uga.miage.l3.enums.StatusEquipeLivreur;
import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repository.EquipeLivreurRepository;
import fr.uga.miage.l3.repository.LivreurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipeLivreurService {

    private final EquipeLivreurRepository equipeLivreurRepository;
    private final LivreurRepository livreurRepository;

    public List<EquipeLivreurEntity> getAllEquipes() {
        List<EquipeLivreurEntity> equipes = equipeLivreurRepository.findAll();
        if (equipes.isEmpty()) {
            throw new EntityNotFoundException("Aucune équipe trouvée.");
        }
        return equipes;
    }

    @Transactional
    public EquipeLivreurEntity createEquipeLivreur(List<LivreurEntity> livreurs) {
        if (livreurs.size() != 2) {
            throw new IllegalArgumentException("L'équipe doit contenir exactement 2 livreurs.");
        }

        // Extraire les identifiants et récupérer les livreurs depuis la base
        List<String> ids = livreurs.stream().map(LivreurEntity::getIdEmploye).toList();
        List<LivreurEntity> existingLivreurs = livreurRepository.findAllByIdEmployeIn(ids);
        if (existingLivreurs.size() != 2) {
            throw new IllegalArgumentException("Un ou plusieurs livreurs n'existent pas dans la base.");
        }

        boolean permisFound = false;
        for (LivreurEntity l : existingLivreurs) {
            if (l.isAffecte()) {
                throw new IllegalArgumentException("Le livreur avec l'ID " + l.getIdEmploye() + " est déjà affecté.");
            }
            if (l.isAPermis()) {
                permisFound = true;
            }
            // Nettoyer toute ancienne référence à une équipe
            l.setEquipeLivreur(null);
        }
        if (!permisFound) {
            throw new IllegalArgumentException("Un des deux livreurs doit avoir le permis.");
        }

        // Marquer les livreurs comme affectés
        existingLivreurs.forEach(l -> l.setAffecte(true));

        // Créer l'équipe avec une nouvelle ArrayList pour éviter les modifications concurrentes
        EquipeLivreurEntity equipe = EquipeLivreurEntity.builder()
                .status(StatusEquipeLivreur.PRET)
                .livreurs(new ArrayList<>(existingLivreurs))
                .build();

        // Lier l'équipe aux livreurs
        existingLivreurs.forEach(l -> l.setEquipeLivreur(equipe));

        // Sauvegarder uniquement l'équipe (la cascade sauvegarde les livreurs)
        equipeLivreurRepository.save(equipe);

        return equipe;
    }

    @Transactional
    public void supprimerEquipe(Integer numEquipe) {
        EquipeLivreurEntity equipe = equipeLivreurRepository.findById(numEquipe)
                .orElseThrow(() -> new EntityNotFoundException("Ne peut pas supprimer cette équipe car elle n'existe pas."));

        // Vérifier que l'équipe n'est pas en livraison
        if(equipe.getStatus() == StatusEquipeLivreur.EN_LIVRAISON) {
            throw new IllegalArgumentException("Impossible de supprimer une équipe en livraison.");
        }

        // Pour chaque livreur de l'équipe, désaffecter et retirer l'association
        equipe.getLivreurs().forEach(livreur -> {
            livreur.setAffecte(false);
            livreur.setEquipeLivreur(null);
        });

        equipeLivreurRepository.delete(equipe);
    }





    /*@Transactional
    public EquipeLivreurEntity modifierEquipe(Integer idEquipe, List<LivreurEntity> newLivreurs) {
        EquipeLivreurEntity equipeExistante = equipeLivreurRepository.findById(idEquipe)
                .orElseThrow(() -> new EntityNotFoundException("Équipe non trouvée avec l'id " + idEquipe));

        // Vérifier que la liste contient exactement 2 livreurs.
        if (newLivreurs.size() != 2) {
            throw new IllegalArgumentException("L'équipe doit contenir exactement 2 livreurs.");
        }

        boolean permisFound = false;
        for (LivreurEntity livreur : newLivreurs) {
            // Si le livreur est déjà affecté et n'appartient pas à l'équipe actuelle.
            if (livreur.isAffecte() && (equipeExistante.getLivreurs() == null ||
                    equipeExistante.getLivreurs().stream().noneMatch(oldLivreur -> oldLivreur.getIdEmploye().equals(livreur.getIdEmploye())))) {
                throw new IllegalArgumentException("Le livreur avec l'ID " + livreur.getIdEmploye() + " est déjà affecté.");
            }
            if (livreur.isAPermis()) {
                permisFound = true;
            }
        }
        if (!permisFound) {
            throw new IllegalArgumentException("Un des deux livreurs doit avoir le permis.");
        }

        // Désaffecter les livreurs qui ne font plus partie de l'équipe.
        if (equipeExistante.getLivreurs() != null) {
            equipeExistante.getLivreurs().stream()
                    .filter(oldLivreur -> newLivreurs.stream().noneMatch(newLivreur -> newLivreur.getIdEmploye().equals(oldLivreur.getIdEmploye())))
                    .forEach(oldLivreur -> {
                        oldLivreur.setAffecte(false);
                        oldLivreur.setEquipeLivreur(null);
                    });
        }

        // Affecter les nouveaux livreurs à l'équipe.
        newLivreurs.forEach(livreur -> {
            livreur.setAffecte(true);
            livreur.setEquipeLivreur(equipeExistante);
        });

        // Mettre à jour l'équipe avec la nouvelle liste de livreurs.
        equipeExistante.setLivreurs(newLivreurs);
        return equipeLivreurRepository.save(equipeExistante);
    }*/
}
