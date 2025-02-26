package fr.uga.miage.l3.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

public class AdresseEntity {
    @Id
    @Column(nullable = false)
    private String adressePostal;

    private String complementAdresse;
    @Column(nullable = false)
    private String ville;
    @Column(nullable = false)
    private Integer codePostal;
    private Integer latitude;
    private Integer longitude;


}