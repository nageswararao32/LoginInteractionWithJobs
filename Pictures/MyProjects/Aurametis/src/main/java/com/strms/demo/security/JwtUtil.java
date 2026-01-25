package com.strms.demo.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.strms.demo.Entites.AdminUser;
import com.strms.demo.ExceptionHandling.InvalidRefreshTokenException;
import com.strms.demo.Repositories.AdminUserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    @Autowired
    private AdminUserRepository adminUserRepository;
    //newly added on 24-01-26 at 10PM
    public Long getTokenExpiration() {
        return expiration;
    }

    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException e) {
            log.error("Failed to extract email from token: {}", e.getMessage());
            return null;
        }
    }
    

    // ✅ Get signing key for token operations (JJWT 0.12.3)
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ✅ Generate access token (short-lived) - for UserDetails
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority())
                .orElse("ROLE_USER"));
        return createToken(claims, userDetails.getUsername(), expiration);
    }

    // ✅ Generate access token (short-lived) - for AdminUser
    public String generateToken(AdminUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("name", user.getName());
        return createToken(claims, user.getEmail(), expiration);
    }

    // ✅ Generate refresh token (long-lived) - for UserDetails
    public String generateRefreshToken(UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails.getUsername(), refreshExpiration);
    }

    // ✅ Generate refresh token (long-lived) - for AdminUser
    public String generateRefreshToken(AdminUser user) {
        return createToken(new HashMap<>(), user.getEmail(), refreshExpiration);
    }

    // ✅ Create token with claims, subject, and validity period
    private String createToken(Map<String, Object> claims, String subject, Long validityPeriod) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityPeriod);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(validity)
                .signWith(getSigningKey()) // ✅ JJWT 0.12.3 - no need for SignatureAlgorithm
                .compact();
    }

    // ✅ Extract username/email from token (JJWT 0.12.3 syntax)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ✅ Extract expiration date from token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ✅ Extract any claim from token using resolver function
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ✅ Extract all claims from token (CORRECT JJWT 0.12.3 SYNTAX)
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey()) // ✅ Use verifyWith() instead of setSigningKey()
                    .build()
                    .parseSignedClaims(token) // ✅ Use parseSignedClaims() instead of parseClaimsJws()
                    .getPayload(); // ✅ Use getPayload() instead of getBody()
        } catch (JwtException e) {
            log.error("JWT parsing error: {}", e.getMessage());
            throw e;
        }
    }

    // ✅ Check if token is expired
    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (JwtException e) {
            log.error("Token expiration check failed: {}", e.getMessage());
            return true;
        }
    }

    // ✅ Validate token with UserDetails
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // ✅ Validate token (without UserDetails)
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // ✅ VALIDATE REFRESH TOKEN (JJWT 0.12.3)
    public AdminUser validateRefreshToken(String refreshToken) {
        try {
            // Parse and validate refresh token using JJWT 0.12.3 API
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();

            // Extract email (subject)
            String email = claims.getSubject();
            if (email == null || email.isEmpty()) {
                throw new JwtException("Invalid refresh token: missing subject");
            }

            // Check if token is expired
            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                throw new JwtException("Refresh token expired");
            }

            // Fetch user from database
            AdminUser user = adminUserRepository.findByEmail(email)
                    .orElseThrow(() -> new JwtException("User not found for this refresh token"));

            log.info("✅ Refresh token validated successfully for user: {}", email);
            return user;

        } catch (JwtException e) {
            log.error("❌ Refresh token validation failed: {}", e.getMessage());
            throw new InvalidRefreshTokenException("Invalid or expired refresh token: " + e.getMessage());
        }
    }

    // ✅ Get role from token
    public String getRoleFromToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("role", String.class);
        } catch (JwtException e) {
            log.error("Failed to extract role: {}", e.getMessage());
            return null;
        }
    }

    // ✅ Get name from token
    public String getNameFromToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("name", String.class);
        } catch (JwtException e) {
            log.error("Failed to extract name: {}", e.getMessage());
            return null;
        }
    }
}