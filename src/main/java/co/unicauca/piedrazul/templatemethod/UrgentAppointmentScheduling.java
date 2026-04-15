package co.unicauca.piedrazul.templatemethod;

import co.unicauca.piedrazul.domain.access.IAppointmentRepository;
import co.unicauca.piedrazul.domain.entities.Appointment;

/**
 * Implementación concreta del Template Method para citas urgentes. Omite la
 * verificación estricta de disponibilidad.
 *
 * @author GINNER
 */
public class UrgentAppointmentScheduling extends AppointmentSchedulingTemplate {

    private final IAppointmentRepository appointmentRepo;

    public UrgentAppointmentScheduling(IAppointmentRepository appointmentRepo) {
        this.appointmentRepo = appointmentRepo;
    }

    @Override
    protected void validateData(Appointment appointment) {
        if (appointment.getDoctor() == null) {
            throw new IllegalArgumentException("La cita urgente debe tener un médico asignado.");
        }
        if (appointment.getPatient() == null) {
            throw new IllegalArgumentException("La cita urgente debe tener un paciente asignado.");
        }
        if (appointment.getReason() == null || appointment.getReason().isBlank()) {
            throw new IllegalArgumentException("El motivo de consulta es obligatorio.");
        }
    }

    @Override
    protected void checkAvailability(Appointment appointment) {
        // Las citas urgentes no verifican disponibilidad estricta
        System.out.println("Cita urgente: verificación de disponibilidad omitida.");
    }

    @Override
    protected Appointment createAppointment(Appointment appointment) {
        boolean saved = appointmentRepo.save(appointment);
        if (!saved) {
            throw new IllegalStateException("No se pudo guardar la cita urgente.");
        }
        return appointment;
    }

    @Override
    protected void notifyConfirmation(Appointment appointment) {
        System.out.println("⚠ CITA URGENTE confirmada para: "
                + appointment.getPatient().getFullName());
    }
}
