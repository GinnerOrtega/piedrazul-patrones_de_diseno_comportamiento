package co.unicauca.piedrazul.domain.state;

import co.unicauca.piedrazul.domain.entities.Appointment;

public class CreadaState implements IAppointmentState {

    @Override
    public void confirmar(Appointment appointment) {
        appointment.setState(new ConfirmadaState());
        System.out.println("[State] Cita confirmada.");
    }

    @Override
    public void atender(Appointment appointment) {
        throw new IllegalStateException("No se puede atender una cita en estado Creada.");
    }

    @Override
    public void cancelar(Appointment appointment) {
        throw new IllegalStateException("No se puede cancelar una cita en estado Creada.");
    }

    @Override
    public void reagendar(Appointment appointment) {
        throw new IllegalStateException("No se puede reagendar una cita en estado Creada.");
    }

    @Override
    public void noAsistio(Appointment appointment) {
        throw new IllegalStateException("No se puede registrar inasistencia en estado Creada.");
    }

    @Override
    public String getNombre() {
        return "Creada";
    }
}