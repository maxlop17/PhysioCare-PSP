package org.example.emailprojectjavafx.models.Patient;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class Patient {

    @SerializedName("_id")
    private String id;
    private String name;
    private String surname;
    private Date birthDate;
    private String address;
    private String insuranceNumber;
    private String email;

    public Patient(String name, String surname, Date birthDate, String address, String insuranceNumber, String email) {
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.address = address;
        this.insuranceNumber = insuranceNumber;
        this.email = email;
    }

    public Patient(String id, String name, String surname, Date birthDate, String address, String insuranceNumber, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.address = address;
        this.insuranceNumber = insuranceNumber;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }




    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return name + " " + surname + " | " + formatter.format(birthDate) + " | " + insuranceNumber;
    }

}
