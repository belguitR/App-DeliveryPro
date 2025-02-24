package fr.uga.miage.l3.component;

import fr.uga.miage.l3.models.HelloWorldEntity;
import fr.uga.miage.l3.repository.HelloWorldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelloWorldComponent {
    private final HelloWorldRepository helloWorldRepository;

    public String create(String name){
        return helloWorldRepository.save(HelloWorldEntity.builder().name(name).build()).getName();
    }

    public String find(String name){
        return helloWorldRepository.findByName(name).getName();
    }
}
