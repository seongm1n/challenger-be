package academy.challenger.auth;

import academy.challenger.auth.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        
        CustomUserDetails principal = new CustomUserDetails(
                annotation.userId(), 
                annotation.email(), 
                "password");
        
        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal, "password", principal.getAuthorities());
        
        context.setAuthentication(auth);
        return context;
    }
} 