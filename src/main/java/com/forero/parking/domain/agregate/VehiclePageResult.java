package com.forero.parking.domain.agregate;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VehiclePageResult<T> {
    private List<T> vehicles;
    private Pagination pagination;
}
