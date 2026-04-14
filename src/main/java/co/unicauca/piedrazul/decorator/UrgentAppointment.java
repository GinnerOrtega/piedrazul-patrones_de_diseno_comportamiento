package co.unicauca.piedrazul.decorator;

import co.unicauca.piedrazul.domain.entities.Appointment;

/**
 * Decorador concreto que marca una cita como URGENTE.
 * Se puede encadenar con otros decoradores.
 */
public class UrgentAppointment extends AppointmentDecorator {

    public UrgentAppointment(Appointment appointment) {
        super(appointment);
    }

    @Override
    public String getDescription() {
        return "[URGENTE] " + appointment.getDescription();
    }
}