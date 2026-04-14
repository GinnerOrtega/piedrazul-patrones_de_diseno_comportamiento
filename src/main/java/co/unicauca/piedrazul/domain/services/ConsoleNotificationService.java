/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.piedrazul.domain.services;

import co.unicauca.piedrazul.domain.entities.Appointment;
import co.unicauca.piedrazul.domain.services.interfaces.INotificationService;

public class ConsoleNotificationService implements INotificationService {

    @Override
    public void notifyUser(Appointment appointment) {
        System.out.println("[Notificación] Paciente: "
                + appointment.getPatient().getFullName()
                + " — Cita el " + appointment.getDate()
                + " a las " + appointment.getStartTime());
    }
    
}
