package com.oooldgreen.financemanager.config;

import com.oooldgreen.financemanager.entity.User;
import com.oooldgreen.financemanager.service.JwtService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.security.remember-me.key:default_secret_key}")
    private String rememberMeKey;

    public SecurityConfig(UserDetailsService userDetailsService, JwtAuthFilter jwtAuthFilter, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Configure the security filter chain:
     * 1. Disable CSRF
     * 2. Enable CORS
     * 3. Allow all Preflight OPTION request
     * 4. Permit login, signup without token
     * 5. All other requests must be authenticated
     * 6. remember me for 7 days
     * **/
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/v3/api-docs/**",    // 解析后的 JSON 数据
                            "/swagger-ui/**",     // Swagger UI 界面
                            "/swagger-ui.html",   // 入口 HTML
                            "/webjars/**",        // UI 所需的静态资源
                            "/swagger-resources/**" // 资源配置
                    ).permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/api/users", "/api/users/signup", "/api/users/signin").permitAll()
                    .anyRequest().authenticated()
            )
            // Set custom authentication provider
            .authenticationProvider(authenticationProvider())
            // Add JWT filter before Spring Security's default filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .rememberMe(rm -> rm
                        .rememberMeServices(rememberMeServices())
                        .authenticationSuccessHandler(((request, response, authentication) -> {
                            Object principal = authentication.getPrincipal();
                            if(principal instanceof User) {
                                Long userId = ((User) principal).getId();
                                String newToken = jwtService.generateToken(userId);
                                response.addHeader("Authorization", "Bearer " + newToken);
                                response.addHeader("Access-Control-Expose-Headers", "Authorization");
                            }
                        }))
                );

        return http.build();
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices(rememberMeKey, userDetailsService);
        rememberMeServices.setParameter("remember");
        rememberMeServices.setTokenValiditySeconds(86400 * 7);
        return rememberMeServices;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
