package co.unicauca.piedrazul.domain.state;

import co.unicauca.piedrazul.domain.entities.Appointment;

public class PendienteConfirmacionState implements IAppointmentState {

    @Override
    public void confirmar(Appointment appointment) {
        appointment.setState(new ConfirmadaState());
        System.out.println("[State] Cita confirmada desde pendiente.");
    }

    @Override
    public void atender(Appointment appointment) {
        throw new IllegalStateException("No se puede atender una cita pendiente de confirmación.");
    }

    @Override
    public void cancelar(Appointment appointment) {
        throw new IllegalStateException("No se puede cancelar una cita pendiente de confirmación.");
    }

    @Override
    public void reagendar(Appointment appointment) {
        throw new IllegalStateException("No se puede reagendar una cita pendiente de confirmación.");
    }

    @Override
    public void noAsistio(Appointment appointment) {
        throw new IllegalStateException("No se puede registrar inasistencia en estado PendienteConfirmacion.");
    }

    @Override
    public String getNombre() {
        return "PendienteConfirmacion";
    }
}