package rentconfigservice.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import rentconfigservice.controller.filter.JwtFilter;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(SecurityProperties.IGNORED_ORDER)
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(POST, "/users/registration")
                .requestMatchers(POST, "/users/login")
                .requestMatchers(GET, "/users/verification")
                .requestMatchers(POST, "/users/send-password-restore-link")
                .requestMatchers(POST, "/users/update-password")
                .requestMatchers(OPTIONS, "/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter filter) throws Exception  {
        // Enable CORS and disable CSRF
        http = http.cors().and().csrf().disable();

        // Set session management to stateless
        http = http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();

        // Set unauthorized requests exception handler
        http = http
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> {
                            response.setStatus(
                                    HttpServletResponse.SC_UNAUTHORIZED
                            );
                        }
                )
                .accessDeniedHandler((request, response, ex) -> {
                    response.setStatus(
                            HttpServletResponse.SC_FORBIDDEN
                    );
                })
                .and();

        // Set permissions on endpoints
        http.authorizeHttpRequests(requests -> requests
                // Our public endpoints
//                .requestMatchers( "/realty/api/**").permitAll()
//                //Следующие два пример делают одно и тоже
                .requestMatchers(GET,"/users").hasAnyRole("ADMIN") //Обрати внимание что тут нет префикса ROLE_
                .requestMatchers(GET,"/users/{id}").hasAnyRole("ADMIN") //Обрати внимание что тут нет префикса ROLE_
                .requestMatchers(POST,"/users").hasAnyRole("ADMIN") //Обрати внимание что тут нет префикса ROLE_
                .requestMatchers(PUT,"/users/{id}").hasAnyRole("ADMIN") //Обрати внимание что тут нет префикса ROLE_
//                .requestMatchers(GET,"/users").hasAnyAuthority("ROLE_ADMIN") //А тут есть
                .requestMatchers(GET,"/users/**").authenticated()
//                // Our private endpoints
//                .anyRequest().authenticated()
        );

        // Add JWT token filter
        http.addFilterBefore(
                filter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}