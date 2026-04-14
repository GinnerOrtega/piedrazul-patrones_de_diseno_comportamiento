import co.unicauca.piedrazul.domain.entities.Appointment;
import co.unicauca.piedrazul.domain.entities.Doctor;
import co.unicauca.piedrazul.domain.entities.Patient;
import co.unicauca.piedrazul.domain.entities.enums.AppointmentStatus;
import co.unicauca.piedrazul.domain.facade.AppointmentFacade;
import co.unicauca.piedrazul.domain.services.interfaces.IAuditService;
import co.unicauca.piedrazul.domain.services.interfaces.IAppointmentService;
import co.unicauca.piedrazul.domain.services.interfaces.INotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Patron Facade - AppointmentFacade")
class AppointmentFacadeTest {

    // Los tres servicios que usa la Facade — se reemplazan por mocks
    // para no depender de base de datos, emails, ni logs reales
    @Mock
    private IAppointmentService appointmentService;

    @Mock
    private INotificationService notification;

    @Mock
    private IAuditService audit;

    private AppointmentFacade facade;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        // Se construye la Facade inyectando los mocks en vez de servicios reales
        facade = new AppointmentFacade(appointmentService, notification, audit);

        // Doctor y paciente de prueba
        Doctor doctor = new Doctor();
        doctor.setId(1002);
        doctor.setFirstName("Carlos");
        doctor.setFirstSurname("Garcia");

        Patient patient = new Patient();
        patient.setId(2002); // este ID se verifica en el test de auditoría
        patient.setFirstName("Ana");
        patient.setFirstSurname("Lopez");

