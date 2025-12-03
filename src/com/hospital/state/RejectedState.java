package com.hospital.state;

import com.hospital.model.Appointment;
import com.hospital.model.AppointmentStatus;

public class RejectedState implements AppointmentState {
    @Override
    public void accept(Appointment appointment, String doctorId) {
        System.out.println("INFO: Appointment " + appointment.getAppointmentId() + " was rejected. Cannot accept now.");
    }

    @Override
    public void reject(Appointment appointment, String doctorId) {
        System.out.println("INFO: Appointment " + appointment.getAppointmentId() + " is already rejected.");
    }

    @Override
    public void cancel(Appointment appointment, String actorId) {
        System.out.println("INFO: Appointment " + appointment.getAppointmentId() + " is rejected. No further cancellation action usually taken.");
    }

    @Override
    public AppointmentStatus getStatus() {
        return AppointmentStatus.REJECTED;
    }
}