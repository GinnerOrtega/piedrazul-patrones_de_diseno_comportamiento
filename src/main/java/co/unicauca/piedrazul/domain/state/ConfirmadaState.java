package co.unicauca.piedrazul.domain.state;

import co.unicauca.piedrazul.domain.entities.Appointment;

public class ConfirmadaState implements IAppointmentState {

    @Override
    public void confirmar(Appointment appointment) {
        throw new IllegalStateException("La cita ya está confirmada.");
    }

    @Override
    public void atender(Appointment appointment) {
        appointment.setState(new AtendidaState());
        System.out.println("[State] Cita marcada como atendida.");
    }

    @Override
    public void cancelar(Appointment appointment) {
        appointment.setState(new CanceladaState());
        System.out.println("[State] Cita cancelada.");
    }

    @Override
    public void reagendar(Appointment appointment) {
        appointment.setState(new ReagendadaState());
        System.out.println("[State] Cita reagendada.");
    }

    @Override
    public void noAsistio(Appointment appointment) {
        appointment.setState(new NoAsistioState());
        System.out.println("[State] Inasistencia registrada.");
    }

    @Override
    public String getNombre() {
        return "Confirmada";
    }
}