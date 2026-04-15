package co.unicauca.piedrazul.domain.services.interfaces;

import co.unicauca.piedrazul.domain.entities.Appointment;

/**
 * Contrato del subsistema de notificaciones.
 */
public interface INotificationService {
    void notifyUser(Appointment appointment);
}
