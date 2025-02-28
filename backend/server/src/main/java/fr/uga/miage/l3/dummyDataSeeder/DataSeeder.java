package fr.uga.miage.l3.dummyDataSeeder;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.uga.miage.l3.models.AgendaEntity;
import fr.uga.miage.l3.models.EquipeLivreurEntity;
import fr.uga.miage.l3.models.LivreurEntity;
import fr.uga.miage.l3.repository.EquipeLivreurRepository;
import fr.uga.miage.l3.repository.LivreurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


@Component
public class DataSeeder implements CommandLineRunner {

    private final LivreurRepository livreurRepository;
    private final EquipeLivreurRepository equipeLivreurRepository;

    public DataSeeder(LivreurRepository livreurRepository, EquipeLivreurRepository equipeLivreurRepository) {
        this.livreurRepository = livreurRepository;
        this.equipeLivreurRepository = equipeLivreurRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed LivreurEntities if needed (assuming you already have dummy_data_livreurs.json loaded)
        if (livreurRepository.count() == 0) {
            ObjectMapper mapper = new ObjectMapper();
            ClassPathResource resourceLivreurs = new ClassPathResource("dummy_data_livreurs.json");
            try (InputStream inputStream = resourceLivreurs.getInputStream()) {
                List<LivreurEntity> livreurs = mapper.readValue(inputStream, new TypeReference<List<LivreurEntity>>() {});
                livreurRepository.saveAll(livreurs);
                System.out.println("Livreurs dummy data seeded!");
            }
        }

        // Seed EquipeLivreurEntities if needed
        if (equipeLivreurRepository.count() == 0) {
            ObjectMapper mapper = new ObjectMapper();
            ClassPathResource resourceEquipes = new ClassPathResource("dummy_data_equipes_livreurs.json");
            try (InputStream inputStream = resourceEquipes.getInputStream()) {
                List<EquipeLivreurDummyDTO> equipesDummy = mapper.readValue(inputStream, new TypeReference<List<EquipeLivreurDummyDTO>>() {});
                List<EquipeLivreurEntity> equipesToSave = new ArrayList<>();
                for (EquipeLivreurDummyDTO dto : equipesDummy) {
                    EquipeLivreurEntity equipe = new EquipeLivreurEntity();
                    equipe.setNumEquipe(dto.getNumEquipe());
                    // Assuming you have an enum conversion for status:
                    equipe.setStatus(Enum.valueOf(fr.uga.miage.l3.enums.StatusEquipeLivreur.class, dto.getStatus()));
                    // For agenda, create an AgendaEntity from dto.getAgenda() (you may need to write a helper method)
                    // For simplicity, let's assume AgendaEntity has a constructor taking idAgenda:
                    // (Make sure AgendaEntity has cascade persist in EquipeLivreurEntity if needed)
                    equipe.setAgenda(new AgendaEntity(
                            dto.getAgenda().getIdAgenda(),
                            new HashSet<fr.uga.miage.l3.models.CreneauEntity>(
                                    (List<fr.uga.miage.l3.models.CreneauEntity>) dto.getAgenda().getCreneaux()
                            )
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
    }
}