package org.example.emailprojectjavafx.models.Appointment;

import org.example.emailprojectjavafx.models.BaseResponse;
import org.example.emailprojectjavafx.models.Patient.Patient;

public class AppointmentResponse extends BaseResponse {

    private Appointment result;

    public Appointment getAppointment() {
        return result;
    }
}
