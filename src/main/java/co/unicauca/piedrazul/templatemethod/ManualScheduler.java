package co.unicauca.piedrazul.templatemethod;

import co.unicauca.piedrazul.domain.entities.Appointment;

/**
 * Estrategia de agendamiento manual, realizada por un médico o agendador.
 * Aplica validaciones más estrictas antes de confirmar la cita.
 */
public class ManualScheduler extends AppointmentScheduler {

    @Override
    protected void validateUser(Appointment appointment) {
        if (appointment.getPatient() == null) {
            throw new IllegalArgumentException("El paciente es obligatorio.");
        }
        if (appointment.getDoctor() == null) {
            throw new IllegalArgumentException("El médico debe especificarse en el agendamiento manual.");
        }
        if (appointment.getReason() == null || appointment.getReason().isBlank()) {
            throw new IllegalArgumentException("El motivo de consulta es obligatorio.");
        }
        System.out.println("[ManualScheduler] Usuario validado manualmente: "
                + appointment.getPatient().getFullName());
    }

    @Override
    protected void checkAvailability(Appointment appointment) {
        if (appointment.getStartTime() == null || appointment.getEndTime() == null) {
            throw new IllegalArgumentException("El horario de la cita debe estar definido.");
        }
        System.out.println("[ManualScheduler] Disponibilidad verificada manualmente para: "
                + appointment.getDate() + " " + appointment.getStartTime()
                + " - " + appointment.getEndTime());
    }

    @Override
    protected void assignProfessional(Appointment appointment) {
        System.out.println("[ManualScheduler] Profesional asignado por agendador: "
                + appointment.getDoctor().getFullName());
    }

    @Override
    protected void confirmAppointment(Appointment appointment) {
        appointment.confirmar();
        System.out.println("[ManualScheduler] Cita confirmada por agendador. Estado: "
                + appointment.getState().getNombre());
    }
}