package org.example.countrymgr.model;

import jakarta.persistence.*;

@Entity
public class Country {
    @Id
    private String code;
    @Column
    private String name;
    @Column
    private Double internetUsers;
    @Column
    private Double adultLiteracyRate;

    //default constructor for JPA
    public Country() {}

    @Override
    public String toString() {
        return "Country{" +
                "adultLiteracyRate=" + adultLiteracyRate +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", internetUsers=" + internetUsers +
                '}';
    }

    public Double getAdultLiteracyRate() {
        return adultLiteracyRate;
    }

    public void setAdultLiteracyRate(Double adultLiteracyRate) {
        this.adultLiteracyRate = adultLiteracyRate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getInternetUsers() {
        return internetUsers;
    }

    public void setInternetUsers(Double internetUsers) {
        this.internetUsers = internetUsers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
