package itmo.labs.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Set;

@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Secure key generation
    private final long jwtExpirationInMs = 3600000; // 1 hour

    /**
     * Generate JWT Token
     *
     * @param username the username
     * @param roles    the roles of the user
     * @return the JWT token
     */
    public String generateToken(String username, Set<String> roles) {
        String roleString = String.join(",", roles);
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roleString)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(key)
                .compact();
    }

    /**
     * Validate JWT Token
     *
     * @param authToken the JWT token
     * @return true if valid
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Log the exception - for production, consider logging the specific exception
            return false;
        }
    }

    /**
     * Get Username from JWT Token
     *
     * @param token the JWT token
     * @return the username
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
        return claims.getSubject();
    }

    /**
     * Get Roles from JWT Token
     *
     * @param token the JWT token
     * @return the set of roles
     */
    public Set<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
        String roles = claims.get("roles", String.class);
        return Set.of(roles.split(","));
    }
}