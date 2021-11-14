package com.pluralsight.michaelhoffman.camel.foundations.routing;

public class Contact {
    private int id;
    private String name;

    public Contact(int id) {
        this.id = id;
    }

    public Contact(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