        // Cita base que se reutiliza en todos los tests
        appointment = new Appointment(
                LocalDate.of(2026, 4, 20),
                LocalTime.of(9, 0),
                LocalTime.of(9, 30),
                AppointmentStatus.AGENDADA,
                doctor,
                patient
        );
        appointment.setReason("Consulta de control general");
    }

    // ── createAppointment - caso exitoso ──────────────────────────────────────

    @Test
    @DisplayName("createAppointment invoca scheduleAppointment exactamente una vez")
    void createAppointment_callsSchedulerOnce() {
        // El scheduler retorna true — la cita se agendó bien
        when(appointmentService.scheduleAppointment(appointment)).thenReturn(true);
        facade.createAppointment(appointment);
        // Confirmamos que el scheduler fue llamado una sola vez con la cita correcta
        verify(appointmentService, times(1)).scheduleAppointment(appointment);
    }

    @Test
    @DisplayName("createAppointment invoca notifyUser exactamente una vez")
    void createAppointment_callsNotificationOnce() {
        when(appointmentService.scheduleAppointment(appointment)).thenReturn(true);
        facade.createAppointment(appointment);
        // Si el agendamiento fue exitoso, se debe notificar al usuario
        verify(notification, times(1)).notifyUser(appointment);
    }

    @Test
    @DisplayName("createAppointment invoca audit.register exactamente una vez")
    void createAppointment_callsAuditOnce() {
        when(appointmentService.scheduleAppointment(appointment)).thenReturn(true);
        facade.createAppointment(appointment);
        // Y también debe quedar registro en auditoría
        verify(audit, times(1)).register(anyString());
    }

    @Test
    @DisplayName("createAppointment pasa la cita correcta al scheduler")
    void createAppointment_passesCorrectAppointmentToScheduler() {
        when(appointmentService.scheduleAppointment(appointment)).thenReturn(true);
        facade.createAppointment(appointment);
        // No basta con que se llame — debe recibir la cita exacta, no cualquier objeto
        verify(appointmentService).scheduleAppointment(appointment);
    }

    @Test
    @DisplayName("createAppointment pasa la cita correcta a notification")
    void createAppointment_passesCorrectAppointmentToNotification() {
        when(appointmentService.scheduleAppointment(appointment)).thenReturn(true);
        facade.createAppointment(appointment);
        verify(notification).notifyUser(appointment);
    }

    @Test
    @DisplayName("createAppointment registra en auditoria con el ID del paciente")
    void createAppointment_auditMessageContainsPatientId() {
        when(appointmentService.scheduleAppointment(appointment)).thenReturn(true);
        facade.createAppointment(appointment);
        // El mensaje de auditoría debe incluir el ID del paciente (2002)
        // para poder rastrear quién agendó la cita
        verify(audit).register(contains("2002"));
    }

    @Test
    @DisplayName("createAppointment llama a los subsistemas en orden correcto")
    void createAppointment_callsSubsystemsInOrder() {
        when(appointmentService.scheduleAppointment(appointment)).thenReturn(true);
        // inOrder garantiza que los servicios se llamen en esta secuencia exacta:
        // primero agendar, luego notificar, por último auditar
        var inOrder = inOrder(appointmentService, notification, audit);
        facade.createAppointment(appointment);
        inOrder.verify(appointmentService).scheduleAppointment(appointment);
        inOrder.verify(notification).notifyUser(appointment);
        inOrder.verify(audit).register(anyString());
    }

    @Test
    @DisplayName("createAppointment no genera interacciones extra en los mocks")
    void createAppointment_noExtraInteractions() {
        when(appointmentService.scheduleAppointment(appointment)).thenReturn(true);
        facade.createAppointment(appointment);
        // Primero verificamos las 3 llamadas esperadas...
        verify(appointmentService).scheduleAppointment(appointment);
        verify(notification).notifyUser(appointment);
        verify(audit).register(anyString());
        // ...luego confirmamos que no hubo ninguna llamada adicional
        verifyNoMoreInteractions(appointmentService, notification, audit);
    }

    // ── createAppointment - caso fallido ──────────────────────────────────────

    @Test
    @DisplayName("createAppointment NO notifica si el scheduler falla")
    void createAppointment_doesNotNotifyIfSchedulerFails() {
        // Si el scheduler falla (retorna false), no tiene sentido notificar
        when(appointmentService.scheduleAppointment(appointment)).thenReturn(false);
        facade.createAppointment(appointment);
        // Verifica que el metodo de notification no se haya invocado
        verify(notification, never()).notifyUser(appointment);
    }

    @Test
    @DisplayName("createAppointment NO audita si el scheduler falla")
    void createAppointment_doesNotAuditIfSchedulerFails() {
        // Tampoco debe quedar registro si la cita nunca se creó
        when(appointmentService.scheduleAppointment(appointment)).thenReturn(false);
        facade.createAppointment(appointment);
        // Verifica que el metodo de register no se haya invocado
        verify(audit, never()).register(anyString());
    }

    // ── cancelAppointment - caso exitoso ──────────────────────────────────────

    @Test
    @DisplayName("cancelAppointment invoca cancelAppointment en el service exactamente una vez")
    void cancelAppointment_callsServiceOnce() {
        when(appointmentService.cancelAppointment(appointment.getAppointmentId())).thenReturn(true);
        facade.cancelAppointment(appointment);
        // El service debe recibir el ID de la cita, no el objeto completo
        verify(appointmentService, times(1)).cancelAppointment(appointment.getAppointmentId());
    }

    @Test
    @DisplayName("cancelAppointment invoca notifyUser exactamente una vez")
    void cancelAppointment_callsNotificationOnce() {
        when(appointmentService.cancelAppointment(appointment.getAppointmentId())).thenReturn(true);
        facade.cancelAppointment(appointment);
        verify(notification, times(1)).notifyUser(appointment);
    }

    @Test
    @DisplayName("cancelAppointment invoca audit.register exactamente una vez")
    void cancelAppointment_callsAuditOnce() {
        when(appointmentService.cancelAppointment(appointment.getAppointmentId())).thenReturn(true);
        facade.cancelAppointment(appointment);
        verify(audit, times(1)).register(anyString());
    }

    @Test
    @DisplayName("cancelAppointment registra en auditoria con el ID de la cita")
    void cancelAppointment_auditMessageContainsAppointmentId() {
        appointment.setAppointmentId(5);
        when(appointmentService.cancelAppointment(5)).thenReturn(true);
        facade.cancelAppointment(appointment);
        // El mensaje debe incluir el ID 5 para identificar qué cita se canceló
        verify(audit).register(contains("5"));
    }

    @Test
    @DisplayName("cancelAppointment llama a notification antes que audit")
    void cancelAppointment_notificationBeforeAudit() {
        when(appointmentService.cancelAppointment(appointment.getAppointmentId())).thenReturn(true);
        // Primero se avisa al usuario, después se deja el registro
        var inOrder = inOrder(notification, audit);
        facade.cancelAppointment(appointment);
        inOrder.verify(notification).notifyUser(appointment);
        inOrder.verify(audit).register(anyString());
    }

    @Test
    @DisplayName("cancelAppointment no genera interacciones extra en los mocks")
    void cancelAppointment_noExtraInteractions() {
        when(appointmentService.cancelAppointment(appointment.getAppointmentId())).thenReturn(true);
        facade.cancelAppointment(appointment);
        verify(appointmentService).cancelAppointment(appointment.getAppointmentId());
        verify(notification).notifyUser(appointment);
        verify(audit).register(anyString());
        verifyNoMoreInteractions(appointmentService, notification, audit);
    }

    // ── cancelAppointment - caso fallido ──────────────────────────────────────

    @Test
    @DisplayName("cancelAppointment NO notifica si el service falla")
    void cancelAppointment_doesNotNotifyIfServiceFails() {
        // Si la cancelación falló, no se debe notificar al usuario
        when(appointmentService.cancelAppointment(appointment.getAppointmentId())).thenReturn(false);
        facade.cancelAppointment(appointment);
        verify(notification, never()).notifyUser(appointment);
    }

    @Test
    @DisplayName("cancelAppointment NO audita si el service falla")
    void cancelAppointment_doesNotAuditIfServiceFails() {
        // Ni auditar — no ocurrió nada que valga la pena registrar
        when(appointmentService.cancelAppointment(appointment.getAppointmentId())).thenReturn(false);
        facade.cancelAppointment(appointment);
        verify(audit, never()).register(anyString());
    }
}