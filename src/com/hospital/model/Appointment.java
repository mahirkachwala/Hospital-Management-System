package com.hospital.model;

import com.hospital.state.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Appointment {
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private LocalDateTime dateTime;
    private String description;
    private AppointmentState currentState;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Appointment(String appointmentId, String patientId, String doctorId, LocalDateTime dateTime, String description) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.dateTime = dateTime;
        this.description = description;
        this.currentState = new PendingApprovalState(); // Default initial state
    }

    public Appointment(String appointmentId, String patientId, String doctorId, LocalDateTime dateTime, String description, AppointmentStatus status) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.dateTime = dateTime;
        this.description = description;
        setCurrentStateFromStatus(status);
    }

    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getDescription() { return description; }
    public AppointmentStatus getStatus() { return currentState.getStatus(); }

    public void setCurrentState(AppointmentState state) {
        this.currentState = state;
    }

    private void setCurrentStateFromStatus(AppointmentStatus status) {
        if (status == null) { // Should not happen with valid data
            this.currentState = new PendingApprovalState();
            return;
        }
        switch (status) {
            case PENDING_APPROVAL:
                this.currentState = new PendingApprovalState();
                break;
            case ACCEPTED:
                this.currentState = new AcceptedState();
                break;
            case REJECTED:
                this.currentState = new RejectedState();
                break;
            case CANCELLED_BY_STAFF:
            // case COMPLETED: // If you add a CompletedState
            default:
                this.currentState = new CancelledState();
                break;
        }
    }

    public void acceptAppointment(String actorId) {
        currentState.accept(this, actorId);
    }

    public void rejectAppointment(String actorId) {
        currentState.reject(this, actorId);
    }

    public void cancelAppointment(String actorId) {
        currentState.cancel(this, actorId);
    }

    @Override
    public String toString() {
        return "Appointment ID: " + appointmentId + ", Patient ID: " + patientId +
               ", Doctor ID: " + doctorId + ", DateTime: " + dateTime.format(formatter) +
               ", Status: " + getStatus() + ", Description: " + description;
    }

    public String toFileString() {
        return String.join(",", appointmentId, patientId, doctorId, dateTime.format(formatter), description, getStatus().name());
    }

    public static Appointment fromFileString(String fileString) {
        String[] parts = fileString.split(",");
        if (parts.length == 6) {
            try {
                LocalDateTime dt = LocalDateTime.parse(parts[3], formatter);
                AppointmentStatus status = AppointmentStatus.valueOf(parts[5].toUpperCase());
                return new Appointment(parts[0], parts[1], parts[2], dt, parts[4], status);
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing appointment date from string: " + fileString + " - " + e.getMessage());
                return null;
            } catch (IllegalArgumentException e) {
                 System.err.println("Error parsing appointment status from string: " + fileString + " - " + e.getMessage());
                return null;
            }
        }
        System.err.println("Error parsing appointment from string due to incorrect parts: " + fileString);
        return null;
    }
}