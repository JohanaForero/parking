package com.forero.parking.domain.agregate;

import com.forero.parking.domain.model.History;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehiclesAgregate {
    private History history;
    private int total;
}
