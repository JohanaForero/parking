package com.forero.parking.infrastructure.adapter.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequestDto {
    private String email;
    private String placa;
    private String mensaje;
    private String parqueaderoNombre;
}
