package co.unicauca.piedrazul.decorator;

import co.unicauca.piedrazul.domain.entities.Appointment;
import co.unicauca.piedrazul.domain.entities.Doctor;
import co.unicauca.piedrazul.domain.entities.Patient;
import co.unicauca.piedrazul.domain.entities.enums.AppointmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Patron Decorator - AppointmentDecorator")
class DecoratorTest {

    private Appointment base;
    private Doctor doctor;
    private Patient patient;

    @BeforeEach
    void setUp() {
        // datos fijos para no repetirlos en cada test
        doctor = new Doctor();
        doctor.setId(1002);
        doctor.setFirstName("Carlos");
        doctor.setFirstSurname("Garcia");

        patient = new Patient();
        patient.setId(2002);
        patient.setFirstName("Ana");
        patient.setFirstSurname("Lopez");

        // cita mínima que sirve de base para todos los decoradores
        base = new Appointment(
                LocalDate.of(2026, 4, 20),
                LocalTime.of(9, 0),
                LocalTime.of(9, 30),
                AppointmentStatus.AGENDADA,
                doctor,
                patient
        );
        base.setReason("Consulta de control general");
    }

    // ── descripción del objeto base ───────────────────────────────────────────

    @Test
    @DisplayName("La descripcion base contiene el nombre del paciente")
    void baseDescription_containsPatientName() {
        // getDescription() debe armar el texto con los datos del paciente
        assertTrue(base.getDescription().contains("Ana Lopez"),
                "La descripcion debe incluir el nombre del paciente");
    }

    @Test
    @DisplayName("La descripcion base contiene el nombre del medico")
    void baseDescription_containsDoctorName() {
        assertTrue(base.getDescription().contains("Carlos Garcia"),
                "La descripcion debe incluir el nombre del medico");
    }

    @Test
    @DisplayName("La descripcion base contiene la fecha")
    void baseDescription_containsDate() {
        // LocalDate.toString() produce "2026-04-20", que es lo que esperamos ver
        assertTrue(base.getDescription().contains("2026-04-20"),
                "La descripcion debe incluir la fecha");
    }

    @Test
    @DisplayName("La descripcion base contiene el estado")
    void baseDescription_containsStatus() {
        assertTrue(base.getDescription().contains("AGENDADA"),
                "La descripcion debe incluir el estado");
    }

    @Test
    @DisplayName("La descripcion base contiene el motivo")
    void baseDescription_containsReason() {
        assertTrue(base.getDescription().contains("Consulta de control general"),
                "La descripcion debe incluir el motivo");
    }

    @Test
    @DisplayName("La descripcion base muestra Sin notas cuando notes es null")
    void baseDescription_showsSinNotasWhenNull() {
        // no se llamó setNotes(), así que debería caer en el caso null
        assertTrue(base.getDescription().contains("Sin notas"),
                "Debe mostrar Sin notas cuando notes es null");
    }

    // ── PriorityAppointment ───────────────────────────────────────────────────

    @Test
    @DisplayName("PriorityAppointment añade [PRIORIDAD ALTA] a la descripcion")
    void priorityDecorator_addsHighPriorityTag() {
        Appointment priority = new PriorityAppointment(base);
        // el decorador solo debe agregar la etiqueta, sin romper lo demás
        assertTrue(priority.getDescription().contains("[PRIORIDAD ALTA]"),
                "El decorador debe añadir la etiqueta [PRIORIDAD ALTA]");
    }

    @Test
    @DisplayName("PriorityAppointment conserva la descripcion base")
    void priorityDecorator_preservesBaseDescription() {
        Appointment priority = new PriorityAppointment(base);
        // si esto falla, el decorador está modificando la descripción en lugar de extenderla
        assertTrue(priority.getDescription().contains("Ana Lopez"),
                "La descripcion del decorador debe seguir incluyendo el nombre del paciente");
    }

    @Test
    @DisplayName("PriorityAppointment delega correctamente la fecha")
    void priorityDecorator_delegatesDate() {
        Appointment priority = new PriorityAppointment(base);
        // el decorador no debe tener su propia fecha; la saca del objeto envuelto
        assertEquals(LocalDate.of(2026, 4, 20), priority.getDate(),
                "La fecha debe delegarse al objeto envuelto");
    }

    @Test
    @DisplayName("PriorityAppointment delega correctamente la hora de inicio")
    void priorityDecorator_delegatesStartTime() {
        Appointment priority = new PriorityAppointment(base);
        assertEquals(LocalTime.of(9, 0), priority.getStartTime(),
                "La hora de inicio debe delegarse al objeto envuelto");
    }

    @Test
    @DisplayName("PriorityAppointment delega correctamente la hora de fin")
    void priorityDecorator_delegatesEndTime() {
        Appointment priority = new PriorityAppointment(base);
        assertEquals(LocalTime.of(9, 30), priority.getEndTime(),
                "La hora de fin debe delegarse al objeto envuelto");
    }

