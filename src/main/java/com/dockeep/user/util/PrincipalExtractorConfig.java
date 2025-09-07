package com.dockeep.user.util;

import com.dockeep.user.model.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

@Configuration
public class PrincipalExtractorConfig {
    @Bean
    PrincipalExtractor principalExtractor(){
        return () -> {
            Object authentication = SecurityContextHolder.getContext().getAuthentication();

            if(authentication instanceof UserPrincipal principal) return principal;

            return UserPrincipal.builder()
                    .username("")
                    .authorities(Set.of(""))
                    .build();
        };
    }

}
