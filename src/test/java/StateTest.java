import co.unicauca.piedrazul.domain.entities.Appointment;
import co.unicauca.piedrazul.domain.entities.Doctor;
import co.unicauca.piedrazul.domain.entities.Patient;
import co.unicauca.piedrazul.domain.entities.enums.AppointmentStatus;
import co.unicauca.piedrazul.domain.state.PendienteConfirmacionState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Patron State - Ciclo de vida de Appointment")
class StateTest {

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        Doctor doctor = new Doctor();
        doctor.setId(1);
        doctor.setFirstName("Carlos");
        doctor.setFirstSurname("Gomez");

        Patient patient = new Patient();
        patient.setId(2);
        patient.setFirstName("Maria");
        patient.setFirstSurname("Perez");

        appointment = new Appointment(
                LocalDate.of(2026, 5, 10),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                AppointmentStatus.AGENDADA,
                doctor,
                patient
        );
        appointment.setReason("Consulta general");
    }

    @Test
    @DisplayName("El estado inicial de una cita es Creada")
    void estadoInicial_esCreada() {
        assertEquals("Creada", appointment.getState().getNombre());
    }

    @Test
    @DisplayName("Creada -> confirmar() -> Confirmada")
    void confirmar_desdeCreada_pasaAConfirmada() {
        appointment.confirmar();
        assertEquals("Confirmada", appointment.getState().getNombre());
    }

    @Test
    @DisplayName("Confirmada -> atender() -> Atendida")
    void atender_desdeConfirmada_pasaAAtendida() {
        appointment.confirmar();
        appointment.atender();
        assertEquals("Atendida", appointment.getState().getNombre());
    }

    @Test
    @DisplayName("Confirmada -> cancelar() -> Cancelada")
    void cancelar_desdeConfirmada_pasaACancelada() {
        appointment.confirmar();
        appointment.cancelar();
        assertEquals("Cancelada", appointment.getState().getNombre());
    }

    @Test
    @DisplayName("Confirmada -> reagendar() -> Reagendada")
    void reagendar_desdeConfirmada_pasaAReagendada() {
        appointment.confirmar();
        appointment.reagendar();
        assertEquals("Reagendada", appointment.getState().getNombre());
    }

    @Test
    @DisplayName("Confirmada -> noAsistio() -> NoAsistio")
    void noAsistio_desdeConfirmada_pasaANoAsistio() {
        appointment.confirmar();
        appointment.noAsistio();
        assertEquals("NoAsistio", appointment.getState().getNombre());
    }

    @Test
    @DisplayName("Reagendada -> confirmar() -> Confirmada")
    void confirmar_desdeReagendada_pasaAConfirmada() {
        appointment.confirmar();
        appointment.reagendar();
        appointment.confirmar();
        assertEquals("Confirmada", appointment.getState().getNombre());
    }

    @Test
    @DisplayName("PendienteConfirmacion -> confirmar() -> Confirmada")
    void confirmar_desdePendiente_pasaAConfirmada() {
        appointment.setState(new PendienteConfirmacionState());
        appointment.confirmar();
        assertEquals("Confirmada", appointment.getState().getNombre());
    }

    @Test
    @DisplayName("Creada no puede ser atendida directamente")
    void atender_desdeCreada_lanzaExcepcion() {
        assertThrows(IllegalStateException.class, () -> appointment.atender());
    }

    @Test
    @DisplayName("Creada no puede ser cancelada directamente")
    void cancelar_desdeCreada_lanzaExcepcion() {
        assertThrows(IllegalStateException.class, () -> appointment.cancelar());
    }

    @Test
    @DisplayName("Atendida es estado final — no acepta ninguna transicion")
    void atendida_esEstadoFinal() {
        appointment.confirmar();
        appointment.atender();
        assertAll(
                () -> assertThrows(IllegalStateException.class, () -> appointment.confirmar()),
                () -> assertThrows(IllegalStateException.class, () -> appointment.cancelar()),
                () -> assertThrows(IllegalStateException.class, () -> appointment.reagendar()),
                () -> assertThrows(IllegalStateException.class, () -> appointment.noAsistio())
        );
    }

    @Test
    @DisplayName("Cancelada es estado final — no acepta ninguna transicion")
    void cancelada_esEstadoFinal() {
        appointment.confirmar();
        appointment.cancelar();
        assertAll(
                () -> assertThrows(IllegalStateException.class, () -> appointment.confirmar()),
                () -> assertThrows(IllegalStateException.class, () -> appointment.atender()),
                () -> assertThrows(IllegalStateException.class, () -> appointment.reagendar()),
                () -> assertThrows(IllegalStateException.class, () -> appointment.noAsistio())
        );
    }

    @Test
    @DisplayName("NoAsistio es estado final — no acepta ninguna transicion")
    void noAsistio_esEstadoFinal() {
        appointment.confirmar();
        appointment.noAsistio();
        assertAll(
                () -> assertThrows(IllegalStateException.class, () -> appointment.confirmar()),
                () -> assertThrows(IllegalStateException.class, () -> appointment.atender()),
                () -> assertThrows(IllegalStateException.class, () -> appointment.cancelar()),
                () -> assertThrows(IllegalStateException.class, () -> appointment.reagendar())
        );
    }

    @Test
    @DisplayName("getDescription() muestra el nombre del estado actual")
    void getDescription_muestraEstado() {
        appointment.confirmar();
        assertTrue(appointment.getDescription().contains("Confirmada"));
    }
}