package com.totemsoft.page.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
import org.springframework.security.web.header.Header;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    public static final String IS_AUTHENTICATED = "isAuthenticated()";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ROLE_ADMIN_PAGE = "ADMIN_PAGE";
    public static final String ROLE_ADMIN_USER = "ADMIN_USER";
    public static final String ROLE_SETUP = "SETUP";
    public static final String HAS_ROLE_ADMIN_PAGE = "hasRole('ADMIN_PAGE')";
    public static final String HAS_ROLE_ADMIN_USER = "hasRole('ADMIN_USER')";
    public static final String HAS_ROLE_SETUP = "hasRole('SETUP')";
    public static final String HAS_AUTHORITY_OIDC_USER = "hasAuthority('OIDC_USER')";
    public static final String PERMIT_ALL = "permitAll()";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserService userService) {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(a -> a
                .requestMatchers("/").permitAll()
                .requestMatchers(HttpMethod.GET, "/css/**", "/js/**", "/*.json", "/*.ico").permitAll()
                .requestMatchers("/*.php").denyAll()
                .anyRequest().authenticated()
            )
            .formLogin(c -> c
                .loginProcessingUrl("/login")
                .loginPage("/login.html")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error")
            )
            .logout(c -> c
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID", "authenticated")
                .invalidateHttpSession(true)
            )
            .oauth2Login(c -> c
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo.oidcUserService(userService))
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error")
            )
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
                .username("user@company.com")
                .password(passwordEncoder.encode("Passw0rd"))
                .roles()
                .build(),
            User.builder()
                .username("admin@company.com")
                .password(passwordEncoder.encode("Passw0rd"))
                //.roles(ROLE_ADMIN_PAGE, ROLE_SETUP)
                .roles(ROLE_ADMIN_PAGE, ROLE_SETUP, ROLE_ADMIN_USER)
                .build()
        ));
    }

}
