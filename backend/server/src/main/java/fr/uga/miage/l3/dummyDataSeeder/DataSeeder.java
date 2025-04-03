package fr.uga.miage.l3.dummyDataSeeder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

                // Persist livreurs if they are not already in the database
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
                    equipe.setStatus(Enum.valueOf(fr.uga.miage.l3.enums.StatusEquipeLivreur.class, dto.getStatus()));

                    // Fetch livreurs using the ids
                    List<LivreurEntity> equipeLivreurs = new ArrayList<>();
                    for (String id : dto.getLivreursIds()) {
                        livreurRepository.findById(id).ifPresent(livreur -> {
                            livreur.setEquipeLivreur(equipe); // Set the equipeLivreur reference
                            equipeLivreurs.add(livreur);
                        });
                    }

                    // Ensure livreurs are not empty before associating them
                    if (!equipeLivreurs.isEmpty()) {
                        equipe.setLivreurs(equipeLivreurs);
                        equipesToSave.add(equipe);
                        livreurRepository.saveAll(equipeLivreurs); // Save the livreurs with updated equipe association
                    } else {
                        System.out.println("Warning: No livreurs found for EquipeLivreur with numEquipe: " + dto.getNumEquipe());
                    }
                }
                equipeLivreurRepository.saveAll(equipesToSave);
                System.out.println("EquipesLivreurs dummy data seeded!");
            }
        }


        if (commandeRepository.count() == 0) {
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
            ClassPathResource resourceCommandes = new ClassPathResource("dummy_data_commandes.json");
            try (InputStream inputStream = resourceCommandes.getInputStream()) {
                List<CommandeEntity> commandes = mapper.readValue(inputStream, new TypeReference<List<CommandeEntity>>() {});

                // Get distinct clients from commandes
                List<ClientEntity> clientsToSave = commandes.stream()
                        .filter(cmd -> cmd.getClient() != null)
                        .map(CommandeEntity::getClient)
                        .distinct()
                        .collect(Collectors.toList());

                // Persist each client and its Adresse
                for (ClientEntity client : clientsToSave) {
                    if (client.getAdresse() != null) {
                        adresseRepository.save(client.getAdresse());
                        System.out.println("Persisted Adresse for client code: " + client.getCode());
                    }
                }

                // Save clients and update commandes with managed client
                List<ClientEntity> savedClients = clientRepository.saveAll(clientsToSave);
                clientRepository.flush();
                Map<String, ClientEntity> clientMap = savedClients.stream()
                        .collect(Collectors.toMap(ClientEntity::getCode, Function.identity(), (existing, replacement) -> existing));
                commandes.forEach(cmd -> {
                    if (cmd.getClient() != null) {
                        ClientEntity managedClient = clientMap.get(cmd.getClient().getCode());
                        if (managedClient != null) {
                            cmd.setClient(managedClient);
                        }
                    }
                });
                commandeRepository.saveAll(commandes);
                System.out.println("Commandes dummy data seeded!");
            }
        }

    }
}
