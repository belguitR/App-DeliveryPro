package fr.uga.miage.l3.components;

import fr.uga.miage.l3.models.CamionEntity;
import fr.uga.miage.l3.models.ClientEntity;
import fr.uga.miage.l3.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClientComponent {

    private ClientRepository clientRepository;

    public List<ClientEntity> findAll() {
        return clientRepository.findAll();
    }

    public Optional<ClientEntity> findByCode(String code) {
        return clientRepository.findById(code);
    }

    public ClientEntity save(ClientEntity entity) {
        return clientRepository.save(entity);
    }
}
