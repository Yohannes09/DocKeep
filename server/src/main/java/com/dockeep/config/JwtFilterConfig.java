package com.dockeep.config;


import com.authmat.events.PublicKeyRotationEvent;
import com.authmat.filter.SimpleJwtAuthenticationFilter;
import com.authmat.client.PublicKeyResolver;
import com.authmat.client.PublicKeyManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Set;

@Slf4j
@Configuration
public class JwtFilterConfig {
    @Bean
    public OncePerRequestFilter jwtAuthenticationFilter(PublicKeyResolver publicKeyResolver){
        return new SimpleJwtAuthenticationFilter(publicKeyResolver, Set.of("/ping"));
    }

    @Bean
    public PublicKeyResolver publicKeyResolver(PublicKeyManager keyManager){
        return keyManager::findKeyByKid;
    }

    @Bean
    public PublicKeyManager publicKeyManager(){
        final int maxKeysTraced = 10;
        return new PublicKeyManager(maxKeysTraced);
    }

    @KafkaListener(topics = "token-rotation-event", groupId = "authmat-service")
    public void publicKeyListener(PublicKeyRotationEvent publicKeyRotationEvent){
        publicKeyManager().addKey(publicKeyRotationEvent);
        log.info("Successfully subscribed to token-rotation-event");
    }

}
