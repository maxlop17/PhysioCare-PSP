package org.example.emailprojectjavafx.models.Record;

import org.example.emailprojectjavafx.models.BaseResponse;
import org.example.emailprojectjavafx.models.Physio.Physio;

import java.util.List;

public class RecordListResponse extends BaseResponse {

    List<Record> resultado;

    public List<Record> getRecords() {
        return resultado;
    }

    @Override
    public String toString() {
        return "RecordListResponse{" +
                "resultado=" + resultado +
                '}';
    }
}
