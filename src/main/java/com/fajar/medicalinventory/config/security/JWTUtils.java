package com.fajar.medicalinventory.config.security;
 
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JWTUtils {
	
	@Value("${app.security.jwtSecret}")
	private String jwtSecret; 
	@Value("${app.security.jwtExpirationMs}")
	private int jwtExpirationMs;

	public String generateJwtToken(Authentication authentication) {
		try { 
			UserDetailDomain userPrincipal = (UserDetailDomain) authentication.getPrincipal();

			return Jwts.builder()
					.setSubject((userPrincipal.getUsername()))
					.setIssuedAt(new Date())
					.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
					.signWith(SignatureAlgorithm.HS512, jwtSecret)
					.compact();
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (io.jsonwebtoken.SignatureException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
		} catch (Exception e) {
			
		}

		return false;
	}
}
