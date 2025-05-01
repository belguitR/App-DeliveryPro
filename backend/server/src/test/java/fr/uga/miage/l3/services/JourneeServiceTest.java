package fr.uga.miage.l3.services;

import fr.uga.miage.l3.components.JourneeComponent;
import fr.uga.miage.l3.enums.EtatCommande;
import fr.uga.miage.l3.enums.EtatTournee;
import fr.uga.miage.l3.exceptions.technical.BusinessRuleException;
import fr.uga.miage.l3.exceptions.technical.ResourceNotFoundException;
import fr.uga.miage.l3.mappers.JourneeMapper;
import fr.uga.miage.l3.models.*;
import fr.uga.miage.l3.repositories.*;
import fr.uga.miage.l3.requests.JourneeRequest;
import fr.uga.miage.l3.requests.TourneeRequest;
import fr.uga.miage.l3.responses.JourneeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.antlr.v4.runtime.tree.xpath.XPath.findAll;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JourneeServiceTest {

    @Mock
    private JourneeComponent journeeComponent;
    @Mock
    private PlanificateurRepository planificateurRepository;
    @Mock
    private CamionRepository camionRepository;
    @Mock
    private TourneeRepository tourneeRepository;
    @Mock
    private EquipeLivreursRepository equipeLivreursRepository;
    @Mock
    private CommandeRepository commandeRepository;

    @InjectMocks
    private JourneeService journeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllJournees_shouldReturnList() {
        JourneeEntity entity = new JourneeEntity();
        entity.setReference("J001");
        when(journeeComponent.findAll()).thenReturn(List.of(entity));

        List<JourneeResponse> result = journeeService.getAllJournees();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReference()).isEqualTo("J001");
        verify(journeeComponent).findAll();
    }

    @Test
    void getJourneeByReference_shouldReturnJournee() {
        JourneeEntity entity = new JourneeEntity();
        entity.setReference("J002");
        when(journeeComponent.findByReference("J002")).thenReturn(Optional.of(entity));

        JourneeResponse result = journeeService.getJourneeByReference("J002");

        assertThat(result.getReference()).isEqualTo("J002");
        verify(journeeComponent).findByReference("J002");
    }

    @Test
    void getJourneeByReference_shouldThrowIfNotFound() {
        when(journeeComponent.findByReference("NON TROUVé")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> journeeService.getJourneeByReference("NON TROUVé"));
    }

    @Test
    void createJournee_shouldThrowIfReferenceIsNull() {
        JourneeRequest request = JourneeRequest.builder().reference(null).date(LocalDate.now()).build();
        assertThrows(BusinessRuleException.class, () -> journeeService.createJournee(request));
    }

    @Test
    void createJournee_shouldThrowIfReferenceIsDuplicate() {
        JourneeRequest request = JourneeRequest.builder().reference("J001").date(LocalDate.now()).build();
        when(journeeComponent.findByReference("J001")).thenReturn(Optional.of(new JourneeEntity()));
        assertThrows(BusinessRuleException.class, () -> journeeService.createJournee(request));
    }

    @Test
    void createJournee_shouldThrowIfDateIsNullOrPast() {
        JourneeRequest request = JourneeRequest.builder().reference("J003").date(null).build();
        JourneeRequest finalRequest = request;
        assertThrows(BusinessRuleException.class, () -> journeeService.createJournee(finalRequest));

        request = JourneeRequest.builder().reference("J003").date(LocalDate.now().minusDays(1)).build();
        JourneeRequest finalRequest1 = request;
        assertThrows(BusinessRuleException.class, () -> journeeService.createJournee(finalRequest1));
    }

    @Test
    void createJournee_shouldCreateJourneeWithTournees() {
        JourneeRequest request = JourneeRequest.builder()
                .reference("J004")
                .date(LocalDate.now().plusDays(1))
                .tournees(List.of(
                        TourneeRequest.builder()
                                .reference("T001")
                                .etat("EN_COURS")
                                .lettre("A")
                                .distanceAParcourir(100.0)
                                .tempsAssemblageReel(2)
                                .equipeLivreursId(1)
                                .camionCode("C001")
                                .commandeReferences(List.of("CMD1"))
                                .build()
                ))
                .build();

        // FIX: Set status to PRET
        EquipeLivreursEntity equipe = new EquipeLivreursEntity();
        equipe.setStatus(fr.uga.miage.l3.enums.StatusEquipeLivreurs.PRET);

        when(journeeComponent.findByReference("J004")).thenReturn(Optional.empty());
        when(equipeLivreursRepository.findById(1)).thenReturn(Optional.of(equipe));
        when(camionRepository.findById("C001")).thenReturn(Optional.of(new CamionEntity()));
        when(tourneeRepository.findById("T001")).thenReturn(Optional.empty());
        CommandeEntity commande = new CommandeEntity();
        commande.setEtat(fr.uga.miage.l3.enums.EtatCommande.OUVERTE);
        when(commandeRepository.findById("CMD1")).thenReturn(Optional.of(commande));
        when(journeeComponent.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(tourneeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(commandeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        JourneeResponse result = journeeService.createJournee(request);

        assertThat(result).isNotNull();
        assertThat(result.getReference()).isEqualTo("J004");
        verify(journeeComponent).save(any());
        verify(tourneeRepository).save(any());
        verify(commandeRepository).save(any());
    }



    @Test
    void createJournee_shouldThrowIfTourneeReferenceNotUnique() {
        JourneeRequest request = JourneeRequest.builder()
                .reference("J005")
                .date(LocalDate.now().plusDays(1))
                .tournees(List.of(
                        TourneeRequest.builder()
                                .reference("T002")
                                .etat("EN_COURS")
                                .lettre("A")
                                .distanceAParcourir(100.0)
                                .tempsAssemblageReel(2)
                                .equipeLivreursId(1)
                                .camionCode("C001")
                                .commandeReferences(List.of("CMD1"))
                                .build()
                ))
                .build();

        when(journeeComponent.findByReference("J005")).thenReturn(Optional.empty());
        when(tourneeRepository.findById("T002")).thenReturn(Optional.of(new TourneeEntity()));

        assertThrows(BusinessRuleException.class, () -> journeeService.createJournee(request));
    }

    @Test
    void createJournee_shouldThrowIfTourneeEtatInvalid() {
        JourneeRequest request = JourneeRequest.builder()
                .reference("J006")
                .date(LocalDate.now().plusDays(1))
                .tournees(List.of(
                        TourneeRequest.builder()
                                .reference("T003")
                                .etat("INVALID")
                                .lettre("A")
                                .distanceAParcourir(100.0)
                                .tempsAssemblageReel(2)
                                .equipeLivreursId(1)
                                .camionCode("C001")
                                .commandeReferences(List.of("CMD1"))
                                .build()
                ))
                .build();

        when(journeeComponent.findByReference("J006")).thenReturn(Optional.empty());
        when(tourneeRepository.findById("T003")).thenReturn(Optional.empty());

        assertThrows(BusinessRuleException.class, () -> journeeService.createJournee(request));
    }

    @Test
    void createJournee_shouldThrowIfTourneeHasNoCommandes() {
        JourneeRequest request = JourneeRequest.builder()
                .reference("J007")
                .date(LocalDate.now().plusDays(1))
                .tournees(List.of(
                        TourneeRequest.builder()
                                .reference("T004")
                                .etat("EN_COURS")
                                .lettre("A")
                                .distanceAParcourir(100.0)
                                .tempsAssemblageReel(2)
                                .equipeLivreursId(1)
                                .camionCode("C001")
                                .commandeReferences(Collections.emptyList())
                                .build()
                ))
                .build();

        when(journeeComponent.findByReference("J007")).thenReturn(Optional.empty());
        when(tourneeRepository.findById("T004")).thenReturn(Optional.empty());
        when(equipeLivreursRepository.findById(1)).thenReturn(Optional.of(new EquipeLivreursEntity()));
        when(camionRepository.findById("C001")).thenReturn(Optional.of(new CamionEntity()));

        assertThrows(BusinessRuleException.class, () -> journeeService.createJournee(request));

        // Vérification des appels de méthodes
        verify(journeeComponent).findByReference("J007");
        verify(tourneeRepository).findById("T004");
        verify(equipeLivreursRepository).findById(1);
        verify(camionRepository).findById("C001");
    }

    @Test
    void createJournee_shouldThrowIfCamionAlreadyAssigned() {
        // Préparation des données de test
        JourneeRequest request = JourneeRequest.builder()
                .reference("J008")
                .date(LocalDate.now().plusDays(1))
                .tournees(List.of(
                        TourneeRequest.builder()
                                .reference("T005")
                                .etat("EN_COURS")
                                .lettre("A")
                                .distanceAParcourir(100.0)
                                .tempsAssemblageReel(2)
                                .equipeLivreursId(1)
                                .camionCode("C001")
                                .commandeReferences(List.of("CMD1"))
                                .build()
                ))
                .build();

        CamionEntity camion = new CamionEntity();
        camion.setCode("C001");

        TourneeEntity existingTournee = new TourneeEntity();
        existingTournee.setCamion(camion);
        existingTournee.setJour(new JourneeEntity());
        existingTournee.getJour().setDate(request.getDate());

        when(journeeComponent.findByReference("J008")).thenReturn(Optional.empty());
        when(equipeLivreursRepository.findById(1)).thenReturn(Optional.of(new EquipeLivreursEntity()));
        when(camionRepository.findById("C001")).thenReturn(Optional.of(camion));
        when(tourneeRepository.findById("T005")).thenReturn(Optional.empty());
        when(tourneeRepository.findAll()).thenReturn(List.of(existingTournee));
        when(commandeRepository.findById("CMD1")).thenReturn(Optional.of(new CommandeEntity()));

        // Exécution du test
        assertThrows(BusinessRuleException.class, () -> journeeService.createJournee(request));

        // Vérification des appels de méthodes
        verify(journeeComponent).findByReference("J008");
        verify(camionRepository).findById("C001");
        verify(tourneeRepository).findById("T005");
        verify(tourneeRepository).findAll();
    }

    @Test
    void createJournee_shouldThrowIfEquipeAlreadyAssigned() {
        // Préparation des données de test
        JourneeRequest request = JourneeRequest.builder()
                .reference("J009")
                .date(LocalDate.now().plusDays(1))
                .tournees(List.of(
                        TourneeRequest.builder()
                                .reference("T006")
                                .etat("EN_COURS")
                                .lettre("A")
                                .distanceAParcourir(100.0)
                                .tempsAssemblageReel(2)
                                .equipeLivreursId(1)
                                .camionCode("C001")
                                .commandeReferences(List.of("CMD1"))
                                .build()
                ))
                .build();

        EquipeLivreursEntity equipe = new EquipeLivreursEntity();
        equipe.setIdEquipe(1);

        TourneeEntity existingTournee = new TourneeEntity();
        existingTournee.setEquipeLivreurs(equipe);
        existingTournee.setJour(new JourneeEntity());
        existingTournee.getJour().setDate(request.getDate());

        when(journeeComponent.findByReference("J009")).thenReturn(Optional.empty());
        when(equipeLivreursRepository.findById(1)).thenReturn(Optional.of(equipe));
        when(camionRepository.findById("C001")).thenReturn(Optional.of(new CamionEntity()));
        when(tourneeRepository.findById("T006")).thenReturn(Optional.empty());
        when(tourneeRepository.findAll()).thenReturn(List.of(existingTournee));
        when(commandeRepository.findById("CMD1")).thenReturn(Optional.of(new CommandeEntity()));

        // Exécution du test
        assertThrows(BusinessRuleException.class, () -> journeeService.createJournee(request));

        // Vérification des appels de méthodes
        verify(journeeComponent).findByReference("J009");
        verify(equipeLivreursRepository).findById(1);
        verify(camionRepository).findById("C001");
        verify(tourneeRepository).findById("T006");
        verify(tourneeRepository, times(1)).findAll();
    }


    @Test
    void createJournee_shouldThrowIfCommandeNotFound() {
        JourneeRequest request = JourneeRequest.builder()
                .reference("J010")
                .date(LocalDate.now().plusDays(1))
                .tournees(List.of(
                        TourneeRequest.builder()
                                .reference("T007")
                                .etat("EN_COURS")
                                .lettre("A")
                                .distanceAParcourir(100.0)
                                .tempsAssemblageReel(2)
                                .equipeLivreursId(1)
                                .camionCode("C001")
                                .commandeReferences(List.of("CMD_NON TROUVé"))
                                .build()
                ))
                .build();

        when(journeeComponent.findByReference("J010")).thenReturn(Optional.empty());
        when(equipeLivreursRepository.findById(1)).thenReturn(Optional.of(new EquipeLivreursEntity()));
        when(camionRepository.findById("C001")).thenReturn(Optional.of(new CamionEntity()));
        when(tourneeRepository.findById("T007")).thenReturn(Optional.empty());
        when(commandeRepository.findById("CMD_NON TROUVé")).thenReturn(Optional.empty());

        assertThrows(BusinessRuleException.class, () -> journeeService.createJournee(request));
    }

    @Test
    void createJournee_shouldThrowIfCommandeAlreadyAssigned() {
        JourneeRequest request = JourneeRequest.builder()
                .reference("J011")
                .date(LocalDate.now().plusDays(1))
                .tournees(List.of(
                        TourneeRequest.builder()
                                .reference("T008")
                                .etat("EN_COURS")
                                .lettre("A")
                                .distanceAParcourir(100.0)
                                .tempsAssemblageReel(2)
                                .equipeLivreursId(1)
                                .camionCode("C001")
                                .commandeReferences(List.of("CMD1"))
                                .build()
                ))
                .build();

        CommandeEntity commande = new CommandeEntity();
        commande.setEtat(fr.uga.miage.l3.enums.EtatCommande.OUVERTE);
        commande.setTournee(new TourneeEntity());

        when(journeeComponent.findByReference("J011")).thenReturn(Optional.empty());
        when(equipeLivreursRepository.findById(1)).thenReturn(Optional.of(new EquipeLivreursEntity()));
        when(camionRepository.findById("C001")).thenReturn(Optional.of(new CamionEntity()));
        when(tourneeRepository.findById("T008")).thenReturn(Optional.empty());
        when(commandeRepository.findById("CMD1")).thenReturn(Optional.of(commande));

        assertThrows(BusinessRuleException.class, () -> journeeService.createJournee(request));
    }

    @Test
    void createJournee_shouldThrowIfTourneeEtatIsNull() {
        // Préparation des données de test
        JourneeRequest request = JourneeRequest.builder()
                .reference("J012")
                .date(LocalDate.now().plusDays(1))
                .tournees(List.of(
                        TourneeRequest.builder()
                                .reference("T010")
                                .etat(null) // État de la tournée est null
                                .lettre("A")
                                .distanceAParcourir(100.0)
                                .tempsAssemblageReel(2)
                                .equipeLivreursId(1)
                                .camionCode("C001")
                                .commandeReferences(List.of("CMD1"))
                                .build()
                ))
                .build();

        when(journeeComponent.findByReference("J012")).thenReturn(Optional.empty());
        when(tourneeRepository.findById("T010")).thenReturn(Optional.empty());

        // Exécution du test
        assertThrows(BusinessRuleException.class, () -> journeeService.createJournee(request));

        // Vérification des appels de méthodes
        verify(journeeComponent).findByReference("J012");
        verify(tourneeRepository).findById("T010");
    }


    @Test
    void createJournee_shouldThrowIfCommandeEtatIsNotOuverte() {
        JourneeRequest request = JourneeRequest.builder()
                .reference("J013")
                .date(LocalDate.now().plusDays(1))
                .tournees(List.of(
                        TourneeRequest.builder()
                                .reference("T011")
                                .etat("EN_COURS")
                                .lettre("A")
                                .distanceAParcourir(100.0)
                                .tempsAssemblageReel(2)
                                .equipeLivreursId(1)
                                .camionCode("C001")
                                .commandeReferences(List.of("CMD1"))
                                .build()
                ))
                .build();

        CommandeEntity commande = new CommandeEntity();
        commande.setReference("CMD1");
        commande.setEtat(fr.uga.miage.l3.enums.EtatCommande.LIVREE);

        EquipeLivreursEntity equipe = new EquipeLivreursEntity();
        equipe.setStatus(fr.uga.miage.l3.enums.StatusEquipeLivreurs.PRET);

        when(journeeComponent.findByReference("J013")).thenReturn(Optional.empty());
        when(equipeLivreursRepository.findById(1)).thenReturn(Optional.of(equipe));
        when(camionRepository.findById("C001")).thenReturn(Optional.of(new CamionEntity()));
        when(tourneeRepository.findById("T011")).thenReturn(Optional.empty());
        when(commandeRepository.findById("CMD1")).thenReturn(Optional.of(commande));

        assertThrows(BusinessRuleException.class, () -> journeeService.createJournee(request));

        verify(journeeComponent).findByReference("J013");
        verify(equipeLivreursRepository).findById(1);
        verify(camionRepository).findById("C001");
        verify(tourneeRepository).findById("T011");
        verify(commandeRepository).findById("CMD1");
    }


    @Test
    void deleteJournee_shouldDeleteJourneeAndTourneesAndUpdateCommandes() {
        // Préparation des données de test
        String journeeReference = "J001";
        JourneeEntity journee = new JourneeEntity();
        journee.setReference(journeeReference);

        TourneeEntity tournee1 = new TourneeEntity();
        tournee1.setReference("T001");
        CommandeEntity commande1 = new CommandeEntity();
        commande1.setReference("CMD1");
        commande1.setEtat(EtatCommande.PLANIFIEE);
        commande1.setTournee(tournee1);
        tournee1.setCommandes(List.of(commande1));

        TourneeEntity tournee2 = new TourneeEntity();
        tournee2.setReference("T002");
        CommandeEntity commande2 = new CommandeEntity();
        commande2.setReference("CMD2");
        commande2.setEtat(EtatCommande.OUVERTE);
        commande2.setTournee(tournee2);
        tournee2.setCommandes(List.of(commande2));

        journee.setTournees(List.of(tournee1, tournee2));

        // Configuration des mocks
        when(journeeComponent.findByReference(journeeReference)).thenReturn(Optional.of(journee));
        when(commandeRepository.save(any(CommandeEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Exécution du test
        journeeService.deleteJournee(journeeReference);

        // Vérification des appels de méthodes
        verify(journeeComponent).findByReference(journeeReference);
        verify(commandeRepository, times(2)).save(any(CommandeEntity.class));
        verify(tourneeRepository).deleteById("T001");
        verify(tourneeRepository).deleteById("T002");
        verify(journeeComponent).deleteByReference(journeeReference);

        // Vérification des états des commandes
        assertThat(commande1.getTournee()).isNull();
        assertThat(commande1.getEtat()).isEqualTo(EtatCommande.OUVERTE);
        assertThat(commande2.getTournee()).isNull();
        assertThat(commande2.getEtat()).isEqualTo(EtatCommande.OUVERTE);
    }



    @Test
    void deleteJournee_shouldDeleteJourneeAndTournees() {
        JourneeEntity journee = new JourneeEntity();
        journee.setReference("J001");
        TourneeEntity tournee = new TourneeEntity();
        tournee.setReference("T001");
        journee.setTournees(List.of(tournee));

        when(journeeComponent.findByReference("J001")).thenReturn(Optional.of(journee));

        journeeService.deleteJournee("J001");

        verify(tourneeRepository).deleteById("T001");
        verify(journeeComponent).deleteByReference("J001");
    }

    @Test
    void deleteJournee_shouldThrowIfNotFound() {
        when(journeeComponent.findByReference("NON TROUVé")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> journeeService.deleteJournee("NON TROUVé"));
    }

    @Test
    void createJournee_shouldThrowIfEquipeIsUnavailable() {
        JourneeRequest request = JourneeRequest.builder()
                .reference("J016")
                .date(LocalDate.now().plusDays(1))
                .tournees(List.of(
                        TourneeRequest.builder()
                                .reference("T014")
                                .etat("EN_COURS")
                                .lettre("A")
                                .distanceAParcourir(100.0)
                                .tempsAssemblageReel(2)
                                .equipeLivreursId(1)
                                .camionCode("C001")
                                .commandeReferences(List.of("CMD1"))
                                .build()
                ))
                .build();

        EquipeLivreursEntity equipe = new EquipeLivreursEntity();
        equipe.setIdEquipe(1);
        equipe.setStatus(fr.uga.miage.l3.enums.StatusEquipeLivreurs.NON_DISPONIBLE);

        when(journeeComponent.findByReference("J016")).thenReturn(Optional.empty());
        when(equipeLivreursRepository.findById(1)).thenReturn(Optional.of(equipe));
        when(camionRepository.findById("C001")).thenReturn(Optional.of(new CamionEntity()));
        when(tourneeRepository.findById("T014")).thenReturn(Optional.empty());
        when(commandeRepository.findById("CMD1")).thenReturn(Optional.of(new CommandeEntity()));

        assertThrows(BusinessRuleException.class, () -> journeeService.createJournee(request));
    }

    @Test
    void createJournee_shouldThrowIfTourneeHasNoEquipe() {
        JourneeRequest request = JourneeRequest.builder()
                .reference("J015")
                .date(LocalDate.now().plusDays(1))
                .tournees(List.of(
                        TourneeRequest.builder()
                                .reference("T013")
                                .etat("EN_COURS")
                                .lettre("A")
                                .distanceAParcourir(100.0)
                                .tempsAssemblageReel(2)
                                .camionCode("C001")
                                .commandeReferences(List.of("CMD1"))
                                .build()
                ))
                .build();

        when(journeeComponent.findByReference("J015")).thenReturn(Optional.empty());
        when(camionRepository.findById("C001")).thenReturn(Optional.of(new CamionEntity()));
        when(tourneeRepository.findById("T013")).thenReturn(Optional.empty());
        when(commandeRepository.findById("CMD1")).thenReturn(Optional.of(new CommandeEntity()));

        assertThrows(BusinessRuleException.class, () -> journeeService.createJournee(request));
    }

    @Test
    void createJournee_shouldThrowIfTourneeHasNoCamion() {
        JourneeRequest request = JourneeRequest.builder()
                .reference("J014")
                .date(LocalDate.now().plusDays(1))
                .tournees(List.of(
                        TourneeRequest.builder()
                                .reference("T012")
                                .etat("EN_COURS")
                                .lettre("A")
                                .distanceAParcourir(100.0)
                                .tempsAssemblageReel(2)
                                .equipeLivreursId(1)
                                .commandeReferences(List.of("CMD1"))
                                .build()
                ))
                .build();

        when(journeeComponent.findByReference("J014")).thenReturn(Optional.empty());
        when(equipeLivreursRepository.findById(1)).thenReturn(Optional.of(new EquipeLivreursEntity()));
        when(tourneeRepository.findById("T012")).thenReturn(Optional.empty());
        when(commandeRepository.findById("CMD1")).thenReturn(Optional.of(new CommandeEntity()));

        assertThrows(BusinessRuleException.class, () -> journeeService.createJournee(request));
    }


}
