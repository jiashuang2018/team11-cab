package com.team11.cab.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team11.cab.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

	@Query("SELECT * FROM booking")
	ArrayList<Booking> ListAll();
	
	@Query("SELECT c from booking c WHERE c.BookingId = :id")
	Booking findBookingByID(@Param("id") String id);
}