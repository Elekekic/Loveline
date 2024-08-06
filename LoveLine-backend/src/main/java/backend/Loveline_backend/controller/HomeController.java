package backend.Loveline_backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "home"; // this should correspond to a view template like home.html or home.jsp
    }

    @GetMapping("/secured")
    public String secured() {
        return "secured"; // this should correspond to a view template like secured.html or secured.jsp
    }
}
