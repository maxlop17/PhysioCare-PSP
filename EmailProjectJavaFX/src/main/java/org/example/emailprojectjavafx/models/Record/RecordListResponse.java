package org.example.emailprojectjavafx.models.Record;

import org.example.emailprojectjavafx.models.BaseResponse;
import org.example.emailprojectjavafx.models.Physio.Physio;

import java.util.List;

public class RecordListResponse extends BaseResponse {

    List<Record> result;

    public List<Record> getRecords() {
        return result;
    }
}
