package com.fcamposj.perumotos.models;

public class Driver {
    String id;
    String name;
    String asociate;
    String email;
    String password;
    String bikeBrand;
    String bikePlate;

    public Driver() {
    }

    public Driver(String id, String name, String asociate, String email, String password, String bikeBrand, String bikePlate) {
        this.id = id;
        this.name = name;
        this.asociate = asociate;
        this.email = email;
        this.password = password;
        this.bikeBrand = bikeBrand;
        this.bikePlate = bikePlate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAsociate() {
        return asociate;
    }

    public void setAsociate(String asociate) {
        this.asociate = asociate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBikeBrand() {
        return bikeBrand;
    }

    public void setBikeBrand(String bikeBrand) {
        this.bikeBrand = bikeBrand;
    }

    public String getBikePlate() {
        return bikePlate;
    }

    public void setBikePlate(String bikePlate) {
        this.bikePlate = bikePlate;
    }
}
