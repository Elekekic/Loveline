package backend.Loveline_backend;

import backend.Loveline_backend.dto.UserDTO;
import backend.Loveline_backend.entity.User;
import backend.Loveline_backend.repository.UserRepository;
import backend.Loveline_backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserServiceTest {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Test
//    public void testSaveUser() {
//        UserDTO userDTO = new UserDTO();
//        userDTO.setUsername("testuser");
//        userDTO.setName("Test");
//        userDTO.setSurname("User");
//        userDTO.setEmail("testuser@example.com");
//        userDTO.setPassword("password");
//
//        String result = userService.saveUser(userDTO);
//        assertTrue(result.contains("created"));
//
//        Optional<User> savedUser = userRepository.findByEmail(userDTO.getEmail());
//        assertTrue(savedUser.isPresent());
//        assertEquals("testuser", savedUser.get().getUsername());
//    }
}
