package com.hospital.state;

import com.hospital.model.Appointment;
import com.hospital.model.AppointmentStatus;

public class AcceptedState implements AppointmentState {
    @Override
    public void accept(Appointment appointment, String doctorId) {
        System.out.println("INFO: Appointment " + appointment.getAppointmentId() + " is already accepted.");
    }

    @Override
    public void reject(Appointment appointment, String doctorId) {
        System.out.println("INFO: Appointment " + appointment.getAppointmentId() + " is already accepted. Cannot reject directly by doctor. Staff may cancel.");
    }

    @Override
    public void cancel(Appointment appointment, String actorId) {
        appointment.setCurrentState(new CancelledState());
        System.out.println("INFO: Appointment " + appointment.getAppointmentId() + " CANCELLED by " + actorId + " after acceptance.");
    }

    @Override
    public AppointmentStatus getStatus() {
        return AppointmentStatus.ACCEPTED;
    }
}