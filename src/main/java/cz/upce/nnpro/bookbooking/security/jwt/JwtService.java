package cz.upce.nnpro.bookbooking.security.jwt;

import cz.upce.nnpro.bookbooking.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}") private String SECRET;

    public String extractToken(String token) {
        return token.replaceFirst("Bearer ", "").trim();
    }

    public Long extractUserId(String token) {
        return Long.parseLong(extractClaim(extractToken(token), Claims::getId));
    }

    public String extractUsername(String token) {
        return extractClaim(extractToken(token), Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(extractToken(token), Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(extractToken(token));
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(extractToken(token)).getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(extractToken(token)).before(new Date());
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(extractToken(token));
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(extractToken(token)));
    }

    public String generateToken(User user) {
        return createToken(new HashMap<>(), user);
    }

    public String generateResetToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("usedFor", "PASSWORD_RESET");
        return createResetToken(extraClaims, user);
    }

    private String createToken(Map<String, Object> extraClaims, User user, Integer expiration) {
        return Jwts.builder()
                   .claims(extraClaims)
                   .id(user.getId().toString())
                   .subject(user.getUsername())
                   .issuedAt(new Date(System.currentTimeMillis()))
                   .expiration(new Date(System.currentTimeMillis() + expiration))
                   .signWith(getSignKey())
                   .compact();
    }

    private String createToken(Map<String, Object> extraClaims, User user) {
        return createToken(extraClaims, user, 1000 * 60 * 60 * 24);
    }

    private String createResetToken(Map<String, Object> extraClaims, User user) {
        return createToken(extraClaims, user, 1000 * 60 * 15);
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    }
}
