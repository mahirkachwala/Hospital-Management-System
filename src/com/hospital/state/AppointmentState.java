package com.hospital.state;

import com.hospital.model.Appointment;
import com.hospital.model.AppointmentStatus;

public interface AppointmentState {
    void accept(Appointment appointment, String actorId);
    void reject(Appointment appointment, String actorId);
    void cancel(Appointment appointment, String actorId);
    AppointmentStatus getStatus();
}