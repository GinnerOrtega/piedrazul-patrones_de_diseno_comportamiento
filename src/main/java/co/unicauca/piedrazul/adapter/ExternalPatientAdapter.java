package co.unicauca.piedrazul.adapter;

import co.unicauca.piedrazul.domain.entities.Patient;
import org.json.JSONObject;

/**
 * Adapter que convierte ExternalService (interfaz incompatible) a
 * PatientDataProvider que espera el dominio.
 */
public class ExternalPatientAdapter implements PatientDataProvider {

    private final ExternalService externalService;

    public ExternalPatientAdapter(ExternalService externalService) {
        this.externalService = externalService;
    }

    @Override
    public Patient getPatient() {
        String json = externalService.getPatientData();
        JSONObject jsonObject = new JSONObject(json);

        Patient patient = new Patient();
        String fullName = jsonObject.getString("name");
        String[] parts = fullName.split(" ", 2);
        patient.setFirstName(parts[0]);
        if (parts.length > 1) {
            patient.setFirstSurname(parts[1]);
        }
        return patient;
    }
}