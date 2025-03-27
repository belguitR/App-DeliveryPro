package fr.uga.miage.l3.services;

import fr.uga.miage.l3.models.EntrepotEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repository.EntrepotRepository;
import fr.uga.miage.l3.repository.LivreurRepository;
import jakarta.persistence.EntityNotFoundException; // Importer depuis jakarta.persistence
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class LivreurService {

    private final LivreurRepository livreurRepository;
    private final EntrepotRepository entrepotRepository;

    /**
     * Crée un nouveau livreur après vérification que l'âge >= 18 ans.
     */
    public LivreurEntity createLivreur(LivreurEntity livreur) {

        // Vérifie l'âge
        if (!isAtLeast18(livreur.getDateNaissance())) {
            throw new IllegalArgumentException("Le livreur doit avoir au moins 18 ans");
        }

        // Vérifie s'il existe déjà
        if (livreur.getIdEmploye() != null && livreurRepository.existsById(livreur.getIdEmploye())) {
            throw new IllegalArgumentException(
                    "Livreur already exists with id: " + livreur.getIdEmploye()
            );
        }

        // Gère l'entrepôt (création si nécessaire)
        if (livreur.getEntrepot() != null) {
            String entrepotName = livreur.getEntrepot().getNom();
            EntrepotEntity existingEntrepot = entrepotRepository.findById(entrepotName).orElse(null);
            if (existingEntrepot != null) {
                livreur.setEntrepot(existingEntrepot);
            } else {
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

    /**
     * Met à jour un livreur existant.
     */
    public LivreurEntity updateLivreur(String idLivreur, LivreurEntity updatedLivreur) {
        // Vérifie si le livreur existe en base
        LivreurEntity existingLivreur = livreurRepository.findById(idLivreur)
                .orElseThrow(() -> new EntityNotFoundException("Livreur not found: " + idLivreur));

        // Mettre à jour les champs autorisés
        existingLivreur.setNom(updatedLivreur.getNom());
        existingLivreur.setPrenom(updatedLivreur.getPrenom());
        existingLivreur.setPhotoURL(updatedLivreur.getPhotoURL());
        existingLivreur.setTelephone(updatedLivreur.getTelephone());
        existingLivreur.setEmail(updatedLivreur.getEmail());
        existingLivreur.setEntrepot(updatedLivreur.getEntrepot());
        existingLivreur.setEquipeLivreur(updatedLivreur.getEquipeLivreur());
        existingLivreur.setAPermis(updatedLivreur.isAPermis());
        existingLivreur.setAffecte(updatedLivreur.isAffecte());

        // Vérifie l'âge pour la nouvelle dateNaissance
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

        // Si c'est déjà un java.sql.Date, on cast directement
        java.sql.Date sqlDate;
        if (dateNaissance instanceof java.sql.Date) {
            sqlDate = (java.sql.Date) dateNaissance;
        } else {
            // Si c'est un java.util.Date, on le convertit en java.sql.Date
            sqlDate = new java.sql.Date(dateNaissance.getTime());
        }

        // Maintenant, on peut appeler toLocalDate() sans UnsupportedOperationException
        LocalDate birthDate = sqlDate.toLocalDate();
        LocalDate today = LocalDate.now();

        return Period.between(birthDate, today).getYears() >= 18;
    }

}
