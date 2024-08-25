package backend.Loveline_backend.service;

import backend.Loveline_backend.entity.User;
import backend.Loveline_backend.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class OAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JavaMailSender javaMailSender;


    private Set<Integer> usedLoverIds = new HashSet<>();

    private final Random random = new Random();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        logger.info("loadUser method called");

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        logger.info("Attributes received from OAuth2 provider: {}", attributes);

        String accessToken = userRequest.getAccessToken().getTokenValue();
        // Retrieve profile picture URL
        String profilePictureUrl = (String) attributes.get("picture");
        if (profilePictureUrl == null) {
            profilePictureUrl = (String) attributes.get("avatar_url");
        }
        if (profilePictureUrl == null) {
            profilePictureUrl = getRandomDefaultProfilePictureUrl(); // Use a random default picture URL
        }

        // Create a modifiable copy of the attributes map because if not, the Github access will give problems with the email
        Map<String, Object> modifiableAttributes = new HashMap<>(attributes);


        User user = processOAuth2User(registrationId, modifiableAttributes, profilePictureUrl, accessToken);

        String userNameAttributeName;
        if ("github".equals(registrationId)) {
            userNameAttributeName = "login";
        } else {
            userNameAttributeName = "email";
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")),
                modifiableAttributes, userNameAttributeName);
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

    private User processOAuth2User(String registrationId, Map<String, Object> attributes, String profilePictureUrl, String accessToken) {
        // Retrieve providerId safely
        String providerId = null;
        if (attributes.containsKey("sub")) {
            providerId = attributes.get("sub").toString();
        } else if (attributes.containsKey("id")) {
            providerId = attributes.get("id").toString();
        }

        if (providerId == null) {
            throw new IllegalArgumentException("Provider ID not found in attributes");
        }

        // Retrieve email safely
        String email = (String) attributes.get("email");
        if (email == null) {

            // Attempt to fetch email using an additional request to GitHub API
            email = fetchGitHubEmail(accessToken);
            logger.info("Email fetched from GitHub: {}", email);
            attributes.put("email", email);

            if (email == null) {
                throw new IllegalArgumentException("Email not found in attributes and could not be fetched");
            }
        } else {
            logger.info("Email found in attributes: {}", email);
        }

        // Retrieve username safely
        String name = (String) attributes.get("name");
        if (name == null) {
            throw new IllegalArgumentException("Username not found in attributes");
        }

        // Retrieve username safely
        String username = (String) attributes.get("login");
        if (username == null) {
            username = (String) attributes.get("name");
            if (username == null) {
                username = email;
            }
        }

        // Retrieve or create user
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isEmpty()) {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setLoverId(generateUniqueLoverId()); // Method to set a unique 5-digit loverId
            user.setUsername(username);
            user.setProvider(registrationId);
            user.setProviderId(providerId);
            user.setPfp(profilePictureUrl); // Set profile picture URL
        } else {
            user = userOptional.get();
            user.setPfp(profilePictureUrl); // Update profile picture URL
        }

        // Save the user if it's new or has changes
        userRepository.save(user);
        sendMailProfileCreated(user.getEmail(), user.getName(), user.getSurname());
        return user;
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

    // Fetch the primary email from GitHub
    private String fetchGitHubEmail(String accessToken) {
        String email = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = "https://api.github.com/user/emails";

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        // Log the response status and body
        logger.info("GitHub email fetch response status: {}", response.getStatusCode());
        logger.info("GitHub email fetch response body: {}", response.getBody());

        // Extract the email from the response
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            for (Map<String, Object> emailEntry : response.getBody()) {
                Boolean primary = (Boolean) emailEntry.get("primary");
                Boolean verified = (Boolean) emailEntry.get("verified");
                if (primary != null && primary && verified != null && verified) {
                    email = (String) emailEntry.get("email");
                    break;
                }
            }
        }

        return email;
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