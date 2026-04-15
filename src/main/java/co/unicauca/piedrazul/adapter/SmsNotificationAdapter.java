package co.unicauca.piedrazul.adapter;

import co.unicauca.piedrazul.domain.entities.Appointment;
import co.unicauca.piedrazul.domain.services.interfaces.INotificationService;

/**
 * Adapter que convierte SmsNotificationService (interfaz incompatible) a
 * INotificationService que espera el dominio.
 *
 * @author GINNER
 */
public class SmsNotificationAdapter implements INotificationService {

    private final SmsNotificationService smsService;

    public SmsNotificationAdapter(SmsNotificationService smsService) {
        this.smsService = smsService;
    }

    @Override
    public void notifyUser(Appointment appointment) {
        // Traduce la llamada del dominio al método del sistema externo
        String phone   = appointment.getPatient().getPhone();
        String message = "Piedrazul: Cita confirmada con Dr. "
                + appointment.getDoctor().getFullName()
                + " el " + appointment.getDate()
                + " a las " + appointment.getStartTime()
                + ". Motivo: " + appointment.getReason();

        smsService.sendSms(phone, message);
    }
}
