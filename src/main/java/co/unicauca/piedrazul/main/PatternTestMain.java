package co.unicauca.piedrazul.main;

import co.unicauca.piedrazul.decorator.PriorityAppointment;
import co.unicauca.piedrazul.decorator.UrgentAppointment;
import co.unicauca.piedrazul.domain.entities.Appointment;
import co.unicauca.piedrazul.domain.entities.Doctor;
import co.unicauca.piedrazul.domain.entities.Patient;
import co.unicauca.piedrazul.domain.entities.enums.AppointmentStatus;
import co.unicauca.piedrazul.domain.facade.AppointmentFacade;
import co.unicauca.piedrazul.domain.services.ConsoleAuditService;
import co.unicauca.piedrazul.domain.services.ConsoleNotificationService;
import co.unicauca.piedrazul.domain.services.interfaces.IAuditService;
import co.unicauca.piedrazul.domain.services.interfaces.IAppointmentService;
import co.unicauca.piedrazul.domain.services.interfaces.INotificationService;
import co.unicauca.piedrazul.infrastructure.factories.PostgresServiceFactory;
import co.unicauca.piedrazul.infrastructure.persistence.PostgreSQLConnection;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Main independiente de JavaFX para demostrar los patrones Decorator y Facade.
 *
 * Flujo:
 *  1. Demuestra el patron Decorator sobre una cita en memoria
 *  2. Crea una cita real en BD usando la Facade (createAppointment)
 *  3. Cancela la cita usando la Facade (cancelAppointment)
 *  4. Limpia la BD borrando las citas de prueba (cleanup)
 *
 * Datos reales de la BD de Railway:
 *  - Doctor  : Andre Felipe Garcia Mora  (user_id = 1002)
 *  - Paciente: Juan Pablo Lopez Ruiz    (user_id = 2002)
 *  - Horario : Lunes a Viernes 08:00-12:00 cada 30 min
 */
public class PatternTestMain {

    // IDs que existen en Railway 
    private static final int DOCTOR_ID  = 1002;
    private static final int PATIENT_ID = 2002;

    // Lunes 20/04/2026 — se eligió lunes porque el doctor 1002 trabaja ese día
    private static final LocalDate TEST_DATE       = LocalDate.of(2026, 4, 20);
    private static final LocalTime TEST_START_TIME = LocalTime.of(9, 0);
    private static final LocalTime TEST_END_TIME   = LocalTime.of(9, 30);

    // El validador rechaza citas sin motivo, así que siempre hay que pasarlo
    private static final String TEST_REASON = "Consulta de control general";

    // Se llena durante el main y se vacía en cleanup — evita dejar basura en BD
    private static final List<Integer> createdIds = new ArrayList<>();

    public static void main(String[] args) {

        // En Windows la consola no siempre muestra UTF-8 por defecto
        // esto evita que tildes y ñ salgan como signos de interrogación
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            // si falla el encoding no es crítico, seguimos igual
        }

        // ── entidades con los datos reales que están en Railway ───────────────
        Doctor doctor = new Doctor();
        doctor.setId(DOCTOR_ID);
        doctor.setFirstName("Andre");
        doctor.setMiddleName("Felipe");
        doctor.setFirstSurname("Garcia");
        doctor.setLastName("Mora");

        Patient patient = new Patient();
        patient.setId(PATIENT_ID);
        patient.setFirstName("Juan");
        patient.setMiddleName("Pablo");
        patient.setFirstSurname("Lopez");
        patient.setLastName("Ruiz");

        // se usa el constructor de 6 parámetros; reason y notes van por setter
        Appointment appointment = new Appointment(
                TEST_DATE,
                TEST_START_TIME,
                TEST_END_TIME,
                AppointmentStatus.AGENDADA,
                doctor,
                patient
        );
        appointment.setReason(TEST_REASON);

        // =====================================================================
        // PATRON FACADE
        // =====================================================================
        System.out.println("");
        System.out.println("        PATRON FACADE                    ");
        System.out.println("");

        // cada subsistema hace una sola cosa: persistir, notificar o auditar
        IAppointmentService appointmentService = PostgresServiceFactory.getInstance().createManualAppointmentService();
        INotificationService notification = new ConsoleNotificationService();
        IAuditService audit = new ConsoleAuditService();

        // la Facade orquesta los tres — el cliente no sabe que existen por separado
        AppointmentFacade facade = new AppointmentFacade(
                appointmentService, notification, audit
        );

