package com.dockeep.user.util;

import com.dockeep.user.model.User;
import com.dockeep.user.model.UserDto;
import com.dockeep.user.model.UserPrincipal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto entityToDto(User user);
    @Mapping(target = "id", expression = "java((Long) userDto.getId())")
    User dtoToEntity(UserDto userDto);
    UserPrincipal entityToPrincipal(User user);
//    @Mapping(target = "domainIdentifier", expression = "java(event.id() != null ? String.valueOf(event.id()) : null)")
//    User eventToEntity(NewUserEvent event);
}
