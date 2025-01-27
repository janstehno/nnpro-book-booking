package cz.upce.nnpro.bookbooking.configuration;

import cz.upce.nnpro.bookbooking.components.AuthorityLoggerFilter;
import cz.upce.nnpro.bookbooking.entity.enums.RoleE;
import cz.upce.nnpro.bookbooking.security.JwtFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration {

    private final JwtFilter jwtFilter;

    private final AuthorityLoggerFilter authorityLoggerFilter;

    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.cors(Customizer.withDefaults())
                   .csrf(AbstractHttpConfigurer::disable)
                   .authenticationProvider(authenticationProvider)
                   .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                   .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                   .addFilterBefore(authorityLoggerFilter, UsernamePasswordAuthenticationFilter.class)
                   .authorizeHttpRequests(a -> a.requestMatchers("/auth/**", "/books/**")
                                                .permitAll()
                                                .requestMatchers("/admin/**")
                                                .hasAuthority(RoleE.ADMIN.name())
                                                .anyRequest()
                                                .authenticated())
                   .build();
    }
}