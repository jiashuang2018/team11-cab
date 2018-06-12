package com.team11.cab.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import com.team11.cab.model.Booking;

public interface BookingService {
	ArrayList<Booking> findAllBookings();
	boolean isBookingValid(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2);

}
