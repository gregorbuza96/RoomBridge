package com.roombridge.service;

import com.roombridge.exception.ResourceNotFoundException;
import com.roombridge.exception.RoomAlreadyExistsException;
import com.roombridge.exception.RoomOccupiedException;
import com.roombridge.mapper.RoomMapper;
import com.roombridge.model.dto.AmenityDto;
import com.roombridge.model.dto.RoomDto;
import com.roombridge.model.entity.Amenity;
import com.roombridge.model.entity.Hotel;
import com.roombridge.model.entity.Room;
import com.roombridge.model.enums.RoomStatus;
import com.roombridge.repository.AmenityRepository;
import com.roombridge.repository.HotelRepository;
import com.roombridge.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final RoomMapper roomMapper;

    public Page<RoomDto> getAllRooms(Pageable pageable) {
        log.info("Fetching all rooms, page: {}", pageable.getPageNumber());
        return roomRepository.findAll(pageable).map(this::toDtoWithAmenities);
    }

    public Page<RoomDto> getRoomsByHotel(Long hotelId, Pageable pageable) {
        return roomRepository.findByHotelId(hotelId, pageable).map(this::toDtoWithAmenities);
    }

    public RoomDto getRoomById(Long id) {
        log.info("Fetching room with id: {}", id);
        return roomRepository.findById(id)
                .map(this::toDtoWithAmenities)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }

    @Transactional
    public RoomDto addRoom(RoomDto request) {
        log.info("Adding room number: {}", request.getRoomNumber());
        if (roomRepository.findByRoomNumber(request.getRoomNumber()).isPresent()) {
            throw new RoomAlreadyExistsException("There is already a room with number: " + request.getRoomNumber());
        }
        Room room = roomMapper.toEntity(request);
        if (request.getHotelId() != null) {
            Hotel hotel = hotelRepository.findById(request.getHotelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + request.getHotelId()));
            room.setHotel(hotel);
        }
        setAmenities(room, request.getAmenityIds());
        return toDtoWithAmenities(roomRepository.save(room));
    }

    @Transactional
    public RoomDto updateRoom(Long id, RoomDto request) {
        log.info("Updating room with id: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        if (!room.getRoomNumber().equals(request.getRoomNumber()) &&
                roomRepository.findByRoomNumber(request.getRoomNumber()).isPresent()) {
            throw new RoomAlreadyExistsException("Room number already in use: " + request.getRoomNumber());
        }
        roomMapper.updateEntity(request, room);
        if (request.getHotelId() != null) {
            Hotel hotel = hotelRepository.findById(request.getHotelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + request.getHotelId()));
            room.setHotel(hotel);
        }
        setAmenities(room, request.getAmenityIds());
        return toDtoWithAmenities(roomRepository.save(room));
    }

    @Transactional
    public void deleteRoom(Long id) {
        log.info("Deleting room with id: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            throw new RoomOccupiedException("The room is occupied, it cannot be deleted.");
        }
        roomRepository.delete(room);
    }

    private void setAmenities(Room room, List<Long> amenityIds) {
        if (amenityIds != null && !amenityIds.isEmpty()) {
            List<Amenity> amenities = amenityRepository.findAllById(amenityIds);
            room.setAmenities(amenities);
        }
    }

    private RoomDto toDtoWithAmenities(Room room) {
        RoomDto dto = roomMapper.toDto(room);
        if (room.getAmenities() != null) {
            dto.setAmenityIds(room.getAmenities().stream().map(Amenity::getId).toList());
            dto.setAmenities(room.getAmenities().stream()
                    .map(a -> AmenityDto.builder().id(a.getId()).name(a.getName()).description(a.getDescription()).build())
                    .toList());
        }
        return dto;
    }
}
