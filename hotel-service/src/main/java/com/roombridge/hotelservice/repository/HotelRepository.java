package com.roombridge.hotelservice.repository;

import com.roombridge.hotelservice.model.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {}
