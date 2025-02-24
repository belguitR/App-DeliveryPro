package fr.uga.miage.l3.endpoints;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("api/hello-world")
public interface HelloWorldEndpoints {

    @GetMapping("/")
    String helloWord();

    @PostMapping("/create")
    String create(@RequestParam String name);

    @GetMapping("/element")
    String element(@RequestParam String name);

}
