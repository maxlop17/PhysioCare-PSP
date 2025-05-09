package org.example.emailprojectjavafx.models.Record;

import org.example.emailprojectjavafx.models.Appointment.Appointment;
import org.example.emailprojectjavafx.models.BaseResponse;

public class RecordResponse extends BaseResponse {

    private Record resultado;

    public Record getRecord() {
        return resultado;
    }

    @Override
    public String toString() {
        return "RecordResponse{" +
                "resultado=" + resultado +
                '}';
    }
}
