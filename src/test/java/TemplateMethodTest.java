
/**
 *
 * @author GINNER
 */
import co.unicauca.piedrazul.domain.entities.Appointment;
import co.unicauca.piedrazul.domain.entities.Doctor;
import co.unicauca.piedrazul.domain.entities.Patient;
import co.unicauca.piedrazul.domain.entities.enums.AppointmentStatus;
import co.unicauca.piedrazul.templatemethod.UrgentAppointmentScheduling;
import co.unicauca.piedrazul.domain.access.IAppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class TemplateMethodTest {

    private IAppointmentRepository repoMock;
    private UrgentAppointmentScheduling urgentScheduling;
    private Appointment appointment;

    @BeforeEach
    public void setUp() {
        // Mock del repositorio para no depender de la BD
        repoMock = Mockito.mock(IAppointmentRepository.class);
        Mockito.when(repoMock.save(Mockito.any())).thenReturn(true);

        urgentScheduling = new UrgentAppointmentScheduling(repoMock);

        Doctor doctor = new Doctor();
        doctor.setId(1);
        doctor.setFirstName("Carlos");
        doctor.setFirstSurname("Pérez");

        Patient patient = new Patient();
        patient.setId(2);
        patient.setFirstName("María");
        patient.setFirstSurname("López");

        appointment = new Appointment(
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(9, 30),
                AppointmentStatus.AGENDADA,
                doctor,
                patient
        );
        appointment.setReason("Dolor de cabeza fuerte");
    }

    @Test
    public void testUrgentAppointmentSchedulingExitoso() {
        // El método plantilla debe ejecutar todos los pasos y retornar la cita
        Appointment result = urgentScheduling.scheduleAppointment(appointment);
        assertNotNull(result, "La cita urgente no debe ser null");
        assertEquals("Dolor de cabeza fuerte", result.getReason());
    }

    @Test
    public void testUrgentAppointmentSinMedicoLanzaExcepcion() {
        appointment.setDoctor(null);
        assertThrows(IllegalArgumentException.class, () -> {
            urgentScheduling.scheduleAppointment(appointment);
        });
    }

    @Test
    public void testUrgentAppointmentSinMotivoLanzaExcepcion() {
        appointment.setReason(null);
        IllegalArgumentException IllegalArgumentException = assertThrows(IllegalArgumentException.class, () -> {
            urgentScheduling.scheduleAppointment(appointment);
        });
        /*assertThrows*/
    }
}
