package com.roombridge.hotelservice.repository;

import com.roombridge.hotelservice.model.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {}
