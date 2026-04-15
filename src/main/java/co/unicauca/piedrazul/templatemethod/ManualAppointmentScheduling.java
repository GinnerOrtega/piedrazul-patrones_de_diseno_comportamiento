package co.unicauca.piedrazul.templatemethod;

import co.unicauca.piedrazul.domain.access.IAppointmentRepository;
import co.unicauca.piedrazul.domain.entities.Appointment;
import co.unicauca.piedrazul.domain.services.AvailabilityService;
import co.unicauca.piedrazul.domain.services.validators.ManualAppointmentValidator;
import java.util.List;

/**
 * Implementación concreta del Template Method para citas manuales.
 *
 * @author GINNER
 */
public class ManualAppointmentScheduling extends AppointmentSchedulingTemplate {

    private final IAppointmentRepository appointmentRepo;
    private final AvailabilityService availabilityService;
    private final ManualAppointmentValidator validator;

    public ManualAppointmentScheduling(
            IAppointmentRepository appointmentRepo,
            AvailabilityService availabilityService,
            ManualAppointmentValidator validator) {
        this.appointmentRepo = appointmentRepo;
        this.availabilityService = availabilityService;
        this.validator = validator;
    }

    @Override
    protected void validateData(Appointment appointment) {
        List<Appointment> existing = appointmentRepo
                .findByDoctorAndDate(
                        appointment.getDoctor().getId(),
                        appointment.getDate().toString()
                );
        validator.validate(
                appointment,
                appointment.getDoctor(),
                appointment.getPatient(),
                existing
        );
    }

    @Override
    protected void checkAvailability(Appointment appointment) {
        List available = availabilityService.getAvailableSlots(
                appointment.getDoctor().getId(),
                appointment.getDate()
        );
        if (available == null || available.isEmpty()) {
            throw new IllegalStateException(
                    "No hay horarios disponibles para el médico en la fecha seleccionada."
            );
        }
    }

    @Override
    protected Appointment createAppointment(Appointment appointment) {
        boolean saved = appointmentRepo.save(appointment);
        if (!saved) {
            throw new IllegalStateException("No se pudo guardar la cita en la base de datos.");
        }
        return appointment;
    }

    @Override
    protected void notifyConfirmation(Appointment appointment) {
        System.out.println("Cita manual confirmada para el paciente: "
                + appointment.getPatient().getFullName()
                + " con el doctor: "
                + appointment.getDoctor().getFullName());
    }
}
