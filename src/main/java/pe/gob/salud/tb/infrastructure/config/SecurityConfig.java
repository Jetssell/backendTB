package pe.gob.salud.tb.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pe.gob.salud.tb.middleware.JwtAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource(
      @Value("${cors.allowed-origins:http://localhost:5173,http://localhost:3000,https://6pjm529l-5173.brs.devtunnels.ms}") String allowedOriginsCsv) {

    List<String> allowed = Arrays.stream(allowedOriginsCsv.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .toList();

    CorsConfiguration cfg = new CorsConfiguration();
    cfg.setAllowedOrigins(allowed);
    cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
    cfg.setAllowedHeaders(List.of("Content-Type","Authorization","X-Requested-With"));
    cfg.setAllowCredentials(true); // necesario para cookies cross-site
    cfg.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }

  // ===== 1) API bajo /api/** (JWT, stateless) =====
  @Bean
  @Order(1)
  public SecurityFilterChain apiChain(HttpSecurity http, JwtAuthenticationFilter jwt) throws Exception {
    http
      .securityMatcher("/api/**")
      .csrf(csrf -> csrf.disable())
      .cors(Customizer.withDefaults())
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
          // Preflight CORS
          .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

          // Público: login/logout
          .requestMatchers("/api/auth/login", "/api/auth/logout").permitAll()

          // Público: catálogos (filtros/autocomplete)
          .requestMatchers("/api/catalogos/**").permitAll()

          // (Opcional) público: doc/health
          .requestMatchers("/api/actuator/**","/api/v3/api-docs/**","/api/swagger-ui/**","/api/swagger-ui.html").permitAll()

          // El resto protegido
          .anyRequest().authenticated()
      );

    http.addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  // ===== 2) App estática / SPA =====
  @Bean
  @Order(2)
  public SecurityFilterChain appChain(HttpSecurity http) throws Exception {
    http
      .securityMatcher("/**")
      .csrf(csrf -> csrf.disable())
      .cors(Customizer.withDefaults())
      .authorizeHttpRequests(auth -> auth
          .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
          .requestMatchers("/", "/index.html", "/assets/**",
                           "/favicon.ico", "/vite.svg",
                           "/robots.txt", "/manifest.json").permitAll()
          .requestMatchers("/actuator/**","/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()
          .anyRequest().permitAll()
      );
    return http.build();
  }
}
