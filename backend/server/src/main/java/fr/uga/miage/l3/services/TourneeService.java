package fr.uga.miage.l3.services;

import fr.uga.miage.l3.enums.EtatCommande;
import fr.uga.miage.l3.enums.EtatTournee;
import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repository.CommandeRepository;
import fr.uga.miage.l3.repository.EquipeLivreurRepository;
import fr.uga.miage.l3.repository.TourneeRepository;
import fr.uga.miage.l3.repository.LivreurRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourneeService {

    private final TourneeRepository tourneeRepository;
    private final LivreurRepository livreurRepository;
    private final EquipeLivreurRepository equipeLivreurRepository;
    private final CommandeRepository commandeRepository;

    public List<TourneeEntity> getAllTournees() {
        return tourneeRepository.findAll();
    }

    @Transactional
    public TourneeEntity getTourneeWithCommandes(Integer numTournee) {
        TourneeEntity tournee = tourneeRepository.findByNumTourneeWithCommandes(numTournee);
        if(tournee == null){
            throw new EntityNotFoundException("Tournee not found with id: " + numTournee);
        }
        // Force initialization in case the commandes list is lazy loaded
        tournee.getCommandes().size();
        return tournee;
    }
@Transactional
public TourneeEntity createTournee(TourneeEntity tourneeEntity, Integer equipeLivreurId, List<Integer> commandesIds) {
    EquipeLivreurEntity equipe = equipeLivreurRepository.findByNumEquipe(equipeLivreurId)
            .orElseThrow(() -> new RuntimeException("EquipeLivreur not found with id: " + equipeLivreurId));
    tourneeEntity.setEquipeLivreur(equipe);

    if (commandesIds != null && !commandesIds.isEmpty()) {
        // Retrieve commandes without guarantee of order
        List<CommandeEntity> commandes = commandeRepository.findAllById(commandesIds);
        // Map commandes by id
        Map<Integer, CommandeEntity> commandesMap = commandes.stream()
                .collect(Collectors.toMap(CommandeEntity::getReference, Function.identity()));
        // Reorder commandes according to commandesIds order
        List<CommandeEntity> orderedCommandes = commandesIds.stream()
                .map(commandesMap::get)
                .collect(Collectors.toList());
        // Set the tournee for each commande and update their status
        orderedCommandes.forEach(commande -> {
            commande.setTournee(tourneeEntity);
            commande.setEtat(EtatCommande.PLANIFIEE);
        });
        tourneeEntity.setCommandes(orderedCommandes);
    }

    return tourneeRepository.save(tourneeEntity);
}


    @Transactional
    public TourneeEntity updateTournee(Integer idTournee, TourneeEntity tourneeDetails) {
        TourneeEntity tournee = tourneeRepository.findByNumTournee(idTournee)
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