        // -- createAppointment ------------------------------------------------
        System.out.println("\n-- facade.createAppointment() --");
        try {
            facade.createAppointment(appointment);

            // la BD genera el ID automáticamente, así que hay que buscarlo
            // justo después del insert para poder cancelarlo y limpiarlo después
            int newId = getLastInsertedId(DOCTOR_ID,
                                          TEST_DATE.toString(),
                                          TEST_START_TIME.toString());
            if (newId > 0) {
                createdIds.add(newId);
                System.out.println("[Test] Cita guardada en BD con ID: " + newId);
                appointment.setAppointmentId(newId);
            }

        } catch (IllegalArgumentException e) {
            // el validador lanzó excepción — se imprime el motivo y se continúa
            System.out.println("[Validacion] " + e.getMessage());
        }

        // =====================================================================
        // PATRON DECORATOR
        // =====================================================================
        System.out.println("         PATRON DECORATOR                 ");

        // descripción sin ningún decorador — texto base del objeto
        System.out.println("\n-- Sin decorador --");
        System.out.println(appointment.getDescription());

        // agrega [PRIORIDAD ALTA] sin tocar el objeto original
        System.out.println("\n-- PriorityAppointment --");
        Appointment priority = new PriorityAppointment(appointment);
        System.out.println(priority.getDescription());

        // agrega [URGENTE] sin tocar el objeto original
        System.out.println("\n-- UrgentAppointment --");
        Appointment urgent = new UrgentAppointment(appointment);
        System.out.println(urgent.getDescription());

        // Envoltorio tras envoltorio
        System.out.println("\n-- Encadenado: Urgente + Alta Prioridad --");
        Appointment chained = new UrgentAppointment(new PriorityAppointment(appointment));
        System.out.println(chained.getDescription());

        // confirma que los getters delegan hasta el objeto base, no retornan null
        System.out.println("\n-- Verificacion de delegacion --");
        System.out.println("Fecha    : " + priority.getDate());
        System.out.println("Hora     : " + priority.getStartTime() + " - " + priority.getEndTime());
        System.out.println("Estado   : " + priority.getStatus());
        System.out.println("Doctor   : " + priority.getDoctor().getFullName());
        System.out.println("Paciente : " + priority.getPatient().getFullName());

        // -- cancelAppointment ------------------------------------------------
        // de vuelta a la Facade para cerrar el ciclo de vida de la cita
        System.out.println("");
        System.out.println("        PATRON FACADE                    ");
        System.out.println("");
        
        System.out.println("\n-- facade.cancelAppointment() --");
        facade.cancelAppointment(appointment);

        // borra todo lo que se insertó para que el Main sea repetible
        System.out.println("\n-- Limpieza de BD --");
        cleanup();

        System.out.println("\n-- Fin de pruebas - BD limpia --");
    }

    /**
     * Busca el ID de la cita recién insertada por doctor, fecha y hora de inicio.
     * Se ordena DESC y se toma el primero por si hubiera registros duplicados previos.
     */
    private static int getLastInsertedId(int doctorId, String date, String startTime) {
        String sql = """
                SELECT appt_id FROM appointments
                WHERE appt_doct_id = ?
                  AND appt_date = ?::date
                  AND appt_start_time = ?::time
                ORDER BY appt_id DESC
                LIMIT 1
                """;
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setString(2, date);
            ps.setString(3, startTime);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("appt_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("[Cleanup] Error al buscar ID: " + e.getMessage());
        }
        // -1 indica que no se encontró nada — el llamador debe verificarlo
        return -1;
    }

    /**
     * Elimina de la BD todas las citas que este Main creó.
     * Sin esto, cada ejecución acumula filas de prueba en Railway.
     */
    private static void cleanup() {
        if (createdIds.isEmpty()) {
            System.out.println("[Cleanup] No hay citas de prueba que eliminar.");
            return;
        }
        String sql = "DELETE FROM appointments WHERE appt_id = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int id : createdIds) {
                ps.setInt(1, id);
                int rows = ps.executeUpdate();
                // rows == 0 significa que alguien ya borró esa cita por otro lado
                System.out.println("[Cleanup] Cita ID " + id
                        + (rows > 0 ? " eliminada OK" : " no encontrada"));
            }
        } catch (SQLException e) {
            System.err.println("[Cleanup] Error al eliminar: " + e.getMessage());
        }
        createdIds.clear();
    }
}