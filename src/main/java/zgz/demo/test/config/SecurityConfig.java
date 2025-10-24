package zgz.demo.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.server.authorization.AuthorizationContext;

import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.cors(Customizer.withDefaults());

        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                ).authenticated()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/products/**").access(this::swaggerRefererOnly)
                .requestMatchers(HttpMethod.POST, "/products/**").access(this::swaggerRefererOnly)
                .requestMatchers(HttpMethod.PUT, "/products/**").access(this::swaggerRefererOnly)
                .requestMatchers(HttpMethod.PATCH, "/products/**").access(this::swaggerRefererOnly)
                .requestMatchers(HttpMethod.DELETE, "/products/**").access(this::swaggerRefererOnly)

                .anyRequest().access(this::swaggerRefererOnly)
        );

        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }

    private AuthorizationDecision swaggerRefererOnly(Supplier<Authentication> authentication,
                                                     RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();
        String referer = request.getHeader("Referer");
        boolean fromSwagger = referer != null && referer.contains("/swagger-ui");
        return new AuthorizationDecision(fromSwagger);
    }
}
