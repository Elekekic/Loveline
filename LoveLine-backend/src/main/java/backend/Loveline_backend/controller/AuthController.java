package backend.Loveline_backend.controller;

import backend.Loveline_backend.dto.UserDTO;
import backend.Loveline_backend.dto.UserLoginDTO;
import backend.Loveline_backend.entity.User;
import backend.Loveline_backend.exception.BadRequestException;
import backend.Loveline_backend.security.AuthenticationResponse;
import backend.Loveline_backend.service.AuthService;
import backend.Loveline_backend.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "register";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("userLoginDTO", new UserLoginDTO());
        return "login";
    }

    // REGISTRATION METHOD
    @PostMapping("/register")
    public String register(@ModelAttribute @Validated UserDTO userDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .reduce("", (s, s2) -> s + s2);
            model.addAttribute("error", errorMessages);
            return "register";
        }
        try {
            userService.saveUser(userDTO);
            return "redirect:/auth/login";
        } catch (Exception e) {
            System.out.println("Exception occurred during user registration: " + e.getMessage());
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    @PostMapping("/login_form")
    public ResponseEntity<AuthenticationResponse> login(@ModelAttribute @Validated UserLoginDTO userLoginDTO, BindingResult bindingResult, Model model, HttpServletResponse response) {
        System.out.println("This is a test message.");
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .reduce("", (s, s2) -> s + s2);
            model.addAttribute("error", errorMessages);
            return ResponseEntity.badRequest().body(new AuthenticationResponse(null, null));
        }

        try {
            AuthenticationResponse authResponse = authService.authenticateUserAndCreateToken(userLoginDTO, response);
            return ResponseEntity.ok(authResponse); // Return the token in the response body
        } catch (Exception e) {
            System.out.println("Exception occurred during user logging in: " + e.getMessage());
            model.addAttribute("error", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthenticationResponse(null, null));
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }
}
