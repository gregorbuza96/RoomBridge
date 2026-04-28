package com.roombridge.mapper;

import com.roombridge.model.dto.AmenityDto;
import com.roombridge.model.entity.Amenity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AmenityMapper {

    @Mapping(target = "rooms", ignore = true)
    Amenity toEntity(AmenityDto dto);

    AmenityDto toDto(Amenity amenity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    void updateEntity(AmenityDto dto, @MappingTarget Amenity amenity);
}
