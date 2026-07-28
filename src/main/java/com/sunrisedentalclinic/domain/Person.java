package com.sunrisedentalclinic.domain;

public abstract class Person {
    protected int personID;
    protected String name;
    protected String contactNo;
    protected String address;

    public Person(int personID, String name, String contactNo, String address){
        this.personID = personID;
        this.name = name;
        this.contactNo = contactNo;
        this.address = address;
    }

    public abstract String getDetails();

    // Getters & setters
    public int getPersonId() { return personID; }
    public void setPersonId(int personId) { this.personID = personId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
