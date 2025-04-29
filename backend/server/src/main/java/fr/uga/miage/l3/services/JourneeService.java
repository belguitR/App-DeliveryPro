package fr.uga.miage.l3.services;

import fr.uga.miage.l3.components.JourneeComponent;
import fr.uga.miage.l3.enums.EtatTournee;
import fr.uga.miage.l3.exceptions.BusinessRuleException;
import fr.uga.miage.l3.exceptions.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Journee not found: " + reference));
        return JourneeMapper.toResponse(entity);
    }


    @Transactional
    public JourneeResponse createJournee(JourneeRequest request) {
        System.out.println("=== Creating Journee: " + request.getReference() + " ===");
        checkJourneeReferenceAndDate(request.getReference(), request.getDate());

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
                System.out.println("  Tournee: " + tReq.getReference());
                if (tourneeRepository.findById(tReq.getReference()).isPresent()) {
                    System.out.println("    ERROR: Tournee reference must be unique");
                    throw new BusinessRuleException("Tournee reference must be unique");
                }
                if (tReq.getEtat() == null) {
                    System.out.println("    ERROR: Tournee etat is required");
                    throw new BusinessRuleException("Tournee etat is required");
                }
                EtatTournee etat;
                try {
                    etat = EtatTournee.valueOf(tReq.getEtat().toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("    ERROR: Invalid etat for Tournee: " + tReq.getEtat());
                    throw new BusinessRuleException("Invalid etat for Tournee: " + tReq.getEtat());
                }
                CamionEntity camion;
                if (tReq.getCamionCode() != null) {
                    camion = camionRepository.findById(tReq.getCamionCode())
                            .orElseThrow(() -> {
                                System.out.println("    ERROR: Camion not found: " + tReq.getCamionCode());
                                return new BusinessRuleException("Camion not found: " + tReq.getCamionCode());
                            });
                    System.out.println("    Camion: " + camion.getCode());
                    if (tourneeRepository.findAll().stream().anyMatch(t ->
                            t.getCamion() != null &&
                                    t.getCamion().getCode().equals(camion.getCode()) &&
                                    t.getJour() != null &&
                                    t.getJour().getDate().equals(request.getDate()))) {
                        throw new BusinessRuleException("Camion already assigned to another tournee on this day");
                    }
                } else {
                    camion = null;
                }
                EquipeLivreursEntity equipe;
                if (tReq.getEquipeLivreursId() != null) {
                    equipe = equipeLivreursRepository.findById(tReq.getEquipeLivreursId())
                            .orElseThrow(() -> {
                                System.out.println("    ERROR: EquipeLivreur not found: " + tReq.getEquipeLivreursId());
                                return new BusinessRuleException("EquipeLivreur not found: " + tReq.getEquipeLivreursId());
                            });
                    System.out.println("    EquipeLivreur: " + equipe.getIdEquipe());
                    if (tourneeRepository.findAll().stream().anyMatch(t ->
                            t.getEquipeLivreurs() != null &&
                                    t.getEquipeLivreurs().getIdEquipe().equals(equipe.getIdEquipe()) &&
                                    t.getJour() != null &&
                                    t.getJour().getDate().equals(request.getDate()))) {
                        throw new BusinessRuleException("EquipeLivreur already assigned to another tournee on this day");
                    }
                } else {
                    equipe = null;
                }
                if (tReq.getCommandeReferences() == null || tReq.getCommandeReferences().isEmpty()) {
                    System.out.println("    ERROR: Tournee must have at least one commande");
                    throw new BusinessRuleException("Tournee must have at least one commande");
                }
                List<CommandeEntity> commandes = tReq.getCommandeReferences().stream()
                        .map(ref -> {
                            CommandeEntity commande = commandeRepository.findById(ref)
                                    .orElseThrow(() -> {
                                        System.out.println("    ERROR: Commande not found: " + ref);
                                        return new BusinessRuleException("Commande not found: " + ref);
                                    });
                            if (commande.getEtat() != fr.uga.miage.l3.enums.EtatCommande.OUVERTE) {
                                System.out.println("    ERROR: Commande " + ref + " is not OUVERTE and cannot be planned");
                                throw new BusinessRuleException("Commande " + ref + " is not OUVERTE and cannot be planned");
                            }
                            if (commande.getTournee() != null) {
                                System.out.println("    ERROR: Commande " + ref + " is already assigned to a tournee");
                                throw new BusinessRuleException("Commande " + ref + " is already assigned to a tournee");
                            }
                            return commande;
                        })
                        .collect(Collectors.toList());

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
                System.out.println("    Tournee " + tReq.getReference() + " validated and ready to save.");
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
                commande.setEtat(fr.uga.miage.l3.enums.EtatCommande.PLANIFIEE);
                commandeRepository.save(commande);
            }
        }

        JourneeEntity withTournees = journeeComponent.findByReference(savedJournee.getReference()).orElse(savedJournee);
        System.out.println("=== Journee " + savedJournee.getReference() + " created successfully ===");
        return JourneeMapper.toResponse(withTournees);
    }


    private void checkJourneeReferenceAndDate(String reference, LocalDate date) {
        if (reference == null || reference.isEmpty()) {
            throw new BusinessRuleException("Journee reference is required");
        }
        if (journeeComponent.findByReference(reference).isPresent()) {
            throw new BusinessRuleException("Journee reference must be unique");
        }
        if (date == null || date.isBefore(LocalDate.now())) {
            throw new BusinessRuleException("Journee date must be today or in the future");
        }
    }


    @Transactional
    public void deleteJournee(String reference) {
        JourneeEntity journee = journeeComponent.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Journee not found: " + reference));
        if (journee.getTournees() != null) {
            for (TourneeEntity tournee : journee.getTournees()) {
                if (tournee.getCommandes() != null) {
                    for (CommandeEntity commande : tournee.getCommandes()) {
                        commande.setTournee(null);
                        if (commande.getEtat() == fr.uga.miage.l3.enums.EtatCommande.PLANIFIEE) {
                            commande.setEtat(fr.uga.miage.l3.enums.EtatCommande.OUVERTE);
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
