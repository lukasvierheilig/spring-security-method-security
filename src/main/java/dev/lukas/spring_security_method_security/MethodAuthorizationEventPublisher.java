package dev.lukas.spring_security_method_security;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.authorization.event.AuthorizationGrantedEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class MethodAuthorizationEventPublisher implements AuthorizationEventPublisher {

    private final ApplicationEventPublisher publisher;
    private final AuthorizationEventPublisher delegate;

    public MethodAuthorizationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
        this.delegate = new SpringAuthorizationEventPublisher(publisher);
    }

    @Override
    public <T> void publishAuthorizationEvent(Supplier<Authentication> authentication,
                                              T object,
                                              AuthorizationDecision decision) {
        if (decision == null) {
            return;
        }

        if (!decision.isGranted()) {
            delegate.publishAuthorizationEvent(authentication, object, decision);
        }


        if (shouldThisEventBePublished(Optional.ofNullable(authentication).map(Supplier::get).orElseThrow())) {
            AuthorizationGrantedEvent<?> granted = new AuthorizationGrantedEvent<>(
                    authentication, object, decision);
            this.publisher.publishEvent(granted);
        }

    }

    private boolean shouldThisEventBePublished(Authentication authentication) {
        assert authentication != null;
        return authentication.getAuthorities().stream()
                .filter(Objects::nonNull)
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }
}
