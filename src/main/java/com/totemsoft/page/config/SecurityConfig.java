package com.totemsoft.page.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(a -> a
                .requestMatchers("/").permitAll()
                .requestMatchers(HttpMethod.GET, "/*.js", "/*.json", "/*.ico").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(c -> {
                c.permitAll();
                c.loginProcessingUrl("/login");
                c.loginPage("/login.html");
                c.defaultSuccessUrl("/page", true); // redirect
                c.failureUrl("/login?error"); // redirect
            })
            .logout(c -> {
                c.permitAll();
                c.logoutUrl("/logout");
                c.logoutSuccessUrl("/"); // redirect
                c.deleteCookies("JSESSIONID", "authenticated");
                c.invalidateHttpSession(true);
            })
            .exceptionHandling(e -> e
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(List.of(
            User.builder()
                .username("user")
                .password(passwordEncoder.encode("Passw0rd"))
                .roles("USER")
                .build(),
            User.builder()
                .username("admin")
                .password(passwordEncoder.encode("Passw0rd"))
                .roles("ADMIN")
                .build()
        ));
    }

}
