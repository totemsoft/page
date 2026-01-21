package com.totemsoft.page.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.header.Header;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
            //.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(a -> a
                .requestMatchers("/").permitAll()
                .requestMatchers(HttpMethod.GET, "/css/**", "/js/**", "/*.json", "/*.ico").permitAll()
                .requestMatchers("/*.php").denyAll()
                .anyRequest().authenticated()
            )
            .formLogin(c -> {
                c.permitAll();
                c.loginProcessingUrl("/login");
                c.loginPage("/login.html");
                c.defaultSuccessUrl("/home", true); // redirect
                c.failureUrl("/login?error"); // redirect
            })
            .logout(c -> {
                c.permitAll();
                c.logoutUrl("/logout");
                c.logoutSuccessUrl("/"); // redirect
                c.deleteCookies("JSESSIONID", "authenticated");
                c.invalidateHttpSession(true);
            })
            //.headers(AbstractHttpConfigurer::disable)
            .headers(c -> c.defaultsDisabled().addHeaderWriter(new StaticHeadersWriter(createHeaders())))
            .exceptionHandling(e -> e
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .build();
    }

    private List<Header> createHeaders() {
        // CacheControl.noCache().cachePrivate().mustRevalidate().getHeaderValue()
        return List.of(
            new Header(HttpHeaders.CACHE_CONTROL, "private,no-cache,must-revalidate,max-age=86400")
        );
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
