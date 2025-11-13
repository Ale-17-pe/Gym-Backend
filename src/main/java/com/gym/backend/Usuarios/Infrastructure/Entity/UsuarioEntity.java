package com.gym.backend.Usuarios.Infrastructure.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String apellido;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String dni;

    private String telefono;

    private String direccion;

    private String password;

    private String rol;
}
