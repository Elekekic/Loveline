package backend.Loveline_backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // HOME PAGE
    @GetMapping("/home")
    public String home() {
        return "home";}


    // SECURED PAGE (only for testing the security for now)
    @GetMapping("/secured")
    public String secured() {
        return "secured";
    }
}
