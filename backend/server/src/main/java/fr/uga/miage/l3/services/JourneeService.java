package fr.uga.miage.l3.services;

import fr.uga.miage.l3.components.JourneeComponent;
import fr.uga.miage.l3.enums.EtatCommande;
import fr.uga.miage.l3.enums.EtatTournee;
import fr.uga.miage.l3.enums.StatusEquipeLivreurs;
import fr.uga.miage.l3.exceptions.technical.BusinessRuleException;
import fr.uga.miage.l3.exceptions.technical.ResourceNotFoundException;
import fr.uga.miage.l3.mappers.JourneeMapper;
import fr.uga.miage.l3.models.*;
import fr.uga.miage.l3.repositories.*;
import fr.uga.miage.l3.requests.JourneeRequest;
import fr.uga.miage.l3.requests.TourneeRequest;
import fr.uga.miage.l3.responses.JourneeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JourneeService {
    private final JourneeComponent journeeComponent;
    private final PlanificateurRepository planificateurRepository;
    private final CamionRepository camionRepository;
    private final TourneeRepository tourneeRepository;
    private final EquipeLivreursRepository equipeLivreursRepository;
    private final CommandeRepository commandeRepository;

    public List<JourneeResponse> getAllJournees() {
        return journeeComponent.findAll().stream()
                .map(JourneeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public JourneeResponse getJourneeByReference(String reference) {
        JourneeEntity entity = journeeComponent.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Journée non trouvée : " + reference));
        return JourneeMapper.toResponse(entity);
    }


    @Transactional
    public JourneeResponse createJournee(JourneeRequest request) {
        checkJourneeReferenceAndDate(request.getReference(), request.getDate());

        if (request.getTournees() == null || request.getTournees().isEmpty()) {
            throw new BusinessRuleException("La journée doit contenir au moins une tournée");
        }

        PlanificateurEntity planificateur = null;
        if (request.getPlanificateurEmail() != null) {
            planificateur = planificateurRepository.findByEmail(request.getPlanificateurEmail())
                    .orElse(null);
        }

        JourneeEntity journee = new JourneeEntity();
        journee.setReference(request.getReference());
        journee.setDate(request.getDate());
        journee.setPlanificateur(planificateur);

        List<TourneeEntity> tournees = new java.util.ArrayList<>();
        List<List<CommandeEntity>> allCommandes = new java.util.ArrayList<>();
        if (request.getTournees() != null) {
            for (TourneeRequest tReq : request.getTournees()) {
                if (tourneeRepository.findById(tReq.getReference()).isPresent()) {
                    throw new BusinessRuleException("La référence de la tournée doit être unique");
                }
                if (tReq.getEtat() == null) {
                    throw new BusinessRuleException("L'état de la tournée est obligatoire");
                }
                EtatTournee etat;
                try {
                    etat = EtatTournee.valueOf(tReq.getEtat().toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new BusinessRuleException("État invalide pour la tournée : " + tReq.getEtat());
                }
                CamionEntity camion;
                if (tReq.getCamionCode() != null) {
                    camion = camionRepository.findById(tReq.getCamionCode())
                            .orElseThrow(() -> {
                                return new BusinessRuleException("Camion non trouvé : " + tReq.getCamionCode());
                            });
                    if (tourneeRepository.findAll().stream().anyMatch(t ->
                            t.getCamion() != null &&
                                    t.getCamion().getCode().equals(camion.getCode()) &&
                                    t.getJour() != null &&
                                    t.getJour().getDate().equals(request.getDate()))) {
                        throw new BusinessRuleException("Camion déjà affecté à une autre tournée ce jour-là");
                    }
                } else {
                    camion = null;
                }
                EquipeLivreursEntity equipe;
                if (tReq.getEquipeLivreursId() != null) {
                    equipe = equipeLivreursRepository.findById(tReq.getEquipeLivreursId())
                            .orElseThrow(() -> {
                                return new BusinessRuleException("Équipe de livreurs non trouvée : " + tReq.getEquipeLivreursId());
                            });
                    if (equipe.getStatus() != StatusEquipeLivreurs.PRET) {
                        throw new BusinessRuleException("L'équipe de livreurs n'est pas disponible pour l'affectation");
                    }
                    if (tourneeRepository.findAll().stream().anyMatch(t ->
                            t.getEquipeLivreurs() != null &&
                                    t.getEquipeLivreurs().getIdEquipe().equals(equipe.getIdEquipe()) &&
                                    t.getJour() != null &&
                                    t.getJour().getDate().equals(request.getDate()))) {
                        throw new BusinessRuleException("L'équipe de livreurs est déjà affectée à une autre tournée ce jour-là");
                    }
                } else {
                    equipe = null;
                }
                if (tReq.getCommandeReferences() == null || tReq.getCommandeReferences().isEmpty()) {
                    throw new BusinessRuleException("La tournée doit contenir au moins une commande");
                }
                List<CommandeEntity> commandes = tReq.getCommandeReferences().stream()
                        .map(ref -> {
                            CommandeEntity commande = commandeRepository.findById(ref)
                                    .orElseThrow(() -> {
                                        return new BusinessRuleException("Commande non trouvée : " + ref);
                                    });
                            if (commande.getEtat() != EtatCommande.OUVERTE) {
                                throw new BusinessRuleException("La commande " + ref + " n'est pas OUVERTE et ne peut pas être planifiée");
                            }
                            if (commande.getTournee() != null) {
                                throw new BusinessRuleException("La commande " + ref + " est déjà affectée à une tournée");
                            }
                            return commande;
                        })
                        .collect(Collectors.toList());

                if (camion == null) {
                    throw new BusinessRuleException("La tournée doit avoir un camion affecté");
                }
                if (equipe == null) {
                    throw new BusinessRuleException("La tournée doit avoir une équipe de livreurs affectée");
                }

                TourneeEntity tournee = new TourneeEntity();
                tournee.setReference(tReq.getReference());
                tournee.setEtat(etat);
                tournee.setLettre(tReq.getLettre());
                tournee.setDistanceAParcourir(tReq.getDistanceAParcourir() != null ? tReq.getDistanceAParcourir() : 0.0);
                tournee.setTempsAssemblageReel(tReq.getTempsAssemblageReel());
                tournee.setJour(journee);
                tournee.setCamion(camion);
                tournee.setEquipeLivreurs(equipe);
                tournee.setCommandes(commandes);

                tournees.add(tournee);
                allCommandes.add(commandes);
            }
        }


        JourneeEntity savedJournee = journeeComponent.save(journee);
        for (int i = 0; i < tournees.size(); i++) {
            TourneeEntity tournee = tournees.get(i);
            tournee.setJour(savedJournee);
            TourneeEntity savedTournee = tourneeRepository.save(tournee);
            List<CommandeEntity> commandes = allCommandes.get(i);
            for (CommandeEntity commande : commandes) {
                commande.setTournee(savedTournee);
                commande.setEtat(EtatCommande.PLANIFIEE);
                commandeRepository.save(commande);
            }
        }

        JourneeEntity withTournees = journeeComponent.findByReference(savedJournee.getReference()).orElse(savedJournee);
        return JourneeMapper.toResponse(withTournees);
    }


    private void checkJourneeReferenceAndDate(String reference, LocalDate date) {
        if (reference == null || reference.isEmpty()) {
            throw new BusinessRuleException("La référence de la journée est obligatoire");
        }
        if (journeeComponent.findByReference(reference).isPresent()) {
            throw new BusinessRuleException("La référence de la journée doit être unique");
        }
        if (date == null || date.isBefore(LocalDate.now())) {
            throw new BusinessRuleException("La date de la journée doit être aujourd'hui ou dans le futur");
        }
    }


    @Transactional
    public void deleteJournee(String reference) {
        JourneeEntity journee = journeeComponent.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Journée non trouvée : " + reference));
        if (journee.getTournees() != null) {
            for (TourneeEntity tournee : journee.getTournees()) {
                if (tournee.getCommandes() != null) {
                    for (CommandeEntity commande : tournee.getCommandes()) {
                        commande.setTournee(null);
                        if (commande.getEtat() == EtatCommande.PLANIFIEE) {
                            commande.setEtat(EtatCommande.OUVERTE);
                        }
                        commandeRepository.save(commande);
                    }
                }
                tourneeRepository.deleteById(tournee.getReference());
            }
        }
        journeeComponent.deleteByReference(reference);
    }


}
