package fr.uga.miage.l3.services;

import fr.uga.miage.l3.component.HelloWorldComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HelloWorldService {
    private final HelloWorldComponent helloWordComponent;

    public String find(String name){
        return helloWordComponent.find(name);
    }
    public String create(String name){
        return helloWordComponent.create(name);
    }



}
