package com.pluralsight.michaelhoffman.camel.foundations.routing;

public class Address {
    private int id;
    private String addressLine1;
    private String city;
    private String state;
    private String postalCode;

    public Address(int id) {
        this.id = id;
    }

    public Address(int id, String addressLine1, String city, String state, String postalCode) {
        this.id = id;
        this.addressLine1 = addressLine1;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

}
