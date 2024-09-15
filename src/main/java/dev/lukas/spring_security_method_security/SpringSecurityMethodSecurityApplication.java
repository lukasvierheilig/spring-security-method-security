package dev.lukas.spring_security_method_security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableMethodSecurity
public class SpringSecurityMethodSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityMethodSecurityApplication.class, args);
    }

    @GetMapping("/public")
    public String index() {
        return "Hello World!";
    }


    @GetMapping("/greet")
    @PreAuthorize("hasAnyRole('USER')")
    public String greet() {
        return "Hello User";
    }

    @GetMapping("/greet/secure")
    @PreAuthorize("hasRole('ADMIN')")
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
                .password("{noop}password").
                roles("USER", "ADMIN").build();
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

}
