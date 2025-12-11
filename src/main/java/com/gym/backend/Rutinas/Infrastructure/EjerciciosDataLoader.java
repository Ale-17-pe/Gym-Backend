package com.gym.backend.Rutinas.Infrastructure;

import com.gym.backend.Rutinas.Domain.Ejercicio;
import com.gym.backend.Rutinas.Domain.EjercicioRepositoryPort;
import com.gym.backend.Rutinas.Domain.Enum.Dificultad;
import com.gym.backend.Rutinas.Domain.Enum.GrupoMuscular;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Carga el catálogo inicial de ejercicios al arrancar la aplicación
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class EjerciciosDataLoader implements CommandLineRunner {

    private final EjercicioRepositoryPort ejercicioRepository;

    @Override
    public void run(String... args) {
        if (ejercicioRepository.listar().isEmpty()) {
            log.info("💪 Inicializando catálogo de ejercicios...");
            cargarEjercicios();
            log.info("✅ {} ejercicios cargados correctamente", ejercicioRepository.listar().size());
        }
    }

    private void cargarEjercicios() {
        List<Ejercicio> ejercicios = List.of(
                // ========== PECHO ==========
                crearEjercicio("Press de Banca con Barra", "Ejercicio básico para pecho",
                        GrupoMuscular.PECHO, GrupoMuscular.TRICEPS, Dificultad.INTERMEDIO, "Barra y banco",
                        "Acuéstate en el banco, baja la barra al pecho y empuja hacia arriba"),
                crearEjercicio("Press Inclinado con Mancuernas", "Enfoca la parte superior del pecho",
                        GrupoMuscular.PECHO, GrupoMuscular.HOMBROS, Dificultad.INTERMEDIO,
                        "Mancuernas y banco inclinado",
                        "Inclina el banco a 30-45 grados, empuja las mancuernas hacia arriba"),
                crearEjercicio("Aperturas con Mancuernas", "Aislamiento de pecho",
                        GrupoMuscular.PECHO, null, Dificultad.PRINCIPIANTE, "Mancuernas y banco",
                        "Abre los brazos manteniendo codos ligeramente flexionados"),
                crearEjercicio("Fondos en Paralelas", "Ejercicio compuesto para pecho y tríceps",
                        GrupoMuscular.PECHO, GrupoMuscular.TRICEPS, Dificultad.AVANZADO, "Barras paralelas",
                        "Inclínate hacia adelante para enfocar más el pecho"),
                crearEjercicio("Press en Máquina", "Press de pecho guiado",
                        GrupoMuscular.PECHO, GrupoMuscular.TRICEPS, Dificultad.PRINCIPIANTE, "Máquina de press",
                        "Movimiento controlado y guiado por la máquina"),
                crearEjercicio("Crossover en Poleas", "Aislamiento de pecho con cables",
                        GrupoMuscular.PECHO, null, Dificultad.INTERMEDIO, "Poleas altas",
                        "Cruza los cables frente al cuerpo apretando el pecho"),

                // ========== ESPALDA ==========
                crearEjercicio("Dominadas", "Ejercicio rey para espalda",
                        GrupoMuscular.ESPALDA, GrupoMuscular.BICEPS, Dificultad.AVANZADO, "Barra de dominadas",
                        "Agarra la barra y sube hasta que la barbilla pase la barra"),
                crearEjercicio("Remo con Barra", "Ejercicio compuesto de espalda",
                        GrupoMuscular.ESPALDA, GrupoMuscular.BICEPS, Dificultad.INTERMEDIO, "Barra",
                        "Inclínate 45 grados y tira la barra hacia el abdomen"),
                crearEjercicio("Remo con Mancuerna", "Remo unilateral",
                        GrupoMuscular.ESPALDA, GrupoMuscular.BICEPS, Dificultad.INTERMEDIO, "Mancuerna y banco",
                        "Apoya una mano en el banco y tira la mancuerna hacia la cadera"),
                crearEjercicio("Jalón al Pecho", "Ejercicio de tirón vertical",
                        GrupoMuscular.ESPALDA, GrupoMuscular.BICEPS, Dificultad.PRINCIPIANTE, "Polea alta",
                        "Tira la barra hacia el pecho manteniendo el torso erguido"),
                crearEjercicio("Peso Muerto", "Ejercicio compuesto total",
                        GrupoMuscular.ESPALDA, GrupoMuscular.GLUTEOS, Dificultad.AVANZADO, "Barra",
                        "Levanta la barra del suelo manteniendo la espalda recta"),
                crearEjercicio("Remo en Máquina", "Remo guiado",
                        GrupoMuscular.ESPALDA, GrupoMuscular.BICEPS, Dificultad.PRINCIPIANTE, "Máquina de remo",
                        "Tira los agarres hacia el abdomen"),

                // ========== HOMBROS ==========
                crearEjercicio("Press Militar con Barra", "Press de hombros básico",
                        GrupoMuscular.HOMBROS, GrupoMuscular.TRICEPS, Dificultad.INTERMEDIO, "Barra",
                        "Empuja la barra desde los hombros hacia arriba"),
                crearEjercicio("Press con Mancuernas Sentado", "Press de hombros con mancuernas",
                        GrupoMuscular.HOMBROS, GrupoMuscular.TRICEPS, Dificultad.INTERMEDIO, "Mancuernas y banco",
                        "Empuja las mancuernas hacia arriba de forma controlada"),
                crearEjercicio("Elevaciones Laterales", "Aislamiento de deltoides lateral",
                        GrupoMuscular.HOMBROS, null, Dificultad.PRINCIPIANTE, "Mancuernas",
                        "Eleva los brazos a los lados hasta la altura de los hombros"),
                crearEjercicio("Elevaciones Frontales", "Aislamiento de deltoides anterior",
                        GrupoMuscular.HOMBROS, null, Dificultad.PRINCIPIANTE, "Mancuernas",
                        "Eleva los brazos al frente alternando"),
                crearEjercicio("Pájaros", "Deltoides posterior",
                        GrupoMuscular.HOMBROS, GrupoMuscular.TRAPECIO, Dificultad.INTERMEDIO, "Mancuernas",
                        "Inclínate y eleva los brazos hacia los lados"),
                crearEjercicio("Face Pull", "Deltoides posterior y trapecio",
                        GrupoMuscular.HOMBROS, GrupoMuscular.TRAPECIO, Dificultad.INTERMEDIO, "Polea",
                        "Tira la cuerda hacia la cara abriendo los codos"),

                // ========== BÍCEPS ==========
                crearEjercicio("Curl con Barra", "Ejercicio básico de bíceps",
                        GrupoMuscular.BICEPS, null, Dificultad.PRINCIPIANTE, "Barra",
                        "Flexiona los codos manteniendo los codos pegados al cuerpo"),
                crearEjercicio("Curl con Mancuernas Alternado", "Curl unilateral",
                        GrupoMuscular.BICEPS, null, Dificultad.PRINCIPIANTE, "Mancuernas",
                        "Alterna flexionando cada brazo con supinación"),
                crearEjercicio("Curl Martillo", "Bíceps y braquial",
                        GrupoMuscular.BICEPS, GrupoMuscular.ANTEBRAZOS, Dificultad.PRINCIPIANTE, "Mancuernas",
                        "Flexiona con agarre neutro (palmas enfrentadas)"),
                crearEjercicio("Curl en Banco Scott", "Aislamiento de bíceps",
                        GrupoMuscular.BICEPS, null, Dificultad.INTERMEDIO, "Banco Scott y barra",
                        "Apoya los brazos en el banco y flexiona"),
                crearEjercicio("Curl en Polea", "Bíceps con tensión constante",
                        GrupoMuscular.BICEPS, null, Dificultad.INTERMEDIO, "Polea baja",
                        "Flexiona manteniendo tensión durante todo el movimiento"),

                // ========== TRÍCEPS ==========
                crearEjercicio("Press Francés", "Aislamiento de tríceps",
                        GrupoMuscular.TRICEPS, null, Dificultad.INTERMEDIO, "Barra Z",
                        "Baja la barra hacia la frente flexionando solo los codos"),
                crearEjercicio("Extensión en Polea", "Tríceps con cable",
                        GrupoMuscular.TRICEPS, null, Dificultad.PRINCIPIANTE, "Polea alta",
                        "Extiende los brazos hacia abajo manteniendo codos fijos"),
                crearEjercicio("Fondos en Banco", "Tríceps con peso corporal",
                        GrupoMuscular.TRICEPS, GrupoMuscular.PECHO, Dificultad.PRINCIPIANTE, "Banco",
                        "Baja el cuerpo flexionando los codos hacia atrás"),
                crearEjercicio("Patada de Tríceps", "Aislamiento con mancuerna",
                        GrupoMuscular.TRICEPS, null, Dificultad.INTERMEDIO, "Mancuerna",
                        "Extiende el brazo hacia atrás manteniendo el codo fijo"),
                crearEjercicio("Press Cerrado", "Tríceps con barra",
                        GrupoMuscular.TRICEPS, GrupoMuscular.PECHO, Dificultad.INTERMEDIO, "Barra y banco",
                        "Press de banca con agarre cerrado para enfocar tríceps"),

                // ========== PIERNAS ==========
                crearEjercicio("Sentadilla con Barra", "Ejercicio rey de piernas",
                        GrupoMuscular.CUADRICEPS, GrupoMuscular.GLUTEOS, Dificultad.INTERMEDIO, "Barra y rack",
                        "Baja manteniendo la espalda recta y rodillas alineadas"),
                crearEjercicio("Prensa de Piernas", "Cuádriceps con máquina",
                        GrupoMuscular.CUADRICEPS, GrupoMuscular.GLUTEOS, Dificultad.PRINCIPIANTE, "Prensa",
                        "Empuja la plataforma con los pies a la altura de los hombros"),
                crearEjercicio("Extensión de Cuádriceps", "Aislamiento de cuádriceps",
                        GrupoMuscular.CUADRICEPS, null, Dificultad.PRINCIPIANTE, "Máquina de extensión",
                        "Extiende las piernas hasta la horizontal"),
                crearEjercicio("Curl Femoral", "Isquiotibiales",
                        GrupoMuscular.ISQUIOTIBIALES, null, Dificultad.PRINCIPIANTE, "Máquina de curl",
                        "Flexiona las piernas hacia los glúteos"),
                crearEjercicio("Peso Muerto Rumano", "Isquiotibiales y glúteos",
                        GrupoMuscular.ISQUIOTIBIALES, GrupoMuscular.GLUTEOS, Dificultad.INTERMEDIO, "Barra",
                        "Inclínate manteniendo las piernas semi-extendidas"),
                crearEjercicio("Zancadas", "Cuádriceps y glúteos",
                        GrupoMuscular.CUADRICEPS, GrupoMuscular.GLUTEOS, Dificultad.INTERMEDIO, "Mancuernas",
                        "Da un paso adelante y baja la rodilla trasera"),
                crearEjercicio("Hip Thrust", "Glúteos",
                        GrupoMuscular.GLUTEOS, GrupoMuscular.ISQUIOTIBIALES, Dificultad.INTERMEDIO, "Barra y banco",
                        "Empuja la cadera hacia arriba apretando glúteos"),
                crearEjercicio("Elevación de Talones", "Pantorrillas",
                        GrupoMuscular.PANTORRILLAS, null, Dificultad.PRINCIPIANTE, "Máquina o escalón",
                        "Eleva los talones hasta la punta de los pies"),
                crearEjercicio("Aductores en Máquina", "Aductores",
                        GrupoMuscular.CUADRICEPS, null, Dificultad.PRINCIPIANTE, "Máquina de aductores",
                        "Junta las piernas contra la resistencia"),
                crearEjercicio("Abductores en Máquina", "Abductores",
                        GrupoMuscular.GLUTEOS, null, Dificultad.PRINCIPIANTE, "Máquina de abductores",
                        "Abre las piernas contra la resistencia"),

                // ========== ABDOMINALES ==========
                crearEjercicio("Crunch Abdominal", "Abdominales básico",
                        GrupoMuscular.ABDOMINALES, null, Dificultad.PRINCIPIANTE, "Colchoneta",
                        "Eleva los hombros del suelo contrayendo el abdomen"),
                crearEjercicio("Plancha", "Core isométrico",
                        GrupoMuscular.ABDOMINALES, GrupoMuscular.LUMBARES, Dificultad.INTERMEDIO, "Colchoneta",
                        "Mantén el cuerpo recto apoyado en antebrazos y pies"),
                crearEjercicio("Elevación de Piernas", "Abdominales inferior",
                        GrupoMuscular.ABDOMINALES, null, Dificultad.INTERMEDIO, "Barra o banco",
                        "Eleva las piernas manteniendo el abdomen contraído"),
                crearEjercicio("Russian Twist", "Oblicuos",
                        GrupoMuscular.OBLICUOS, GrupoMuscular.ABDOMINALES, Dificultad.INTERMEDIO, "Peso o balón",
                        "Gira el torso de lado a lado con los pies elevados"),
                crearEjercicio("Ab Wheel", "Core completo",
                        GrupoMuscular.ABDOMINALES, GrupoMuscular.LUMBARES, Dificultad.AVANZADO, "Rueda abdominal",
                        "Rueda hacia adelante y regresa controladamente"),

                // ========== CARDIO ==========
                crearEjercicio("Cinta de Correr", "Cardio básico",
                        GrupoMuscular.CARDIO, null, Dificultad.PRINCIPIANTE, "Cinta de correr",
                        "Camina o corre a intensidad moderada"),
                crearEjercicio("Bicicleta Estática", "Cardio de bajo impacto",
                        GrupoMuscular.CARDIO, GrupoMuscular.CUADRICEPS, Dificultad.PRINCIPIANTE, "Bicicleta",
                        "Pedalea a intensidad constante o por intervalos"),
                crearEjercicio("Elíptica", "Cardio de cuerpo completo",
                        GrupoMuscular.CARDIO, GrupoMuscular.CUERPO_COMPLETO, Dificultad.PRINCIPIANTE, "Elíptica",
                        "Movimiento de bajo impacto para todo el cuerpo"),
                crearEjercicio("Remo Ergómetro", "Cardio y fuerza",
                        GrupoMuscular.CARDIO, GrupoMuscular.ESPALDA, Dificultad.INTERMEDIO, "Máquina de remo",
                        "Combina tirón de espalda con empuje de piernas"),
                crearEjercicio("Saltos de Cuerda", "Cardio de alta intensidad",
                        GrupoMuscular.CARDIO, GrupoMuscular.PANTORRILLAS, Dificultad.INTERMEDIO, "Cuerda",
                        "Salta manteniendo los codos pegados al cuerpo"));

        ejercicios.forEach(ejercicioRepository::guardar);
    }

    private Ejercicio crearEjercicio(String nombre, String descripcion,
            GrupoMuscular grupoPrimario, GrupoMuscular grupoSecundario,
            Dificultad dificultad, String equipamiento, String instrucciones) {
        return Ejercicio.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .grupoMuscular(grupoPrimario)
                .grupoMuscularSecundario(grupoSecundario)
                .dificultad(dificultad)
                .equipamiento(equipamiento)
                .instrucciones(instrucciones)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }
}
