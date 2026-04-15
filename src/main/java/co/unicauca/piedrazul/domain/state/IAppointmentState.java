package co.unicauca.piedrazul.domain.state;

import co.unicauca.piedrazul.domain.entities.Appointment;

public interface IAppointmentState {
    void confirmar(Appointment appointment);
    void atender(Appointment appointment);
    void cancelar(Appointment appointment);
    void reagendar(Appointment appointment);
    void noAsistio(Appointment appointment);
    String getNombre();
}