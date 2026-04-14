// domain/facade/AppointmentFacade.java
package co.unicauca.piedrazul.domain.facade;

import co.unicauca.piedrazul.domain.services.interfaces.IAuditService;
import co.unicauca.piedrazul.domain.services.interfaces.INotificationService;
import co.unicauca.piedrazul.domain.entities.Appointment;
import co.unicauca.piedrazul.domain.services.interfaces.IAppointmentService;

/**
 * Fachada que simplifica la interacción entre los diferentes subsistemas
 * de la plataforma: agendamiento, notificaciones y auditoría.
 */
public class AppointmentFacade {

    private final IAppointmentService scheduler;
    private final INotificationService notification;
    private final IAuditService audit;

    public AppointmentFacade(IAppointmentService scheduler,
                             INotificationService notification,
                             IAuditService audit) {
        this.scheduler    = scheduler;
        this.notification = notification;
        this.audit        = audit;
    }

    public void createAppointment(Appointment appointment) {
        boolean scheduled = scheduler.scheduleAppointment(appointment);
        if (scheduled) {
            notification.notifyUser(appointment);
            audit.register("Cita creada para paciente ID: "
                    + appointment.getPatient().getId());
        }
    }

    public void cancelAppointment(Appointment appointment) {
        boolean cancelled = scheduler.cancelAppointment(appointment.getAppointmentId());
        if (cancelled) {
            notification.notifyUser(appointment);
            audit.register("Cita cancelada, ID: " + appointment.getAppointmentId());
        }
    }
}