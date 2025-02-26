package fr.uga.miage.l3.services;

import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repository.LivreurRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class LivreurService {

    private final LivreurRepository livreurRepository;

    public LivreurService(LivreurRepository livreurRepository) {
        this.livreurRepository = livreurRepository;
    }

    /**
     * Crée un nouveau livreur après vérification que l'âge >= 18 ans.
     */
    public LivreurEntity createLivreur(LivreurEntity livreur) {
        if (!isAtLeast18(livreur.getDateNaissance())) {
            throw new IllegalArgumentException("Le livreur doit avoir au moins 18 ans");
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
     * @param idLivreur  identifiant du livreur (String)
     * @param livreur    nouvelle version du livreur
     */
    public LivreurEntity updateLivreur(String idLivreur, LivreurEntity livreur) {
        if (!livreurRepository.existsById(idLivreur)) {
            throw new RuntimeException("Livreur introuvable : " + idLivreur);
        }

        livreur.setIdEmploye(idLivreur);
        return livreurRepository.save(livreur);
    }

    /**
     * Vérifie que la date de naissance correspond à un âge >= 18 ans.
     */
    private boolean isAtLeast18(Date dateNaissance) {
        if (dateNaissance == null) {
            return false;
        }
        LocalDate birthDate = dateNaissance.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return Period.between(birthDate, LocalDate.now()).getYears() >= 18;
    }
}

