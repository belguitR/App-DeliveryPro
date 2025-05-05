package fr.uga.miage.l3.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipeLivreursResponse {
    private Integer idEquipe;
    private String status;
    private List<String> livreurIds;

}
