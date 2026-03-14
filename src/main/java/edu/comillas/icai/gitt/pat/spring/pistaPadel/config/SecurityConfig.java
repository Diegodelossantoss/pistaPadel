package edu.comillas.icai.gitt.pat.spring.pistaPadel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                    //publicos
                    .requestMatchers("/pistaPadel/auth/register").permitAll()
                    .requestMatchers("/pistaPadel/auth/login").permitAll()
                    .requestMatchers("/pistaPadel/health").permitAll()
                    .requestMatchers("/pistaPadel/availability/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/pistaPadel/courts").permitAll()
                    .requestMatchers(HttpMethod.GET, "/pistaPadel/courts/*").permitAll()

                    //autenticado
                    .requestMatchers("/pistaPadel/auth/me").authenticated()
                    .requestMatchers("/pistaPadel/auth/logout").authenticated()
                    .requestMatchers("/pistaPadel/reservas/**").authenticated()
                    .requestMatchers("/pistaPadel/users/*").authenticated()

                    //solo admin
                    .requestMatchers("/pistaPadel/admin/**").hasRole("ADMIN")
                    .requestMatchers("/pistaPadel/users").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/pistaPadel/courts").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/pistaPadel/courts/*").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/pistaPadel/courts/*").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/pistaPadel/courts/*").hasRole("ADMIN")

                    .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
