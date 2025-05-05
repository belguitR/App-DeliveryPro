package fr.uga.miage.l3.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RedirectController {

    @RequestMapping("/")
    public String redirectToIndex() {
        return "forward:/index.html";
    }

    @RequestMapping({"/planificateur", "/planificateur/"})
    public String forwardPlanificateurRoot() {
        return "forward:/planificateur/index.html";
    }

    @RequestMapping({"/livreur", "/livreur/"})
    public String forwardLivreurRoot() {
        return "forward:/livreur/index.html";
    }




    @RequestMapping(value = "/planificateur/{path:[^\\.]*}")
    public String forwardPlanificateur() {
        return "forward:/planificateur/index.html";
    }

    @RequestMapping(value = "/livreur/{path:[^\\.]*}")
    public String forwardLivreur() {
        return "forward:/livreur/index.html";
    }

}
