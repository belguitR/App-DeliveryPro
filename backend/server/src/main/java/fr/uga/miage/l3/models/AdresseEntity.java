package fr.uga.miage.l3.models;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "adresse")
public class AdresseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String adressePostal;

    private String complementAdresse;
    @Column(nullable = false)
    private String ville;
    @Column(nullable = false)
    private Integer codePostal;
    private double latitude;
    private double longitude;


}