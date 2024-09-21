package dev.lukas.spring_security_method_security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.ExpressionAuthorizationDecision;
import org.springframework.security.authorization.event.AuthorizationEvent;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.function.Supplier;

@SpringBootApplication
@RestController
@EnableMethodSecurity
public class SpringSecurityMethodSecurityApplication {

    private static final Logger LOG = LoggerFactory.getLogger(SpringSecurityMethodSecurityApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityMethodSecurityApplication.class, args);
    }

    @GetMapping("/public")
    public String index() {
        return "Hello World!";
    }


    @GetMapping("/greet")
    public String greet() {
        return "Hello User";
    }

    @GetMapping("/greet/secure")
    @PreAuthorize("hasAuthority('READ_SECRET')")
    public String secure() {
        return "Hello Admin";
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails anonymous = User.builder()
                .username("anonymous")
                .password("{noop}password")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password("{noop}password")
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}password")
                .authorities("ROLE_USER", "ROLE_ADMIN", "READ_SECRET")
                .build();
        return new InMemoryUserDetailsManager(admin, anonymous, user);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/public").permitAll()
                .requestMatchers("/greet").hasRole("USER")
                .anyRequest().authenticated());
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public AuthorizationEventPublisher authorizationEventPublisher
            (ApplicationEventPublisher applicationEventPublisher) {
        return new MethodAuthorizationEventPublisher(applicationEventPublisher);
    }

    @EventListener
    public void onAuthorization(AuthorizationEvent event) {
        Authentication authentication = Optional.ofNullable(event.getAuthentication())
                .map(Supplier::get)
                .orElseThrow();
        switch (event.getAuthorizationDecision()) {
            case null -> throw new IllegalArgumentException("Authorization decision is null");
            case ExpressionAuthorizationDecision expressionDecision -> LOG.info("SUCCESS at method level: [{}] -> {}",
                    authentication.getName(),
                    expressionDecision.getClass().getSimpleName());
            case AuthorizationDecision authorizationDecision -> LOG.info("SUCCESS at route level: [{}] -> {}",
                    authentication.getName(),
                    authorizationDecision.getClass().getSimpleName());
        }
    }

}
