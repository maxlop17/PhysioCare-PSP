package org.example.emailprojectjavafx.models.Appointment;

import org.example.emailprojectjavafx.models.BaseResponse;
import org.example.emailprojectjavafx.models.Patient.Patient;

import java.util.List;

public class AppointmentListResponse extends BaseResponse {

    List<Appointment> resultado;

    public List<Appointment> getAppointments() {
        return resultado;
    }
}
