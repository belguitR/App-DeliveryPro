package fr.uga.miage.l3.services;

import fr.uga.miage.l3.models.EntrepotEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repository.EntrepotRepository;
import fr.uga.miage.l3.repository.LivreurRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LivreurService {
     // missing exceptions in exception folder


    private final LivreurRepository livreurRepository;
    private final EntrepotRepository entrepotRepository;

    /**
     * Crée un nouveau livreur après vérification que l'âge >= 18 ans.
     */
    public LivreurEntity createLivreur(LivreurEntity livreur) {

        // check 18
        if (!isAtLeast18(livreur.getDateNaissance())) {
            throw new IllegalArgumentException("Le livreur doit avoir au moins 18 ans");
        }

        // check if livreur already exists - autogenerate sinon
        if (livreur.getIdEmploye() != null && livreurRepository.existsById(livreur.getIdEmploye())) {
            throw new RuntimeException("Livreur already exists with id: " + livreur.getIdEmploye());
        }



        // Handle the entrepot: if provided, check for an existing one by name
        if (livreur.getEntrepot() != null) {
            String entrepotName = livreur.getEntrepot().getNom();
            // Since 'nom' is the PK in EntrepotEntity, use findById
            EntrepotEntity existingEntrepot = entrepotRepository.findById(entrepotName).orElse(null);
            if (existingEntrepot != null) {
                livreur.setEntrepot(existingEntrepot);
            } else {
                // Save the new entrepot if it doesn't exist
                EntrepotEntity newEntrepot = entrepotRepository.save(livreur.getEntrepot());
                livreur.setEntrepot(newEntrepot);
            }
        }

        return livreurRepository.save(livreur);
    }

    /**
     * Récupère la liste de tous les livreurs.
     */
    public List<LivreurEntity> getAllLivreurs() {
        return livreurRepository.findAll();
    }

    public LivreurEntity updateLivreur(String idLivreur, LivreurEntity updatedLivreur) {
        // Retrieve the existing entity
        LivreurEntity existingLivreur = livreurRepository.findById(idLivreur)
                .orElseThrow(() -> new RuntimeException("Livreur not found: " + idLivreur));

        // Update allowed fields (excluding id)
        existingLivreur.setNom(updatedLivreur.getNom());
        existingLivreur.setPrenom(updatedLivreur.getPrenom());
        existingLivreur.setPhotoURL(updatedLivreur.getPhotoURL());
        existingLivreur.setTelephone(updatedLivreur.getTelephone());
        existingLivreur.setEmail(updatedLivreur.getEmail());
        existingLivreur.setEntrepot(updatedLivreur.getEntrepot());
        existingLivreur.setEquipeLivreur(updatedLivreur.getEquipeLivreur());
        existingLivreur.setAPermis(updatedLivreur.isAPermis());
        existingLivreur.setAffecte(updatedLivreur.isAffecte());

        // Validate and update dateNaissance
        Date newDateNaissance = updatedLivreur.getDateNaissance();
        if (newDateNaissance != null && !isAtLeast18(newDateNaissance)) {
            throw new IllegalArgumentException("Livreur must be at least 18 years old.");
        }
        existingLivreur.setDateNaissance(newDateNaissance);

        return livreurRepository.save(existingLivreur);
    }
    /**
     * Vérifie que la date de naissance correspond à un âge >= 18 ans.
     */
    private boolean isAtLeast18(Date dateNaissance) {
        if (dateNaissance == null) {
            return false;
        }

        LocalDate birthDate = dateNaissance.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();

        return Period.between(birthDate, today).getYears() >= 18;
    }
}

