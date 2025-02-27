package fr.uga.miage.l3.services;

import fr.uga.miage.l3.enums.EtatTournee;
import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repository.TourneeRepository;
import fr.uga.miage.l3.repository.LivreurRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TourneeService {

    private final TourneeRepository tourneeRepository;
    private final LivreurRepository livreurRepository;

    public List<TourneeEntity> getAllTournees() {
        return tourneeRepository.findAll();
    }

    public TourneeEntity createTournee(TourneeEntity tournee) {
        return tourneeRepository.save(tournee);
    }

    @Transactional
    public TourneeEntity updateTournee(Integer idTournee, TourneeEntity tourneeDetails) {
        TourneeEntity tournee = tourneeRepository.findById(idTournee)
                .orElseThrow(() -> new EntityNotFoundException("Tournée non trouvée avec l'ID : " + idTournee));

        if (tourneeDetails.getDate() == null || tourneeDetails.getEquipeLivreur() == null) {
            throw new IllegalArgumentException("Date et équipe livreur doivent être fournies.");
        }

        tournee.setDate(tourneeDetails.getDate());
        tournee.setEtat(tourneeDetails.getEtat());
        tournee.setDistanceAParcourir(tourneeDetails.getDistanceAParcourir());
        tournee.setMontant(tourneeDetails.getMontant());
        tournee.setTempsDeMontageTheorique(tourneeDetails.getTempsDeMontageTheorique());
        tournee.setTempsDeMontageEffectif(tourneeDetails.getTempsDeMontageEffectif());
        tournee.setPlanificateur(tourneeDetails.getPlanificateur());
        tournee.setEquipeLivreur(tourneeDetails.getEquipeLivreur());
        tournee.setClients(tourneeDetails.getClients());

        return tourneeRepository.save(tournee);
    }

    public TourneeEntity getTourneeDuJourParLivreur(String emailLivreur) {
        LivreurEntity livreur = livreurRepository.findByEmail(emailLivreur)
                .orElseThrow(() -> new EntityNotFoundException("Aucun livreur trouvé avec l'email : " + emailLivreur));

        if (livreur.getEquipeLivreur() == null) {
            throw new EntityNotFoundException("Le livreur n'est pas affecté à une équipe.");
        }

        LocalDate today = LocalDate.now();

        Optional<TourneeEntity> tourneeOpt = tourneeRepository.findAll().stream()
                .filter(tournee -> tournee.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(today)
                        && tournee.getEquipeLivreur().equals(livreur.getEquipeLivreur()))
                .findFirst();

        return tourneeOpt.orElseThrow(() -> new EntityNotFoundException("Aucune tournée trouvée pour aujourd'hui."));
    }
}
