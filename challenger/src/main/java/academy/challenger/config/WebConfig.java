package academy.challenger.config;

import academy.challenger.auth.security.JwtAuthenticationInterceptor;
import academy.challenger.auth.security.LoginUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired(required = false)
    private LoginUserArgumentResolver loginUserArgumentResolver;
    
    @Autowired(required = false)
    private JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        if (loginUserArgumentResolver != null) {
            resolvers.add(loginUserArgumentResolver);
        }
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (jwtAuthenticationInterceptor != null) {
            registry.addInterceptor(jwtAuthenticationInterceptor)
                    .addPathPatterns("/api/**")
                    .excludePathPatterns("/api/auth/**");
        }
    }
}