    @Test
    @DisplayName("PriorityAppointment delega correctamente el estado")
    void priorityDecorator_delegatesStatus() {
        Appointment priority = new PriorityAppointment(base);
        assertEquals(AppointmentStatus.AGENDADA, priority.getStatus(),
                "El estado debe delegarse al objeto envuelto");
    }

    @Test
    @DisplayName("PriorityAppointment delega correctamente el doctor")
    void priorityDecorator_delegatesDoctor() {
        Appointment priority = new PriorityAppointment(base);
        // misma referencia, no una copia — el decorador no debe clonar objetos
        assertEquals(doctor, priority.getDoctor(),
                "El doctor debe delegarse al objeto envuelto");
    }

    @Test
    @DisplayName("PriorityAppointment delega correctamente el paciente")
    void priorityDecorator_delegatesPatient() {
        Appointment priority = new PriorityAppointment(base);
        assertEquals(patient, priority.getPatient(),
                "El paciente debe delegarse al objeto envuelto");
    }

    // ── UrgentAppointment ─────────────────────────────────────────────────────

    @Test
    @DisplayName("UrgentAppointment añade [URGENTE] a la descripcion")
    void urgentDecorator_addsUrgentTag() {
        Appointment urgent = new UrgentAppointment(base);
        assertTrue(urgent.getDescription().contains("[URGENTE]"),
                "El decorador urgente debe añadir la etiqueta [URGENTE]");
    }

    @Test
    @DisplayName("UrgentAppointment conserva la descripcion base")
    void urgentDecorator_preservesBaseDescription() {
        Appointment urgent = new UrgentAppointment(base);
        assertTrue(urgent.getDescription().contains("Ana Lopez"),
                "La descripcion urgente debe seguir incluyendo el nombre del paciente");
    }

    @Test
    @DisplayName("UrgentAppointment delega correctamente la fecha")
    void urgentDecorator_delegatesDate() {
        Appointment urgent = new UrgentAppointment(base);
        assertEquals(LocalDate.of(2026, 4, 20), urgent.getDate(),
                "La fecha debe delegarse al objeto envuelto");
    }

    @Test
    @DisplayName("UrgentAppointment delega correctamente el estado")
    void urgentDecorator_delegatesStatus() {
        Appointment urgent = new UrgentAppointment(base);
        assertEquals(AppointmentStatus.AGENDADA, urgent.getStatus(),
                "El estado debe delegarse al objeto envuelto");
    }

    @Test
    @DisplayName("UrgentAppointment delega correctamente el paciente")
    void urgentDecorator_delegatesPatient() {
        Appointment urgent = new UrgentAppointment(base);
        assertEquals(patient, urgent.getPatient(),
                "El paciente debe delegarse al objeto envuelto");
    }

    // ── decoradores encadenados ───────────────────────────────────────────────

    @Test
    @DisplayName("Encadenado Urgente + Priority incluye ambas etiquetas")
    void chainedDecorators_bothTagsPresent() {
        // UrgentAppointment envuelve a PriorityAppointment, que a su vez envuelve base
        // la descripción final debería tener las dos etiquetas sin importar el orden
        Appointment chained = new UrgentAppointment(new PriorityAppointment(base));
        String desc = chained.getDescription();
        assertAll(
                () -> assertTrue(desc.contains("[URGENTE]"),
                        "Debe contener [URGENTE]"),
                () -> assertTrue(desc.contains("[PRIORIDAD ALTA]"),
                        "Debe contener [PRIORIDAD ALTA]")
        );
    }

    @Test
    @DisplayName("Encadenado conserva la informacion del paciente")
    void chainedDecorators_preservesPatientInfo() {
        Appointment chained = new UrgentAppointment(new PriorityAppointment(base));
        // dos capas de decoración no deben borrar los datos originales
        assertTrue(chained.getDescription().contains("Ana Lopez"),
                "La descripcion encadenada debe incluir el nombre del paciente");
    }

    @Test
    @DisplayName("Encadenado delega correctamente la fecha")
    void chainedDecorators_delegatesDate() {
        Appointment chained = new UrgentAppointment(new PriorityAppointment(base));
        // la llamada viaja por la cadena hasta llegar al base, que tiene la fecha real
        assertEquals(base.getDate(), chained.getDate(),
                "La fecha debe propagarse correctamente por la cadena");
    }

    @Test
    @DisplayName("Encadenado delega correctamente el estado")
    void chainedDecorators_delegatesStatus() {
        Appointment chained = new UrgentAppointment(new PriorityAppointment(base));
        assertEquals(AppointmentStatus.AGENDADA, chained.getStatus(),
                "El estado debe propagarse correctamente por la cadena");
    }

    @Test
    @DisplayName("Encadenado delega correctamente el doctor")
    void chainedDecorators_delegatesDoctor() {
        Appointment chained = new UrgentAppointment(new PriorityAppointment(base));
        assertEquals(doctor, chained.getDoctor(),
                "El doctor debe propagarse correctamente por la cadena");
    }
}