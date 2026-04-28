package com.roombridge.service;

import com.roombridge.exception.ResourceNotFoundException;
import com.roombridge.mapper.AppUserMapper;
import com.roombridge.model.dto.AppUserDto;
import com.roombridge.model.entity.AppUser;
import com.roombridge.model.enums.UserRole;
import com.roombridge.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private AppUserMapper userMapper;

    @InjectMocks
    private AppUserService userService;

    private AppUser user;
    private AppUserDto userDto;

    @BeforeEach
    void setUp() {
        user = AppUser.builder()
                .id(1L).username("john").email("john@test.com")
                .password("secret").role(UserRole.USER)
                .build();
        userDto = AppUserDto.builder()
                .id(1L).username("john").email("john@test.com")
                .password("secret").role(UserRole.USER)
                .build();
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<AppUserDto> result = userService.getAllUsers();

        assertEquals(1, result.size());
    }

    @Test
    void getUserById_ShouldReturnDto_WhenFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        AppUserDto result = userService.getUserById(1L);

        assertEquals("john", result.getUsername());
    }

    @Test
    void getUserById_ShouldThrow_WhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void createUser_ShouldSave_WhenUsernameAndEmailUnique() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        AppUserDto result = userService.createUser(userDto);

        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void createUser_ShouldThrow_WhenUsernameTaken() {
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_ShouldThrow_WhenEmailTaken() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_ShouldDelete_WhenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrow_WhenNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(99L));
    }
}
