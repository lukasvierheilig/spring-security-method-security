package dev.lukas.spring_security_method_security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/greet")
public class SpringSecurityMethodSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityMethodSecurityApplication.class, args);
	}


	@GetMapping()
	public String greet() {
		return "Hello World";
	}

	@GetMapping("/secure")
	public String secure() {
		return "Stop there this is private!";
	}


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
		return http.build();
	}

}
