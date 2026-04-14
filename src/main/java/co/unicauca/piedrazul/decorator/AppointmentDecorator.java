package co.unicauca.piedrazul.decorator;

import co.unicauca.piedrazul.domain.entities.Appointment;
import co.unicauca.piedrazul.domain.entities.Doctor;
import co.unicauca.piedrazul.domain.entities.Patient;
import co.unicauca.piedrazul.domain.entities.enums.AppointmentStatus;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Clase abstracta base del patrón Decorator.
 * Extiende Appointment y envuelve otra instancia de Appointment,
 * delegando todos sus métodos a la cita decorada.
 * Las subclases concretas sobreescriben solo lo que necesitan ampliar.
 */
public abstract class AppointmentDecorator extends Appointment {

    // La cita real que se está decorando
    protected final Appointment appointment;

    public AppointmentDecorator(Appointment appointment) {
        this.appointment = appointment;
    }

    // ── Delegación completa al objeto envuelto ──────────────────────────────

    @Override
    public int getAppointmentId() {
        return appointment.getAppointmentId();
    }

    @Override
    public LocalDate getDate() {
        return appointment.getDate();
    }

    @Override
    public LocalTime getStartTime() {
        return appointment.getStartTime();
    }

    @Override
    public LocalTime getEndTime() {
        return appointment.getEndTime();
    }

    @Override
    public AppointmentStatus getStatus() {
        return appointment.getStatus();
    }

    @Override
    public Doctor getDoctor() {
        return appointment.getDoctor();
    }

    @Override
    public Patient getPatient() {
        return appointment.getPatient();
    }

    /**
     * Descripción base delegada. Las subclases la sobreescriben
     * para añadir información extra.
     * @return 
     */
    @Override
    public String getDescription() {
        return appointment.getDescription();
    }
}