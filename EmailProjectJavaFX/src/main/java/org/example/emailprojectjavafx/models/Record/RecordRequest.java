package org.example.emailprojectjavafx.models.Record;

public class RecordRequest {
    private String patient;
    private String medicalRecord;

    public RecordRequest(String patient, String medicalRecord) {
        this.patient = patient;
        this.medicalRecord = medicalRecord;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(String medicalRecord) {
        this.medicalRecord = medicalRecord;
    }
}
