package fr.uga.miage.l3.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RedirectController {
    // Existing mapping for "/"
    @RequestMapping("/")
    public String redirectToIndex() {
        return "forward:/index.html";
    }

    // Catch-all mapping to handle client-side routes
    @RequestMapping(value = "/{[path:[^\\.]*}")
    public String redirect() {
        return "forward:/index.html";
    }
}
