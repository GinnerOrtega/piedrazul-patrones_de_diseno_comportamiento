package co.unicauca.piedrazul.domain.state;

import co.unicauca.piedrazul.domain.entities.Appointment;

public class AtendidaState implements IAppointmentState {

    @Override
    public void confirmar(Appointment appointment) {
        throw new IllegalStateException("No se puede confirmar una cita ya atendida.");
    }

    @Override
    public void atender(Appointment appointment) {
        throw new IllegalStateException("La cita ya fue atendida.");
    }

    @Override
    public void cancelar(Appointment appointment) {
        throw new IllegalStateException("No se puede cancelar una cita ya atendida.");
    }

    @Override
    public void reagendar(Appointment appointment) {
        throw new IllegalStateException("No se puede reagendar una cita ya atendida.");
    }

    @Override
    public void noAsistio(Appointment appointment) {
        throw new IllegalStateException("No se puede registrar inasistencia en una cita ya atendida.");
    }

    @Override
    public String getNombre() {
        return "Atendida";
    }
}