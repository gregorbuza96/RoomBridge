package com.roombridge.mapper;

import com.roombridge.model.dto.HotelDto;
import com.roombridge.model.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    @Mapping(target = "rooms", ignore = true)
    Hotel toEntity(HotelDto dto);

    HotelDto toDto(Hotel hotel);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    void updateEntity(HotelDto dto, @MappingTarget Hotel hotel);
}
