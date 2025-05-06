package com.fdp.datareport.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void shouldGenerateAndValidateTokenSuccessfully() {
        String token = jwtUtil.generateToken("test123", "EDITOR");

        assertThat(token).isNotBlank();

        Claims claims = jwtUtil.validateToken(token);
        assertThat(claims.getSubject()).isEqualTo("test123");
        assertThat(claims.get("role")).isEqualTo("EDITOR");
        assertThat(claims.getExpiration()).isNotNull();
    }

    @Test
    void shouldThrowExceptionForInvalidToken() {
        String invalidToken = "invalid.token.value";

        assertThatThrownBy(() -> jwtUtil.validateToken(invalidToken))
                .isInstanceOfAny(SignatureException.class, MalformedJwtException.class, IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionForExpiredToken() {
        JwtUtil shortExpiryUtil = new JwtUtil() {
            @Override
            public String generateToken(String brid, String role) {
                return Jwts.builder()
                        .setSubject(brid)
                        .claim("role", role)
                        .setIssuedAt(new Date(System.currentTimeMillis() - 60000))
                        .setExpiration(new Date(System.currentTimeMillis() - 1000)) // already expired
                        .signWith(Keys.hmacShaKeyFor("fdpdatareportforfdpdatareportfor12".getBytes()))
                        .compact();
            }
        };

        String expiredToken = shortExpiryUtil.generateToken("user", "VIEWER");

        assertThatThrownBy(() -> shortExpiryUtil.validateToken(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
