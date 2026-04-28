package com.roombridge.service;

import com.roombridge.exception.RoomAlreadyExistsException;
import com.roombridge.exception.RoomOccupiedException;
import com.roombridge.mapper.RoomMapper;
import com.roombridge.model.dto.RoomDto;
import com.roombridge.model.entity.Room;
import com.roombridge.model.enums.ComfortLevel;
import com.roombridge.model.enums.RoomStatus;
import com.roombridge.model.enums.RoomType;
import com.roombridge.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomService roomService;

    private RoomDto roomDto;
    private Room room;

    @BeforeEach
    void setUp() {
        roomDto = new RoomDto(1L,101, RoomType.DOUBLE, ComfortLevel.SUPERIOR, 150.0, 2, RoomStatus.AVAILABLE, "Sea view room");
        room = new Room(1L, 101, RoomType.DOUBLE, ComfortLevel.SUPERIOR, 150.0, 2, RoomStatus.AVAILABLE, "Sea view room");
    }

    /**
     * Test adding a new room successfully.
     */
    @Test
    void addRoom_ShouldSaveRoom_WhenRoomDoesNotExist() {
        when(roomRepository.findByRoomNumber(roomDto.getRoomNumber())).thenReturn(Optional.empty());
        when(roomMapper.toEntity(roomDto)).thenReturn(room);
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toDto(room)).thenReturn(roomDto);

        RoomDto savedRoom = roomService.addRoom(roomDto);

        assertNotNull(savedRoom);
        assertEquals(roomDto.getRoomNumber(), savedRoom.getRoomNumber());
        verify(roomRepository, times(1)).save(room);
    }

    /**
     * Test adding a duplicate room should throw exception.
     */
    @Test
    void addRoom_ShouldThrowException_WhenRoomAlreadyExists() {
        when(roomRepository.findByRoomNumber(roomDto.getRoomNumber())).thenReturn(Optional.of(room));

        assertThrows(RoomAlreadyExistsException.class, () -> roomService.addRoom(roomDto));
        verify(roomRepository, never()).save(any());
    }

    /**
     * Test deleting a room successfully.
     */
    @Test
    void deleteRoom_ShouldDeleteRoom_WhenRoomIsAvailable() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        assertDoesNotThrow(() -> roomService.deleteRoom(1L));

        verify(roomRepository, times(1)).delete(room);
    }

    /**
     * Test deleting an occupied room should throw exception.
     */
    @Test
    void deleteRoom_ShouldThrowException_WhenRoomIsOccupied() {
        room.setStatus(RoomStatus.OCCUPIED);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        assertThrows(RoomOccupiedException.class, () -> roomService.deleteRoom(1L));

        verify(roomRepository, never()).delete(any());
    }

    /**
     * Test deleting a non-existing room should throw exception.
     */
    @Test
    void deleteRoom_ShouldThrowException_WhenRoomDoesNotExist() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> roomService.deleteRoom(1L));

        verify(roomRepository, never()).delete(any());
    }
}
