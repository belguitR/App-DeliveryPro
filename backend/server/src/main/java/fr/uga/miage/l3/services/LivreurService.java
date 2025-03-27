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

        // Vérifie que nom et prénom sont renseignés
        if (livreur.getNom() == null || livreur.getNom().trim().isEmpty() ||
                livreur.getPrenom() == null || livreur.getPrenom().trim().isEmpty()) {
            throw new IllegalArgumentException("nom et prenom non specifié");
        }

        // Vérifie que email est renseigné
        if (livreur.getEmail() == null || livreur.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("email non specifié");
        }

        // Vérifie que le numéro de téléphone est renseigné
        if (livreur.getTelephone() == null || livreur.getTelephone().trim().isEmpty()) {
            throw new IllegalArgumentException("numero de telephone non specifié");
        }

        // Vérifie que la date de naissance est renseignée
        if (livreur.getDateNaissance() == null) {
            throw new IllegalArgumentException("dateNaissance non specifié");
        }

        // Vérifie l'âge
        if (!isAtLeast18(livreur.getDateNaissance())) {
            throw new IllegalArgumentException("Le livreur doit avoir au moins 18 ans");
        }

        // Vérifie si le livreur existe déjà
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
        // Vérifie que nom et prénom sont renseignés
        if (updatedLivreur.getNom() == null || updatedLivreur.getNom().trim().isEmpty() ||
                updatedLivreur.getPrenom() == null || updatedLivreur.getPrenom().trim().isEmpty()) {
            throw new IllegalArgumentException("nom et prenom non specifié");
        }

        // Vérifie que email est renseigné
        if (updatedLivreur.getEmail() == null || updatedLivreur.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("email non specifié");
        }
        // Vérifie que le numéro de téléphone est renseigné
        if (updatedLivreur.getTelephone() == null || updatedLivreur.getTelephone().trim().isEmpty()) {
            throw new IllegalArgumentException("numero de telephone non specifié");
        }
        // Vérifie que la date de naissance est renseignée
        if (updatedLivreur.getDateNaissance() == null) {
            throw new IllegalArgumentException("dateNaissance non specifié");
        }

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

        java.sql.Date sqlDate;
        if (dateNaissance instanceof java.sql.Date) {
            sqlDate = (java.sql.Date) dateNaissance;
        } else {
            sqlDate = new java.sql.Date(dateNaissance.getTime());
        }

        LocalDate birthDate = sqlDate.toLocalDate();
        LocalDate today = LocalDate.now();

        return Period.between(birthDate, today).getYears() >= 18;
    }

}
