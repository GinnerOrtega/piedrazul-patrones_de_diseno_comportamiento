import co.unicauca.piedrazul.domain.entities.Appointment;
import co.unicauca.piedrazul.domain.entities.Doctor;
import co.unicauca.piedrazul.domain.entities.Patient;
import co.unicauca.piedrazul.domain.entities.enums.AppointmentStatus;
import co.unicauca.piedrazul.templatemethod.AppointmentScheduler;
import co.unicauca.piedrazul.templatemethod.AutonomousScheduler;
import co.unicauca.piedrazul.templatemethod.ManualScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Patron Template Method - AppointmentScheduler")
public class TemplateMethodTest {

    private Doctor doctor;
    private Patient patient;
    private Appointment appointment;

    @BeforeEach
    public void setUp() {
        doctor = new Doctor();
        doctor.setId(1);
        doctor.setFirstName("Carlos");
        doctor.setFirstSurname("Perez");

        patient = new Patient();
        patient.setId(2);
        patient.setFirstName("Maria");
        patient.setFirstSurname("Lopez");

        appointment = new Appointment(
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(9, 30),
                AppointmentStatus.AGENDADA,
                doctor,
                patient
        );
        appointment.setReason("Consulta general");
    }

    // ── AutonomousScheduler ───────────────────────────────────────────────────

    @Test
    @DisplayName("AutonomousScheduler es subclase de AppointmentScheduler")
    public void autonomousScheduler_esSubclase() {
        assertInstanceOf(AppointmentScheduler.class, new AutonomousScheduler());
    }

    @Test
    @DisplayName("AutonomousScheduler.schedule() ejecuta sin excepcion con datos validos")
    public void autonomousScheduler_scheduleExitoso() {
        AppointmentScheduler scheduler = new AutonomousScheduler();
        assertDoesNotThrow(() -> scheduler.schedule(appointment));
    }

    @Test
    @DisplayName("AutonomousScheduler confirma la cita al finalizar")
    public void autonomousScheduler_confirmaCita() {
        AppointmentScheduler scheduler = new AutonomousScheduler();
        scheduler.schedule(appointment);
        assertEquals("Confirmada", appointment.getState().getNombre());
    }

    @Test
    @DisplayName("AutonomousScheduler sin paciente lanza excepcion")
    public void autonomousScheduler_sinPacienteLanzaExcepcion() {
        appointment.setPatient(null);
        AppointmentScheduler scheduler = new AutonomousScheduler();
        assertThrows(IllegalArgumentException.class, () -> scheduler.schedule(appointment));
    }

    @Test
    @DisplayName("AutonomousScheduler sin motivo lanza excepcion")
    public void autonomousScheduler_sinMotivoLanzaExcepcion() {
        appointment.setReason(null);
        AppointmentScheduler scheduler = new AutonomousScheduler();
        assertThrows(IllegalArgumentException.class, () -> scheduler.schedule(appointment));
    }

    // ── ManualScheduler ───────────────────────────────────────────────────────

    @Test
    @DisplayName("ManualScheduler es subclase de AppointmentScheduler")
    public void manualScheduler_esSubclase() {
        assertInstanceOf(AppointmentScheduler.class, new ManualScheduler());
    }

    @Test
    @DisplayName("ManualScheduler.schedule() ejecuta sin excepcion con datos validos")
    public void manualScheduler_scheduleExitoso() {
        AppointmentScheduler scheduler = new ManualScheduler();
        assertDoesNotThrow(() -> scheduler.schedule(appointment));
    }

    @Test
    @DisplayName("ManualScheduler confirma la cita al finalizar")
    public void manualScheduler_confirmaCita() {
        AppointmentScheduler scheduler = new ManualScheduler();
        scheduler.schedule(appointment);
        assertEquals("Confirmada", appointment.getState().getNombre());
    }

    @Test
    @DisplayName("ManualScheduler sin medico lanza excepcion")
    public void manualScheduler_sinMedicoLanzaExcepcion() {
        appointment.setDoctor(null);
        AppointmentScheduler scheduler = new ManualScheduler();
        assertThrows(IllegalArgumentException.class, () -> scheduler.schedule(appointment));
    }

    @Test
    @DisplayName("ManualScheduler sin paciente lanza excepcion")
    public void manualScheduler_sinPacienteLanzaExcepcion() {
        appointment.setPatient(null);
        AppointmentScheduler scheduler = new ManualScheduler();
        assertThrows(IllegalArgumentException.class, () -> scheduler.schedule(appointment));
    }

    // ── Polimorfismo ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Ambos schedulers se tratan igual desde AppointmentScheduler")
    public void polimorfismo_ambosFuncionanDesdeClaseBase() {
        AppointmentScheduler autonomous = new AutonomousScheduler();
        AppointmentScheduler manual = new ManualScheduler();
        assertDoesNotThrow(() -> autonomous.schedule(appointment));

        // reset state para la segunda llamada
        appointment = new Appointment(
                LocalDate.now().plusDays(2),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                AppointmentStatus.AGENDADA,
                doctor,
                patient
        );
        appointment.setReason("Segunda consulta");
        assertDoesNotThrow(() -> manual.schedule(appointment));
    }
}