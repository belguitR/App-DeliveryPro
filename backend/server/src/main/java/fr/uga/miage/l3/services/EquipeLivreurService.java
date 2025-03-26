package fr.uga.miage.l3.services;

import fr.uga.miage.l3.enums.StatusEquipeLivreur;
import fr.uga.miage.l3.mappers.EquipeLivreurMapper;
import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repository.EquipeLivreurRepository;
import fr.uga.miage.l3.repository.LivreurRepository;
import fr.uga.miage.l3.responses.EquipeLivreurDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipeLivreurService {

    private final EquipeLivreurRepository equipeLivreurRepository;
    private final EquipeLivreurMapper equipeLivreurMapper;
    private final LivreurRepository livreurRepository;

    public List<EquipeLivreurEntity> getAllEquipes() {
        return equipeLivreurRepository.findAll();
    }

//    public List<EquipeLivreurDTO> getAllEquipes() {
//        return equipeLivreurRepository.findAllWithLivreurs()
//                .stream()
//                .map(equipeLivreurMapper::toDTO)
//                .collect(Collectors.toList());
//    }

    @Transactional
    public EquipeLivreurEntity createEquipeLivreur(List<LivreurEntity> livreurs, Integer numEquipe) {
        // Verify exactly 2 livreurs are provided.
        if (livreurs.size() != 2) {
            throw new IllegalArgumentException("Une équipe doit contenir exactement 2 livreurs.");
        }

        // Fetch the livreurs from the database to ensure they are managed.
        List<LivreurEntity> existingLivreurs = livreurRepository.findAllByIdEmployeIn(
                livreurs.stream().map(LivreurEntity::getIdEmploye).collect(Collectors.toList())
        );

        // Check if any livreur is already affected.
        for (LivreurEntity livreur : existingLivreurs) {
            if (livreur.isAffecte()) {
                throw new IllegalArgumentException("Livreur with ID " + livreur.getIdEmploye() + " is already affected.");
            }
        }

        // Verify at least one livreur has a valid permit.
        Date today = new Date();
        boolean hasValidDriver = existingLivreurs.stream()
                .anyMatch(livreur -> livreur.isAPermis() &&
                        (livreur.getDateExpirationPermis() != null && livreur.getDateExpirationPermis().after(today)));
        if (!hasValidDriver) {
            throw new IllegalArgumentException("At least one livreur must have a valid permit.");
        }

        // Mark each livreur as affected and set team association.
        existingLivreurs.forEach(livreur -> {
            livreur.setAffecte(true);
            livreur.setEquipeLivreur(null); // Will be assigned with the equipe after creation
        });

        // Create and save the EquipeLivreur
        EquipeLivreurEntity equipeLivreur = EquipeLivreurEntity.builder()
                .numEquipe(numEquipe)
                .status(StatusEquipeLivreur.PRET)
                .livreurs(existingLivreurs)
                .build();

        // Assign the team association to each livreur
        existingLivreurs.forEach(livreur -> livreur.setEquipeLivreur(equipeLivreur));

        // Save the equipe and the livreurs
        equipeLivreurRepository.save(equipeLivreur);
        livreurRepository.saveAll(existingLivreurs);  // Save the livreurs with the updated team

        return equipeLivreur;
    }

    @Transactional
    public EquipeLivreurEntity modifierEquipe(Integer idEquipe, EquipeLivreurEntity equipeLivreur) {
        EquipeLivreurEntity existingEquipe = equipeLivreurRepository.findById(idEquipe)
                .orElseThrow(() -> new EntityNotFoundException("Équipe not found with number: " + idEquipe));

        List<LivreurEntity> newLivreurs = equipeLivreur.getLivreurs();

        // Verify exactly 2 livreurs are provided.
        if (newLivreurs.size() != 2) {
            throw new IllegalArgumentException("Une équipe doit contenir exactement 2 livreurs.");
        }

        // Check if each new livreur is either not affected or is already part of the current team.
        for (LivreurEntity livreur : newLivreurs) {
            if (livreur.isAffecte() && (existingEquipe.getLivreurs() == null ||
                    existingEquipe.getLivreurs().stream().noneMatch(oldL -> oldL.getIdEmploye().equals(livreur.getIdEmploye())))) {
                throw new IllegalArgumentException("Livreur with ID " + livreur.getIdEmploye() + " is already affected.");
            }
        }

        // Verify that at least one livreur has a valid permit.
        Date today = new Date();
        boolean hasValidDriver = newLivreurs.stream()
                .anyMatch(livreur -> livreur.isAPermis() &&
                        (livreur.getDateExpirationPermis() != null && livreur.getDateExpirationPermis().after(today)));
        if (!hasValidDriver) {
            throw new IllegalArgumentException("At least one livreur must have a valid permit.");
        }

        // Remove association for livreurs that are no longer in the new list.
        if (existingEquipe.getLivreurs() != null) {
            existingEquipe.getLivreurs().stream()
                    .filter(oldLivreur -> newLivreurs.stream().noneMatch(newL -> newL.getIdEmploye().equals(oldLivreur.getIdEmploye())))
                    .forEach(oldLivreur -> {
                        oldLivreur.setAffecte(false);
                        oldLivreur.setEquipeLivreur(null);
                    });
        }

        // For each new livreur, set affectation and update team association.
        newLivreurs.forEach(livreur -> {
            livreur.setAffecte(true);
            livreur.setEquipeLivreur(existingEquipe);
        });

        // Update the team details (e.g., numEquipe if needed).
        existingEquipe.setLivreurs(newLivreurs);
        existingEquipe.setNumEquipe(equipeLivreur.getNumEquipe());

        return equipeLivreurRepository.save(existingEquipe);
    }
    @Transactional
    public void supprimerEquipe(Integer numEquipe) {
        EquipeLivreurEntity equipe = equipeLivreurRepository.findById(numEquipe)
                .orElseThrow(() -> new EntityNotFoundException("Equipe not found with the number: " + numEquipe));

        // For each livreur in the team, update their affecte flag and remove the team association.
        equipe.getLivreurs().forEach(livreur -> {
            livreur.setAffecte(false);
            livreur.setEquipeLivreur(null);
        });

        // Delete the team.
        equipeLivreurRepository.delete(equipe);
    }



}