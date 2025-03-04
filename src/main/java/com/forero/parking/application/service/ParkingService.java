package com.forero.parking.application.service;

import com.forero.parking.application.port.DbPort;
import com.forero.parking.domain.agregate.Pagination;
import com.forero.parking.domain.agregate.VehiclePageResult;
import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.Parking;
import com.forero.parking.infrastructure.util.JwtUtil;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public record ParkingService(DbPort dbPort, ValidationService validationService) {

    public int createParking(final Parking parking) {
        this.validationService.validateParkingNameAvailability(parking.getName());
        return this.dbPort.saveParking(parking);
    }

    public List<Parking> getParkings(final String token) {
        final boolean isPartner = JwtUtil.isUserPartner(token);
        if (!isPartner) {
            return this.dbPort.getAllParkings();
        }
        final String partnerId = JwtUtil.getClaimFromToken(token, JwtClaimNames.SUB);
        return this.dbPort.findAllParking(partnerId);
    }

    public Parking getParking(final int idParking, final String token) {
        final boolean isPartner = JwtUtil.isUserPartner(token);
        if (!isPartner) {
            return this.dbPort.findById(idParking);
        }

        final String partnerId = JwtUtil.getClaimFromToken(token, JwtClaimNames.SUB);
        this.validationService.validateParkingBelongsToPartner(idParking, partnerId);
        return this.dbPort.findById(idParking);
    }

    public void deleteParking(final int idParking) {
        this.validationService.parkingIsEmpty(idParking);
        this.dbPort.deleteParking(idParking);
    }

    public void updatePartially(final Parking parking) {
        this.dbPort.updatePartially(parking);
    }

    public void updateParking(final Parking parking) {
        this.validationService.validateNameChange(parking);
        final int parkingId = parking.getId().intValue();
        this.validationService.validateUpdateParkingCodeNotLowerThanInUse(parkingId, parking.getNumberOfParkingLots());
        this.dbPort.updateParking(parking);
    }

    public VehiclePageResult<History> getVehiclesInParking(final String token, final Parking parking,
                                                           final Pagination pagination) {
        final VehiclePageResult<History> vehiclesPageResult = new VehiclePageResult<>();
        final boolean isPartner = JwtUtil.isUserPartner(token);
        if (!isPartner) {
            final List<History> historiesAdmin = this.dbPort.getVehicles(parking.getId().intValue(), pagination);
            this.validationService.validateVehicles(historiesAdmin);
            vehiclesPageResult.setVehicles(historiesAdmin);
            final Pagination paginationResult = this.buildPagination(pagination, parking.getId().intValue());
            vehiclesPageResult.setPagination(paginationResult);
            return vehiclesPageResult;
        }
        final String partnerId = JwtUtil.getClaimFromToken(token, JwtClaimNames.SUB);
        this.validationService.validateParkingBelongsToPartner(parking.getId().intValue(), partnerId);
        final List<History> historiesPartner = this.dbPort.getVehicles(parking.getId().intValue(), pagination);
        this.validationService.validateVehicles(historiesPartner);
        vehiclesPageResult.setVehicles(historiesPartner);
        final Pagination paginationResult = this.buildPagination(pagination, parking.getId().intValue());
        vehiclesPageResult.setPagination(paginationResult);
        return vehiclesPageResult;
    }

    private Pagination buildPagination(final Pagination paginationRequest, final int parkingId) {
        final int totalVehicles = this.dbPort.getTotalVehicles(parkingId);
        final int pageSize = paginationRequest.getPageSize();
        final int totalPages = this.calculateTotalPages(totalVehicles, pageSize);
        paginationRequest.setTotal(totalVehicles);
        paginationRequest.setTotalPages(totalPages);
        return paginationRequest;
    }

    public int calculateTotalPages(final int totalItems, final int itemsPerPage) {
        if (itemsPerPage == 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalItems / itemsPerPage);
    }

    public List<History> getLimitedVehiclesInParkingById(final int parkingId) {

        final List<History> histories = this.dbPort.getVehiclesStatics(parkingId);
        return histories;
    }
}
