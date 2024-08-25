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
            sendMailProfileCreated(user.getEmail(), user.getName(), user.getSurname());
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


    // SEND EMAIL "PROFILE CREATED" METHOD
    public void sendMailProfileCreated(String email, String name, String surname) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject(String.format("Loveline is happy to have you, %s \uD83D\uDC8C", name));

            String htmlMsg = String.format(
                    "<html>" +
                            "<body style='font-family: Poppins, Helvetica, sans-serif;'>" +
                            "<div style='max-width: 600px; margin: 0 auto;'>" +
                            "<img src='cid:bannerGif' alt='Welcome Banner' style='width: 100%%; height: auto;'>" +
                            "<h1>Dear %s %s,</h1>" +
                            "<h2 style='margin-bottom: 0;'>Your Loveline account has been successfully created! \uD83D\uDC98 <br>We can't wait to see you on our platform! \uD83D\uDC40</h2>" +
                            "<p>You can now access the website using the credentials you provided during registration. </p>" +
                            "<br>" +
                            "<p>Don't forget to update your info if needed! Have fun personalizing your own experience with LoveLine, especially with your partner! \uD83D\uDC65</p>" +
                            "<br>" +
                            "<br>" +

                            "<div style='text-align: center; margin-top: 20px;'>" +
                            "<p style='font-size: 18px; font-weight: bold;'>Invite your lover to join LoveLine and start creating memorable moments together! \uD83E\uDD70</p>" +
                            "<p style='margin-bottom: 0;'>Click and share this link with them:</p>" +
                            "<a href='http://localhost:4200/auth/register' target='_blank' " +
                            "style='display: inline-block; text-align: center; font-family: Poppins, Helvetica, sans-serif; background-color: transparent; border: 2px solid black; padding: 10px 35px; border-radius: 20px; cursor: pointer; text-decoration: none; color: black; margin: 20px auto;'>" +
                            "Join LoveLine now</a>" +
                            "</div>" +

                            "<br>" +
                            "<br>" +
                            "<p style='margin: 0;'>Best regards,</p>" +
                            "<p style='margin: 0; margin-bottom: 20px; font-weight: bold;'>The Loveline Team</p>" +
                            "<img src='cid:logoImage' alt='Loveline Logo' style='width: 150px; height: auto;'>" +
                            "<p style='font-size: 12px; margin: 0; margin-top: 20px;'>© 2024 LoveLine</p>" +
                            "<p style='font-size: 12px; margin: 0;'>Trieste, Italy</p>" +
                            "</div>" +
                            "</body>" +
                            "</html>",
                    name, surname
            );

            helper.setText(htmlMsg, true);

            // Add inline images for the email
            ClassPathResource logoResource = new ClassPathResource("static/logo.png");
            helper.addInline("logoImage", logoResource);
            ClassPathResource bannerResource = new ClassPathResource("static/banner.gif");
            helper.addInline("bannerGif", bannerResource);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("Error sending email to {}: {}", email, e.getMessage());
        }
    }
}
