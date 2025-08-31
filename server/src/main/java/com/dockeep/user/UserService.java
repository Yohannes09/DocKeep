package com.dockeep.user;

import com.authmat.events.NewUserEvent;
import com.dockeep.user.model.User;
import com.dockeep.user.repository.CachedUserRepository;
import com.dockeep.user.util.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserMapper mapper;
    private final CachedUserRepository cachedUserRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    @KafkaListener(topics = "user-created-events", groupId = "authmat-service")
    public void createUser(NewUserEvent newUserEvent){
        log.info("Subscribed to user_created event");
        User user = User.builder()
                .domainIdentifier(newUserEvent.id().toString())
                .username(newUserEvent.username())
                .email(newUserEvent.email())
                .firstName("N/A")
                .lastName("N/A")
                .status("")
                .build();

        cachedUserRepository.save(user);
    }

    public void serviceAcknowledgement(NewUserEvent newUserEvent){
        kafkaTemplate.send("user-created-events", newUserEvent);
    }

}
