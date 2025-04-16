package org.example.emailprojectjavafx.models.Physio;

import org.example.emailprojectjavafx.models.BaseResponse;
import org.example.emailprojectjavafx.models.Patient.Patient;

import java.util.List;

public class PhysioListResponse extends BaseResponse {

    List<Physio> result;

    public List<Physio> getPhysios() {
        return result;
    }
}
