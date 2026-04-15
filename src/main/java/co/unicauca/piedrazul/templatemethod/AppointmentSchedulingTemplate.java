package co.unicauca.piedrazul.templatemethod;

import co.unicauca.piedrazul.domain.entities.Appointment;

/**
 *
 * @author GINNER
 */
public abstract class AppointmentSchedulingTemplate {

    /**
     * Método plantilla — define el orden fijo del algoritmo.Es final para que
     * ninguna subclase pueda cambiar el orden de los pasos.
     *
     * @param appointment
     * @return
     */
    public final Appointment scheduleAppointment(Appointment appointment) {
        validateData(appointment);
        checkAvailability(appointment);
        Appointment scheduled = createAppointment(appointment);
        notifyConfirmation(scheduled);
        return scheduled;
    }

    /**
     * Paso 1: Validar los datos de la cita.Cada tipo de cita puede tener reglas
     * distintas.
     *
     * @param appointment
     */
    protected abstract void validateData(Appointment appointment);

    /**
     * Paso 2: Verificar disponibilidad del médico.Puede variar según si es
     * urgente o manual.
     *
     * @param appointment
     */
    protected abstract void checkAvailability(Appointment appointment);

    /**
     * Paso 3: Persistir la cita.Común para todos, pero puede variar en lógica
     * de negocio.
     *
     * @param appointment
     * @return
     */
    protected abstract Appointment createAppointment(Appointment appointment);

    /**
     * Paso 4: Notificar la confirmación.Hook con comportamiento por defecto —
     * las subclases pueden sobreescribirlo o dejarlo como está.
     *
     * @param appointment
     */
    protected void notifyConfirmation(Appointment appointment) {
        System.out.println("Cita agendada exitosamente. ID: "
                + appointment.getAppointmentId());
    }
}
