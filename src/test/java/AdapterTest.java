import co.unicauca.piedrazul.adapter.EmailNotificationAdapter;
import co.unicauca.piedrazul.adapter.EmailNotificationService;
import co.unicauca.piedrazul.adapter.ExternalPatientAdapter;
import co.unicauca.piedrazul.adapter.ExternalService;
import co.unicauca.piedrazul.adapter.PatientDataProvider;
import co.unicauca.piedrazul.adapter.SmsNotificationAdapter;
import co.unicauca.piedrazul.adapter.SmsNotificationService;
import co.unicauca.piedrazul.domain.entities.Appointment;
import co.unicauca.piedrazul.domain.entities.Doctor;
import co.unicauca.piedrazul.domain.entities.Patient;
import co.unicauca.piedrazul.domain.entities.enums.AppointmentStatus;
import co.unicauca.piedrazul.domain.services.interfaces.INotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Patron Adapter")
public class AdapterTest {

    private Appointment appointment;

    @BeforeEach
    public void setUp() {
        Doctor doctor = new Doctor();
        doctor.setId(1);
        doctor.setFirstName("Carlos");
        doctor.setFirstSurname("Perez");

        Patient patient = new Patient();
        patient.setId(2);
        patient.setFirstName("Maria");
        patient.setFirstSurname("Lopez");
        patient.setPhone("3001234567");

        appointment = new Appointment(
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(9, 30),
                AppointmentStatus.AGENDADA,
                doctor,
                patient
        );
        appointment.setReason("Control general");
    }

    // ── ExternalPatientAdapter ────────────────────────────────────────────────

    @Test
    @DisplayName("ExternalPatientAdapter implementa PatientDataProvider")
    public void externalPatientAdapter_implementaInterfaz() {
        PatientDataProvider provider = new ExternalPatientAdapter(new ExternalService());
        assertInstanceOf(PatientDataProvider.class, provider);
    }

    @Test
    @DisplayName("ExternalPatientAdapter retorna un paciente no nulo")
    public void externalPatientAdapter_retornaPacienteNoNulo() {
        PatientDataProvider provider = new ExternalPatientAdapter(new ExternalService());
        Patient patient = provider.getPatient();
        assertNotNull(patient, "El paciente no debe ser null");
    }

    @Test
    @DisplayName("ExternalPatientAdapter parsea correctamente el nombre del JSON")
    public void externalPatientAdapter_parseaNombreCorrectamente() {
        PatientDataProvider provider = new ExternalPatientAdapter(new ExternalService());
        Patient patient = provider.getPatient();
        assertEquals("Jose", patient.getFirstName(), "El nombre debe ser Jose");
        assertEquals("Lopez", patient.getFirstSurname(), "El apellido debe ser Lopez");
    }

    // ── EmailNotificationAdapter ──────────────────────────────────────────────

    @Test
    @DisplayName("EmailNotificationAdapter implementa INotificationService")
    public void emailAdapter_implementaInterfaz() {
        INotificationService notifier = new EmailNotificationAdapter(new EmailNotificationService());
        assertInstanceOf(INotificationService.class, notifier);
    }

    @Test
    @DisplayName("EmailNotificationAdapter no lanza excepcion con datos validos")
    public void emailAdapter_notifyUserNoLanzaExcepcion() {
        INotificationService notifier = new EmailNotificationAdapter(new EmailNotificationService());
        assertDoesNotThrow(() -> notifier.notifyUser(appointment));
    }

    // ── SmsNotificationAdapter ────────────────────────────────────────────────

    @Test
    @DisplayName("SmsNotificationAdapter implementa INotificationService")
    public void smsAdapter_implementaInterfaz() {
        INotificationService notifier = new SmsNotificationAdapter(new SmsNotificationService());
        assertInstanceOf(INotificationService.class, notifier);
    }

    @Test
    @DisplayName("SmsNotificationAdapter no lanza excepcion con datos validos")
    public void smsAdapter_notifyUserNoLanzaExcepcion() {
        INotificationService notifier = new SmsNotificationAdapter(new SmsNotificationService());
        assertDoesNotThrow(() -> notifier.notifyUser(appointment));
    }

    // ── Polimorfismo ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Email y SMS adapters se tratan igual desde INotificationService")
    public void polimorfismo_ambosFuncionanDesdeInterfaz() {
        INotificationService email = new EmailNotificationAdapter(new EmailNotificationService());
        INotificationService sms = new SmsNotificationAdapter(new SmsNotificationService());
        assertDoesNotThrow(() -> email.notifyUser(appointment));
        assertDoesNotThrow(() -> sms.notifyUser(appointment));
    }
}