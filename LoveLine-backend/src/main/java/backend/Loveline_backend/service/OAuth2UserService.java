package backend.Loveline_backend.service;

import backend.Loveline_backend.entity.User;
import backend.Loveline_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

        // Determine the attribute key for username based on provider
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
        return user;
    }


    // GENERATE UNIQUE 5-DIGIT LOVER ID
    private int generateUniqueLoverId() {
        int loverId;
        do {
            loverId = ThreadLocalRandom.current().nextInt(10000, 100000); // Generate number between 10000 and 99999
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
}