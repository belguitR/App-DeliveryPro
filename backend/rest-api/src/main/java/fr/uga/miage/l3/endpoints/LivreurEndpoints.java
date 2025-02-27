package fr.uga.miage.l3.endpoints;

import fr.uga.miage.l3.dto.LivreurDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Livreur", description = "Endpoints pour gérer les livreurs")
@RequestMapping("/api/livreur")
public interface LivreurEndpoints {

    /**
     * Récupère la liste de tous les livreurs
     */
    @GetMapping
    List<LivreurDTO> getAllLivreurs();

    /**
     * Crée un nouveau livreur
     */
    @PostMapping
    LivreurDTO createLivreur(@RequestBody LivreurDTO livreur);

    /**
     * Met à jour un livreur existant
     * @param idLivreur identifiant du livreur
     */
    @PutMapping("/{idLivreur}")
    LivreurDTO updateLivreur(@PathVariable String idLivreur, @RequestBody LivreurDTO livreur);
}

