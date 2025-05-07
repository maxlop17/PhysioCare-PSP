package org.example.emailprojectjavafx.models.Appointment;

import com.google.gson.annotations.SerializedName;
import org.example.emailprojectjavafx.models.Physio.Physio;

import java.time.LocalDate;
import java.util.Date;

public class Appointment {
    @SerializedName("_id")
    private String id;
    private Date date;
    private String physio;
    private String diagnosis;
    private String treatment;
    private String observations;
    private Boolean confirmed;

    public Appointment(Date date, String physio, String diagnosis, String treatment, String observations, Boolean confirmed) {
        this.date = date;
        this.physio = physio;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.observations = observations;
        this.confirmed = confirmed;
    }

    public Appointment(String id, Date date, String physio, String diagnosis, String treatment, String observations, Boolean confirmed) {
        this.id = id;
        this.date = date;
        this.physio = physio;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.observations = observations;
        this.confirmed = confirmed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPhysio() {
        return physio;
    }

    public void setPhysio(String physio) {
        this.physio = physio;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "date=" + date +
                ", physio='" + physio + '\'' +
                ", diagnosis='" + diagnosis + '\'' +
                ", treatment='" + treatment + '\'' +
                ", observations='" + observations + '\'' +
                '}';
    }
}

