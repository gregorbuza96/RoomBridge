package com.roombridge.mapper;

import com.roombridge.model.dto.UserProfileDto;
import com.roombridge.model.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "user", ignore = true)
    UserProfile toEntity(UserProfileDto dto);

    @Mapping(target = "userId", source = "user.id")
    UserProfileDto toDto(UserProfile profile);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntity(UserProfileDto dto, @MappingTarget UserProfile profile);
}
