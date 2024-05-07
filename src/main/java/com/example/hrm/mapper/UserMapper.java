package com.example.hrm.mapper;

import com.example.hrm.dto.request.UserRequest;
import com.example.hrm.dto.response.UserResponse;
import com.example.hrm.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);
    @Mapping(target = "email", source = "email")
    @Mapping(target = "citizenId", source = "citizenId")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "jobTitle", source = "jobTitle")
    User userRequestToUser(UserRequest userRequest);
}
