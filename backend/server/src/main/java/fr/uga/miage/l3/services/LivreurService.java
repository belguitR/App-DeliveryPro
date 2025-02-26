package fr.uga.miage.l3.services;


import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repository.LivreurRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

@Service
public class LivreurService {

    private final LivreurRepository livreurRepository;

    public LivreurService(LivreurRepository livreurRepository) {
        this.livreurRepository = livreurRepository;
    }

    public LivreurEntity createLivreur(LivreurEntity livreur) {

        if (!isAtLeast18(livreur.getDateNaissance())) {
            throw new IllegalArgumentException("Le livreur doit avoir au moins 18 ans");
        }

        return livreurRepository.save(livreur);
    }

    private boolean isAtLeast18(Date dateNaissance) {
        if (dateNaissance == null) {

            return false;
        }
        LocalDate birthDate = dateNaissance
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return Period.between(birthDate, LocalDate.now()).getYears() >= 18;
    }
}

