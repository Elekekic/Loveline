package backend.Loveline_backend.service;

import backend.Loveline_backend.dto.UserLoginDTO;
import backend.Loveline_backend.exception.UnauthorizedException;
import backend.Loveline_backend.exception.UserNotFoundException;
import backend.Loveline_backend.security.AuthenticationResponse;
import backend.Loveline_backend.security.JwtTool;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import backend.Loveline_backend.entity.User;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTool jwtTool;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // Existing method to authenticate user and create token with login credentials
    public AuthenticationResponse authenticateUserAndCreateToken(UserLoginDTO userLoginDTO, HttpServletResponse response) {
        logger.info("Authenticating user: {}", userLoginDTO.getEmail());

        User user = userService.getUserByEmail(userLoginDTO.getEmail());
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }

        if (passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            String token = jwtTool.createToken(user);
            response.setHeader("Authorization", "Bearer " + token);
            return new AuthenticationResponse(token, user);
        } else {
            throw new UnauthorizedException("Invalid credentials");
        }
    }

    // This method is for handling OAuth2 user authentication
    public AuthenticationResponse authenticateOAuth2UserAndCreateToken(String email) {
        logger.info("Authenticating user: {}", userService.getUserByEmail(email));
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        String token = jwtTool.createToken(user);
        logger.info("JWT token created for user: {}", email);
        return new AuthenticationResponse(token, user);
    }
}
