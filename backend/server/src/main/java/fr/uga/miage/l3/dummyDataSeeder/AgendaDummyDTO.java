package fr.uga.miage.l3.dummyDataSeeder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "Dummy data for Agenda")
public class AgendaDummyDTO {
    private String idAgenda;
    private List<fr.uga.miage.l3.models.CreneauEntity> creneaux = new ArrayList<>();

}

