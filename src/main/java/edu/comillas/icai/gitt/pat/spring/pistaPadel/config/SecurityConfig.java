package edu.comillas.icai.gitt.pat.spring.pistaPadel.config;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .userDetailsService(customUserDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/pistaPadel/auth/register").permitAll()
                        .requestMatchers("/pistaPadel/auth/login").permitAll()
                        .requestMatchers("/pistaPadel/health").permitAll()
                        .requestMatchers("/pistaPadel/availability/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/pistaPadel/courts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/pistaPadel/courts/*").permitAll()

                        .requestMatchers("/pistaPadel/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/pistaPadel/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/pistaPadel/courts").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/pistaPadel/courts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/pistaPadel/courts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/pistaPadel/courts/**").hasRole("ADMIN")

                        .requestMatchers("/pistaPadel/auth/me").authenticated()
                        .requestMatchers("/pistaPadel/auth/logout").authenticated()
                        .requestMatchers("/pistaPadel/reservations/**").authenticated()
                        .requestMatchers("/pistaPadel/users/**").authenticated()

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}