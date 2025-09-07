package com.dockeep.user.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Objects;
@Builder
@Getter
public class UserDto {
    private Object id;
    private String username;
    private String email;

    @Override
    public int hashCode(){
        return Objects.hash(username, email, id);
    }

    @Override
    public boolean equals(Object object){
        if(this == object) return true;
        if(object == null || getClass() != object.getClass()) return false;

        UserDto dto = (UserDto) object;
        return Objects.equals(username, dto.username) &&
                Objects.equals(email, dto.email) &&
                Objects.equals(id, dto.id);
    }
}
