/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uts.aip.detentiontracker.domain;

import java.io.*;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 *
 * @author james
 */
public class Detention implements Serializable {
    
//    private Login login;
    private int DetentionID;
    private String firstName;
    private String LastName;
    private YearType yearType;
    private DetentionType detentionType;
    private DepartmentType departmentType;
    private String reason;
    
    @Id
    @GeneratedValue
    public int getDetentionID() {
        return DetentionID;
    }

    public void setDetentionID(int DetentionID) {
        this.DetentionID = DetentionID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String LastName) {
        this.LastName = LastName;
    }
    @Enumerated(EnumType.STRING)
    public YearType getYearType() {
        return yearType;
    }

    public void setYearType(YearType yearType) {
        this.yearType = yearType;
    }
    @Enumerated(EnumType.STRING)
    public DetentionType getDetentionType() {
        return detentionType;
    }

    public void setDetentionType(DetentionType detentionType) {
        this.detentionType = detentionType;
    }
    @Enumerated(EnumType.STRING)
    public DepartmentType getDepartmentType() {
        return departmentType;
    }

    public void setDepartmentType(DepartmentType departmentType) {
        this.departmentType = departmentType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    
//    @ManyToOne
//    public Login getLogin() {
//        return login;
//    }
//
//    public void setLogin(Login login) {
//        this.login = login;
//    }
    
    
}