package fr.uga.miage.l3.services;

import fr.uga.miage.l3.enums.EtatCommande;
import fr.uga.miage.l3.enums.EtatTournee;
import fr.uga.miage.l3.enums.StatusEquipeLivreur;
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
import java.util.*;
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
        if (tournee == null) {
            throw new EntityNotFoundException("Tournee not found with id: " + numTournee);
        }
        tournee.getCommandes().size();
        return tournee;
    }

    @Transactional
    public TourneeEntity createTournee(TourneeEntity tourneeEntity, Integer equipeLivreurId, List<Integer> commandesIds) {
        int newId = tourneeRepository.findAll().stream()
                .map(TourneeEntity::getNumTournee)
                .max(Integer::compareTo)
                .orElse(0) + 1;
        tourneeEntity.setNumTournee(newId);

        EquipeLivreurEntity equipe = equipeLivreurRepository.findByNumEquipe(equipeLivreurId)
                .orElseThrow(() -> new RuntimeException("EquipeLivreur not found with id: " + equipeLivreurId));

//        if (equipe.getStatus() != StatusEquipeLivreur.PRET) {
//            throw new IllegalStateException("L'équipe n'est pas prête");
//        }

        LocalDate tourneeDate = tourneeEntity.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        if (equipe.getDatesAffectes() != null && equipe.getDatesAffectes().contains(tourneeDate)) {
            throw new IllegalStateException("L'équipe est déjà affectée à cette date");
        }

        // Initialize the dates list if null
        if (equipe.getDatesAffectes() == null) {
            equipe.setDatesAffectes(new HashSet<>());
        }

        Map<Integer, CommandeEntity> commandesMap = commandeRepository.findAllById(commandesIds).stream()
                .collect(Collectors.toMap(CommandeEntity::getReference, Function.identity()));

        if (commandesMap.size() != commandesIds.size()) {
            throw new EntityNotFoundException("One or more Commande entities not found");
        }

        List<CommandeEntity> orderedCommandes = commandesIds.stream()
                .map(id -> {
                    CommandeEntity cmd = commandesMap.get(id);
                    if (cmd.getEtat() != EtatCommande.OUVERTE) {
                        throw new IllegalArgumentException("Commande " + cmd.getReference() + " n'est pas OUVERTE");
                    }
                    cmd.setTournee(tourneeEntity);
                    cmd.setEtat(EtatCommande.PLANIFIEE);
                    return cmd;
                })
                .collect(Collectors.toList());

        // Save commandes BEFORE saving tournee
        commandeRepository.saveAll(orderedCommandes);

        tourneeEntity.setCommandes(orderedCommandes);
        tourneeEntity.setEquipeLivreur(equipe);

        // Add the date to datesAffectes
        equipe.getDatesAffectes().add(tourneeDate);
        equipe.setStatus(StatusEquipeLivreur.EN_LIVRAISON);
        equipeLivreurRepository.save(equipe); // Make sure to save the updated equipe


        TourneeEntity saved = tourneeRepository.save(tourneeEntity);
        return getTourneeWithCommandes(saved.getNumTournee());
    }





    @Transactional
    public void deleteTournee(Integer idTournee) {
        TourneeEntity tournee = tourneeRepository.findByNumTournee(idTournee)
                .orElseThrow(() -> new EntityNotFoundException("Tournée non trouvée avec l'ID : " + idTournee));

        if (tournee.getEtat() != EtatTournee.EN_ATTENTE) {
            throw new IllegalStateException("Seules les tournées en attente peuvent être supprimées");
        }


        List<CommandeEntity> commandes = tournee.getCommandes();
        commandes.forEach(cmd -> {
            cmd.setEtat(EtatCommande.OUVERTE);
            cmd.setTournee(null);
            commandeRepository.save(cmd);
        });
        EquipeLivreurEntity equipe = tournee.getEquipeLivreur();
        if (equipe != null) {
            LocalDate tourneeDate = new java.sql.Date(tournee.getDate().getTime()).toLocalDate();
            equipe.getDatesAffectes().remove(tourneeDate);
            equipe.setStatus(StatusEquipeLivreur.PRET);
        }

        tourneeRepository.delete(tournee);
    }


    public List<TourneeEntity> getTourneesParLivreur(String emailLivreur) {
        LivreurEntity livreur = livreurRepository.findByEmail(emailLivreur)
                .orElseThrow(() -> new EntityNotFoundException("Aucun livreur trouvé avec l'email : " + emailLivreur));

        if (livreur.getEquipeLivreur() == null) {
            throw new EntityNotFoundException("Le livreur n'est pas affecté à une équipe.");
        }

        return tourneeRepository.findAll().stream()
                .filter(t -> t.getEquipeLivreur() != null &&
                        t.getEquipeLivreur().getNumEquipe().equals(livreur.getEquipeLivreur().getNumEquipe()))
                .map(t -> getTourneeWithCommandes(t.getNumTournee()))
                .collect(Collectors.toList());
    }


    @Transactional
    public TourneeEntity updateTournee(TourneeEntity updatedTournee, Integer equipeLivreurId, List<Integer> newCommandeIds) {
        TourneeEntity existingTournee = tourneeRepository.findByNumTournee(updatedTournee.getNumTournee())
                .orElseThrow(() -> new EntityNotFoundException("Tournée non trouvée avec l'ID : " + updatedTournee.getNumTournee()));


        List<CommandeEntity> previousCommandes = existingTournee.getCommandes();
        previousCommandes.forEach(cmd -> {
            cmd.setEtat(EtatCommande.OUVERTE);
            cmd.setTournee(null);
            commandeRepository.save(cmd);
        });


        EquipeLivreurEntity newEquipe = equipeLivreurRepository.findByNumEquipe(equipeLivreurId)
                .orElseThrow(() -> new EntityNotFoundException("Équipe non trouvée avec l'ID : " + equipeLivreurId));

        LocalDate newDate;
        LocalDate oldDate;

        Date rawNewDate = updatedTournee.getDate();
        Date rawOldDate = existingTournee.getDate();

        if (rawNewDate instanceof java.sql.Date) {
            newDate = ((java.sql.Date) rawNewDate).toLocalDate();
        } else {
            newDate = rawNewDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        if (rawOldDate instanceof java.sql.Date) {
            oldDate = ((java.sql.Date) rawOldDate).toLocalDate();
        } else {
            oldDate = rawOldDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }


        EquipeLivreurEntity oldEquipe = existingTournee.getEquipeLivreur();
        if (!Objects.equals(oldEquipe.getNumEquipe(), newEquipe.getNumEquipe())) {
            oldEquipe.getDatesAffectes().remove(oldDate);
            oldEquipe.setStatus(StatusEquipeLivreur.PRET);
        }


        if (newEquipe.getStatus() != StatusEquipeLivreur.PRET && !Objects.equals(oldEquipe.getNumEquipe(), newEquipe.getNumEquipe())) {
            throw new IllegalStateException("Nouvelle équipe non disponible");
        }
        if (!Objects.equals(oldDate, newDate) && newEquipe.getDatesAffectes().contains(newDate)) {
            throw new IllegalStateException("Nouvelle équipe déjà affectée à cette date");
        }

        // garder l'ordre
        Map<Integer, CommandeEntity> commandesMap = commandeRepository.findAllById(newCommandeIds).stream()
                .collect(Collectors.toMap(CommandeEntity::getReference, Function.identity()));

        if (commandesMap.size() != newCommandeIds.size()) {
            throw new EntityNotFoundException("Une ou plusieurs commandes sont introuvables");
        }

        List<CommandeEntity> orderedCommandes = newCommandeIds.stream()
                .map(id -> {
                    CommandeEntity cmd = commandesMap.get(id);
                    if (cmd.getEtat() != EtatCommande.OUVERTE && !Objects.equals(cmd.getTournee(), existingTournee)) {
                        throw new IllegalArgumentException("Commande " + id + " non modifiable");
                    }
                    cmd.setTournee(existingTournee);
                    cmd.setEtat(EtatCommande.PLANIFIEE);
                    return cmd;
                })
                .collect(Collectors.toList());

        // --- Update tournée
        existingTournee.setDate(updatedTournee.getDate());
        existingTournee.setEtat(updatedTournee.getEtat());
        existingTournee.setDistanceAParcourir(updatedTournee.getDistanceAParcourir());
        existingTournee.setMontant(updatedTournee.getMontant());
        existingTournee.setTempsDeMontageTheorique(updatedTournee.getTempsDeMontageTheorique());
        existingTournee.setTempsDeMontageEffectif(updatedTournee.getTempsDeMontageEffectif());
        existingTournee.setCommandes(orderedCommandes);
        existingTournee.setEquipeLivreur(newEquipe);

        // --- Set équipe dispo
        newEquipe.getDatesAffectes().add(newDate);
        newEquipe.setStatus(StatusEquipeLivreur.EN_LIVRAISON);

        return tourneeRepository.save(existingTournee);
    }



}