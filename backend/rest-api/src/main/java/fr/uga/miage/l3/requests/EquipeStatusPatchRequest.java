package fr.uga.miage.l3.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipeStatusPatchRequest {
    private String status;
}
