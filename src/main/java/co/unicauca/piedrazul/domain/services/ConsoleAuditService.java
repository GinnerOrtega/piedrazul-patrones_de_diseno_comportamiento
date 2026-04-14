package co.unicauca.piedrazul.domain.services;

import co.unicauca.piedrazul.domain.services.interfaces.IAuditService;
import java.time.LocalDateTime;

/**
 * Implementación de AuditService que imprime en consola con marca de tiempo.
 *
 */
public class ConsoleAuditService implements IAuditService {

    @Override
    public void register(String event) {
        System.out.println("[Auditoría] " + LocalDateTime.now() + " — " + event);
    }
}