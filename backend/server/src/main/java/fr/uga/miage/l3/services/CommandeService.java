package fr.uga.miage.l3.services;

import fr.uga.miage.l3.enums.EtatCommande;
import fr.uga.miage.l3.enums.EtatTournee;
import fr.uga.miage.l3.models.ClientEntity;
import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.models.TourneeEntity;
import fr.uga.miage.l3.repository.ClientRepository;
import fr.uga.miage.l3.repository.CommandeRepository;
import fr.uga.miage.l3.repository.TourneeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final TourneeRepository tourneeRepository;
    private final ClientRepository clientRepository;

    public List<CommandeEntity> getAllCommandes() {
        return commandeRepository.findAll();
    }

    public List<CommandeEntity> getCommandesByTournee(Integer idTournee) {
        return tourneeRepository.findByNumTourneeWithCommandes(idTournee).getCommandes();
    }
    public List<CommandeEntity> getCommandesNonLivrees() {
        return commandeRepository.findByEtatNotOrderByDateAsc(EtatCommande.LIVREE);
    }


    @Transactional
    public CommandeEntity updateCommande(Integer reference, CommandeEntity commandeDetails) {
        CommandeEntity commande = commandeRepository.findById(reference)
                .orElseThrow(() -> new EntityNotFoundException("Commande non trouvée avec la référence : " + reference));

        // Vérifier doublon (mêmes champs sauf référence)
        List<CommandeEntity> commandes = commandeRepository.findAll();
        for (CommandeEntity c : commandes) {
            if (!c.getReference().equals(reference)
                    && Objects.equals(c.getEtat(), commandeDetails.getEtat())
                    && Objects.equals(c.getDate(), commandeDetails.getDate())
                    && Objects.equals(c.getHorairePreferable(), commandeDetails.getHorairePreferable())
                    && Objects.equals(c.getDateLivre(), commandeDetails.getDateLivre())
                    && Objects.equals(c.getDateLivraisonEstimee(), commandeDetails.getDateLivraisonEstimee())
                    && Objects.equals(c.getMontantTotal(), commandeDetails.getMontantTotal())) {
                throw new IllegalArgumentException("Commande en doublons");
            }
        }

        if (commandeDetails.getEtat() != EtatCommande.LIVREE && commandeDetails.getDateLivre() != null) {
            throw new IllegalArgumentException("Impossible de définir une dateLivre si la commande n'est pas LIVREE");
        }

        // Mettre à jour les champs
        commande.setEtat(commandeDetails.getEtat());
        commande.setDate(commandeDetails.getDate());
        commande.setHorairePreferable(commandeDetails.getHorairePreferable());
        commande.setDateLivre(commandeDetails.getDateLivre());
        commande.setDateLivraisonEstimee(commandeDetails.getDateLivraisonEstimee());
        commande.setMontantTotal(commandeDetails.getMontantTotal());


        if (commandeDetails.getClient() != null) {
            String clientCode = commandeDetails.getClient().getCode();
            ClientEntity existingClient = clientRepository.findById(clientCode)
                    .orElseThrow(() -> new EntityNotFoundException("Client non trouvé avec le code : " + clientCode));

            commande.setClient(existingClient);
        }

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