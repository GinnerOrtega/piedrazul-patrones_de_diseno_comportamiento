package co.unicauca.piedrazul.domain.state;

import co.unicauca.piedrazul.domain.entities.Appointment;

public class ReagendadaState implements IAppointmentState {

    @Override
    public void confirmar(Appointment appointment) {
        appointment.setState(new ConfirmadaState());
        System.out.println("[State] Cita reagendada confirmada.");
    }

    @Override
    public void atender(Appointment appointment) {
        throw new IllegalStateException("No se puede atender una cita en estado Reagendada.");
    }

    @Override
    public void cancelar(Appointment appointment) {
        throw new IllegalStateException("No se puede cancelar una cita en estado Reagendada.");
    }

    @Override
    public void reagendar(Appointment appointment) {
        throw new IllegalStateException("La cita ya fue reagendada.");
    }

    @Override
    public void noAsistio(Appointment appointment) {
        throw new IllegalStateException("No se puede registrar inasistencia en estado Reagendada.");
    }

    @Override
    public String getNombre() {
        return "Reagendada";
    }
}