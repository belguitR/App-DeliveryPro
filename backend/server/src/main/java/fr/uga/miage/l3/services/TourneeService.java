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
    public TourneeEntity createTournee(TourneeEntity tourneeEntity,
                                       Integer equipeLivreurId,
                                       List<Integer> commandesIds) {
        // 1. Vérifier que l'équipe existe
        EquipeLivreurEntity equipe = equipeLivreurRepository.findByNumEquipe(equipeLivreurId)
                .orElseThrow(() -> new RuntimeException("EquipeLivreur not found with id: " + equipeLivreurId));

        // Associer l'équipe à la tournée
        tourneeEntity.setEquipeLivreur(equipe);

        // 2. Si des commandesIds sont fournies, vérifier leur état
        if (commandesIds != null && !commandesIds.isEmpty()) {
            // Récupère toutes les commandes correspondantes aux IDs
            List<CommandeEntity> commandes = commandeRepository.findAllById(commandesIds);

            // Vérifier que chaque commande est en état OUVERTE
            boolean hasInvalidCommand = commandes.stream()
                    .anyMatch(cmd -> cmd.getEtat() != EtatCommande.OUVERTE);

            if (hasInvalidCommand) {
                // Si au moins une commande n'est pas OUVERTE, on rejette la création
                throw new IllegalArgumentException("Une ou plusieurs commandes ne sont pas en état OUVERTE !");
            }

            // 3. Réordonner les commandes selon l'ordre des IDs
            Map<Integer, CommandeEntity> commandesMap = commandes.stream()
                    .collect(Collectors.toMap(CommandeEntity::getReference, Function.identity()));
            List<CommandeEntity> orderedCommandes = commandesIds.stream()
                    .map(commandesMap::get)
                    .collect(Collectors.toList());

            // 4. Associer la tournée à chaque commande
            orderedCommandes.forEach(commande -> {
                commande.setTournee(tourneeEntity);
                // On met l'état à PLANIFIEE
                commande.setEtat(EtatCommande.PLANIFIEE);
            });
            // Assigner la liste ordonnée à la tournée
            tourneeEntity.setCommandes(orderedCommandes);
        }

        // 5. Sauvegarder la tournée
        return tourneeRepository.save(tourneeEntity);
    }



    @Transactional
    public void deleteTournee(Integer idTournee) {
        TourneeEntity tournee = tourneeRepository.findByNumTournee(idTournee)
                .orElseThrow(() -> new EntityNotFoundException("Tournée non trouvée avec l'ID : " + idTournee));
        tourneeRepository.delete(tournee);
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