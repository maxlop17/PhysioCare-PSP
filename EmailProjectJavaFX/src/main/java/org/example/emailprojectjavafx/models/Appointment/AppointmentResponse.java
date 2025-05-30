package org.example.emailprojectjavafx.models.Appointment;

import org.example.emailprojectjavafx.models.BaseResponse;
import org.example.emailprojectjavafx.models.Patient.Patient;
import org.example.emailprojectjavafx.models.Record.Record;

public class AppointmentResponse extends BaseResponse {

    private Record resultado;

    public Record getResultado() {
        return resultado;
    }

    @Override
    public String toString() {
        return "AppointmentResponse{" +
                "resultado=" + resultado +
                '}';
    }
}
