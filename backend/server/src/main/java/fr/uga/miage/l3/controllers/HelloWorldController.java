package fr.uga.miage.l3.controllers;

import fr.uga.miage.l3.endpoints.HelloWorldEndpoints;
import fr.uga.miage.l3.services.HelloWorldService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloWorldController implements HelloWorldEndpoints {
    private final HelloWorldService helloWordService;

    @Override
    public String helloWord() {
        return "Hello World";
    }

    @Override
    public String create(String name) {
        return helloWordService.create(name);
    }

    @Override
    public String element(String name) {
        return helloWordService.find(name);
    }
}
