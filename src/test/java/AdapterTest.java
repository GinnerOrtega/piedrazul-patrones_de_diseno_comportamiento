
/**
 *
 * @author GINNER
 */
import co.unicauca.piedrazul.adapter.EmailNotificationAdapter;
import co.unicauca.piedrazul.adapter.EmailNotificationService;
import co.unicauca.piedrazul.adapter.SmsNotificationAdapter;
import co.unicauca.piedrazul.adapter.SmsNotificationService;
import co.unicauca.piedrazul.domain.entities.Appointment;
import co.unicauca.piedrazul.domain.entities.Doctor;
import co.unicauca.piedrazul.domain.entities.Patient;
import co.unicauca.piedrazul.domain.entities.enums.AppointmentStatus;
import co.unicauca.piedrazul.domain.services.interfaces.INotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class AdapterTest {

    private Appointment appointment;

    @BeforeEach
    public void setUp() {
        Doctor doctor = new Doctor();
        doctor.setId(1);
        doctor.setFirstName("Carlos");
        doctor.setFirstSurname("Pérez");

        Patient patient = new Patient();
        patient.setId(2);
        patient.setFirstName("María");
        patient.setFirstSurname("López");
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

    @Test
    public void testEmailAdapterImplementaInterfaz() {
        // El adapter debe poder usarse como INotificationService
        INotificationService notifier = new EmailNotificationAdapter(
                new EmailNotificationService()
        );
        assertInstanceOf(INotificationService.class, notifier);
    }

    @Test
    public void testSmsAdapterImplementaInterfaz() {
        // El adapter debe poder usarse como INotificationService
        INotificationService notifier = new SmsNotificationAdapter(
                new SmsNotificationService()
        );
        assertInstanceOf(INotificationService.class, notifier);
    }

    @Test
    public void testEmailAdapterNotifyUserNoLanzaExcepcion() {
        // notifyUser() no debe lanzar ninguna excepción con datos válidos
        INotificationService notifier = new EmailNotificationAdapter(
                new EmailNotificationService()
        );
        assertDoesNotThrow(() -> notifier.notifyUser(appointment));
    }

    @Test
    public void testSmsAdapterNotifyUserNoLanzaExcepcion() {
        // notifyUser() no debe lanzar ninguna excepción con datos válidos
        INotificationService notifier = new SmsNotificationAdapter(
                new SmsNotificationService()
        );
        assertDoesNotThrow(() -> notifier.notifyUser(appointment));
    }

    @Test
    public void testPolimorfismoAdapters() {
        // Ambos adapters deben comportarse igual desde INotificationService
        INotificationService email = new EmailNotificationAdapter(new EmailNotificationService());
        INotificationService sms = new SmsNotificationAdapter(new SmsNotificationService());

        // Ninguno debe lanzar excepción — el dominio los trata igual
        assertDoesNotThrow(() -> email.notifyUser(appointment));
        assertDoesNotThrow(() -> sms.notifyUser(appointment));
    }
}
