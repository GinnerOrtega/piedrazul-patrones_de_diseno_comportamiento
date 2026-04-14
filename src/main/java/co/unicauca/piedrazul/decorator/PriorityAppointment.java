package co.unicauca.piedrazul.decorator;

import co.unicauca.piedrazul.domain.entities.Appointment;

/**
 * Decorador concreto que marca una cita como de ALTA PRIORIDAD.
 * Añade la etiqueta [PRIORIDAD ALTA] a la descripción de la cita
 * sin modificar la clase Appointment directamente.
 */
public class PriorityAppointment extends AppointmentDecorator {

    public PriorityAppointment(Appointment appointment) {
        super(appointment);
    }

    @Override
    public String getDescription() {
        return appointment.getDescription() + " [PRIORIDAD ALTA]";
    }
}