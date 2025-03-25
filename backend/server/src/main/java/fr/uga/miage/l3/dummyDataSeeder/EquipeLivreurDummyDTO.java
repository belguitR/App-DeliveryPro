package fr.uga.miage.l3.dummyDataSeeder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Dummy data for EquipeLivreur")
public class EquipeLivreurDummyDTO {
    private int numEquipe;
    private String status;
    private List<String> livreursIds;
}