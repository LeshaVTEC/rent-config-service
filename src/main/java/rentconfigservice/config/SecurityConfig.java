package rentconfigservice.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import rentconfigservice.controller.filter.JwtFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter filter) throws Exception  {
        http = http.cors().and().csrf().disable();

        http = http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();

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


        http.authorizeHttpRequests(requests -> requests
                .requestMatchers(POST,"/users/registration").permitAll()
                .requestMatchers(POST,"/users/login").permitAll()
                .requestMatchers(GET,"/users/verification").permitAll()
                .requestMatchers(GET,"/users/me").authenticated()
                .requestMatchers(POST,"/users/send-password-restore-link").permitAll()
                .requestMatchers(POST,"/users/update-password").permitAll()
                .requestMatchers(GET,"/users").hasAnyRole("ADMIN")
                .requestMatchers(GET,"/users/{id}").hasAnyRole("ADMIN")
                .requestMatchers(POST,"/users").hasAnyRole("ADMIN")
                .requestMatchers(PUT,"/users/{id}").hasAnyRole("ADMIN")
                .anyRequest().authenticated()
        );

        http.addFilterBefore(
                filter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}