package backend.Loveline_backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "home";}


    @GetMapping("/secured")
    public String secured() {
        return "secured";
    }
}
