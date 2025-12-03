package com.hospital.factory;

import com.hospital.model.*;
import java.time.LocalDateTime;

public interface EntityFactory {
    Patient createPatient(String id, String name, String contactNumber, int age, String gender, String address);
    Doctor createDoctor(String id, String name, String contactNumber, String specialization, String department);
    Appointment createAppointment(String appointmentId, String patientId, String doctorId, LocalDateTime dateTime, String description);
    User createUser(String username, String password, Role role, String entityId);
}