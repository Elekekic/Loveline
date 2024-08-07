package backend.Loveline_backend.controller;

import backend.Loveline_backend.entity.User;
import backend.Loveline_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public Optional<User> getUserProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.getUserById(user.getId());
    }


//    @PutMapping("/profile")
//    public String updateUserProfile(@RequestBody @Validated UserDTO userDTO, BindingResult bindingResult, Authentication authentication) {
//        if (bindingResult.hasErrors()) {
//            throw new BadRequestException(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).reduce("", (s, s2) -> s + s2));
//        }
//        User user = (User) authentication.getPrincipal();
//        return userService.updateUserProfile(user.getId(), userDTO);
//    }

}
