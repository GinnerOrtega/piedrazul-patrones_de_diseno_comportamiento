package co.unicauca.piedrazul.adapter;

/**
 * Clase incompatible que retorna un paciente en formato JSON. Es el adaptado.
 */
public class ExternalService {

    public String getPatientData() {
        return "{\"name\":\"Jose Lopez\"}";
    }
}