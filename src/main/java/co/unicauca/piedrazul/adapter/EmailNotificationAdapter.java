package co.unicauca.piedrazul.adapter;

import co.unicauca.piedrazul.domain.entities.Appointment;
import co.unicauca.piedrazul.domain.services.interfaces.INotificationService;

/**
 * Adapter que convierte EmailNotificationService (interfaz incompatible) a
 * INotificationService que espera el dominio.
 *
 * @author GINNER
 */
public class EmailNotificationAdapter implements INotificationService {

    private final EmailNotificationService emailService;

    public EmailNotificationAdapter(EmailNotificationService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void notifyUser(Appointment appointment) {
        // Traduce la llamada del dominio al método del sistema externo
        String recipient = appointment.getPatient().getFullName();
        String subject = "Confirmación de cita médica — Piedrazul";
        String body = "Estimado/a " + appointment.getPatient().getFullName()
                + ", su cita con el Dr. " + appointment.getDoctor().getFullName()
                + " fue confirmada para el " + appointment.getDate()
                + " a las " + appointment.getStartTime()
                + ". Motivo: " + appointment.getReason();

        emailService.sendEmail(recipient, subject, body);
    }
}
