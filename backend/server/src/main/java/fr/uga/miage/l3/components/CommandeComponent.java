package fr.uga.miage.l3.components;

import fr.uga.miage.l3.models.CommandeEntity;
import fr.uga.miage.l3.repositories.CommandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommandeComponent {
    private final CommandeRepository commandeRepository;

    public List<CommandeEntity> findAll() {
        return commandeRepository.findAll();
    }

    public Optional<CommandeEntity> findByReference(String reference) {
        return commandeRepository.findById(reference);
    }

    public CommandeEntity save(CommandeEntity entity) {
        return commandeRepository.save(entity);
    }
}
