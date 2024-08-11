package backend.Loveline_backend.controller;

import backend.Loveline_backend.dto.UserDTO;
import backend.Loveline_backend.entity.User;
import backend.Loveline_backend.exception.BadRequestException;
import backend.Loveline_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // GET USER PROFILE METHOD
    @GetMapping("/profile")
    public Optional<User> getUserProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.getUserById(user.getId());
    }

    // UPDATE USER METHOD
    @PutMapping("/profile")
    public String updateUserProfile(@RequestBody @Validated UserDTO userDTO, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(bindingResult.getAllErrors().stream()
                    .map(objectError -> objectError.getDefaultMessage()).reduce("", (s, s2) -> s + s2));
        }
        User user = (User) authentication.getPrincipal();
        return userService.updateUser(user.getId(), userDTO);
    }

    // DELETE USER METHOD
    @DeleteMapping("/profile")
    public String deleteUserProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.deleteUser(user.getId());
    }

}
