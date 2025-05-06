package com.fdp.datareport.config;

import com.fdp.datareport.filters.JwtAuthFilter;
import com.fdp.datareport.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SecurityConfigTest {

    @Configuration
    static class MockBeans {

        @Bean
        public JwtAuthFilter jwtAuthFilter() {
            return Mockito.mock(JwtAuthFilter.class);
        }

        @Bean
        public JwtUtil jwtUtil() {
            return Mockito.mock(JwtUtil.class);
        }

        @Bean
        public SecurityConfig securityConfig(JwtAuthFilter jwtAuthFilter) {
            return new SecurityConfig(jwtAuthFilter);
        }
    }

    @Test
    void contextLoads() {
        // Just verify context loads
        assertThat(true).isTrue();
    }

    @Test
    void shouldCreatePasswordEncoder() {
        JwtAuthFilter mockFilter = Mockito.mock(JwtAuthFilter.class);
        SecurityConfig config = new SecurityConfig(mockFilter);
        PasswordEncoder encoder = config.passwordEncoder();
        assertThat(encoder).isNotNull();
        assertThat(encoder.encode("test")).isNotBlank();
    }

    @Test
    void shouldCreateCorsConfigurationSource() {
        JwtAuthFilter mockFilter = Mockito.mock(JwtAuthFilter.class);
        SecurityConfig config = new SecurityConfig(mockFilter);

        CorsConfigurationSource corsSource = config.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");

        CorsConfiguration corsConfig = corsSource.getCorsConfiguration(request);
        assertThat(corsConfig).isNotNull();
        assertThat(corsConfig.getAllowedOrigins()).contains("http://localhost:4200");
        assertThat(corsConfig.getAllowedMethods()).contains("GET", "POST", "PUT", "DELETE");
    }

    @Test
    void shouldCreateSecurityFilterChain() throws Exception {
        JwtAuthFilter mockFilter = Mockito.mock(JwtAuthFilter.class);
        SecurityConfig config = new SecurityConfig(mockFilter);

        HttpSecurity http = Mockito.mock(HttpSecurity.class, Mockito.RETURNS_DEEP_STUBS);
        // Just ensure method call doesn't throw
        config.filterChain(http);
        assertThat(true).isTrue();
    }
}
