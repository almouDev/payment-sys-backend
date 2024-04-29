package com.almou.payment.security;

import com.almou.payment.beans.Client;
import com.almou.payment.metier.ServiceMetier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.Collection;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private ServiceMetier serviceMetier;
    private AuthenticationManager authenticationManager;

    public SecurityConfig(ServiceMetier serviceMetier, @Lazy AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.serviceMetier = serviceMetier;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return
                http
                        .authorizeHttpRequests((requests) -> requests.requestMatchers(  "/signup").permitAll()
                                .anyRequest().authenticated()
                        )
                        .addFilter(new JwtAuthenticationFilter(authenticationManager))
                        .addFilterBefore(new JwtAuthorizationToken(), UsernamePasswordAuthenticationFilter.class)
                        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .and()
                        .csrf().disable()
                        .build();
    }

    @Bean
    public UserDetailsService userDetails() {
        return username -> {
            Client appUser = serviceMetier.getClientById(username);
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            appUser.getUserRoles().forEach(appRole -> {
                authorities.add(new SimpleGrantedAuthority(appRole.getName()));
            });
            return new User(username, appUser.getPassword(), authorities);
        };
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}