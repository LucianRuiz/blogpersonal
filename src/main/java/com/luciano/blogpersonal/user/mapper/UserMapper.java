package com.luciano.blogpersonal.user.mapper;

import com.luciano.blogpersonal.user.dto.UserCreateRequest;
import com.luciano.blogpersonal.user.dto.UserResponse;
import com.luciano.blogpersonal.user.dto.UserUpdateRequest;
import com.luciano.blogpersonal.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserMapper() {

    }

    public User toEntity(UserCreateRequest userCreateRequest){
        if (userCreateRequest == null) {
            return null;
        }
        User user = new User();
        user.setName(userCreateRequest.getName());
        user.setLastName(userCreateRequest.getLastName());
        user.setUsername(userCreateRequest.getUsername());
        user.setEmail(userCreateRequest.getEmail());
        user.setPassword(userCreateRequest.getPassword());
        user.setBio(userCreateRequest.getBio());

        return user;
    }

    public void updateEntityFromDto (User user, UserUpdateRequest userUpdateRequest){
        if (userUpdateRequest == null)
            return;

        if (userUpdateRequest.getName()!= null ){
            user.setName(userUpdateRequest.getName());
        }

        if (userUpdateRequest.getLastName()!= null){
            user.setLastName(userUpdateRequest.getLastName());
        }

        if (userUpdateRequest.getEmail()!= null){
            user.setEmail(userUpdateRequest.getEmail());
        }

        if (userUpdateRequest.getBio() != null){
            user.setBio(userUpdateRequest.getBio());
        }

        if (userUpdateRequest.getProfileImage()!= null){
            user.setProfileImage(userUpdateRequest.getProfileImage());
        }
    }

    public UserResponse toDto(User user){
        if (user == null){
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImage(user.getProfileImage())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .role(user.getRole())
                .active(user.isActive())
                .build();
    }


}
