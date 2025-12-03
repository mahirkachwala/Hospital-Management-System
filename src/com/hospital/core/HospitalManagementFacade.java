package com.hospital.core;

import com.hospital.factory.EntityFactory;
import com.hospital.factory.HospitalEntityFactory;
import com.hospital.model.*; // All models
import com.hospital.observer.EventManager;
import com.hospital.service.FileManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class HospitalManagementFacade {
    private final EntityFactory entityFactory;
    private final FileManager fileManager;
    private final EventManager eventManager;

    private List<Patient> patients;
    private List<Doctor> doctors;
    private List<Appointment> appointments;
    private List<User> users;

    private User currentUser; // For session management

    public HospitalManagementFacade(EventManager eventManager) {
        this.entityFactory = new HospitalEntityFactory();
        this.fileManager = FileManager.getInstance();
        this.eventManager = eventManager;

        this.patients = fileManager.loadPatients();
        this.doctors = fileManager.loadDoctors();
        this.appointments = fileManager.loadAppointments();
        this.users = fileManager.loadUsers(); // Load users
        this.currentUser = null;
    }

    // --- Authentication ---
    public boolean login(String username, String password) {
        Optional<User> userOpt = users.stream()
                                      .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                                      .findFirst();
        if (userOpt.isPresent()) {
            this.currentUser = userOpt.get();
            eventManager.publishEvent("LOGIN_SUCCESS", "User: " + username);
            return true;
        }
        eventManager.publishEvent("LOGIN_FAILURE", "User: " + username);
        return false;
    }

    public void logout() {
        if (currentUser != null) {
            eventManager.publishEvent("LOGOUT", "User: " + currentUser.getUsername());
            this.currentUser = null;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Role getCurrentUserRole() {
        return (currentUser != null) ? currentUser.getRole() : null;
    }
    
    // --- Utility to check staff role ---
    private boolean isStaff() {
        return currentUser != null && currentUser.getRole() == Role.STAFF;
    }

    // --- Utility to check doctor role ---
    private boolean isDoctor() {
        return currentUser != null && currentUser.getRole() == Role.DOCTOR;
    }
    
    private boolean isAuthorizedDoctor(String doctorId) {
        return isDoctor() && currentUser.getEntityId() != null && currentUser.getEntityId().equals(doctorId);
    }


    // --- Patient Operations (Staff) ---
    public Patient registerNewPatient(String name, String contactNumber, int age, String gender, String address) {
        if (!isStaff()) {
            System.err.println("Access Denied: Only STAFF can register patients.");
            return null;
        }
        String patientId = "PAT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Patient patient = entityFactory.createPatient(patientId, name, contactNumber, age, gender, address);
        patients.add(patient);
        fileManager.savePatients(patients);
        eventManager.publishEvent("PATIENT_REGISTERED", patient + " by " + currentUser.getUsername());
        return patient;
    }

    public List<Patient> getAllPatients() {
        if (currentUser == null) {
             System.err.println("Access Denied: Please login.");
             return new ArrayList<>();
        }
        // Both staff and doctors can view patients
        return new ArrayList<>(patients);
    }

    public Optional<Patient> findPatientById(String patientId) {
         if (currentUser == null) {
             System.err.println("Access Denied: Please login.");
             return Optional.empty();
        }
        return patients.stream().filter(p -> p.getId().equals(patientId)).findFirst();
    }

    // --- Doctor Operations (Staff) ---
    public Doctor addNewDoctor(String name, String contactNumber, String specialization, String department) {
        if (!isStaff()) {
            System.err.println("Access Denied: Only STAFF can add doctors.");
            return null;
        }
        String doctorId = "DOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Doctor doctor = entityFactory.createDoctor(doctorId, name, contactNumber, specialization, department);
        doctors.add(doctor);
        fileManager.saveDoctors(doctors);
        
        // Also add a corresponding user for this doctor for login (simplified)
        // In a real system, user creation might be a separate, more secure process.
        String username = name.toLowerCase().replaceAll("\\s+", "") + doctorId.substring(4, 7); // e.g., johnsmithSAM
        String defaultPassword = "doctor"+doctorId.substring(4,7); // e.g., doctorSAM
        User doctorUser = new User(username, defaultPassword, Role.DOCTOR, doctorId);
        if(users.stream().noneMatch(u -> u.getUsername().equals(username))) {
            users.add(doctorUser);
            fileManager.saveUsers(users);
             eventManager.publishEvent("DOCTOR_USER_CREATED", doctorUser + " (Password: " + defaultPassword +")");
        }
       
        eventManager.publishEvent("DOCTOR_ADDED", doctor + " by " + currentUser.getUsername());
        return doctor;
    }

    public List<Doctor> getAllDoctors() {
         if (currentUser == null) {
             System.err.println("Access Denied: Please login.");
             return new ArrayList<>();
        }
        // Both staff and doctors can view doctors
        return new ArrayList<>(doctors);
    }

    public Optional<Doctor> findDoctorById(String doctorId) {
        if (currentUser == null) {
             System.err.println("Access Denied: Please login.");
             return Optional.empty();
        }
        return doctors.stream().filter(d -> d.getId().equals(doctorId)).findFirst();
    }


    // --- Appointment Operations ---
    public Appointment scheduleNewAppointment(String patientId, String doctorId, LocalDateTime dateTime, String description) {
        if (!isStaff()) { // Only staff can schedule new appointments
            System.err.println("Access Denied: Only STAFF can schedule new appointments.");
            return null;
        }
        if (findPatientById(patientId).isEmpty() || findDoctorById(doctorId).isEmpty()) {
            System.err.println("Error: Invalid Patient ID or Doctor ID.");
            return null;
        }

        String appointmentId = "APP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        // Appointments are created in PENDING_APPROVAL state by default (handled by Appointment constructor)
        Appointment appointment = entityFactory.createAppointment(appointmentId, patientId, doctorId, dateTime, description);
        appointments.add(appointment);
        fileManager.saveAppointments(appointments);
        eventManager.publishEvent("APPOINTMENT_SCHEDULED_PENDING", appointment + " by " + currentUser.getUsername());
        return appointment;
    }

    public List<Appointment> getAllAppointments() {
        if (!isStaff()) {
            System.err.println("Access Denied: Only STAFF can view all appointments.");
            return new ArrayList<>();
        }
        return new ArrayList<>(appointments);
    }

    public List<Appointment> getAppointmentsForPatient(String patientId) {
        if (currentUser == null) {
            System.err.println("Access Denied: Please login.");
            return new ArrayList<>();
        }
        // Staff can see any patient's appointments
        // Doctors can see their own patient's appointments if they are the assigned doctor (more complex check not added here for brevity)
        return appointments.stream()
                           .filter(app -> app.getPatientId().equals(patientId))
                           .collect(Collectors.toList());
    }

    // --- Doctor-Specific Appointment Management ---
    public List<Appointment> getAppointmentsForDoctor(String doctorId, AppointmentStatus statusFilter) {
        if (!isAuthorizedDoctor(doctorId) && !isStaff()) { // Staff can also use this to view
             System.err.println("Access Denied or Doctor ID mismatch.");
             return new ArrayList<>();
        }
        return appointments.stream()
                           .filter(app -> app.getDoctorId().equals(doctorId) && (statusFilter == null || app.getStatus() == statusFilter))
                           .collect(Collectors.toList());
    }


    public boolean processAppointmentAction(String appointmentId, String action) { // action: "ACCEPT" or "REJECT"
        if (!isDoctor()) {
            System.err.println("Access Denied: Only DOCTORs can accept/reject appointments.");
            return false;
        }
        Optional<Appointment> appOpt = appointments.stream()
                                                   .filter(a -> a.getAppointmentId().equals(appointmentId) &&
                                                                a.getDoctorId().equals(currentUser.getEntityId())) // Ensure it's their appointment
                                                   .findFirst();
        if (appOpt.isPresent()) {
            Appointment appointment = appOpt.get();
            String oldStatus = appointment.getStatus().name();
            if (action.equalsIgnoreCase("ACCEPT")) {
                appointment.acceptAppointment(currentUser.getEntityId());
            } else if (action.equalsIgnoreCase("REJECT")) {
                appointment.rejectAppointment(currentUser.getEntityId());
            } else {
                System.err.println("Invalid action.");
                return false;
            }
            fileManager.saveAppointments(appointments); // Save changes
            eventManager.publishEvent("APPOINTMENT_" + action.toUpperCase() + "ED",
                                     "ID: " + appointment.getAppointmentId() +
                                     ", Old Status: " + oldStatus + ", New Status: " + appointment.getStatus().name() +
                                     " by Dr. " + currentUser.getUsername());
            return true;
        } else {
            System.err.println("Appointment not found or not assigned to you.");
            return false;
        }
    }
    
    // Staff can cancel appointments
    public boolean cancelAppointmentByStaff(String appointmentId) {
        if (!isStaff()) {
            System.err.println("Access Denied: Only STAFF can cancel appointments this way.");
            return false;
        }
        Optional<Appointment> appOpt = appointments.stream()
                                                   .filter(a -> a.getAppointmentId().equals(appointmentId))
                                                   .findFirst();
        if (appOpt.isPresent()) {
            Appointment appointment = appOpt.get();
            // Check if appointment is in a cancellable state (e.g., PENDING or ACCEPTED)
            if (appointment.getStatus() == AppointmentStatus.PENDING_APPROVAL || appointment.getStatus() == AppointmentStatus.ACCEPTED) {
                String oldStatus = appointment.getStatus().name();
                appointment.cancelAppointment(currentUser.getUsername()); // Actor is current staff user
                fileManager.saveAppointments(appointments);
                eventManager.publishEvent("APPOINTMENT_CANCELLED",
                                         "ID: " + appointment.getAppointmentId() +
                                         ", Old Status: " + oldStatus + ", New Status: " + appointment.getStatus().name() +
                                         " by Staff " + currentUser.getUsername());
                return true;
            } else {
                System.err.println("Appointment cannot be cancelled from its current state: " + appointment.getStatus());
                return false;
            }
        } else {
            System.err.println("Appointment ID " + appointmentId + " not found.");
            return false;
        }
    }
}