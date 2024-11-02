package backend.Loveline_backend.security;

import backend.Loveline_backend.entity.User;
import backend.Loveline_backend.service.AuthService;
import backend.Loveline_backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("OAuth2SuccessHandler called for authentication: {}", authentication.getName());
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();
        logger.info("attributes: {}", attributes);
        String email = (String) attributes.get("email");

        try {
            // Generate the token
            AuthenticationResponse authResponse = authService.authenticateOAuth2UserAndCreateToken(email);

            // Set the user in the response body
            User user = userService.getUserByEmail(email);
            authResponse.setUser(user);

            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(authResponse));

            logger.info("Authentication successful: {}", authResponse);
        } catch (Exception e) {
            logger.error("Error during OAuth2 user authentication: ", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication failed");
        }
    }
}
