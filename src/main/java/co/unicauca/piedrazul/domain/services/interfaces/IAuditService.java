package co.unicauca.piedrazul.domain.services.interfaces;

/**
 * Contrato del subsistema de auditoría.
 */
public interface IAuditService {
    void register(String event);
}