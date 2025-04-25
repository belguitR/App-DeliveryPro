package fr.uga.miage.l3.endpoints;

import fr.uga.miage.l3.responses.CamionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/camions")
@CrossOrigin(origins = "http://localhost:4200")
public interface CamionEndpoint {

    @Operation(summary = "Get all camions")
    @ApiResponse(responseCode = "200", description = "List of camions")
    @GetMapping
    ResponseEntity<List<CamionResponse>> getAllCamions();
}