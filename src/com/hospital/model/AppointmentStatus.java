package com.hospital.model;

public enum AppointmentStatus {
    PENDING_APPROVAL,
    ACCEPTED,
    REJECTED,
    CANCELLED_BY_STAFF, // Or more generic CANCELLED
    COMPLETED // Future state, not fully implemented in logic
}