package fr.uga.miage.l3.services;

import fr.uga.miage.l3.enums.EtatCommande;
import fr.uga.miage.l3.enums.EtatTournee;
import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.repository.CommandeRepository;
import fr.uga.miage.l3.repository.TourneeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final TourneeRepository tourneeRepository;

    public List<CommandeEntity> getAllCommandes() {
        return commandeRepository.findAll();
    }

    public List<CommandeEntity> getCommandesByTournee(Integer idTournee) {
        return commandeRepository.findByTourneeNumTournee(idTournee);
    }

    @Transactional
    public CommandeEntity updateCommande(String reference, CommandeEntity commandeDetails) {
        CommandeEntity commande = commandeRepository.findById(reference)
                .orElseThrow(() -> new EntityNotFoundException("Commande non trouvée avec la référence : " + reference));

        // Mise à jour des champs
        commande.setEtat(commandeDetails.getEtat());
        commande.setDate(commandeDetails.getDate());
        commande.setHorairePreferable(commandeDetails.getHorairePreferable());
        commande.setDateLivre(commandeDetails.getDateLivre());
        commande.setDateLivraisonEstimee(commandeDetails.getDateLivraisonEstimee());
        commande.setMontantTotal(commandeDetails.getMontantTotal());
        commande.setTournee(commandeDetails.getTournee());

        CommandeEntity updatedCommande = commandeRepository.save(commande);

        if (commande.getTournee() != null) {
            mettreAJourEtatTournee(commande.getTournee());
        }

        return updatedCommande;
    }

    private void mettreAJourEtatTournee(TourneeEntity tournee) {
        List<CommandeEntity> commandes = commandeRepository.findByTourneeNumTournee(tournee.getNumTournee());

        boolean enCours = commandes.stream().anyMatch(cmd -> cmd.getEtat() == EtatCommande.EN_LIVRAISON);
        boolean toutesLivrees = commandes.stream().allMatch(cmd -> cmd.getEtat() == EtatCommande.LIVREE);

        if (toutesLivrees) {
            tournee.setEtat(EtatTournee.COMPLETEE);
        } else if (enCours) {
            tournee.setEtat(EtatTournee.EN_COURS);
        }

        tourneeRepository.save(tournee);
    }
}