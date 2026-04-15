package co.unicauca.piedrazul.domain.state;

import co.unicauca.piedrazul.domain.entities.Appointment;

public class CanceladaState implements IAppointmentState {

    @Override
    public void confirmar(Appointment appointment) {
        throw new IllegalStateException("No se puede confirmar una cita cancelada.");
    }

    @Override
    public void atender(Appointment appointment) {
        throw new IllegalStateException("No se puede atender una cita cancelada.");
    }

    @Override
    public void cancelar(Appointment appointment) {
        throw new IllegalStateException("La cita ya está cancelada.");
    }

    @Override
    public void reagendar(Appointment appointment) {
        throw new IllegalStateException("No se puede reagendar una cita cancelada.");
    }

    @Override
    public void noAsistio(Appointment appointment) {
        throw new IllegalStateException("No se puede registrar inasistencia en una cita cancelada.");
    }

    @Override
    public String getNombre() {
        return "Cancelada";
    }
}