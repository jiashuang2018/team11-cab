package com.team11.cab.service;

import java.util.ArrayList;
import java.util.List;

import com.team11.cab.model.Facility;

public interface FacilityService {
	List<Facility> findAllFacilities();
	ArrayList<Facility> findFacilitiesByFacilityType(int facilityTypeId);
	Facility findFacilityById(int facilityId);
}
