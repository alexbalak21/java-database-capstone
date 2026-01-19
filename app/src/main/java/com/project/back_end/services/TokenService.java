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
import java.util.Optional;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenService {

	private static final Logger log = LoggerFactory.getLogger(TokenService.class);

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expiration:604800000}") // 7 days default
	private long jwtExpiration;

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

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

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
