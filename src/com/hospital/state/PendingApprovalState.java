package com.hospital.state;

import com.hospital.model.Appointment;
import com.hospital.model.AppointmentStatus;

public class PendingApprovalState implements AppointmentState {
    @Override
    public void accept(Appointment appointment, String doctorId) {
        if (appointment.getDoctorId().equals(doctorId)) {
            appointment.setCurrentState(new AcceptedState());
            System.out.println("INFO: Appointment " + appointment.getAppointmentId() + " ACCEPTED by Dr. " + doctorId);
        } else {
            System.err.println("Error: Doctor " + doctorId + " not authorized to accept appointment " + appointment.getAppointmentId() + " (Assigned to Dr. " + appointment.getDoctorId() + ")");
        }
    }

    @Override
    public void reject(Appointment appointment, String doctorId) {
         if (appointment.getDoctorId().equals(doctorId)) {
            appointment.setCurrentState(new RejectedState());
            System.out.println("INFO: Appointment " + appointment.getAppointmentId() + " REJECTED by Dr. " + doctorId);
        } else {
            System.err.println("Error: Doctor " + doctorId + " not authorized to reject appointment " + appointment.getAppointmentId() + " (Assigned to Dr. " + appointment.getDoctorId() + ")");
        }
    }

    @Override
    public void cancel(Appointment appointment, String actorId) {
        appointment.setCurrentState(new CancelledState());
        System.out.println("INFO: Appointment " + appointment.getAppointmentId() + " CANCELLED by " + actorId + " while pending.");
    }

    @Override
    public AppointmentStatus getStatus() {
        return AppointmentStatus.PENDING_APPROVAL;
    }
}