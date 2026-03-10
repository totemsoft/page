package com.totemsoft.page.config;

import java.nio.charset.StandardCharsets;
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
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.header.Header;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.bind.annotation.RequestMethod;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserService userService) {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(a -> a
                .requestMatchers("/").permitAll()
                .requestMatchers(HttpMethod.GET, "/css/**", "/js/**", "/*.json", "/*.ico").permitAll()
                .requestMatchers("/*.php").denyAll()
                //.requestMatchers(RegexRequestMatcher.regexMatcher("/.+\\.php\\?.*")).denyAll()
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
                .roles(
                    RoleEnum.ADMIN_PAGE.name(),
                    //RoleEnum.ADMIN_USER.name(),
                    RoleEnum.SETUP.name())
                .build()
        ));
    }

    @Bean
    public HttpFirewall httpFirewall() {
        final var firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(false); // Disallow encoded slashes (default)
        firewall.setAllowSemicolon(false);       // Disallow semicolons (prevents some bypasses) (default)
        firewall.setAllowBackSlash(false);       // Disallow backslash (default)
        firewall.setAllowedHttpMethods(List.of(RequestMethod.GET.name(), RequestMethod.POST.name()));
        //firewall.setAllowedHeaderNames(StrictHttpFirewall.ALLOWED_HEADER_NAMES);   // default
        //firewall.setAllowedHeaderValues(StrictHttpFirewall.ALLOWED_HEADER_VALUES); // default
        firewall.setAllowedHeaderValues(header -> {
            // In the case of header values, consider parsing them as UTF-8 at verification time
            final var parsed = new String(header.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            return StrictHttpFirewall.ALLOWED_HEADER_VALUES.test(parsed);
        });
        //firewall.setAllowedParameterNames(StrictHttpFirewall.ALLOWED_PARAMETER_NAMES); // default
        firewall.setAllowedParameterValues(value -> {
            // In the case of parameter values, consider parsing them as UTF-8 at verification time (default is to allow any parameter value)
            final var parsed = new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            return StrictHttpFirewall.ALLOWED_HEADER_VALUES.test(parsed);
        });
        return firewall;
    }

}
