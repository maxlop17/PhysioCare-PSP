package org.example.emailprojectjavafx.models.Patient;

import com.google.gson.annotations.SerializedName;
import org.example.emailprojectjavafx.models.BaseResponse;

import java.util.List;

public class PatientListResponse extends BaseResponse {

    private List<Patient> resultado;

    public List<Patient> getPatients() {
        return resultado;
    }
}

