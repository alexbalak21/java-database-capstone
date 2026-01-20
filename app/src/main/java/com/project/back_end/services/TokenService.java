package com.project.back_end.services;

import com.project.back_end.exceptions.InvalidTokenException;
import com.project.back_end.exceptions.TokenExpiredException;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.models.Patient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Service for managing JWT token generation and validation.
 * 
 * Configuration:
 * - JWT Secret: Loaded from application.properties via ${jwt.secret} property.
 *   Must be at least 32 characters for HMAC-SHA algorithms.
 * - JWT Expiration: Loaded from ${jwt.expiration} property (default: 7 days in milliseconds).
 * 
 * The secret is used to sign tokens during generation and to verify token authenticity
 * during validation. All tokens are signed using HMAC-SHA with the configured secret.
 */
@Component
public class TokenService {

	private static final Logger log = LoggerFactory.getLogger(TokenService.class);

	@Value("${jwt.secret}")
	private String jwtSecret; // JWT secret key loaded from application properties, used for signing and verifying tokens

	@Value("${jwt.expiration:604800000}") // 7 days default
	private long jwtExpiration; // Token expiration time in milliseconds

	private final AdminRepository adminRepository;
	private final DoctorRepository doctorRepository;
	private final PatientRepository patientRepository;

	public TokenService(AdminRepository adminRepository,
					  DoctorRepository doctorRepository,
					  PatientRepository patientRepository) {
		this.adminRepository = adminRepository;
		this.doctorRepository = doctorRepository;
		this.patientRepository = patientRepository;
	}

	/**
	 * Derives the HMAC-SHA signing key from the configured JWT secret.
	 * 
	 * This method converts the jwtSecret string into a SecretKey that can be used
	 * by the JWT library to sign tokens during generation and verify signatures
	 * during validation. The secret must be at least 32 characters to meet HMAC-SHA
	 * security requirements.
	 * 
	 * @return SecretKey derived from the configured JWT secret
	 */
	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Generates a JWT token for the given identifier.
	 * 
	 * The token is signed using HMAC-SHA with the configured secret (via getSigningKey()).
	 * The token includes:
	 * - Subject: The provided identifier (typically email or username)
	 * - Issued At: Current timestamp
	 * - Expiration: Current time + configured expiration duration
	 * 
	 * @param identifier The user's email or username to encode in the token
	 * @return A signed JWT token string
	 */
	public String generateToken(String identifier) {
		return Jwts.builder()
				.subject(identifier)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + jwtExpiration))
				.signWith(getSigningKey())
				.compact();
	}

	public String extractIdentifier(String token) {
		try {
			Claims claims = Jwts.parser()
							.verifyWith(getSigningKey())
							.build()
							.parseSignedClaims(token)
							.getPayload();
			return claims.getSubject();
		} catch (ExpiredJwtException e) {
			throw new TokenExpiredException("Token has expired", e);
		} catch (Exception e) {
			throw new InvalidTokenException("Invalid token: " + e.getMessage(), e);
		}
	}

	public String extractEmail(String token) {
		return extractIdentifier(token);
	}

	public boolean validateToken(String token, String role) {
		try {
			String identifier = extractIdentifier(token);

			switch (role.toLowerCase()) {
				case "admin":
					return adminRepository.findByUsername(identifier) != null;
				case "doctor":
					return doctorRepository.findByEmail(identifier).isPresent();
				case "patient":
					return patientRepository.findByEmail(identifier).isPresent();
				default:
					return false;
			}
		} catch (TokenExpiredException | InvalidTokenException e) {
			log.warn("Token validation failed: {}", e.getMessage());
			return false;
		}
	}

	public Long extractDoctorId(String token) {
		try {
			String email = extractIdentifier(token);
			return doctorRepository.findByEmail(email)
					.map(doc -> doc.getId())
					.orElse(null);
		} catch (Exception e) {
			log.warn("Failed to extract doctor id: {}", e.getMessage());
			return null;
		}
	}

	public Long extractPatientId(String token) {
		try {
			String email = extractIdentifier(token);
			return patientRepository.findByEmail(email)
					.map(Patient::getId)
					.orElse(null);
		} catch (Exception e) {
			log.warn("Failed to extract patient id: {}", e.getMessage());
			return null;
		}
	}

	public long getTokenRemainingTime(String token) {
		try {
			Claims claims = Jwts.parser()
							.verifyWith(getSigningKey())
							.build()
							.parseSignedClaims(token)
							.getPayload();
			long remainingTime = claims.getExpiration().getTime() - System.currentTimeMillis();
			return Math.max(remainingTime, -1);
		} catch (Exception e) {
			log.warn("Failed to compute token remaining time: {}", e.getMessage());
			return -1;
		}
	}
}
