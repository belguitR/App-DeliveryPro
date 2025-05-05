package fr.uga.miage.l3.dataTypes;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeoPosition {
    private double lat;
    private double lng;

}