package com.currency_exchange.currency_application.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtUtils {
	private final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	private final String jwtSecret = "eGfR/Q9XmVfzMHAofJ8Jc7JFQw+DiXkmI9W4docxZdqCJ7WkLwqSxhbRME5MPtwA7oOiN/HhK03QNiCQWHECiw==";
	
	private final int jwtExpirationMs = 1000 * 60 * 60 * 10; // 10 hours
	
	// Retrieve username from JWT token
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	// Extract all claims from the token
	public Claims extractAllClaims(String token) {
		try {
            return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token has expired.");
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Invalid JWT token.");
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid token signature.");
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("JWT token is unsupported.");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT claims string is empty.");
        } catch (JwtException e) {
        	throw new RuntimeException("Invalid  JWT token.");
        }
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	// Generate JWT token
	public String generateToken(String username) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, username);
	}

	// Create JWT token
	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS256, jwtSecret).compact();
	}

	// Validate token
	public Boolean validateToken(String token, String username) {
		final String extractedUsername = extractUsername(token);
		return (extractedUsername.equals(username) && !isTokenExpired(token));
	}

	private Boolean isTokenExpired(String token) {
		return extractAllClaims(token).getExpiration().before(new Date());
	}
}
