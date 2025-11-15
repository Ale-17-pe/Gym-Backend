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
        java.time.Duration duracion = java.time.Duration.between(java.time.LocalDateTime.now(), paymentCode.getFechaExpiracion());
        long horas = duracion.toHours();
        long minutos = duracion.toMinutesPart();
        return String.format("%02d:%02d", horas, minutos);
    }
}
