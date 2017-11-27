package com.example.enduser.lostpets;

/**
 * Created by ZenithPC on 11/12/2017.
 */

public class Pet {
    //TODO Add pet description in order to allow on click to work successfully
    private String name;
    private String weight;
    private String gender;
    private String zipCode;
    private String breed;
    private String description;
    private String microchip;

    public Pet(String name, String weight, String gender, String zipCode, String breed){
        this.name = name;
        this.weight = weight;
        this.gender = gender;
        this.zipCode = zipCode;
        this.breed = breed;
    }

    public Pet(String name, String weight, String gender, String zipCode, String breed, String microchip, String description){
        this.name = name;
        this.weight = weight;
        this.gender = gender;
        this.zipCode = zipCode;
        this.breed = breed;
        this.description = description;
        this.microchip = microchip;
    }
    public void setName(String name){
        this.name = name;
    }
    public  void setWeight(String weight){
        this.weight = weight;
    }
    public void setGender(String gender){
        this.gender = gender;
    }
    public void setZipCode(String zip){
        zipCode = zip;
    }
    public void setBreed(String breed){
        this.breed = breed;
    }
    public String getName(){return name;}
    public String getWeight(){return weight;}
    public String getGender(){return gender;}
    public String getZipCode(){return zipCode;}
    public String getBreed(){return breed;}




}
