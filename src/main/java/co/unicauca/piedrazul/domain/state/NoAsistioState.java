package co.unicauca.piedrazul.domain.state;

import co.unicauca.piedrazul.domain.entities.Appointment;

public class NoAsistioState implements IAppointmentState {

    @Override
    public void confirmar(Appointment appointment) {
        throw new IllegalStateException("No se puede confirmar una cita con inasistencia registrada.");
    }

    @Override
    public void atender(Appointment appointment) {
        throw new IllegalStateException("No se puede atender una cita con inasistencia registrada.");
    }

    @Override
    public void cancelar(Appointment appointment) {
        throw new IllegalStateException("No se puede cancelar una cita con inasistencia registrada.");
    }

    @Override
    public void reagendar(Appointment appointment) {
        throw new IllegalStateException("No se puede reagendar una cita con inasistencia registrada.");
    }

    @Override
    public void noAsistio(Appointment appointment) {
        throw new IllegalStateException("La inasistencia ya fue registrada.");
    }

    @Override
    public String getNombre() {
        return "NoAsistio";
    }
}