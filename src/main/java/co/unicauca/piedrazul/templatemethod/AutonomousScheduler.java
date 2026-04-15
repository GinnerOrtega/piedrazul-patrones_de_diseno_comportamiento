package co.unicauca.piedrazul.templatemethod;

import co.unicauca.piedrazul.domain.entities.Appointment;

/**
 * Estrategia de agendamiento automático. El sistema asigna disponibilidad
 * y profesional sin intervención manual.
 */
public class AutonomousScheduler extends AppointmentScheduler {

    @Override
    protected void validateUser(Appointment appointment) {
        if (appointment.getPatient() == null) {
            throw new IllegalArgumentException("El paciente es obligatorio para el agendamiento autónomo.");
        }
        if (appointment.getReason() == null || appointment.getReason().isBlank()) {
            throw new IllegalArgumentException("El motivo de consulta es obligatorio.");
        }
        System.out.println("[AutonomousScheduler] Usuario validado automáticamente: "
                + appointment.getPatient().getFullName());
    }

    @Override
    protected void checkAvailability(Appointment appointment) {
        System.out.println("[AutonomousScheduler] Disponibilidad verificada automáticamente para: "
                + appointment.getDate() + " a las " + appointment.getStartTime());
    }

    @Override
    protected void assignProfessional(Appointment appointment) {
        if (appointment.getDoctor() == null) {
            throw new IllegalArgumentException("No hay médico disponible para asignar automáticamente.");
        }
        System.out.println("[AutonomousScheduler] Profesional asignado automáticamente: "
                + appointment.getDoctor().getFullName());
    }

    @Override
    protected void confirmAppointment(Appointment appointment) {
        appointment.confirmar();
        System.out.println("[AutonomousScheduler] Cita confirmada automáticamente. Estado: "
                + appointment.getState().getNombre());
    }
}