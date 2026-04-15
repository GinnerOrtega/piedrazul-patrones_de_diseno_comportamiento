package co.unicauca.piedrazul.domain.entities;

import co.unicauca.piedrazul.domain.entities.enums.AppointmentStatus;
import co.unicauca.piedrazul.domain.state.CreadaState;
import java.time.LocalDate;
import java.time.LocalTime;
import co.unicauca.piedrazul.domain.state.IAppointmentState;

/**
 * @author Valentina Añasco
 * @author Camila Dorado
 * @author Felipe Gutierrez
 * @author Ginner Ortega
 * @author Santiago Solarte
 */
public class Appointment {

    private int appointmentId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;
    private Doctor doctor;
    private Patient patient;
    private String reason;
    private String notes;

    private IAppointmentState state;

    public Appointment() {
        this.state = new CreadaState();
    }

    public Appointment(int appointmentId, LocalDate date, LocalTime startTime, LocalTime endTime, Doctor doctor, Patient patient, String reason, String notes, AppointmentStatus status) {
        this.appointmentId = appointmentId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.doctor = doctor;
        this.patient = patient;
        this.reason = reason;
        this.notes = notes;
        this.state = new CreadaState();
    }

    public Appointment(LocalDate date, LocalTime startTime, LocalTime endTime, AppointmentStatus status, Doctor doctor, Patient patient) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.doctor = doctor;
        this.patient = patient;
        this.state = new CreadaState();
    }

    public void confirmar() {
        state.confirmar(this);
    }

    public void atender() {
        state.atender(this);
    }

    public void cancelar() {
        state.cancelar(this);
    }

    public void reagendar() {
        state.reagendar(this);
    }

    public void noAsistio() {
        state.noAsistio(this);
    }

    public IAppointmentState getState() {
        return state;
    }

    public void setState(IAppointmentState state) {
        this.state = state;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDescription() {
        return "Cita #" + appointmentId
                + " | Paciente: " + (patient != null ? patient.getFullName() : "N/A")
                + " | Médico: " + (doctor != null ? doctor.getFullName() : "N/A")
                + " | Fecha: " + date
                + " | " + startTime + " - " + endTime
                + " | Motivo: " + (reason != null && !reason.isEmpty() ? reason : "Sin motivo")
                + " | Notas: " + (notes != null && !notes.isEmpty() ? notes : "Sin notas")
                + " | Estado: " + status + " (" + (state != null ? state.getNombre() : "N/A") + ")";
    }
}
