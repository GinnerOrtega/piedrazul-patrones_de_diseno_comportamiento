package co.unicauca.piedrazul.templatemethod;

import co.unicauca.piedrazul.domain.entities.Appointment;

/**
 * Clase template que define un flujo común de agendamiento de citas,
 * permitiendo que distintas estrategias de agendamiento personalicen algunos pasos.
 */
public abstract class AppointmentScheduler {

    public final void schedule(Appointment appointment) {
        validateUser(appointment);
        checkAvailability(appointment);
        assignProfessional(appointment);
        confirmAppointment(appointment);
    }

    protected abstract void validateUser(Appointment appointment);

    protected abstract void checkAvailability(Appointment appointment);

    protected abstract void assignProfessional(Appointment appointment);

    protected abstract void confirmAppointment(Appointment appointment);
}