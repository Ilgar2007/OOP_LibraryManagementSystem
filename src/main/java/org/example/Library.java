package org.example;

public class Library {
    private String id;
    private String name;
    private Address address;

    public Library(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    public Address getAddress(){
        return address;
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

    public void setAddress(Address address) {
        this.address = address;
    }
}
