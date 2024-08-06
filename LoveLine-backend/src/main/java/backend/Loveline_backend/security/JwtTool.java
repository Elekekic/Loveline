package backend.Loveline_backend.security;

import backend.Loveline_backend.entity.User;
import backend.Loveline_backend.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTool {

    @Value("${jwt.secret}")
    private String secret;

    private String userToken;

    private synchronized void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public synchronized String getUserToken() {
        return userToken;
    }

    public String createToken(User user) {
        String newToken = Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setSubject(String.valueOf(user.getId()))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();

        setUserToken(newToken);

        return newToken;
    }

    public void verifyToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception e) {
            throw new UnauthorizedException("Failed to authorize. Please login again to access this resource.");
        }
    }

    public int getIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Integer.parseInt(claims.getSubject());
    }
}
