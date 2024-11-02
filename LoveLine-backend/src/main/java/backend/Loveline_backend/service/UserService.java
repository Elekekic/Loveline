package backend.Loveline_backend.service;

import backend.Loveline_backend.dto.UserDTO;
import backend.Loveline_backend.entity.Event;
import backend.Loveline_backend.entity.User;
import backend.Loveline_backend.exception.BadRequestException;
import backend.Loveline_backend.exception.EventNotFoundException;
import backend.Loveline_backend.exception.UserNotFoundException;
import backend.Loveline_backend.repository.UserRepository;
import com.cloudinary.Cloudinary;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Set<Integer> usedLoverIds = new HashSet<>();

    // QUERY - FIND USER BY USERNAME
    public User getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new UserNotFoundException("User with username: " + username + " not found");
        }
    }

    // QUERY - FIND USER BY EMAIL
    public User getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new UserNotFoundException("User with email: " + email + " not found");
        }
    }


    // FIND USER BY LOVER ID METHOD
    public Optional<User> getUserByLoverId(int loverId) {
        Optional<User> userOptional = userRepository.findByLoverId(loverId);
        if (userOptional.isPresent()) {
            return userRepository.findByLoverId(loverId);
        } else {
            throw new UserNotFoundException("User with id: " + loverId + " not found");
        }
    }

    // FIND USER BY ID METHOD
    public Optional<User> getUserById(int id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userRepository.findById(id);
        } else {
            throw new UserNotFoundException("User with id: " + id + " not found");
        }
    }

    // SAVE USER METHOD
    public String saveUser(UserDTO userDTO) {
        Optional<User> userOptional = userRepository.findByEmail(userDTO.getEmail());

        if (userOptional.isPresent()) {
            throw new BadRequestException("This email is already associated with another account");
        } else {
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setName(userDTO.getName());
            user.setSurname(userDTO.getSurname());
            user.setEmail(userDTO.getEmail());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setLoverId(generateUniqueLoverId()); // setting a unique 5-digit loverId

            if (userDTO.getPfp() == null || userDTO.getPfp().isEmpty()) {
                user.setPfp(getRandomDefaultProfilePictureUrl());
            } else {
                user.setPfp(userDTO.getPfp());
            }
            userRepository.save(user);
            return "User with ID: " + user.getId() + " created";
        }
    }

    // UPDATE USER METHOD
    public String updateUser(int id, UserDTO userDTO) {

        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User with ID: " + id + " not found");
        }

        User user = userOptional.get();

        // checking if the email is being updated and if it already exists for another user
        if (!user.getEmail().equals(userDTO.getEmail())) {
            Optional<User> existingUserWithEmail = userRepository.findByEmail(userDTO.getEmail());
            if (existingUserWithEmail.isPresent()) {
                throw new BadRequestException("This email is already associated with another account");
            }
            user.setEmail(userDTO.getEmail());
        }


        user.setUsername(userDTO.getUsername());
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setLoverId(user.getLoverId()); // lover id will never change

        // Update password if provided
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        // Update profile picture URL if provided, otherwise keep the existing one
        if (userDTO.getPfp() != null) {
            user.setPfp(userDTO.getPfp());
        }

        userRepository.save(user);

        return "The user: " + user.getUsername() + " has been updated";
    }


    // DELETE USER METHOD
    public String deleteUser(int id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {

            userOptional.get().setMyLover(null);
            userOptional.get().setTimeline(null);
            userOptional.get().setEvents(null);

            userRepository.deleteById(id);
            return "The user: " + userOptional.get().getUsername() + " has been deleted";
        } else throw new UserNotFoundException("User with id: " + id + " not found");
    }


    // GENERATE UNIQUE 5-DIGIT LOVER ID
    private int generateUniqueLoverId() {
        int loverId;
        do {
            loverId = ThreadLocalRandom.current().nextInt(10000, 100000);
        } while (usedLoverIds.contains(loverId));
        usedLoverIds.add(loverId);
        return loverId;
    }

    private String getRandomDefaultProfilePictureUrl() {
        List<String> defaultProfilePictureUrls = List.of(
                "https://api.dicebear.com/9.x/glass/svg?seed=Luna",
                "https://api.dicebear.com/9.x/glass/svg?seed=Bear",
                "https://api.dicebear.com/9.x/glass/svg?seed=Cookie",
                "https://api.dicebear.com/9.x/glass/svg?seed=Bailey",
                "https://api.dicebear.com/9.x/glass/svg?seed=Sasha",
                "https://api.dicebear.com/9.x/glass/svg?seed=Lily",
                "https://api.dicebear.com/9.x/glass/svg?seed=Chester",
                "https://api.dicebear.com/9.x/glass/svg?seed=Lucy",
                "https://api.dicebear.com/9.x/glass/svg?seed=Sam",
                "https://api.dicebear.com/9.x/glass/svg?seed=Snowball",
                "https://api.dicebear.com/9.x/glass/svg?seed=Sassy",
                "https://api.dicebear.com/9.x/glass/svg?seed=Tigger",
                "https://api.dicebear.com/9.x/glass/svg?seed=Max",
                "https://api.dicebear.com/9.x/glass/svg?seed=Sheba",
                "https://api.dicebear.com/9.x/glass/svg?seed=Cuddles",
                "https://api.dicebear.com/9.x/glass/svg?seed=Zoe",
                "https://api.dicebear.com/9.x/glass/svg?seed=Patches",
                "https://api.dicebear.com/9.x/glass/svg?seed=Missy",
                "https://api.dicebear.com/9.x/glass/svg?seed=Spooky",
                "https://api.dicebear.com/9.x/glass/svg?seed=Shadow"
        );
        int index = ThreadLocalRandom.current().nextInt(defaultProfilePictureUrls.size());
        return defaultProfilePictureUrls.get(index);
    }

}
