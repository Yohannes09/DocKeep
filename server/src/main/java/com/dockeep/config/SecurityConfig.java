package com.dockeep.config;

import com.authmat.filter.SimpleJwtAuthenticationFilter;
import com.authmat.tool.exception.UserNotFoundException;
import com.dockeep.user.repository.CachedUserRepository;
import com.dockeep.user.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final CachedUserRepository userRepository;
    private final SimpleJwtAuthenticationFilter simpleJwtAuthenticationFilter;
    private final UserMapper userMapper;

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return usernameOrEmail ->
                Optional.of(userRepository.findUserEntityByUsernameOrEmail(usernameOrEmail))
                        .map(userMapper::entityToPrincipal)
                        .orElseThrow(() -> new UserNotFoundException("Could not find user " + usernameOrEmail));
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/ping").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(simpleJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
