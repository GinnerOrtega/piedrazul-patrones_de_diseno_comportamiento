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
import co.unicauca.piedrazul.adapter.ExternalService;
import co.unicauca.piedrazul.adapter.ExternalPatientAdapter;
import co.unicauca.piedrazul.adapter.PatientDataProvider;
import co.unicauca.piedrazul.adapter.EmailNotificationAdapter;
import co.unicauca.piedrazul.adapter.EmailNotificationService;
import co.unicauca.piedrazul.adapter.SmsNotificationAdapter;
import co.unicauca.piedrazul.adapter.SmsNotificationService;
import co.unicauca.piedrazul.templatemethod.AppointmentScheduler;
import co.unicauca.piedrazul.templatemethod.AutonomousScheduler;
import co.unicauca.piedrazul.templatemethod.ManualScheduler;

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

public class PatternTestMain {

    private static final int DOCTOR_ID = 1002;
    private static final int PATIENT_ID = 2002;
    private static final LocalDate TEST_DATE = LocalDate.of(2026, 4, 20);
    private static final LocalTime TEST_START_TIME = LocalTime.of(9, 0);
    private static final LocalTime TEST_END_TIME = LocalTime.of(9, 30);
    private static final String TEST_REASON = "Consulta de control general";

    private static final List<Integer> createdIds = new ArrayList<>();

    public static void main(String[] args) {

        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        } catch (Exception e) { }

        // Inicialización de entidades
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

        Appointment appointment = new Appointment(
                TEST_DATE,
                TEST_START_TIME,
                TEST_END_TIME,
                AppointmentStatus.AGENDADA,
                doctor,
                patient
        );
        appointment.setReason(TEST_REASON);

        // --- PATRÓN FACADE ---
        System.out.println("\n--- DEMOSTRACIÓN PATRÓN FACADE ---");

        IAppointmentService appointmentService = PostgresServiceFactory.getInstance().createManualAppointmentService();
        INotificationService notification = new ConsoleNotificationService();
        IAuditService audit = new ConsoleAuditService();

        AppointmentFacade facade = new AppointmentFacade(appointmentService, notification, audit);

        System.out.println("\nEjecutando facade.createAppointment()...");
        try {
            facade.createAppointment(appointment);
            int newId = getLastInsertedId(DOCTOR_ID, TEST_DATE.toString(), TEST_START_TIME.toString());
            if (newId > 0) {
                createdIds.add(newId);
                System.out.println("[Test] Cita guardada en BD con ID: " + newId);
                appointment.setAppointmentId(newId);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("[Validacion] " + e.getMessage());
        }

        // --- PATRÓN DECORATOR ---
        System.out.println("\n--- DEMOSTRACIÓN PATRÓN DECORATOR ---");
        System.out.println("\nDescripción base:");
        System.out.println(appointment.getDescription());

        System.out.println("\nDecorador: PriorityAppointment");
        Appointment priority = new PriorityAppointment(appointment);
        System.out.println(priority.getDescription());

        System.out.println("\nDecorador: UrgentAppointment");
        Appointment urgent = new UrgentAppointment(appointment);
        System.out.println(urgent.getDescription());

        System.out.println("\nDecoradores encadenados (Urgente + Prioridad):");
        Appointment chained = new UrgentAppointment(new PriorityAppointment(appointment));
        System.out.println(chained.getDescription());

        // --- PATRÓN STATE ---
        System.out.println("\n--- DEMOSTRACIÓN PATRÓN STATE ---");
        Appointment citaState = new Appointment(TEST_DATE, TEST_START_TIME, TEST_END_TIME, AppointmentStatus.AGENDADA, doctor, patient);
        citaState.setReason(TEST_REASON);

        System.out.println("Estado inicial: " + citaState.getState().getNombre());
        citaState.confirmar();
        System.out.println("Estado actual: " + citaState.getState().getNombre());
        citaState.atender();
        System.out.println("Estado actual: " + citaState.getState().getNombre());

        // --- PATRÓN TEMPLATE METHOD ---
        System.out.println("\n--- DEMOSTRACIÓN PATRÓN TEMPLATE METHOD ---");

        AppointmentScheduler autonomous = new AutonomousScheduler();
        Appointment citaAutonoma = new Appointment(
                TEST_DATE.plusDays(5),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                AppointmentStatus.AGENDADA,
                doctor,
                patient
        );
        citaAutonoma.setReason("Consulta de control por sistema autónomo");

        System.out.println("\nEjecutando AutonomousScheduler.schedule()...");
        autonomous.schedule(citaAutonoma);
        System.out.println("Estado tras schedule: " + citaAutonoma.getState().getNombre());

        AppointmentScheduler manual = new ManualScheduler();
        Appointment citaManual = new Appointment(
                TEST_DATE.plusDays(6),
                LocalTime.of(11, 0),
                LocalTime.of(11, 30),
                AppointmentStatus.AGENDADA,
                doctor,
                patient
        );
        citaManual.setReason("Consulta asignada por agendador");

        System.out.println("\nEjecutando ManualScheduler.schedule()...");
        manual.schedule(citaManual);
        System.out.println("Estado tras schedule: " + citaManual.getState().getNombre());

        // --- PATRÓN ADAPTER ---
        System.out.println("\n--- DEMOSTRACIÓN PATRÓN ADAPTER ---");

        System.out.println("\n[1] ExternalPatientAdapter — adapta ExternalService (JSON) a PatientDataProvider:");
        PatientDataProvider provider = new ExternalPatientAdapter(new ExternalService());
        Patient externalPatient = provider.getPatient();
        System.out.println("Paciente obtenido desde JSON: "
                + externalPatient.getFirstName() + " " + externalPatient.getFirstSurname());

        System.out.println("\n[2] EmailNotificationAdapter — adapta EmailNotificationService a INotificationService:");
        INotificationService emailNotif = new EmailNotificationAdapter(new EmailNotificationService());
        emailNotif.notifyUser(appointment);

        System.out.println("\n[3] SmsNotificationAdapter — adapta SmsNotificationService a INotificationService:");
        INotificationService smsNotif = new SmsNotificationAdapter(new SmsNotificationService());
        smsNotif.notifyUser(appointment);

        // --- FINALIZACIÓN Y LIMPIEZA ---
        System.out.println("\n--- FINALIZACIÓN DE PRUEBAS ---");
        System.out.println("\nEjecutando facade.cancelAppointment() para la cita original...");
        facade.cancelAppointment(appointment);

        System.out.println("\nLimpiando registros de prueba en BD...");
        cleanup();
        System.out.println("\nPruebas terminadas correctamente.");
    }

    private static int getLastInsertedId(int doctorId, String date, String startTime) {
        String sql = "SELECT appt_id FROM appointments WHERE appt_doct_id = ? AND appt_date = ?::date AND appt_start_time = ?::time ORDER BY appt_id DESC LIMIT 1";
        try (Connection conn = PostgreSQLConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setString(2, date);
            ps.setString(3, startTime);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("appt_id");
            }
        } catch (SQLException e) {
            System.err.println("[Error] " + e.getMessage());
        }
        return -1;
    }

    private static void cleanup() {
        if (createdIds.isEmpty()) return;
        String sql = "DELETE FROM appointments WHERE appt_id = ?";
        try (Connection conn = PostgreSQLConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int id : createdIds) {
                ps.setInt(1, id);
                ps.executeUpdate();
                System.out.println("[Cleanup] Cita ID " + id + " eliminada");
            }
        } catch (SQLException e) {
            System.err.println("[Cleanup Error] " + e.getMessage());
        }
        createdIds.clear();
    }
}