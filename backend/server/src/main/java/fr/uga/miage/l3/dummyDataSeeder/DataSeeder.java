package fr.uga.miage.l3.dummyDataSeeder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.uga.miage.l3.models.AgendaEntity;
import fr.uga.miage.l3.models.AdresseEntity;
import fr.uga.miage.l3.models.ClientEntity;
import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repository.AdresseRepository;
import fr.uga.miage.l3.repository.ClientRepository;
import fr.uga.miage.l3.repository.CommandeRepository;
import fr.uga.miage.l3.repository.EquipeLivreurRepository;
import fr.uga.miage.l3.repository.LivreurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DataSeeder implements CommandLineRunner {

    private final LivreurRepository livreurRepository;
    private final EquipeLivreurRepository equipeLivreurRepository;
    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;
    private final AdresseRepository adresseRepository;

    public DataSeeder(LivreurRepository livreurRepository,
                      EquipeLivreurRepository equipeLivreurRepository,
                      CommandeRepository commandeRepository,
                      ClientRepository clientRepository,
                      AdresseRepository adresseRepository) {
        this.livreurRepository = livreurRepository;
        this.equipeLivreurRepository = equipeLivreurRepository;
        this.commandeRepository = commandeRepository;
        this.clientRepository = clientRepository;
        this.adresseRepository = adresseRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // --- Seed LivreurEntities ---
        if (livreurRepository.count() == 0) {
            ClassPathResource resourceLivreurs = new ClassPathResource("dummy_data_livreurs.json");
            try (InputStream inputStream = resourceLivreurs.getInputStream()) {
                List<LivreurEntity> livreurs = mapper.readValue(inputStream, new TypeReference<List<LivreurEntity>>() {});
                livreurRepository.saveAll(livreurs);
                System.out.println("Livreurs dummy data seeded!");
            }
        }

        // --- Seed EquipeLivreurEntities ---
        if (equipeLivreurRepository.count() == 0) {
            ClassPathResource resourceEquipes = new ClassPathResource("dummy_data_equipes_livreurs.json");
            try (InputStream inputStream = resourceEquipes.getInputStream()) {
                List<EquipeLivreurDummyDTO> equipesDummy = mapper.readValue(inputStream, new TypeReference<List<EquipeLivreurDummyDTO>>() {});
                List<EquipeLivreurEntity> equipesToSave = new ArrayList<>();
                for (EquipeLivreurDummyDTO dto : equipesDummy) {
                    EquipeLivreurEntity equipe = new EquipeLivreurEntity();
                    equipe.setNumEquipe(dto.getNumEquipe());
                    // Convert status string to enum
                    equipe.setStatus(Enum.valueOf(fr.uga.miage.l3.enums.StatusEquipeLivreur.class, dto.getStatus()));
                    // Create AgendaEntity from DTO data (adjust as needed)
                    equipe.setAgenda(new AgendaEntity(
                            dto.getAgenda().getIdAgenda(),
                            new HashSet<>((List<fr.uga.miage.l3.models.CreneauEntity>) dto.getAgenda().getCreneaux())
                    ));
                    // Look up each LivreurEntity by ID
                    List<LivreurEntity> equipeLivreurs = new ArrayList<>();
                    for (String id : dto.getLivreursIds()) {
                        livreurRepository.findById(id).ifPresent(equipeLivreurs::add);
                    }
                    equipe.setLivreurs(equipeLivreurs);
                    equipesToSave.add(equipe);
                }
                equipeLivreurRepository.saveAll(equipesToSave);
                System.out.println("EquipesLivreurs dummy data seeded!");
            }
        }

        // --- Seed CommandeEntities ---
        // --- Seed CommandeEntities ---
        if (commandeRepository.count() == 0) {
            // Configure the ObjectMapper to use ISO 8601 date format
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
            ClassPathResource resourceCommandes = new ClassPathResource("dummy_data_commandes.json");
            try (InputStream inputStream = resourceCommandes.getInputStream()) {
                List<CommandeEntity> commandes = mapper.readValue(inputStream, new TypeReference<List<CommandeEntity>>() {});

                // Extract distinct clients from commandes
                List<ClientEntity> clientsToSave = commandes.stream()
                        .filter(cmd -> cmd.getClient() != null)
                        .map(CommandeEntity::getClient)
                        .distinct()  // Ensure proper equals() and hashCode() on ClientEntity
                        .collect(Collectors.toList());

                // For each client, check if its address exists; if not, persist the address first.
                for (ClientEntity client : clientsToSave) {
                    if (client.getAdresse() != null) {
                        // Assuming the identifier is the postal code (an Integer)
                        Integer adresseId = client.getAdresse().getCodePostal();
                        if (!adresseRepository.existsById(adresseId)) {
                            adresseRepository.save(client.getAdresse());
                            System.out.println("Persisted Adresse with codePostal " + adresseId);
                        }
                    }
                }

                // Save all clients so that they become managed
                List<ClientEntity> savedClients = clientRepository.saveAll(clientsToSave);
                clientRepository.flush();

                // Build a lookup map by client code with a merge function for duplicates
                Map<String, ClientEntity> clientMap = savedClients.stream()
                        .collect(Collectors.toMap(ClientEntity::getCode, Function.identity(), (existing, replacement) -> existing));

                // Update each commande with the managed client instance
                commandes.forEach(cmd -> {
                    if (cmd.getClient() != null) {
                        ClientEntity managedClient = clientMap.get(cmd.getClient().getCode());
                        if (managedClient != null) {
                            cmd.setClient(managedClient);
                        } else {
                            System.out.println("Warning: Managed client for code " + cmd.getClient().getCode() + " not found.");
                        }
                    }
                });

                commandeRepository.saveAll(commandes);
                System.out.println("Commandes dummy data seeded!");
            }
        }

    }
}
