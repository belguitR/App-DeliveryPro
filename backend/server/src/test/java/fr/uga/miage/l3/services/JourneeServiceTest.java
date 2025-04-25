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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

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
        when(journeeComponent.findByReference("NOTFOUND")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> journeeService.getJourneeByReference("NOTFOUND"));
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

        when(journeeComponent.findByReference("J004")).thenReturn(Optional.empty());
        when(equipeLivreursRepository.findById(1)).thenReturn(Optional.of(new EquipeLivreursEntity()));
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

        assertThrows(BusinessRuleException.class, () -> journeeService.createJournee(request));
    }
}
