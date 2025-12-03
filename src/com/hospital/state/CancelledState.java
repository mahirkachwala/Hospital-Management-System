package com.hospital.state;

import com.hospital.model.Appointment;
import com.hospital.model.AppointmentStatus;

public class CancelledState implements AppointmentState {
    @Override
    public void accept(Appointment appointment, String doctorId) {
        System.out.println("INFO: Appointment " + appointment.getAppointmentId() + " is cancelled. Cannot accept.");
    }

    @Override
    public void reject(Appointment appointment, String doctorId) {
        System.out.println("INFO: Appointment " + appointment.getAppointmentId() + " is cancelled. Cannot reject.");
    }

    @Override
    public void cancel(Appointment appointment, String actorId) {
        System.out.println("INFO: Appointment " + appointment.getAppointmentId() + " is already cancelled.");
    }

    @Override
    public AppointmentStatus getStatus() {
        return AppointmentStatus.CANCELLED_BY_STAFF;
    }
}