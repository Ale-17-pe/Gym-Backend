package com.gym.backend.Membresias.Application.Dto;


import lombok.Data;

@Data
public class MembresiaDTO {

    private Long id;
    private Long usuarioId;
    private Long planId;

    private String fechaInicio;
    private String fechaFin;

    private String estado;
}
