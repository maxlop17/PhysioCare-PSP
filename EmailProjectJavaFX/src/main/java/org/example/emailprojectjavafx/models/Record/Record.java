package org.example.emailprojectjavafx.models.Record;

import com.google.gson.annotations.SerializedName;
import org.example.emailprojectjavafx.models.Appointment.Appointment;
import org.example.emailprojectjavafx.models.Patient.Patient;

import java.util.List;

public class Record {
    @SerializedName("_id")
    private String id;
    private String patient;
    private String medicalRecord;
    private List<String> appointments;

    public Record(String patient, String medicalRecord, List<String> appointments) {
        this.patient = patient;
        this.medicalRecord = medicalRecord;
        this.appointments = appointments;
    }

    public Record(String id, String patient, String medicalRecord, List<String> appointments) {
        this.id = id;
        this.patient = patient;
        this.medicalRecord = medicalRecord;
        this.appointments = appointments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<String> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<String> appointments) {
        this.appointments = appointments;
    }


    @Override
    public String toString() {
        return "Record{" +
                "patient='" + patient + '\'' +
                ", medicalRecord='" + medicalRecord + '\'' +
                ", appointments=" + appointments +
                '}';
    }
}
