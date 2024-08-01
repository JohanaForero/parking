package com.forero.parking.infrastructure.mapper;

import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.infrastructure.repository.entity.DetailRegisterVehiclesEntity;
import com.forero.parking.infrastructure.repository.entity.ParkingLotEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DetailRegisterVehicleMapper {
    @Mapping(target = "parkingLot", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "entranceDate", ignore = true)
    DetailRegisterVehicle toModel(DetailRegisterVehiclesEntity detailRegisterVehiclesEntity);

    ParkingLot toDomain(ParkingLotEntity parkingLotEntity);

//    default ParkingVehiclesResponseDto toDto(final List<ParkingLot> parkingLots) {
//        if (parkingLots == null) {
//            return null;
//        }
//
//        final ParkingVehiclesResponseDto parkingVehiclesResponseDto = new ParkingVehiclesResponseDto();
//        parkingVehiclesResponseDto.vehicles(this.toDtos(parkingLots));
//
//        return parkingVehiclesResponseDto;
//    }

//    List<VehicleDto> toDtos(List<ParkingLot> parkingLots);
}
