package com.dockeep.user.repository;

import com.authmat.tool.exception.UserNotFoundException;
import com.dockeep.user.model.User;
import com.dockeep.user.model.UserDto;
import com.dockeep.user.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class CachedUserRepository {
    private static final String KEY_PREFIX = "dockeep:users:";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public UserDto findByUsernameOrEmail(String usernameOrEmail){
        return findUser(
                searchParam -> userRepository
                        .findByUsernameOrEmail(searchParam)
                        .orElseThrow(() -> new UserNotFoundException("")),
                usernameOrEmail,
                StringUtils::hasText,
                dto -> dto
        );
    }

    public UserDto findById(Long id){
        return findUser(
                userId -> userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("")),
                id,
                Objects::nonNull,
                userDto -> userDto
        );
    }

    public User findUserEntityByUsernameOrEmail(String usernameOrEmail){
        return findUser(
                identifier -> userRepository
                        .findByUsernameOrEmail(identifier)
                        .orElseThrow(() -> new UserNotFoundException("")),
                usernameOrEmail,
                Objects::nonNull,
                userMapper::dtoToEntity
        );
    }

    public User findUserEntityById(Long id) {
        return findUser(
                userId -> userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("")),
                id,
                Objects::nonNull,
                userMapper::dtoToEntity
        );
    }

    public UserDto save(User user){
        if(!StringUtils.hasText(user.getUsername())){
            throw new IllegalStateException("No username or email was provided.");
        }

        String key = KEY_PREFIX.concat(user.getUsername());
        User savedUser = userRepository.save(user);

        UserDto userDto = Optional.of(savedUser)
                .map(userMapper::entityToDto)
                .orElseThrow();
        redisTemplate.opsForValue().set(key, userDto);

        return userDto;
    }


    private <T,R> R findUser(
            Function<T, User> repositoryFunction,
            T searchParameter,
            Predicate<T> validationCondition,
            Function<UserDto, R> mapFunction
    ){
        if(!validationCondition.test(searchParameter)){
            throw new IllegalStateException("");
        }

        String key = KEY_PREFIX.concat(searchParameter.toString());
        Object cachedUser = redisTemplate.opsForValue().get(key);

        UserDto user = switch (cachedUser){
            case UserDto dto -> dto;
            case null, default ->
                    Optional.ofNullable(repositoryFunction.apply(searchParameter))
                            .map(userMapper::entityToDto)
                            .orElseThrow();
        };

        redisTemplate.opsForValue().set(key, user);
        return mapFunction.apply(user);
    }

}
//private <T> UserDto findUser(
//            Function<T, User> repositoryFunction,
//            T searchParameter,
//            Predicate<T> validationCondition
//    ){
//        if(!validationCondition.test(searchParameter)){
//            throw new IllegalStateException("");
//        }
//
//        String key = KEY_PREFIX.concat(searchParameter.toString());
//        Object cachedUser = redisTemplate.opsForValue().get(key);
//
//        UserDto user = switch (cachedUser){
//            case UserDto dto -> dto;
//            case null, default ->
//                    Optional.ofNullable(repositoryFunction.apply(searchParameter))
//                            .map(userMapper::entityToDto)
//                            .orElseThrow();
//        };
//
//        redisTemplate.opsForValue().set(key, user);
//        return user;
//    }
//    // Combine this logic and choose the return type
//    private <T> User findUserEntity(
//            Function<T, User> repositoryFunction,
//            T searchParameter,
//            Predicate<T> validationCondition
//    ){
//        if(!validationCondition.test(searchParameter)){
//            throw new IllegalStateException("");
//        }
//
//        String key = KEY_PREFIX.concat(searchParameter.toString());
//        Object cachedUser = redisTemplate.opsForValue().get(key);
//
//        User user = switch (cachedUser){
//            case UserDto dto -> userMapper.dtoToEntity(dto);
//            case null, default ->
//                    Optional.ofNullable(repositoryFunction.apply(searchParameter))
//                            .orElseThrow();
//        };
//
//        redisTemplate.opsForValue().set(key, userMapper.entityToDto(user));
//        return user;
//    }
