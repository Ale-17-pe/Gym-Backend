package com.gym.backend.PaymentCode.Application.Mapper;

import com.gym.backend.PaymentCode.Application.Dto.PaymentCodeDTO;
import com.gym.backend.PaymentCode.Application.Dto.PaymentCodeResponse;
import com.gym.backend.PaymentCode.Domain.PaymentCode;
import com.gym.backend.PaymentCode.Infrastructure.Entity.PaymentCodeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PaymentCodeMapper {
    PaymentCode toDomain(PaymentCodeEntity entity);
    PaymentCodeEntity toEntity(PaymentCode domain);
    PaymentCodeDTO toDTO(PaymentCode domain);

    @Mapping(target = "expirado", expression = "java(paymentCode.estaExpirado())")
    @Mapping(target = "puedeUsar", expression = "java(paymentCode.puedeSerUsado())")
    @Mapping(target = "tiempoRestante", source = "paymentCode", qualifiedByName = "calcularTiempoRestante")
    PaymentCodeResponse toResponse(PaymentCode paymentCode);

    @Named("calcularTiempoRestante")
    default String calcularTiempoRestante(PaymentCode paymentCode) {
        if (paymentCode.estaExpirado()) return "Expirado";

        long minutos = paymentCode.minutosRestantes();
        if (minutos < 60) {
            return minutos + " min";
        } else {
            long horas = paymentCode.horasRestantes();
            long minutosRestantes = minutos % 60;
            return String.format("%d h %02d min", horas, minutosRestantes);
        }
    }
}