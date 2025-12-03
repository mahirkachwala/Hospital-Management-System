package com.hospital.factory;

import com.hospital.model.*;
import java.time.LocalDateTime;

public class HospitalEntityFactory implements EntityFactory {
    @Override
    public Patient createPatient(String id, String name, String contactNumber, int age, String gender, String address) {
        return new Patient(id, name, contactNumber, age, gender, address);
    }

    @Override
    public Doctor createDoctor(String id, String name, String contactNumber, String specialization, String department) {
        return new Doctor(id, name, contactNumber, specialization, department);
    }

    @Override
    public Appointment createAppointment(String appointmentId, String patientId, String doctorId, LocalDateTime dateTime, String description) {
        // Default state (PENDING_APPROVAL) is handled by Appointment constructor
        return new Appointment(appointmentId, patientId, doctorId, dateTime, description);
    }

    @Override
    public User createUser(String username, String password, Role role, String entityId) {
        return new User(username, password, role, entityId);
    }
}