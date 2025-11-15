package com.gym.backend.Membresias.Domain.Exceptions;

public class MembresiaNotFoundException extends MembresiaException {
    public MembresiaNotFoundException(Long id) {
        super("Membresía con ID " + id + " no encontrada", "MEMBRESIA_NOT_FOUND");
    }
}