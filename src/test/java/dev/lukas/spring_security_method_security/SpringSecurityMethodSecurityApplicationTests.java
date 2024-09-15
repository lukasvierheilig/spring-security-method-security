package dev.lukas.spring_security_method_security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
class SpringSecurityMethodSecurityApplicationTests {

    @Autowired
    SpringSecurityMethodSecurityApplication application;

    @Test
    void contextLoads() {
        // Smoke test
    }

    @Test
    void whenAnonymousUserRequestsIndexThenServeIndex() {
        Assertions.assertThat(application.index()).isEqualTo("Hello World!");
    }

    @Test
    @WithMockUser()
    void whenUserRequestsGreetingThenGreetUser() {
        Assertions.assertThat(application.greet()).isEqualTo("Hello User");
    }

    @Test
    void whenNonUserRequestsGreetingThenRefuse() {
        Assertions.assertThatException().isThrownBy(() -> application.greet());
    }

    @Test
    void whenUserRequestsSecureGreetingThenRefuse() {
        Assertions.assertThatException().isThrownBy(() -> application.secure());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenAdminRequestsSecureGreetingThenGreetAdmin() {
        Assertions.assertThat(application.secure()).isEqualTo("Hello Admin");
    }


}
